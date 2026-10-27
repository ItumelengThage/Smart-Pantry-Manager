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
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private EditText edtName, edtQuantity, edtExpiry;
    private Spinner spinnerUnit;
    private DatabaseHelper databaseHelper;
    private int itemId = -1;

    private final String[] units = {"pieces", "g", "kg", "ml", "litres", "cups", "tbsp", "tsp"};

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
                this, android.R.layout.simple_spinner_item, units);

        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        edtExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            title.setText("Edit Ingredient");
            save.setText("Update Ingredient");
            loadExisting();
        }

        save.setOnClickListener(v -> saveIngredient());
        cancel.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            edtExpiry.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadExisting() {
        PantryItem p = databaseHelper.getPantryItemById(itemId);
        if (p == null) return;

        edtName.setText(p.getName());

        edtQuantity.setText(String.valueOf(p.getQuantity()));

        edtExpiry.setText(p.getExpiryDate());

        ArrayAdapter adapter = (ArrayAdapter) spinnerUnit.getAdapter();

        int pos = adapter.getPosition(p.getUnit());

        if (pos >= 0) spinnerUnit.setSelection(pos);
    }

    private void saveIngredient() {
        edtName.setError(null);
        edtQuantity.setError(null);

        String name = edtName.getText().toString().trim();
        String qtyStr = edtQuantity.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return;
        }
        if (name.length() > 80) {
            edtName.setError("Max 80 characters");
            return;
        }

        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid number");
            edtQuantity.requestFocus();
            return;
        }
        if (!Double.isFinite(qty) || qty <= 0) {
            edtQuantity.setError("Quantity must be greater than zero");
            edtQuantity.requestFocus();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        long result;
        boolean success;
        if (itemId == -1) {
            result = databaseHelper.insertPantryItem(name, qty, unit, expiry);
            success = result != -1;
        } else {
            result = databaseHelper.updatePantryItem(itemId, name, qty, unit, expiry);
            success = result > 0;
        }

        if (success) {
            Toast.makeText(this,
                    itemId == -1 ? "Ingredient added" : "Ingredient updated",
                    Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }
        else {
            Toast.makeText(
                    this, "Could not save ingredient",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.close();
    }
}