package calculator;

import java.util.HashMap;

public class KeyConverter extends HashMap<String, String> {
    public static final KeyConverter converter;

    public String replace(String value) {
        if (containsKey(value)) {
            return get(value);
        }
        return value;
    }

    static {
        converter = new KeyConverter();

        converter.put("*", "×");
        converter.put("/", "÷");
    }
}
