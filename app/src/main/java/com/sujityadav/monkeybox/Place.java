package com.sujityadav.monkeybox;

/**
 * Created by sujit yadav on 3/14/2017.
 */

public class Place {

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    private String name;
    private String address;
    private String imagelink;

}
