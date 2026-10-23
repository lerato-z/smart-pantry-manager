package com.smartpantry.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartpantry.app.data.DatabaseHelper;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.logic.UnitConverter;
import com.smartpantry.app.model.MatchResult;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.RecipeIngredient;

import java.util.Collections;
import java.util.List;

/**
 * Recipe Detail screen: full ingredient list (with an in-pantry / missing marker)
 * and the numbered method. The recipe id arrives through the launching Intent.
 */
public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "com.smartpantry.app.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = db.getRecipe(recipeId);
        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setupToolbar(recipe.getName(), true);

        MatchResult match = RecipeMatcher.evaluate(
                Collections.singletonList(recipe), db.getAllPantryItems()).get(0);

        ((TextView) findViewById(R.id.text_detail_description)).setText(recipe.getDescription());
        ((TextView) findViewById(R.id.text_detail_meta)).setText(getString(R.string.recipe_meta,
                recipe.getPrepMinutes(), recipe.getServings(), recipe.getIngredients().size()));
        bindStatus(match);
        bindIngredients(recipe.getIngredients(), match.getMissing());
        bindSteps(recipe.getSteps());
    }

    private void bindStatus(MatchResult match) {
        TextView status = findViewById(R.id.text_detail_status);
        if (match.isStrictMatch()) {
            status.setText(R.string.status_ready);
            status.setBackgroundResource(R.drawable.bg_status_ready);
        } else {
            status.setText(getResources().getQuantityString(R.plurals.status_missing,
                    match.getMissing().size(), match.getMissing().size()));
            status.setBackgroundResource(R.drawable.bg_status_missing);
        }
    }

    private void bindIngredients(List<RecipeIngredient> ingredients, List<String> missing) {
        LinearLayout container = findViewById(R.id.container_ingredients);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (RecipeIngredient ingredient : ingredients) {
            View row = inflater.inflate(R.layout.item_ingredient_row, container, false);
            boolean isMissing = missing.contains(ingredient.getName());

            ImageView icon = row.findViewById(R.id.image_have);
            icon.setImageResource(isMissing ? R.drawable.ic_cancel : R.drawable.ic_check_circle);
            icon.setContentDescription(getString(isMissing ? R.string.cd_missing : R.string.cd_in_pantry));
            ((TextView) row.findViewById(R.id.text_ingredient_name)).setText(ingredient.getName());
            ((TextView) row.findViewById(R.id.text_ingredient_qty)).setText(
                    UnitConverter.describe(ingredient.getQuantity(), ingredient.getUnit(), false));
            container.addView(row);
        }
    }

    private void bindSteps(List<String> steps) {
        LinearLayout container = findViewById(R.id.container_steps);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_step_row, container, false);
            ((TextView) row.findViewById(R.id.text_step_number)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.text_step)).setText(steps.get(i));
            container.addView(row);
        }
    }
}
