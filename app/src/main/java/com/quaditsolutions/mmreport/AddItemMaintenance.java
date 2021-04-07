package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddItemMaintenance extends AppCompatActivity {

    Button btnAdd;
    EditText itemName, etItemPrice;
    SQLiteDatabase sqlDB;
    String item_name, companyCode, userCode, storeLocation, spinnerSelectedItem,weekNo;
    Spinner spinnerStoreLocation;
    Cursor cursor;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);
        weekNo = sp.getString("weekNo", null);

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
        setContentView(R.layout.activity_add_item_maintenance);

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        btnAdd=(Button) findViewById(R.id.btnAdd);
        itemName=(EditText) findViewById(R.id.editTextItemName);
        etItemPrice = (EditText)findViewById(R.id.etItemPrice);
        spinnerStoreLocation = (Spinner)findViewById(R.id.spinner);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemName.getText().toString().isEmpty())
                {
                    itemName.setError("Required field");
                }else if(etItemPrice.getText().toString().isEmpty()){
                    etItemPrice.setError("Required field");
                }
                else
                {
                    try
                    {
                        item_name=itemName.getText().toString();
                        Double itemPrice = Double.parseDouble(etItemPrice.getText().toString());
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.format(itemPrice);

                        sqlDB.execSQL("INSERT INTO pricechanged" +
                                "(itemName,itemPrice, userCode, weekNo,storeLocation) " +
                                "VALUES" +
                                "('" + item_name + "','"+itemPrice+"', '"+userCode+"','"+weekNo+"'," +
                                "'"+spinnerSelectedItem+"')");
                        //Toast.makeText(AddItemMaintenance.this, item_name+" ("+itemPrice+") added successfully!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddItemMaintenance.this, item_name + " Successfully Saved!", Toast.LENGTH_SHORT).show();
                        itemName.setText("");
                        etItemPrice.setText("");
                    }
                    catch(Exception e)
                    {
                        //Toast.makeText(AddItemMaintenance.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spinnerLoad();

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
        {
            Intent in = new Intent(AddItemMaintenance.this, PriceItemActivity.class);
            startActivity(in);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        Intent in = new Intent(AddItemMaintenance.this, PriceItemActivity.class);
        startActivity(in);
    }

    private void spinnerLoad()
    {
        getAllSpinnerContentStoreLocation();

        spinnerStoreLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(DeliveryActivity.this, spinnerStoreLocation
                //        .getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();

                SpinnerKeyValue storeName = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = storeName.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // get LocCode and storeName
    public void getAllSpinnerContentStoreLocation() {
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        // for location name spinner
        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
                //Toast.makeText(this, cursor.getString(0) + " " + cursor.getString(1), Toast.LENGTH_SHORT).show();

            } while (cursor.moveToNext());
        }
        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(
                AddItemMaintenance.this, R.layout.spinnner_bg, spinnerContent);
        spinnerStoreLocation.setAdapter(adapter);
    }

}
