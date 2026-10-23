package com.smartpantry.app.model;

import java.util.List;

/**
 * Outcome of checking one recipe against the pantry.
 * A recipe is a strict match only when nothing is missing.
 */
public class MatchResult {

    private final Recipe recipe;
    private final List<String> missing;

    public MatchResult(Recipe recipe, List<String> missing) {
        this.recipe = recipe;
        this.missing = missing;
    }

    public Recipe getRecipe() { return recipe; }
    public List<String> getMissing() { return missing; }

    public boolean isStrictMatch() {
        return missing.isEmpty();
    }

    public boolean isAlmostThere() {
        return missing.size() == 1;
    }
}
