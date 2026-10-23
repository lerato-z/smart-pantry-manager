package com.smartpantry.app.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns free-typed ingredient names into one canonical key so that
 * "Tomatoes", "tomato" and " ripe Tomato " are treated as the same ingredient.
 * Both pantry items and recipe ingredients pass through this class,
 * so the comparison is always like-for-like.
 */
public final class IngredientNormaliser {

    private static final Set<String> DESCRIPTORS = new HashSet<>(Arrays.asList(
            "fresh", "ripe", "large", "medium", "small", "chopped", "diced", "sliced",
            "grated", "whole", "raw", "frozen", "organic", "of"));

    private static final Set<String> NO_SINGULAR = new HashSet<>(Arrays.asList(
            "rice", "couscous", "hummus", "molasses", "samp", "swiss", "asparagus", "citrus", "boerewors"));

    private static final Map<String, String> IRREGULAR = new HashMap<>();

    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        IRREGULAR.put("leaves", "leaf");
        IRREGULAR.put("halves", "half");
        IRREGULAR.put("loaves", "loaf");

        SYNONYMS.put("mealie meal", "maize meal");
        SYNONYMS.put("mielie meal", "maize meal");
        SYNONYMS.put("maizemeal", "maize meal");
        SYNONYMS.put("pap", "maize meal");
        SYNONYMS.put("bell pepper", "green pepper");
        SYNONYMS.put("sweet pepper", "green pepper");
        SYNONYMS.put("capsicum", "green pepper");
        SYNONYMS.put("green onion", "spring onion");
        SYNONYMS.put("scallion", "spring onion");
        SYNONYMS.put("cilantro", "coriander");
        SYNONYMS.put("dhania", "coriander");
        SYNONYMS.put("chili", "chilli");
        SYNONYMS.put("chile", "chilli");
        SYNONYMS.put("mince", "beef mince");
        SYNONYMS.put("minced beef", "beef mince");
        SYNONYMS.put("ground beef", "beef mince");
        SYNONYMS.put("flour", "cake flour");
        SYNONYMS.put("all purpose flour", "cake flour");
        SYNONYMS.put("plain flour", "cake flour");
        SYNONYMS.put("oil", "cooking oil");
        SYNONYMS.put("sunflower oil", "cooking oil");
        SYNONYMS.put("vegetable oil", "cooking oil");
        SYNONYMS.put("cheddar", "cheese");
        SYNONYMS.put("cheddar cheese", "cheese");
        SYNONYMS.put("gouda", "cheese");
        SYNONYMS.put("gouda cheese", "cheese");
        SYNONYMS.put("bread", "bread slice");
        SYNONYMS.put("slice bread", "bread slice");
        SYNONYMS.put("white bread", "bread slice");
        SYNONYMS.put("brown bread", "bread slice");
        SYNONYMS.put("garlic", "garlic clove");
        SYNONYMS.put("clove garlic", "garlic clove");
        SYNONYMS.put("bicarb", "bicarbonate soda");
        SYNONYMS.put("baking soda", "bicarbonate soda");
        SYNONYMS.put("bicarbonate", "bicarbonate soda");
        SYNONYMS.put("chicken", "chicken piece");
        SYNONYMS.put("stock", "stock cube");
        SYNONYMS.put("butternut squash", "butternut");
        SYNONYMS.put("mayo", "mayonnaise");
    }

    private IngredientNormaliser() {
    }

    public static String normalise(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        StringBuilder builder = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            if (word.isEmpty() || DESCRIPTORS.contains(word)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(singular(word));
        }

        String key = builder.toString();
        String canonical = SYNONYMS.get(key);
        return canonical != null ? canonical : key;
    }

    static String singular(String word) {
        if (word.length() <= 3 || NO_SINGULAR.contains(word)) {
            return word;
        }
        String irregular = IRREGULAR.get(word);
        if (irregular != null) {
            return irregular;
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
