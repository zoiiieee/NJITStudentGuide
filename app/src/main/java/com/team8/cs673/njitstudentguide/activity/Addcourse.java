package com.team8.cs673.njitstudentguide.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.team8.cs673.njitstudentguide.EventNotification.Alarm;
import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.Utilities.Courses;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.Meeting;
import com.team8.cs673.njitstudentguide.Utilities.MeetingAdapter;
import com.team8.cs673.njitstudentguide.Utilities.Semester;
import com.team8.cs673.njitstudentguide.helper.HttpCalls;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Addcourse extends ActionBarActivity {
    private Spinner spinner2;
    private Button cancel, save, addMeating;
    private EditText lblC_code;
    private EditText lblC_name;
    private ListView m_lv;
    private static TextView semester_start;
    private static TextView semester_end;
    final Context context = this;
    public MeetingAdapter meetingAdapter;
    private SQLiteHandler db;
    private String email;
    private String[] daysList = new String[]{"Select Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private List<String> days = new ArrayList<String>();
    private List<String> buildings = new ArrayList<String>();
    public  static HashMap<Integer, String> buildingHash = new HashMap<>();
    private ArrayList<Meeting> course_meeting;
    private String selectedDay = "";
    private String selectedBuilding = "";
    private String time = "";
    private String room = "";
    private String selectedSemester = "";
    private String courseNo = "";
    private String courseName = "";
    private ArrayList<Semester> seList;
    private HttpCalls hc;
    private int index;
    private Alarm alarm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        index=intent.getIntExtra("index", 0);
        customActionbar();
        alarm=new Alarm(this);
        course_meeting = new ArrayList<>();
        seList=new ArrayList<>();
        hc=new HttpCalls(this,email);
        setContentView(R.layout.add_course);
        getAllgetItem_name();
        addItemsOnSpinner2();
        lblC_code = (EditText) findViewById(R.id.c_code);
        lblC_name = (EditText) findViewById(R.id.c_name);
        days.addAll(Arrays.asList(daysList));
        m_lv = (ListView) findViewById(R.id.meeting_lv);
        cancel = (Button) findViewById(R.id.c_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Addcourse.this, Classes.class);
                startActivity(intent);
                finish();
            }
        });
        save = (Button) findViewById(R.id.c_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
            }
        });
        addMeating = (Button) findViewById(R.id.btn_add_meeting);
        addMeating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeetingDialog();
            }
        });
        lblC_code = (EditText) findViewById(R.id.c_code);
        lblC_name = (EditText) findViewById(R.id.c_name);
        db=new SQLiteHandler(getApplicationContext());
    }

    private void saveCourse() {
        boolean semesterOK, courseNoOK, courseNameOK, meetingListOK;
        if (semester_start.getText().length()>5&&semester_end.getText().length()>5) {
            if(rangeOK(semester_start.getText().toString(), semester_end.getText().toString())){
            selectedSemester = spinner2.getSelectedItem().toString();
            semesterOK = true;}
            else{semesterOK = false;}
        } else {
            semesterOK = false;
            Toast.makeText(getApplicationContext(),
                    "Please Select a Semester", Toast.LENGTH_LONG)
                    .show();
        }
        if (!isEmpty(lblC_code)) {
            courseNo = lblC_code.getText().toString();
            courseNoOK = true;
        } else {
            courseNoOK = false;
            Toast.makeText(getApplicationContext(),
                    "You need to Enter Course Number", Toast.LENGTH_LONG)
                    .show();
        }
        if (!isEmpty(lblC_name)) {
            courseName = lblC_name.getText().toString();
            courseNameOK = true;
        } else {
            courseNameOK = false;
            Toast.makeText(getApplicationContext(),
                    "You need to Enter Course Number", Toast.LENGTH_LONG)
                    .show();
        }
        if (course_meeting.size() <= 0) {
            meetingListOK = false;
            Toast.makeText(getApplicationContext(),
                    "Add meetings for the Course", Toast.LENGTH_LONG)
                    .show();
        } else {
            meetingListOK = true;
        }
        Log.e("Save", semesterOK + ", " + courseNoOK + ", " + courseNameOK + ", " + meetingListOK);
        if (semesterOK && courseNoOK && courseNameOK && meetingListOK) {
            Semester s;
            boolean s_update=false;
            String begin=semester_start.getText().toString();
            String finish=semester_end.getText().toString();
            Log.d("Semester :begin:"+begin,"End: "+finish);
            s=db.getSemester(selectedSemester);

            if(s!=null) {
                if (!(s.getTitle().equals(selectedSemester) && s.getStart().equals(begin) && s.getEnd().equals(finish))) {
                    s = new Semester(selectedSemester, begin, finish);
                    hc.uploadSingleSemester(s);
                    Log.e("semester exist s", "updated");
                    s_update=true;

                }else{ Log.e("exist s","no action");}
            }else{s = new Semester(selectedSemester, begin, finish);
                hc.uploadSingleSemester(s);
                Log.e("no exist s", "added");
            }

            Courses c = new Courses(selectedSemester, courseNo, courseName, course_meeting);

            hc.uploadSingleCourse(c);
            //db.addcourse(c);
            if(s_update){
                updateCourseMeetingDates(s);
                scheduleClassesToEvents(c, s);
            }else {
                scheduleClassesToEvents(c, s);
            }
            Toast.makeText(getApplicationContext(),
                    "one course added ", Toast.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(Addcourse.this, Classes.class);
            startActivity(intent);
            finish();
        }
    }

private void updateCourseMeetingDates(Semester s){
    ArrayList<Courses>cList;
    cList=db.getAllCourses(s.getTitle());
    for (Courses co: cList){
        deleteScheduledEvents(co);
    }
    for (Courses co: cList){
       scheduleClassesToEvents(co,s);
    }


}
    private void addMeetingDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_meating_dialog);
        final EditText classRoom = (EditText) dialog.findViewById(R.id.m_room);
        final TimePicker class_start = (TimePicker) dialog.findViewById(R.id.meeting_start);
        final TimePicker class_end = (TimePicker) dialog.findViewById(R.id.meeting_end);
        class_start.setIs24HourView(true);
        class_end.setIs24HourView(true);
        final Spinner daysSpinner = (Spinner) dialog.findViewById(R.id.m_day);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, days);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        daysSpinner.setAdapter(dataAdapter);
        final Spinner bulidingList = (Spinner) dialog.findViewById(R.id.m_building);
        ArrayAdapter<String> bAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, buildings);
        bAdapter.setDropDownViewResource(R.layout.spinner_item);
        bulidingList.setAdapter(bAdapter);
        final Button dimiss = (Button) dialog.findViewById(R.id.btnDismissAdd);
        dimiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Button saveMeeting = (Button) dialog.findViewById(R.id.btnSaveAdd);
        saveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean dayOK, timeOK, buildingOK, roomOK;
                if (daysSpinner.getSelectedItemPosition() != 0) {
                    selectedDay = daysSpinner.getSelectedItem().toString();
                    dayOK = true;
                } else {
                    dayOK = false;
                    Toast.makeText(getApplicationContext(),
                            "Please Select a Day", Toast.LENGTH_LONG)
                            .show();
                }
                if (class_start.getCurrentHour() <= class_end.getCurrentHour()) {
                    if ((class_start.getCurrentHour() == class_end.getCurrentHour()&&class_start.getCurrentMinute() <= class_end.getCurrentMinute())
                            || (class_start.getCurrentHour() < class_end.getCurrentHour())) {
                        time = class_start.getCurrentHour() + ":" + class_start.getCurrentMinute() + " - "
                                + class_end.getCurrentHour() + ":" + class_end.getCurrentMinute();
                        timeOK = true;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Starting time should be less than Ending time", Toast.LENGTH_SHORT)
                                .show();
                        timeOK = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Starting time should be less than Ending time", Toast.LENGTH_SHORT)
                            .show();
                    timeOK = false;
                }
                if (bulidingList.getSelectedItemPosition() != 0) {
                    selectedBuilding = bulidingList.getSelectedItem().toString();
                    buildingOK = true;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Select a Building", Toast.LENGTH_LONG)
                            .show();
                    buildingOK = false;
                }

                if (!isEmpty(classRoom)) {
                    room = classRoom.getText().toString();
                    roomOK = true;
                } else {
                    roomOK = false;
                    Toast.makeText(getApplicationContext(),
                            "Enter Meeting Room", Toast.LENGTH_LONG)
                            .show();
                }
                if (dayOK && timeOK && buildingOK && roomOK) {
                    Meeting m = new Meeting(0, selectedDay, time, getBuildingKey(selectedBuilding), room);
                    course_meeting.add(m);
                    prepareMeetingList();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void addItemsOnSpinner2() {
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        semester_start=(TextView)findViewById(R.id.s_start);
        semester_end=(TextView)findViewById(R.id.s_end);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        int year = calendar.get(Calendar.YEAR);
        final List<String> semesters;

        semesters = Arrays.asList(getResources().getStringArray(R.array.semester_arrays));
        Log.i("semesters", semesters.toString());
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 2; i++) {
            int y = year + i;
            for (int j = 0; j < semesters.size(); j++) {
                list.add(semesters.get(j) + " " + y);
            }
        }
        list.add(0, "Select Semester");
        spinner2.post(new Runnable() {
            @Override
            public void run() {
                spinner2.setSelection(index);
            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner2.setAdapter(dataAdapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayList<Semester>sList;
                sList =db.getAllSemester();
                if (spinner2.getSelectedItemPosition()==0){
                    semester_start.setText("");
                    semester_end.setText("");
                }
                else {
                    Semester s=db.getSemester((String)spinner2.getItemAtPosition(position));
                    if(s!=null){

                        semester_start.setText(s.getStart());
                        semester_end.setText(s.getEnd());
                    }
                else {
                        semester_start.setText("");
                        semester_end.setText("");
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    private void getAllgetItem_name() {
        this.buildingHash=Classes.buildingHash;
        buildings = new ArrayList<String>(buildingHash.values());
        buildings.add(0, "Select Building");
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public int getBuildingKey(String building) {
        for (Map.Entry entry : buildingHash.entrySet()) {
            if (building.equals(entry.getValue())) {
                Log.e("wwww", entry.getKey() + "");
                return (int) entry.getKey();
            }
        }
        return 0;
    }

    private void prepareMeetingList() {
        meetingAdapter = new MeetingAdapter(Addcourse.this, course_meeting, this.buildingHash,true);
        m_lv.setAdapter(meetingAdapter);
    }

    public void deleteEvent(Meeting m) {

    }

    public void scheduleClassesToEvents(Courses c, Semester s) {
        String start = s.getStart();
        String end  = s.getEnd();
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        Calendar c_Start = Calendar.getInstance();
        Calendar c_End = Calendar.getInstance();
        try {
            startDate = curFormater.parse(start);
            endDate = curFormater.parse(end);
            c_Start.setTime(startDate);
            c_End.setTime(endDate);
            ArrayList<Meeting> mList = c.getMlist();
            String day;
            for (Meeting m : mList) {
               c_Start.setTime(startDate);
                Log.e("TEST_mList size"," "+mList.size());
                boolean dayReached = false;
                day = m.getMeeeting_Day();
                while (c_Start.before(c_End)) {
                    if (c_Start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).equals(day)) {
                        Log.e("TEST", day+" "+ c_Start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));

                        dayReached = true;
                        String formatted = curFormater.format(c_Start.getTime());
                        String title = c.getCourse_No() + " " + c.getCourse_Name();
                        String desc = buildingHash.get(m.getMeeting_Building()) + " " + m.getMeeting_Room();
                        Event e=new Event(formatted, m.getMeeting_time(), title, desc);
                        long eID=db.addEvent(e);
                        alarm.addAlarm(e, (int)eID);
                    }
                    if (dayReached) {
                        c_Start.add(Calendar.DATE, 7);
                    } else {
                        c_Start.add(Calendar.DATE, 1);
                    }
                }
            }

        } catch (ParseException e) {
        }


    }

    public void deleteScheduledEvents(Courses c) {
        ArrayList<Meeting> mList = c.getMlist();
        for (Meeting m : mList) {
            db.deleteClassEvents(c.getCourse_No() + " " + c.getCourse_Name(),buildingHash.get(m.getMeeting_Building()) + " " + m.getMeeting_Room());
               }
    }
    public void startDatePickerDialog(View v) {
        if (spinner2.getSelectedItemPosition()!=0){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "start");}
        else{ Toast.makeText(getApplicationContext(),
                "Please Select a Semester", Toast.LENGTH_SHORT)
                .show();}
    }
    public void endDatePickerDialog(View v) {
        if (spinner2.getSelectedItemPosition()!=0){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "end");}
        else{ Toast.makeText(getApplicationContext(),
                "Please Select a Semester", Toast.LENGTH_SHORT)
                .show();}

    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            int m=month+1;
           if(getTag().equals("start")){
            semester_start.setText(year + "-" + m +"-"+day);
           }
            if(getTag().equals("end")){
                semester_end.setText(year + "-" + m +"-"+day);
            }
        }
    }

    private void customActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View view = getLayoutInflater().inflate(R.layout.action_bar,
                null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layoutParams);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(-10, -10);
        TextView title=(TextView)findViewById(R.id.txtitle);
        title.setText("Add Course");
        Button btnLogin_logout = (Button) findViewById(R.id.btnlogin_out);
        btnLogin_logout.setVisibility(View.INVISIBLE);
        ImageButton back = (ImageButton) findViewById(R.id.btn_backtomain);
        back.setVisibility(View.INVISIBLE);

    }

    private boolean rangeOK(String s,String e)  {

        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        Calendar c_Start = Calendar.getInstance();
        Calendar c_End = Calendar.getInstance();

        try {
            startDate = curFormater.parse(s);
            endDate = curFormater.parse(e);
            if (startDate.before(endDate)){
                return true;
            }
            else{Toast.makeText(getApplicationContext(),
                    "Starting date should be less than Ending date", Toast.LENGTH_SHORT)
                    .show();
                return false;
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
            return false;
        }



    }
}
