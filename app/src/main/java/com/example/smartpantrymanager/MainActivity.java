package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

import android.content.Intent;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });

        databaseHelper = new DatabaseHelper(this);

        RecyclerView recyclerPantry =
                findViewById(R.id.recyclerPantry);

        txtEmptyPantry =
                findViewById(R.id.txtEmptyPantry);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter();
        recyclerPantry.setAdapter(pantryAdapter);

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

        List<PantryItem> items =
                databaseHelper.getAllPantryItems();

        pantryAdapter.setItems(items);

        if (items.isEmpty()) {
            txtEmptyPantry.setVisibility(View.VISIBLE);
        } else {
            txtEmptyPantry.setVisibility(View.GONE);
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