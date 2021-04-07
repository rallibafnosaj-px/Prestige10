package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity {

    GlobalVar gv;

    SQLiteDatabase sqlDB;
    String userCode, companyCode;

    SoapPrimitive resultUpdatePassword;
    String TAG= "Response", currentIPAddress;

    EditText edtCurrentPass, edtNewPass, edtConfirmPass;
    TextView txtSPCurPass, txtSPNewPass, txtSPConPass;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);

        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        currentIPAddress = sp.getString("ipAddress",null);

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
        setContentView(R.layout.change_password_layout);

        edtCurrentPass = (EditText) findViewById(R.id.edtCurrentPass);
        edtNewPass = (EditText) findViewById(R.id.edtNewPass);
        edtConfirmPass = (EditText) findViewById(R.id.edtConfirmPass);

        txtSPCurPass = (TextView) findViewById(R.id.txtSPCurPass);
        txtSPNewPass = (TextView) findViewById(R.id.txtSPNewPass);
        txtSPConPass = (TextView) findViewById(R.id.txtSPConPass);

        btnSave = (Button) findViewById(R.id.btnSave);

        ShowPasswordsStart();

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                Cursor cursor = sqlDB.rawQuery("SELECT passWord " +
                        "FROM users " +
                        "WHERE userCode='"+userCode+"'", null);

                if(cursor.moveToFirst())
                {
                    if(edtCurrentPass.getText().toString().trim().length()==0)
                    {
                        Toast.makeText(ChangePasswordActivity.this, "Invalid Current Password.",Toast.LENGTH_LONG).show();
                    }
                    else if( edtNewPass.getText().toString().trim().length()==0)
                    {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter your new password.",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(edtCurrentPass.getText().toString().equals(cursor.getString(0)))
                        {
                            if(edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString()))
                            {
                                updatePasswordStart();

                                sqlDB.execSQL("UPDATE users " +
                                        "SET passWord='"+ edtNewPass.getText() +
                                        "' WHERE userCode='"+ userCode +"';");

                                sqlDB.close();

                                Toast.makeText(ChangePasswordActivity.this, "Saved" ,Toast.LENGTH_LONG).show();

                                finish();
                            }
                            else
                            {
                                Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ChangePasswordActivity.this,"Please enter old password.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(ChangePasswordActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    //Update Password
    private class updatePasswordClass extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.i(TAG, "doInBackground");
            updatePassword();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.i(TAG, "onPostExecute" + result);
            try
            {
                if(resultUpdatePassword.toString().equals("Failed"))
                {
                    String message = "Error change password";
                    // Toast.makeText(ChangePasswordActivity.this, message , Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        //Toast.makeText(ChangePasswordActivity.this, "Success update password" , Toast.LENGTH_LONG).show();
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
            catch (Exception e)
            {
                String msg = "Connection timeout.\nPlease try again later.";
                //Toast.makeText(ChangePasswordActivity.this, msg , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updatePassword()
    {
        String SOAP_ACTION = "http://tempuri.org/updatePasswordDataJSON";
        String METHOD_NAME = "updatePasswordDataJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);
            Request.addProperty("password", edtNewPass.getText().toString());


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultUpdatePassword = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Change Password: " + resultUpdatePassword);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    private void updatePasswordStart()
    {
        updatePasswordClass sUpdate = new updatePasswordClass();
        sUpdate.execute();
    }


    private void ShowPasswordsStart()
    {
        txtSPCurPass.setVisibility(View.GONE);
        txtSPNewPass.setVisibility(View.GONE);
        txtSPConPass.setVisibility(View.GONE);

        edtCurrentPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (edtCurrentPass.getText().length() > 0)
                {
                    txtSPCurPass.setVisibility(View.VISIBLE);
                }
                else
                {
                    txtSPCurPass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtSPCurPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtSPCurPass.getText().equals("Show"))
                {
                    txtSPCurPass.setText("Hide");
                    edtCurrentPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtCurrentPass.setSelection(edtCurrentPass.length());
                }
                else
                {
                    txtSPCurPass.setText("Show");
                    edtCurrentPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtCurrentPass.setSelection(edtCurrentPass.length());
                }
            }
        });

        edtNewPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (edtNewPass.getText().length() > 0)
                {
                    txtSPNewPass.setVisibility(View.VISIBLE);
                }
                else
                {
                    txtSPNewPass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtSPNewPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtSPNewPass.getText().equals("Show"))
                {
                    txtSPNewPass.setText("Hide");
                    edtNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtNewPass.setSelection(edtNewPass.length());
                }
                else
                {
                    txtSPNewPass.setText("Show");
                    edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtNewPass.setSelection(edtNewPass.length());
                }
            }
        });

        edtConfirmPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (edtConfirmPass.getText().length() > 0)
                {
                    txtSPConPass.setVisibility(View.VISIBLE);
                }
                else
                {
                    txtSPConPass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtSPConPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtSPConPass.getText().equals("Show"))
                {
                    txtSPConPass.setText("Hide");
                    edtConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtConfirmPass.setSelection(edtConfirmPass.length());
                }
                else
                {
                    txtSPConPass.setText("Show");
                    edtConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtConfirmPass.setSelection(edtConfirmPass.length());
                }
            }
        });
    }
}

