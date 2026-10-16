package jp.osscons.datarep;

import java.nio.ByteOrder;

/**
 * The subset of compiler configuration (config/*.conf) that affects these
 * programs. Defaults match config/default.conf.
 */
public final class CobolConfig {
    private final BinarySize binarySize;
    private final ByteOrder byteOrder;
    private final boolean prettyDisplay;

    public CobolConfig(BinarySize binarySize, ByteOrder byteOrder, boolean prettyDisplay) {
        this.binarySize = binarySize;
        this.byteOrder = byteOrder;
        this.prettyDisplay = prettyDisplay;
    }

    public static CobolConfig defaults() {
        return new CobolConfig(BinarySize.SIZE_1_2_4_8, ByteOrder.BIG_ENDIAN, true);
    }

    public BinarySize binarySize() {
        return binarySize;
    }

    public ByteOrder byteOrder() {
        return byteOrder;
    }

    public boolean prettyDisplay() {
        return prettyDisplay;
    }

    public static ByteOrder parseByteOrder(String text) {
        switch (text) {
            case "big-endian":
                return ByteOrder.BIG_ENDIAN;
            case "native":
                return ByteOrder.nativeOrder();
            default:
                throw new IllegalArgumentException("Unsupported binary-byteorder: " + text);
        }
    }

    public static boolean parseYesNo(String option, String text) {
        switch (text) {
            case "yes":
                return true;
            case "no":
                return false;
            default:
                throw new IllegalArgumentException("Unsupported " + option + ": " + text);
        }
    }
}
