package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rozz on 31/07/2018.
 */

// created change request schedule
public class ViewScheduleActivity extends AppCompatActivity {

    String userCode, companyCode, storeLocation, remarks;

    SQLiteDatabase sqlDB;
    Cursor cursor;

    GlobalVar gv = new GlobalVar();
    List<GlobalVar> getDataAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewScheduleView recyclerViewScheduleView;

    Button btnSubmitDeleteSchedule, btnSubmitChangeSchedule;
    CheckBox chkSchedID;
    String TAG = "Response", currentIPAddress;

    SoapPrimitive resultUpdateScheduleChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // test if login as prestige, regris etc.
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);
        currentIPAddress = sp.getString("ipAddress", null);

        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                //imgViewHome.setImageResource(R.drawable.prestige);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                }
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                //imgViewHome.setImageResource(R.drawable.regcris);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                }
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                //imgViewHome.setImageResource(R.drawable.tmarks);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                }
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_schedule_layout);

        getDataAdapter = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_schedule_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewScheduleView = new RecyclerViewScheduleView(getDataAdapter, this);
        recyclerView.setAdapter(recyclerViewScheduleView);

        chkSchedID = (CheckBox) findViewById(R.id.chkSchedID);

        btnSubmitChangeSchedule = (Button) findViewById(R.id.btnSubmitChangeSchedule);
        btnSubmitChangeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                    cursor = sqlDB.rawQuery("SELECT storeName, " +
                            "workingDays, " +
                            "startTime, " +
                            "endTime " +
                            " FROM scheduleTemp", null);

                    if (cursor.getCount() != 0) {
                        if (cursor.moveToFirst()) {
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewScheduleActivity.this);
                            final EditText edtRemarks = new EditText(ViewScheduleActivity.this);

                            builder.setTitle("Request Change Schedule");
                            builder.setMessage("Please input reason for requesting change of schedule.");
                            builder.setView(edtRemarks);

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    remarks = edtRemarks.getText().toString();
                                    if (remarks.equalsIgnoreCase("")) {
                                        Toast.makeText(ViewScheduleActivity.this, "Reason cannot be empty!", Toast.LENGTH_SHORT).show();
                                        edtRemarks.requestFocus();
                                    } else {
                                        requestScheduleStart();
                                    }
                                }
                            });

                            builder.setNegativeButton("NO", null);
                            android.support.v7.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(ViewScheduleActivity.this, "No schedule requested", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("TAG", "Error: " + e);
                }
            }
        });

        btnSubmitDeleteSchedule = (Button) findViewById(R.id.btnSubmitDeleteSchedule);
        btnSubmitDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(ViewScheduleActivity.this);
                b.setTitle("Confirm Delete");
                b.setMessage("Are you sure you want to delete?");
                b.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                            cursor = sqlDB.rawQuery("SELECT chkID FROM scheduleCheckedID", null);

                            if (cursor.getCount() == 0) {
                                Toast.makeText(ViewScheduleActivity.this, "Choose schedule to delete.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (cursor.moveToFirst()) {
                                    do {
                                        sqlDB.execSQL("DELETE FROM scheduleCheckedID WHERE chkID = '" + cursor.getString(0) + "'");
                                        sqlDB.execSQL("DELETE FROM scheduleTemp WHERE scheduleID = '" + cursor.getString(0) + "'");
                                        finish();
                                        startActivity(getIntent());
                                        Toast.makeText(ViewScheduleActivity.this, cursor.getString(0) + "Schedule Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                    while (cursor.moveToNext());
                                    recyclerViewScheduleView.notifyDataSetChanged();
                                    sqlDB.close();
                                }
                            }
                        } catch (Exception e) {
                            Log.i("TAG", "Error deleting created schedule: " + e);
                        }

                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();
            }
        });

        getScheduleDataList();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void getScheduleDataList() {
        try {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("SELECT workingDays, " +
                    "startTime, " +
                    "endTime, " +
                    "storeName, " +
                    "storeLocCode,scheduleID FROM scheduleTemp ORDER BY " +
                    "case when workingDays = 'Monday' then 1 " +
                    "when workingDays = 'Tuesday' then 2 " +
                    "when workingDays = 'Wednesday' then 3 " +
                    "when workingDays = 'Thursday' then 4 " +
                    "when workingDays = 'Friday' then 5 " +
                    "when workingDays = 'Saturday' then 6 " +
                    "when workingDays = 'Sunday' then 7 " +
                    "else 8 end ASC", null);

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No Schedule Found.", Toast.LENGTH_SHORT).show();
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        GlobalVar globalVar = new GlobalVar();

                        globalVar.schedWorkingDay = cursor.getString(0);
                        globalVar.schedStartTime = cursor.getString(1);
                        globalVar.schedEndTime = cursor.getString(2);
                        globalVar.schedStoreName = cursor.getString(3);
                        globalVar.schedStoreLocCode = cursor.getString(4);
                        globalVar.schedscheduleID = cursor.getString(5);

                        getDataAdapter.add(globalVar);
                    }
                    while (cursor.moveToNext());

                    recyclerViewScheduleView.notifyDataSetChanged();
                    sqlDB.close();

                }
            }
        } catch (Exception e) {
            Log.i("TAG", "Error fetching schedule list: " + e);
        }
    }

    //Update Message Status
    private class requestChangedScheduleClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            /*
            final int totalProgressTime = 100;
            int jumTime = 0;
            p = new ProgressDialog(RequestDeviationActivity.this);
            p.setMessage("Sending ...");
            p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            p.setIndeterminate(true);
            p.show();
            p.setCancelable(true);
            */

            cursor = sqlDB.rawQuery("SELECT workingDays, " +
                    "startTime, " +
                    "endTime, " +
                    "storeLocCode " +
                    " FROM scheduleTemp", null);

            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        requestChangedSchedule(cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));
                    }
                    while (cursor.moveToNext());
                }
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultUpdateScheduleChanged.toString().equals("Failed")) {
                    android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(ViewScheduleActivity.this);
                    b.setTitle("Failed");
                    b.setMessage("Please try again later.");
                    b.show();
                } else {
                    try {
                        Log.i(TAG, "Request Schedule");

                        android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(ViewScheduleActivity.this);
                        b.setTitle("Success");
                        b.setMessage("Change schedule request was sent successfully.");
                        b.setCancelable(true);
                        b.setPositiveButton("OK", null);
                        b.show();

                        sqlDB.execSQL("DELETE FROM scheduleTemp");
                    } catch (Exception e) {
                        Log.i(TAG, "Error :" + e);
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "Error: " + e);
            }
        }
    }

    private void requestChangedSchedule(String workingDays, String startTime, String endTime, String storeLocCode) {
        String SOAP_ACTION = "http://tempuri.org/insertChangeScheduleRequest";
        String METHOD_NAME = "insertChangeScheduleRequest";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("reason", remarks);
            Request.addProperty("employeeNo", userCode);
            Request.addProperty("workingDays", workingDays);
            Request.addProperty("starttime", startTime);
            Request.addProperty("endtime", endTime);
            Request.addProperty("storeLocCode", storeLocCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultUpdateScheduleChanged = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Request: " + resultUpdateScheduleChanged);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    private void requestScheduleStart() {
        requestChangedScheduleClass sRequest = new requestChangedScheduleClass();
        sRequest.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}