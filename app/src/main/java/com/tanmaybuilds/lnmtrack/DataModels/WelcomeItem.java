package com.tanmaybuilds.lnmtrack.DataModels;

public class WelcomeItem {
    private int image;
    private String title;
    private String description;

    public WelcomeItem(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }
    // Getters
    public int getImage() { return image; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
