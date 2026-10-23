package com.smartpantry.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Shared screen setup: toolbar, edge-to-edge insets and the bottom navigation
 * bar that links the three top-level screens (Pantry, Recipes, Settings).
 */
public abstract class BaseActivity extends AppCompatActivity {

    private int navItemId = View.NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
    }

    protected void setupToolbar(CharSequence title, boolean showUpButton) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(showUpButton);
        }
        applySystemBarInsets();
    }

    protected void setupBottomNavigation(int currentItemId) {
        navItemId = currentItemId;
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(currentItemId);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == navItemId) {
                return true;
            }
            if (id == R.id.nav_pantry) {
                openTopLevel(MainActivity.class);
            } else if (id == R.id.nav_recipes) {
                openTopLevel(SuggestedRecipesActivity.class);
            } else if (id == R.id.nav_settings) {
                openTopLevel(SettingsActivity.class);
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navItemId != View.NO_ID) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            MenuItem item = bottomNav.getMenu().findItem(navItemId);
            if (item != null) {
                item.setChecked(true);
            }
        }
    }

    private void openTopLevel(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /** Keeps the toolbar below the status bar and content above the navigation bar and keyboard. */
    private void applySystemBarInsets() {
        View root = findViewById(R.id.root);
        View toolbar = findViewById(R.id.toolbar);
        View bottomNav = findViewById(R.id.bottom_nav);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            int bottom = Math.max(bars.bottom, ime.bottom);

            view.setPadding(bars.left, 0, bars.right, bottomNav == null ? bottom : 0);
            toolbar.setPadding(toolbar.getPaddingLeft(), bars.top, toolbar.getPaddingRight(), 0);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, bars.bottom);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
