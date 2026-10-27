package com.example.smartpantrymanager.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private String imageName;
    private List<RecipeIngredient> ingredients;

    public Recipe(int id, String name, String instructions) {
        this(id, name, instructions, "");
    }

    public Recipe(int id, String name, String instructions, String imageName) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.imageName = imageName == null ? "" : imageName;
        this.ingredients = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getInstructions() {
        return instructions;
    }
    public String getImageName() {
        return imageName;
    }
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}