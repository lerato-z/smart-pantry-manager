package com.smartpantry.app.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.smartpantry.app.model.MatchResult;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeMatcherTest {

    private static Recipe chakalaka() {
        List<RecipeIngredient> ingredients = Arrays.asList(
                new RecipeIngredient("Onion", 1, "pcs"),
                new RecipeIngredient("Tomato", 2, "pcs"),
                new RecipeIngredient("Carrot", 2, "pcs"),
                new RecipeIngredient("Baked beans", 410, "g"),
                new RecipeIngredient("Cooking oil", 2, "tbsp"));
        return new Recipe(1, "Chakalaka", "", 35, 6, ingredients, Collections.<String>emptyList());
    }

    private static List<PantryItem> fullPantry() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Onions", 2, "pcs", null));
        pantry.add(new PantryItem("tomatoes", 3, "pcs", null));
        pantry.add(new PantryItem("Carrots", 500, "g", null));
        pantry.add(new PantryItem("Baked Beans", 410, "g", null));
        pantry.add(new PantryItem("Sunflower oil", 1, "L", null));
        return pantry;
    }

    private static MatchResult evaluate(List<PantryItem> pantry) {
        return RecipeMatcher.evaluate(Collections.singletonList(chakalaka()), pantry).get(0);
    }

    @Test
    public void allIngredientsPresent_isStrictMatch() {
        assertTrue(evaluate(fullPantry()).isStrictMatch());
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.remove(3);
        MatchResult result = evaluate(pantry);
        assertFalse(result.isStrictMatch());
        assertTrue(result.isAlmostThere());
        assertEquals("Baked beans", result.getMissing().get(0));
    }

    @Test
    public void notEnoughQuantity_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.get(1).setQuantity(1);
        assertFalse(evaluate(pantry).isStrictMatch());
    }

    @Test
    public void quantitiesOfDuplicateItemsAreAdded() {
        List<PantryItem> pantry = fullPantry();
        pantry.get(1).setQuantity(1);
        pantry.add(new PantryItem("Tomato", 1, "pcs", null));
        assertTrue(evaluate(pantry).isStrictMatch());
    }

    @Test
    public void strictAndAlmostListsAreSeparate() {
        List<PantryItem> pantry = fullPantry();
        pantry.remove(0);
        List<MatchResult> results = RecipeMatcher.evaluate(Collections.singletonList(chakalaka()), pantry);
        assertEquals(0, RecipeMatcher.strictMatches(results).size());
        assertEquals(1, RecipeMatcher.almostThere(results).size());
    }
}
