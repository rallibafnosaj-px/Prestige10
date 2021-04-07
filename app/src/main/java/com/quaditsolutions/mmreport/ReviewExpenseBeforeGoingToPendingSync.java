package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rozz on 19/04/2018.
 */

public class ReviewExpenseBeforeGoingToPendingSync extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    RequestQueue requestQueue ;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    String userCode, companyCode, storeLocation;

    @Override
    public void onCreate(Bundle savedInstanceState){

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);

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
        setContentView(R.layout.review_expense_layout);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_sync_expense);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(ReviewExpenseBeforeGoingToPendingSync.this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        getItemData(userCode);
        recyclerViewadapter = new RecyclerViewSyncForExpense(GetDataAdapter1,ReviewExpenseBeforeGoingToPendingSync.this);
        recyclerView.setAdapter(recyclerViewadapter);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    public void getItemData(final String userCode){
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        GetDataAdapter1.clear();
        cursor = sqlDB.rawQuery("SELECT date, "+
                "expenseCode, "+
                "expenseType, " +
                "meansOfTransportations, " +
                "amount, " +
                "notes " +
                "FROM expenses " +
                "WHERE employeeCode=? AND " +
                "syncStatus='not sync' ORDER BY expenseID DESC", new String[]{userCode});
        if(cursor.moveToFirst())
        {
            do
            {
                GlobalVar globalVar = new GlobalVar();
                globalVar.expDate = cursor.getString(0);
                globalVar.expCode = cursor.getString(1);
                globalVar.expType = cursor.getString(2);
                globalVar.expMeansOfTransportation = cursor.getString(3);
                globalVar.expAmount = cursor.getString(4);
                globalVar.expNote = cursor.getString(5);

                GetDataAdapter1.add(globalVar);

            }while(cursor.moveToNext());
        }
        sqlDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}