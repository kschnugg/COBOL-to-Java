#!/bin/sh
# Regenerate src/test/resources/expected from the original COBOL templates.
# Requires an installed opensource COBOL (cobc on PATH) and a C compiler.
# Usage: ./compare-with-cobol.sh [output-dir]   (default: src/test/resources/expected)
set -e
cd "$(dirname "$0")"
here=$(pwd)
src=$here/../tests/data-rep.src
out=$(cd "${1:-src/test/resources/expected}" && pwd)
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
cd "$work"
: "${COB_CONFIG_DIR:=$here/../config}"
export COB_CONFIG_DIR COB_LIBRARY_PATH="$work"

dumper() {
  printf '#include <stdio.h>\nint dump (unsigned char *data) { int i; for (i = 0; i < %s; i++) printf ("%%02x", data[i]); puts (""); return 0; }\n' "$1" > dump.c
  ${CC:-cc} -shared -fPIC -o dump.so dump.c
}

# name usage template conf-text [extra cobc flags]
run() {
  sed -e "s/@USAGE@/$2/" "$src/$3.cob" > prog.cob
  printf '%b' "$4" > test.conf
  cobc -x $5 -conf="$work/test.conf" -o prog prog.cob 2>/dev/null
  ./prog > "$out/$1.txt"
  echo "wrote $1.txt"
}

dumper 8
for size in 2-4-8 1-2-4-8 1--8; do
  for order in big-endian native; do
    # Native fixtures hold little-endian output; DataRepTest skips them on big-endian hosts.
    if [ "$order" = native ] && [ "$(printf I | od -An -to2 | tr -d ' ')" != 000111 ]; then
      continue
    fi
    conf="include \"cobol2002.conf\"\nbinary-size: $size\nbinary-byteorder: $order\n"
    run "dump-binary-$size-$order" BINARY numeric-dump "$conf"
    run "display-binary-$size-$order" BINARY numeric-display "$conf"
  done
done
run display-binary-fullprint BINARY numeric-display 'include "cobol2002.conf"\nbinary-size: 1--8\npretty-display: no\n'
dumper 10
run dump-packed PACKED-DECIMAL numeric-dump 'include "default.conf"\n'
# -free: the PACKED-DECIMAL lines run past column 72 in fixed format.
run display-packed PACKED-DECIMAL numeric-display 'include "default.conf"\n' -free
