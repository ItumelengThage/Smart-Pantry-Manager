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

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemActionListener {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        pantryAdapter = new PantryAdapter(this);
        recycler.setAdapter(pantryAdapter);

        Button btnAdd = findViewById(R.id.btnAddIngredient);
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditIngredientActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button btnSuggested = findViewById(R.id.btnSuggestedRecipes);
        btnSuggested.setOnClickListener(v -> {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button btnSettings = findViewById(R.id.btnOpenSettings);
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        txtEmptyPantry.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);

        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());

        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this).setTitle("Delete ingredient")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (d, w) -> {

                    int result = databaseHelper.deletePantryItem(item.getId());

                    if (result > 0) {
                        Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                        loadPantryItems();
                    }
                    else {
                        Toast.makeText(this, "Could not delete", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.close();
    }
}