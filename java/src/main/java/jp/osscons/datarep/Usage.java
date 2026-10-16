package jp.osscons.datarep;

/** Storage formats substituted for {@code @USAGE@} in the COBOL templates. */
public enum Usage {
    BINARY,
    PACKED_DECIMAL;

    public static Usage parse(String text) {
        switch (text.toUpperCase()) {
            case "BINARY":
            case "COMP":
            case "COMPUTATIONAL":
                return BINARY;
            case "PACKED-DECIMAL":
            case "COMP-3":
            case "COMPUTATIONAL-3":
                return PACKED_DECIMAL;
            default:
                throw new IllegalArgumentException("Unsupported usage: " + text);
        }
    }
}
