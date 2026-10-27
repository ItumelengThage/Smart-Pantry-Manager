package com.example.smartpantrymanager.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.RecipeDetailActivity;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes = new ArrayList<>();

    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        List<RecipeIngredient> ingredients = recipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            RecipeIngredient ing = ingredients.get(i);

            if (i > 0) sb.append(" • ");
            sb.append(ing.getIngredientName())
                    .append(" (")
                    .append(formatQty(ing.getRequiredQuantity()))
                    .append(" ")
                    .append(ing.getUnit())
                    .append(")");
        }
        holder.ingredients.setText(sb.toString());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            v.getContext().startActivity(intent);
        });
    }

    private String formatQty(double q) {
        if (q == Math.floor(q)) return String.valueOf((int) q);
        return String.valueOf(q);
    }

    @Override
    public int getItemCount() { return recipes.size(); }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name, ingredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtRecipeName);
            ingredients = itemView.findViewById(R.id.txtRecipeIngredients);
        }
    }
}