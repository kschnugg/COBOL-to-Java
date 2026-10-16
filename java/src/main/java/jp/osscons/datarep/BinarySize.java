package jp.osscons.datarep;

/**
 * The {@code binary-size} compiler option: how many bytes a BINARY item with a
 * given number of digits occupies (see cobc/field.c, compute_size).
 */
public enum BinarySize {
    SIZE_2_4_8("2-4-8"),
    SIZE_1_2_4_8("1-2-4-8"),
    SIZE_1__8("1--8");

    private final String configName;

    BinarySize(String configName) {
        this.configName = configName;
    }

    public static BinarySize parse(String text) {
        for (BinarySize size : values()) {
            if (size.configName.equals(text)) {
                return size;
            }
        }
        throw new IllegalArgumentException("Unsupported binary-size: " + text);
    }

    public int bytesFor(int digits, boolean signed) {
        if (digits < 1 || digits > 18) {
            throw new IllegalArgumentException("BINARY items must have 1 to 18 digits: " + digits);
        }
        switch (this) {
            case SIZE_2_4_8:
                return digits <= 4 ? 2 : digits <= 9 ? 4 : 8;
            case SIZE_1_2_4_8:
                return digits <= 2 ? 1 : digits <= 4 ? 2 : digits <= 9 ? 4 : 8;
            default:
                if (signed) {
                    return digits <= 2 ? 1 : digits <= 4 ? 2 : digits <= 6 ? 3 : digits <= 9 ? 4
                            : digits <= 11 ? 5 : digits <= 14 ? 6 : digits <= 16 ? 7 : 8;
                }
                return digits <= 2 ? 1 : digits <= 4 ? 2 : digits <= 7 ? 3 : digits <= 9 ? 4
                        : digits <= 12 ? 5 : digits <= 14 ? 6 : digits <= 16 ? 7 : 8;
        }
    }
}
