package com.quaditsolutions.mmreport;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Khyrz on 10/9/2017.
 */

public class DeliveryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private String spinnerSelectedItem, spinnerSelectedItem2, spinnerSelectedLocation,
            itemName, itemCode, userCode, shelfLife, rangeSL, companyCode, selectedStoreName,
            spinnerSelectedItemCode, categoryName, itemCatName;

    TextView dtPickDeli, dtPickEx, dtPickProd, txtShelfLife, tvUnitOfMeasure, tvItemName,
            tvItemCode, shelfLifeValue;
    EditText edtQty, edtLotNumber, edtBoxCase, edtShelfLife;
    Button btnSave;
    Spinner spinner, spinnerShelfLife, spinnerStoreLocation, spinnerUnitOfMeasure,
            spinnerStoreLocationName;
    String flagDate, store_location, currentIPAddress;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    SQLiteDatabase sqlDB;
    Cursor cursor, cursor2;

    int secDay = 86400;
    int secWeek = 604800;
    int secMonth = 2628000;
    int secYear = 31536000;
    int countSL = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        categoryName = sp.getString("categoryName", null);
        currentIPAddress = sp.getString("ipAddress", null);

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
        setContentView(R.layout.delivery_layout);

        shelfLifeValue = (TextView) findViewById(R.id.shelfLifeValue);
        dtPickDeli = (TextView) findViewById(R.id.dtpickDeli);
        dtPickEx = (TextView) findViewById(R.id.dtpickEx);
        dtPickProd = (TextView) findViewById(R.id.dtpickProd);
        txtShelfLife = (TextView) findViewById(R.id.txtShelfLife);
        edtQty = (EditText) findViewById(R.id.edtQty);
        edtBoxCase = (EditText) findViewById(R.id.edtBoxCase);
        edtLotNumber = (EditText) findViewById(R.id.edtLotNumber);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerShelfLife = (Spinner) findViewById(R.id.spinnerShelfLife);
        spinnerShelfLife.setEnabled(false);
        edtShelfLife = (EditText) findViewById(R.id.edtShelfLife);
        spinnerStoreLocation = (Spinner) findViewById(R.id.spinnerStoreLocation);
        spinnerUnitOfMeasure = (Spinner) findViewById(R.id.spinnerUnitOfMeasure);
        tvUnitOfMeasure = (TextView) findViewById(R.id.tvUnitOfMeasure);
        tvItemName = (TextView) findViewById(R.id.tvItemName);
        tvItemCode = (TextView) findViewById(R.id.tvItemCode);

        btnSave = (Button) findViewById(R.id.btnSave);

        Intent getIn = getIntent();
        itemName = getIn.getStringExtra("itemName");
        itemCode = getIn.getStringExtra("itemCode");

        tvItemCode.setText(itemCode);
        itemCatName = categoryName + " " + itemName;
        tvItemName.setText(itemCatName);

        //tvItemName.setText(itemName);


        // get the list of items and display it in the spinner -> this causes delay
        /*
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT shelfLife FROM assignment WHERE userCode=? AND itemCode=?", new String[]{userCode, itemCode});
        if(cursor.getCount()!=0)
        {
            if(cursor.moveToFirst())
            {
                shelfLife = cursor.getString(0);
                String[] slSplit = shelfLife.split(" ");
                countSL = Integer.parseInt(slSplit[0]);
                rangeSL = slSplit[1];
            }
        }
        else
        {
            txtShelfLife.setVisibility(View.VISIBLE);
            edtShelfLife.setVisibility(View.VISIBLE);
            spinnerShelfLife.setVisibility(View.VISIBLE);
        }
        */
        //end of list

        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);

        //dtPickDeli.setText(dateTime);
        dtPickDeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** int id = v.getId();
                 if (id == R.id.dtpickDeli) {
                 //mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_START_DATE);
                 //mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                 Toast.makeText(DeliveryActivity.this, "Deli", Toast.LENGTH_SHORT).show();
                 } else if (id == R.id.dtpickProd) {
                 //mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_END_DATE);
                 //mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                 Toast.makeText(DeliveryActivity.this, "Prod", Toast.LENGTH_SHORT).show();
                 }**/

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpdDeliveryDate = DatePickerDialog.newInstance(DeliveryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdDeliveryDate.setMaxDate(Calendar.getInstance());
                flagDate = "dtPickDeli";
                dpdDeliveryDate.show(getFragmentManager(), "DatepickerdialogDelivery");
            }
        });

        dtPickProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**int id = v.getId();
                 if (id == R.id.dtpickDeli) {
                 //mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_START_DATE);
                 //mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                 Toast.makeText(DeliveryActivity.this, "Deli", Toast.LENGTH_SHORT).show();
                 } else if (id == R.id.dtpickProd) {
                 //mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_END_DATE);
                 //mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                 Toast.makeText(DeliveryActivity.this, "Prod", Toast.LENGTH_SHORT).show();
                 }**/
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(DeliveryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(Calendar.getInstance());
                flagDate = "dtPickProd";
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        dtPickEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();

                int currentYear, currentMonth, currentDay;
                currentYear = now.get(Calendar.YEAR);
                currentMonth = now.get(Calendar.MONTH) + 1;
                currentDay = now.get(Calendar.DAY_OF_MONTH) + 1;

                DatePickerDialog dpd = DatePickerDialog.newInstance(DeliveryActivity.this,

                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(Calendar.getInstance());
                flagDate = "dtExpirationDate";
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });

        dtPickEx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //computeShelfLife();
            }

            @Override
            public void afterTextChanged(Editable s) {
                computeShelfLife();
            }
        });

        dtPickProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
                // autoExpirationDate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dtPickDeli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //autoExpirationDate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*edtShelfLife.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //autoExpirationDate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        //Open Database
        /* get item from list - cause delay
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        cursor = sqlDB.rawQuery("SELECT DISTINCT itemCode, itemName FROM assignment WHERE userCode=? AND freshness='1'", new String[]{userCode});

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
                    sqlDB.execSQL("UPDATE assignment SET tag='1' WHERE freshness='1' AND itemCode='"+cursor.getString(0)+"'");
                    //Toast.makeText(this, cursor.getString(0), Toast.LENGTH_LONG).show();
                }
            } while(cursor.moveToNext());
        }*/

        //SPINNER ITEMS
        spinnerLoad();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String error = "Required field";

                if (dtPickProd.getText().toString().equals("")) {
                    dtPickProd.setError(error);
                    //edtBoxCase.setError(error);
                } else if (edtQty.getText().toString().equalsIgnoreCase("")) {
                    edtQty.setError(error);
                    //}else if(dtPickDeli.getText().toString().equalsIgnoreCase("")) {
                    //    dtPickDeli.setError("Required field");
                } else if (dtPickProd.getText().toString().equalsIgnoreCase("0000-00-00")) {
                    dtPickProd.setError("Required field");
                } else if (dtPickEx.getText().toString().equalsIgnoreCase("0000-00-00")) {
                    dtPickEx.setError("Required field");
                    //}else if(edtLotNumber.getText().toString().equalsIgnoreCase("")){
                    //    edtLotNumber.setError(error);
                } else {
                    //if(spinnerSelectedItem.equals("No id")){
                    //  Toast.makeText(DeliveryActivity.this, "No item.", Toast.LENGTH_SHORT).show();
                    //}else{
                    insertInventory();
                    //}
                }
            }
        });

        //getAllSpinnerContentStoreLocation();

        /*store_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue2 store = (SpinnerKeyValue2) parent.getSelectedItem();
                spinnerSelectedItem2 = store.getId();
                // Toast.makeText(Expenses.this, spinnerSelectedItem2+" StoreCode", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    int timeStamp(int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (int) (c.getTimeInMillis() / 1000L);
    }

    private void spinnerLoad() {
        getSpinnerValue();
        //getAllSpinnerContent();
        getAllSpinnerContentStoreLocation();

        // spinner for items -> causes delays
        /*
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
        */

        spinnerStoreLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(DeliveryActivity.this, spinnerStoreLocation
                //        .getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();

                SpinnerKeyValue storeCode = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItemCode = storeCode.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> uomList = new ArrayList<>();
        uomList.add("Box");
        uomList.add("Case");
        uomList.add("Piece");
        ArrayAdapter uomAdapter = new ArrayAdapter(DeliveryActivity.this, R.layout.spinnner_bg, uomList);
        spinnerUnitOfMeasure.setAdapter(uomAdapter);

        spinnerUnitOfMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                //spinnerSelectedLocation = store.getId();
                selectedStoreName = spinnerUnitOfMeasure.getSelectedItem().toString();
                if (selectedStoreName.equals("Box")) {
                    tvUnitOfMeasure.setText("Box");
                } else if (selectedStoreName.equals("Case")) {
                    tvUnitOfMeasure.setText("Case");
                } else if (selectedStoreName.equals("Piece")) {
                    tvUnitOfMeasure.setText("Piece");
                } else {
                    tvUnitOfMeasure.setText("");
                }

                try {
                    int valOfQty = Integer.parseInt(edtQty.getText().toString().trim());
                    selectedStoreName = spinnerUnitOfMeasure.getSelectedItem().toString();

                    if (selectedStoreName.equals("Box")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Boxes");
                        } else
                            tvUnitOfMeasure.setText("Box");
                    } else if (selectedStoreName.equals("Case")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Cases");
                        } else
                            tvUnitOfMeasure.setText("Case");
                    } else if (selectedStoreName.equals("Piece")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Pieces");
                        } else
                            tvUnitOfMeasure.setText("Piece");
                    } else {
                        tvUnitOfMeasure.setText("");
                    }
                } catch (Exception e) {
                    Log.i("TAG", "Error in uom change: " + e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerShelfLife.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedItem2 = spinnerShelfLife.getItemAtPosition(position).toString();
                //autoExpirationDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int valOfQty = Integer.parseInt(edtQty.getText().toString().trim());
                    selectedStoreName = spinnerUnitOfMeasure.getSelectedItem().toString();

                    if (selectedStoreName.equals("Box")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Boxes");
                        } else
                            tvUnitOfMeasure.setText("Box");
                    } else if (selectedStoreName.equals("Case")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Cases");
                        } else
                            tvUnitOfMeasure.setText("Case");
                    } else if (selectedStoreName.equals("Per-piece")) {
                        if (valOfQty > 1) {
                            tvUnitOfMeasure.setText("Pieces");
                        } else
                            tvUnitOfMeasure.setText("Piece");
                    } else {
                        tvUnitOfMeasure.setText("");
                    }
                } catch (Exception e) {
                    //Toast.makeText(DeliveryActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                DeliveryActivity.this, R.layout.spinnner_bg, spinnerContent);
        spinnerStoreLocation.setAdapter(adapter);
    }

    private void computeShelfLife() {

        try {
            String tempPickExDate = dtPickEx.getText().toString();
            Log.i("TAG", "Temp Prod Date: " + tempPickExDate);

            if (!tempPickExDate.equalsIgnoreCase("0000-00-00")) {
                String[] dtExSplit = tempPickExDate.split("-");

                int prodYear = Integer.parseInt(dtExSplit[0]);
                int prodMonth = Integer.parseInt(dtExSplit[1]);
                int prodDay = Integer.parseInt(dtExSplit[2]) + 1;

                Log.i("TAG", "" +
                        "\nYear: " + prodYear +
                        "\nMonth: " + prodMonth +
                        "\nDay: " + prodDay);


                int prodTimeStamp = timeStamp(prodYear, prodMonth - 1, prodDay);

                prodTimeStamp += secDay;// * countSL; // this is in sec 2628000 * 12

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(prodTimeStamp * 1000L);

                //String date = DateFormat.format("yyyy-MM-dd", cal).toString();
                //dtPickEx.setText(date);

                Calendar today = Calendar.getInstance();

                long diff = cal.getTimeInMillis() - today.getTimeInMillis();
                long days = diff / (24 * 60 * 60 * 1000);
                days = days - 1;

                if (String.valueOf(days).equalsIgnoreCase("0")) {
                    shelfLifeValue.setText("Expired!");
                    AlertDialog.Builder b = new AlertDialog.Builder(DeliveryActivity.this);
                    b.setTitle("Warning!");
                    b.setCancelable(false);
                    b.setMessage("This SKU is already expired!");
                    b.setPositiveButton("OK", null);
                    b.show();
                } else if (days < 10) {
                    AlertDialog.Builder b = new AlertDialog.Builder(DeliveryActivity.this);
                    b.setTitle("Warning!");
                    b.setCancelable(false);
                    b.setMessage("This SKU will expired in " + String.valueOf(days) + " day(s)");
                    b.setPositiveButton("OK", null);
                    b.show();
                    shelfLifeValue.setText(String.valueOf(days) + " Days");
                } else if (String.valueOf(days).equalsIgnoreCase("1")) {
                    shelfLifeValue.setText(String.valueOf(days) + " Day");
                } else {
                    shelfLifeValue.setText(String.valueOf(days) + " Days");
                }

            }
        } catch (Exception e) {
            Log.i("TAG", "Error in compute shelf life: " + e);
        }

    }

    private void autoExpirationDate() {

        try {
            String tempProdDate = dtPickProd.getText().toString();
            String[] dtExSplit = tempProdDate.split("-");
            rangeSL = "Month(s)";

            int prodYear = Integer.parseInt(dtExSplit[0]);
            int prodMonth = Integer.parseInt(dtExSplit[1]);
            int prodDay = Integer.parseInt(dtExSplit[2]) + 1;

            int prodTimeStamp = timeStamp(prodYear, prodMonth - 1, prodDay);

            if (rangeSL.equals("Day(s)")) {
                prodTimeStamp += secDay * countSL;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(prodTimeStamp * 1000L);
                String date = DateFormat.format("yyyy-MM-dd", cal).toString();
                dtPickEx.setText(date);

            } else if (rangeSL.equals("Week(s)")) {
                prodTimeStamp += secWeek * countSL;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(prodTimeStamp * 1000L);
                String date = DateFormat.format("yyyy-MM-dd", cal).toString();
                dtPickEx.setText(date);

            } else if (rangeSL.equals("Month(s)")) {
                prodTimeStamp += secMonth * countSL; // this is in sec 2628000 * 12

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(prodTimeStamp * 1000L);
                String date = DateFormat.format("yyyy-MM-dd", cal).toString();
                dtPickEx.setText(date);

                Calendar today = Calendar.getInstance();

                long diff = cal.getTimeInMillis() - today.getTimeInMillis();
                long days = diff / (24 * 60 * 60 * 1000);
                shelfLifeValue.setText(String.valueOf(days) + " Days");

            } else if (rangeSL.equals("Year(s)")) {
                prodTimeStamp += secYear * countSL;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(prodTimeStamp * 1000L);
                String date = DateFormat.format("yyyy-MM-dd", cal).toString();
                dtPickEx.setText(date);
            }
        } catch (Exception e) {
            Log.i("TAG", "Error in computing expiration date: " + e);
        }

    }

    /* spinner content for item list - causes delays
    public void getAllSpinnerContent(){
        cursor = sqlDB.rawQuery("SELECT DISTINCT itemCode, itemName " +
                "FROM assignment " +
                "WHERE " +
                "userCode=? " +
                "AND tag='1' " +
                "AND freshness='1'", new String[]{userCode});

        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if(cursor.getCount()!=0)
        {
            if(cursor.moveToFirst()){
                do{
                    spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
                }while(cursor.moveToNext());
            }
        }
        else
        {
            spinnerContent.add(new SpinnerKeyValue("No id", "No item"));
        }
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(DeliveryActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerContent);
        spinner.setAdapter(adapter);
    }
    */

    public void getSpinnerValue() {

        List<String> spinnerContent = new ArrayList<>();
        spinnerContent.add("Day(s)");
        spinnerContent.add("Week(s)");
        spinnerContent.add("Month(s)");
        spinnerContent.add("Year(s)");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(DeliveryActivity.this, R.layout.spinnner_bg, spinnerContent);
        spinnerShelfLife.setAdapter(adapter2);
    }

    private void insertInventory() {
        final String sItemName, sItemCode, sProductName;
        sItemName = tvItemName.getText().toString().trim();
        sItemCode = tvItemCode.getText().toString().trim();
        final String dateToday = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        final String strLotNo = edtLotNumber.getText().toString().trim();

        AlertDialog.Builder d = new AlertDialog.Builder(DeliveryActivity.this);
        d.setTitle("Review Product Details");
        d.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    //IF NO RECORD
                    sqlDB.execSQL("INSERT INTO delivery" +
                            "(userCode, " +
                            "itemCode, " +
                            "itemName, " +
                            "quantity, " +
                            "unitOfMeasure, " +
                            "listStatus, " +
                            "productionDate, " +
                            "expirationDate, " +
                            "storeName, " +
                            "storeLocCode, " +
                            "boxCase, " +
                            "lotNumber, " +
                            "postStatus, " +
                            "listtag, " +
                            "tag, " +
                            "popupStatus, " +
                            "syncStatus," +
                            "dateRecorded," +
                            "shelfLife) " +
                            "VALUES('" + userCode + "', " +
                            "'" + sItemCode + "', " +
                            "'" + sItemName + "', " +
                            "'" + edtQty.getText().toString().trim() + "', " +
                            "'" + spinnerUnitOfMeasure.getSelectedItem().toString().trim() + "', '1'," +
                            "'" + dtPickProd.getText().toString() + "', " +
                            "'" + dtPickEx.getText().toString() + "', " +
                            "'" + spinnerStoreLocation.getSelectedItem().toString().trim() + "'," +
                            "'" + spinnerSelectedItemCode + "', " +
                            "'" + edtBoxCase.getText().toString().trim() + "', " +
                            "'" + strLotNo + "', " +
                            "'not sync', " +
                            "'0', " +
                            "'none', " +
                            "'1'," +
                            "'not sync'," +
                            "'"+dateToday+"'," +
                            "'"+shelfLifeValue.getText().toString().trim()+"')");

                    Log.i("TAG", "Store Loc Code Val: " + spinnerSelectedItemCode);

                    sqlDB.execSQL("UPDATE " +
                            "assignment " +
                            "SET tag='0' " +
                            "WHERE freshness='1' " +
                            "AND itemCode='" + sItemCode + "'");

                    Toast.makeText(DeliveryActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(DeliveryActivity.this, FreshnessItem.class));
                    finish();
                } catch (Exception e) {
                    Log.i("TAG", "Error in saving delivery: " + e);
                }

            }
        });

        if (strLotNo.equalsIgnoreCase("")) {
            d.setMessage(//"User Code: "+userCode+"\n"+
                    //"Store Code: "+spinnerSelectedItemName+"\n"+
                    "Store Name: " + sItemName + "\n" +
                            //"Item Code: "+spinnerSelectedItem+"\n"+
                            "Product Name: " + sItemName + "\n" +
                            "Quantity: " + edtQty.getText() + "\n" +
                            "Unit of Measure: " + spinnerUnitOfMeasure.getSelectedItem().toString().trim() + "\n" +
                            //"Delivery Date: "+dtPickDeli.getText()+"\n"+
                            "Production Date: " + dtPickProd.getText() + "\n" +
                            "Expiration Date: " + dtPickEx.getText() + "\n" +
                            "Shelf Life: " + shelfLifeValue.getText().toString().trim());
            //"Post Status: "+"not sync"+"\n"+
            //"List Tag: "+0+"\n"+
            //"Tag: "+"None"+"\n"+
            //"Popup Status: "+"1"+"\n");
        } else {
            d.setMessage(//"User Code: "+userCode+"\n"+
                    //"Store Code: "+spinnerSelectedItemName+"\n"+
                            "Store Name: " + spinnerStoreLocation.getSelectedItem().toString().trim() + "\n" +
                            //"Item Code: "+spinnerSelectedItem+"\n"+
                            "Product Name: " + sItemName + "\n" +
                            "Quantity: " + edtQty.getText() + "\n" +
                            "Unit of Measure: " + spinnerUnitOfMeasure.getSelectedItem().toString().trim() + "\n" +
                            //"Delivery Date: "+dtPickDeli.getText()+"\n"+
                            "Production Date: " + dtPickProd.getText() + "\n" +
                            "Expiration Date: " + dtPickEx.getText() + "\n" +
                            "Shelf Life: " + shelfLifeValue.getText().toString().trim() + "\n" +
                            //"Box: "+edtBoxCase.getText()+"\n"+
                            "Lot No.: " + strLotNo);
            //"Post Status: "+"not sync"+"\n"+
            //"List Tag: "+0+"\n"+
            //"Tag: "+"None"+"\n"+
            //"Popup Status: "+"1"+"\n");
        }

        d.setNegativeButton("CANCEL", null);
        d.show();

        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        spinnerLoad();

                        edtQty.setText("");
                        edtBoxCase.setText("");
                        edtLotNumber.setText("");
                        edtQty.requestFocus();
                        break;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sqlDB.execSQL("UPDATE assignment SET tag='0'");
        sqlDB.close();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {

        String date = year + "-" + parseMonth(monthOfYear) + "-" + parseDay(dayOfMonth);

        if (flagDate == "dtPickDeli") {
            dtPickDeli.setText(date);
        } else if (flagDate == "dtExpirationDate") {
            dtPickEx.setText(date);
        } else {
            dtPickProd.setText(date);
        }


    }

    private String parseMonth(int month) {
        switch (month) {
            case 0:
                return "01";
            case 1:
                return "02";
            case 2:
                return "03";
            case 3:
                return "04";
            case 4:
                return "05";
            case 5:
                return "06";
            case 6:
                return "07";
            case 7:
                return "08";
            case 8:
                return "09";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "12";
        }
        return null;
    }

    private String parseDay(int day) {
        switch (day) {
            case 0:
                return "00";
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
        }
        return String.valueOf(day);
    }

    // spinner value for store location
    /*public void getAllSpinnerContentStoreLocation(){
        cursor2 = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        ArrayList<SpinnerKeyValue2> spinnerContent = new ArrayList<>();
        if(cursor2.moveToFirst()){
            do{
                spinnerContent.add(new SpinnerKeyValue2(cursor2.getString(0), cursor2.getString(1)));
            }while(cursor2.moveToNext());
        }

        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue2> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerContent);
        store_location.setAdapter(adapter2);
        //spinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}