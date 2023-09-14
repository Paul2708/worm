package de.paul2708.worm.attributes.util;

public class AttributeNameTransformer {

    private AttributeNameTransformer() {

    }

    public static String transform(String attributeName) {
        String[] words = attributeName.split("[-_]");

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
