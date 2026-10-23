package com.example.smartpantrymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private EditText edtName, edtQuantity, edtExpiry;
    private Spinner spinnerUnit;
    private DatabaseHelper databaseHelper;
    private int itemId = -1;

    private final String[] units = {
            "pieces", "g", "kg", "ml", "litres", "cups", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtExpiry = findViewById(R.id.edtExpiryDate);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        TextView title = findViewById(R.id.txtFormTitle);
        Button save = findViewById(R.id.btnSaveIngredient);
        Button cancel = findViewById(R.id.btnCancelIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        edtExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        if (itemId != -1) {
            title.setText("Edit Ingredient");
            save.setText("Update Ingredient");
            loadExistingIngredient();
        }

        save.setOnClickListener(v -> saveIngredient());

        cancel.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format(
                            Locale.US, "%04d-%02d-%02d",
                            year, month + 1, day);
                    edtExpiry.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void loadExistingIngredient() {
        List<PantryItem> items = databaseHelper.getAllPantryItems();

        for (PantryItem item : items) {
            if (item.getId() == itemId) {
                edtName.setText(item.getName());
                edtQuantity.setText(
                        String.valueOf(item.getQuantity()));
                edtExpiry.setText(item.getExpiryDate());

                for (int i = 0; i < units.length; i++) {
                    if (units[i].equalsIgnoreCase(item.getUnit())) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }
                return;
            }
        }

        Toast.makeText(this,
                "Ingredient not found",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveIngredient() {
        String name = edtName.getText().toString().trim();
        String quantityText =
                edtQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = edtExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter an ingredient name");
            edtName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter a quantity");
            edtQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid number");
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            edtQuantity.setError("Quantity must be greater than zero");
            return;
        }

        if (name.length() > 80) {
            edtName.setError("Name must not exceed 80 characters");
            return;
        }

        long result;

        if (itemId == -1) {
            result = databaseHelper.insertPantryItem(
                    name, quantity, unit, expiry);
        } else {
            result = databaseHelper.updatePantryItem(
                    itemId, name, quantity, unit, expiry);
        }

        boolean success = itemId == -1
                ? result != -1
                : result > 0;

        if (success) {
            Toast.makeText(this,
                    itemId == -1
                            ? "Ingredient added successfully"
                            : "Ingredient updated successfully",
                    Toast.LENGTH_SHORT).show();

            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this,
                    "Could not save ingredient",
                    Toast.LENGTH_LONG).show();
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