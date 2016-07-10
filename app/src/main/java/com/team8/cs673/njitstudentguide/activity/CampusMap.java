package com.team8.cs673.njitstudentguide.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.Utilities.ExpandableListAdapter;
import com.team8.cs673.njitstudentguide.Utilities.Location;
import com.team8.cs673.njitstudentguide.helper.HttpCalls;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;
import com.team8.cs673.njitstudentguide.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampusMap extends FragmentActivity  {
ImageButton btnBack;
    Circle circle;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private HttpCalls hc;
    HashMap<String, List<Location>> listDataChild;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);
        setUpMapIfNeeded();
        hc=new HttpCalls(this,"XXX");
        session=new SessionManager(getApplicationContext());
        db=new SQLiteHandler(getApplicationContext());
        btnBack = (ImageButton) findViewById(R.id.btn_backtomainMap);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CampusMap.this, Dashboard_Activity.class);
                startActivity(intent);
                finish();
            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        // preparing list data
        if (session.isLocLoaded()) {
            prepareListData();
        }else {hc.downloadLocations();
            prepareListData();
        }

    }



    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Location>>();

        // Adding child data
        listDataHeader.add("Building");
        listDataHeader.add("Administrative Departments");
        listDataHeader.add("Student Services");
        listDataHeader.add("Academic Departments");
        listDataHeader.add("Student Parking");

        List<Location> building = new ArrayList<Location>();
        building= db.getAllLocation("Building");
        listDataChild.put(listDataHeader.get(0), building);

        List<Location> administrative = new ArrayList<Location>();
        administrative= db.getAllLocation("Administrative Departments");
        listDataChild.put(listDataHeader.get(1), administrative);

        List<Location> sservices = new ArrayList<Location>();
        sservices= db.getAllLocation("Student Services");
        listDataChild.put(listDataHeader.get(2), sservices);

        List<Location> academic = new ArrayList<Location>();
        academic= db.getAllLocation("Academic Departments");
        listDataChild.put(listDataHeader.get(3), academic);

        List<Location> parking = new ArrayList<Location>();
        parking= db.getAllLocation("Student Parking");
        listDataChild.put(listDataHeader.get(4), parking);

        for(int i=0;i<building.size(); i++){
            String sss=building.get(i).getCoordinates();
            String[] items=sss.split(",");
            double lat=Double.parseDouble(items[0]);
            double lon=Double.parseDouble(items[1]);

            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(building.get(i).getItem_name()));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                String loc=marker.getPosition().latitude+","+marker.getPosition().longitude;
                String sn=db.getAllLocTags(loc);
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.map_dialog);
                final TextView lblTageTitle=(TextView)dialog.findViewById(R.id.lbltagTitle);
                final TextView lblTages=(TextView)dialog.findViewById(R.id.lbltags);
                final Button gotDirectioon=(Button)dialog.findViewById(R.id.getdirection);
                gotDirectioon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "http://maps.google.com/maps/?daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                lblTageTitle.setText(marker.getTitle());
                if (sn.equals("")){lblTages.setVisibility(View.GONE);}
                lblTages.setText(sn);

                //dialog.setTitle(marker.getTitle());
                dialog.show();

                return false;
            }
        });
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Location l=listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                String sss=l.getCoordinates();
                String[] items=sss.split(",");
                double lat=Double.parseDouble(items[0]);
                double lon=Double.parseDouble(items[1]);

                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(l.getItem_name()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
                if(circle!=null){
                    circle.remove();
                }
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lon))
                        .radius(30)
                        .strokeColor(Color.argb(50,200,0,0))
                        .fillColor(Color.argb(70,0,0,200)));
                return false;
            }
        });
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(40.741927, -74.179634)).
                tilt(45).
                zoom(17).
                bearing(0).
                        build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setBuildingsEnabled(true);
    }

}

