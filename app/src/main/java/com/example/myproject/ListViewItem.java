package com.example.myproject;

import io.realm.RealmObject;

//Realm에서 사용할 수 있도로 RealmObject에 상속시킵니다.
public class ListViewItem extends RealmObject {

    private String title;
    private String content;
    private String picture;
    private String latitude;
    private String longitude;

    public void setTitle(String string) {
        title = string;
    }

    public void setContent(String string) {
        content = string;
    }

    public void setPicture(String string) { picture = string; }

    public void setLatitude(Double dou) {
        latitude = String.valueOf(dou);
    }

    public void setLongitude(Double dou) {
        longitude = String.valueOf(dou);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String  getPicture() {
        return picture;
    }

    public Double getLatitude() {
        return Double.valueOf(latitude);
    }

    public Double getLongitude() {
        return Double.valueOf(longitude);
    }
}