package com.smartpantry.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.model.MatchResult;
import com.smartpantry.app.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays recipe match results. The same adapter serves the strict
 * "Ready to cook" list and the separate "Almost there" list.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<MatchResult> results = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<MatchResult> newResults) {
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        MatchResult result = results.get(position);
        Recipe recipe = result.getRecipe();
        Context context = holder.itemView.getContext();

        holder.name.setText(recipe.getName());
        holder.description.setText(recipe.getDescription());
        holder.meta.setText(context.getString(R.string.recipe_meta,
                recipe.getPrepMinutes(), recipe.getServings(), recipe.getIngredients().size()));

        if (result.isStrictMatch()) {
            holder.missing.setVisibility(View.GONE);
        } else {
            holder.missing.setVisibility(View.VISIBLE);
            holder.missing.setText(context.getString(R.string.missing_list,
                    TextUtils.join(", ", result.getMissing())));
        }
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView meta;
        final TextView missing;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_recipe_name);
            description = itemView.findViewById(R.id.text_recipe_description);
            meta = itemView.findViewById(R.id.text_recipe_meta);
            missing = itemView.findViewById(R.id.text_recipe_missing);
        }
    }
}
