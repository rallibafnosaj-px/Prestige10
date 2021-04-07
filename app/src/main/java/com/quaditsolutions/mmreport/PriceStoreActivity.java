package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class PriceStoreActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;

    List<GlobalVar> GetDataAdapter1;

    SQLiteDatabase sqlDB;

    String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerViewadapter = new RecyclerViewAdapterStore_price(GetDataAdapter1, this);
        recyclerView.setAdapter(recyclerViewadapter);

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        getStoreData(userCode);
        checkWeekNo(userCode);
    }

    private void checkWeekNo(String userCode)
    {
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        Cursor cursor = sqlDB.rawQuery("SELECT weekNo FROM headerprice WHERE weekNo=" +
                "(strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1 " +
                "AND userCode=?", new String[]{userCode});
        if(cursor.getCount()==0 || cursor.equals(null))
        {
            alertDialog();
        }
        else
        {
            if(cursor.moveToFirst())
            {
                GlobalVar globalVar = new GlobalVar();
                globalVar.weekNoPrice = cursor.getString(0);
            }
        }
        sqlDB.close();
    }

    private void getStoreData(final String userCode){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, /*GlobalVar.SERVER_ADDRESS +*/ "getStoreData.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    GetDataAdapter1.clear();
                    JSONArray jArray = new JSONArray(response);

                    for(int i=0;i<jArray.length();i++){
                        JSONObject json_data = jArray.getJSONObject(i);
                        GlobalVar globalVar = new GlobalVar();
                        globalVar.storeCodePrice= json_data.getString("storeCode");
                        globalVar.storeNamePrice= json_data.getString("storeName");
                        globalVar.storeLocationPrice= json_data.getString("storeLocation");

                        GetDataAdapter1.add(globalVar);

                        /*sqlDB.execSQL("INSERT INTO stores" +
                                "(storeCode, storeName) " +
                                "VALUES" +
                                "('"+ globalVar.StoreCode +
                                "', '"+globalVar.StoreName+"');");

                        sqlDB.execSQL("INSERT INTO storelocation" +
                                "(storeName, areaName) " +
                                "VALUES" +
                                "('"+ globalVar.StoreName +
                                "', '"+globalVar.StoreLocation+"');");*/
                    }
                    recyclerViewadapter.notifyDataSetChanged();
                    sqlDB.close();
                } catch (JSONException e) {
                    //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                    if(!"null".equals(e) || !"".equals(e)) {
                        Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PriceStoreActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("userCode", userCode);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void alertDialog()
    {
        DialogInterface.OnClickListener recon = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch(which) {
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

    private void startInventory()
    {
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        Cursor cursor = sqlDB.rawQuery("SELECT (strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1" +
                " AS weekNo FROM users", null);

        if(cursor.moveToFirst())
        {
            sqlDB.execSQL("INSERT INTO headerprice" +
                    "(weekNo, status, sentDate, userCode) " +
                    "VALUES('" +
                    cursor.getString(0) +
                    "', 'open', " +
                    "date('now')," +
                    "'"+userCode+"')");
            checkWeekNo(userCode);
        }
    }
}
