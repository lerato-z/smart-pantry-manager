package com.smartpantry.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.app.logic.IngredientNormaliser;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Local SQLite database for the pantry and the seeded recipe collection.
 * Three tables: pantry_items, recipes and recipe_ingredients (one-to-many from recipes).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_NORMALISED = "normalised_name";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_UNIT = "unit";
    private static final String COL_EXPIRY = "expiry_date";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PREP_MINUTES = "prep_minutes";
    private static final String COL_SERVINGS = "servings";
    private static final String COL_STEPS = "steps";
    private static final String COL_RECIPE_ID = "recipe_id";

    private static final String STEP_SEPARATOR = "\n";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_NORMALISED + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL CHECK (" + COL_QUANTITY + " > 0), "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL UNIQUE, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_PREP_MINUTES + " INTEGER, "
                + COL_SERVINGS + " INTEGER, "
                + COL_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_ID + " INTEGER NOT NULL REFERENCES " + TABLE_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_NORMALISED + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    /** Runs once, inside onCreate, so the 20 recipes exist from the very first launch. */
    private void seedRecipes(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (Recipe recipe : SeedData.recipes()) {
                ContentValues values = new ContentValues();
                values.put(COL_NAME, recipe.getName());
                values.put(COL_DESCRIPTION, recipe.getDescription());
                values.put(COL_PREP_MINUTES, recipe.getPrepMinutes());
                values.put(COL_SERVINGS, recipe.getServings());
                values.put(COL_STEPS, joinSteps(recipe.getSteps()));
                long recipeId = db.insert(TABLE_RECIPES, null, values);

                for (RecipeIngredient ingredient : recipe.getIngredients()) {
                    ContentValues row = new ContentValues();
                    row.put(COL_RECIPE_ID, recipeId);
                    row.put(COL_NAME, ingredient.getName());
                    row.put(COL_NORMALISED, IngredientNormaliser.normalise(ingredient.getName()));
                    row.put(COL_QUANTITY, ingredient.getQuantity());
                    row.put(COL_UNIT, ingredient.getUnit());
                    db.insert(TABLE_RECIPE_INGREDIENTS, null, row);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // ---------- Pantry CRUD ----------

    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_PANTRY, null, toValues(item));
        item.setId(id);
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor));
            }
        }
        return items;
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readPantryItem(cursor) : null;
        }
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, toValues(item), COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        getWritableDatabase().delete(TABLE_PANTRY, null, null);
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().trim());
        values.put(COL_NORMALISED, IngredientNormaliser.normalise(item.getName()));
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        if (item.hasExpiryDate()) {
            values.put(COL_EXPIRY, item.getExpiryDate());
        } else {
            values.putNull(COL_EXPIRY);
        }
        return values;
    }

    private PantryItem readPantryItem(Cursor cursor) {
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_EXPIRY);
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
    }

    // ---------- Recipes (read-only for the user) ----------

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = new HashMap<>();
        try (Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, null, null, null, null, COL_ID)) {
            while (cursor.moveToNext()) {
                long recipeId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));
                List<RecipeIngredient> list = ingredientsByRecipe.get(recipeId);
                if (list == null) {
                    list = new ArrayList<>();
                    ingredientsByRecipe.put(recipeId, list);
                }
                list.add(readIngredient(cursor));
            }
        }

        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                recipes.add(readRecipe(cursor, ingredientsByRecipe.get(id)));
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, COL_ID)) {
            while (cursor.moveToNext()) {
                ingredients.add(readIngredient(cursor));
            }
        }
        try (Cursor cursor = db.query(TABLE_RECIPES, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readRecipe(cursor, ingredients) : null;
        }
    }

    public int countRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private RecipeIngredient readIngredient(Cursor cursor) {
        return new RecipeIngredient(
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
    }

    private Recipe readRecipe(Cursor cursor, List<RecipeIngredient> ingredients) {
        String steps = cursor.getString(cursor.getColumnIndexOrThrow(COL_STEPS));
        return new Recipe(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREP_MINUTES)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_SERVINGS)),
                ingredients,
                new ArrayList<>(Arrays.asList(steps.split(STEP_SEPARATOR))));
    }

    private static String joinSteps(List<String> steps) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) {
                builder.append(STEP_SEPARATOR);
            }
            builder.append(steps.get(i));
        }
        return builder.toString();
    }
}
