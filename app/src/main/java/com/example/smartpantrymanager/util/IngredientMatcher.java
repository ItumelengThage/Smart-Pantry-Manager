package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    private IngredientMatcher() {
        // Utility class: no object needed.
    }

    /**
     * Returns only recipes for which every required ingredient
     * is present in sufficient quantity.
     */
    public static List<Recipe> getSuggestedRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        List<Recipe> suggestions = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean allIngredientsAvailable = true;

            for (RecipeIngredient required : recipe.getIngredients()) {
                if (!hasEnoughIngredient(required, pantryItems)) {
                    allIngredientsAvailable = false;
                    break;
                }
            }

            if (allIngredientsAvailable
                    && !recipe.getIngredients().isEmpty()) {
                suggestions.add(recipe);
            }
        }

        return suggestions;
    }

    private static boolean hasEnoughIngredient(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        String requiredName =
                normalizeIngredientName(required.getIngredientName());

        String requiredUnit = normalizeUnit(required.getUnit());
        String requiredDimension = getDimension(requiredUnit);

        if (requiredDimension == null) {
            return false;
        }

        double requiredAmount = toBaseQuantity(
                required.getRequiredQuantity(), requiredUnit);

        double availableAmount = 0;

        for (PantryItem item : pantryItems) {
            String pantryName = normalizeIngredientName(item.getName());

            if (!requiredName.equals(pantryName)) {
                continue;
            }

            String pantryUnit = normalizeUnit(item.getUnit());

            // Do not compare incompatible units, such as grams and cups.
            if (!requiredDimension.equals(getDimension(pantryUnit))) {
                continue;
            }

            availableAmount += toBaseQuantity(
                    item.getQuantity(), pantryUnit);
        }

        return availableAmount + 0.000001 >= requiredAmount;
    }

    private static String normalizeIngredientName(String name) {
        if (name == null) {
            return "";
        }

        String normalized = Normalizer.normalize(
                name.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");

        normalized = normalized.replaceAll("\\s+", " ");

        // Common irregular plurals.
        switch (normalized) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "leaves":
                return "leaf";
            case "loaves":
                return "loaf";
            default:
                break;
        }

        // Examples: berries -> berry, onions -> onion.
        if (normalized.endsWith("ies") && normalized.length() > 3) {
            return normalized.substring(0, normalized.length() - 3) + "y";
        }

        // General plural handling: eggs -> egg, beans -> bean.
        if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 2) {
            return normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String normalized = unit.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pieces";

            case "gram":
            case "grams":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kgs":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "litres";

            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "teaspoon":
            case "teaspoons":
                return "tsp";

            case "cups":
                return "cup";

            default:
                return normalized;
        }
    }

    private static String getDimension(String unit) {
        switch (unit) {
            case "pieces":
                return "count";

            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "litres":
            case "cup":
            case "tbsp":
            case "tsp":
                return "volume";

            default:
                return null;
        }
    }

    /**
     * Converts quantities to a common base:
     * count = pieces, mass = grams, volume = millilitres.
     */
    private static double toBaseQuantity(double quantity, String unit) {
        switch (unit) {
            case "kg":
                return quantity * 1000.0;

            case "litres":
                return quantity * 1000.0;

            case "cup":
                return quantity * 240.0;

            case "tbsp":
                return quantity * 15.0;

            case "tsp":
                return quantity * 5.0;

            case "g":
            case "ml":
            case "pieces":
                return quantity;

            default:
                return 0;
        }
    }
}
