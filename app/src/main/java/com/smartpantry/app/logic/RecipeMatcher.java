package com.smartpantry.app.logic;

import com.smartpantry.app.logic.UnitConverter.Dimension;
import com.smartpantry.app.model.MatchResult;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strict-matching engine. A recipe is suggested only when EVERY required
 * ingredient is in the pantry in at least the required quantity.
 */
public final class RecipeMatcher {

    private RecipeMatcher() {
    }

    public static List<MatchResult> evaluate(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Map<Dimension, Double>> stock = buildStock(pantry);
        List<MatchResult> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            results.add(check(recipe, stock));
        }
        return results;
    }

    public static List<MatchResult> strictMatches(List<MatchResult> results) {
        List<MatchResult> strict = new ArrayList<>();
        for (MatchResult result : results) {
            if (result.isStrictMatch()) {
                strict.add(result);
            }
        }
        return strict;
    }

    public static List<MatchResult> almostThere(List<MatchResult> results) {
        List<MatchResult> almost = new ArrayList<>();
        for (MatchResult result : results) {
            if (result.isAlmostThere()) {
                almost.add(result);
            }
        }
        return almost;
    }

    /** Totals the pantry per ingredient key and per dimension, in base units. */
    static Map<String, Map<Dimension, Double>> buildStock(List<PantryItem> pantry) {
        Map<String, Map<Dimension, Double>> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = IngredientNormaliser.normalise(item.getName());
            Dimension dimension = UnitConverter.dimensionOf(item.getUnit());
            double base = UnitConverter.toBase(item.getQuantity(), item.getUnit());

            Map<Dimension, Double> perDimension = stock.get(key);
            if (perDimension == null) {
                perDimension = new EnumMap<>(Dimension.class);
                stock.put(key, perDimension);
            }
            Double current = perDimension.get(dimension);
            perDimension.put(dimension, (current == null ? 0 : current) + base);
        }
        return stock;
    }

    private static MatchResult check(Recipe recipe, Map<String, Map<Dimension, Double>> stock) {
        List<String> missing = new ArrayList<>();
        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = IngredientNormaliser.normalise(needed.getName());
            Dimension neededDim = UnitConverter.dimensionOf(needed.getUnit());
            double required = UnitConverter.toBase(needed.getQuantity(), needed.getUnit());
            double available = availableAmount(stock.get(key), neededDim, key);

            if (!UnitConverter.isEnough(available, required)) {
                missing.add(needed.getName());
            }
        }
        return new MatchResult(recipe, missing);
    }

    private static double availableAmount(Map<Dimension, Double> held, Dimension target, String key) {
        if (held == null) {
            return 0;
        }
        double total = 0;
        for (Map.Entry<Dimension, Double> entry : held.entrySet()) {
            Double converted = UnitConverter.convertBase(entry.getValue(), entry.getKey(), target, key);
            if (converted != null) {
                total += converted;
            }
        }
        return total;
    }
}
