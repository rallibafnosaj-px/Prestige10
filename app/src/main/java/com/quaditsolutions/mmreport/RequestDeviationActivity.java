package com.quaditsolutions.mmreport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class RequestDeviationActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    GlobalVar gv = new GlobalVar();

    SQLiteDatabase sqlDB;
    Cursor cursor;

    String TAG = "Response";

    SoapPrimitive resultUpdateScheduleChanged;

    String userCode, companyCode, storeLocation, remarks, currentIPAddress;

    Button btnSubmitChangeSchedule, btnViewSchedule,
            btnMondaySchedule, btnTuesdaySchedule, btnWednesdaySchedule,
            btnThursdaySchedule, btnFridaySchedule, btnSaturdaySchedule, btnSundaySchedule;

    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.items_layout2);
        setTitle("Change of Outlet Schedule");

        createTableTemp();

        btnMondaySchedule = (Button) findViewById(R.id.btnMondaySchedule);
        btnMondaySchedule.setOnClickListener(this);

        btnTuesdaySchedule = (Button) findViewById(R.id.btnTuesdaySchedule);
        btnTuesdaySchedule.setOnClickListener(this);

        btnWednesdaySchedule = (Button) findViewById(R.id.btnWednesdaySchedule);
        btnWednesdaySchedule.setOnClickListener(this);

        btnThursdaySchedule = (Button) findViewById(R.id.btnThursdaySchedule);
        btnThursdaySchedule.setOnClickListener(this);

        btnFridaySchedule = (Button) findViewById(R.id.btnFridaySchedule);
        btnFridaySchedule.setOnClickListener(this);

        btnSaturdaySchedule = (Button) findViewById(R.id.btnSaturdaySchedule);
        btnSaturdaySchedule.setOnClickListener(this);

        btnSundaySchedule = (Button) findViewById(R.id.btnSundaySchedule);
        btnSundaySchedule.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        btnSubmitChangeSchedule = (Button) findViewById(R.id.btnSubmitChangeSchedule);
        btnSubmitChangeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                    //sqlDB.execSQL("DELETE FROM scheduleTemp");

                    cursor = sqlDB.rawQuery("SELECT storeName, " +
                            "workingDays, " +
                            "startTime, " +
                            "endTime " +
                            " FROM scheduleTemp", null);

                    if (cursor.getCount() != 0) {
                        if (cursor.moveToFirst()) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(RequestDeviationActivity.this);
                            final EditText edtRemarks = new EditText(RequestDeviationActivity.this);

                            builder.setTitle("Request Change Schedule");
                            builder.setMessage("Please input reason for requesting change of schedule.");
                            builder.setView(edtRemarks);

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    remarks = edtRemarks.getText().toString();
                                    if (remarks.equalsIgnoreCase("")) {
                                        Toast.makeText(RequestDeviationActivity.this, "Reason cannot be empty!", Toast.LENGTH_SHORT).show();
                                        edtRemarks.requestFocus();
                                    } else {
                                        requestScheduleStart();

                                    }
                                }
                            });

                            builder.setNegativeButton("NO", null);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(RequestDeviationActivity.this, "No schedule requested", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Error: " + e);
                }
            }
        });

        btnViewSchedule = (Button) findViewById(R.id.btnViewSchedule);
        btnViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDeviationActivity.this, ViewScheduleActivity.class));
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        //Intent in = new Intent(RequestDeviationActivity.this, RequestDeviationSchedule.class);
        Intent in = new Intent(RequestDeviationActivity.this, CreateNewSchedule.class);

        if (v.getId() == R.id.btnMondaySchedule) {
            in.putExtra("workingDays", "Monday");
        } else if (v.getId() == R.id.btnTuesdaySchedule) {
            in.putExtra("workingDays", "Tuesday");
        } else if (v.getId() == R.id.btnWednesdaySchedule) {
            in.putExtra("workingDays", "Wednesday");
        } else if (v.getId() == R.id.btnThursdaySchedule) {
            in.putExtra("workingDays", "Thursday");
        } else if (v.getId() == R.id.btnFridaySchedule) {
            in.putExtra("workingDays", "Friday");
        } else if (v.getId() == R.id.btnSaturdaySchedule) {
            in.putExtra("workingDays", "Saturday");
        } else if (v.getId() == R.id.btnSundaySchedule) {
            in.putExtra("workingDays", "Sunday");
        }
        insertScheduleRestDays();
        startActivity(in);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createTableTemp() {
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                "scheduleTemp(scheduleID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "workingDays VARCHAR, " +
                "startTime TEXT, " +
                "endTime TEXT, " +
                "storeName VARCHAR, " +
                "storeLocCode VARCHAR);";

        sqlDB.execSQL(SCHEDULE_TABLE);

        String SCHEDULE_RESTDAYS = "CREATE TABLE IF NOT EXISTS " +
                "scheduleRestDaysTemp(scheduleID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "restDays VARCHAR);";

        sqlDB.execSQL(SCHEDULE_RESTDAYS);
    }

    private void insertScheduleRestDays() {
        String[] restDays = {
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"};

        try {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("SELECT restDays " +
                    "FROM scheduleRestDaysTemp ", null);

            if (cursor.getCount() == 0) {
                for (int i = 0; i < restDays.length; i++) {
                    sqlDB.execSQL("INSERT INTO scheduleRestDaysTemp " +
                            "(restDays) " +
                            "VALUES " +
                            "('" + restDays[i] + "' );");
                }
            }
            sqlDB.close();
        } catch (Exception e) {
            //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
            if (!"null".equals(e) || !"".equals(e)) {
                Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Update Message Status
    private class requestChangedScheduleClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog.setTitle("Sending");
            progressDialog.setMessage("Please wait . . .");
            progressDialog.show();
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
            progressDialog.dismiss();

            try {
                if (resultUpdateScheduleChanged.toString().equals("Failed")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(RequestDeviationActivity.this);
                    b.setTitle("Failed");
                    b.setMessage("Please try again later.");
                    b.show();
                } else {
                    try {
                        Log.i(TAG, "Request Schedule");

                        AlertDialog.Builder b = new AlertDialog.Builder(RequestDeviationActivity.this);
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
}