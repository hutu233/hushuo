package github.hutu233.hushuo;

public final class Common {

    private Common() {
    }

    // 帧的最大长度。如果帧的长度超过这个值，解码器将抛出  TooLongFrameException
    public static int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    // 长度字段的偏移量（从帧头开始的字节数）。即长度字段在帧中的位置。
    public static int LENGTH_FIELD_OFFSET = 0;

    // 长度字段的字节数。即用于表示帧长度的字段的长度。
    public static int LENGTH_FIELD_LENGTH = 1;

    // 长度字段的调整值。通常用于修正帧长度字段不包含整个帧长度的情况。例如，如果长度字段表示的是帧头之后的长度，而不是整个帧的长度，
    // 那么需要设置这个参数来调整。
    public static int LENGTH_ADJUSTMENT = 0;

    // 从解码后的帧中剥离的字节数。通常用于去除帧头部分。 （例如长度字段）
    public static int INITIAL_BYTES_TO_STRIP = 1;

    public static char MESSAGE_TYPE_SUBSCRIBE = 'S';

    public static char MESSAGE_TYPE_PUBLISH = 'P';

    public static char MESSAGE_TYPE_ERROR = 'E';

    public static char MESSAGE_TYPE_DISCONNECT = 'D';
}
