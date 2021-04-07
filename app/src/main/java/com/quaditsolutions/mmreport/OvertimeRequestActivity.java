package com.quaditsolutions.mmreport;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.quaditsolutions.mmreport.CheckInActivity.rotateImage;

/**
 * Created by Rozz on 05/03/2018.
 */

public class OvertimeRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    // initialization

    String userCode, storeLocation, companyCode, storeLocCode, encodedImage="", TAG = "Response",
            timeIn24HrsVal="", timeOut24HrsVal="", currentIPAddress;
    Button btnCamera, btnSaveExpense;
    ImageView imgOvertimeLetter;
    TextView tvFromDateCoveredet, tvFromDateCovered, tvToDateCovered,etTimeIn, etTimeOut,
            etFromDateCovered, etToDateCovered;
    EditText etReasonOfAbsence;

    String flagDate = "", timeFlag="", otLetter="empty",spinnerSelectedItem;
    final static int DIALOG_ID = 0;
    int hour_val, minute_val;

    ProgressDialog progressDialog;

    SoapPrimitive resultOvertimeRequest;
    GlobalVar gv;
    private Uri fileUri;
    private String imageName;
    private ProgressDialog pDialog;
    Spinner spinner, spinnerRequestType;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // test if login as prestige, regris etc.
        // get usercode , companycode , storelocation from sharedpref
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);
        currentIPAddress = sp.getString("ipAddress", null);

        // notify user that overtime is not authorize
        // until prior approval from client
        AlertDialog.Builder ot = new AlertDialog.Builder(this);
        ot.setTitle("Overtime Approval");
        ot.setMessage("Overtime is not authorized WITHOUT client approval.");
        ot.setCancelable(false);
        ot.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ot.show();

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
        setContentView(R.layout.overtime_request_layout);
        setTitle("Overtime Request");

        Permission();

        progressDialog = new ProgressDialog(this);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        imgOvertimeLetter = (ImageView)findViewById(R.id.imgOvertimeLetter);
        btnSaveExpense = (Button)findViewById(R.id.btnSaveExpense);
        // datepicker
        tvFromDateCovered = (TextView)findViewById(R.id.etFromDateCovered);
        tvToDateCovered = (TextView)findViewById(R.id.etToDateCovered);
        // time picker
        etTimeIn = (TextView)findViewById(R.id.etTimeIn);
        etTimeOut = (TextView)findViewById(R.id.etTimeOut);
        spinner = (Spinner)findViewById(R.id.spinner);
        mContext = this;
        spinnerRequestType = (Spinner)findViewById(R.id.spinnerRequestType);

        // submit ot request
        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(OvertimeRequestActivity.this, encodedImage, Toast.LENGTH_SHORT).show();
                String dateIn, dateOut, timeIn, timeOut;
                dateIn = tvFromDateCovered.getText().toString().trim();
                dateOut = tvToDateCovered.getText().toString().trim();
                timeIn = etTimeIn.getText().toString().trim();
                timeOut = etTimeOut.getText().toString().trim();

                // test if no date and time chosen
                if (dateIn.equalsIgnoreCase("Select Date In")) {
                    tvFromDateCovered.setError("Required field");
                    Toast.makeText(OvertimeRequestActivity.this, "Please choose date in.", Toast.LENGTH_SHORT).show();
                }else if (dateOut.equalsIgnoreCase("Select Date Out")) {
                    tvToDateCovered.setError("Required field");
                    Toast.makeText(OvertimeRequestActivity.this, "Please choose date out.", Toast.LENGTH_SHORT).show();
                }else if (timeIn.equalsIgnoreCase("Select Time In")){
                    etTimeIn.setError("Required field");
                    Toast.makeText(OvertimeRequestActivity.this, "Please choose time in.", Toast.LENGTH_SHORT).show();
                }else if(timeOut.equalsIgnoreCase("Select Time Out")) {
                    etTimeOut.setError("Required field");
                    Toast.makeText(OvertimeRequestActivity.this, "Please choose time out.", Toast.LENGTH_SHORT).show();
                }else if (otLetter.equalsIgnoreCase("empty")){
                    AlertDialog.Builder otLetdg = new AlertDialog.Builder(OvertimeRequestActivity.this);
                    otLetdg.setTitle("Overtime Letter Empty");
                    otLetdg.setMessage("Please take a picture of your overtime letter.");
                    otLetdg.setPositiveButton("OK",null);
                    otLetdg.show();
                } else {
                    // get values of date in and out , time in and out
                    AlertDialog.Builder otVal = new AlertDialog.Builder(OvertimeRequestActivity.this);
                    otVal.setTitle("Review Overtime Details");
                    otVal.setMessage(
                                    "Store Location: "+spinnerSelectedItem+"\n"+
                                    "Date In: "+dateIn +"\n"+
                                    "Date Out: "+dateOut+"\n"+
                                    "Time In: "+timeIn+"\n"+
                                    "Time Out: "+timeOut);
                    otVal.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(OvertimeRequestActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();

                            // call method to send ot request

                            sendOvertimeRequest();

                        }
                    });

                    otVal.setNegativeButton("CANCEL", null);
                    otVal.show();
                }
            }
        });

        // open camera
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*fileUri = Uri.fromFile(new File(imageFolderPath, imageName));

                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, 1888);*/
                String imageFolderPath = Environment.getExternalStorageDirectory().toString() + "/.khyrz";
                File imageFolder = new File(imageFolderPath);
                imageFolder.mkdirs();

                imageName = new Date().toString() + ".png";
                fileUri = Uri.fromFile(new File(imageFolderPath, imageName));

                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(openCamera, 1888);
            }
        });

        // date from and to
        tvFromDateCovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate="dtDateFrom";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(OvertimeRequestActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });
        tvToDateCovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDate="dtDateTo";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(OvertimeRequestActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMinDate(Calendar.getInstance());
                dpdDeliveryDate.show(getFragmentManager(), "dpDateFromAbsenceRequest");
            }
        });

        showTimePickerDialog();

        //SPINNER ITEMS
        getAllSpinnerContent();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = store.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // back button arrow
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    public void getAllSpinnerContent() {
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
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(mContext,
                R.layout.spinnner_bg, spinnerContent);
        spinner.setAdapter(adapter);
        //spinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.

        List<String> spinnerContent2 = new ArrayList<>();
        spinnerContent2.add("Overtime");
        spinnerContent2.add("Offset");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(OvertimeRequestActivity.this,
                R.layout.spinnner_bg, spinnerContent2);
        spinnerRequestType.setAdapter(adapter2);

    }

    // method to send request ot to web
    private void sendOvertimeRequest(){
        sendRequestOvertimeClass sMessage = new sendRequestOvertimeClass();
        sMessage.execute();
    }

    // class for online ot request
    private class sendRequestOvertimeClass extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "onPreExecute");
            progressDialog.setTitle("Sending");
            progressDialog.setMessage("Please wait . . .");
            progressDialog.show();
        }
        // part of ksoap passing data to online db
        @Override
        protected Void doInBackground(Void... params)
        {
            Log.i(TAG, "doInBackground");

            sendOvertimeRequestToWebService();

            return null;
        }
        // part of ksoap passing data to online db
        @Override
        protected void onPostExecute(Void result)
        {
            Log.i(TAG, "onPostExecute" + result);

            progressDialog.dismiss();

            try
            {
                if(resultOvertimeRequest.toString().equals("Failed"))
                {
                    String message = "Request failed!";
                    Toast.makeText(OvertimeRequestActivity.this,message,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        //String message = "Overtime Successfully Requested!";
                        AlertDialog.Builder s = new AlertDialog.Builder(OvertimeRequestActivity.this);
                        s.setTitle("Process completed");
                        s.setMessage("Overtime request successfully sent. Pending for supervisor approval.");
                        s.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // pDialog.hide();
                            }
                        });
                        s.show();
                        //Toast.makeText(OvertimeRequestActivity.this,message,Toast.LENGTH_SHORT).show();

                        Log.i(TAG, "Request Successfully Send");
                        tvFromDateCovered.setText("Select Date In");
                        tvToDateCovered.setText("Select Date Out");
                        etTimeIn.setText("Select Time In");
                        etTimeOut.setText("Select Time Out");
                        imgOvertimeLetter.setImageResource(R.drawable.defaultphoto);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();

                        if (!"null".equals(e) || !"".equals(e))
                        {
                            Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                String msg="Connection timeout.\nPlease try again later.";
                Toast.makeText(OvertimeRequestActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    // function for sending data to webservice using ksoap
    public void sendOvertimeRequestToWebService(){

        String SOAP_ACTION = "http://tempuri.org/insertRequestOvertime";
        String METHOD_NAME = "insertRequestOvertime";
        String NAMESPACE = "http://tempuri.org/";

        try
        {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            /*
            keys to my webservice
                employeeNo:
                otIn:
                otOut:
                attachment:
                dateIn:
                dateOut:

            format to pass to webservice
                "employeeNo", "B2"
                "otIn", "20:00"
                "otOut", "20:30"
                "attachment", ""
                "dateIn", "2018-03-13"
                "dateOut", "2018-03-13"
            */

            // asign values to var here to pass to webservice
            String sEmpNo, sOtIn, sOtOut, sAttachment, sDateIn, sDateOut;
            sEmpNo      = userCode;
            sOtIn       = timeIn24HrsVal;
            sOtOut      = timeOut24HrsVal;
            sAttachment = encodedImage;
            sDateIn     = tvFromDateCovered.getText().toString().trim();
            sDateOut    = tvToDateCovered.getText().toString().trim();

            // asign variables to keys here to pass to webservice
            Request.addProperty("employeeNo", sEmpNo);
            Request.addProperty("storeLocCode", spinnerSelectedItem);
            Request.addProperty("otIn", sOtIn);
            Request.addProperty("otOut", sOtOut);
            Request.addProperty("attachment", sAttachment);
            Request.addProperty("dateIn", sDateIn);
            Request.addProperty("dateOut", sDateOut);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultOvertimeRequest = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Overtime Request: " + resultOvertimeRequest);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error Sending Request: " + ex.getMessage());
        }
    }

    // showTimePickerDialog
    public void showTimePickerDialog(){

        // edit text onclick command to show time picker
        etTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
                timeFlag = "timein";
            }
        });
        etTimeOut.setOnClickListener(new View.OnClickListener() {
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
            return new TimePickerDialog(OvertimeRequestActivity.this, kTimePickerListener, hour_val, minute_val, false);
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
                etTimeIn.setText(aTime);

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
                etTimeOut.setText(aTime);

            }else{

            }
        }
    };

    // permission
    private void Permission()
    {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(OvertimeRequestActivity.this,
                    new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA
                            }, 1);
        }
        else
        {}
    }

    // convert image to base64
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {

            try{
                pDialog = new ProgressDialog(OvertimeRequestActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                final Bitmap photo = BitmapFactory.decodeFile(fileUri.getPath(), options);

                ExifInterface ei = new ExifInterface((fileUri.getPath()));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap;  // = null;
                switch (orientation){

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(photo, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(photo, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(photo, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = photo;
                }

                imgOvertimeLetter.setImageBitmap(rotatedBitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                pDialog.hide();
                otLetter = "good";

            }catch (Exception e){
                Log.i("TAG","Error in OT Letter: "+e);
            }
        }
        else
        {
            Toast.makeText(OvertimeRequestActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            otLetter = "empty";
        }
    }

    // back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

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
            tvFromDateCovered.setText(date);
        }
        else if (flagDate=="dtDateTo") {
            tvToDateCovered.setText(date);
        }else{

        }
    }
}
