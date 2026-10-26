package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.IngredientMatcher;


import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtRecipeMessage;

    private RecyclerView recyclerRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        txtRecipeMessage = findViewById(R.id.txtRecipeMessage);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);

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

        RecipeAdapter adapter = new RecipeAdapter(suggestions);
        recyclerRecipes.setAdapter(adapter);

        if (suggestions.isEmpty()) {
            txtRecipeMessage.setText("Add ingredients to get recipe ingredients.");
        } else {
            txtRecipeMessage.setText(suggestions.size() + " recipe(s) you can make:");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }


}
