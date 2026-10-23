package com.smartpantry.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartpantry.app.adapter.PantryAdapter;
import com.smartpantry.app.data.DatabaseHelper;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.util.DateUtils;
import com.smartpantry.app.util.Prefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantry List screen (launcher). Shows every pantry item from SQLite in a RecyclerView.
 */
public class MainActivity extends BaseActivity implements PantryAdapter.OnPantryItemListener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private View emptyState;
    private TextView expiryBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar(getString(R.string.title_pantry), false);
        setupBottomNavigation(R.id.nav_pantry);

        db = DatabaseHelper.getInstance(this);
        emptyState = findViewById(R.id.empty_pantry);
        expiryBanner = findViewById(R.id.text_expiry_banner);

        RecyclerView recyclerView = findViewById(R.id.recycler_pantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        List<PantryItem> items = db.getAllPantryItems();
        boolean alerts = Prefs.expiryAlertsEnabled(this);
        int alertDays = Prefs.expiryDays(this);

        adapter.setDisplayOptions(Prefs.useMetricBaseUnits(this), alerts, alertDays);
        adapter.setItems(items);
        emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getResources()
                    .getQuantityString(R.plurals.pantry_count, items.size(), items.size()));
        }
        showExpiryBanner(items, alerts, alertDays);
    }

    private void showExpiryBanner(List<PantryItem> items, boolean alerts, int alertDays) {
        List<String> soon = new ArrayList<>();
        for (PantryItem item : items) {
            Integer days = DateUtils.daysUntil(item.getExpiryDate());
            if (days != null && days <= alertDays) {
                soon.add(item.getName());
            }
        }
        if (!alerts || soon.isEmpty()) {
            expiryBanner.setVisibility(View.GONE);
            return;
        }
        expiryBanner.setVisibility(View.VISIBLE);
        expiryBanner.setText(getString(R.string.expiry_banner, alertDays, TextUtils.join(", ", soon)));
    }

    @Override
    public void onPantryItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onPantryItemDelete(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    Toast.makeText(this, getString(R.string.deleted_item, item.getName()), Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .show();
    }
}
