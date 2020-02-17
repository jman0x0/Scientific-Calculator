package calculator;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyConverter extends HashMap<String, String> {
    public static final KeyConverter converter;

    /**
     * Find the mapped string.
     * @param value The string to process.
     * @return The substituted mapping, or the original string.
     */
    public String replace(String value) {
        if (containsKey(value)) {
            return get(value);
        }
        return value;
    }

    public ArrayList<String> getAllConversions(String target) {
        final ArrayList<String> keys = new ArrayList<>();
        for (var entry : entrySet()) {
            if (entry.getValue().equals(target)) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    static {
        converter = new KeyConverter();

        converter.put("*", "×");
        converter.put("/", "÷");
        converter.put("!=", "≠");
        converter.put("<=", "≤");
        converter.put(">=", "≥");
        converter.put("pi", "π");
        converter.put("phi", "φ");
    }
}
