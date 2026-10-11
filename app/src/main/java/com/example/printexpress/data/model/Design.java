package com.example.printexpress.data.model;

/** Artwork uploaded by the customer (the file is kept in the app's private storage). */
public class Design {
    private final long designId;
    private final String title;
    private final String originalName;
    private final String fileName;
    private final String uploadedAt;

    public Design(long designId, String title, String originalName, String fileName, String uploadedAt) {
        this.designId = designId;
        this.title = title;
        this.originalName = originalName;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
    }

    public long getDesignId() { return designId; }
    public String getTitle() { return title; }
    public String getOriginalName() { return originalName; }
    public String getFileName() { return fileName; }
    public String getUploadedAt() { return uploadedAt; }

    public boolean isPdf() {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }
}
