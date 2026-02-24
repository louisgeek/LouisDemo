package com.louis.myffmpegkit.download;

import android.util.Log;

import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.MediaInformation;
import com.arthenica.ffmpegkit.MediaInformationSession;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.StreamInformation;

import java.util.List;

public class DownloadUtils {
    private static final String TAG = "DownloadUtils";
    public static String buildFFmpegCommand(String inputUrl, String outputPath) {
        return buildFFmpegCommand(inputUrl, outputPath, null);
    }

    public static String buildFFmpegCommand(String inputUri, String outputPath, String headers) {
        int timeoutSeconds = 30;
        Log.d(TAG, "buildFFmpegCommand inputUri=" + inputUri);
        StringBuilder command = new StringBuilder();
        //自动重连参数（网络不稳定时自动重试，减少转换失败概率）
        //启用重连 流媒体重连
//        command.append("-reconnect 1 -reconnect_streamed 1 -reconnect_delay_max 5 ");
        //网络超时设置
//        command.append("-timeout ").append(timeoutSeconds * 1000000).append(" "); //微秒
//        command.append("-rw_timeout ").append(timeoutSeconds * 1000000).append(" ");
        //协议白名单
        command.append("-protocol_whitelist file,http,https,tcp,tls,crypto ");
        //如果 M3U8 引用了非标准扩展名（如 .ts 以外的文件），此参数是必须的
        command.append("-allowed_extensions ALL ");
//        command.append("-max_reload 3 ");           // 最大重载次数
//        command.append("-m3u8_hold_counters 3 ");   // M3U8 保持计数器
        // 添加HTTP headers
        if (headers != null && !headers.trim().isEmpty()) {
            command.append("-headers \"").append(headers).append("\" ");
        }
//        command.append("-user_agent \"Mozilla/5.0 (Linux; Android) AppleWebKit/537.36 Chrome/91.0.4472.120 Mobile Safari/537.36\" ");
        command.append("-i \"").append(inputUri).append("\" ");
        command.append("-c copy "); //直接复制流，不重新编码（高效）
        // 检测音频格式决定是否使用AAC过滤器
//        if (!isMp3Audio(inputUrl)) {
//          command.append(" -bsf:a aac_adtstoasc");  //AAC 音频流修复 将 AAC 音频流的 ADTS 格式转换为 ASC 格式
//        }
        // MP4 优化参数
        command.append("-movflags +faststart ");  //快速播放启动（元数据前置）
//        command.append(" -avoid_negative_ts make_zero");  //修复时间戳问题（修复负时间戳问题）
        command.append("-f mp4 "); //显式指定输出格式为 MP4（可选但推荐，避免自动推断错误）
        command.append("-y "); //覆盖已存在的文件
        command.append(" \"").append(outputPath).append("\"");
        return command.toString();
    }

    private static boolean isMp3Audio(String url) {
        try {
            MediaInformationSession session = FFprobeKit.getMediaInformation(url);
            Log.e("AudioFormat", "Detection info1: " + session.getMediaInformation());
            Log.e("AudioFormat", "Detection info2: " + session.getReturnCode());
            Log.e("AudioFormat", "Detection info3: " + session.getFailStackTrace());
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                MediaInformation info = session.getMediaInformation();
                Log.e("AudioFormat", "Detection info: " + info.getFilename());
                List<StreamInformation> streams = info.getStreams();
                for (StreamInformation stream : streams) {
                    Log.e("AudioFormat", "Detection getType: " + stream.getType());
                    Log.e("AudioFormat", "Detection getCodec: " + stream.getCodec());
                    if ("audio".equals(stream.getType()) && "mp3".equalsIgnoreCase(stream.getCodec())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("AudioFormat", "Detection failed: " + e.getMessage());
        }
        return false;
    }

}