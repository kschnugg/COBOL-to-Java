package jp.osscons.datarep;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java version of tests/data-rep.src/numeric-dump.cob.
 *
 * <p>Each group G-n / G-Sn holds X-n / X-Sn ({@code PIC [S]9(n)}, VALUE 1, 12,
 * ... or -1, -12, ...) followed by {@code FILLER PIC X(18) VALUE SPACE}. The
 * program hex-dumps every group, then again after {@code INITIALIZE} of each
 * item, then again after {@code MOVE ZERO} to each item.
 *
 * <p>The dumped width matches the test suite's dump.c helper: 8 bytes for
 * BINARY and 10 bytes for PACKED-DECIMAL.
 */
public final class NumericDump {
    private static final int FILLER_SIZE = 18;

    private NumericDump() {
    }

    public static void main(String[] args) {
        run(ProgramOptions.parse(args), System.out);
    }

    static void run(ProgramOptions options, PrintStream out) {
        int width = options.usage == Usage.BINARY ? 8 : 10;
        List<NumericField> fields = new ArrayList<>();
        for (int n = 1; n <= 18; n++) {
            fields.add(new NumericField("X-" + n, n, false, options.usage, options.config,
                    ProgramOptions.templateValue(n)));
        }
        for (int n = 1; n <= 18; n++) {
            fields.add(new NumericField("X-S" + n, n, true, options.usage, options.config,
                    -ProgramOptions.templateValue(n)));
        }

        for (NumericField field : fields) {
            out.println(dump(group(field), width));
        }
        for (NumericField field : fields) {
            field.initialize();
            out.println(dump(group(field), width));
        }
        for (NumericField field : fields) {
            field.moveZero();
            out.println(dump(group(field), width));
        }
        out.flush();
    }

    /** Storage of {@code 01 G-n. 02 X-n ... 02 FILLER PIC X(18) VALUE SPACE.} */
    static byte[] group(NumericField field) {
        byte[] item = field.toBytes();
        byte[] group = new byte[item.length + FILLER_SIZE];
        System.arraycopy(item, 0, group, 0, item.length);
        Arrays.fill(group, item.length, group.length, (byte) ' ');
        return group;
    }

    /** Equivalent of the test suite's dump.c: lowercase hex of the first {@code width} bytes. */
    static String dump(byte[] data, int width) {
        StringBuilder hex = new StringBuilder(width * 2);
        for (int i = 0; i < width; i++) {
            hex.append(String.format("%02x", data[i] & 0xFF));
        }
        return hex.toString();
    }
}
