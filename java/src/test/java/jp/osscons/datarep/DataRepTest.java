package jp.osscons.datarep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

/**
 * Runs the Java programs with every configuration the COBOL test suite uses
 * (tests/data-rep.src/binary.at and packed.at) and compares the output with
 * what the compiled COBOL programs print (src/test/resources/expected).
 */
public final class DataRepTest {
    private static int failures;
    private static int passed;

    private DataRepTest() {
    }

    public static void main(String[] args) throws IOException {
        String[] sizes = {"2-4-8", "1-2-4-8", "1--8"};
        String[] orders = {"big-endian", "native"};
        for (String size : sizes) {
            for (String order : orders) {
                if (order.equals("native") && ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                    continue;
                }
                String[] options = {"--usage", "BINARY", "--binary-size", size, "--binary-byteorder", order};
                check("dump-binary-" + size + "-" + order, NumericDump::run, options);
                check("display-binary-" + size + "-" + order, NumericDisplay::run, options);
            }
        }
        check("display-binary-fullprint", NumericDisplay::run,
                "--usage", "BINARY", "--binary-size", "1--8", "--pretty-display", "no");
        check("dump-packed", NumericDump::run, "--usage", "PACKED-DECIMAL");
        check("display-packed", NumericDisplay::run, "--usage", "PACKED-DECIMAL");

        System.out.println(passed + " passed, " + failures + " failed");
        if (failures > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, BiConsumer<ProgramOptions, PrintStream> program, String... args)
            throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(buffer, true, StandardCharsets.US_ASCII)) {
            program.accept(ProgramOptions.parse(args), out);
        }
        String actual = buffer.toString(StandardCharsets.US_ASCII).replace(System.lineSeparator(), "\n");
        String expected = readExpected(name);
        if (actual.equals(expected)) {
            passed++;
            System.out.println("PASS " + name);
            return;
        }
        failures++;
        System.out.println("FAIL " + name);
        String[] a = actual.split("\n", -1);
        String[] e = expected.split("\n", -1);
        for (int i = 0; i < Math.max(a.length, e.length); i++) {
            String al = i < a.length ? a[i] : "<missing>";
            String el = i < e.length ? e[i] : "<missing>";
            if (!al.equals(el)) {
                System.out.println("  line " + (i + 1) + ": expected " + el + " but got " + al);
                break;
            }
        }
    }

    private static String readExpected(String name) throws IOException {
        try (InputStream in = DataRepTest.class.getResourceAsStream("/expected/" + name + ".txt")) {
            if (in == null) {
                throw new IOException("Missing expected output: " + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.US_ASCII);
        }
    }
}
