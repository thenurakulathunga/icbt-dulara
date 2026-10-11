package com.example.printexpress.data.model;

public class Category {
    private final long categoryId;
    private final String name;
    private final String description;
    private final String iconName;

    public Category(long categoryId, String name, String description, String iconName) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
    }

    public long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIconName() { return iconName; }
}
