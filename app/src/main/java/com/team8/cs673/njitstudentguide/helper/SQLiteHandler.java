package com.team8.cs673.njitstudentguide.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.team8.cs673.njitstudentguide.Utilities.Contact;
import com.team8.cs673.njitstudentguide.Utilities.Courses;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.Location;
import com.team8.cs673.njitstudentguide.Utilities.Meeting;
import com.team8.cs673.njitstudentguide.Utilities.Semester;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_SEMESTER= "semester";
    private static final String TABLE_USER = "user";
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_COURSES= "course";
    private static final String TABLE_MEETING= "meeting";

    private static final String SEMESTER_ID = "id";
    private static final String S_NAME = "name";
    private static final String S_START = "start";
    private static final String S_END = "end";

    // Contacts Table Columns names
    private static final String CONTACTS_ID = "id";
    private static final String C_NAME = "name";
    private static final String C_NUMBER = "number";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String LOC_ID = "id";
    private static final String COOR = "coor";
    private static final String HEADER= "header";
    private static final String ITEM_NAME = "name";

    private static final String EVENT_ID ="id";
    private static final String EVENT_DATE ="event_date";
    private static final String EVENT_TIME ="event_time";
    private static final String EVENT_TITLE ="event_title";
    private static final String EVENT_DESCRIPTION ="event_desc";

    private static final String COURSE_ID ="id";
    private static final String COURSE_NO ="c_no";
    private static final String COURSE_NAME ="c_name";
    private static final String COURSE_SEMESTER ="c_semester";

    private static final String MEETING_ID ="id";
    private static final String M_COURSE_ID ="m_c_id";
    private static final String MEETING_DAY ="m_day";
    private static final String MEETING_TIME ="m_time";
    private static final String M_BUILDING_ID ="m_building";
    private static final String MEETING_ROOM="m_room";



    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "User Database tables created");

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + C_NAME + " TEXT UNIQUE," + C_NUMBER + " TEXT UNIQUE)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d(TAG, "Contacts Database tables created");

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COOR + " TEXT," + HEADER + " TEXT,"+ ITEM_NAME + " TEXT"+" )";
        db.execSQL(CREATE_LOCATIONS_TABLE);
        Log.d(TAG, "Locations Database tables created");

        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + EVENT_DATE + " TEXT,"
                + EVENT_TIME + " TEXT,"
                +  EVENT_TITLE+ " TEXT,"
                + EVENT_DESCRIPTION + " TEXT,"
                +  "UNIQUE("+EVENT_DATE+", "+ EVENT_TITLE +", "+EVENT_TIME+") ON CONFLICT REPLACE)";
        db.execSQL(CREATE_EVENTS_TABLE);
        Log.d(TAG, "Event Database tables created");

        String CREATE_MEETING_TABLE = "CREATE TABLE " + TABLE_MEETING + "("
                + MEETING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + M_COURSE_ID + " INTEGER,"
                + MEETING_DAY + " TEXT,"
                +  MEETING_TIME + " TEXT,"
                + M_BUILDING_ID + " INTEGER,"
                + MEETING_ROOM + " TEXT"
                + ")";
        db.execSQL(CREATE_MEETING_TABLE);
        Log.d(TAG, "MEETING Database tables created");

        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COURSE_SEMESTER + " TEXT,"
                + COURSE_NO + " TEXT,"
                +  COURSE_NAME+ " TEXT"
                + ")";
        db.execSQL(CREATE_COURSE_TABLE);
        Log.d(TAG, "COURSE Database tables created");

        String CREATE_SEMESTER_TABLE = "CREATE TABLE " + TABLE_SEMESTER + "("
                + SEMESTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + S_NAME + " TEXT unique,"
                + S_START + " TEXT,"
                +  S_END + " TEXT"
                + ")";
        db.execSQL(CREATE_SEMESTER_TABLE);
        Log.d(TAG, "SEMESTER Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEMESTER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */






    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    public void addContact(Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_NAME, c.getName()); // Name
        values.put(C_NUMBER, c.getNumber()); // number
        // Inserting Row
        long id = db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New contact inserted into sqlite: " + id);
    }
    public void addLocation(Location l) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COOR, l.getCoordinates()); // Name
        values.put(HEADER, l.getHeader());
        values.put(ITEM_NAME, l.getItem_name());
        // Inserting Row
        long id = db.insert(TABLE_LOCATIONS, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New Loc inserted into sqlite: " + id);
    }
    public Long addEvent(Event e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_DATE, e.getEventDate()); // Name
        values.put(EVENT_TIME, e.getEventTime()); // Email
        values.put(EVENT_TITLE, e.getEventTitle()); // Email
        values.put(EVENT_DESCRIPTION, e.getEventDesc()); // Created At
        // Inserting Row
        long id = db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New event inserted into sqlite: " + id);
        return id;
    }

    public void addcourse(Courses c){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_SEMESTER, c.getSemester());
        values.put(COURSE_NO, c.getCourse_No());
        values.put(COURSE_NAME, c.getCourse_Name());
        // Inserting Row
        long id = db.insert(TABLE_COURSES, null, values);
        db.close(); // Closing database connection
        addMeeting(id, c.getMlist());
        Log.d(TAG, "New course inserted into sqlite: " + id);

    }

    public void addMeeting(long c_id, ArrayList<Meeting> mlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i=0;i<mlist.size();i++){
        mlist.get(i).setC_id((int)c_id);
        values.put(M_COURSE_ID, c_id);
        values.put(MEETING_DAY, mlist.get(i).getMeeeting_Day());
        values.put(MEETING_TIME,  mlist.get(i).getMeeting_time());
        values.put(M_BUILDING_ID,  mlist.get(i).getMeeting_Building());
        values.put(MEETING_ROOM, mlist.get(i).getMeeting_Room());
        // Inserting Row
        long id = db.insert(TABLE_MEETING, null, values);
        Log.d(TAG, "New meeting inserted into sqlite: " + id);
        }
    }

    public void addSingleMeeting(int c_id,Meeting m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

            values.put(M_COURSE_ID, c_id);
            values.put(MEETING_DAY, m.getMeeeting_Day());
            values.put(MEETING_TIME, m.getMeeting_time());
            values.put(M_BUILDING_ID,  m.getMeeting_Building());
            values.put(MEETING_ROOM, m.getMeeting_Room());
            // Inserting Row
        long id = db.insert(TABLE_MEETING, null, values);
        Log.d(TAG, "New meeting inserted into sqlite: " + id);

    }
    public void addSemester(Semester s){
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "SELECT * FROM "+TABLE_SEMESTER+ " WHERE "+ S_NAME+" = '"+s.getTitle()+ "'";
        Cursor cursor = db.rawQuery(QUERY, null);

        cursor.moveToFirst();
        if(cursor.getCount()>0){
            ContentValues values = new ContentValues();
            values.put(S_START, s.getStart());
            values.put(S_END, s.getEnd());
            boolean id=db.update(TABLE_SEMESTER,values, S_NAME + "= '" + s.getTitle() + "'", null)>0;
            db.close();// Closing database connection
            Log.d(TAG, "update semester : " + id);
        }else {
            ContentValues values = new ContentValues();
            values.put(S_NAME, s.getTitle()); // Name
            values.put(S_START, s.getStart());
            values.put(S_END, s.getEnd());
            // Inserting Row
            long id = db.insert(TABLE_SEMESTER, null, values);
            db.close(); // Closing database connection
            Log.d(TAG, "New semester inserted into sqlite: " + id);
        }


    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public ArrayList<Contact> getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contact> cList = null;
        try{
            cList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_CONTACTS;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Contact c = new Contact(cursor.getString(1),cursor.getString(2));
                    cList.add(c);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return cList;
    }

    public ArrayList<Location> getAllLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Location> lList = null;
        try{
            lList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_LOCATIONS;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Location l = new Location(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                    lList.add(l);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return lList;
    }
    public Semester getSemester(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Semester s=null;
        try{
            String QUERY = "SELECT * FROM "+TABLE_SEMESTER+ " WHERE "+ S_NAME+" = '"+title+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                s=new Semester(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
                Log.e("getSemester", s.toString());
            }
            cursor.close();
            db.close();

        }catch (Exception e){
            Log.e("error",e+"");

        }
        return s;
    }

    public ArrayList<Semester> getAllSemester(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Semester> sList = null;
        try{
            sList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_SEMESTER;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Semester s = new Semester(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
                    sList.add(s);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return sList;
    }
    public ArrayList<Location> getAllLocation(String header) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Location> lList = null;
        try{
            lList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_LOCATIONS+ " WHERE "+ HEADER+" = '"+header+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Location l = new Location(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                    lList.add(l);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return lList;
    }


    public HashMap<Integer,String> getAllLoction(String header) {
            HashMap<Integer, String> buildings = new HashMap<Integer, String>();
            SQLiteDatabase db = this.getReadableDatabase();
            try{
                String QUERY = "SELECT * FROM "+TABLE_LOCATIONS+ " WHERE "+ HEADER+" = '"+header+"'";
                Cursor cursor = db.rawQuery(QUERY, null);
                if(!cursor.isLast())
                {
                    while (cursor.moveToNext()) {
                        buildings.put(cursor.getInt(0), cursor.getString(3));
                    }
                }
                db.close();
            }catch (Exception e){
                Log.e("error",e+"");
            }
            return buildings;
    }

    public ArrayList<String>getAllEvent(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> eList = null;
        try{
            eList = new ArrayList<>();
            String QUERY = "SELECT "+EVENT_DATE+" FROM "+TABLE_EVENTS;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    eList.add(cursor.getString(0));
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return eList;
    }


public ArrayList<Event> getEvent(String title, String desc,String time ){
    ArrayList<Event> eList=null;
    SQLiteDatabase db = this.getReadableDatabase();
    try{
        eList = new ArrayList<>();
    String QUERY =  "SELECT * FROM "+TABLE_EVENTS +" WHERE "+EVENT_TIME+" ='"+time+"' AND "+ EVENT_TITLE+" ='"+title+"' AND "+EVENT_DESCRIPTION+" ='"+desc+"'";
    Cursor cursor = db.rawQuery(QUERY, null);
    if(!cursor.isLast())
    {
        Log.e("!cursor.isLast()","Not Last");
        while (cursor.moveToNext())
        {
           eList.add(new Event(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            Log.e("cursor.moveToNext()", eList.size()+"");
        }

    }
    db.close();
}catch (Exception e1){
        Log.e("error",e1+"");
        return null;
    }
    return  eList;
}
    public ArrayList<Event>getEvents(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Event> eList = null;
        try{
            eList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_EVENTS;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    eList.add(new Event(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return eList;
    }

    public ArrayList<Event> getAllDayEvent(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Event> eList = null;
        try{
            eList = new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_EVENTS+ " WHERE "+ EVENT_DATE+" = '"+date+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Event e = new Event(cursor.getInt(0),cursor.getString(1),cursor.getString(2),
                            cursor.getString(3),cursor.getString(4));
                    eList.add(e);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return eList;
    }
    public ArrayList<Courses> getCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Courses> cList = null;
        ArrayList<Meeting>mList=null;
        try{
            cList = new ArrayList<>();
            mList= new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_COURSES;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    mList= getMeeting(cursor.getInt(0));
                    Courses c = new Courses(cursor.getInt(0),cursor.getString(1),cursor.getString(2),
                            cursor.getString(3),mList);
                    cList.add(c);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return cList;
    }


    public ArrayList<Courses> getAllCourses(String semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Courses> cList = null;
        ArrayList<Meeting>mList=null;
        try{
            cList = new ArrayList<>();
            mList= new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_COURSES+ " WHERE "+ COURSE_SEMESTER+" = '"+semester+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    mList= getMeeting(cursor.getInt(0));
                    Courses c = new Courses(cursor.getInt(0),cursor.getString(1),cursor.getString(2),
                            cursor.getString(3),mList);
                    cList.add(c);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return cList;
    }

    public ArrayList<Meeting> getMeeting(int c_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Meeting>mList=null;
        try{
            mList= new ArrayList<>();
            String QUERY = "SELECT * FROM "+TABLE_MEETING+ " WHERE "+ M_COURSE_ID+" = "+c_id;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Meeting m = new Meeting(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),
                            cursor.getString(3),cursor.getInt(4),cursor.getString(5));
                    mList.add(m);

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
            return null;
        }
        return mList;
    }

    public String getAllLocTags(String location) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tags="", tag;
        try{
            String QUERY = "SELECT * FROM "+TABLE_LOCATIONS+ " WHERE "+ COOR+" = '"+location+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {

                while (cursor.moveToNext())
                {
                    if(!cursor.getString(2).equals("Building")){
                    tag =cursor.getString(3);
                  tags=tags+"\n- "+ tag;}

                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return tags;
    }
    public boolean deleteEvent(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS, EVENT_ID + "=" + id, null) > 0;

    }

    public boolean deleteClassEvents(String title,String desc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS, EVENT_TITLE + " = '" + title+"' AND "+ EVENT_DESCRIPTION + " = '"+desc+"'", null) > 0;

    }

    public boolean deleteMeeting(int c_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEETING, M_COURSE_ID + "=" + c_id, null) > 0;
    }

    public boolean deleteCoures(int c_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.delete(TABLE_COURSES, COURSE_ID + "=" + c_id, null)>0){
        if (deleteMeeting(c_id));{return true;}
        }
    else {return false;}
    }

    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_EVENTS, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_COURSES, null, null);
        db.delete(TABLE_MEETING,null,null);
        db.close();
        Log.d(TAG, "Deleted all classes info from sqlite");
    }

    public void deleteSemester() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_SEMESTER, null, null);
        db.close();
        Log.d(TAG, "Deleted all SEMESTER info from sqlite");
    }
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

}