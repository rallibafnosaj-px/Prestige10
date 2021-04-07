package com.quaditsolutions.mmreport;

import android.app.NotificationManager;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearlyToExpiredList extends AppCompatActivity {

    String userCode, companyCode;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerViewAdapterItem_nearlyExpired sAdapter;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

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
        setContentView(R.layout.activity_nearlyexpired_item);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        sAdapter = new RecyclerViewAdapterItem_nearlyExpired(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);

        //Open database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        // query for nearly expiring item list
        cursor = sqlDB.rawQuery("SELECT DISTINCT " +
                "d.itemCode, " +
                "d.itemName," +
                "d.expirationDate, "+
                "d.deliveryID " +
                "FROM delivery AS d " +
                "INNER JOIN assignment AS a " +
                "WHERE d.userCode=? " +
                "AND d.userCode=a.userCode " +
                "AND d.itemCode=a.itemCode " +
                "AND d.tag='nearToExpire' AND d.listStatus='1'", new String[]{userCode});

        if (cursor.getCount() == 0)
        {
            Toast.makeText(this, "No record(s).", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (cursor.moveToFirst())
            {
                GetDataAdapter1.clear();
                do
                {
                    GlobalVar gv = new GlobalVar();
                    gv.ItemName = cursor.getString(1);
                    gv.ItemCode = cursor.getString(0);
                    gv.rtvExpDate = cursor.getString(2);

                    GetDataAdapter1.add(gv);

                    NotificationManager notificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(cursor.getInt(3));

                }
                while (cursor.moveToNext());
                cursor.close();
                sqlDB.close();
            }
        }

        sqlDB.close();

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                final List<GlobalVar> filteredModelList = filter(GetDataAdapter1, newText);
                sAdapter.setFilter(filteredModelList);
                return false;
            }
            private List<GlobalVar> filter(List<GlobalVar> models, String query)
            {
                query = query.toLowerCase();final List<GlobalVar> filteredModelList = new ArrayList<>();
                for (GlobalVar model : models)
                {
                    final String text = model.ItemName.toLowerCase();
                    if (text.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                }
                return filteredModelList;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}