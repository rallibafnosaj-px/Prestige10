package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SKUStocksListActivityList extends AppCompatActivity {

    TextView tvDateToday, tvCurrentWeekNo;
    String userCode, companyCode, categoryName;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerViewInventoryItemList recyclerviewList;
    List<GlobalVar> getDataAdapter;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    SharedPreferences sp, spWeekNum;
    GlobalVar gv;
    String dateToday = "Date not Set.", currentWeekNo, weekNo, storeLocCode, storeLocName;
    SwipeRefreshLayout swipeRefreshInventory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        companyColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_item);

        typecast();
        companyDetails();
        setUpRecyclerViewAndToolbar();
        getIntentAndSharedPref();
        setDateTodayWeekNumber();
        getItemQry();
        backButton();
        //computeEndingInventory();
        //textChangeListeners();

    }

    private void companyDetails() {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        categoryName = sp.getString("categoryName", null);
        weekNo = sp.getString("weekNo", null);
        storeLocCode = sp.getString("storeLocCode", null);
        storeLocName = sp.getString("storeLocName", null);
    }

    private void typecast() {
        swipeRefreshInventory = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshInventory);
        swipeRefreshInventory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItemQry();
                swipeRefreshInventory.setRefreshing(false);
            }
        });

    }

    private void setDateTodayWeekNumber() {
        // set date today
        dateToday = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US).format(new Date());
        tvDateToday = (TextView) findViewById(R.id.tvDateToday);
        tvDateToday.setText(dateToday);

        // set week no.
        tvCurrentWeekNo = (TextView) findViewById(R.id.tvWeekNo);
        tvCurrentWeekNo.setText("WEEK " + currentWeekNo);
    }

    private void backButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getItemQry() {
        try {
            //Open database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            // get item with inventory
            cursor = sqlDB.rawQuery("" +
                            "SELECT " +
                            "itemName," +
                            "itemCode," +
                            "categoryName," +
                            "beginningSA," +
                            "beginningWH," +
                            "deliveryInPcs," +
                            "deliveryAdjustment," +
                            "returnsPullOut," +
                            "returnsBO," +
                            "returnsDamaged," +
                            "returnsExpired," +
                            "endingSA," +
                            "endingWH," +
                            "offtake," +
                            "beginningWHPcs," +
                            "caseQty," +
                            "outOfStocks " +
                            "FROM assignment " +
                            "WHERE userCode = ? " +
                            "AND categoryName = ?",
                    new String[]{userCode, categoryName});

            if (cursor.getCount() > 0) {

                AlertDialog.Builder b = new AlertDialog.Builder(SKUStocksListActivityList.this);
                b.setTitle(cursor.getCount() + " Item(s) Found.");
                b.setPositiveButton("OK", null);
                b.show();

                getDataAdapter.clear();

                if (cursor.moveToFirst()) {
                    do {

                        gv = new GlobalVar();

                        gv.invStoreCode = storeLocCode;
                        gv.invStoreName = storeLocName;
                        gv.invUserCode = userCode;
                        gv.invWeekNo = weekNo;
                        gv.invInventoryDate = dateToday;

                        gv.invItemName = cursor.getString(0);
                        gv.invItemCode = cursor.getString(1);
                        gv.invCategoryName = cursor.getString(2);

                        gv.invBegSA = cursor.getString(3);
                        gv.invBegWH = cursor.getString(4);

                        gv.invDelAddPcs = cursor.getString(5);
                        gv.invDelAdj = cursor.getString(6);
                        gv.invRetPullout = cursor.getString(7);
                        gv.invRetBO = cursor.getString(8);
                        gv.invRetDamaged = cursor.getString(9);
                        gv.invRetExp = cursor.getString(10);
                        gv.invEndSA = cursor.getString(11);
                        gv.invEndWH = cursor.getString(12);
                        gv.invOfftake = cursor.getString(13);
                        gv.invBegWHPcs = cursor.getString(14);
                        gv.invCaseQty = cursor.getString(15);
                        gv.invOutOfStocks = cursor.getString(16);

                        getDataAdapter.add(gv);
                        recyclerviewList.notifyDataSetChanged();

                    } while (cursor.moveToNext());
                }
            } else {
                Toast.makeText(this, "No Record Found!", Toast.LENGTH_SHORT).show();
            }

            sqlDB.close();
        } catch (Exception e) {
            Log.i("TAG", "Error in getting item qry: " + e);
        }
    }

    private void getIntentAndSharedPref() {
        Intent getIn = getIntent();
        categoryName = getIn.getStringExtra("categoryName");

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("categoryName", categoryName);
        editor.apply();

        // get week no
        spWeekNum = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        currentWeekNo = sp.getString("weekNo", null);

        // set title to category value
        getSupportActionBar().setTitle(categoryName);
    }

    private void setUpRecyclerViewAndToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getDataAdapter = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerviewList = new RecyclerViewInventoryItemList(getDataAdapter, this);
        recyclerView.setAdapter(recyclerviewList);
    }

    private void companyColor() {
        sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
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
                final List<GlobalVar> filteredModelList = filter(getDataAdapter, newText);
                recyclerviewList.setFilter(filteredModelList);
                return false;
            }

            private List<GlobalVar> filter(List<GlobalVar> models, String query) {
                query = query.toLowerCase();
                final List<GlobalVar> filteredModelList = new ArrayList<>();
                for (GlobalVar model : models) {

                    final String itemName = model.invItemName.toLowerCase();

                    if (itemName.contains(query)) {
                        filteredModelList.add(model);
                    }

                }
                return filteredModelList;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(SKUStocksListActivityList.this, ItemCategoryActivity.class);
        i.putExtra("module", "Inventory");
        finish();
        return super.onOptionsItemSelected(item);
    }
}