package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "pantry_settings";
    private static final String KEY_DEFAULT_UNIT = "default_unit";

    private SharedPreferences preferences;
    private Spinner spinnerDefaultUnit;
    private DatabaseHelper databaseHelper;

    private final String[] units = {"pieces", "g", "kg", "ml",
            "litres", "cups", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        databaseHelper = new DatabaseHelper(this);

        spinnerDefaultUnit = findViewById(R.id.spinnerDefaultUnit);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        Button btnClearPantry = findViewById(R.id.btnClearPantry);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDefaultUnit.setAdapter(adapter);

        String savedUnit = preferences.getString(KEY_DEFAULT_UNIT, "pieces");

        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(savedUnit)) {
                spinnerDefaultUnit.setSelection(i);
                break;
            }
        }

        btnSaveSettings.setOnClickListener(v -> saveSettings());

        btnClearPantry.setOnClickListener(v -> confirmClearPantry());
    }

    private void saveSettings() {
        String selectedUnit = spinnerDefaultUnit.getSelectedItem().toString();

        preferences.edit().putString(KEY_DEFAULT_UNIT, selectedUnit).apply();

        Toast.makeText(this, "Settings saved.", Toast.LENGTH_SHORT).show();
    }

    private void confirmClearPantry() {
        new AlertDialog.Builder(this)
                .setTitle("Clear pantry")
                .setMessage("Are you sure you want to delete all pantry " + "ingredients? This cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    int deleted = databaseHelper.clearPantry();

                    Toast.makeText(this, deleted >= 0 ? "Pantry cleared." : "Could not clear pantry.",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
