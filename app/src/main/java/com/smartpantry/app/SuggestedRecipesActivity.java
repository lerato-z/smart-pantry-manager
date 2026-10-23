package com.smartpantry.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.adapter.RecipeAdapter;
import com.smartpantry.app.data.DatabaseHelper;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.model.MatchResult;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.util.Prefs;

import java.util.List;

/**
 * Suggested Recipes screen. Runs the strict-matching rule against the current
 * pantry every time the screen becomes visible, so changes show immediately.
 */
public class SuggestedRecipesActivity extends BaseActivity implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;
    private TextView strictHeader;
    private View strictEmpty;
    private View almostSection;
    private TextView almostHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setupToolbar(getString(R.string.title_recipes), false);
        setupBottomNavigation(R.id.nav_recipes);

        db = DatabaseHelper.getInstance(this);
        strictHeader = findViewById(R.id.text_strict_header);
        strictEmpty = findViewById(R.id.card_no_matches);
        almostSection = findViewById(R.id.section_almost);
        almostHeader = findViewById(R.id.text_almost_header);

        strictAdapter = new RecipeAdapter(this);
        almostAdapter = new RecipeAdapter(this);
        setupList(R.id.recycler_strict, strictAdapter);
        setupList(R.id.recycler_almost, almostAdapter);
    }

    private void setupList(int recyclerId, RecipeAdapter adapter) {
        RecyclerView recyclerView = findViewById(recyclerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        List<MatchResult> results = RecipeMatcher.evaluate(db.getAllRecipes(), db.getAllPantryItems());
        List<MatchResult> strict = RecipeMatcher.strictMatches(results);
        List<MatchResult> almost = RecipeMatcher.almostThere(results);

        strictAdapter.setResults(strict);
        strictHeader.setText(getString(R.string.header_ready, strict.size()));
        strictEmpty.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);

        boolean showAlmost = Prefs.showAlmostThere(this) && !almost.isEmpty();
        almostSection.setVisibility(showAlmost ? View.VISIBLE : View.GONE);
        almostAdapter.setResults(almost);
        almostHeader.setText(getString(R.string.header_almost, almost.size()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.recipes_subtitle, results.size()));
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
