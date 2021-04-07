package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Rozz on 16/03/2018.
 */

public class FeedbackActivity extends AppCompatActivity {
    // initialization
    EditText etMessage;
    RatingBar ratingBar;
    Button btnSubmitFeedback;
    String userCode, companyCode, storeLocation, TAG = "Response", ratingVal, currentIPAddress;
    SoapPrimitive resultFeedbackRequest;
    GlobalVar gv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // get shared data
        final SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);
        currentIPAddress = sp.getString("ipAddress", null);

        // change color of toolbar depending on employee login
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
        setContentView(R.layout.feedback_layout);
        setTitle("Feedback");

        // typecast
        etMessage = (EditText) findViewById(R.id.etMessage);
        ratingBar = (RatingBar) findViewById(R.id.ratingStar);
        btnSubmitFeedback = (Button) findViewById(R.id.btnSubmitFeedback);

        // submit btn action
        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesVal = etMessage.getText().toString().trim();
                if (mesVal.equals("")) {
                    etMessage.requestFocus();
                    etMessage.setError("Message is Empty");
                    Float r = ratingBar.getRating();
                    ratingVal = r.toString();
                    //Toast.makeText(FeedbackActivity.this, ratingVal, Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(FeedbackActivity.this, "Feedback Sent!\nThank You!", Toast.LENGTH_SHORT).show();
                    Float r = ratingBar.getRating();
                    ratingVal = r.toString();
                    sendRequestFeedbackStart();
                }
            }
        });

        // back button in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //Send Message
    // part of ksoap passing data to online db
    private class sendRequestFeedbackClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        // part of ksoap passing data to online db
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            sendFeedbackRequest();

            return null;
        }

        // part of ksoap passing data to online db
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultFeedbackRequest.toString().equals("Failed")) {
                    String message = "Request failed!";
                    Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String message = "Feedback Successfully Sent!";
                        Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_SHORT).show();

                        Log.i(TAG, "Request Successfully Sent");
                        etMessage.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Error" + e, Toast.LENGTH_SHORT).show();

                        if (!"null".equals(e) || !"".equals(e)) {
                            //Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Connection timeout.\nPlease try again later.";
                Toast.makeText(FeedbackActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // part of ksoap passing data to online db
    public void sendFeedbackRequest() {
        String SOAP_ACTION = "http://tempuri.org/insertFeedback";
        String METHOD_NAME = "insertFeedback";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            //Float r = ratingBar.getRating();
            //String ratingVal = r.toString();
            String sMes = etMessage.getText().toString().trim();
            //Toast.makeText(this, ratingVal, Toast.LENGTH_SHORT).show();

            Request.addProperty("rating", ratingVal);
            Request.addProperty("feedbackMessage", sMes);
            Request.addProperty("employeeNo", userCode);

            //Toast.makeText(this, userCode+" "+ratingVal+" "+sMes, Toast.LENGTH_SHORT).show();

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultFeedbackRequest = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Feedback: " + resultFeedbackRequest);
        } catch (Exception ex) {
            Log.e(TAG, "Error Sending Request: " + ex.getMessage());
        }
    }

    // part of ksoap passing data to online db
    private void sendRequestFeedbackStart() {
        FeedbackActivity.sendRequestFeedbackClass sMessage = new FeedbackActivity.sendRequestFeedbackClass();
        sMessage.execute();
    }

    // toolbar command
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);

    }
}
