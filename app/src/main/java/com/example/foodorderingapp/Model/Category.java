package com.example.foodorderingapp.Model;

public class Category {

    private int id;
    private String categoryName;
    private String imagePath;

    public Category() {

    }
    public Category(int id, String categoryName, String imagePath) {
        this.id = id;
        this.categoryName = categoryName;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
