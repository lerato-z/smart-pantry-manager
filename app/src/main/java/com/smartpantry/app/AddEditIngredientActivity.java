package com.smartpantry.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.app.data.DatabaseHelper;
import com.smartpantry.app.logic.UnitConverter;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.util.DateUtils;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Add/Edit Ingredient screen. Opened with no extra to add a new item,
 * or with EXTRA_ITEM_ID to edit (and optionally delete) an existing one.
 */
public class AddEditIngredientActivity extends BaseActivity {

    public static final String EXTRA_ITEM_ID = "com.smartpantry.app.EXTRA_ITEM_ID";

    private static final double MAX_QUANTITY = 100000;
    private static final String NAME_PATTERN = "[\\p{L} '\\-]+";

    private DatabaseHelper db;
    private PantryItem editingItem;
    private String selectedExpiry;

    private TextInputLayout nameLayout;
    private TextInputLayout quantityLayout;
    private TextInputLayout expiryLayout;
    private TextInputEditText nameInput;
    private TextInputEditText quantityInput;
    private TextInputEditText expiryInput;
    private Spinner unitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        db = DatabaseHelper.getInstance(this);

        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        expiryLayout = findViewById(R.id.layout_expiry);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        expiryInput = findViewById(R.id.input_expiry);
        unitSpinner = findViewById(R.id.spinner_unit);
        MaterialButton saveButton = findViewById(R.id.button_save);
        MaterialButton deleteButton = findViewById(R.id.button_delete_item);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UnitConverter.UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            editingItem = db.getPantryItem(itemId);
        }

        if (editingItem != null) {
            setupToolbar(getString(R.string.title_edit_ingredient), true);
            fillForm(editingItem);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            setupToolbar(getString(R.string.title_add_ingredient), true);
            unitSpinner.setSelection(Arrays.asList(UnitConverter.UNITS).indexOf("pcs"));
            deleteButton.setVisibility(View.GONE);
        }

        expiryInput.setOnClickListener(v -> showDatePicker());
        expiryLayout.setEndIconOnClickListener(v -> {
            selectedExpiry = null;
            expiryInput.setText(null);
            expiryLayout.setError(null);
        });
        saveButton.setOnClickListener(v -> save());
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void fillForm(PantryItem item) {
        nameInput.setText(item.getName());
        quantityInput.setText(UnitConverter.format(item.getQuantity()));
        int unitIndex = Arrays.asList(UnitConverter.UNITS).indexOf(item.getUnit());
        unitSpinner.setSelection(Math.max(unitIndex, 0));
        selectedExpiry = item.getExpiryDate();
        expiryInput.setText(DateUtils.toDisplay(selectedExpiry));
    }

    private void showDatePicker() {
        Calendar start = DateUtils.parse(selectedExpiry);
        if (start == null) {
            start = Calendar.getInstance();
        }
        new DatePickerDialog(this, (picker, year, month, day) -> {
            selectedExpiry = DateUtils.toStorage(year, month, day);
            expiryInput.setText(DateUtils.toDisplay(selectedExpiry));
            expiryLayout.setError(null);
        }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates every field and shows inline errors; returns null when the form is invalid. */
    private PantryItem readValidatedForm() {
        boolean valid = true;
        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText() == null ? "" : quantityInput.getText().toString().trim();
        String unit = (String) unitSpinner.getSelectedItem();

        nameLayout.setError(null);
        quantityLayout.setError(null);
        expiryLayout.setError(null);

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2) {
            nameLayout.setError(getString(R.string.error_name_short));
            valid = false;
        } else if (!name.matches(NAME_PATTERN)) {
            nameLayout.setError(getString(R.string.error_name_letters));
            valid = false;
        }

        double quantity = 0;
        if (TextUtils.isEmpty(quantityText)) {
            quantityLayout.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText.replace(',', '.'));
                if (quantity <= 0) {
                    quantityLayout.setError(getString(R.string.error_quantity_positive));
                    valid = false;
                } else if (quantity > MAX_QUANTITY) {
                    quantityLayout.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                quantityLayout.setError(getString(R.string.error_quantity_number));
                valid = false;
            }
        }

        Integer daysLeft = DateUtils.daysUntil(selectedExpiry);
        boolean dateChanged = editingItem == null || !TextUtils.equals(selectedExpiry, editingItem.getExpiryDate());
        if (daysLeft != null && daysLeft < 0 && dateChanged) {
            expiryLayout.setError(getString(R.string.error_expiry_past));
            valid = false;
        }

        if (!valid) {
            return null;
        }
        return new PantryItem(name, quantity, unit, selectedExpiry);
    }

    private void save() {
        PantryItem formItem = readValidatedForm();
        if (formItem == null) {
            return;
        }
        if (editingItem == null) {
            db.insertPantryItem(formItem);
            Toast.makeText(this, getString(R.string.saved_item, formItem.getName()), Toast.LENGTH_SHORT).show();
        } else {
            formItem.setId(editingItem.getId());
            db.updatePantryItem(formItem);
            Toast.makeText(this, getString(R.string.updated_item, formItem.getName()), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, editingItem.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deletePantryItem(editingItem.getId());
                    Toast.makeText(this, getString(R.string.deleted_item, editingItem.getName()), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
