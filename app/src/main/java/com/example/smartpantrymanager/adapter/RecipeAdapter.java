package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    List<Recipe> recipes = new ArrayList<>();

    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public RecipeAdapter(List<Recipe> recipes){
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);
        holder.txtRecipeName.setText(recipe.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);

            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());

            v.getContext().startActivity(intent);
        });

        StringBuilder ingredients = new StringBuilder("Required ingredients:\n");

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (i > 0) {
                ingredients.append("\n");
            }

            ingredients.append("• ").append(recipe.getIngredients().get(i).getIngredientName())
                    .append(" — ")
                    .append(recipe.getIngredients().get(i)
                            .getRequiredQuantity())
                    .append(" ")
                    .append(recipe.getIngredients().get(i).getUnit());
        }

        holder.txtRecipeIngredients.setText(ingredients.toString());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        TextView txtRecipeIngredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRecipeName = itemView.findViewById(R.id.txtRecipeName);

            txtRecipeIngredients = itemView.findViewById(R.id.txtRecipeIngredients);
        }
    }



}
