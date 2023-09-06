package de.paul2708.worm.columns.util;

public class ColumnNameTransformer {

    private ColumnNameTransformer() {

    }

    public static String transform(String columnName) {
        String[] words = columnName.split("[-_]");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                String transformedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                result.append(transformedWord);
            }
        }

        return result.toString();
    }
}
