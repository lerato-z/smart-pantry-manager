package com.smartpantry.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its required ingredients and numbered preparation steps.
 */
public class Recipe {

    private long id;
    private final String name;
    private final String description;
    private final int prepMinutes;
    private final int servings;
    private final List<RecipeIngredient> ingredients;
    private final List<String> steps;

    public Recipe(long id, String name, String description, int prepMinutes, int servings,
                  List<RecipeIngredient> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepMinutes = prepMinutes;
        this.servings = servings;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<RecipeIngredient>();
        this.steps = steps != null ? steps : new ArrayList<String>();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrepMinutes() { return prepMinutes; }
    public int getServings() { return servings; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
}
