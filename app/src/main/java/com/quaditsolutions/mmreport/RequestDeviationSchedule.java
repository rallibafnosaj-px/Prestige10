package com.quaditsolutions.mmreport;

import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class RequestDeviationSchedule extends AppCompatActivity
{
    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;

    private RecyclerViewRequestDeviation sAdapter;

    List<GlobalVar> GetDataAdapter1;

    SQLiteDatabase sqlDB;
    Cursor cursor, cursor2;

    String userCode, companyCode, storeLocation, workingDays;

    LinearLayout restDayLayout;

    TextView txtPreviousRestDay;

    Spinner spinnerRestDays;

    Button btnRequestChangeRestDay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // test if login as prestige, regris etc.
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
        setContentView(R.layout.items_layout);

        Intent getIn = getIntent();
        workingDays = getIn.getStringExtra("workingDays");
        setTitle(workingDays);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        sAdapter = new RecyclerViewRequestDeviation(GetDataAdapter1, this);
        recyclerView.setAdapter(sAdapter);

        getItemData();

        restDayLayout = (LinearLayout)findViewById(R.id.restDayLayout);
        txtPreviousRestDay = (TextView) findViewById(R.id.txtPreviousRestDay);
        spinnerRestDays = (Spinner)findViewById(R.id.spinnerRestDays);
        btnRequestChangeRestDay = (Button) findViewById(R.id.btnRequestChangeRestDay);

        if (GetDataAdapter1.isEmpty()) //For rest-day layout
        {
            restDayLayout.setVisibility(View.VISIBLE);
            txtPreviousRestDay.setText(workingDays);
            ScheduleRestDaysStart();
        }

        btnRequestChangeRestDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                    String restDay = spinnerRestDays.getSelectedItem().toString();

                    String strRest = "Rest Day";

                    cursor = sqlDB.rawQuery("SELECT startTime, " +
                            "endTime, " +
                            "storeName, " +
                            "storeLocCode " +
                            "FROM scheduleTemp " +
                            "WHERE workingDays=?", new String[]{restDay});

                    if (cursor.getCount() != 0)
                    {
                        if (cursor.moveToFirst())
                        {

                            /*---------------- Update Rest-Day -------------------*/

                            sqlDB.execSQL("UPDATE scheduleTemp "  +
                                    "SET "                        +
                                    "workingDays =  '" + restDay  + "', " +
                                    "startTime =    '" + strRest  + "', " +
                                    "endTime =      '" + strRest  + "', " +
                                    "storeName=     '" + strRest  + "', " +
                                    "storeLocCode = '" + strRest  + "' "  +
                                    "WHERE workingDays = '" + restDay + "' ");

                            /*Toast.makeText(RequestDeviationSchedule.this,
                                    "Success Update Request - Rest Day"
                                            +"\n\n" + "Working days: " + restDay
                                            +"\n"   + "Start Time: "   + strRest
                                            +"\n"   + "End Time: "     + strRest,
                                    Toast.LENGTH_LONG).show();
                            */

                           /*-------------- Update Work-Schedule -----------------*/

                            sqlDB.execSQL("UPDATE scheduleTemp " +
                                    "SET "             +
                                    "workingDays = '"  + workingDays + "', " +
                                    "startTime= '"     + cursor.getString(0) + "', " +
                                    "endTime = '"      + cursor.getString(1) + "', " +
                                    "storeName = '"    + cursor.getString(2) + "', " +
                                    "storeLocCode = '" + cursor.getString(3) +"' "   +
                                    "WHERE workingDays = '" + workingDays + "' ");

                            /*Toast.makeText(RequestDeviationSchedule.this,
                                    "Success Update Request - Work Schedule"
                                            +"\n\n" + "Working days: "  + workingDays
                                            +"\n"   + "Start Time: "    + cursor.getString(0)
                                            +"\n"   + "End Time: "      + cursor.getString(1)
                                            +"\n"   + "Store Name: "    + cursor.getString(2),
                                    Toast.LENGTH_LONG).show();*/
                            Toast.makeText(RequestDeviationSchedule.this,workingDays+" schedule set.", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }

                    else
                    {
                        cursor2 = sqlDB.rawQuery("SELECT startTime, " +
                                "endTime, "                               +
                                "storeName, "                             +
                                "storeLocCode "                           +
                                " FROM schedule "                         +
                                "WHERE workingDays=?", new String[]{restDay});

                        if (cursor2.moveToFirst())
                        {
                                /*---------------- Insert Rest-Day -------------------*/

                            sqlDB.execSQL("INSERT INTO scheduleTemp " +
                                    "(workingDays, "                  +
                                    "storeLocCode, "                  +
                                    "storeName, "                     +
                                    "startTime, "                     +
                                    "endTime) "                       +
                                    "VALUES "                         +
                                    "('"   + restDay +
                                    "', '" + strRest +
                                    "', '" + strRest +
                                    "', '" + strRest +
                                    "', '" + strRest +"');");

                            Toast.makeText(RequestDeviationSchedule.this,
                                    "Success Request - Rest Day"
                                            +"\n\n" + "Rest days: "  + restDay
                                            +"\n"   + "Start Time: " + strRest
                                            +"\n"   + "End Time: "   + strRest,
                                    Toast.LENGTH_LONG).show();

                            /*---------------- Insert Work-Schedule -----------------*/

                            sqlDB.execSQL("INSERT INTO scheduleTemp " +
                                    "(workingDays, "      +
                                    "startTime, "         +
                                    "endTime, "           +
                                    "storeName, "         +
                                    "storeLocCode) "      +
                                    "VALUES "             +
                                    "('"   + workingDays  +
                                    "', '" + cursor2.getString(0) +
                                    "', '" + cursor2.getString(1) +
                                    "', '" + cursor2.getString(2) +
                                    "', '" + cursor2.getString(3) +"');");

                            Toast.makeText(RequestDeviationSchedule.this,
                                    "Success Request - Work Schedule"
                                            +"\n\n" + "Working days: " + workingDays
                                            +"\n"   + "Start Time: "   + cursor2.getString(0)
                                            +"\n"   + "End Time: "     + cursor2.getString(1)
                                            +"\n"   + "Store Name: "   + cursor2.getString(2),
                                    Toast.LENGTH_LONG).show();

                            finish();
                        }
                    }
                }
                catch (Exception e)
                {
                    //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                    if (!"null".equals(e) || !"".equals(e))
                    {
                        Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
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

    private void getItemData()
    {
        try
        {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            GetDataAdapter1.clear();

            cursor = sqlDB.rawQuery("SELECT storeLocCode, " +
                    "storeName, " +
                    "startTime, " +
                    "endTime " +
                    "FROM scheduleTemp " +
                    "WHERE workingDays=?", new String[]{workingDays});

            if (cursor.getCount() != 0)
            {
                if (cursor.moveToLast())
                {
                    String strRest = "Rest Day";

                    if (cursor.getString(3).equals(strRest))
                    {

                    }
                    else
                    {
                        GlobalVar globalVar = new GlobalVar();
                        globalVar.workingDays = workingDays;
                        globalVar.storeLocCode= cursor.getString(0);
                        globalVar.storeName = cursor.getString(1);
                        globalVar.startTime = cursor.getString(2);
                        globalVar.endTime = cursor.getString(3);
                        GetDataAdapter1.add(globalVar);
                    }
                }
            }
            else
            {
                cursor2 = sqlDB.rawQuery("SELECT storeLocCode, " +
                        "storeName, " +
                        "startTime, " +
                        "endTime " +
                        " FROM schedule " +
                        "WHERE workingDays=?", new String[]{workingDays});

                if (cursor2.getCount() != 0)
                {
                    if(cursor2.moveToFirst())
                    {
                        do
                        {
                            GlobalVar globalVar = new GlobalVar();
                            globalVar.workingDays = workingDays;
                            globalVar.storeLocCode= cursor2.getString(0);
                            globalVar.storeName = cursor2.getString(1);
                            globalVar.startTime = cursor2.getString(2);
                            globalVar.endTime = cursor2.getString(3);
                            GetDataAdapter1.add(globalVar);
                        }
                        while(cursor2.moveToNext());
                    }
                }
            }
            sAdapter.notifyDataSetChanged();
            sqlDB.close();
        }
        catch (Exception e)
        {
            //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
            if (!"null".equals(e) || !"".equals(e))
            {
                Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ScheduleRestDaysStart()
    {
        try
        {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("SELECT " +
                    " restDays " +
                    " FROM scheduleRestDaysTemp " +
                    " WHERE restDays != '" + workingDays + "' ", null);

            if (cursor.getCount() != 0)
            {
                if(cursor.moveToFirst())
                {
                    List<String> listRestDays = new ArrayList<>();

                    //  listRestDays.add("Select Day");
                    do
                    {
                        listRestDays.add(cursor.getString(0));
                    }
                    while (cursor.moveToNext());

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinnner_bg, listRestDays);

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRestDays.setAdapter(dataAdapter);
                }
            }
            sqlDB.close();
        }
        catch (Exception e)
        {
            //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
            if (!"null".equals(e) || !"".equals(e))
            {
                Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
