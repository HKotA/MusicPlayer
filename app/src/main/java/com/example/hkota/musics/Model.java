package com.example.hkota.musics;

public class Model {

    public String name;
    public String song;
    public String url;

    public Model(){

    }

    public Model(String name, String song, String url) {
        this.name = name;
        this.song = song;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
