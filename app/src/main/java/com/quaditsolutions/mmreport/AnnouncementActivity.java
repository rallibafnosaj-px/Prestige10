package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Service.START_NOT_STICKY;

/**
 * Created by Rozz on 14/03/2018.
 */

public class AnnouncementActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    GlobalVar gv = new GlobalVar();

    SQLiteDatabase sqlDB;
    Cursor cursor;

    List<GlobalVar> getDataAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAnnouncementAdapter rvAnnouncementList;
    SwipeRefreshLayout mSwipeRefreshLayout;

    String userCode, companyCode, storeLocation, TAG = "TAG", currentIPAddress;

    Date currentTime;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ProgressDialog b;
    SoapPrimitive resultAnnouncement;

    TextView tvAnnouncement;
    ImageView imgAnnouncement;

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
        setContentView(R.layout.recyclerview_for_announcement);

        // typecast
        tvAnnouncement = (TextView) findViewById(R.id.tvAnnouncement);
        imgAnnouncement = (ImageView) findViewById(R.id.imgAnnouncement);

        // get announcement from portal and save to sqlite
        if (haveNetworkConnection(getApplicationContext())) {
            getAnnouncementFromOnlineDatabase();
        }

        currentTime = new Date();
        b = new ProgressDialog(AnnouncementActivity.this);

        getDataAdapter = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_for_announcement);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAnnouncementList = new RecyclerViewAnnouncementAdapter(getDataAdapter, this);
        recyclerView.setAdapter(rvAnnouncementList);

        getItemData();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void getItemData() {
        try {
            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
            //sqlDB.beginTransaction();

            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            cursor = sqlDB.rawQuery("SELECT " +
                    "announceID, " +
                    "announceTitle, " +
                    "announceMessage, " +
                    "announcePostedBy," +
                    "announceDateCreated " +
                    "FROM announcement " +
                    "ORDER BY announceID DESC", null);

            if (cursor.getCount() == 0) {

                Toast.makeText(AnnouncementActivity.this, "No Item Found.", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "Selecting from customerList table return 0");
            } else {
                setTitle("Announcement (" + cursor.getCount() + ")");

                Log.i("TAG", "Selecting from customer list return > 0 rows");

                if (cursor.moveToFirst()) {
                    do {
                        GlobalVar globalVar = new GlobalVar();

                        globalVar.announceID = cursor.getString(0);
                        globalVar.announceTitle = cursor.getString(1);
                        globalVar.announceMessage = cursor.getString(2);
                        globalVar.announcePostedBy = cursor.getString(3);
                        globalVar.announceDateCreated = cursor.getString(4);

                        getDataAdapter.add(globalVar);
                    }
                    while (cursor.moveToNext());

                    rvAnnouncementList.notifyDataSetChanged();

                } else {
                }
            }
            //sqlDB.setTransactionSuccessful();
            //sqlDB.endTransaction();
            //sqlDB.close();
            //   }
            //});
        } catch (Exception e) {
            Log.i("TAG", "Error fetching customer list: " + e);
        }
    }

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
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

    // method for getting announcement to online db
    private void getAnnouncementFromOnlineDatabase() {
        //noAnnouncementAvailable();
        announcementClass aClass = new announcementClass();
        aClass.execute();
    }

    // pass data using ksoap to webservice
    public void passAnnouncementDataToWebService() {

        String SOAP_ACTION = "http://tempuri.org/getAnnouncementJSON";
        String METHOD_NAME = "getAnnouncementJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);
            Log.i("TAG", "User Code: " + userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultAnnouncement = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Announcement: " + resultAnnouncement);

        } catch (Exception ex) {

            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    // class asyntask
    private class announcementClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            passAnnouncementDataToWebService();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            try {
                if (resultAnnouncement.toString().equals("failed")) {
                    Toast.makeText(AnnouncementActivity.this, "No Announcement Found.", Toast.LENGTH_SHORT).show();
                } else {

                    sqlDB.execSQL("DELETE FROM announcement");

                    try {
                        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                        // start transaction
                        sqlDB.beginTransaction();
                        // object
                        JSONArray jArray = new JSONArray(resultAnnouncement.toString());

                        // start loop
                        for (int i = 0; i < jArray.length(); i++) {

                            // values
                            JSONObject json_data = jArray.getJSONObject(i);
                            gv.announceID = json_data.getString("announceID");
                            gv.announceTitle = json_data.getString("title");
                            gv.announceMessage = json_data.getString("message");
                            gv.announcePostedBy = json_data.getString("postedBy");
                            gv.announceDateCreated = json_data.getString("dateCreated");

                            // qry insert
                            try {
                                /*
                                *  "announcement(announceID VARCHAR, " +
                                    "announceTitle VARCHAR, " +
                                    "announceMessage VARCHAR, " +
                                    "announcePostedBy VARCHAR, " +
                                    "announceDateCreated VARCHAR);";
                                * */

                                sqlDB.execSQL("INSERT OR " +
                                        "REPLACE INTO announcement " +
                                        "(announceID, " +
                                        "announceTitle, " +
                                        "announceMessage, " +
                                        "announcePostedBy, " +
                                        "announceDateCreated) " +
                                        "VALUES " +
                                        "('" + gv.announceID +
                                        "', '" + gv.announceTitle +
                                        "', '" + gv.announceMessage +
                                        "', '" + gv.announcePostedBy +
                                        "', '" + gv.announceDateCreated + "');");

                                Log.i("TAG", "Rows affected: " + i + "\n");

                            } catch (Exception e) {
                                Log.i("TAG", "Error in inserting item list data: " + i + "\n" + e);
                            }
                        }

                        // end transaction
                        sqlDB.setTransactionSuccessful();
                        sqlDB.endTransaction();
                        sqlDB.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AnnouncementActivity.this, "loading...", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        Log.i("TAG", "Error in saving announcement: " + e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        Log.i("TAG", "Update Item List: " + e);
                    }
                }
            } catch (Exception e) {

                GlobalVar g = new GlobalVar();
                String numOfAnn = "0";
                g.setNumberOfAnnouncement(numOfAnn);
                noAnnouncementAvailable();

            }
        }
    }

    public void noAnnouncementAvailable() {

        Toast.makeText(this, "No Announcement Found.", Toast.LENGTH_SHORT).show();
        /*AlertDialog.Builder a = new AlertDialog.Builder(AnnouncementActivity.this);
        a.setTitle("No Announcement");
        a.setMessage("No announcement found.\nPlease try again later.");
        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(AnnouncementActivity.this, MainActivity.class));
            }
        });
        a.show();
        prgDlg.hide();
        */
    }

    // toolbar command
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(AnnouncementActivity.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}