package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class ChangeIPAddressActivity extends AppCompatActivity {

    String publicIP = "http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
    String localIP  = "http://192.168.3.6/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
    String currentIP = "";

    GlobalVar gv;

    SQLiteDatabase sqlDB;
    String userCode, companyCode;

    SoapPrimitive resultUpdatePassword;
    String TAG = "ChangeIPAddress", currentIPAddress;

    EditText edtCurrentPass, edtNewPass, edtConfirmPass;
    TextView txtSPCurPass, txtSPNewPass, txtSPConPass, currentConnection;
    Button btnPublicIP, btnLocalIP;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);

        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
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
        setContentView(R.layout.change_ip_address_layout);

        /*
        AlertDialog.Builder b = new AlertDialog.Builder(ChangeIPAddressActivity.this);
        b.setTitle("Current Address");
        b.setMessage(currentIPAddress);
        b.show();
        b.setPositiveButton("OK", null);
        */

        btnLocalIP = (Button) findViewById(R.id.btnLocalIP);
        btnPublicIP = (Button) findViewById(R.id.btnPublicIP);
        currentConnection = (TextView) findViewById(R.id.tvCurrentConnection);

        if(currentIPAddress.equalsIgnoreCase("http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx")){
            currentConnection.setText("Connected to Public IP");
        }else if(currentIPAddress.equalsIgnoreCase("payroll/PrestigeV3/WebService/Mobile/MobileWebService.asmx")){
            currentConnection.setText("Connected to Local IP");
        }else{
            Toast.makeText(ChangeIPAddressActivity.this, "No IP Address Connected.", Toast.LENGTH_SHORT).show();
        }


        btnLocalIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("ipAddress", localIP);
                editor.apply();
                Log.i("TAG", "IP Address: " + localIP);
                String ip = "Connected to local IP";
                currentConnection.setText(ip);

            }
        });

        btnPublicIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("ipAddress", publicIP);
                editor.apply();
                Log.i("TAG", "IP Address: " + publicIP);
                String ip = "Connected to public IP";
                currentConnection.setText(ip);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(ChangeIPAddressActivity.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}