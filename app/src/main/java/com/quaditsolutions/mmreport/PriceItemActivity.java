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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class PriceItemActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerViewAdapterItem_price sAdapter;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    TextView txtItemName, txtWeekNo, txtPreviousPrice, tvWeekNoPriceSurvey;
    String itemCode, itemName, storeCode, weekNo = "1", companyCode, userCode, TAG = "Response", currentIPAddress;
    Button btnAdd, btnSendSurvey;
    TextView txtItems;
    SoapPrimitive resultPriceItemRequest, resultPriceSurvey;
    GlobalVar gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        weekNo = sp.getString("weekNo", null);
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
        setContentView(R.layout.price_item_layout);

        checkWeekNo(userCode);

        btnAdd = (Button) findViewById(R.id.btnAdd);

        Intent getIn = getIntent();
        String storeCode = getIn.getStringExtra("storeCode");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("storeCodePrice", storeCode);
        editor.apply();

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        //sets the query condtion in php
        sAdapter = new RecyclerViewAdapterItem_price(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);
        //getItemData(userCode, storeCode);
        tvWeekNoPriceSurvey = (TextView) findViewById(R.id.tvWeekNoPriceSurvey);
        if (weekNo == null) {
            //tvWeekNoPriceSurvey.setText("Week 1");
        } else {
            tvWeekNoPriceSurvey.setText("Week " + weekNo);
        }

        btnSendSurvey = (Button) findViewById(R.id.btnSendSurvey);
        btnSendSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove this after testing
                //Toast.makeText(PriceItemActivity.this, "Survey Sent!", Toast.LENGTH_SHORT).show();
                sendRequestFeedbackStart();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PriceItemActivity.this, AddItemMaintenance.class);
                startActivity(in);
                finish();
            }
        });

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        cursor = sqlDB.rawQuery("SELECT priceID, " +
                "itemName, " +
                "itemPrice, " +
                "lastPrice " +
                "FROM pricechanged ORDER BY itemName ASC", null);


        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                //    txtPreviousPrice.setText(cursor.getString(1));

                do {
                    GlobalVar globalVar = new GlobalVar();
                    globalVar.pcItemID = cursor.getString(0);
                    globalVar.pcItemName = cursor.getString(1);
                    globalVar.pcLastPrice2 = cursor.getDouble(2);
                    GetDataAdapter1.add(globalVar);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } else {
            Toast.makeText(this, "No Item Found", Toast.LENGTH_LONG).show();
            //     txtPreviousPrice.setText("0");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void checkWeekNo(String userCode) {

        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        Cursor cursor = sqlDB.rawQuery("SELECT weekNo FROM headerprice WHERE weekNo=" +
                "(strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1 " +
                "AND userCode=?", new String[]{userCode});
        if (cursor.getCount() == 0 || cursor.equals(null)) {
            alertDialog();
        } else {
            if (cursor.moveToFirst()) {

                SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("weekNo", cursor.getString(0));
                editor.apply();
                String priceWeekNo = sp.getString(weekNo, null);
                // tvWeekNoPriceSurvey.setText("Week Number: "+priceWeekNo);
                // tvWeekNoPriceSurvey.setText(weekNo);
            }
        }

        sqlDB.close();

    }

    private void alertDialog() {
        DialogInterface.OnClickListener recon = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startInventory();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                }
            }
        };
        AlertDialog.Builder mExit = new AlertDialog.Builder(this);
        mExit.setTitle("Start Price Survey");
        mExit.setMessage("Do you want to start a Price Survey for this week?");
        mExit.setCancelable(false);
        mExit.setPositiveButton("Start", recon);
        mExit.setNegativeButton("Cancel", recon);
        mExit.show();
    }

    private void startInventory() {
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        Cursor cursor = sqlDB.rawQuery("SELECT (strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1" +
                " AS weekNo FROM users", null);

        if (cursor.moveToFirst()) {
            try {
                sqlDB.execSQL("INSERT INTO headerprice" +
                        "(weekNo, status, sentDate, userCode) " +
                        "VALUES('" +
                        cursor.getString(0) +
                        "', 'open', " +
                        "date('now')," +
                        "'" + userCode + "')");
                checkWeekNo(userCode);
            } catch (Exception e) {
                //Toast.makeText(this,e+ "", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<GlobalVar> filteredModelList = filter(GetDataAdapter1, newText);
                sAdapter.setFilter(filteredModelList);
                return false;
            }

            private List<GlobalVar> filter(List<GlobalVar> models, String query) {
                query = query.toLowerCase();
                final List<GlobalVar> filteredModelList = new ArrayList<>();
                for (GlobalVar model : models) {
                    final String text = model.pcItemName.toLowerCase();
                    if (text.contains(query)) {
                        filteredModelList.add(model);
                    }
                }
                return filteredModelList;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Check in/out
    private class sendPriceItemClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog.setTitle("Sending");
            progressDialog.setMessage("Please wait . . .");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(TAG, "doInBackground");
            PriceSurvey();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Log.i(TAG, "onPostExecute-" + resultCheckInOut.toString());
            progressDialog.dismiss();
            try {
                if (resultPriceSurvey.toString().equalsIgnoreCase("Success")) {
                    Log.i(TAG, "Price Survey Result: " + resultPriceSurvey);
                    AlertDialog.Builder b = new AlertDialog.Builder(PriceItemActivity.this);
                    b.setTitle("Process Complete");
                    b.setMessage("Survey Successfully Sent!");
                    b.setPositiveButton("OK", null);
                    b.show();
                } else {
                    Log.i(TAG, "Failed to send!");
                }
            } catch (Exception e) {
                Log.d(TAG + " - All", e.toString());
            }
        }
    }

    // part of ksoap passing data to online db
    private void sendRequestFeedbackStart() {
        try {
            sendPriceItemClass ps = new sendPriceItemClass();
            ps.execute();
        } catch (Exception e) {
            Log.i(TAG, "Error in sending price survey: " + e);
        }
    }

    private void PriceSurvey() {

        String SOAP_ACTION = "http://tempuri.org/insertPriceSurvey";
        String METHOD_NAME = "insertPriceSurvey";
        String NAMESPACE = "http://tempuri.org/";

        // priceID, itemName, itemPrice, lastPrice, weekNo, storeLocation, dateAdded

        cursor = sqlDB.rawQuery("SELECT priceID, " +
                "itemName, " +
                "itemPrice, " +
                "lastPrice, " +
                "weekNo, " +
                "storeLocation, " +
                "dateAdded, " +
                "userCode " +
                "FROM pricechanged", null);

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {

                    String weekNo, employeeNo, SKU, itemPrice, storeLocCode, lastPrice, itemCode, currentPrice;

                    weekNo = cursor.getString(cursor.getColumnIndex("weekNo"));
                    employeeNo = cursor.getString(cursor.getColumnIndex("userCode"));
                    SKU = cursor.getString(cursor.getColumnIndex("itemName"));
                    itemPrice = cursor.getString(cursor.getColumnIndex("itemPrice"));
                    storeLocCode = cursor.getString(cursor.getColumnIndex("storeLocation"));
                    lastPrice = cursor.getString(cursor.getColumnIndex("lastPrice"));
                    //itemCode    = cursor.getString(cursor.getColumnIndex("weekNo"));
                    //currentPrice  = cursor.getString(cursor.getColumnIndex("userCode"));

                    try {

                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("weekNo", weekNo);
                        Request.addProperty("employeeNo", employeeNo);
                        Request.addProperty("SKU", SKU);
                        Request.addProperty("itemPrice", itemPrice);
                        Request.addProperty("storeLocCode", storeLocCode);
                        if (lastPrice == null) {
                            Request.addProperty("lastPrice", itemPrice);
                        } else {
                            Request.addProperty("lastPrice", lastPrice);
                        }
                        Request.addProperty("itemCode", "code");
                        Request.addProperty("currentPrice", 1);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultPriceSurvey = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result Price Survey: " + resultPriceSurvey);
                    } catch (Exception ex) {
                        Log.i(TAG, "Error Price Survey: " + ex);
                    }
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}