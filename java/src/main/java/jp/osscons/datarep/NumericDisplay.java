package jp.osscons.datarep;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Java version of tests/data-rep.src/numeric-display.cob.
 *
 * <p>Declares X-P1..X-P18 ({@code PIC 9(n) VALUE 1, 12, ...}) and X-N1..X-N18
 * ({@code PIC S9(n) VALUE -1, -12, ...}) with the chosen usage, then DISPLAYs
 * each one on its own line.
 */
public final class NumericDisplay {
    private NumericDisplay() {
    }

    public static void main(String[] args) {
        run(ProgramOptions.parse(args), System.out);
    }

    static void run(ProgramOptions options, PrintStream out) {
        List<NumericField> fields = new ArrayList<>();
        for (int n = 1; n <= 18; n++) {
            fields.add(new NumericField("X-P" + n, n, false, options.usage, options.config,
                    ProgramOptions.templateValue(n)));
        }
        for (int n = 1; n <= 18; n++) {
            fields.add(new NumericField("X-N" + n, n, true, options.usage, options.config,
                    -ProgramOptions.templateValue(n)));
        }
        for (NumericField field : fields) {
            out.println(field.display());
        }
        out.flush();
    }
}
