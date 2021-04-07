package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// ITEM LIST
public class FreshnessItemList extends AppCompatActivity {

    String userCode, companyCode;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerViewAdapterItem_freshness sAdapter;
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
        setContentView(R.layout.activity_inventory_item);

        setTitle("Item List");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        sAdapter = new RecyclerViewAdapterItem_freshness(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);

        //btnPost = (Button) findViewById(R.id.btnPost);

        Intent getIn = getIntent();
        String categoryName = getIn.getStringExtra("categoryName");

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("categoryName", categoryName);
        editor.apply();

        // set title to category value
        getSupportActionBar().setTitle(categoryName);

        //Open database
        sqlDB=openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        // fix duplicate data itemane here in disctinct
        cursor=sqlDB.rawQuery("" +
                        "SELECT DISTINCT " +
                        "itemCode, " +
                        "itemName " +
                        "FROM assignment " +
                        "WHERE userCode=? " +
                        "AND categoryName=?",
                new String[]{userCode, categoryName});

        if(cursor.getCount()==0){
            Reconnect(userCode, categoryName);
        }else{
            if(cursor.moveToFirst())
            {
                do {
                    GlobalVar gv = new GlobalVar();

                    gv.ItemName = cursor.getString(1);
                    gv.ItemCode=cursor.getString(0);
                    gv.categoryName = categoryName;

                    GetDataAdapter1.add(gv);
                }while(cursor.moveToNext());
                cursor.close();
                sqlDB.close();
            }
        }

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.post_inventory) {
            DialogInterface.OnClickListener post = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int which) {
                    switch(which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            postInventory();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder mValid = new AlertDialog.Builder(FreshnessItemList.this);
            mValid.setTitle("Post Inventory");
            mValid.setMessage("Are you sure you want to post this inventory?");
            mValid.setCancelable(true);
            mValid.setPositiveButton("Yes", post);
            mValid.setNegativeButton("No", post);
            mValid.show();
        }

        if (id==android.R.id.home){
            finish();
        }*/

        startActivity(new Intent(FreshnessItemList.this, ItemCategoryActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void Reconnect(final String userCode, final String categoryName)
    {
        if(haveNetworkConnection())
        {
            getItemData(userCode);
        }
        else
        {
            DialogInterface.OnClickListener recon = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int which) {
                    switch(which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Reconnect(userCode, categoryName);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            finish();
                            break;
                    }
                }
            };
            AlertDialog.Builder mValid = new AlertDialog.Builder(this);
            mValid.setTitle("Process failed");
            mValid.setMessage("No data found. Please use internet connection.");
            mValid.setCancelable(true);
            mValid.setPositiveButton("Reconnect", recon);
            mValid.setNegativeButton("Cancel", recon);
            mValid.show();
        }
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ("WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void getItemData(final String userCode){

        try {
            //Open Or Create Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            cursor = sqlDB.rawQuery("SELECT DISTINCT " +
                    "itemCode, " +
                    "itemName, " +
                    "categoryName " +
                    "FROM assignment " +
                    "WHERE userCode=? ORDER BY assignmentID DESC" +
                    "", new String[]{userCode});

            GetDataAdapter1.clear();

            if(cursor.getCount()==0)
            {
                //continue;
            }
            else
            {
                GlobalVar globalVar = new GlobalVar();
                 if(cursor.moveToFirst())
                 {
                     do
                     {
                         //globalVar = new GlobalVar();
                         globalVar.ItemCode= cursor.getString(0);
                         globalVar.ItemName= cursor.getString(1);
                         globalVar.categoryName = cursor.getString(2);
                         GetDataAdapter1.add(globalVar);

                     }while(cursor.moveToNext());
                 }

                do {
                    if (globalVar.ItemCode.equals(cursor.getString(0)))
                    {
                        sqlDB.execSQL("UPDATE assignment " +
                                "SET tag='0' " +
                                "WHERE itemCode='"+globalVar.ItemCode+"' " +
                                "AND userCode='"+userCode+"';");

                    }
                }while(cursor.moveToNext());
            }

            sqlDB.close();
        }

        catch (Exception e)
        {
            //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
            if(!"null".equals(e) || !"".equals(e)) {
                Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private void postInventory()
    {
        if(haveNetworkConnection())
        {
            try
            {
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                sqlDB.execSQL("UPDATE headerinventory SET status='not sync', invStatus='posted' WHERE userCode='"+userCode+"'");

                AlertDialog.Builder mValid = new AlertDialog.Builder(FreshnessItemList.this);
                mValid.setTitle("Process completed");
                mValid.setMessage("Item(s) successfully posted.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
            catch (Exception e)
            {
                //Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            AlertDialog.Builder mValid = new AlertDialog.Builder(FreshnessItemList.this);
            mValid.setTitle("Process failed");
            mValid.setMessage("Connection timeout.\nPlease check your internet connection.");
            mValid.setCancelable(true);
            mValid.setPositiveButton("Ok", null);
            mValid.show();
        }
    }*/
}