package jp.osscons.datarep;

import java.nio.ByteOrder;

/**
 * Command-line equivalents of {@code sed s/@USAGE@/.../} and the {@code -conf}
 * file used by the test suite, e.g.
 * {@code --usage BINARY --binary-size 2-4-8 --binary-byteorder big-endian --pretty-display no}.
 */
final class ProgramOptions {
    final Usage usage;
    final CobolConfig config;

    private ProgramOptions(Usage usage, CobolConfig config) {
        this.usage = usage;
        this.config = config;
    }

    static ProgramOptions parse(String[] args) {
        CobolConfig defaults = CobolConfig.defaults();
        Usage usage = Usage.BINARY;
        BinarySize binarySize = defaults.binarySize();
        ByteOrder byteOrder = defaults.byteOrder();
        boolean prettyDisplay = defaults.prettyDisplay();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for " + args[i]);
            }
            String value = args[i + 1];
            switch (args[i]) {
                case "--usage":
                    usage = Usage.parse(value);
                    break;
                case "--binary-size":
                    binarySize = BinarySize.parse(value);
                    break;
                case "--binary-byteorder":
                    byteOrder = CobolConfig.parseByteOrder(value);
                    break;
                case "--pretty-display":
                    prettyDisplay = CobolConfig.parseYesNo("pretty-display", value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        return new ProgramOptions(usage, new CobolConfig(binarySize, byteOrder, prettyDisplay));
    }

    /** VALUE literal used for a PIC 9(digits) item in the templates: 1, 12, 123, ... */
    static long templateValue(int digits) {
        return Long.parseLong("123456789012345678".substring(0, digits));
    }
}
