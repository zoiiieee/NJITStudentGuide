package com.team8.cs673.njitstudentguide.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "NJIT_GUIDE";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String TIMESTAMP = "timeStamp";

    private static final String CURRENT_TEMP = "currentTime";

    private static final String LOAD_CONTACTS = "loadContacts";

    private static final String LOAD_LOCATION = "loadLoc";

    private static final String LOAD_EVENT = "loadEvent";

    private static final String LOAD_COURSES = "loadCor";

    private static final String LOAD_SEMESTER = "loadEvent";

    private static final String LOAD_M_TO_EVENT = "meeting_to_event";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public Boolean updateWeather(){
        long current= System.currentTimeMillis();
        long stored =pref.getLong(TIMESTAMP, 0);
        // wait 10 minute
        if (stored+(10000*60)<current||getTemp().equals("")){
            Log.d(TAG,"updateWeather"+" =True");
            editor.putLong(TIMESTAMP, current);
            // commit changes
            editor.commit();
            return true;
        }
        else {
            Log.d(TAG,"updateWeather"+" =False");
            return false;
        }
    }
    public void setTemp(String temp) {

        editor.putString(CURRENT_TEMP, temp);
        // commit changes
        editor.commit();
        Log.d(TAG, "temp modified!");
    }
    public String getTemp(){
        return pref.getString(CURRENT_TEMP, "");
    }

    public  boolean isContLoaded(){
        return pref.getBoolean(LOAD_CONTACTS, false);
    }

    public void contactsLoaded(boolean x){
        editor.putBoolean(LOAD_CONTACTS, x);
        editor.commit();
    }
    public  boolean isLocLoaded(){
        return pref.getBoolean(LOAD_LOCATION, false);
    }
    public void locLoaded(boolean x){
        editor.putBoolean(LOAD_LOCATION, x);
        editor.commit();
    }

    public  boolean isEventLoaded(){
        return pref.getBoolean(LOAD_EVENT, false);
    }
    public void eventLoaded(boolean x){
        editor.putBoolean(LOAD_EVENT, x);
        editor.commit();
    }

    public  boolean isCORSESLoaded(){
        return pref.getBoolean(LOAD_COURSES, false);
    }
    public void coursesLoaded(boolean x){
        editor.putBoolean(LOAD_COURSES, x);
        editor.commit();
    }
    public  boolean isSemesterLoaded(){
        return pref.getBoolean(LOAD_SEMESTER, false);
    }
    public void semesterLoaded(boolean x){
        editor.putBoolean(LOAD_SEMESTER, x);
        editor.commit();
    }

    public boolean isMeetingToEvents() {
        return pref.getBoolean(LOAD_M_TO_EVENT, false);
    }

    public void meetingStoredToEvent(boolean x){
        editor.putBoolean(LOAD_M_TO_EVENT, x);
        editor.commit();}
}