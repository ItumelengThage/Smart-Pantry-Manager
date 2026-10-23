package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.IngredientMatcher;
import com.example.smartpantrymanager.adapter.RecipeAdapter;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtRecipeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        txtRecipeMessage = findViewById(R.id.txtRecipeMessage);

        RecyclerView recyclerRecipes = findViewById(R.id.recyclerRecipes);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<Recipe> recipes = databaseHelper.getAllRecipes();


        for (Recipe recipe : recipes) {
            recipe.setIngredients(databaseHelper.getRecipeIngredients(recipe.getId()));
        }

        List<PantryItem> pantryItems = databaseHelper.getAllPantryItems();

        List<Recipe> suggestions = IngredientMatcher.getSuggestedRecipes(recipes, pantryItems);

        txtRecipeMessage.setText(suggestions.isEmpty() ? "No recipes match your current pantry. " +
                        "Add ingredients or update their quantities."
                        : suggestions.size() +
                        " recipe(s) you can make with your pantry:"
        );
        RecipeAdapter adapter = new RecipeAdapter();
        adapter.setRecipes(suggestions);

        RecyclerView recyclerRecipes =
                findViewById(R.id.recyclerRecipes);

        recyclerRecipes.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }


}
