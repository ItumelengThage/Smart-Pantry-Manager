package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe could not be found.", Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        displayRecipe(recipeId);
    }

    private void displayRecipe(int recipeId) {
        TextView txtRecipeName = findViewById(R.id.txtDetailRecipeName);

        TextView txtRecipeIngredients = findViewById(R.id.txtDetailIngredients);

        TextView txtRecipeInstructions = findViewById(R.id.txtDetailInstructions);

        Recipe selectedRecipe = null;

        for (Recipe recipe : databaseHelper.getAllRecipes()) {
            if (recipe.getId() == recipeId) {
                selectedRecipe = recipe;
                break;
            }
        }

        if (selectedRecipe == null) {
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        List<RecipeIngredient> ingredients = databaseHelper.getRecipeIngredients(recipeId);

        txtRecipeName.setText(selectedRecipe.getName());

        StringBuilder ingredientText = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            ingredientText.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" — ")
                    .append(ingredient.getRequiredQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        txtRecipeIngredients.setText(ingredientText.toString());

        txtRecipeInstructions.setText(selectedRecipe.getInstructions());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
