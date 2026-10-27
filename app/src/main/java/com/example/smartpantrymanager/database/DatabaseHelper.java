package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_IMAGE_NAME = "image_name";

    public static final String COL_RI_ID = "ri_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_REQUIRED_QUANTITY = "required_quantity";
    public static final String COL_RECIPE_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL CHECK (" + COL_QUANTITY + " > 0), " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY_DATE + " TEXT)");

        createRecipeTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_RECIPES +
                        " ADD COLUMN " + COL_IMAGE_NAME + " TEXT NOT NULL DEFAULT ''");
            } catch (Exception ignored) {

            }
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_INSTRUCTIONS + " TEXT NOT NULL, " +
                COL_IMAGE_NAME + " TEXT NOT NULL DEFAULT '')");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS +
                " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COL_REQUIRED_QUANTITY + " REAL NOT NULL CHECK (" +
                COL_REQUIRED_QUANTITY + " > 0), " +
                COL_RECIPE_UNIT + " TEXT NOT NULL)");
    }

    public long insertPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name.trim());
        v.put(COL_QUANTITY, quantity);
        v.put(COL_UNIT, unit.trim());
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            v.putNull(COL_EXPIRY_DATE);
        } else {
            v.put(COL_EXPIRY_DATE, expiryDate.trim());
        }
        return db.insert(TABLE_PANTRY, null, v);
    }

    public int updatePantryItem(int id, String name, double quantity, String unit, String
            expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name.trim());
        v.put(COL_QUANTITY, quantity);
        v.put(COL_UNIT, unit.trim());
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            v.putNull(COL_EXPIRY_DATE);
        } else {
            v.put(COL_EXPIRY_DATE, expiryDate.trim());
        }
        return db.update(TABLE_PANTRY, v, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int clearPantry() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, null, null);
    }

    public PantryItem getPantryItemById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        PantryItem item = null;
        if (c.moveToFirst()) {
            item = mapCursorToPantryItem(c);
        }
        c.close();
        return item;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE_PANTRY, null, null, null,
                null, null, COL_NAME + " ASC");
        while (c.moveToNext()) {
            items.add(mapCursorToPantryItem(c));
        }
        c.close();
        return items;
    }

    private PantryItem mapCursorToPantryItem(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
        String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
        double quantity = c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY));
        String unit = c.getString(c.getColumnIndexOrThrow(COL_UNIT));
        String expiry = c.isNull(c.getColumnIndexOrThrow(COL_EXPIRY_DATE))
                ? "" : c.getString(c.getColumnIndexOrThrow(COL_EXPIRY_DATE));
        return new PantryItem(id, name, quantity, unit, expiry);
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES,
                new String[]{COL_RECIPE_ID, COL_RECIPE_NAME, COL_INSTRUCTIONS,
                        COL_IMAGE_NAME},
                null, null, null, null, COL_RECIPE_NAME + " ASC");
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID));
            String name = c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME));
            String instructions = c.getString(c.getColumnIndexOrThrow(COL_INSTRUCTIONS));
            String image = c.getString(c.getColumnIndexOrThrow(COL_IMAGE_NAME));
            recipes.add(new Recipe(id, name, instructions, image));
        }
        c.close();
        return recipes;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, COL_INGREDIENT_NAME + " ASC");
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(COL_RI_ID));
            int parentId = c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID));
            String name = c.getString(c.getColumnIndexOrThrow(COL_INGREDIENT_NAME));
            double qty =
                    c.getDouble(c.getColumnIndexOrThrow(COL_REQUIRED_QUANTITY));
            String unit = c.getString(c.getColumnIndexOrThrow(COL_RECIPE_UNIT));
            ingredients.add(new RecipeIngredient(id, parentId, name, qty, unit));
        }
        c.close();
        return ingredients;
    }

    private void seedRecipes(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null);
        try {
            if (c.moveToFirst() && c.getInt(0) > 0) return;
        } finally {
            c.close();
        }

        db.beginTransaction();
        try {
            seedRecipe(db, "Tomato Pasta",
                    "1. Boil pasta until tender.\n2. Cook chopped tomatoes with onion and oil.\n" +
                            "3. Mix sauce with pasta and serve.",
            new String[][]{
                    {"pasta", "200", "g"},
                    {"tomato", "2", "pieces"},
                    {"onion", "1", "pieces"},
                    {"oil", "1", "tbsp"}
            });

            seedRecipe(db, "Magwinya (Vetkoek)",
                    "1. Mix flour, sugar, salt and yeast.\n2. " +
                            "Add warm water and knead into a soft dough.\n3. " +
                            "Let it rise for 1 hour.\n4. " +
                            "Shape into balls and deep fry in hot oil until golden brown.",
            new String[][]{
                    {"flour", "500", "g"},
                    {"sugar", "2", "tbsp"},
                    {"salt", "1", "tsp"},
                    {"yeast", "10", "g"},
                    {"water", "300", "ml"},
                    {"oil", "500", "ml"}
            });

            seedRecipe(db, "Seven Colours Dish",
                    "1. Boil the pumpkin and carrots separately until soft, then mash.\n2. " +
                            "Cook the green beans and corn.\n3. " +
                            "Fry the onion and tomatoes for the relish.\n4. " +
                            "Boil the rice and potatoes.\n5. " + "\n6 Season pieces of chicken with spices and sliced peri-peri" +
                            "\n7 Grease the baking dish, place the pieces of chicken, and put it in the oven." +
                            "\n8 Let the chicken bake for 30 minutes at 200 degrees" +
                            "Serve all the colourful components together on one plate.",
            new String[][]{
                    {"pumpkin", "200", "g"},
                    {"carrot", "2", "pieces"},
                    {"cabbage", "100", "g"},
                    {"mayonnaise", "100", "g"},
                    {"onion", "1", "pieces"},
                    {"green pepper", "1", "pieces"},
                    {"rice", "150", "g"},
                    {"potato", "200", "g"},
                    {"oil", "2", "tbsp"},
                    {"spice", "10", "g"},
                    {"peri-peri", "10", "pieces"}

            });

            seedRecipe(db, "Beef Stew",
                    "1. Brown the beef cubes in a pot with oil.\n2. " +
                            "Add chopped onions and cook until soft.\n3. " +
                            "Add tomatoes, carrots, and beef stock.\n4. " +
                            "Cover and simmer for 1.5 hours until the meat is tender.",
            new String[][]{
                    {"beef", "500", "g"},
                    {"onion", "1", "pieces"},
                    {"tomato", "2", "pieces"},
                    {"carrot", "2", "pieces"},
                    {"beef stock", "500", "ml"},
                    {"oil", "2", "tbsp"}
            });

            seedRecipe(db, "Lasagna",
                    "1. Cook the lasagna sheets until al dente.\n2. Fry the mince with onions and " +
                            "tomato sauce.\n3. Make a white sauce by melting butter and mixing in flour and milk.\n4. Layer sheets, " +
                            "meat sauce, and white sauce in a dish.\n5. " +
                            "Top with cheese and bake at 180°C for 30 minutes.",
            new String[][]{
                    {"lasagna sheets", "200", "g"},
                    {"mince", "400", "g"},
                    {"onion", "1", "pieces"},
                    {"tomato sauce", "300", "ml"},
                    {"butter", "50", "g"},
                    {"flour", "50", "g"},
                    {"milk", "500", "ml"},
                    {"cheese", "150", "g"}
            });

            seedRecipe(db, "Cheese Omelette",
                    "1. Beat eggs.\n2. Add grated cheese.\n3. Cook in a lightly oiled pan.",
                    new String[][]{
                            {"egg", "2", "pieces"},
                            {"cheese", "30", "g"},
                            {"oil", "1", "tsp"}
                    });

            seedRecipe(db, "Banana Pancakes",
                    "1. Mash banana.\n2. Mix with egg and flour.\n" +
                            "3. Cook small pancakes in a pan.",
            new String[][]{
                    {"banana", "1", "pieces"},
                    {"egg", "1", "pieces"},
                    {"flour", "100", "g"}
            });

            seedRecipe(db, "Garlic Pasta",
                    "1. Boil pasta.\n2. Gently cook garlic in oil.\n3. Toss pasta in garlic oil.",
                    new String[][]{
                            {"pasta", "200", "g"},
                            {"garlic", "2", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Tomato Soup",
                    "1. Chop tomatoes and onion.\n2. Cook until soft with water.\n" +
                            "3. Blend until smooth.",
            new String[][]{
                    {"tomato", "4", "pieces"},
                    {"onion", "1", "pieces"},
                    {"water", "300", "ml"}
            });

            seedRecipe(db, "Egg Fried Rice",
                    "1. Cook rice.\n2. Scramble eggs.\n3. Stir-fry rice, egg and peas.",
                    new String[][]{
                            {"rice", "200", "g"},
                            {"egg", "2", "pieces"},
                            {"peas", "50", "g"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Chicken Rice",
                    "1. Cook chicken thoroughly.\n2. Boil rice.\n3. Combine and season.",
                    new String[][]{
                            {"chicken", "200", "g"},
                            {"rice", "150", "g"},
                            {"onion", "1", "pieces"}
                    });

            seedRecipe(db, "Mashed Potatoes",
                    "1. Boil peeled potatoes.\n2. Mash with milk and butter.",
                    new String[][]{
                            {"potato", "300", "g"},
                            {"milk", "50", "ml"},
                            {"butter", "20", "g"}
                    });

            seedRecipe(db, "Cheese Toast",
                    "1. Place cheese on bread.\n2. Toast until melted.",
                    new String[][]{
                            {"bread", "2", "pieces"},
                            {"cheese", "40", "g"}
                    });

            seedRecipe(db, "Peanut Butter Banana Toast",
                    "1. Toast bread.\n2. Spread peanut butter.\n3. Top with sliced banana.",
                    new String[][]{
                            {"bread", "2", "pieces"},
                            {"peanut butter", "2", "tbsp"},
                            {"banana", "1", "pieces"}
                    });

            seedRecipe(db, "Carrot Soup",
                    "1. Chop carrots and onion.\n2. Boil in water.\n3. Blend into soup.",
                    new String[][]{
                            {"carrot", "3", "pieces"},
                            {"onion", "1", "pieces"},
                            {"water", "400", "ml"}
                    });

            seedRecipe(db, "Simple Coleslaw",
                    "1. Shred cabbage and carrot.\n2. Mix with mayonnaise.",
                    new String[][]{
                            {"cabbage", "200", "g"},
                            {"carrot", "1", "pieces"},
                            {"mayonnaise", "2", "tbsp"}
                    });

            seedRecipe(db, "Apple Oatmeal",
                    "1. Cook oats in milk.\n2. Add chopped apple.\n3. Simmer until soft.",
                    new String[][]{
                            {"oats", "50", "g"},
                            {"milk", "200", "ml"},
                            {"apple", "1", "pieces"}
                    });

            seedRecipe(db, "Bean Salad",
                    "1. Rinse beans.\n2. Chop tomato and onion.\n3. Dress with oil.",
                    new String[][]{
                            {"beans", "200", "g"},
                            {"tomato", "1", "pieces"},
                            {"onion", "0.5", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Garlic Potatoes",
                    "1. Cut potatoes.\n2. Toss with oil and garlic.\n3. Roast until golden.",
                    new String[][]{
                            {"potato", "300", "g"},
                            {"garlic", "2", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Spinach Omelette",
                    "1. Cook spinach.\n2. Pour beaten eggs over it.\n3. Cook until set.",
                    new String[][]{
                            {"spinach", "50", "g"},
                            {"egg", "2", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Chicken Sandwich",
                    "1. Cook and slice chicken.\n2. Layer on bread with lettuce and mayo.",
                    new String[][]{
                            {"chicken", "100", "g"},
                            {"bread", "2", "pieces"},
                            {"lettuce", "20", "g"},
                            {"mayonnaise", "1", "tbsp"}
                    });

            seedRecipe(db, "Tomato Cheese Sandwich",
                    "1. Slice tomato.\n2. Layer tomato and cheese on bread.\n3. Toast if desired.",
                    new String[][]{
                            {"bread", "2", "pieces"},
                            {"tomato", "1", "pieces"},
                            {"cheese", "30", "g"}
                    });

            seedRecipe(db, "Sweet Corn Rice",
                    "1. Cook rice.\n2. Heat corn with a little oil.\n3. Mix and serve.",
                    new String[][]{
                            {"rice", "150", "g"},
                            {"corn", "100", "g"},
                            {"oil", "1", "tbsp"}
                    });

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    private void seedRecipe(SQLiteDatabase db, String name, String instructions, String[][]
            ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(COL_RECIPE_NAME, name);
        rv.put(COL_INSTRUCTIONS, instructions);
        long recipeId = db.insertOrThrow(TABLE_RECIPES, null, rv);

        for (String[] row : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put(COL_RECIPE_ID, recipeId);
            iv.put(COL_INGREDIENT_NAME, row[0]);
            iv.put(COL_REQUIRED_QUANTITY, Double.parseDouble(row[1]));
            iv.put(COL_RECIPE_UNIT, row[2]);
            db.insertOrThrow(TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }
}