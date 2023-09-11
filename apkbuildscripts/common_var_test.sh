source $(dirname $0)/common_var.txt

print_blue "\nTesting variables...\n"
print_green "PROJECT_DIR: $PROJECT_DIR\n"
print_green "OUTPUT_DIR: $OUTPUT_DIR\n"
print_green "test concat: "
echo "$PROJECT_DIR"app/build/outputs/apk/

print_blue "\nCHECKING EXPECTED VERSION NAME\n"
print_green "$expectedVersionName\n"

print_blue "\nCHECKING EXPECTED VERSION CODE\n"
print_green "$expectedVersionCode\n"

print_blue "\nTest End\n"
