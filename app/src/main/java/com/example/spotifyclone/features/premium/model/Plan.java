package com.example.spotifyclone.features.premium.model;

public class Plan {
    private String title;
    private String type;
    private String description;
    private String term;
    private Integer currentPrice;
    private Integer newCharge;
    private Integer duration; // in months

    public Plan(String title, String type, String description, String term, Integer currentPrice, Integer newCharge, Integer duration) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.term = term;
        this.currentPrice = currentPrice;
        this.newCharge = newCharge;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getNewCharge() {
        return newCharge;
    }

    public void setNewCharge(Integer newCharge) {
        this.newCharge = newCharge;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
