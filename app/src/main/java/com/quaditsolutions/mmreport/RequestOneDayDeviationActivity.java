package com.quaditsolutions.mmreport;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rozz on 24/03/2018.
 */

public class RequestOneDayDeviationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // initialization
    ProgressDialog progressDialog;
    String itemCode, itemName, storeLocCode, weekNo, categoryName, userCode, companyCode,
            sStoreLocation = "", sSelectedDate, sSelectedTimeIn, sSelectedTimeOut, sReason,
            sSpinnerSelectedStore = "", flagDate = "", timeFlag = "", timeOut24HrsVal = "", timeIn24HrsVal = "",
            TAG = "Response", sDeviationReason, currentIPAddress;
    TextView tvRequestDate, tvRequestTimeIn, tvRequestTimeOut;
    Spinner spStoreLocation;
    RadioGroup rgReasionOfDeviation;
    RadioButton rbRequestedByClient, rbRequestedByAgency;
    Button btnSubmitDeviationRequest;
    Cursor cursor;
    GlobalVar gv;
    SQLiteDatabase sqlDB;
    final static int DIALOG_ID = 0;
    int hour_val, minute_val;
    SoapPrimitive sresultDeviationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        categoryName = sp.getString("categoryNameOSA", null);
        weekNo = sp.getString("weekNoOSA", null);
        storeLocCode = sp.getString("storeLocCode", null);
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
        setContentView(R.layout.request_one_day_deviation_layout);
        setTitle("Deviation Request");

        // typecast
        spStoreLocation = (Spinner) findViewById(R.id.spinnerStoreLoc);
        tvRequestDate = (TextView) findViewById(R.id.tvRequestDate);
        tvRequestTimeIn = (TextView) findViewById(R.id.tvRequestTimeIn);
        tvRequestTimeOut = (TextView) findViewById(R.id.tvRequestTimeOut);
        rgReasionOfDeviation = (RadioGroup) findViewById(R.id.radioGroupReasonForDeviation);
        rbRequestedByAgency = (RadioButton) findViewById(R.id.rbRequestedByAgency);
        rbRequestedByClient = (RadioButton) findViewById(R.id.rbRequestedByClient);
        btnSubmitDeviationRequest = (Button) findViewById(R.id.btnSubmitRequestOneDayDeviation);
        progressDialog = new ProgressDialog(this);

        // get spinner content
        getSpinnerStoreContent();

        // spinner action
        spStoreLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                sSpinnerSelectedStore = store.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // submit action
        btnSubmitDeviationRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                initializeValues();

                /*Values to be stored
                *            userCode +"\n"+
                             sStoreLocation + "\n" +
                             sSpinnerSelectedStore + "\n" +
                             sSelectedDate + "\n" +
                             sSelectedTimeIn + "\n" +
                             sSelectedTimeOut + "\n" +
                             sDeviationReason);*/

                // validation
                if (sSelectedDate.equals("Select Date")) {
                    Toast.makeText(RequestOneDayDeviationActivity.this, "Please select request date.", Toast.LENGTH_SHORT).show();
                    tvRequestDate.setError("Required field");
                } else if (sSelectedTimeIn.equals("Select Time In")) {
                    tvRequestTimeIn.setError("Required field");
                    Toast.makeText(RequestOneDayDeviationActivity.this, "Please select time in.", Toast.LENGTH_SHORT).show();
                } else if (sSelectedTimeOut.equals("Select Time Out")) {
                    tvRequestTimeOut.setError("Required field");
                    Toast.makeText(RequestOneDayDeviationActivity.this, "Please select time out.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(RequestOneDayDeviationActivity.this);
                    b.setTitle("Review Deviation Details");
                    b.setMessage(//userCode +"\n"+
                            "Store: " + sStoreLocation + "\n" +
                                    //sSpinnerSelectedStore + "\n" +
                                    "Date: " + sSelectedDate + "\n" +
                                    "Time In: " + sSelectedTimeIn + "\n" +
                                    "Time Out: " + sSelectedTimeOut + "\n" +
                                    "Reason: " + sDeviationReason);

                    b.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRequestDeviation();
                        }
                    });
                    b.setNegativeButton("CANCEL", null);
                    b.show();
                }
            }
        });

        // select date action
        tvRequestDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();

                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(RequestOneDayDeviationActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //dpdDeliveryDate.setMaxDate(Calendar.getInstance());
                flagDate = "flagDateSelectDate";
                dpdDeliveryDate.show(getFragmentManager(), "DatepickerdialogDelivery");
            }
        });

        // select time in action


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showTimePickerDialog();
    }

    // request deviation to portal
    private void sendRequestDeviation() {
        RequestOneDayDeviationActivity.sendRequestFeedbackClass sMsg = new RequestOneDayDeviationActivity.sendRequestFeedbackClass();
        sMsg.execute();
    }

    // class request deviation
    // part of ksoap passing data to online db
    private class sendRequestFeedbackClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog.setTitle("Sending");
            progressDialog.setMessage("Please wait . . .");
            progressDialog.show();
        }

        // part of ksoap passing data to online db
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            sendDeviationRequest();

            return null;
        }

        // part of ksoap passing data to online db
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            progressDialog.dismiss();

            try {
                if (sresultDeviationRequest.toString().equals("Failed")) {
                    String message = "Request failed!";
                    Toast.makeText(RequestOneDayDeviationActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        AlertDialog.Builder s = new AlertDialog.Builder(RequestOneDayDeviationActivity.this);
                        s.setTitle("Process completed");
                        s.setMessage("Deviation request successfully sent. Pending for supervisor approval.");
                        s.setPositiveButton("OK", null);
                        s.show();

                        tvRequestDate.setText("Select Date");
                        tvRequestTimeIn.setText("Select Time In");
                        tvRequestTimeOut.setText("Select Time Out");
                        tvRequestDate.setError(null);
                        tvRequestTimeIn.setError(null);
                        tvRequestTimeOut.setError(null);

                        Log.i(TAG, "Request Successfully Sent");

                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Error" + e, Toast.LENGTH_SHORT).show();

                        if (!"null".equals(e) || !"".equals(e)) {
                            //Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Connection timeout.\nPlease try again later.";
                Toast.makeText(RequestOneDayDeviationActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // deviation request to webserver
    public void sendDeviationRequest() {

        String SOAP_ACTION = "http://tempuri.org/insertDeviation";
        String METHOD_NAME = "insertDeviation";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            initializeValues();

            Request.addProperty("sStoreLocation", sSpinnerSelectedStore);
            Request.addProperty("sSelectedDate", sSelectedDate);
            Request.addProperty("sSelectedTimeIn", timeIn24HrsVal);
            Request.addProperty("sSelectedTimeOut", timeOut24HrsVal);
            Request.addProperty("sDeviationReason", sDeviationReason);
            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);

            sresultDeviationRequest = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Feedback: " + sresultDeviationRequest);
        } catch (Exception ex) {
            Log.e(TAG, "Error Sending Request: " + ex.getMessage());
        }
    }

    public void initializeValues() {
        sStoreLocation = spStoreLocation.getSelectedItem().toString().trim();

        // get string values
        sSelectedDate = tvRequestDate.getText().toString().trim();
        sSelectedTimeIn = tvRequestTimeIn.getText().toString().trim();
        sSelectedTimeOut = tvRequestTimeOut.getText().toString().trim();

        // get selected reason for deviating
        int selectedReasonId = rgReasionOfDeviation.getCheckedRadioButtonId();
        RadioButton rbSelectedReason = (RadioButton) findViewById(selectedReasonId);
        sDeviationReason = rbSelectedReason.getText().toString().trim();
    }

    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {

        String date = year + "-" + parseMonth(monthOfYear) + "-" + parseDay(dayOfMonth);

        if (flagDate == "flagDateSelectDate") {
            tvRequestDate.setText(date);
        } else if (flagDate == "dtExpirationDate") {
            //dtPickEx.setText(date);
        } else {
            //dtPickProd.setText(date);
        }
    }

    private String parseMonth(int month) {
        switch (month) {
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

    private String parseDay(int day) {
        switch (day) {
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

    // spinner content
    public void getSpinnerStoreContent() {
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
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(RequestOneDayDeviationActivity.this,
                R.layout.spinnner_bg, spinnerContent);
        spStoreLocation.setAdapter(adapter);
    }

    // showTimePickerDialog
    public void showTimePickerDialog() {

        // edit text onclick command to show time picker
        tvRequestTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
                timeFlag = "timein";
            }
        });
        tvRequestTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
                timeFlag = "timeout";
            }
        });
    }

    // for time picker
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new TimePickerDialog(RequestOneDayDeviationActivity.this, kTimePickerListener, hour_val, minute_val, false);
        return null;
    }

    // for time picker
    protected TimePickerDialog.OnTimeSetListener kTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_val = hourOfDay;
            minute_val = minute;

            //String timeValue = Integer.toString(hour_val)+":"+Integer.toString(minute_val);
            // determine which edittext to edit
            //Toast.makeText(OvertimeRequestActivity.this, timeValue, Toast.LENGTH_SHORT).show();
            if (timeFlag.equalsIgnoreCase("timein")) {

                timeIn24HrsVal = String.valueOf(hour_val) + ":" + String.valueOf(minute_val);

                String timeSet = "";
                if (hour_val > 12) {
                    hour_val -= 12;
                    timeSet = "PM";
                } else if (hour_val == 0) {
                    hour_val += 12;
                    timeSet = "AM";
                } else if (hour_val == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String min = "";
                String hr = "";
                if (minute_val < 10)
                    min = "0" + minute_val;
                else
                    min = String.valueOf(minute_val);

                if (hour_val < 10)
                    hr = "0" + hour_val;
                else
                    hr = String.valueOf(hour_val);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hr).append(':')
                        .append(min).append(" ").append(timeSet).toString();
                tvRequestTimeIn.setText(aTime);

            } else if (timeFlag.equalsIgnoreCase("timeout")) {

                timeOut24HrsVal = String.valueOf(hour_val) + ":" + String.valueOf(minute_val);

                String timeSet = "";
                if (hour_val > 12) {
                    hour_val -= 12;
                    timeSet = "PM";
                } else if (hour_val == 0) {
                    hour_val += 12;
                    timeSet = "AM";
                } else if (hour_val == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String min = "", hr = "";

                if (minute_val < 10)
                    min = "0" + minute_val;
                else
                    min = String.valueOf(minute_val);
                if (hour_val < 10)
                    hr = "0" + hour_val;
                else
                    hr = String.valueOf(hour_val);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hr).append(':')
                        .append(min).append(" ").append(timeSet).toString();
                tvRequestTimeOut.setText(aTime);

            } else {

            }
        }
    };

    // toolbar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
