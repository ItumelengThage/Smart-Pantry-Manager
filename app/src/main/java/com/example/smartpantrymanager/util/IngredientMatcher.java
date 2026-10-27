package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    private IngredientMatcher() {}

    public static List<Recipe> getSuggestedRecipes(List<Recipe> recipes,
                                                   List<PantryItem> pantry) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getIngredients().isEmpty()) continue;
            boolean all = true;
            for (RecipeIngredient req : recipe.getIngredients()) {
                if (!hasEnoughIngredient(req, pantry)) {
                    all = false;
                    break;
                }
            }
            if (all) result.add(recipe);
        }
        return result;
    }

    public static boolean hasEnoughIngredient(RecipeIngredient required,
                                              List<PantryItem> pantry) {
        String reqName = normalizeIngredientName(required.getIngredientName());
        String reqUnit = normalizeUnit(required.getUnit());
        String reqDim = getDimension(reqUnit);
        if (reqDim == null) return false;

        double requiredAmount = toBaseQuantity(required.getRequiredQuantity(), reqUnit);
        double available = 0.0;

        for (PantryItem item : pantry) {
            if (!reqName.equals(normalizeIngredientName(item.getName()))) continue;
            String pantryUnit = normalizeUnit(item.getUnit());
            if (!reqDim.equals(getDimension(pantryUnit))) continue;
            available += toBaseQuantity(item.getQuantity(), pantryUnit);
        }
        return available + 0.000001 >= requiredAmount;
    }

    private static String normalizeIngredientName(String name) {
        if (name == null) return "";
        String n = Normalizer.normalize(name.trim().toLowerCase(Locale.ROOT),
                        Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "");
        switch (n) {
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "leaves":   return "leaf";
            case "loaves":   return "loaf";
        }
        if (n.endsWith("ies") && n.length() > 3) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 2) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "piece": case "pieces": case "pc": case "pcs":
                return "pieces";
            case "gram": case "grams":
                return "g";
            case "kilogram": case "kilograms": case "kgs":
                return "kg";
            case "millilitre": case "millilitres":
            case "milliliter": case "milliliters":
                return "ml";
            case "litre": case "litres":
            case "liter": case "liters": case "l":
                return "litres";
            case "tablespoon": case "tablespoons":
                return "tbsp";
            case "teaspoon": case "teaspoons":
                return "tsp";
            case "cups":
                return "cup";
            default:
                return u;
        }
    }

    private static String getDimension(String unit) {
        switch (unit) {
            case "pieces": return "count";
            case "g": case "kg": return "mass";
            case "ml": case "litres": case "cup": case "tbsp": case "tsp":
                return "volume";
            default: return null;
        }
    }

    private static double toBaseQuantity(double qty, String unit) {
        switch (unit) {
            case "kg": return qty * 1000.0;
            case "litres": return qty * 1000.0;
            case "cup": return qty * 240.0;
            case "tbsp": return qty * 15.0;
            case "tsp": return qty * 5.0;
            case "g": case "ml": case "pieces": return qty;
            default: return 0;
        }
    }
}