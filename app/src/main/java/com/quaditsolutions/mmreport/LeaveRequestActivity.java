
package com.quaditsolutions.mmreport;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rozz on 04/03/2018.
 */

public class LeaveRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // initialization
    String TAG = "Response", userCode, currentIPAddress;
    TextView tvFromDateCoveredet, etFromDateCovered, etToDateCovered;
    EditText etDays, etTotalHours, etReasonOfAbsence;
    String flagDate = "";
    Button btnSubmitRequestOfAbsence;
    RadioButton rbRequestedByClient, rbRequestedByEmployer, rbRequestedByOutlet, rbRequestedByAgency, rbRequestedByDayOff;
    RadioGroup rgRequest;
    GlobalVar gv;
    SoapPrimitive resultAbsenceRequest;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        String companyCode = sp.getString("companyCode", null);
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
        setContentView(R.layout.leave_request_form_layout);
        setTitle("Period of Absence");

        // notify user that they need internet connection when filling absence
        AlertDialog.Builder builder = new AlertDialog.Builder(LeaveRequestActivity.this);
        builder.setTitle("Information");
        builder.setMessage("Please make sure you are connected to the internet before sending your request.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        // typecast
        etFromDateCovered = (TextView) findViewById(R.id.etFromDateCovered);
        etToDateCovered = (TextView) findViewById(R.id.etToDateCovered);

        etDays = (EditText) findViewById(R.id.etDays);
        etTotalHours = (EditText) findViewById(R.id.etTotalHours);
        etReasonOfAbsence = (EditText) findViewById(R.id.etReasonOfAbsence);

        btnSubmitRequestOfAbsence = (Button) findViewById(R.id.btnSubmitRequestOfAbsence);
        progressDialog = new ProgressDialog(this);

        /*
        rbRequestedByClient = (RadioButton)findViewById(R.id.rbRequestdByClient);
        rbRequestedByEmployer = (RadioButton)findViewById(R.id.rbRequestByEmployer);
        rbRequestedByOutlet = (RadioButton)findViewById(R.id.rbRequestByOutlet);
        rbRequestedByAgency = (RadioButton)findViewById(R.id.rbRequestByAgency);
        rbRequestedByDayOff = (RadioButton)findViewById(R.id.rbRequestDayOff);
        rgRequest = (RadioGroup)findViewById(R.id.radioGroupTypeOfAbsenses);
        */

        btnSubmitRequestOfAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valFrom = etFromDateCovered.getText().toString().trim();
                String valTo = etToDateCovered.getText().toString().trim();
                //String x = etDays.getText().toString().trim();
                String valDays = etDays.getText().toString().trim();
                String valTotalHrs = etTotalHours.getText().toString().trim();
                String valReasonOfAbsence = etReasonOfAbsence.getText().toString().trim();

                if (valFrom.equals("") || valTo.equals("") || valDays.equals("") || valTotalHrs.equals("")) {
                    Toast.makeText(LeaveRequestActivity.this, "Please complete all information.", Toast.LENGTH_LONG).show();
                    //}else if (rgRequest.getCheckedRadioButtonId()==-1){
                    //    Toast.makeText(LeaveRequestActivity.this, "Please select type of absence.", Toast.LENGTH_SHORT).show();
                } else if (etFromDateCovered.getText().toString().trim().equalsIgnoreCase("Select Date")) {
                    Toast.makeText(LeaveRequestActivity.this, "Please select date from", Toast.LENGTH_LONG).show();
                    etFromDateCovered.setError("Required field");
                } else if (etToDateCovered.getText().toString().trim().equalsIgnoreCase("Select Date")) {
                    Toast.makeText(LeaveRequestActivity.this, "Please select date to.", Toast.LENGTH_SHORT).show();
                    etToDateCovered.setError("Required field");
                } else if (valReasonOfAbsence.equals("")) {
                    Toast.makeText(LeaveRequestActivity.this, "Reason of absence is empty.", Toast.LENGTH_SHORT).show();
                    etReasonOfAbsence.requestFocus();
                } else {

                    // show dialog box for user to review input data before sending to webservice
                    AlertDialog.Builder builder = new AlertDialog.Builder(LeaveRequestActivity.this);
                    builder.setTitle("Please Review Details Below");
                    builder.setMessage(
                            "From: " + valFrom + "\n" +
                                    "To: " + valTo + "\n" +
                                    "Day(s): " + valDays + " day(s)\n" +
                                    "Total Hours: " + valTotalHrs + " hrs.\n" +
                                    "Reason: " + valReasonOfAbsence);
                    builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Show location settings when the user acknowledges the alert dialog

                            sendRequestAbsenceStart();

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Dialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                }
            }
        });

        etFromDateCovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate = "dtDateFrom";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(LeaveRequestActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });

        etToDateCovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate = "dtDateTo";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(LeaveRequestActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });

        // set totoal hours automatically base on number of day(s)
        etDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String sTotalHrs = etDays.getText().toString().trim();
                    String sTotalDays = etTotalHours.getText().toString().trim();

                    if (sTotalHrs.equals("")) {
                        etTotalHours.setText("0");
                    } else {
                        Double x = Double.parseDouble(sTotalHrs) * 24;
                        String tHrs = x.longValue() == x ? "" + x.longValue() : "" + x;
                        etTotalHours.setText(tHrs);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTotalHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    /*String sTotalHrs = etDays.getText().toString().trim();
                    String sTotalDays = etTotalHours.getText().toString().trim();

                    if (sTotalHrs.equals("")){
                        etDays.setText("0");
                    }else{
                        if (sTotalDays.equals("")){
                            etDays.setText("0");
                        }else{
                            int x = Integer.parseInt(sTotalDays) / 24;
                            etDays.setText(String.valueOf(x));
                        }
                    }*/
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //Send Message
    // part of ksoap passing data to online db
    private class sendRequestAbsenceClass extends AsyncTask<Void, Void, Void> {
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

            sendAbsenceRequest();

            return null;
        }

        // part of ksoap passing data to online db
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            progressDialog.dismiss();

            try {
                if (resultAbsenceRequest.toString().equals("Failed")) {
                    String message = "Request failed!";
                    Toast.makeText(LeaveRequestActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        AlertDialog.Builder s = new AlertDialog.Builder(LeaveRequestActivity.this);
                        s.setTitle("Process completed");
                        s.setMessage("Absence request successfully sent. Pending for supervisor approval.");
                        s.setPositiveButton("OK", null);
                        s.show();

                        etFromDateCovered.setText("Select Date");
                        etToDateCovered.setText("Select Date");
                        etDays.setText("");
                        etTotalHours.setText("");
                        etReasonOfAbsence.setText("");

                        Log.i(TAG, "Request Successfully Send");
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Error" + e, Toast.LENGTH_SHORT).show();

                        if (!"null".equals(e) || !"".equals(e)) {
                            //Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Connection timeout.\nPlease try again later.";
                Toast.makeText(LeaveRequestActivity.this, msg, Toast.LENGTH_SHORT).show();

            }
        }
    }

    // part of ksoap passing data to online db
    public void sendAbsenceRequest() {
        String SOAP_ACTION = "http://tempuri.org/insertRequestAbsent";
        String METHOD_NAME = "insertRequestAbsent";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            String valOfDays = etDays.getText().toString();
            String valOfHours = etTotalHours.getText().toString();

            //int selectedRadioButtonID = rgRequest.getCheckedRadioButtonId();
            //RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
            //String selectedRadioButtonText = selectedRadioButton.getText().toString();
            //String selectedRadioButtonText = "";

            Request.addProperty("dateFrom", etFromDateCovered.getText().toString());
            Request.addProperty("dateTo", etToDateCovered.getText().toString());
            Request.addProperty("days", valOfDays);
            Request.addProperty("hours", valOfHours);
            //Request.addProperty("typeOfAbsence",selectedRadioButtonText);
            Request.addProperty("reason", etReasonOfAbsence.getText().toString());
            Request.addProperty("employeeNo", userCode);
/*
            Request.addProperty("dateFrom", "");
            Request.addProperty("dateTo", "");
            Request.addProperty("days", "");
            Request.addProperty("hours", "");
            //Request.addProperty("typeOfAbsence",selectedRadioButtonText);
            Request.addProperty("reason", "");
            Request.addProperty("employeeNo","");
*/
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultAbsenceRequest = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Absence Request: " + resultAbsenceRequest);
        } catch (Exception ex) {
            Log.e(TAG, "Error Sending Request: " + ex.getMessage());
        }
    }

    // part of ksoap passing data to online db
    private void sendRequestAbsenceStart() {
        sendRequestAbsenceClass sMessage = new sendRequestAbsenceClass();
        sMessage.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = year + "/" + parseMonth(monthOfYear) + "/" + parseDay(dayOfMonth);
        String date = year + "-" + parseMonth(monthOfYear) + "-" + parseDay(dayOfMonth);
        String day = parseDay(dayOfMonth);
        String currentYear = Integer.toString(year);
        String month = parseMonth(monthOfYear);

        if (flagDate == "dtDateFrom") {
            etFromDateCovered.setText(date);
            //etFromDateCovered.setText(date);
        } else if (flagDate == "dtDateTo") {


            //String eday = parseDay(dayOfMonth);

            //Toast.makeText(this, dateDifference, Toast.LENGTH_SHORT).show();
            etToDateCovered.setText(date);

            String start = etFromDateCovered.getText().toString().trim();
            String end = etToDateCovered.getText().toString().trim();

            /**int finalVal = Integer.parseInt(end) - Integer.parseInt(start);

             String fv = Integer.toString(finalVal);

             //etDays.setText(fv);

             getCountOfDays(start, end);**/
        } else {

        }
    }

    /**
     * public int GetDifference(long start,long end){
     * <p>
     * return  diff;
     * }
     **/

    public String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount + " Days");

    }
}
