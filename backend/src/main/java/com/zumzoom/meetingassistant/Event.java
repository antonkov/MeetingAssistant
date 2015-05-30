package com.zumzoom.meetingassistant;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Event {

    @Id
    private String id;

    private String title;
    private String date;
    private byte[] audio;
    private String text;
    private List<Integer> offsets;
    private List<String> users;
    private Boolean hasAudio = false;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(Boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public List<Integer> gglolOffsets() {
        return offsets;
    }

    public void sslolOffsets(List<Integer> offsets) {
        this.offsets = offsets;
    }
}

