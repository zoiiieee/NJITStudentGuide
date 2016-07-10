package com.team8.cs673.njitstudentguide.Utilities;


public class Meeting {
    private int c_id;
    private int meeting_ID;
    private String meeting_Day;
    private String meeting_time;
    private int meeting_Building;
    private String meeting_Room;

    public Meeting(int meeting_ID,int c_id,  String meeting_Day, String meeting_time, int meeting_Building, String meeting_room ) {
        this.c_id = c_id;
        this.meeting_ID = meeting_ID;
        this.meeting_Day = meeting_Day;
        this.meeting_time = meeting_time;
        this.meeting_Building = meeting_Building;
        this.meeting_Room=meeting_room;
    }

    public Meeting(int c_id, String meeting_Day, String meeting_time, int meeting_Building,  String meeting_room) {
        this.c_id = c_id;
        this.meeting_Day = meeting_Day;
        this.meeting_time = meeting_time;
        this.meeting_Building = meeting_Building;
        this.meeting_Room=meeting_room;
    }

    public int getMeeting_ID() {
        return meeting_ID;
    }

    public void setMeeting_ID(int meeting_ID) {
        this.meeting_ID = meeting_ID;
    }

    public String getMeeeting_Day() {
        return meeting_Day;
    }

    public void setMeeeting_Day(String meeeting_Day) {
        this.meeting_Day = meeeting_Day;
    }

    public String getMeeting_time() {
        return meeting_time;
    }

    public void setMeeting_time(String meeting_time) {
        this.meeting_time = meeting_time;
    }

    public int getMeeting_Building() {
        return meeting_Building;
    }

    public void setMeeting_Building(int meeting_Building) {
        this.meeting_Building = meeting_Building;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public String getMeeting_Room() {
        return meeting_Room;
    }

    public void setMeeting_Room(String meeting_Room) {
        this.meeting_Room = meeting_Room;
    }
}