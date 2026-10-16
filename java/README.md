# Java versions of the data-rep COBOL test programs

Java ports of the two COBOL programs in `tests/data-rep.src`:

| COBOL template | Java class | What it does |
| --- | --- | --- |
| `numeric-display.cob` | `jp.osscons.datarep.NumericDisplay` | DISPLAYs `PIC 9(1)`..`9(18)` and `S9(1)`..`S9(18)` items |
| `numeric-dump.cob` | `jp.osscons.datarep.NumericDump` | Hex-dumps the storage of the same items, then again after `INITIALIZE` and after `MOVE ZERO` |

The COBOL files are templates: the test suite replaces `@USAGE@` and passes a
`-conf` file. The Java programs take the same choices as options:

```
--usage BINARY | PACKED-DECIMAL        (default BINARY)
--binary-size 2-4-8 | 1-2-4-8 | 1--8   (default 1-2-4-8)
--binary-byteorder big-endian | native (default big-endian)
--pretty-display yes | no              (default yes)
```

## Run

Needs Java 11 or newer. No other dependencies.

```
./run-tests.sh
java -cp build/classes jp.osscons.datarep.NumericDump --usage BINARY --binary-size 2-4-8
java -cp build/classes jp.osscons.datarep.NumericDisplay --usage BINARY --pretty-display no
```

`run-tests.sh` runs every configuration used by `tests/data-rep.src/binary.at`
and `packed.at`, and compares the output byte-for-byte with what the compiled
COBOL programs print (`src/test/resources/expected`).

`compare-with-cobol.sh` regenerates those expected files from the COBOL
templates. It needs `cobc` from this repository installed and on `PATH`.
