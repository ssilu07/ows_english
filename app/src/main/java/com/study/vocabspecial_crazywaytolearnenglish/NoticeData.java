package com.study.vocabspecial_crazywaytolearnenglish;

public class NoticeData {
    String title,image, key,description,key_value;

    public NoticeData() {
    }

    public NoticeData(String title, String image, String key, String description, String key_value) {
        this.title = title;
        this.image = image;
        this.key = key;
        this.description = description;
        this.key_value = key_value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey_value() {
        return key_value;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }
}
