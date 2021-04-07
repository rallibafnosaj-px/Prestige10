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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class OSAActivity extends AppCompatActivity {

    SQLiteDatabase sqlDB;
    Cursor cursor;
    TextView txtItemName, txtMaxCapacity, txtNumFacing, txtWeekNo, txtTitle1,
            txtTitle2, txtNumFacing1, txtMaxCapacity2, tvItemName, tvItemCode, txtHomeShelfPcs,
            txtSecondaryShelfPcs;
    EditText edtNumFacing, edtMaxCapacity, edtMaxCapacity2, edtNumFacing1, edtHomeShelfPcs, edtSecondaryShelfPcs;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner spinner;
    Button btnSave;
    String itemCode, itemName, storeLocCode, weekNo, radioVal, categoryName, userCode, spinnerSelectedItem,
            companyCode, flagOSA;
    int facingVal, insert, homeShelf, secShelf;
    String homeShelfPcs, secondShelfPcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        categoryName = sp.getString("categoryNameOSA", null);
        weekNo = sp.getString("weekNoOSA", null);
        storeLocCode = sp.getString("storeLocCode", null);

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
        setContentView(R.layout.osa_layout);
        setTitle("On Shelf Availability");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //txtItemName = (TextView) findViewById(R.id.txtItemName);
        //txtTitle1 = (TextView)findViewById(R.id.tvTitle1);
        //txtTitle2 = (TextView)findViewById(R.id.tvTitle2);
        //txtNumFacing1 = (TextView)findViewById(R.id.txtNumFacing1);
        //txtMaxCapacity2 = (TextView)findViewById(R.id.txtMaxCapacity2);
        //edtMaxCapacity2 = (EditText) findViewById(R.id.edtMaxCapacity2);
        //edtNumFacing1 = (EditText) findViewById(R.id.edtNumFacing1);

        edtSecondaryShelfPcs = (EditText) findViewById(R.id.edtSecondaryShelfPcs);
        edtHomeShelfPcs = (EditText) findViewById(R.id.edtHomeShelfPcs);
        txtSecondaryShelfPcs = (TextView) findViewById(R.id.txtSecondaryShelfPcs);
        txtHomeShelfPcs = (TextView) findViewById(R.id.txtHomeShelfPcs);

        txtNumFacing = (TextView) findViewById(R.id.txtNumFacing);
        txtWeekNo = (TextView) findViewById(R.id.txtWeekNo);
        edtNumFacing = (EditText) findViewById(R.id.edtNumFacing);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioID);
        spinner = (Spinner) findViewById(R.id.spnnrItem);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);
        String week = "Week " + weekNo;
        txtWeekNo.setText(week);
        tvItemName = (TextView) findViewById(R.id.tvItemName);
        tvItemCode = (TextView) findViewById(R.id.tvItemCode);

        // get intent value from adapter
        Intent getIn = getIntent();
        itemName = getIn.getStringExtra("itemNameOSA");
        itemCode = getIn.getStringExtra("itemCodeOSA");

        if (getIntent().hasExtra("itemCategory")) {
            String cat = getIn.getStringExtra("itemCategory") + " " + itemName;
            tvItemName.setText(cat);
            tvItemCode.setText(itemCode);
        } else {
            tvItemName.setText(itemName);
            tvItemCode.setText(itemCode);
        }

        //Toast.makeText(this, itemName, Toast.LENGTH_SHORT).show();
        edtMaxCapacity = (EditText) findViewById(R.id.edtMaxCapacity);
        txtMaxCapacity = (TextView) findViewById(R.id.txtMaxCapacity);
        //txtItemName.setText(itemName);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbOnShelf) {
                    //txtNumFacing1.setVisibility(View.VISIBLE);
                    //txtMaxCapacity2.setVisibility(View.VISIBLE);
                    //edtNumFacing1.setVisibility(View.VISIBLE);
                    //edtMaxCapacity2.setVisibility(View.VISIBLE);
                    //txtTitle1.setVisibility(View.VISIBLE);
                    //txtTitle2.setVisibility(View.VISIBLE);
                    txtNumFacing.setVisibility(View.VISIBLE);
                    edtNumFacing.setVisibility(View.VISIBLE);

                    txtMaxCapacity.setVisibility(View.VISIBLE);
                    edtMaxCapacity.setVisibility(View.VISIBLE);

                    txtHomeShelfPcs.setVisibility(View.VISIBLE);
                    edtHomeShelfPcs.setVisibility(View.VISIBLE);

                    txtSecondaryShelfPcs.setVisibility(View.VISIBLE);
                    edtSecondaryShelfPcs.setVisibility(View.VISIBLE);

                    btnSave.setVisibility(View.VISIBLE);
                    flagOSA = "onshelf";
                    radioVal = "On Shelf";
                } else if (checkedId == R.id.rbOutOfStock) {
                    flagOSA = "outofstock";


                    txtMaxCapacity.setVisibility(View.GONE);
                    edtMaxCapacity.setVisibility(View.GONE);
                    edtMaxCapacity.setText("0");

                    txtNumFacing.setVisibility(View.GONE);
                    edtNumFacing.setVisibility(View.GONE);
                    edtNumFacing.setText("0");

                    txtHomeShelfPcs.setVisibility(View.GONE);
                    edtHomeShelfPcs.setVisibility(View.GONE);
                    edtHomeShelfPcs.setText("0");

                    txtSecondaryShelfPcs.setVisibility(View.GONE);
                    edtSecondaryShelfPcs.setVisibility(View.GONE);
                    edtSecondaryShelfPcs.setText("0");

                    btnSave.setVisibility(View.VISIBLE);
                    radioVal = "Out of Stock";

                } else if (checkedId == R.id.rbNotCarried) {

                    flagOSA = "notcarried";

                    edtMaxCapacity.setVisibility(View.GONE);
                    txtMaxCapacity.setVisibility(View.GONE);
                    edtMaxCapacity.setText("0");

                    txtNumFacing.setVisibility(View.GONE);
                    edtNumFacing.setVisibility(View.GONE);
                    edtNumFacing.setText("0");

                    txtHomeShelfPcs.setVisibility(View.GONE);
                    edtHomeShelfPcs.setVisibility(View.GONE);
                    edtHomeShelfPcs.setText("0");

                    txtSecondaryShelfPcs.setVisibility(View.GONE);
                    edtSecondaryShelfPcs.setVisibility(View.GONE);
                    edtSecondaryShelfPcs.setText("0");

                    btnSave.setVisibility(View.VISIBLE);
                    radioVal = "Not Carried";

                } else if (radioVal.isEmpty()) {
                    radioVal = "";
                }
            }
        });


        /*
        //Open Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        cursor = sqlDB.rawQuery("SELECT itemCode, itemName FROM assignment WHERE userCode=?", new String[]{userCode});

        boolean update = false;

        if(cursor.moveToFirst())
        {
            do {
                if (cursor.getString(0).equals(itemCode))
                {
                    update = true;
                    //Toast.makeText(this, "Update TRUE", Toast.LENGTH_LONG).show();
                }
                if(update)
                {
                    sqlDB.execSQL("UPDATE assignment SET tag='1' WHERE itemCode='"+cursor.getString(0)+"'");
                    //Toast.makeText(this, cursor.getString(0), Toast.LENGTH_LONG).show();
                }

            } while(cursor.moveToNext());
        }*/

        //SPINNER ITEMS
        /*
        spinnerLoad();
*/
        String sItemCode = tvItemCode.getText().toString().trim();

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT " +
                        "osa, " +
                        "facing " +
                        "FROM osa " +
                        "WHERE itemCode=?;",
                new String[]{sItemCode}); //storeCode});
        if (cursor.getCount() != 0) {
            insert = 0;
            if (cursor.moveToLast()) {
                edtNumFacing.setText(cursor.getString(cursor.getColumnIndex("facing")));
            }
        } else {
            insert = 1;
        }
        cursor.close();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final String sFacing = edtNumFacing.getText().toString().trim();
                    final String sMaxCap = edtMaxCapacity.getText().toString().trim();
                    final String sHomeShelf = edtHomeShelfPcs.getText().toString().trim();
                    final String sSecShelf = edtSecondaryShelfPcs.getText().toString().trim();

                    if (sHomeShelf.equalsIgnoreCase("")) {
                        edtHomeShelfPcs.setText("0");
                    }
                    if (sSecShelf.equalsIgnoreCase("")) {
                        edtSecondaryShelfPcs.setText("0");
                    }

                    if (radioVal.equals("On Shelf")) {
                        if (sMaxCap.equals("") ||
                                sMaxCap.equals("0") ||
                                sMaxCap.equals("00") ||
                                sMaxCap.equals("000") ||
                                sMaxCap.equals("0000") ||
                                sMaxCap.equals("00000") ||
                                sMaxCap.equals("000000")) {

                            edtMaxCapacity.requestFocus();
                            edtMaxCapacity.setError("Max Capacity is Required.");

                        } else {
                            saveOSA();
                        }

                    } else {
                        saveOSA();
                    }
                } catch (Exception e) {
                    Log.i("TAG", "OSA Error: " + e);
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void saveOSA() {
        String sItemCode = tvItemCode.getText().toString().trim();
        int maxCap = Integer.parseInt(edtMaxCapacity.getText().toString());

        switch (insert) {
            // test if max capacity and facing is null
            case 0:
                //if(radioVal.equals("On Shelf") && edtNumFacing.getText().toString().equalsIgnoreCase(""))
                if (radioVal.equals("On Shelf") && edtMaxCapacity.getText().toString().equalsIgnoreCase("")) {
                    String error = "Required field";
                    edtMaxCapacity.setError(error);
                    if (maxCap == 0) {
                        edtMaxCapacity.setError("Max Capacity cannot be 0");
                    }
                } else if (radioVal.equals("Out of Stock")) {

                    edtNumFacing.setText("0");
                    edtMaxCapacity.setText("0");
                    edtHomeShelfPcs.setText("0");
                    edtSecondaryShelfPcs.setText("0");
                    edtMaxCapacity.setVisibility(View.GONE);

                    updateOSA();
                    Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                } else if (radioVal.equals("Not Carried")) {

                    edtNumFacing.setText("0");
                    edtMaxCapacity.setText("0");
                    edtHomeShelfPcs.setText("0");
                    edtSecondaryShelfPcs.setText("0");
                    edtMaxCapacity.setVisibility(View.GONE);

                    updateOSA();
                    Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    // if on shelf save goes here too
                    updateOSA();
                    Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                }
                break;

            case 1:
                if (sItemCode.equals("No id")) {
                    Toast.makeText(OSAActivity.this, "No item.", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioVal.equals("On Shelf") && edtMaxCapacity.getText().toString().equalsIgnoreCase("")) {
                        String error = "Required field";
                        edtMaxCapacity.setError(error);
                        if (maxCap == 0 || edtMaxCapacity.getText().toString().equalsIgnoreCase("")) {
                            edtMaxCapacity.setError("Max Capacity cannot be 0 or empty");
                        }
                    } else if (radioVal.equals("Out of Stock")) {
                        edtNumFacing.setText("0");
                        edtMaxCapacity.setText("0");
                        edtHomeShelfPcs.setText("0");
                        edtSecondaryShelfPcs.setText("0");
                        edtMaxCapacity.setVisibility(View.GONE);
                        insertOSA();
                        Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    } else if (radioVal.equals("Not Carried")) {
                        edtNumFacing.setText("0");
                        edtMaxCapacity.setText("0");
                        edtHomeShelfPcs.setText("0");
                        edtSecondaryShelfPcs.setText("0");
                        edtMaxCapacity.setVisibility(View.GONE);
                        insertOSA();
                        Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        insertOSA();
                        Toast.makeText(OSAActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
        }
    }

    /*private void spinnerLoad()
    {
        getAllSpinnerContent();

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
    }

    public void getAllSpinnerContent(){
        cursor = sqlDB.rawQuery("SELECT DISTINCT itemCode, itemName " +
                "FROM assignment " +
                "WHERE categoryName=? " +
                "AND userCode=? " +
                //"AND itemCode=?"+
                "AND tag='1'", new String[]{categoryName, userCode});

        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();

        if(cursor.getCount()!=0)
        {
            //spinner.setSelected();
            if(cursor.moveToFirst()){
                do{

                    spinnerContent.add(new SpinnerKeyValue(cursor.getString(1), cursor.getString(1)));
                    //spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
                }while(cursor.moveToNext());
            }
        }
        else
        {
            spinnerContent.add(new SpinnerKeyValue("No id", "No item"));
        }
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(OSAActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerContent);
        spinner.setAdapter(adapter);

    }*/

    private void insertOSA() {
        String carriedNotCarried = "";
        final String sItemName = tvItemName.getText().toString().trim();
        final String sItemCode = tvItemCode.getText().toString().trim();

        if (radioVal.equals("Not Carried")) {
            carriedNotCarried = "Not Carried";
        } else {
            carriedNotCarried = "Carried";
        }

        if (edtNumFacing.getText().toString().trim().equals("")) {
            facingVal = 0;
        } else {
            facingVal = Integer.valueOf(edtNumFacing.getText().toString().trim());
        }

        if (edtMaxCapacity.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Shelf max capacity cannot be empty.", Toast.LENGTH_LONG).show();
        }

        int maxCap = Integer.parseInt(edtMaxCapacity.getText().toString().trim());
        homeShelfPcs = edtHomeShelfPcs.getText().toString().trim();
        secondShelfPcs = edtSecondaryShelfPcs.getText().toString().trim();
        if (homeShelfPcs.equalsIgnoreCase("")) {
            homeShelfPcs = "0";
        }
        if (secondShelfPcs.equalsIgnoreCase("")) {
            secondShelfPcs = "0";
        }

        final String maxCapStr = String.valueOf(maxCap);

        AlertDialog.Builder b = new AlertDialog.Builder(OSAActivity.this);

        if (radioVal.equalsIgnoreCase("Not Carried") ||
                radioVal.equalsIgnoreCase("Out of Stock")) {
            b.setTitle("Review OSA Details");
            b.setMessage("Week No.: " + weekNo + "\n" +
                    "Item: " + sItemName + "\n" +
                    //"Loc Code: "+storeLocCode+"\n"+
                    "OSA: " + radioVal + "\n" +
                    "Availability: " + carriedNotCarried + "\n" +
                    "Facing: 0\n" +
                    "Max Caps: \n" +
                    "Home Shelf: 0\n" +
                    "Second Shelf: 0");
        } else {
            b.setTitle("Review OSA Details");
            b.setMessage("Week No.: " + weekNo + "\n" +
                    "Item: " + sItemName + "\n" +
                    //"Loc Code: "+storeLocCode+"\n"+
                    "OSA: " + radioVal + "\n" +
                    "Availability: " + carriedNotCarried + "\n" +
                    "Facing: " + facingVal + "\n" +
                    "Max Caps: " + maxCapStr + "\n" +
                    "Home Shelf: " + homeShelfPcs + "\n" +
                    "Second Shelf: " + secondShelfPcs);
        }

        b.setNegativeButton("CANCEL", null);
        b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                //IF NO RECORD
                sqlDB.execSQL("INSERT INTO osa" +
                        "(itemCode, " +
                        "itemName, " +
                        "storeCode, " +
                        "osa, " +
                        "facing, " +
                        "weekNo, " +
                        "maxCapacity, " +
                        "userCode, " +
                        "syncStatus," +
                        "homeShelfPcs," +
                        "secondShelfPcs) " +
                        "VALUES" +
                        "('" + sItemCode + "'," +
                        "'" + sItemName + "'," +
                        "'" + storeLocCode + "'," +
                        "'" + radioVal + "'," +
                        "'" + facingVal + "'," +
                        "'" + weekNo + "'," +
                        "'" + maxCapStr + "'," +
                        "'" + userCode + "'," +
                        "'not sync'," +
                        "'" + homeShelfPcs + "'," +
                        "'" + secondShelfPcs + "')");

                // set facing and maxcap to 0
                edtNumFacing.setText("0");
                edtMaxCapacity.setText("0");

                sqlDB.execSQL("UPDATE assignment SET tag='0' WHERE itemCode='" + sItemCode + "'");
                //Toast.makeText(this, spinnerSelectedItem+"\n"+storeLocCode+"\n"+radioVal+"\n"+facingVal+"\n"+weekNo+"\n"+userCode, Toast.LENGTH_SHORT).show();
                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //spinnerLoad();
                                break;
                        }
                    }
                };
                AlertDialog.Builder v = new AlertDialog.Builder(OSAActivity.this);
                v.setTitle("OSA Details");
                /*v.setMessage(
                        "Week No.: "+weekNo+"\n"+
                                //"Store: "+storeLocCode+"\n"+
                                "Item name: "+spinnerSelectedItem+"\n"+
                                "Facing: "+facingVal+"\n"+
                                "Max Capacity: "+maxCap+"\n"+
                                "Availability: "+carriedNotCarried);*/
                v.setTitle("Process completed");
                v.setMessage("Saved!");
                v.setCancelable(true);
                v.setPositiveButton("Ok", null);
                v.show();
            }
        });
        b.show();
    }

    private void updateOSA() {
        String carriedNotCarried = "";
        int maxCap = Integer.parseInt(edtMaxCapacity.getText().toString().trim());

        final String maxCapStr = String.valueOf(maxCap);
        String sItemName = tvItemName.getText().toString().trim();
        final String sItemCode = tvItemCode.getText().toString().trim();

        try {
            if (edtNumFacing.getText().toString().trim().equals("")) {
                facingVal = 0;
            } else {
                facingVal = Integer.valueOf(edtNumFacing.getText().toString().trim());
            }

            if (edtHomeShelfPcs.getText().toString().trim().equalsIgnoreCase("")) {
                homeShelf = Integer.valueOf(edtHomeShelfPcs.getText().toString().trim());
            }

            if (edtSecondaryShelfPcs.getText().toString().trim().equalsIgnoreCase("")) {
                secShelf = Integer.valueOf(edtSecondaryShelfPcs.getText().toString().trim());
            }

            //RECORD EXIST
            sqlDB.execSQL("UPDATE osa SET " +
                    "osa='" + radioVal + "', " +
                    "itemName='" + sItemName + "', " +
                    "maxCapacity='" + maxCapStr + "', " +
                    "facing = " + facingVal + ", " +
                    "weekNo = '" + weekNo + "'," +
                    "homeShelfPcs = '" + homeShelf + "'," +
                    "secondShelfPcs = '" + secShelf + "' " +
                    "WHERE itemCode='" + sItemCode + "' " +
                    "AND storeCode='" + storeLocCode + "';");

            sqlDB.execSQL("UPDATE assignment SET tag='0' WHERE itemCode='" + sItemCode + "'");

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //spinnerLoad();
                            break;
                    }
                }
            };

            AlertDialog.Builder mValid = new AlertDialog.Builder(OSAActivity.this);
            mValid.setTitle("Process completed");
            mValid.setMessage("Saved!");
            mValid.setCancelable(true);
            mValid.setPositiveButton("Ok", ok);
            mValid.show();

        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sqlDB.execSQL("UPDATE assignment SET tag='0'");
        sqlDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}