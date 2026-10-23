package com.smartpantry.app.logic;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts kitchen quantities to one base unit per dimension
 * (grams for mass, millilitres for volume, pieces for countable items)
 * so that "1 kg" in the pantry can be compared with "500 g" in a recipe.
 * Densities and average piece weights allow a limited, safe crossover
 * between dimensions, e.g. "3 eggs" against "150 g egg".
 */
public final class UnitConverter {

    public enum Dimension { MASS, VOLUME, COUNT }

    public static final String[] UNITS = {"g", "kg", "ml", "L", "tsp", "tbsp", "cup", "pcs"};

    private static final double EPSILON = 0.0001;

    private static final Map<String, Double> FACTOR_TO_BASE = new HashMap<>();
    private static final Map<String, Dimension> DIMENSION = new HashMap<>();
    private static final Map<String, Double> DENSITY_G_PER_ML = new HashMap<>();
    private static final Map<String, Double> PIECE_WEIGHT_G = new HashMap<>();

    static {
        register("g", 1, Dimension.MASS);
        register("kg", 1000, Dimension.MASS);
        register("ml", 1, Dimension.VOLUME);
        register("l", 1000, Dimension.VOLUME);
        register("tsp", 5, Dimension.VOLUME);
        register("tbsp", 15, Dimension.VOLUME);
        register("cup", 250, Dimension.VOLUME);
        register("pcs", 1, Dimension.COUNT);

        DENSITY_G_PER_ML.put("cake flour", 0.53);
        DENSITY_G_PER_ML.put("maize meal", 0.60);
        DENSITY_G_PER_ML.put("sugar", 0.85);
        DENSITY_G_PER_ML.put("brown sugar", 0.90);
        DENSITY_G_PER_ML.put("salt", 1.20);
        DENSITY_G_PER_ML.put("rice", 0.85);
        DENSITY_G_PER_ML.put("samp", 0.80);
        DENSITY_G_PER_ML.put("sugar bean", 0.80);
        DENSITY_G_PER_ML.put("lentil", 0.85);
        DENSITY_G_PER_ML.put("milk", 1.03);
        DENSITY_G_PER_ML.put("cream", 1.00);
        DENSITY_G_PER_ML.put("cooking oil", 0.92);
        DENSITY_G_PER_ML.put("butter", 0.96);
        DENSITY_G_PER_ML.put("curry powder", 0.45);
        DENSITY_G_PER_ML.put("cinnamon", 0.55);
        DENSITY_G_PER_ML.put("black pepper", 0.50);
        DENSITY_G_PER_ML.put("baking powder", 0.90);
        DENSITY_G_PER_ML.put("bicarbonate soda", 0.90);
        DENSITY_G_PER_ML.put("instant yeast", 0.60);
        DENSITY_G_PER_ML.put("apricot jam", 1.30);
        DENSITY_G_PER_ML.put("mayonnaise", 0.95);
        DENSITY_G_PER_ML.put("vinegar", 1.00);
        DENSITY_G_PER_ML.put("tomato sauce", 1.10);

        PIECE_WEIGHT_G.put("egg", 50.0);
        PIECE_WEIGHT_G.put("onion", 150.0);
        PIECE_WEIGHT_G.put("tomato", 120.0);
        PIECE_WEIGHT_G.put("potato", 170.0);
        PIECE_WEIGHT_G.put("carrot", 70.0);
        PIECE_WEIGHT_G.put("green pepper", 150.0);
        PIECE_WEIGHT_G.put("garlic clove", 5.0);
        PIECE_WEIGHT_G.put("bread slice", 35.0);
        PIECE_WEIGHT_G.put("spring onion", 15.0);
        PIECE_WEIGHT_G.put("chilli", 10.0);
        PIECE_WEIGHT_G.put("stock cube", 10.0);
        PIECE_WEIGHT_G.put("chicken piece", 150.0);
        PIECE_WEIGHT_G.put("butternut", 1000.0);
        PIECE_WEIGHT_G.put("cabbage", 1000.0);
        PIECE_WEIGHT_G.put("lemon", 100.0);
        PIECE_WEIGHT_G.put("banana", 120.0);
    }

    private UnitConverter() {
    }

    private static void register(String unit, double factor, Dimension dimension) {
        FACTOR_TO_BASE.put(unit, factor);
        DIMENSION.put(unit, dimension);
    }

    public static String cleanUnit(String unit) {
        if (unit == null) {
            return "pcs";
        }
        String u = unit.trim().toLowerCase(Locale.ROOT);
        if (u.isEmpty() || u.equals("piece") || u.equals("pieces") || u.equals("each") || u.equals("whole")) {
            return "pcs";
        }
        if (u.equals("gram") || u.equals("grams")) return "g";
        if (u.equals("litre") || u.equals("liter") || u.equals("litres") || u.equals("liters")) return "l";
        if (u.equals("cups")) return "cup";
        return u;
    }

    public static boolean isKnownUnit(String unit) {
        return FACTOR_TO_BASE.containsKey(cleanUnit(unit));
    }

    public static Dimension dimensionOf(String unit) {
        Dimension d = DIMENSION.get(cleanUnit(unit));
        return d != null ? d : Dimension.COUNT;
    }

    public static double toBase(double quantity, String unit) {
        Double factor = FACTOR_TO_BASE.get(cleanUnit(unit));
        return quantity * (factor != null ? factor : 1);
    }

    /**
     * Expresses a base quantity of one dimension in the base unit of another.
     * Returns null when no safe conversion exists for this ingredient.
     */
    public static Double convertBase(double baseQuantity, Dimension from, Dimension to, String ingredientKey) {
        if (from == to) {
            return baseQuantity;
        }
        Double grams = toGrams(baseQuantity, from, ingredientKey);
        if (grams == null) {
            return null;
        }
        return fromGrams(grams, to, ingredientKey);
    }

    private static Double toGrams(double value, Dimension from, String key) {
        if (from == Dimension.MASS) return value;
        if (from == Dimension.VOLUME) {
            Double density = DENSITY_G_PER_ML.get(key);
            return density != null ? value * density : null;
        }
        Double piece = PIECE_WEIGHT_G.get(key);
        return piece != null ? value * piece : null;
    }

    private static Double fromGrams(double grams, Dimension to, String key) {
        if (to == Dimension.MASS) return grams;
        if (to == Dimension.VOLUME) {
            Double density = DENSITY_G_PER_ML.get(key);
            return density != null ? grams / density : null;
        }
        Double piece = PIECE_WEIGHT_G.get(key);
        return piece != null ? grams / piece : null;
    }

    public static boolean isEnough(double available, double required) {
        return available + EPSILON >= required;
    }

    public static String baseUnitLabel(Dimension dimension) {
        if (dimension == Dimension.MASS) return "g";
        if (dimension == Dimension.VOLUME) return "ml";
        return "pcs";
    }

    public static String format(double quantity) {
        DecimalFormat df = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(quantity);
    }

    public static String describe(double quantity, String unit, boolean metricBase) {
        if (metricBase) {
            Dimension d = dimensionOf(unit);
            return format(toBase(quantity, unit)) + " " + baseUnitLabel(d);
        }
        String clean = cleanUnit(unit);
        return format(quantity) + " " + (clean.equals("l") ? "L" : clean);
    }
}
