package com.smartpantry.app.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IngredientNormaliserTest {

    @Test
    public void pluralsAndCaseAreIgnored() {
        assertEquals("tomato", IngredientNormaliser.normalise("Tomatoes"));
        assertEquals("tomato", IngredientNormaliser.normalise("  ripe TOMATO "));
        assertEquals("potato", IngredientNormaliser.normalise("potatoes"));
        assertEquals("egg", IngredientNormaliser.normalise("Eggs"));
        assertEquals("garlic clove", IngredientNormaliser.normalise("Garlic cloves"));
    }

    @Test
    public void localSynonymsMapToOneName() {
        assertEquals("maize meal", IngredientNormaliser.normalise("Mealie meal"));
        assertEquals("cooking oil", IngredientNormaliser.normalise("Sunflower oil"));
        assertEquals("beef mince", IngredientNormaliser.normalise("Mince"));
    }

    @Test
    public void wordsThatOnlyLookPluralAreKept() {
        assertEquals("rice", IngredientNormaliser.normalise("Rice"));
        assertEquals("couscous", IngredientNormaliser.normalise("Couscous"));
    }

    @Test
    public void unitsConvertWithinAndAcrossDimensions() {
        assertEquals(1000.0, UnitConverter.toBase(1, "kg"), 0.001);
        assertEquals(15.0, UnitConverter.toBase(1, "tbsp"), 0.001);
        Double eggsInGrams = UnitConverter.convertBase(3, UnitConverter.Dimension.COUNT,
                UnitConverter.Dimension.MASS, "egg");
        assertEquals(150.0, eggsInGrams, 0.001);
        assertTrue(UnitConverter.isEnough(500, 500));
        assertFalse(UnitConverter.isEnough(499, 500));
    }
}
