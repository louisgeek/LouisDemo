package com.louisgeek.myffmpeg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 不依赖媒体素材的契约测试：错误码、原子输出、清单白名单、
 * 清单契约（ENDLIST 必须、query/fragment 拒绝、master+子清单放行）。
 * 素材矩阵（H.264/HEVC × AAC/AC3/无音轨）需要真实样本，另行补充。
 */
@RunWith(AndroidJUnit4.class)
public class FFmpegUtilTest {

    private File dir;
    private int opSeq = 0;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dir = new File(context.getCacheDir(), "myffmpeg_test_" + System.nanoTime());
        assertTrue(dir.mkdirs());
    }

    @After
    public void tearDown() {
        deleteRecursively(dir);
    }

    private String nextOperationId() {
        return "test:" + System.nanoTime() + ":" + (++opSeq);
    }

    private static void write(File file, String content) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String read(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
            return out.toString("UTF-8");
        }
    }

    @Test
    public void ffmpegVersionIsAvailable() {
        String version = FFmpegUtil.getFFmpegVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    public void emptyArgumentsAreRejected() {
        assertEquals(RemuxResult.CODE_INVALID_ARGUMENT,
                FFmpegUtil.executeRemux("", "/a.ts", "/b.mp4").code);
        assertEquals(RemuxResult.CODE_INVALID_ARGUMENT,
                FFmpegUtil.executeRemux("op", "", "/b.mp4").code);
        assertEquals(RemuxResult.CODE_INVALID_ARGUMENT,
                FFmpegUtil.executeRemux("op", "/a.ts", "").code);
    }

    @Test
    public void executeWithoutPrepareIsRejected() {
        RemuxResult result = FFmpegUtil.executeRemux(
                nextOperationId(), new File(dir, "in.ts").getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath());
        assertEquals(RemuxResult.CODE_NOT_PREPARED, result.code);
    }

    @Test
    public void prepareRejectsEmptyAndDuplicateIds() {
        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        assertFalse(FFmpegUtil.prepareRemux(id));
        assertFalse(FFmpegUtil.prepareRemux(""));
        assertFalse(FFmpegUtil.cancelRemux("unknown:" + System.nanoTime()));

        // 取消未执行的操作即注销，之后不能再执行
        assertTrue(FFmpegUtil.cancelRemux(id));
        assertEquals(RemuxResult.CODE_NOT_PREPARED,
                FFmpegUtil.executeRemux(id, "/a.ts", "/b.mp4").code);
    }

    @Test
    public void sameInputAndOutputIsRejected() throws IOException {
        File file = new File(dir, "same.ts");
        write(file, "not media");
        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(
                id, file.getAbsolutePath(), file.getAbsolutePath());
        assertEquals(RemuxResult.CODE_INVALID_ARGUMENT, result.code);
        assertTrue(file.exists());
        assertEquals("not media", read(file));
    }

    @Test
    public void failureKeepsExistingOutputAndLeavesNoTempFile() throws IOException {
        File input = new File(dir, "bad_input.bin");
        write(input, "this is not a media file");
        File output = new File(dir, "existing.mp4");
        write(output, "keep-me");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(
                id, input.getAbsolutePath(), output.getAbsolutePath());

        assertEquals(RemuxResult.CODE_INPUT_ERROR, result.code);
        assertTrue(output.exists());
        assertEquals("keep-me", read(output));
        assertFalse(new File(output.getAbsolutePath() + ".part").exists());
    }

    @Test
    public void playlistReferencingFileOutsideRootIsRejected() throws IOException {
        File root = new File(dir, "root");
        assertTrue(root.mkdirs());
        write(new File(dir, "outside.ts"), "segment");
        File playlist = new File(root, "index.m3u8");
        write(playlist, "#EXTM3U\n#EXTINF:1,\n../outside.ts\n");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(id, playlist.getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath(), root.getAbsolutePath(), null);
        assertEquals(RemuxResult.CODE_INSECURE_INPUT, result.code);
    }

    @Test
    public void playlistWithRemoteUriIsRejected() throws IOException {
        File root = new File(dir, "root2");
        assertTrue(root.mkdirs());
        write(new File(root, "seg.ts"), "segment");
        File playlist = new File(root, "index.m3u8");
        write(playlist, "#EXTM3U\n"
                + "#EXT-X-KEY:METHOD=AES-128,URI=\"https://example.com/key\"\n"
                + "#EXTINF:1,\nseg.ts\n");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(id, playlist.getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath(), root.getAbsolutePath(), null);
        assertEquals(RemuxResult.CODE_INSECURE_INPUT, result.code);
    }

    @Test
    public void mediaPlaylistWithoutEndlistIsRejected() throws IOException {
        File root = new File(dir, "root3");
        assertTrue(root.mkdirs());
        write(new File(root, "seg.ts"), "segment");
        File playlist = new File(root, "index.m3u8");
        // 无 #EXT-X-ENDLIST：live/半截清单，必须在扫描阶段拒绝而不是执行时挂死
        write(playlist, "#EXTM3U\n#EXTINF:1,\nseg.ts\n");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(id, playlist.getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath(), root.getAbsolutePath(), null);
        assertEquals(RemuxResult.CODE_INSECURE_INPUT, result.code);
    }

    @Test
    public void playlistUriWithQueryOrFragmentIsRejected() throws IOException {
        File root = new File(dir, "root4");
        assertTrue(root.mkdirs());
        write(new File(root, "seg.ts"), "segment");
        File playlist = new File(root, "index.m3u8");
        write(playlist, "#EXTM3U\n#EXTINF:1,\nseg.ts#frag\n");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(id, playlist.getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath(), root.getAbsolutePath(), null);
        assertEquals(RemuxResult.CODE_INSECURE_INPUT, result.code);

        // query 形式同样拒绝
        File playlist2 = new File(root, "index2.m3u8");
        write(playlist2, "#EXTM3U\n#EXTINF:1,\nseg.ts?token=1\n#EXT-X-ENDLIST\n");
        String id2 = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id2));
        assertEquals(RemuxResult.CODE_INSECURE_INPUT,
                FFmpegUtil.executeRemux(id2, playlist2.getAbsolutePath(),
                        new File(dir, "out2.mp4").getAbsolutePath(),
                        root.getAbsolutePath(), null).code);
    }

    @Test
    public void masterPlaylistWithEndedSubPlaylistPassesValidation() throws IOException {
        File root = new File(dir, "root5");
        assertTrue(root.mkdirs());
        write(new File(root, "seg.ts"), "segment");
        write(new File(root, "media.m3u8"),
                "#EXTM3U\n#EXTINF:1,\nseg.ts\n#EXT-X-ENDLIST\n");
        // master 清单本身无媒体分片，不要求 ENDLIST；由子清单保证
        File master = new File(root, "master.m3u8");
        write(master, "#EXTM3U\n#EXT-X-STREAM-INF:BANDWIDTH=1000000\nmedia.m3u8\n");

        String id = nextOperationId();
        assertTrue(FFmpegUtil.prepareRemux(id));
        RemuxResult result = FFmpegUtil.executeRemux(id, master.getAbsolutePath(),
                new File(dir, "out.mp4").getAbsolutePath(), root.getAbsolutePath(), null);
        // 预扫描应通过（seg.ts 是伪素材，后续在 FFmpeg 打开/读取阶段才失败，
        // 但绝不能以 INSECURE_INPUT 拒绝）
        assertTrue("master+含 ENDLIST 子清单应通过预扫描，实际: " + result,
                result.code != RemuxResult.CODE_INSECURE_INPUT);
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
