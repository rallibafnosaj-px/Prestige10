package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerViewItemCategory sAdapter;
    List<GlobalVar> GetDataAdapter1;

    SQLiteDatabase sqlDB;
    Cursor cursor;

    String userCode, companyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

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
        setContentView(R.layout.items_layout);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        sAdapter = new RecyclerViewItemCategory(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);

        //checkStoreLocCode();
        //checkWeekNo(userCode);
        getItemData(userCode);
        //

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void getItemData(final String userCode) {

        try {
            //Open Or Create Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            GetDataAdapter1.clear();

            cursor = sqlDB.rawQuery("" +
                    "SELECT DISTINCT categoryName " +
                    "FROM assignment " +
                    "WHERE userCode=? " +
                    "ORDER BY categoryName ASC", new String[]{userCode});

            if (cursor.moveToFirst()) {
                do {
                    GlobalVar globalVar = new GlobalVar();
                    globalVar.categoryName = cursor.getString(0);
                    GetDataAdapter1.add(globalVar);
                } while (cursor.moveToNext());
            }

            sAdapter.notifyDataSetChanged();
            sqlDB.close();
        } catch (Exception e) {
            //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
            if (!"null".equals(e) || !"".equals(e)) {
                Toast.makeText(getApplication(), "No Item Category.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(ItemCategoryActivity.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
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
                    final String text = model.categoryName.toLowerCase();
                    if (text.contains(query)) {
                        filteredModelList.add(model);
                    }
                }
                return filteredModelList;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
