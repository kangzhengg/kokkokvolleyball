package com.example.testing;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String status;
    private int views;
    private int interested;
    private String uploadedBy;
    private String description;
    private String genre;
    private String condition;

    public Book() { } // required for Firebase

    public Book(String title, String author, String status, int views, int interested) {
        this(title, author, status, views, interested, "");
    }

    public Book(String title, String author, String status, int views, int interested, String uploadedBy) {
        this(title, author, status, views, interested, uploadedBy, "", "", "");
    }

    public Book(String title, String author, String status, int views, int interested, String uploadedBy, String description, String genre, String condition) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.views = views;
        this.interested = interested;
        this.uploadedBy = uploadedBy;
        this.description = description;
        this.genre = genre;
        this.condition = condition;
    }

    // getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getInterested() { return interested; }
    public void setInterested(int interested) { this.interested = interested; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}