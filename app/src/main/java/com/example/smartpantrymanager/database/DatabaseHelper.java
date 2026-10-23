package com.example.smartpantrymanager.database;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import com.example.smartpantrymanager.model.PantryItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "smart_pantry.db";

    private static final int DATABASE_VERSION = 1;

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
    public void onCreate(SQLiteDatabase db){
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY +
                "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " + COL_QUANTITY +
                " REAL NOT NULL CHECK (" + COL_QUANTITY + " > 0)," +
                COL_UNIT + " TEXT NOT NULL, " + COL_EXPIRY_DATE +
                " TEXT" + ")";

        db.execSQL(createPantryTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        return;
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

    public int updatePantryItem(int id, String name,
                                double quantity, String unit,
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


}
