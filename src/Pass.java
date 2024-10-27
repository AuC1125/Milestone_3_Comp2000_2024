import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pass {
    public static void main(String[] args) throws IOException {
        Set<String> validEyeColors = Set.of("amber", "blue", "brown", "gray", "green", "hazel", "other");
        List<String> requiredFields = List.of("born", "issued", "expires", "height", "hair", "eyes", "usmca");

        try (Stream<String> lines = Files.lines(Paths.get("data/pass.txt"))) {
            List<Map<String, String>> validRecords = Arrays.stream(lines.collect(Collectors.joining("\n")).split("\n{2}"))
                .map(Pass::parseRecord)
                .filter(record -> isValidRecord(record, requiredFields, validEyeColors))
                .peek(record -> System.out.println(repeat("-", 132) + "\n" + record))
                .limit(2)
                .collect(Collectors.toList());

            System.out.println(repeat("=", 132));
            System.out.println("Valid records: " + validRecords.size());
        }
    }

    private static Map<String, String> parseRecord(String record) {
        return Arrays.stream(record.replace("\n", " ").split(" "))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private static boolean isValidRecord(Map<String, String> record, List<String> requiredFields, Set<String> validEyeColors) {
        int currentYear = Year.now().getValue();
        try {
            return requiredFields.stream().allMatch(record::containsKey) &&
                   isOfValidAge(record.get("born"), currentYear) &&
                   isValidIssuedYear(record.get("issued"), currentYear) &&
                   isValidExpiryYear(record.get("expires"), currentYear) &&
                   isValidHeight(record.get("height")) &&
                   isValidHairColor(record.get("hair")) &&
                   validEyeColors.contains(record.get("eyes")) &&
                   record.get("usmca").matches("\\d{9}");
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isOfValidAge(String born, int currentYear) {
        return currentYear - Integer.parseInt(born) >= 21;
    }

    private static boolean isValidIssuedYear(String issued, int currentYear) {
        int issuedYear = Integer.parseInt(issued);
        return issuedYear <= currentYear && currentYear - issuedYear <= 10;
    }

    private static boolean isValidExpiryYear(String expires, int currentYear) {
        int expiryYear = Integer.parseInt(expires);
        return expiryYear >= currentYear && expiryYear - currentYear <= 10;
    }

    private static boolean isValidHeight(String height) {
        if (height.endsWith("cm")) {
            int heightCm = Integer.parseInt(height.replace("cm", ""));
            return heightCm >= 150 && heightCm <= 193;
        } else if (height.endsWith("in")) {
            int heightIn = Integer.parseInt(height.replace("in", ""));
            return heightIn >= 59 && heightIn <= 76;
        }
        return false;
    }

    private static boolean isValidHairColor(String hair) {
        return hair.matches("#[0-9a-fA-F]{6}");
    }

    private static String repeat(String str, int times) {
        return str.repeat(times);
    }
}
