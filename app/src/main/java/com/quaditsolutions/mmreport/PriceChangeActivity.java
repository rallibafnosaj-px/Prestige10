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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class PriceChangeActivity extends AppCompatActivity {
    SQLiteDatabase sqlDB;
    Cursor cursor;
    TextView txtItemName, txtWeekNo, txtPreviousPrice;
    EditText tvSKU;
    Button btnSave, btnDelete;
    String itemID, itemName, userCode, storeCode, weekNo,lastPrice,currentPrice, companyCode;
    Double lastPrice2;
    EditText edtCurrentPricce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeCode = sp.getString("storeCode", null);
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
        setContentView(R.layout.price_change_layout);

        txtItemName = (TextView) findViewById(R.id.txtSKU);
        txtWeekNo = (TextView) findViewById(R.id.txtWeekNo);
        txtPreviousPrice = (TextView) findViewById(R.id.txtPreviousPrice);
        edtCurrentPricce=(EditText) findViewById(R.id.edtCurrentPrice);
        tvSKU = (EditText) findViewById(R.id.txtSKU);
        btnSave = (Button) findViewById(R.id.btnSave);

        if (getIntent().hasExtra("itemID") &&
                getIntent().hasExtra("itemName") &&
                getIntent().hasExtra("lastPrice")){
            Log.d("TAG","Get Intent Extra Itemname, price, lastprice");
            Intent g = getIntent();
            itemID = g.getStringExtra("itemID");
            itemName = g.getStringExtra("itemName");
            lastPrice2 = g.getDoubleExtra("lastPrice",0.00);
        }

        /*
        Intent getIn = getIntent();
        itemID = getIn.getStringExtra("itemID");
        itemName = getIn.getStringExtra("itemName");
        lastPrice2 = getIn.getDoubleExtra("lastPrice",0.00);
        */

        txtItemName.setText(itemName);
        txtWeekNo.setText(weekNo);
        //lastPrice=lastPrice2.toString();
        DecimalFormat df = new DecimalFormat("0.00");
        txtPreviousPrice.setText(String.valueOf(df.format(lastPrice2)));
        btnDelete = (Button)findViewById(R.id.btnDelete);
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder d = new AlertDialog.Builder(PriceChangeActivity.this);
                d.setTitle("Confirm delete");
                d.setMessage("Are you sure you want to delete?");
                d.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                        sqlDB.execSQL("DELETE FROM pricechanged WHERE priceID = '"+itemID+"'");
                        startActivity(new Intent(PriceChangeActivity.this, PriceItemActivity.class));
                        Toast.makeText(PriceChangeActivity.this, "Item Successfully Deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("NO",null);
                d.show();
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtCurrentPricce.getText().toString().isEmpty())
                {
                    edtCurrentPricce.setError("Required field");
                }else if(tvSKU.getText().toString().isEmpty()){
                    tvSKU.setError("Required field");
                }
                else
                {
                    try
                    {
                        //    if(lastPrice.equals("null") || lastPrice.equals("") || lastPrice.equals("NULL") || lastPrice.equals("Null") ){
                        //        lastPrice="0.0";
                        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                        currentPrice=edtCurrentPricce.getText().toString();
                        //Toast.makeText(PriceChangeActivity.this,currentPrice+ "Value ng Current Price", Toast.LENGTH_LONG).show();
                        sqlDB.execSQL("UPDATE pricechanged SET " +
                                "itemPrice= '"+currentPrice+"',lastPrice='"+txtPreviousPrice.getText().toString()+"', itemName= '"+tvSKU.getText().toString().trim()+"', " +
                                "weekNo= '"+weekNo+"' " +
                                "WHERE priceID='"+itemID+"'");
                        //Toast.makeText(PriceChangeActivity.this, "Current Price= "+currentPrice+" and "+"Week NO= "+weekNo+" has been successfully Updated "+lastPrice, Toast.LENGTH_SHORT).show();

                        Intent in = new Intent(PriceChangeActivity.this, PriceItemActivity.class);
                        startActivity(in);
                        finish();
                        //      }

                    }
                    catch(Exception e)
                    {
                        Toast.makeText(PriceChangeActivity.this, "Item not saved.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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
            Intent in = new Intent(PriceChangeActivity.this, PriceItemActivity.class);
            startActivity(in);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent in = new Intent(PriceChangeActivity.this, PriceItemActivity.class);
        startActivity(in);
        finish();
    }
}
