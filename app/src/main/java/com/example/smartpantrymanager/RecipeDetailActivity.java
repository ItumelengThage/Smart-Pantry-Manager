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

        TextView name = findViewById(R.id.txtDetailRecipeName);
        TextView ingredients = findViewById(R.id.txtDetailIngredients);
        TextView instructions = findViewById(R.id.txtDetailInstructions);

        Recipe selected = null;
        for (Recipe r : databaseHelper.getAllRecipes()) {
            if (r.getId() == recipeId) { selected = r; break; }
        }

        if (selected == null) {
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<RecipeIngredient> list = databaseHelper.getRecipeIngredients(recipeId);

        name.setText(selected.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : list) {
            sb.append("• ")
                    .append(ing.getIngredientName())
                    .append(" — ")
                    .append(formatQty(ing.getRequiredQuantity()))
                    .append(" ")
                    .append(ing.getUnit())
                    .append("\n");
        }
        ingredients.setText(sb.toString().trim());
        instructions.setText(selected.getInstructions());
    }

    private String formatQty(double q) {
        if (q == Math.floor(q)) return String.valueOf((int) q);

        return String.valueOf(q);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.close();
    }
}