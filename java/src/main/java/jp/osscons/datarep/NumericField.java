package jp.osscons.datarep;

import java.nio.ByteOrder;

/** A numeric data item: {@code PIC [S]9(digits) USAGE usage}. */
public final class NumericField {
    /** Digits shown for a BINARY item of n bytes when pretty-display is off (libcob/termio.c). */
    private static final int[] BIN_DIGITS = {1, 3, 5, 8, 10, 13, 15, 17, 20};

    private final String name;
    private final int digits;
    private final boolean signed;
    private final Usage usage;
    private final CobolConfig config;
    private long value;

    public NumericField(String name, int digits, boolean signed, Usage usage, CobolConfig config, long value) {
        if (digits < 1 || digits > 18) {
            throw new IllegalArgumentException(name + ": digits must be 1 to 18");
        }
        this.name = name;
        this.digits = digits;
        this.signed = signed;
        this.usage = usage;
        this.config = config;
        set(value);
    }

    public String name() {
        return name;
    }

    public long value() {
        return value;
    }

    public void set(long newValue) {
        if (!signed && newValue < 0) {
            throw new IllegalArgumentException(name + " is unsigned: " + newValue);
        }
        long limit = pow10(digits);
        if (newValue >= limit || newValue <= -limit) {
            throw new IllegalArgumentException(name + " cannot hold " + newValue + " in " + digits + " digits");
        }
        value = newValue;
    }

    /** {@code INITIALIZE field}: numeric items are set to zero. */
    public void initialize() {
        value = 0;
    }

    /** {@code MOVE ZERO TO field}. */
    public void moveZero() {
        value = 0;
    }

    /** Number of bytes the item occupies in storage. */
    public int size() {
        if (usage == Usage.BINARY) {
            return config.binarySize().bytesFor(digits, signed);
        }
        return digits / 2 + 1;
    }

    /** The item's bytes exactly as the COBOL runtime stores them. */
    public byte[] toBytes() {
        return usage == Usage.BINARY ? binaryBytes() : packedBytes();
    }

    private byte[] binaryBytes() {
        int size = size();
        byte[] bytes = new byte[size];
        boolean bigEndian = config.byteOrder() == ByteOrder.BIG_ENDIAN;
        for (int i = 0; i < size; i++) {
            byte b = (byte) (value >> (8 * i));
            bytes[bigEndian ? size - 1 - i : i] = b;
        }
        return bytes;
    }

    private byte[] packedBytes() {
        int size = size();
        int nibbleCount = size * 2;
        int[] nibbles = new int[nibbleCount];
        long remaining = Math.abs(value);
        for (int i = nibbleCount - 2; i >= 0 && remaining > 0; i--) {
            nibbles[i] = (int) (remaining % 10);
            remaining /= 10;
        }
        nibbles[nibbleCount - 1] = !signed ? 0xF : value < 0 ? 0xD : 0xC;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ((nibbles[2 * i] << 4) | nibbles[2 * i + 1]);
        }
        return bytes;
    }

    /** Text written by {@code DISPLAY field}. */
    public String display() {
        int shownDigits = usage == Usage.BINARY && !config.prettyDisplay() ? BIN_DIGITS[size()] : digits;
        String number = String.valueOf(Math.abs(value));
        StringBuilder text = new StringBuilder(shownDigits + 1);
        if (signed) {
            text.append(value < 0 ? '-' : '+');
        }
        for (int i = number.length(); i < shownDigits; i++) {
            text.append('0');
        }
        return text.append(number).toString();
    }

    private static long pow10(int n) {
        long result = 1;
        for (int i = 0; i < n; i++) {
            result *= 10;
        }
        return result;
    }
}
