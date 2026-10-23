package com.smartpantry.app.data;

import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The 20 South African home-cooking recipes loaded on first run,
 * plus an optional sample pantry used for demonstrations.
 */
public final class SeedData {

    private SeedData() {
    }

    public static List<Recipe> recipes() {
        List<Recipe> list = new ArrayList<>();

        list.add(recipe("Stiff Pap", "Classic firm maize-meal porridge served with any stew or relish.", 30, 4,
                new String[][]{{"Maize meal", "500", "g"}, {"Salt", "1", "tsp"}, {"Butter", "15", "g"}},
                "Bring 1 litre of water and the salt to the boil in a heavy pot.",
                "Pour in a third of the maize meal and stir into a thin porridge; cover and simmer for 5 minutes.",
                "Add the rest of the maize meal and mix firmly with a wooden spoon until no dry spots remain.",
                "Cover, lower the heat and steam for 20 minutes, stirring once halfway.",
                "Stir in the butter and serve hot."));

        list.add(recipe("Chakalaka", "Spicy vegetable relish, a braai-day favourite.", 35, 6,
                new String[][]{{"Onion", "1", "pcs"}, {"Green pepper", "1", "pcs"}, {"Carrot", "2", "pcs"},
                        {"Tomato", "2", "pcs"}, {"Baked beans", "410", "g"}, {"Curry powder", "2", "tsp"},
                        {"Cooking oil", "2", "tbsp"}, {"Garlic clove", "2", "pcs"}},
                "Heat the oil and fry the chopped onion and crushed garlic until soft.",
                "Add the curry powder and fry for one minute.",
                "Add the diced green pepper and grated carrot and cook for 5 minutes.",
                "Stir in the chopped tomatoes and simmer for 10 minutes.",
                "Add the baked beans, heat through and season to taste."));

        list.add(recipe("Scrambled Eggs", "Soft, creamy eggs ready in five minutes.", 5, 1,
                new String[][]{{"Egg", "3", "pcs"}, {"Milk", "60", "ml"}, {"Butter", "15", "g"}, {"Salt", "0.25", "tsp"}},
                "Whisk the eggs, milk and salt together in a bowl.",
                "Melt the butter in a pan over low heat.",
                "Pour in the eggs and stir slowly until just set.",
                "Serve immediately."));

        list.add(recipe("Vetkoek", "Golden fried dough balls, great with mince or jam.", 90, 8,
                new String[][]{{"Cake flour", "500", "g"}, {"Instant yeast", "10", "g"}, {"Sugar", "1", "tbsp"},
                        {"Salt", "1", "tsp"}, {"Cooking oil", "500", "ml"}},
                "Mix the flour, yeast, sugar and salt in a large bowl.",
                "Add about 350 ml lukewarm water and knead into a soft dough.",
                "Cover and leave to rise in a warm place for 1 hour.",
                "Heat the oil and fry spoonfuls of dough until golden on both sides.",
                "Drain on paper towel."));

        list.add(recipe("Savoury Mince", "Everyday mince in a rich tomato gravy.", 35, 4,
                new String[][]{{"Beef mince", "500", "g"}, {"Onion", "1", "pcs"}, {"Tomato", "2", "pcs"},
                        {"Curry powder", "1", "tsp"}, {"Cooking oil", "1", "tbsp"}, {"Stock cube", "1", "pcs"}},
                "Fry the chopped onion in the oil until soft.",
                "Add the mince and brown it, breaking up any lumps.",
                "Add the curry powder, chopped tomatoes and crumbled stock cube.",
                "Add 125 ml water and simmer for 20 minutes until thick."));

        list.add(recipe("Morogo with Onion and Tomato", "Wilted spinach cooked the traditional way.", 20, 4,
                new String[][]{{"Spinach", "300", "g"}, {"Onion", "1", "pcs"}, {"Tomato", "1", "pcs"},
                        {"Cooking oil", "1", "tbsp"}, {"Salt", "0.5", "tsp"}},
                "Wash and roughly chop the spinach.",
                "Fry the onion in the oil until soft, then add the chopped tomato.",
                "Add the spinach and salt, cover and cook for 8 minutes.",
                "Stir and serve with pap."));

        list.add(recipe("Umngqusho (Samp and Beans)", "Slow-cooked Xhosa staple of samp and sugar beans.", 180, 6,
                new String[][]{{"Samp", "500", "g"}, {"Sugar beans", "250", "g"}, {"Onion", "1", "pcs"},
                        {"Stock cube", "1", "pcs"}, {"Salt", "1", "tsp"}},
                "Soak the samp and beans overnight, then rinse.",
                "Cover with fresh water and boil for about 2 hours until soft.",
                "Fry the onion and add it with the stock cube and salt.",
                "Simmer for 30 minutes more, adding water if needed, until creamy."));

        list.add(recipe("Bobotie", "Cape Malay spiced mince baked under a savoury custard.", 60, 6,
                new String[][]{{"Beef mince", "500", "g"}, {"Onion", "1", "pcs"}, {"Bread slice", "1", "pcs"},
                        {"Milk", "250", "ml"}, {"Egg", "2", "pcs"}, {"Curry powder", "1", "tbsp"},
                        {"Apricot jam", "2", "tbsp"}, {"Cooking oil", "1", "tbsp"}},
                "Soak the bread in half the milk, then squeeze it out and keep the milk.",
                "Fry the onion in the oil, add the curry powder and mince and brown well.",
                "Stir in the soaked bread and the apricot jam and spoon into a baking dish.",
                "Whisk the eggs with the remaining milk and pour over the mince.",
                "Bake at 180 °C for 35 minutes until the topping is set."));

        list.add(recipe("Malva Pudding", "Sticky, spongy baked dessert with a warm cream sauce.", 60, 8,
                new String[][]{{"Cake flour", "150", "g"}, {"Sugar", "200", "g"}, {"Egg", "1", "pcs"},
                        {"Apricot jam", "1", "tbsp"}, {"Butter", "30", "g"}, {"Milk", "125", "ml"},
                        {"Bicarbonate soda", "1", "tsp"}, {"Vinegar", "1", "tsp"}, {"Cream", "250", "ml"}},
                "Beat the egg with half of the sugar and the jam until pale.",
                "Melt half of the butter with the vinegar and milk.",
                "Fold the flour, bicarbonate soda and milk mixture into the egg mixture.",
                "Bake at 180 °C for 40 minutes.",
                "Heat the cream, remaining sugar and butter and pour over the hot pudding."));

        list.add(recipe("Potato and Egg Salad", "Creamy braai-side salad.", 30, 6,
                new String[][]{{"Potato", "4", "pcs"}, {"Egg", "3", "pcs"}, {"Mayonnaise", "125", "ml"},
                        {"Spring onion", "2", "pcs"}},
                "Boil the potatoes until tender, cool and cube them.",
                "Hard-boil the eggs, cool, peel and chop.",
                "Mix the potatoes, eggs, sliced spring onion and mayonnaise.",
                "Chill before serving."));

        list.add(recipe("Tomato and Onion Smoor", "Simple tomato relish for pap and wors.", 25, 4,
                new String[][]{{"Tomato", "4", "pcs"}, {"Onion", "2", "pcs"}, {"Sugar", "1", "tsp"},
                        {"Cooking oil", "1", "tbsp"}, {"Salt", "0.5", "tsp"}},
                "Fry the sliced onions in the oil until golden.",
                "Add the chopped tomatoes, sugar and salt.",
                "Simmer for 15 minutes until thick."));

        list.add(recipe("Pumpkin Fritters", "Pampoenkoekies dusted with cinnamon sugar.", 40, 6,
                new String[][]{{"Pumpkin", "500", "g"}, {"Cake flour", "125", "g"}, {"Egg", "1", "pcs"},
                        {"Baking powder", "1", "tsp"}, {"Cinnamon", "1", "tsp"}, {"Sugar", "2", "tbsp"},
                        {"Cooking oil", "125", "ml"}},
                "Boil the pumpkin until soft, drain well and mash.",
                "Mix in the egg, flour and baking powder to make a thick batter.",
                "Shallow-fry spoonfuls in hot oil until golden on both sides.",
                "Mix the sugar and cinnamon and sprinkle over the fritters."));

        list.add(recipe("Chicken and Potato Stew", "One-pot Sunday stew.", 70, 6,
                new String[][]{{"Chicken pieces", "6", "pcs"}, {"Potato", "3", "pcs"}, {"Carrot", "2", "pcs"},
                        {"Onion", "1", "pcs"}, {"Stock cube", "1", "pcs"}, {"Cooking oil", "2", "tbsp"}},
                "Brown the chicken pieces in the oil and remove from the pot.",
                "Fry the onion until soft.",
                "Return the chicken, add the cubed potatoes and carrots.",
                "Dissolve the stock cube in 500 ml water, pour in and simmer for 45 minutes."));

        list.add(recipe("Bean Bunny Chow", "Durban-style hollowed loaf filled with bean curry.", 35, 2,
                new String[][]{{"Bread loaf", "1", "pcs"}, {"Baked beans", "410", "g"}, {"Onion", "1", "pcs"},
                        {"Tomato", "1", "pcs"}, {"Curry powder", "1", "tbsp"}, {"Cooking oil", "1", "tbsp"}},
                "Fry the onion in the oil, add the curry powder and cook for one minute.",
                "Add the chopped tomato and simmer for 5 minutes.",
                "Stir in the baked beans and heat through.",
                "Cut the loaf in half, hollow it out and fill with the curry."));

        list.add(recipe("Mealie Bread", "Moist sweetcorn bread, steamed or baked.", 60, 8,
                new String[][]{{"Sweetcorn", "410", "g"}, {"Cake flour", "250", "g"}, {"Egg", "2", "pcs"},
                        {"Baking powder", "2", "tsp"}, {"Sugar", "2", "tbsp"}, {"Salt", "1", "tsp"},
                        {"Butter", "50", "g"}},
                "Melt the butter and mix it with the eggs and sweetcorn.",
                "Sift in the flour, baking powder, sugar and salt and mix well.",
                "Pour into a greased loaf tin.",
                "Bake at 180 °C for 45 minutes."));

        list.add(recipe("French Toast", "Eggy bread for a quick breakfast.", 15, 2,
                new String[][]{{"Bread slice", "4", "pcs"}, {"Egg", "2", "pcs"}, {"Milk", "125", "ml"},
                        {"Sugar", "1", "tbsp"}, {"Butter", "20", "g"}, {"Cinnamon", "0.5", "tsp"}},
                "Whisk the eggs, milk, sugar and cinnamon.",
                "Dip each slice of bread into the mixture.",
                "Fry in the butter until golden on both sides."));

        list.add(recipe("South African Pancakes", "Thin pancakes rolled with cinnamon sugar.", 30, 4,
                new String[][]{{"Cake flour", "250", "g"}, {"Egg", "2", "pcs"}, {"Milk", "500", "ml"},
                        {"Cooking oil", "2", "tbsp"}, {"Salt", "0.25", "tsp"}, {"Sugar", "3", "tbsp"},
                        {"Cinnamon", "1", "tsp"}},
                "Whisk the flour, salt, eggs, milk and oil into a smooth, thin batter.",
                "Rest the batter for 15 minutes.",
                "Pour a thin layer into a hot greased pan and cook both sides.",
                "Sprinkle with cinnamon sugar and roll up."));

        list.add(recipe("Fried Cabbage", "Quick buttery cabbage side dish.", 20, 4,
                new String[][]{{"Cabbage", "500", "g"}, {"Onion", "1", "pcs"}, {"Cooking oil", "2", "tbsp"},
                        {"Salt", "0.5", "tsp"}, {"Black pepper", "0.25", "tsp"}},
                "Fry the sliced onion in the oil until soft.",
                "Add the shredded cabbage, salt and pepper.",
                "Stir-fry for 10 minutes until just tender."));

        list.add(recipe("Butternut Soup", "Smooth, warming winter soup.", 45, 4,
                new String[][]{{"Butternut", "1", "kg"}, {"Onion", "1", "pcs"}, {"Stock cube", "1", "pcs"},
                        {"Cream", "125", "ml"}, {"Butter", "20", "g"}},
                "Melt the butter and fry the onion until soft.",
                "Add the peeled, cubed butternut and the stock cube dissolved in 750 ml water.",
                "Simmer for 25 minutes until the butternut is soft.",
                "Blend until smooth and stir in the cream."));

        list.add(recipe("Lentil Curry with Rice", "Budget-friendly, filling lentil curry.", 45, 4,
                new String[][]{{"Lentils", "250", "g"}, {"Rice", "250", "g"}, {"Onion", "1", "pcs"},
                        {"Tomato", "2", "pcs"}, {"Curry powder", "1", "tbsp"}, {"Garlic clove", "2", "pcs"},
                        {"Cooking oil", "1", "tbsp"}},
                "Cook the rice according to the packet instructions.",
                "Fry the onion and garlic in the oil, then add the curry powder.",
                "Add the tomatoes, rinsed lentils and 750 ml water.",
                "Simmer for 30 minutes until the lentils are soft and serve with the rice."));

        return list;
    }

    /** Sample pantry: {name, quantity, unit, days until expiry or empty}. */
    public static String[][] samplePantry() {
        return new String[][]{
                {"Eggs", "6", "pcs", "10"},
                {"Milk", "1", "L", "2"},
                {"Butter", "250", "g", "30"},
                {"Salt", "500", "g", ""},
                {"Onions", "4", "pcs", "14"},
                {"Tomatoes", "5", "pcs", "4"},
                {"Cooking oil", "750", "ml", ""},
                {"Mealie meal", "2.5", "kg", ""},
                {"Potatoes", "1", "kg", "20"},
                {"Spinach", "300", "g", "3"},
                {"Curry powder", "50", "g", ""},
                {"Sugar", "1", "kg", ""},
                {"Cake flour", "1", "kg", ""}
        };
    }

    private static Recipe recipe(String name, String description, int minutes, int servings,
                                 String[][] ingredients, String... steps) {
        List<RecipeIngredient> list = new ArrayList<>();
        for (String[] row : ingredients) {
            list.add(new RecipeIngredient(row[0], Double.parseDouble(row[1]), row[2]));
        }
        return new Recipe(-1, name, description, minutes, servings, list, Arrays.asList(steps));
    }
}
