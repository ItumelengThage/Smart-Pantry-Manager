package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.IngredientMatcher;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        TextView message = findViewById(R.id.txtRecipeMessage);
        RecyclerView recycler = findViewById(R.id.recyclerRecipes);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        RecipeAdapter adapter = new RecipeAdapter();
        recycler.setAdapter(adapter);

        List<Recipe> all = databaseHelper.getAllRecipes();
        for (Recipe r : all) {
            r.setIngredients(databaseHelper.getRecipeIngredients(r.getId()));
        }
        List<PantryItem> pantry = databaseHelper.getAllPantryItems();
        List<Recipe> matches = IngredientMatcher.getSuggestedRecipes(all, pantry);

        adapter.setRecipes(matches);

        if (matches.isEmpty()) {
            message.setText("No recipes match your current pantry. Add ingredients");
        }
        else
        {
            message.setText(matches.size() + " recipe(s) you can make with your pantry:");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.close();
    }
}