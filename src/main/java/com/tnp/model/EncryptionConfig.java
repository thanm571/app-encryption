package com.tnp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EncryptionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipientKeyId;
    private String sourceFilePath;
    private String destinationFilePath;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientKeyId() {
        return recipientKeyId;
    }

    public void setRecipientKeyId(String recipientKeyId) {
        this.recipientKeyId = recipientKeyId;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }

    public void setDestinationFilePath(String destinationFilePath) {
        this.destinationFilePath = destinationFilePath;
    }
}