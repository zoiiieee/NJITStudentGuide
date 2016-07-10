package com.team8.cs673.njitstudentguide.Utilities;

/**
 * Created by Aledo-PC on 11/9/2015.
 */
public class Semester {
    private int id;
    private String title;
    private String start;
    private String end;

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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Semester(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public Semester(int id, String title, String start, String end) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
    }
}
