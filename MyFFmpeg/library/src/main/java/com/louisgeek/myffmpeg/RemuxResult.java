package com.louisgeek.myffmpeg;

/**
 * 转封装结果：结构化错误码 + 可读消息。
 *
 * <p>错误码与 native 层一一对应，调用方应根据 {@link #code} 分支处理；
 * {@link #message} 只用于日志和排查，不保证内容稳定。
 */
public final class RemuxResult {

    /** 转封装成功，输出已原子落盘。 */
    public static final int CODE_SUCCESS = 0;
    /** 已按调用方请求取消，输出临时文件已删除。 */
    public static final int CODE_CANCELLED = 1;
    /** 参数非法：空路径、输入输出同路径、allowedInputRoot 不存在等。 */
    public static final int CODE_INVALID_ARGUMENT = 2;
    /** 操作未通过 {@link FFmpegUtil#prepareRemux} 登记。 */
    public static final int CODE_NOT_PREPARED = 3;
    /** 同一操作已在执行，或同一输出路径正被其他操作写入。 */
    public static final int CODE_ALREADY_RUNNING = 4;
    /** 并发数达到上限（当前 2），稍后重试。 */
    public static final int CODE_BUSY = 5;
    /** 输入无法打开/解析，或读取过程中出错。 */
    public static final int CODE_INPUT_ERROR = 6;
    /** 输入流不受支持：无音视频流，或音频不是 AAC。 */
    public static final int CODE_UNSUPPORTED_STREAM = 7;
    /** 本地清单引用了允许根目录之外的文件，或包含远程 URI。 */
    public static final int CODE_INSECURE_INPUT = 8;
    /** 输出文件无法创建，或临时文件无法落到目标路径。 */
    public static final int CODE_OUTPUT_ERROR = 9;
    /** 写容器头/写包/写文件尾失败。 */
    public static final int CODE_MUX_ERROR = 10;
    /** 内部错误：内存分配失败、JNI 参数转换失败等。 */
    public static final int CODE_INTERNAL_ERROR = 11;

    public final int code;
    public final String message;

    public RemuxResult(int code, String message) {
        this.code = code;
        this.message = message == null ? "" : message;
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    public boolean isCancelled() {
        return code == CODE_CANCELLED;
    }

    @Override
    public String toString() {
        return "RemuxResult{code=" + code + ", message='" + message + "'}";
    }
}
