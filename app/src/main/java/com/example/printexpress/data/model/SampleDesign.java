package com.example.printexpress.data.model;

/** A design idea in the gallery. */
public class SampleDesign {
    private final long sampleId;
    private final long categoryId;
    private final String categoryName;
    private final String iconName;
    private final String title;
    private final String description;
    private final String styleColor;

    public SampleDesign(long sampleId, long categoryId, String categoryName, String iconName,
                        String title, String description, String styleColor) {
        this.sampleId = sampleId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.title = title;
        this.description = description;
        this.styleColor = styleColor;
    }

    public long getSampleId() { return sampleId; }
    public long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getIconName() { return iconName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStyleColor() { return styleColor; }
}
