package com.quaditsolutions.mmreport;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class RequestDeviationChangeActivity extends AppCompatActivity
{

    SQLiteDatabase sqlDB;
    Cursor cursor;

    String userCode, companyCode,
            workingDays, startTime, endTime, storeName, storeLocCode, timeIn24HrsVal="", timeOut24HrsVal="";

    TextView txtStartTimeRequest, txtEndTimeRequest,
            txtStoreNameDisplay, txtStartTimeDisplay, txtEndTimeDisplay;

    String timeFlag="";
    final static int DIALOG_ID = 0;
    int hour_val, minute_val;

    Button btnSetScheduleRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // test if login as prestige, regris etc.
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

        switch (companyCode)
        {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                //imgViewHome.setImageResource(R.drawable.prestige);
                if (Build.VERSION.SDK_INT >= 21)
                {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                }
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                //imgViewHome.setImageResource(R.drawable.regcris);
                if (Build.VERSION.SDK_INT >= 21)
                {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                }
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                //imgViewHome.setImageResource(R.drawable.tmarks);
                if (Build.VERSION.SDK_INT >= 21)
                {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                }
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_deviation_change);
        setTitle("Schedule Details");

        Intent getIn = getIntent();
        workingDays = getIn.getStringExtra("workingDays");
        storeLocCode = getIn.getStringExtra("storeLocCode");
        storeName = getIn.getStringExtra("storeName");
        startTime = getIn.getStringExtra("startTime");
        endTime = getIn.getStringExtra("endTime");

        txtStoreNameDisplay = (TextView)findViewById(R.id.txtStoreNameDisplay);
        txtStartTimeDisplay = (TextView)findViewById(R.id.txtStartTimeDisplay);
        txtEndTimeDisplay = (TextView)findViewById(R.id.txtEndTimeDisplay);

        txtStartTimeRequest = (TextView)findViewById(R.id.txtStartTimeRequest);
        txtEndTimeRequest = (TextView)findViewById(R.id.txtEndTimeRequest);
        txtStoreNameDisplay.setText(storeName);

        getAndSetValueOfCurrentTimeInOut();

        btnSetScheduleRequest= (Button) findViewById(R.id.btnSetScheduleRequest);
        btnSetScheduleRequest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (timeIn24HrsVal.isEmpty())
                {
                    Toast.makeText(RequestDeviationChangeActivity.this,"Please select new time in",Toast.LENGTH_LONG).show();
                }
                else if (timeOut24HrsVal.isEmpty())
                {
                    Toast.makeText(RequestDeviationChangeActivity.this,"Please select new time out",Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                        cursor = sqlDB.rawQuery("SELECT workingDays, " +
                                "startTime, " +
                                "endTime, " +
                                "storeName, " +
                                "storeLocCode " +
                                " FROM scheduleTemp " +
                                "WHERE workingDays=?", new String[]{workingDays});

                        if (cursor.getCount() != 0)
                        {
                            if (cursor.moveToFirst())
                            {
                                sqlDB.execSQL("UPDATE scheduleTemp " +
                                        "SET " +
                                        "workingDays = '" + workingDays + "', " +
                                        "startTime= '" + timeIn24HrsVal + "', " +
                                        "endTime = '" + timeOut24HrsVal + "', " +
                                        "storeName = '" + storeName + "', " +
                                        "storeLocCode = '" + storeLocCode +"' " +
                                        "WHERE workingDays = '" + workingDays + "' ");

                                Log.i("TAG", "Working Days: "+workingDays);

                                /*Toast.makeText(RequestDeviationChangeActivity.this,
                                        "Success Set Change - Work Schedule"
                                                +"\n\n"+"Working days: " + workingDays
                                                +"\n"+ "Start Time: " + txtStartTimeRequest.getText().toString()
                                                +"\n"+ "End Time: " + txtEndTimeRequest.getText().toString()
                                                +"\n"+ "Store Name: " + storeName,
                                        Toast.LENGTH_LONG).show();*/

                                Toast.makeText(RequestDeviationChangeActivity.this,workingDays+" Schedule Set.", Toast.LENGTH_LONG).show();

                                //Intent in = new Intent(RequestDeviationChangeActivity.this, RequestDeviationActivity.class);
                                //startActivity(in);

                            }
                        }
                        else
                        {
                            sqlDB.execSQL("INSERT INTO scheduleTemp " +
                                    "(workingDays, "      +
                                    "startTime, "         +
                                    "endTime, "           +
                                    "storeName, "         +
                                    "storeLocCode) "      +
                                    "VALUES "             +
                                    "('"   + workingDays  +
                                    "', '" + timeIn24HrsVal  +
                                    "', '" + timeOut24HrsVal +
                                    "', '" + storeName       +
                                    "', '" + storeLocCode    +"');");

                            /*Toast.makeText(RequestDeviationChangeActivity.this,
                                    "Success Set Request - Work Schedule"
                                            +"\n\n"+"Working days: " + workingDays
                                            +"\n"+ "Start Time: " + txtStartTimeRequest.getText().toString()
                                            +"\n"+ "End Time: " + txtEndTimeRequest.getText().toString()
                                            +"\n"+ "Store Name: " + storeName,
                                    Toast.LENGTH_LONG).show();*/

                            Toast.makeText(RequestDeviationChangeActivity.this,workingDays+" Schedule Set.", Toast.LENGTH_LONG).show();

                            //Intent in = new Intent(RequestDeviationChangeActivity.this, RequestDeviationActivity.class);
                            //startActivity(in);

                        }
                    }

                    catch (Exception e)
                    {
                        if (!"null".equals(e) || !"".equals(e))
                        {
                            Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        showTimePickerDialog();
    }

    // get and set value of current time in and out to 12 hrs format
    public void getAndSetValueOfCurrentTimeInOut(){

        String sTime = startTime, eTime=endTime, DELIMETER=":";

        ArrayList<String> arrItems = new ArrayList<String>();
        String[] strTemp = sTime.split(DELIMETER);
        arrItems.add(strTemp[0]);
        arrItems.add(strTemp[1]);
        arrItems.add(strTemp[2]);

        ArrayList<String> arrEndTime = new ArrayList<>();
        String[] strEndTime = eTime.split(DELIMETER);
        arrEndTime.add(strEndTime[0]);
        arrEndTime.add(strEndTime[1]);
        arrEndTime.add(strEndTime[2]);

        int stHrs = Integer.parseInt(strTemp[0]);
        String timeSet = "";

        if (stHrs > 12){
            stHrs -= 12;
            timeSet = "PM";
        }else if(stHrs == 0){
            stHrs += 12;
            timeSet = "AM";
        }else if(stHrs == 12){
            timeSet = "PM";
        }else{
            timeSet = "AM";
        }

        int etHrs = Integer.parseInt(strEndTime[0]);
        String endTimeSet = "";

        if (etHrs > 12){
            etHrs -= 12;
            endTimeSet = "PM";
        }else if(etHrs == 0){
            etHrs += 12;
            endTimeSet = "AM";
        }else if(etHrs == 12){
            endTimeSet =  "PM";
        }else{
            endTimeSet = "AM";
        }

        txtStartTimeDisplay.setText(stHrs+":"+strTemp[1]+" "+timeSet);
        txtEndTimeDisplay.setText(etHrs+":"+strEndTime[1]+" "+endTimeSet);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // showTimePickerDialog
    public void showTimePickerDialog()
    {

        // edit text onclick command to show time picker
        txtStartTimeRequest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(DIALOG_ID);
                timeFlag = "timein";
            }
        });
        txtEndTimeRequest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(DIALOG_ID);
                timeFlag = "timeout";
            }
        });
    }

    // for time picker
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id==DIALOG_ID)
            return new TimePickerDialog(RequestDeviationChangeActivity.this, kTimePickerListener, hour_val, minute_val, false);
        return null;
    }

    // for time picker
    protected TimePickerDialog.OnTimeSetListener kTimePickerListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {

            hour_val = hourOfDay;
            minute_val = minute;

            if (timeFlag.equalsIgnoreCase("timein"))
            {
                String timeSet = "";

                if (hour_val > 12)
                {
                    hour_val -= 12;
                    timeSet = "PM";
                }
                else if (hour_val == 0)
                {
                    hour_val += 12;
                    timeSet = "AM";
                }
                else if (hour_val == 12)
                {
                    timeSet = "PM";
                }
                else
                {
                    timeSet = "AM";
                }

                String min = "";
                String hr = "";

                if (minute_val < 10)
                    min = "0" + minute_val ;
                else
                    min = String.valueOf(minute_val);

                if (hour_val < 10)
                    hr = "0" + hour_val;
                else
                    hr = String.valueOf(hour_val);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hr).append(':')
                        .append(min ).append(" ").append(timeSet).toString();

                txtStartTimeRequest.setText(aTime);

                timeIn24HrsVal = String.valueOf(String.format ("%02d", hourOfDay)) + ":" + String.valueOf(String.format ("%02d", minute) + ":" + "00");

            }
            else if (timeFlag.equalsIgnoreCase("timeout"))
            {
                String timeSet = "";
                if (hour_val > 12)
                {
                    hour_val -= 12;
                    timeSet = "PM";
                }
                else if (hour_val == 0)
                {
                    hour_val += 12;
                    timeSet = "AM";
                }
                else if (hour_val == 12)
                {
                    timeSet = "PM";
                }
                else
                {
                    timeSet = "AM";
                }

                String min = "", hr = "";

                if (minute_val < 10)
                    min = "0" + minute_val ;
                else
                    min = String.valueOf(minute_val);
                if (hour_val < 10)
                    hr = "0" + hour_val;
                else
                    hr = String.valueOf(hour_val);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hr).append(':')
                        .append(min ).append(" ").append(timeSet).toString();

                txtEndTimeRequest.setText(aTime);

                timeOut24HrsVal = String.valueOf(String.format ("%02d", hourOfDay)) + ":" + String.valueOf(String.format ("%02d", minute) + ":" + "00");
            }
            else
            {
                //Do nothing...
            }
        }
    };
}
