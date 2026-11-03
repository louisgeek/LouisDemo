package com.louisgeek.myffmpegdemo;

import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.louisgeek.myffmpeg.FFmpegUtil;
import com.louisgeek.myffmpeg.RemuxResult;
import com.louisgeek.myffmpegdemo.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final ExecutorService remuxExecutor = Executors.newSingleThreadExecutor();
    private volatile String currentOperationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sampleText.setText("FFmpeg Version: " + FFmpegUtil.getFFmpegVersion());
        binding.tvPrerequisite.setText("前提：素材（.m3u8/.m3u 清单 + 分片）已由下载模块落盘到\n"
                + businessDir() + "\n"
                + "本 Demo 不负责下载/导入，转换直接读取该目录（零拷贝）。\n"
                + "调试时可用 adb push 把素材拷到该目录。");

        binding.btnConvert.setOnClickListener(v -> startConversion());

        binding.btnCancel.setOnClickListener(v -> {
            String id = currentOperationId;
            if (id != null) {
                // 只置原子标志，可在主线程直接调用，不阻塞
                boolean delivered = FFmpegUtil.cancelRemux(id);
                appendStatus("已请求取消" + (delivered ? "" : "（操作可能已结束）"));
            } else {
                appendStatus("当前没有进行中的转换");
            }
        });
    }

    /** 通用最小接入：素材已由外部落盘在业务目录，直接读取该目录里的清单转换。 */
    private void startConversion() {
        appendStatus("----");
        runInBackground(() -> {
            final File dir = businessDir();
            String playlist = findPlaylistRecursive(dir);
            if (playlist == null) {
                postStatus("未找到素材：请先把 m3u8 清单+分片放到 " + dir);
                return;
            }
            postStatus("读取业务目录素材（零拷贝）: " + playlist);
            executeRemuxAndReport(playlist, dir.getAbsolutePath());
        });
    }

    /** 共享执行路径：prepare -> executeRemux -> 结果展示（在后台线程运行）。 */
    private void executeRemuxAndReport(String inputPath, String allowedInputRoot) {
        try {
            // 库先写 .part 再原子替换；失败时保留上一次输出，不预删
            File output = new File(businessDir(), "remux_output.mp4");

            final String opId = "demo:" + System.currentTimeMillis();
            currentOperationId = opId;
            postStatus("转换中…（可点「取消转换」）");
            runOnUiThread(() -> binding.progressRemux.setProgress(0));

            if (!FFmpegUtil.prepareRemux(opId)) {
                currentOperationId = null;
                postStatus("操作登记失败: " + opId);
                return;
            }

            long begin = System.currentTimeMillis();
            final RemuxResult result = FFmpegUtil.executeRemux(
                    opId, inputPath, output.getAbsolutePath(), allowedInputRoot,
                    percent -> runOnUiThread(() -> {
                        if (!isDestroyed()) {
                            binding.progressRemux.setProgress(percent);
                        }
                    }));
            long elapsed = System.currentTimeMillis() - begin;
            currentOperationId = null;

            final String size = result.isSuccess() && output.exists()
                    ? String.format(Locale.US, "%.2f MB", output.length() / 1024.0 / 1024.0)
                    : "-";
            postStatus("结果: " + statusName(result.code) + "（code=" + result.code + "）"
                    + "，耗时 " + elapsed + " ms"
                    + "\n输出: " + output.getAbsolutePath() + "（" + size + "）"
                    + (result.message.isEmpty() ? "" : "\n信息: " + result.message));
            if (result.isSuccess()) {
                runOnUiThread(() ->
                        Toast.makeText(this, "转换完成", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> binding.progressRemux.setProgress(0));
            }
        } catch (Exception e) {
            currentOperationId = null;
            postStatus("异常: " + e);
        }
    }

    private static String statusName(int code) {
        switch (code) {
            case RemuxResult.CODE_SUCCESS: return "success";
            case RemuxResult.CODE_CANCELLED: return "cancelled";
            case RemuxResult.CODE_INVALID_ARGUMENT: return "invalid argument";
            case RemuxResult.CODE_NOT_PREPARED: return "not prepared";
            case RemuxResult.CODE_ALREADY_RUNNING: return "already running";
            case RemuxResult.CODE_BUSY: return "busy";
            case RemuxResult.CODE_INPUT_ERROR: return "input error";
            case RemuxResult.CODE_UNSUPPORTED_STREAM: return "unsupported stream";
            case RemuxResult.CODE_INSECURE_INPUT: return "insecure input";
            case RemuxResult.CODE_OUTPUT_ERROR: return "output error";
            case RemuxResult.CODE_MUX_ERROR: return "mux error";
            case RemuxResult.CODE_INTERNAL_ERROR: return "internal error";
            default: return "unknown";
        }
    }

    // -------------------------------------------------------------------------
    // 业务落盘：模拟下载模块把素材落到 app 自有目录，转换直接读落盘路径
    // -------------------------------------------------------------------------

    /** 业务落盘根目录：模拟下载模块管理的目录（app 自有，native 可直接访问）。 */
    private File businessDir() {
        File dir = new File(getExternalFilesDir(null), "downloads");
        if (!dir.exists() && !dir.mkdirs()) {
            // 外部files目录不可用时退回内部存储
            dir = new File(getFilesDir(), "downloads");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    // -------------------------------------------------------------------------
    // 内部工具
    // -------------------------------------------------------------------------

    /** Demo 取业务目录里找到的第一个 m3u8/m3u 清单；真实业务应记录下载任务的入口清单路径。 */
    private String findPlaylistRecursive(File dir) {
        File[] children = dir.listFiles();
        if (children == null) {
            return null;
        }
        String found = null;
        for (File f : children) {
            if (f.isDirectory()) {
                final String r = findPlaylistRecursive(f);
                if (found == null) {
                    found = r;
                }
            } else {
                final String lower = f.getName().toLowerCase(Locale.US);
                if (lower.endsWith(".m3u8") || lower.endsWith(".m3u")) {
                    if (found == null) {
                        found = f.getAbsolutePath();
                    }
                }
            }
        }
        return found;
    }

    /** 后台线程执行并降低线程优先级，避免与 UI/播放争抢 CPU。 */
    private void runInBackground(Runnable task) {
        remuxExecutor.execute(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            task.run();
        });
    }

    private void postStatus(String msg) {
        runOnUiThread(() -> appendStatus(msg));
    }

    private void appendStatus(String msg) {
        binding.tvStatus.append(msg + "\n");
    }

    @Override
    protected void onDestroy() {
        // 释放时取消所有 native 操作，防止残留执行
        FFmpegUtil.cancelAllRemuxes();
        remuxExecutor.shutdownNow();
        super.onDestroy();
    }
}
