package com.quaditsolutions.mmreport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.LoginFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class LogInActivity extends AppCompatActivity {

    // 203.160.168.60
    // 18.141.216.6
    String publicIP = "http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
    String localIP = "http://192.168.3.6/PrestigeV3/WebService/Mobile/MobileWebService.asmx";

    SQLiteDatabase sqlDB;
    Cursor cursor;
    Boolean doubleBackToExitPressedOnce = false;

    EditText edtEmailAddress, edtPassword, edtContactNumber;
    Button btnLogin;
    CheckBox chkShowPass;

    GlobalVar gv = new GlobalVar();

    SoapPrimitive resultRegister, resultAppVersion, resultUpdateContactNo;
    String TAG = "Respond", userContactNumber, userCode,
            currentIPAddress = "http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx";

    private ProgressDialog progressDialog;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
        }

        setContentView(R.layout.login_layout);

        //prgsDlg = new ProgressDialog(LogInActivity.this);
        //prgsDlg.setMessage("Please wait...");
        //prgsDlg.setCancelable(false);
        //prgsDlg.show();

        /*AppVersion av = new AppVersion();
        av.execute();
        */

        sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);

        chkShowPass = (CheckBox) findViewById(R.id.chkShowPass);
        edtEmailAddress = (EditText) findViewById(R.id.edtEmailAddress);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtContactNumber = (EditText) findViewById(R.id.edtContactNumber);
        userContactNumber = edtContactNumber.getText().toString().trim();
        //TextView tvRegister = (TextView) findViewById(R.id.tvRegister);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);

        progressDialog = new ProgressDialog(LogInActivity.this);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(LogInActivity.this);
                b.setTitle("Information");
                b.setMessage("Please contact your administrator to reset your password.");
                b.setPositiveButton("OK", null);
                b.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //prgsDlg = new ProgressDialog(LogInActivity.this);
                //prgsDlg.setMessage("Please wait...");
                //prgsDlg.setCancelable(false);
                //prgsDlg.show();


                edtEmailAddress.setText(edtEmailAddress.getText().toString().toLowerCase());

                //Open Database
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                AlertDialog.Builder b = new AlertDialog.Builder(LogInActivity.this);
                b.setTitle("Choose connection");
                b.setMessage("Choose 'PUBLIC' if you are using your own data or wifi connection. " +
                        "\n\nChoose 'LOCAL' if you are connected to the office wifi.");
                b.setPositiveButton("PUBLIC", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("ipAddress", publicIP);
                        editor.apply();
                        currentIPAddress = publicIP;

                        Log.i("TAG", "IP Address: " + publicIP);
                        logInValidation();
                    }
                });
                b.setNegativeButton("LOCAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("ipAddress", localIP);
                        editor.apply();
                        currentIPAddress = localIP;

                        Log.i("TAG", "IP Address: " + localIP);
                        logInValidation();
                    }
                });
                b.show();

            }
        });

        // hide keyboard on load
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // hide password
        edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        chkShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String publicIP = "http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
        editor.putString("ipAddress", publicIP);
        editor.apply();

    }

    public void Validation(String message) {
        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alertDialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //prgsDlg.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder mValid = new AlertDialog.Builder(this);
        mValid.setTitle("Login Failed");
        mValid.setMessage(message);
        mValid.setCancelable(true);
        mValid.setPositiveButton("Ok", ok);
        mValid.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double tap to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void logInValidation() {
        String message;
        if (edtEmailAddress.getText().toString().trim().length() == 0) {
            //prgsDlg.dismiss();
            message = "Email is required";
            edtEmailAddress.setError(message);
            edtEmailAddress.requestFocus();
        } else if (edtPassword.getText().toString().trim().length() == 0) {
            //prgsDlg.dismiss();
            message = "Password is required";
            edtPassword.setError(message);
            edtPassword.requestFocus();
        } else if (edtContactNumber.getText().toString().trim().length() == 0) {
            //prgsDlg.dismiss();
            message = "Contact number is required";
            edtContactNumber.setError(message);
            edtContactNumber.requestFocus();
        } else {
            cursor = sqlDB.rawQuery("SELECT " +
                            "userCode, " +
                            "userName, " +
                            "customerCode, " +
                            "passWord, " +
                            "companyCode " +
                            "FROM users " +
                            "WHERE userName=?",
                    new String[]{edtEmailAddress.getText().toString().trim()});

            if (cursor.getCount() == 0) // if no user is login
            {
                if (haveNetworkConnection(this)) {
                    registerUser register = new registerUser();
                    register.execute();
                    cursor.close();
                } else {
                    //prgsDlg.dismiss();
                    message = "Please connect to internet.";
                    Validation(message);
                }
            } else {
                if (cursor.moveToFirst()) {
                    if (edtEmailAddress.getText().toString().equals(cursor.getString(1))
                            && edtPassword.getText().toString().equals(cursor.getString(3))) {

                        if (cursor.getString(3).equals("1234")) {
                            DialogInterface.OnClickListener post = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:

                                            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("userCode", cursor.getString(0));
                                            editor.putString("userName", cursor.getString(1));
                                            editor.putString("customerCode", cursor.getString(2));
                                            editor.putString("companyCode", cursor.getString(4));
                                            editor.putString("storeLocCode", "");
                                            editor.apply();

                                            //prgsDlg.dismiss();

                                            // update contact number upon login
                                            updateContactNo();

                                            Intent in = new Intent(LogInActivity.this, MainActivity.class);
                                            startActivity(in);
                                            finish();

                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder mValid = new AlertDialog.Builder(LogInActivity.this);
                            mValid.setTitle("Warning!");
                            mValid.setMessage("Please change your password for security purposes.");
                            mValid.setCancelable(false);
                            mValid.setPositiveButton("Ok", post);
                            mValid.show();
                        } else {
                            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("userCode", cursor.getString(0));
                            editor.putString("userName", cursor.getString(1));
                            editor.putString("customerCode", cursor.getString(2));
                            editor.putString("companyCode", cursor.getString(4));
                            editor.putString("storeLocCode", "");
                            editor.apply();

                            //prgsDlg.dismiss();

                            Intent in = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                        //Toast.makeText(LogInActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    } else {
                        //prgsDlg.dismiss();
                        message = "Email Address or Password is incorrect.";
                        Validation(message);
                    }
                }
                sqlDB.close();

            }
        }
    }

    private class registerUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait while logging in ...");
            progressDialog.show();

            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            register();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            progressDialog.dismiss();
            Log.i(TAG, "onPostExecute" + result);

            try {
                if (resultRegister.toString().equals("failed")) {
                    String message = "Incorrect email or password.";
                    Validation(message);
                } else {
                    //Open Database
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                    try {
                        JSONArray jArray = new JSONArray(resultRegister.toString());
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject json_data = jArray.getJSONObject(i);
                            gv.firstName = json_data.getString("firstName");
                            gv.middleName = json_data.getString("middleName");
                            gv.lastName = json_data.getString("lastName");
                            gv.email = json_data.getString("email");
                            gv.password = json_data.getString("password");
                            gv.userCode = json_data.getString("employeeNo");
                            gv.customerCode = json_data.getString("customerCode");
                            gv.companyCode = json_data.getString("companyCode");
                        }

                        sqlDB.execSQL("INSERT INTO users" +
                                "(userCode, " +
                                "userName, " +
                                "passWord, " +
                                "firstName, " +
                                "middleName, " +
                                "lastName, " +
                                "customerCode, " +
                                "companyCode) " +
                                "VALUES" +
                                "('" + gv.userCode +
                                "', '" + gv.email +
                                "', '" + gv.password +
                                "', '" + gv.firstName +
                                "', '" + gv.middleName +
                                "', '" + gv.lastName +
                                "', '" + gv.customerCode +
                                "', '" + gv.companyCode + "');");

                        logInValidation();

                        Log.i(TAG, "INSERT INTO users");
                    } catch (JSONException e) {
                        //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                        //prgsDlg.dismiss();
                        if (!"null".equals(e) || !"".equals(e)) {
                            Toast.makeText(getApplication(), "Invalid Username or password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Connection timeout. Please try again later.";
                Validation(msg);
            }
            //Toast.makeText(MainActivity.this, "" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //private PropertyInfo pi1;
    public void register() {
        String SOAP_ACTION = "http://tempuri.org/getUserDataJSONwithPass";
        String METHOD_NAME = "getUserDataJSONwithPass";
        String NAMESPACE = "http://tempuri.org/";






        String emailVal = edtEmailAddress.getText().toString().trim();
        String psw = edtPassword.getText().toString().trim();

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("email", emailVal);
            Request.addProperty("password", psw);


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultRegister = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Register: " + resultRegister);
        } catch (Exception ex) {
            //prgsDlg.dismiss();
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    /*private class AppVersion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            GetAppVer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultAppVersion.toString().equals("Outdated")) {
                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface alertDialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    String url = "http://google.com.ph";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    System.exit(0);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder mValid = new AlertDialog.Builder(LogInActivity.this);
                    mValid.setTitle("Update");
                    mValid.setMessage("A newer version of the app is now available. Please download the update and install it");
                    mValid.setCancelable(false);
                    mValid.setPositiveButton("Download", ok);
                    mValid.setNegativeButton("Cancel", ok);
                    mValid.show();
                }
            } catch (Exception e) {
                String msg = "Connection timeout. Please try again later.";
                Validation(msg);
            }
            //Toast.makeText(MainActivity.this, "" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }
    */
    //private PropertyInfo pi1;
    /*public void GetAppVer() {
        String SOAP_ACTION = "http://tempuri.org/getAppVersion";
        String METHOD_NAME = "getAppVersion";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("appversion", gv.appVersion);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultAppVersion = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result AppVersion: " + resultAppVersion);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        //prgsDlg.dismiss();
    }
    */
    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
                //   Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                haveConnectedMobile = true;
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    // update contact number if successful login
    public void updateContactNo() {
        updateContactNoClass sMessage = new updateContactNoClass();
        sMessage.execute();
    }

    // class for update contact info
    private class updateContactNoClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        // part of ksoap passing data to online db
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            sendContactNoToWebService();

            return null;
        }

        // part of ksoap passing data to online db
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultUpdateContactNo.toString().equals("Failed")) {
                    String message = "Update Contact Number failed!";
                    Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.i(TAG, "Request Successfully Send");
                    } catch (Exception e) {
//                        Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();

                        if (!"null".equals(e) || !"".equals(e)) {
                            Toast.makeText(getApplication(), "Failed to update contact!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Connection timeout. Please try again later.";
                Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ksoap for sending contact number to portal
    public void sendContactNoToWebService() {
        String SOAP_ACTION = "http://tempuri.org/updateContactNo";
        String METHOD_NAME = "updateContactNo";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
            userCode = sp.getString("userCode", null);

            // asign values to var here to pass to webservice
            String sEmpNo = "";
            String cNo = edtContactNumber.getText().toString().trim();
            sEmpNo = userCode;

            // asign variables to keys here to pass to webservice
            Request.addProperty("employeeNo", sEmpNo);
            Request.addProperty("contactNo", cNo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultUpdateContactNo = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Update Contact Request: " + resultUpdateContactNo);
        } catch (Exception ex) {
            Log.e(TAG, "Error Sending Request: " + ex.getMessage());
        }
    }
    // end
}