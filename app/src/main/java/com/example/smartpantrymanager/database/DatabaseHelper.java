package com.example.smartpantrymanager.database;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "smart_pantry.db";

    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_QUANTITY + " REAL NOT NULL CHECK (" +
                        COL_QUANTITY + " > 0), " +
                        COL_UNIT + " TEXT NOT NULL, " +
                        COL_EXPIRY_DATE + " TEXT" +
                        ")";

        db.execSQL(createPantryTable);

        createRecipeTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }
    public long insertPantryItem(String name, double quantity, String unit, String expiryDate){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());

        if(expiryDate == null || expiryDate.trim().isEmpty()){
            values.putNull(COL_EXPIRY_DATE);
        }
        else{
            values.put(COL_EXPIRY_DATE, expiryDate.trim());
        }

        return db.insert(TABLE_PANTRY, null, values);
    }

    public List<PantryItem> getAllPantryItems() {

        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COL_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_UNIT));

                String expiryDate = cursor.isNull(
                        cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE))
                        ? ""
                        : cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE));

                PantryItem item = new PantryItem(
                        id, name, quantity, unit, expiryDate);

                items.add(item);
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public int updatePantryItem(int id, String name, double quantity, String unit,
                                String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull(COL_EXPIRY_DATE);
        } else {
            values.put(COL_EXPIRY_DATE, expiryDate.trim());
        }

        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deletePantryItem(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_RECIPE_INGREDIENT_ID = "recipe_ingredient_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_REQUIRED_QUANTITY = "required_quantity";
    public static final String COL_RECIPE_UNIT = "unit";

    private void createRecipeTables(SQLiteDatabase db) {

        String createRecipes =
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_RECIPE_NAME + " TEXT NOT NULL, " +
                        COL_INSTRUCTIONS + " TEXT NOT NULL" +
                        ")";

        db.execSQL(createRecipes);

        String createRecipeIngredients =
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS + " (" +
                        COL_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        +
                        COL_RECIPE_ID + " INTEGER NOT NULL, " +
                        COL_INGREDIENT_NAME + " TEXT NOT NULL, " +
                        COL_REQUIRED_QUANTITY + " REAL NOT NULL CHECK (" +
                        COL_REQUIRED_QUANTITY + " > 0), " +
                        COL_RECIPE_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + COL_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE" +
                        ")";

        db.execSQL(createRecipeIngredients);
    }

    private void seedRecipes(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES, null);

        try {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        } finally {
            cursor.close();
        }

        db.beginTransaction();

        try {
            seedRecipe(db, "Tomato Pasta",
                    "1. Boil the pasta until tender.\n" +
                            "2. Chop and cook the tomatoes with onion and oil.\n" +
                            "3. Mix the sauce with the pasta.",
                    new String[][] {
                            {"pasta", "200", "g"},
                            {"tomato", "2", "pieces"},
                            {"onion", "1", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Cheese Omelette",
                    "1. Beat the eggs.\n" +
                            "2. Add grated cheese.\n" +
                            "3. Cook in a lightly oiled pan until set.",
                    new String[][] {
                            {"egg", "2", "pieces"},
                            {"cheese", "30", "g"},
                            {"oil", "1", "tsp"}
                    });

            seedRecipe(db, "Banana Pancakes",
                    "1. Mash the banana.\n" +
                            "2. Mix with egg and flour.\n" +
                            "3. Cook small pancakes in a pan.",
                    new String[][] {
                            {"banana", "1", "pieces"},
                            {"egg", "1", "pieces"},
                            {"flour", "100", "g"}
                    });

            seedRecipe(db, "Garlic Pasta",
                    "1. Boil the pasta.\n" +
                            "2. Gently cook garlic in oil.\n" +
                            "3. Toss the pasta in the garlic oil.",
                    new String[][] {
                            {"pasta", "200", "g"},
                            {"garlic", "2", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Tomato Soup",
                    "1. Chop the tomatoes and onion.\n" +
                            "2. Cook until soft with water.\n" +
                            "3. Blend or mash until smooth.",
                    new String[][] {
                            {"tomato", "4", "pieces"},
                            {"onion", "1", "pieces"},
                            {"water", "300", "ml"}
                    });

            seedRecipe(db, "Egg Fried Rice",
                    "1. Cook the rice.\n" +
                            "2. Scramble the eggs in a pan.\n" +
                            "3. Add rice and peas, then stir-fry.",
                    new String[][] {
                            {"rice", "200", "g"},
                            {"egg", "2", "pieces"},
                            {"peas", "50", "g"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Chicken Rice",
                    "1. Cook the chicken thoroughly.\n" +
                            "2. Boil the rice.\n" +
                            "3. Combine and season using available ingredients.",
                    new String[][] {
                            {"chicken", "200", "g"},
                            {"rice", "150", "g"},
                            {"onion", "1", "pieces"}
                    });

            seedRecipe(db, "Potato Mash",
                    "1. Peel and boil the potatoes until soft.\n" +
                            "2. Mash with milk and butter.",
                    new String[][] {
                            {"potato", "300", "g"},
                            {"milk", "50", "ml"},
                            {"butter", "20", "g"}
                    });

            seedRecipe(db, "Cheese Toast",
                    "1. Place cheese on the bread.\n" +
                            "2. Toast in a pan or toaster oven until melted.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"cheese", "40", "g"}
                    });

            seedRecipe(db, "Peanut Butter Banana Toast",
                    "1. Toast the bread.\n" +
                            "2. Spread peanut butter.\n" +
                            "3. Slice the banana and place it on top.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"peanut butter", "2", "tbsp"},
                            {"banana", "1", "pieces"}
                    });

            seedRecipe(db, "Carrot Soup",
                    "1. Chop the carrots and onion.\n" +
                            "2. Boil in water until tender.\n" +
                            "3. Blend or mash into soup.",
                    new String[][] {
                            {"carrot", "3", "pieces"},
                            {"onion", "1", "pieces"},
                            {"water", "400", "ml"}
                    });

            seedRecipe(db, "Simple Coleslaw",
                    "1. Shred the cabbage and carrot.\n" +
                            "2. Mix with mayonnaise and serve.",
                    new String[][] {
                            {"cabbage", "200", "g"},
                            {"carrot", "1", "pieces"},
                            {"mayonnaise", "2", "tbsp"}
                    });

            seedRecipe(db, "Apple Oatmeal",
                    "1. Cook the oats with milk.\n" +
                            "2. Chop the apple and add it.\n" +
                            "3. Simmer until the oats are soft.",
                    new String[][] {
                            {"oats", "50", "g"},
                            {"milk", "200", "ml"},
                            {"apple", "1", "pieces"}
                    });

            seedRecipe(db, "Bean Salad",
                    "1. Drain and rinse the beans if canned.\n" +
                            "2. Chop the tomato and onion.\n" +
                            "3. Combine and dress with oil.",
                    new String[][] {
                            {"beans", "200", "g"},
                            {"tomato", "1", "pieces"},
                            {"onion", "0.5", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Garlic Potatoes",
                    "1. Cut the potatoes into pieces.\n" +
                            "2. Toss with oil and chopped garlic.\n" +
                            "3. Roast until tender and golden.",
                    new String[][] {
                            {"potato", "300", "g"},
                            {"garlic", "2", "pieces"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Spinach Omelette",
                    "1. Cook the spinach until wilted.\n" +
                            "2. Beat the eggs and pour over it.\n" +
                            "3. Cook until the eggs are set.",
                    new String[][] {
                            {"spinach", "50", "g"},
                            {"egg", "2", "pieces"},
                            {"oil", "1", "tsp"}
                    });

            seedRecipe(db, "Chicken Sandwich",
                    "1. Cook the chicken thoroughly and slice it.\n" +
                            "2. Place on bread with lettuce and mayonnaise.",
                    new String[][] {
                            {"chicken", "100", "g"},
                            {"bread", "2", "pieces"},
                            {"lettuce", "20", "g"},
                            {"mayonnaise", "1", "tbsp"}
                    });

            seedRecipe(db, "Tomato Cheese Sandwich",
                    "1. Slice the tomato.\n" +
                            "2. Place tomato and cheese between bread slices.\n" +
                            "3. Toast if desired.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"tomato", "1", "pieces"},
                            {"cheese", "30", "g"}
                    });

            seedRecipe(db, "Sweet Corn Rice",
                    "1. Cook the rice.\n" +
                            "2. Heat the corn with a little oil.\n" +
                            "3. Mix together and serve.",
                    new String[][] {
                            {"rice", "150", "g"},
                            {"corn", "100", "g"},
                            {"oil", "1", "tbsp"}
                    });

            seedRecipe(db, "Flour Flatbread",
                    "1. Mix flour, water and salt into a dough.\n" +
                            "2. Knead and flatten into thin rounds.\n" +
                            "3. Cook in a hot pan until done on both sides.",
                    new String[][] {
                            {"flour", "200", "g"},
                            {"water", "100", "ml"},
                            {"salt", "2", "g"}
                    });

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    private void seedRecipe(SQLiteDatabase db, String name, String instructions, String[][] ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_INSTRUCTIONS, instructions);

        long recipeId = db.insertOrThrow(TABLE_RECIPES, null, recipeValues);

        for (String[] ingredient : ingredients) {

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(COL_RECIPE_ID, recipeId);
            ingredientValues.put(COL_INGREDIENT_NAME, ingredient[0]);
            ingredientValues.put(COL_REQUIRED_QUANTITY, Double.parseDouble(ingredient[1]));
            ingredientValues.put(COL_RECIPE_UNIT, ingredient[2]);

            db.insertOrThrow(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
        }
    }

    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPES, null, null, null,
                null,
                null,
                COL_RECIPE_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));

                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));

                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS));

                recipes.add(new Recipe(id, name, instructions));
            }
        } finally {
            cursor.close();
        }

        return recipes;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {

        List<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COL_INGREDIENT_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_INGREDIENT_ID));

                int parentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));

                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME));

                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REQUIRED_QUANTITY));

                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_UNIT));

                ingredients.add(new RecipeIngredient(id, parentId, name, quantity, unit));
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    public int clearPantry() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, null, null);
    }



}
