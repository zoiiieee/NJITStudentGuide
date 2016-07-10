package com.team8.cs673.njitstudentguide.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.Utilities.Contact;
import com.team8.cs673.njitstudentguide.Utilities.Contacts_Adapter;
import com.team8.cs673.njitstudentguide.app.AppConfig;
import com.team8.cs673.njitstudentguide.app.AppController;
import com.team8.cs673.njitstudentguide.helper.HttpCalls;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;
import com.team8.cs673.njitstudentguide.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contacts extends ActionBarActivity {
    ImageButton back;
    private SessionManager session;
    private SQLiteHandler db;
    private ListView listView;
    private ArrayList<Contact> contactsList;
    private Contacts_Adapter adapter;
    private HttpCalls hc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        customActionbar();
        hc=new HttpCalls(this,"XXX");
        session = new SessionManager(getApplicationContext());
        db=new SQLiteHandler(getApplicationContext());
        listView = (ListView) findViewById(R.id.contacts_list);
        if (session.isContLoaded()) {
            showContacts();
        }else {hc.downloadContacts();
            showContacts();
        }
    }


    private void showContacts(){
        contactsList=new ArrayList<>();
        contactsList=db.getAllContacts();
        adapter = new Contacts_Adapter(Contacts.this,contactsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+listView.getItemAtPosition(position).toString()));
                startActivity(callIntent);
            }
        });

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
        title.setText("Contacts");
        Button btnLogin_logout = (Button) findViewById(R.id.btnlogin_out);
        btnLogin_logout.setVisibility(View.INVISIBLE);
        back = (ImageButton) findViewById(R.id.btn_backtomain);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(Contacts.this, Dashboard_Activity.class);
                    startActivity(intent);
                    finish();
            }
        });
    }
}
