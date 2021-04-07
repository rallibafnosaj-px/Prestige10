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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rozz on 17/03/2018.
 */

public class WeeklyInventoryStatus extends AppCompatActivity {
    // initialization
    String userCode, companyCode, customerCode, currentWeekNo = "";
    Button btnStartInventory, btnEndInventory, btnSKUStocks;
    Spinner spinner;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    TextView tvCurrentWeekNo, tvInventoryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        customerCode = sp.getString("customerCode", null);
        currentWeekNo = sp.getString("weekNo", null);

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
        setContentView(R.layout.weekly_inventory_status_layout);

        btnStartInventory = (Button) findViewById(R.id.btnStartInventory);
        btnEndInventory = (Button) findViewById(R.id.btnEndInventory);
        spinner = (Spinner) findViewById(R.id.spinner_weekly_inventory_store_location);
        btnSKUStocks = (Button) findViewById(R.id.btnSKUStocks);

        btnSKUStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeeklyInventoryStatus.this, SKUStocksListActivityList.class));
                finish();
            }
        });

        tvCurrentWeekNo = (TextView) findViewById(R.id.tvCurrentWeekNo);
        tvCurrentWeekNo.setText("WEEK " + currentWeekNo);

        tvInventoryStatus = (TextView) findViewById(R.id.tvInventoryStatus);

        btnStartInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnStartInventory.setText("PROCEED TO ITEM LIST");
                Intent i = new Intent(WeeklyInventoryStatus.this, ItemCategoryActivity.class);
                i.putExtra("module", "Inventory");
                startActivity(i);

                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                cursor = sqlDB.rawQuery("SELECT invStatus FROM headerinventory WHERE userCode = ? ", new String[]{userCode});
                if (cursor.moveToFirst()) {
                    String invStatusStarted = cursor.getString(0);
                    tvInventoryStatus.setText("Status: Inventory Ongoing");
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                    sqlDB.execSQL("UPDATE headerinventory SET status='not sync', invStatus='Inventory Ongoing' WHERE " +
                            "id= (SELECT MAX(id) FROM headerinventory) AND userCode='" + userCode + "'");
                }
            }
        });

        btnEndInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder postD = new AlertDialog.Builder(WeeklyInventoryStatus.this);
                postD.setTitle("Post Inventory");
                postD.setMessage("Are you sure you want to end this inventory?" +
                        " This process cannot be undone once posted.");
                postD.setPositiveButton("POST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(WeeklyInventoryStatus.this, "Inventory Successfully Posted!", Toast.LENGTH_SHORT).show();
                        String invStats = tvInventoryStatus.getText().toString().trim();
                        //Toast.makeText(WeeklyInventoryStatus.this, invStats, Toast.LENGTH_SHORT).show();
                        if (invStats.equals("Status: No Inventory Started")) {
                            Toast.makeText(WeeklyInventoryStatus.this, "No Ongoing Inventory", Toast.LENGTH_SHORT).show();
                        } else {
                            postInventory();
                        }
                    }
                });
                postD.setNegativeButton("CANCEL", null);
                postD.show();
            }
        });

        checkStoreLocCode();
        checkStatusInventory();
        checkWeekNo(userCode);
        getAllSpinnerContent();

        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void checkStatusInventory() {
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT invStatus FROM headerinventory WHERE userCode=? LIMIT 1", new String[]{userCode});

        if (cursor.moveToFirst()) {
            String invStatus = "Status: " + cursor.getString(0);
            //Toast.makeText(this, invStatus, Toast.LENGTH_SHORT).show();
            //Log.d("inv status" , invStatus);
            //if (invStatus.equalsIgnoreCase("posted")) {
            if (cursor.getString(0) == null) {
                tvInventoryStatus.setText("Status: No Inventory Started");
            } else {
                tvInventoryStatus.setText(invStatus);
            }
            //}
        }
        //
        // Toast.makeText(this, cursor.getString(0), Toast.LENGTH_SHORT).show();
    }

    public void getAllSpinnerContent() {
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(WeeklyInventoryStatus.this,
                R.layout.spinnner_bg, spinnerContent);
        spinner.setAdapter(adapter);
        //spinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.
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

    private void postInventory() {
        if (haveNetworkConnection()) {
            try {
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                sqlDB.execSQL("UPDATE headerinventory SET status='not sync', invStatus='Inventory Posted' WHERE userCode='" + userCode + "'");

                AlertDialog.Builder mValid = new AlertDialog.Builder(WeeklyInventoryStatus.this);
                mValid.setTitle("Process completed");
                mValid.setMessage("Item(s) successfully posted.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
                tvInventoryStatus.setText("Status: Inventory Posted");
                btnStartInventory.setText("START NEW INVENTORY");
            } catch (Exception e) {
//                Toast.makeText(this, e+" err", Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder mValid = new AlertDialog.Builder(WeeklyInventoryStatus.this);
            mValid.setTitle("Process failed");
            mValid.setMessage("Connection timeout.\nPlease check your internet connection.");
            mValid.setCancelable(true);
            mValid.setPositiveButton("Ok", null);
            mValid.show();
        }
    }

    private void checkWeekNo(String userCode) {
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        Cursor cursor = sqlDB.rawQuery("SELECT weekNo FROM headerinventory WHERE weekNo=" +
                "(strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1 " +
                " AND userCode=?", new String[]{userCode});
        if (cursor.getCount() == 0 || cursor.equals(null)) {
            alertDialog();
        } else {
            if (cursor.moveToFirst()) {
                SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("weekNo", cursor.getString(0));
                editor.apply();
            }
        }
    }

    private void alertDialog() {
        DialogInterface.OnClickListener recon = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        tvInventoryStatus.setText("Status: Inventory Started");
                        startInventory();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        startActivity(new Intent(WeeklyInventoryStatus.this, MainActivity.class));
                        finish();
                }
            }
        };
        AlertDialog.Builder mExit = new AlertDialog.Builder(this);
        mExit.setTitle("Start Inventory");
        mExit.setMessage("Do you want to start an Inventory for this week?");
        mExit.setCancelable(false);
        mExit.setPositiveButton("Start", recon);
        mExit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(WeeklyInventoryStatus.this, MainActivity.class));
                finish();
            }
        });
        mExit.show();
    }

    private void startInventory() {
        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        Cursor cursor = sqlDB.rawQuery("SELECT (strftime('%j', date('now', '-3 days', 'weekday 4')) - 1) / 7 + 1" +
                " AS weekNo FROM users", null);

        if (cursor.moveToFirst()) {
            sqlDB.execSQL("INSERT INTO headerinventory" +
                    "(weekNo, status, sentDate, userCode) " +
                    "VALUES('" +
                    cursor.getString(0) +
                    "', 'open', " +
                    "date('now')," +
                    "'" + userCode + "')");
            checkWeekNo(userCode);
        }
    }

    // check if there is stored store location if none user needs to check first
    private void checkStoreLocCode() {
        try {
            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
            String chkStoreLocCode = sp.getString("storeLocCode", null);

            assert chkStoreLocCode != null;
            if (chkStoreLocCode.equals("")) {
                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                startActivity(new Intent(WeeklyInventoryStatus.this, CheckInActivity.class));
                                finish();
                                break;
                        }
                    }
                };

                AlertDialog.Builder mExit = new AlertDialog.Builder(this);
                mExit.setTitle("Access denied");
                mExit.setMessage("Please Time In first to start weekly inventory.");
                mExit.setCancelable(false);
                mExit.setPositiveButton("Ok", ok);
                mExit.show();
            }

        } catch (Exception e) {
            Log.e("Response chkSLC", e.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(WeeklyInventoryStatus.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}
