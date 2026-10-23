package com.smartpantry.app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.smartpantry.app.data.DatabaseHelper;
import com.smartpantry.app.data.SeedData;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.util.DateUtils;
import com.smartpantry.app.util.Prefs;

/**
 * Settings screen: expiring-soon alerts, the optional "Almost there" list,
 * the unit display preference and pantry data tools.
 */
public class SettingsActivity extends BaseActivity {

    private DatabaseHelper db;
    private Spinner daysSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar(getString(R.string.title_settings), false);
        setupBottomNavigation(R.id.nav_settings);
        db = DatabaseHelper.getInstance(this);

        MaterialSwitch alertsSwitch = findViewById(R.id.switch_expiry_alerts);
        MaterialSwitch almostSwitch = findViewById(R.id.switch_almost_there);
        MaterialSwitch metricSwitch = findViewById(R.id.switch_metric);
        daysSpinner = findViewById(R.id.spinner_expiry_days);

        setupDaysSpinner();

        alertsSwitch.setChecked(Prefs.expiryAlertsEnabled(this));
        daysSpinner.setEnabled(alertsSwitch.isChecked());
        alertsSwitch.setOnCheckedChangeListener((button, checked) -> {
            Prefs.setExpiryAlertsEnabled(this, checked);
            daysSpinner.setEnabled(checked);
        });

        almostSwitch.setChecked(Prefs.showAlmostThere(this));
        almostSwitch.setOnCheckedChangeListener((button, checked) -> Prefs.setShowAlmostThere(this, checked));

        metricSwitch.setChecked(Prefs.useMetricBaseUnits(this));
        metricSwitch.setOnCheckedChangeListener((button, checked) -> Prefs.setUseMetricBaseUnits(this, checked));

        MaterialButton sampleButton = findViewById(R.id.button_load_sample);
        MaterialButton clearButton = findViewById(R.id.button_clear_pantry);
        sampleButton.setOnClickListener(v -> loadSamplePantry());
        clearButton.setOnClickListener(v -> confirmClearPantry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataInfo();
    }

    private void refreshDataInfo() {
        TextView info = findViewById(R.id.text_data_info);
        info.setText(getString(R.string.data_info, db.countRecipes(), db.getAllPantryItems().size()));
    }

    private void setupDaysSpinner() {
        String[] labels = new String[Prefs.EXPIRY_DAY_OPTIONS.length];
        int selected = 0;
        for (int i = 0; i < labels.length; i++) {
            int days = Prefs.EXPIRY_DAY_OPTIONS[i];
            labels[i] = getResources().getQuantityString(R.plurals.days_before, days, days);
            if (days == Prefs.expiryDays(this)) {
                selected = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);
        daysSpinner.setSelection(selected);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.setExpiryDays(SettingsActivity.this, Prefs.EXPIRY_DAY_OPTIONS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadSamplePantry() {
        for (String[] row : SeedData.samplePantry()) {
            String expiry = row[3].isEmpty() ? null : DateUtils.daysFromToday(Integer.parseInt(row[3]));
            db.insertPantryItem(new PantryItem(row[0], Double.parseDouble(row[1]), row[2], expiry));
        }
        Toast.makeText(this, R.string.sample_loaded, Toast.LENGTH_SHORT).show();
        refreshDataInfo();
    }

    private void confirmClearPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    db.clearPantry();
                    Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                    refreshDataInfo();
                })
                .show();
    }
}
