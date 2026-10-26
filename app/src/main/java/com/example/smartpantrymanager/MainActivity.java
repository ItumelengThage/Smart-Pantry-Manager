package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenSettings = findViewById(R.id.btnOpenSettings);

        btnOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    SettingsActivity.class);
            startActivity(intent);
        });

        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    SuggestedRecipesActivity.class);
        });



        databaseHelper = new DatabaseHelper(this);


        RecyclerView recyclerPantry = findViewById(R.id.recyclerPantry);

        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter(new PantryAdapter.OnPantryItemActionListener() {

                    @Override
                    public void onEdit(PantryItem item) {

                        editIngredient(item);
                    }

                    @Override
                    public void onDelete(PantryItem item) {

                        confirmDelete(item);
                    }
                }
        );

        recyclerPantry.setAdapter(pantryAdapter);

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this,
                    AddEditIngredientActivity.class);
            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && pantryAdapter != null) {

            loadPantryItems();
        }
    }

    private void loadPantryItems() {

        List<PantryItem> items = databaseHelper.getAllPantryItems();

        pantryAdapter.setItems(items);

        if (items.isEmpty()) {
            txtEmptyPantry.setVisibility(View.VISIBLE);
        }
        else {
            txtEmptyPantry.setVisibility(View.GONE);
        }
    }

    private void editIngredient(PantryItem item) {

        Intent intent = new Intent(MainActivity.this,
                AddEditIngredientActivity.class);

        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());

        startActivity(intent);
    }

    private void confirmDelete(PantryItem item) {

        new AlertDialog.Builder(this).setTitle("Delete ingredient")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    int result = databaseHelper.deletePantryItem(item.getId());

                    if (result > 0) {
                        Toast.makeText(this,
                                "Ingredient deleted", Toast.LENGTH_SHORT).show();

                        loadPantryItems();
                    }
                    else {
                        Toast.makeText(this, "Could not delete ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}