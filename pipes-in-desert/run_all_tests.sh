#!/bin/bash

# run_all_tests.sh - Runs all test cases

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAIN_CLASS="software.project.Main"
CLASSES_DIR="$PROJECT_DIR/target/classes"
INPUT_DIR="$PROJECT_DIR/test-inputs"
OUTPUT_DIR="$PROJECT_DIR/test-outputs"

TOTAL=0
SKIPPED=0

# Width for the test name column (adjust if needed)
NAME_WIDTH=45

# Colors
GREEN='\033[0;32m'
CYAN='\033[0;36m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

mkdir -p "$OUTPUT_DIR"

echo -e "${RED}Running Tests${NC}"
echo ""

for input_file in "$INPUT_DIR"/*; do
    # Skip directories
    [ -f "$input_file" ] || continue

    filename=$(basename "$input_file")
    base="${filename%.in}"

    # Skip files that don't end with .in
    if [[ "$filename" != *.in ]]; then
        echo -e "${YELLOW}[SKIP]${NC} Ignoring ${CYAN}${filename}${NC} (not a .in file)"
        SKIPPED=$((SKIPPED + 1))
        continue
    fi

    # Skip files that don't start with "test"
    if [[ "$base" != test* ]]; then
        echo -e "${YELLOW}[SKIP]${NC} Ignoring ${CYAN}${filename}${NC} (does not start with 'test')"
        SKIPPED=$((SKIPPED + 1))
        continue
    fi

    output_file="$OUTPUT_DIR/${base}.out"

    TOTAL=$((TOTAL + 1))

    printf "${GREEN}[%02d]${NC} Running ${CYAN}%-*s${NC} " "$TOTAL" "$NAME_WIDTH" "$base"

    java -cp "$CLASSES_DIR" "$MAIN_CLASS" "$input_file" "$output_file" 2>/dev/null

    echo -e "${GREEN}DONE${NC}"
done

echo ""
if [ "$SKIPPED" -gt 0 ]; then
    echo -e "${YELLOW}${SKIPPED} file(s) skipped.${NC}"
fi
echo -e "${GREEN}All ${TOTAL} tests completed successfully.${NC}"