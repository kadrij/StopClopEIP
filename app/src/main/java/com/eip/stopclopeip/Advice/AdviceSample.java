package com.eip.stopclopeip.Advice;

public class AdviceSample {
    private int id;
    private String content;
    private int likes;
    private String tag;
    private boolean liked;

    public AdviceSample(int id, String content, int likes, String tag, boolean liked) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.tag = tag;
        this.liked = liked;
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

}
