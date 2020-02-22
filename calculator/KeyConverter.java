package calculator;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyConverter extends HashMap<String, String> {
    public static final KeyConverter converter;
    public static class FieldEdit {
        String text;
        int cursor;

        public FieldEdit(String text, int cursor) {
            this.text = text;
            this.cursor = cursor;
        }
    }

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

    public String replaceAll(String str) {
        final StringBuilder builder = new StringBuilder(str);
        for (var keypair : KeyConverter.converter.entrySet()) {
            final String key = keypair.getKey();
            final String value = keypair.getValue();
            int index = 0;

            //Replace all characters.
            while ((index = builder.indexOf(key, index)) != -1) {
                builder.delete(index, index+key.length());
                builder.insert(index, value);
                index += key.length();
            }
        }
        return builder.toString();
    }

    public FieldEdit replaceAll(String str, int cursor) {
        final StringBuilder builder = new StringBuilder(str);
        for (var keypair : KeyConverter.converter.entrySet()) {
            final String key = keypair.getKey();
            final String value = keypair.getValue();

            //Replace all characters.
            int index = 0;
            while ((index = builder.indexOf(key, index)) >= 0) {
                final int delta = value.length() - key.length();
                builder.delete(index, index+key.length());
                builder.insert(index, value);
                if (cursor >= index && cursor - index > value.length()) {
                    cursor += Math.max(value.length() + index - cursor, delta);
                }
                index += delta;
            }
        }
        return new FieldEdit(builder.toString(), cursor);
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
