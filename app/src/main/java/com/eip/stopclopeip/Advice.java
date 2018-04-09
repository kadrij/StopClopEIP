package com.eip.stopclopeip;

public class Advice {
    private int id;
    private String content;
    private int likes;
    private String tag;

    public Advice(int id, String content, int likes, String tag) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
