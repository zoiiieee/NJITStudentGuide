package com.team8.cs673.njitstudentguide.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.team8.cs673.njitstudentguide.EventNotification.Alarm;
import com.team8.cs673.njitstudentguide.Utilities.Contact;
import com.team8.cs673.njitstudentguide.Utilities.Courses;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.Location;
import com.team8.cs673.njitstudentguide.Utilities.Meeting;
import com.team8.cs673.njitstudentguide.Utilities.Semester;
import com.team8.cs673.njitstudentguide.app.AppConfig;
import com.team8.cs673.njitstudentguide.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpCalls {
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Context c;
    private String email;
Alarm alarm;


    public HttpCalls(Context c, String email){
        this.c=c;
        db=new SQLiteHandler(c);
        pDialog = new ProgressDialog(c);
        session=new SessionManager(c);
        alarm=new Alarm(c);
       this.email=email;

    }


    public void downloadLocations(){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_LOCATION, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        boolean error=response.getBoolean("error");
                        if (!error){
                            int contactSize=response.getInt("size");
                            for (int i=0; i<=contactSize;i++)   {
                                JSONObject locItem=response.getJSONObject(i+"");
                                String coord=locItem.getString("lat")+","+locItem.getString("lon");
                                Location l=new Location(coord,locItem.getString("header"), locItem.getString("item_name"));
                                db.addLocation(l);
                            }
                            session.locLoaded(true);


                        }}
                    else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "location Error: " + error.getMessage());

            }
        }
        );
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    public void downloadContacts(){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_CONTACT, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        boolean error=response.getBoolean("error");
                        if (!error){
                            int contactSize=response.getInt("size");
                            for (int i=0; i<=contactSize;i++)   {
                                JSONObject contactItem=response.getJSONObject(i+"");
                                Contact c=new Contact(contactItem.getString("name"),contactItem.getString("no"));
                                db.addContact(c);
                            }
                            session.contactsLoaded(true);



                        }}
                    else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Login Error: " + error.getMessage());

            }
        }
        );
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    public void downloadEvent() {

        String tag_string_req = "req_getAllEvent";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Event: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        int eventSize = jObj.getInt("size");
                        for (int i = 0; i <= eventSize; i++) {
                            JSONObject eventItem = jObj.getJSONObject(i + "");
                            Event e = new Event(eventItem.getString("date"), eventItem.getString("time"), eventItem.getString("title"), eventItem.getString("des"));

                            db.addEvent(e);

                        }
                        session.eventLoaded(true);
                    } else {

                        String errorMsg = jObj.getString("error_msg");

                     Log.e(errorMsg, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("Json error: ", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error: " + error.getMessage());



            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("date", "");
                params.put("time", "");
                params.put("title", "");
                params.put("des", "");
                params.put("action", "3");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void downloadClasses() {
        final String tag_string_req = "req_getAllEvent";
Log.d("Email:", email);



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLASSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                 Log.d("AllCourses", "Course: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        int classesSize = jObj.getInt("size");
                        String temp_cid="temp";
                        ArrayList<Courses>cList=new ArrayList<>();
                        Courses c=null;
                        Meeting m=null;
                        ArrayList<Meeting>mList=new ArrayList<>();
                        for (int i = 0; i < classesSize; i++) {
                            JSONObject eventItem = jObj.getJSONObject(i + "");

                           String semester= eventItem.getString("semester");
                           String c_no=eventItem.getString("c_no");
                           String c_name=eventItem.getString("c_name");
                           String meeting_cid=eventItem.getString("meeting_cid");
                           String meeting_day=eventItem.getString("meeting_day");
                           String meeting_time=eventItem.getString("meeting_time");
                           String meeting_building=eventItem.getString("meeting_building");
                           String meeting_room=eventItem.getString("meeting_room");

                            m=new Meeting(0,meeting_day,meeting_time,Integer.parseInt(meeting_building),meeting_room);
                           // mList.add(m);
                          //  c=new Courses(semester,c_no,c_name, mList);

                             if(temp_cid.equals(meeting_cid)){
                                c.addMeeting(m);

                            }
                            else{
                                 if(c!=null)
                                 { db.addcourse(c);
                                     cList.add(c);
                                     mList.clear();
                                 }
                                 mList.add(m);
                                 c=new Courses(semester,c_no,c_name, mList);

                            }
                            temp_cid=meeting_cid;
                            if(i==classesSize-1){
                                db.addcourse(c);
                                cList.add(c);
                            }
                        }
                        session.coursesLoaded(true);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        session.coursesLoaded(true);


                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(c, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("semester","XXX");
                params.put("c_no", "XXX");
                params.put("c_name", "XXX");
                params.put("meeting_cid", "XXX");
                params.put("meeting_day", "XXX");
                params.put("meeting_time","XXX");
                params.put("meeting_building", "XXX");
                params.put("meeting_room", "XXX");
                params.put("action", "3");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void uploadSingleCourse(final Courses co) {

        String tag_string_req = "req_getAdd";
        Log.d("AddClass Para", co.getCourse_Name()+", "+co.getCourse_No()+", "+email);
        pDialog.setMessage("Adding ...");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLASSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d("error", "Adding Response: " + response.toString());
                String cid ;


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json

                    if (!error){
                         cid = jObj.getString("add");
                            Log.d("Class", cid+"  ");
                            db.addcourse(co);
                            for(Meeting m: co.getMlist()){
                            uploadSingleMeeting(m, cid);

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }



                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorAdd", "adding Error: " + error.getMessage());
                Toast.makeText(c,
                        "Adding Error", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("semester", co.getSemester());
                params.put("c_no", co.getCourse_No());
                params.put("c_name", co.getCourse_Name());
                params.put("meeting_cid", "XXX");
                params.put("meeting_day", "XXX");
                params.put("meeting_time","XXX");
                params.put("meeting_building", "XXX");
                params.put("meeting_room", "XXX");
                params.put("action", "1");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void uploadSingleMeeting(final Meeting m, final String cid) {
        String tag_string_req = "req_getAdd";
        Log.d("ADDMEETING para", m.getMeeeting_Day() + ", " + m.getMeeting_Room() + ", " + m.getMeeting_time() + ", " + m.getMeeting_Building() + ", " + cid + ", " + email);
        pDialog.setMessage("Adding ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLASSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json

                    if (!error) {
                        boolean added = jObj.getBoolean("add");
                        if(added){
                            int id=Integer.parseInt(cid);
                            db.addSingleMeeting(id, m);

                            Toast.makeText(c, "Event has been added", Toast.LENGTH_SHORT).show();}
                        else{ Toast.makeText(c, "Error in added event", Toast.LENGTH_SHORT).show();}

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorAdd", "adding Error: " + error.getMessage());
                Toast.makeText(c,
                        "Adding Error", Toast.LENGTH_LONG).show();
               // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("semester", "XXX");
                params.put("c_no", "XXX");
                params.put("c_name","XXX");
                params.put("meeting_cid", cid);
                params.put("meeting_day", m.getMeeeting_Day());
                params.put("meeting_time",m.getMeeting_time());
                params.put("meeting_building", m.getMeeting_Building()+"");
                params.put("meeting_room", m.getMeeting_Room());
                params.put("action", "0");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void uploadSingleSemester(final Semester s) {
        String tag_string_req = "req_getAdd";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEMESTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json

                    if (!error) {
                        boolean added = jObj.getBoolean("add");
                        if(added){
                            Log.d("SemesterAdd", added+"");
                            db.addSemester(s);

                            Toast.makeText(c, "Semester Dates has been modified", Toast.LENGTH_SHORT).show();}
                        else{ Toast.makeText(c, "Error in added Semester Dates", Toast.LENGTH_SHORT).show();
                            Log.d("SemesterAdd", added + "");
                        }

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorAdd", "adding Error: " + error.getMessage());
                Toast.makeText(c,
                        "Adding Error", Toast.LENGTH_LONG).show();
                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("title", s.getTitle());
                params.put("start", s.getStart());
                params.put("ended",s.getEnd());
                params.put("action", "0");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void deleteSingleClass(final Courses co) {
        Log.d("Email:",email);
        String tag_string_req = "req_getAdd";

        pDialog.setMessage("Deleting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLASSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("error", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        boolean deleted = jObj.getBoolean("delete");
                        Log.d("DeleteClass", deleted + "");
                        if(deleted){
                            db.deleteCoures(co.getCourse_ID());


                            Toast.makeText(c, "Event has been deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(c, "Error in deleting event", Toast.LENGTH_SHORT).show();
                        }



                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorDelete", "deleting Error: " + error.getMessage());
                Toast.makeText(c,
                        "Deleting Error", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("semester", co.getSemester());
                params.put("c_no", co.getCourse_No());
                params.put("c_name", co.getCourse_Name());
                params.put("meeting_cid", "XXX");
                params.put("meeting_day", "XXX");
                params.put("meeting_time","XXX");
                params.put("meeting_building", "XXX");
                params.put("meeting_room", "XXX");
                params.put("action", "2");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void downloadSemester() {
        final String tag_string_req = "req_getAllEvent";
        Log.d("Email:", email);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEMESTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("AllSemester", "Semester: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        int size = jObj.getInt("size");
                        Log.e("Size of semester", size+"");
                        for (int i = 0; i < size; i++) {
                            JSONObject item = jObj.getJSONObject(i + "");
                            String title = item.getString("title");
                            String start = item.getString("start");
                            String end = item.getString("ended");
                            Semester s=new Semester(title,start,end);
                            db.addSemester(s);
                        }
                         session.semesterLoaded(true);
                    } else {

                        String errorMsg = jObj.getString("error_msg");

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                   Log.e("Json error: ", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("title","XXX");
                params.put("start", "XXX");
                params.put("ended", "XXX");
                params.put("action", "1");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void uploadSingleEvent(final Event e) {
        String tag_string_req = "req_getAdd";
        Log.e("in add Event","Event");
        pDialog.setMessage("Adding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("EVENT", "Adding Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json

                    if (!error) {
                        boolean added = jObj.getBoolean("add");
                        if(added){


                            Toast.makeText(c, "Event has been added", Toast.LENGTH_SHORT).show();}
                        else{ Toast.makeText(c, "Error in added event", Toast.LENGTH_SHORT).show();}

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EVENT", "adding Error: " + error.getMessage());
                Toast.makeText(c,
                        "Adding Error", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("date", e.getEventDate());
                params.put("time", e.getEventTime());
                params.put("title", e.getEventTitle());
                params.put("des", e.getEventDesc());
                params.put("action", "1");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void deleteSingleEvent(final Event e) {
        String tag_string_req = "req_getAdd";

        pDialog.setMessage("Deleting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("EVENT", "Deleting Event: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        boolean deleted = jObj.getBoolean("delete");
                        if(deleted){

                            Toast.makeText(c, "Event has been deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(c, "Error in deleting event", Toast.LENGTH_SHORT).show();
                        }



                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(c,
                                errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EVENT", "deleting Error: " + error.getMessage());
                Toast.makeText(c,
                        "Deleting Error", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("date", e.getEventDate());
                params.put("time", e.getEventTime());
                params.put("title", e.getEventTitle());
                params.put("des", e.getEventDesc());
                params.put("action", "2");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
