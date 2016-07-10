package com.team8.cs673.njitstudentguide.Utilities;


import java.util.List;

public class Location {
    private String coordinates;
    private String header;
    private String item_name;

    public Location(String coordinates, String header, String item_name) {
        this.coordinates = coordinates;
        this.header = header;
        this.item_name = item_name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }


}
