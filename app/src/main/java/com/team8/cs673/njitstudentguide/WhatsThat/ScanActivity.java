package com.team8.cs673.njitstudentguide.WhatsThat;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;
import com.team8.cs673.njitstudentguide.R;

public class ScanActivity extends Activity implements AutoScannerSession.Listener , Scanner.SyncListener {

    private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE | Result.Type.EAN13;
    private ImageView im;
    private TextView tv;
    private AutoScannerSession session = null;
    private static final String API_KEY    = "ndttagcu3o0cxlxuarqo";
    private static final String API_SECRET = "GW24gl5LIgMhNXrL";

    private boolean compatible = false;
    private Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        SurfaceView preview = (SurfaceView)findViewById(R.id.preview);


        compatible = Scanner.isCompatible();
        if (compatible) {
            try {
                scanner = Scanner.get();
                String path = Scanner.pathFromFilesDir(this, "scanner.db");
                scanner.open(path, API_KEY, API_SECRET);
                scanner.setSyncListener(this);
                scanner.sync();
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
        try {
            session = new AutoScannerSession(this, Scanner.get(), this, preview);
            session.setResultTypes(TYPES);
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        session.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.stop();
    }

    @Override
    public void onCameraOpenFailed(Exception e) {
        // You should inform the user if this occurs!
    }

    @Override
    public void onWarning(String debugMessage) {
        // Useful for debugging!
    }

    @Override
    public void onResult(Result result) {
        Log.e("Found,", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.building_found_dialog , null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.resume();
            }
        });
        im=(ImageView)dialoglayout.findViewById(R.id.image_result);
        tv=(TextView)dialoglayout.findViewById(R.id.text_result);
        switch (result.getValue()) {
            case "1":
                tv.setText("Central King Building");
                break;
            case "2":
                tv.setText("Tiernan Hall");
                break;
            case "3":
                tv.setText("Campus Center");
                break;
            case "4":
                tv.setText("Kupfrian Hall");
                break;
            case "5":
                tv.setText("GITC");
                break;
        }

         //   tv.setText(result.getValue());
        builder.setView(dialoglayout);

      //  builder.setTitle(result.getType() == Result.Type.IMAGE ? "Image:" : "Barcode:");
        //builder.setMessage(result.getValue());
        builder.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compatible) {
            try {
                scanner.close();
                scanner.destroy();
                scanner = null;
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSyncStart() {
        Log.d("Moodstocks SDK", "Sync will start.");
    }

    @Override
    public void onSyncComplete() {
        try {
            Log.d("Moodstocks SDK", "Sync succeeded ("+scanner.count()+" images)");
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncFailed(MoodstocksError e) {
        Log.d("Moodstocks SDK", "Sync error #"+e.getErrorCode()+": "+e.getMessage());
    }

    @Override
    public void onSyncProgress(int total, int current) {
        int percent = (int) ((float) current / (float) total * 100);
        Log.d("Moodstocks SDK", "Sync progressing: "+percent+"%");
    }
}
