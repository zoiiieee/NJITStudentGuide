package com.team8.cs673.njitstudentguide.Utilities;

import java.util.ArrayList;

public class Courses {
    private String semester;
    private int course_ID;
    private String course_No;
    private String course_Name;
    private ArrayList<Meeting> mlist;



    public Courses(int course_ID,String semester,  String course_No, String course_Name,ArrayList<Meeting> mlist ) {
        this.semester = semester;
        this.course_ID = course_ID;
        this.course_No = course_No;
        this.course_Name = course_Name;
        this.mlist=mlist;

    }
    public Courses(String semester,  String course_No, String course_Name,ArrayList<Meeting> mlist ) {
        this.semester = semester;

        this.course_No = course_No;
        this.course_Name = course_Name;
        this.mlist=mlist;

    }

    public  void addMeeting(Meeting m){
        this.mlist.add(m);
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getCourse_ID() {
        return course_ID;
    }

    public void setCourse_ID(int course_ID) {
        this.course_ID = course_ID;
    }

    public String getCourse_No() {
        return course_No;
    }

    public void setCourse_No(String course_No) {
        this.course_No = course_No;
    }

    public String getCourse_Name() {
        return course_Name;
    }

    public void setCourse_Name(String course_Name) {
        this.course_Name = course_Name;
    }


    public ArrayList<Meeting> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<Meeting> mlist) {
        this.mlist = mlist;
    }
}
