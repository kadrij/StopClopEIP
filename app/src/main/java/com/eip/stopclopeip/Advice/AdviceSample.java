package com.eip.stopclopeip.Advice;

public class AdviceSample {
    private int id;
    private String title;
    private String comment;
    private String date;
    private String categorie;

    public AdviceSample(int id, String title, String comment, String date, String categorie) {
        this.id = id;
        this.title = title;
        this.comment = comment;
        this.date = date;
        this.categorie = categorie;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
