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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class DTRReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    GlobalVar gv = new GlobalVar();
    SQLiteDatabase sqlDB;
    Cursor cursor;
    List<GlobalVar> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewSyncForReviewDTR sAdapter;
    SoapPrimitive resultDTRReview;
    String TAG = "Respond";
    String userCode, companyCode, currentIPAddress;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView tvTotalDtrFound;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
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
        setContentView(R.layout.dtr_review);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        sAdapter = new RecyclerViewSyncForReviewDTR(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);

        tvTotalDtrFound = (TextView) findViewById(R.id.tvTotalDtrFound);
        progressDialog = new ProgressDialog(DTRReviewActivity.this);

        //checkStoreLocCode();
        //checkWeekNo(userCode);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        getItemData();

        /*
        AlertDialog.Builder b = new AlertDialog.Builder(DTRReviewActivity.this);
        b.setTitle("Current IP Address");
        b.setMessage(currentIPAddress);
        b.show();
        b.setPositiveButton("OK",null);
        */

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void getItemData() {
        try {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("" +
                    "SELECT " +
                    "dtrID, " +
                    "dateIn, " +
                    "dateOut, " +
                    "addressIn, " +
                    "addressOut, " +
                    "storeLocCode, " +
                    "storeName, " +
                    "timeIn, " +
                    "timeOut " +
                    "FROM reviewDtr " +
                    "ORDER BY dtrID DESC", null);

            String totalDTRCount = "Total DTR Found: " + String.valueOf(cursor.getCount());
            tvTotalDtrFound.setText(totalDTRCount);

            if (cursor.getCount() == 0) {

//                Toast.makeText(this, cursor.getCount() + " Record(s) Found", Toast.LENGTH_LONG).show();

                if (haveNetworkConnection(this)) {
                    getOnlineDTRClass d = new getOnlineDTRClass();
                    d.execute();
                } else {

                    AlertDialog.Builder b = new AlertDialog.Builder(DTRReviewActivity.this);
                    b.setTitle("Connection Failed");
                    b.setMessage("Please check your internet connection.");
                    b.setPositiveButton("OK", null);
                    b.show();

                }
            } else {
                if (cursor.moveToFirst()) {

                    GetDataAdapter1.clear();

                    do {
                        GlobalVar globalVar = new GlobalVar();
                        globalVar.rdtrDateIn = cursor.getString(1);
                        globalVar.rdtrDateOut = cursor.getString(2);
                        globalVar.rdtrCurrentLocationIn = cursor.getString(3);
                        globalVar.rdtrCurrentLocationOut = cursor.getString(4);
                        globalVar.rdtrStoreCode = cursor.getString(5);
                        globalVar.rdtrStoreName = cursor.getString(6);
                        globalVar.rdtrTimeIn = cursor.getString(7);
                        globalVar.rdtrTimeOut = cursor.getString(8);

                        GetDataAdapter1.add(globalVar);

                    } while (cursor.moveToNext());

                    sAdapter.notifyDataSetChanged();
                    sqlDB.close();

                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRefresh() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        try {
            if (haveNetworkConnection(this)) {
                progressDialog.dismiss();
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                cursor = sqlDB.rawQuery("DELETE FROM reviewDtr", null);

                if (cursor.getCount() == 0) {
//                    Toast.makeText(this, "List cleaned!", Toast.LENGTH_SHORT).show();

                    sqlDB.close();

                    finish();
                    startActivity(getIntent());

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Internet connection required.", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.i("TAG", "Error in refreshing: " + e);
        }
    }

    // json getOnlineDTR
    private class getOnlineDTRClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            getOnlineDTR();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultDTRReview.toString().equals("Failed")) {

                    AlertDialog.Builder b = new AlertDialog.Builder(DTRReviewActivity.this);
                    b.setTitle("No DTR Available");
                    b.setMessage("Please try again later.");
                    b.setPositiveButton("OK", null);
                    b.show();

                } else {
                    try {

                        JSONArray jArray = new JSONArray(resultDTRReview.toString());
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            gv.rdtrEmployeeNo = json_data.getString("employeeNo");
                            gv.rdtrDateIn = json_data.getString("dtrDateIn");
                            gv.rdtrDateOut = json_data.getString("dtrDateOut");
                            gv.rdtrTimeIn = json_data.getString("timeIn");
                            gv.rdtrTimeOut = json_data.getString("timeOut");
                            gv.rdtrCurrentLocationIn = json_data.getString("addressIn");
                            gv.rdtrCurrentLocationOut = json_data.getString("addressOut");
                            gv.rdtrStoreCode = json_data.getString("storeLocationCode");
                            gv.rdtrStoreName = json_data.getString("storeLocationName");
                            gv.rdtrLongIn = json_data.getString("longIn");
                            gv.rdtrLongOut = json_data.getString("longOut");
                            gv.rdtrLatIn = json_data.getString("latIn");
                            gv.rdtrLatOut = json_data.getString("latOut");

                            sqlDB.execSQL("INSERT INTO reviewDtr" +
                                    "(storeLocCode, " +
                                    "storeName, " +
                                    "timeIn, " +
                                    "timeOut, " +
                                    "dateIn, " +
                                    "dateOut, " +
                                    "locationLatIn, " +
                                    "locationLatOut, " +
                                    "locationLongIn, " +
                                    "locationLongOut, " +
                                    "addressIn, " +
                                    "addressOut) " +
                                    "VALUES" +
                                    "('" + gv.rdtrStoreCode + "', " +
                                    "'" + gv.rdtrStoreName + "', " +
                                    "'" + gv.rdtrTimeIn + "', " +
                                    "'" + gv.rdtrTimeOut + "', " +
                                    "'" + gv.rdtrDateIn + "', " +
                                    "'" + gv.rdtrDateOut + "', " +
                                    "'" + gv.rdtrLatIn + "', " +
                                    "'" + gv.rdtrLatOut + "', " +
                                    "'" + gv.rdtrLongIn + "', " +
                                    "'" + gv.rdtrLongOut + "', " +
                                    "'" + gv.rdtrCurrentLocationIn + "', " +
                                    "'" + gv.rdtrCurrentLocationOut + "');");

                        }
                        sqlDB.close();
                        getItemData();
                        Log.i(TAG, "Success Review DTR");
                    } catch (Exception e) {
                        Log.i(TAG, "Error: " + e);
                    }
                }
            } catch (Exception e) {
                Log.i("TAG", "Error in inserting : " + e);
            }
        }
    }

    // pass
    public void getOnlineDTR() {

        Log.i("TAG", "Current IP: " + currentIPAddress);

        String SOAP_ACTION = "http://tempuri.org/getOnlineDTR";
        String METHOD_NAME = "getOnlineDTR";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultDTRReview = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result DTR Review: " + resultDTRReview);
        } catch (Exception ex) {
            Log.e(TAG, "Error DTR Review: " + ex.getMessage());
        }
    }

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            progressDialog.setTitle("Cleaning List");
            progressDialog.setMessage("Please wait . . .");
            progressDialog.show();

            sqlDB.execSQL("DELETE FROM reviewDtr");
            cursor = sqlDB.rawQuery("" +
                    "SELECT dtrID FROM reviewDtr", null);
            if (cursor.getCount() == 0) {
                progressDialog.dismiss();
                startActivity(new Intent(DTRReviewActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "List was not clean.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                startActivity(new Intent(DTRReviewActivity.this, MainActivity.class));
                finish();
            }

            sqlDB.close();
        } catch (Exception e) {
            Log.i("TAG", "Error in deleting review dtr table: " + e);
        }

        return super.onOptionsItemSelected(item);
    }

}
