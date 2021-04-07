package com.quaditsolutions.mmreport;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Rozz on 30/07/2018.
 */

public class CreateNewSchedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    String userCode, companyCode, storeLocation, workingDay="", timeIn24HrsVal="", timeOut24HrsVal="", Inser="";
    TextView tvDays;
    Spinner spinnerStoreList;
    Cursor cursor, cursor2, cursor3;
    SQLiteDatabase sqlDB;
    Context mContext;
    Button btnSetScheduleRequest,btnSetAsDayOff;
    TextView txtStartTimeRequest, txtEndTimeRequest;
    String flagDate = "", timeFlag="", spinnerSelectedItem;
    final static int DIALOG_ID = 0;
    int hour_val, minute_val;

    @Override
    public void onCreate(Bundle savedInstanceState){
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);

        switch (companyCode)
        {
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
        setContentView(R.layout.create_new_schedule_layout);

        Intent getIn = getIntent();
        workingDay = getIn.getStringExtra("workingDays");

        tvDays = (TextView)findViewById(R.id.tvDay);
        tvDays.setText(workingDay);

        btnSetScheduleRequest = (Button)findViewById(R.id.btnSetScheduleRequest);
        String btnTitle = "Save " + workingDay + " Schedule";
        btnSetScheduleRequest.setText(btnTitle);

        btnSetAsDayOff = (Button)findViewById(R.id.btnSetAsDayOff);
        btnSetAsDayOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(CreateNewSchedule.this);
                b.setTitle("Set Day Off");
                b.setMessage("Are you sure you want to set "+workingDay+" as day off?");
                b.setNegativeButton("CANCEL",null);
                b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try{

                            String strRest = "Rest Day";

                            cursor = sqlDB.rawQuery("SELECT startTime, " +
                                    "endTime, " +
                                    "storeName, " +
                                    "storeLocCode " +
                                    "FROM scheduleTemp " +
                                    "WHERE storeLocCode='"+strRest+"' AND workingDays=?", new String[]{workingDay});

                            if (cursor.getCount() != 0)
                            {
                                Toast.makeText(CreateNewSchedule.this, workingDay+" already set as day off.", Toast.LENGTH_LONG).show();
                            }else{
                                sqlDB.execSQL("INSERT INTO scheduleTemp " +
                                        "(workingDays, "                  +
                                        "storeLocCode, "                  +
                                        "storeName, "                     +
                                        "startTime, "                     +
                                        "endTime) "                       +
                                        "VALUES "                         +
                                        "('"   + workingDay +
                                        "', '" + strRest +
                                        "', '" + strRest +
                                        "', '" + strRest +
                                        "', '" + strRest +"');");

                                Toast.makeText(CreateNewSchedule.this, "Day Off Saved!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(CreateNewSchedule.this, "Day Off Not Saved. Try again.", Toast.LENGTH_SHORT).show();
                            Log.i("TAG","Error inserting day off: "+e);
                        }
                    }
                });
                b.show();
            }
        });

        btnSetScheduleRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sTimeIn = txtStartTimeRequest.getText().toString();
                final String sTimeOut = txtEndTimeRequest.getText().toString();

                if (sTimeIn.equalsIgnoreCase("Select Time In")){
                    Toast.makeText(CreateNewSchedule.this, "Time in cannot be empty!", Toast.LENGTH_SHORT).show();
                }else if(sTimeOut.equalsIgnoreCase("Select Time Out")){
                    Toast.makeText(CreateNewSchedule.this, "Time out cannot be empty!", Toast.LENGTH_SHORT).show();
                }else{

                    final String sOtIn, sOtOut;
                    sOtIn  = timeIn24HrsVal;
                    sOtOut = timeOut24HrsVal;

                    AlertDialog.Builder b = new AlertDialog.Builder(CreateNewSchedule.this);
                    b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Insert Work-Schedule
                            try{

                                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                                cursor = sqlDB.rawQuery("SELECT workingDays, " +
                                        "startTime, " +
                                        "endTime, " +
                                        "storeName, " +
                                        "storeLocCode " +
                                        " FROM scheduleTemp " +
                                        "WHERE storeLocCode = '"+spinnerSelectedItem+"' AND startTime = '"+timeIn24HrsVal+"' AND endTime = '"+timeOut24HrsVal+"' AND workingDays=?", new String[]{workingDay});

                                if (cursor.getCount() != 0) // update schedule
                                {
                                    if (cursor.moveToFirst())
                                    {
                                        Log.i("TAG", "Working Days: "+workingDay);
                                        Toast.makeText(CreateNewSchedule.this,"Schedule Already Exist.", Toast.LENGTH_LONG).show();
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
                                            "('"   + workingDay  +
                                            "', '" + sOtIn +
                                            "', '" + sOtOut +
                                            "', '" + spinnerStoreList.getSelectedItem().toString() +
                                            "', '" + spinnerSelectedItem +"');");

                                    Log.i("TAG","Time In: "+sOtIn + "TimeOut: "+sOtOut);

                                    Toast.makeText(CreateNewSchedule.this,workingDay + " Schedule Added.", Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception e)
                            { Log.i("TAG", "Error in inserting new schedule: "+e); }
                        }
                    });
                    b.setNegativeButton("CANCEL",null);
                    b.setTitle("Review Details");
                    try {
                        b.setMessage("Store: "+spinnerStoreList.getSelectedItem().toString()+"\n"+
                                "Time In: "+txtStartTimeRequest.getText().toString()+"\n"+
                                "Time Out: "+txtEndTimeRequest.getText().toString());
                    }catch(Exception e){
                        b.setMessage("Please complete information");
                        Log.i("TAG","Error "+e);
                    }
                    b.show();
                }
            }
        });

        spinnerStoreList = (Spinner)findViewById(R.id.spinnerStoreList);
        mContext = this;

        getSpinnerStoreList();

        spinnerStoreList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = store.getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        txtStartTimeRequest = (TextView)findViewById(R.id.txtStartTimeRequest);
        txtEndTimeRequest = (TextView)findViewById(R.id.txtEndTimeRequest);

        // date from and to
        txtStartTimeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate="dtDateFrom";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(CreateNewSchedule.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });
        txtEndTimeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate="dtDateTo";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(CreateNewSchedule.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });

        showTimePickerDialog();

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    public void getSpinnerStoreList(){

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(mContext, R.layout.spinnner_bg, spinnerContent);
        spinnerStoreList.setAdapter(adapter);

    }

    // showTimePickerDialog
    public void showTimePickerDialog(){

        // edit text onclick command to show time picker
        txtStartTimeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
                timeFlag = "timein";
            }
        });
        txtEndTimeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
                timeFlag = "timeout";
            }
        });
    }

    // for time picker
    @Override
    protected Dialog onCreateDialog(int id){
        if (id==DIALOG_ID)
            return new TimePickerDialog(CreateNewSchedule.this, kTimePickerListener, hour_val, minute_val, false);
        return null;
    }

    // for time picker
    protected TimePickerDialog.OnTimeSetListener kTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_val = hourOfDay;
            minute_val = minute;
            Log.i("TAG","hour val: "+hour_val+" minute val: "+minute_val);

            //String timeValue = Integer.toString(hour_val)+":"+Integer.toString(minute_val);
            // determine which edittext to edit
            //Toast.makeText(OvertimeRequestActivity.this, timeValue, Toast.LENGTH_SHORT).show();
            if (timeFlag.equalsIgnoreCase("timein")){

                timeIn24HrsVal = String.valueOf(hour_val) + ":" + String.valueOf(minute_val);

                String timeSet = "";
                if (hour_val > 12) {
                    hour_val -= 12;
                    timeSet = "PM";
                } else if (hour_val == 0) {
                    hour_val += 12;
                    timeSet = "AM";
                } else if (hour_val == 12){
                    timeSet = "PM";
                }else{
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

            }else if (timeFlag.equalsIgnoreCase("timeout")){

                timeOut24HrsVal = String.valueOf(hour_val) + ":" + String.valueOf(minute_val);

                String timeSet = "";
                if (hour_val > 12) {
                    hour_val -= 12;
                    timeSet = "PM";
                } else if (hour_val == 0) {
                    hour_val += 12;
                    timeSet = "AM";
                } else if (hour_val == 12){
                    timeSet = "PM";
                }else{
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

            }else{

            }
        }
    };

    // for datepicker
    private String parseMonth(int month) {
        switch(month) {
            case 0:
                return "01";
            case 1:
                return "02";
            case 2:
                return "03";
            case 3:
                return "04";
            case 4:
                return "05";
            case 5:
                return "06";
            case 6:
                return "07";
            case 7:
                return "08";
            case 8:
                return "09";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "12";
        }
        return null;
    }

    // for datepicker
    private String parseDay(int day) {
        switch(day) {
            case 0:
                return "00";
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
        }
        return String.valueOf(day);
    }

    // for datepicker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = year + "/" + parseMonth(monthOfYear) + "/" + parseDay(dayOfMonth);
        String date = year + "-" + parseMonth(monthOfYear) + "-" + parseDay(dayOfMonth);
        String day = parseDay(dayOfMonth);
        String currentYear = Integer.toString(year);
        String month = parseMonth(monthOfYear);

        if (flagDate=="dtDateFrom") {
            txtStartTimeRequest.setText(date);
        }
        else if (flagDate=="dtDateTo") {
            txtEndTimeRequest.setText(date);
        }else{

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}