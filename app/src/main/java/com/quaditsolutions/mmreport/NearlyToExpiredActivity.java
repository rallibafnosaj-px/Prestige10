package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Khyrz on 10/12/2017.
 */

public class NearlyToExpiredActivity extends AppCompatActivity {

    SQLiteDatabase sqlDB;
    Cursor cursor, cursorStore;

    EditText edtRemarks, edtRsNo, edtQty, edtLotNo;
    TextView tvItemRTVExpDate;
    Spinner spinner, spinnerStatus, spinnerStoreLoc;

    Button btnSave,btnSold;

    String itemCode, itemName, userCode, spinnerSelectedItem, spinnerSelectedItem2,
            companyCode,itemRTVExpDate,spinnerSelectedItemStore;

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
        setContentView(R.layout.nearly_to_expired_layout);

        //txtItemName = (TextView) findViewById(R.id.txtItemName);
        edtRemarks = (EditText) findViewById(R.id.edtRemarks);
        edtRsNo = (EditText) findViewById(R.id.edtRsNo);
        edtQty = (EditText) findViewById(R.id.edtQty);
        edtLotNo = (EditText) findViewById(R.id.edtLotNo);
        tvItemRTVExpDate = (TextView)findViewById(R.id.tvItemRTVExpDate);

        spinnerStoreLoc = (Spinner) findViewById(R.id.spinner);
        spinner = (Spinner) findViewById(R.id.spnnrItem);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSold = (Button)findViewById(R.id.btnSold);

        Intent getIn = getIntent();
        itemName = getIn.getStringExtra("itemNameNE");
        itemCode = getIn.getStringExtra("itemCodeNE");
        itemRTVExpDate = getIn.getStringExtra("itemRTVExpDate");

        String expVal = "Expiry Date: " + itemRTVExpDate;
        tvItemRTVExpDate.setText(expVal);
        //txtItemName.setText(itemName);

        //Open Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        cursor = sqlDB.rawQuery("SELECT " +
                "DISTINCT d.itemCode, " +
                "a.itemName " +
                "FROM delivery AS d " +
                "INNER JOIN assignment AS a " +
                "WHERE d.userCode=? " +
                "AND d.userCode=a.userCode " +
                "AND d.itemCode=a.itemCode " +
                "AND d.tag='nearToExpire'", new String[]{userCode});

        boolean update = false;

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(itemCode)) {
                    update = true;
                    //Toast.makeText(this, "Update TRUE", Toast.LENGTH_LONG).show();
                }
                if (update)
                {
                    sqlDB.execSQL("UPDATE delivery SET listtag='1' WHERE itemCode='" + cursor.getString(0) + "'");
                    //Toast.makeText(this, cursor.getString(0), Toast.LENGTH_LONG).show();
                }

            } while (cursor.moveToNext());
        }

        //SPINNER ITEMS

        spinnerLoad();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (edtRemarks.getText().toString().isEmpty())
                {

                }
                else if (edtQty.getText().toString().isEmpty())
                {

                }
                else if(edtRsNo.getText().toString().isEmpty())
                {

                }
                else if (edtLotNo.getText().toString().isEmpty())
                {

                }
                else
                {
                    Date date = new Date();
                    Format formatter;
                    formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    final String dateToday = formatter.format(date);

                    AlertDialog.Builder b = new AlertDialog.Builder(NearlyToExpiredActivity.this);
                    b.setTitle("Review RTV Details");
                    b.setMessage("Store: "+spinnerStoreLoc.getSelectedItem().toString().trim()+"\n"+
                                 "Store Code: " +spinnerSelectedItemStore + "\n"+
                                 "Item name: "+itemName+"\n"+
                                 "Today: "+dateToday+"\n"+
                                 "Exp. Date: "+itemRTVExpDate+"\n"+
                                 "Remarks: "+spinnerSelectedItem2+"\n"+
                                 "Quantity: "+edtQty.getText().toString().trim()+"\n"+
                                 "RTV No.: "+edtRsNo.getText().toString().trim()+"\n"+
                                 "Lot No.: "+edtLotNo.getText().toString().trim()+"\n"+
                                 "Status: "+ edtRemarks.getText().toString().trim());
                    b.setCancelable(true);
                    b.setNegativeButton("Cancel",null);
                    b.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlDB.execSQL("INSERT INTO nearlytoexpired(userCode, " +
                                    "itemCode, " +
                                    "rtvNo, " +
                                    "status, " +
                                    "tag, " +
                                    "listtag, " +
                                    "popupStatus, " +
                                    "remarks, " +
                                    "storeLocCode, " +
                                    "quantity, " +
                                    "lotNo, " +
                                    "expirationDate, " +
                                    "dateRecorded, " +
                                    "syncStatus) " +
                                    "VALUES('"+userCode+"', " +
                                    "'"+itemCode+"', " +
                                    "'"+edtRsNo.getText().toString().trim()+"', " +
                                    "'"+edtRemarks.getText().toString().trim()+"', " +
                                    "'none', " +
                                    "'0', " +
                                    "'0', " +
                                    "'"+spinnerSelectedItem2+"', '"+spinnerSelectedItemStore+"', " +
                                    "'"+edtQty.getText().toString().trim()+"', " +
                                    "'"+edtLotNo.getText().toString().trim()+"'," +
                                    " '"+itemRTVExpDate+"','"+dateToday+"','not sync')");

                            // update static value = expDate, recDate, storeLC,

                            sqlDB.execSQL("UPDATE delivery SET tag='not sync', popupStatus='0', listStatus='0'" +
                                    " WHERE itemCode='"+itemCode+"' AND expirationDate='"+itemRTVExpDate+"'");

                            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int which)
                                {
                                    switch(which)
                                    {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            spinnerLoad();

                                            edtRemarks.setText("");
                                            edtRemarks.requestFocus();
                                            edtQty.setText("");
                                            edtRsNo.setText("");
                                            edtLotNo.setText("");
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder mValid = new AlertDialog.Builder(NearlyToExpiredActivity.this);
                            mValid.setTitle("Process completed");
                            mValid.setMessage("Saved!");
                            mValid.setCancelable(true);
                            mValid.setPositiveButton("Ok", ok);
                            mValid.show();
                        }
                    });
                    b.show();
                }
            }
        });

        btnSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    AlertDialog.Builder b = new AlertDialog.Builder(NearlyToExpiredActivity.this);
                    b.setNegativeButton("CANCEL",null);
                    b.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Date date = new Date();
                            Format formatter;
                            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            final String dateToday = formatter.format(date);

                            sqlDB.execSQL("INSERT INTO nearlytoexpired(userCode, " +
                                    "itemCode, " +
                                    "rtvNo, " +
                                    "status, " +
                                    "tag, " +
                                    "listtag, " +
                                    "popupStatus, " +
                                    "remarks, " +
                                    "storeLocCode, " +
                                    "quantity, " +
                                    "lotNo, " +
                                    "expirationDate, " +
                                    "dateRecorded, " +
                                    "syncStatus) " +
                                    "VALUES('"+userCode+"', " +
                                    "'"+itemCode+"', " +
                                    "'"+edtRsNo.getText().toString().trim()+"', " +
                                    "'"+edtRemarks.getText().toString().trim()+"', " +
                                    "'none', " +
                                    "'0', " +
                                    "'0', " +
                                    "'"+spinnerSelectedItem2+"', '"+spinnerSelectedItemStore+"', " +
                                    "'"+edtQty.getText().toString().trim()+"', " +
                                    "'"+edtLotNo.getText().toString().trim()+"'," +
                                    " '"+itemRTVExpDate+"','"+dateToday+"','not sync')");

                            sqlDB.execSQL("UPDATE delivery SET tag='not sync', popupStatus='0', listStatus='0'" +
                                    " WHERE itemCode='"+itemCode+"' AND expirationDate='"+itemRTVExpDate+"'");

                            Toast.makeText(NearlyToExpiredActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    b.show();
            }
        });

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    private void spinnerLoad()
    {
        getSpinnerValue();
        getAllSpinnerContent();

        spinnerStoreLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItemStore = store.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = store.getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedItem2 = spinnerStatus.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getAllSpinnerContent(){

        cursor = sqlDB.rawQuery("SELECT DISTINCT d.itemCode, a.itemName FROM delivery AS d INNER JOIN assignment AS a " +
                "WHERE d.userCode=? AND d.userCode=a.userCode AND d.itemCode=a.itemCode AND d.tag='nearToExpire' AND listtag='1'", new String[]{userCode});
        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
            }while(cursor.moveToNext());
        }
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(NearlyToExpiredActivity.this,
                R.layout.spinnner_bg, spinnerContent);

        spinner.setAdapter(adapter);

        // For store spinner items
        cursorStore = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});
        ArrayList<SpinnerKeyValue> spinnerContent2 = new ArrayList<>();
        if (cursorStore.moveToFirst()) {
            do {
                spinnerContent2.add(new SpinnerKeyValue(cursorStore.getString(0), cursorStore.getString(1)));
            } while (cursorStore.moveToNext());
        }

        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter2 = new ArrayAdapter<>(NearlyToExpiredActivity.this,
                R.layout.spinnner_bg, spinnerContent2);
        spinnerStoreLoc.setAdapter(adapter2);

    }

    public void getSpinnerValue(){

        List<String> spinnerContent = new ArrayList<>();
        spinnerContent.add("Damaged");
        spinnerContent.add("Rat Bites");
        spinnerContent.add("Expired Product");
        spinnerContent.add("Factory Defect");
        spinnerContent.add("Old Packaging");
        spinnerContent.add("Delisted SKU");
        spinnerContent.add("Stocks with Pest");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(NearlyToExpiredActivity.this,
                R.layout.spinnner_bg, spinnerContent);
        spinnerStatus.setAdapter(adapter2);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sqlDB.execSQL("UPDATE delivery SET listtag='0'");
        sqlDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}