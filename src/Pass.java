import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pass {
    public static void main(String[] args) throws IOException {
        // Define valid eye colors as a Set
        Set<String> validEyeColors = new HashSet<>(Arrays.asList("amber", "blue", "brown", "gray", "green", "hazel", "other"));

        // Define required fields as a List
        List<String> requiredFields = Arrays.asList("born", "issued", "expires", "height", "hair", "eyes", "usmca");

        try (Stream<String> lines = Files.lines(Paths.get("data/pass.txt"))) {
            // Process data using Stream pipeline
            List<Map<String, String>> validRecords = Arrays.stream(lines.collect(Collectors.joining("\n")).split("\n{2}"))
                .map(record -> {
                    // Parse colon-separated key/value pairs into a HashMap
                    Map<String, String> obj = new HashMap<>();
                    Arrays.stream(record.replace("\n", " ").split(" "))
                        .forEach(str -> {
                            String[] parts = str.split(":");
                            obj.put(parts[0], parts[1]);
                        });
                    return obj;
                })
                .filter(obj -> {
                    // Check if all required fields except "state" are present and validate with simplified logic
                    return requiredFields.stream().allMatch(obj::containsKey) && simpleValidateLicense(obj, validEyeColors);
                })
                .limit(2)  // Limit to the first two valid records
                .collect(Collectors.toList());

            // Print each validated record
            validRecords.forEach(obj -> {
                System.out.println(repeat("-", 132));
                System.out.println(obj);
            });

            // Print the count of validated records separated by a double-dashed line
            System.out.println(repeat("=", 132));
            System.out.println("Valid records: " + validRecords.size());
        }
    }

    // Simplified validation to identify if records are being filtered incorrectly
    private static boolean simpleValidateLicense(Map<String, String> obj, Set<String> validEyeColors) {
        try {
            // Only basic checks on each field
            if (!obj.get("born").matches("\\d{4}")) return false;
            if (!obj.get("issued").matches("\\d{4}")) return false;
            if (!obj.get("expires").matches("\\d{4}")) return false;

            // Only check height format but skip specific range constraints
            String height = obj.get("height");
            if (!(height.endsWith("cm") || height.endsWith("in"))) return false;

            // Basic hair color, eye color, and license number checks
            if (!obj.get("hair").startsWith("#")) return false;
            if (!validEyeColors.contains(obj.get("eyes"))) return false;
            if (obj.get("usmca").length() != 9) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Loop-based method to repeat a string
    private static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
