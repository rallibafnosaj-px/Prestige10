package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *Created by Khyrz on 11/10/2017.
 */

public class SyncForDailyTimeRecord extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    String userCode, companyCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.sync_for_daily_time_record, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_sync_dtr);

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        getItemData(userCode);

        recyclerViewadapter = new RecyclerViewSyncForDTR(GetDataAdapter1,getActivity());

        recyclerView.setAdapter(recyclerViewadapter);

        //Returning the layout file after inflating
        //Change R.layout.sync_for_daily_time_record in you classes
        return rootView;

    }

    public void getItemData(final String userCode)
    {

            //Open Or Create Database
            sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            GetDataAdapter1.clear();

            try {
                /*
                cursor = sqlDB.rawQuery("SELECT dtr.dtrID, "+
                        "dtr.dateIn, "+
                        "dtr.dateOut, " +
                        "dtr.addressIn, " +
                        "dtr.addressOut, " +
                        "dtr.storeLocCode, " +
                        "schedule.storeName, "+
                        "CASE " +
                            "WHEN dtr.dateOut = '0000-00-00' " +
                            "THEN 'Time In' " +
                            "ELSE 'Time Out' " +
                        "END AS cType, " +
                        "dtr.timeIn, " +
                        "dtr.timeOut " +
                        "FROM dtr LEFT JOIN schedule ON dtr.storeLocCode = schedule.storeLocCode " +
                        "WHERE dtr.userCode=? ORDER BY dtr.dtrID DESC",new String[]{userCode});

                */
                /*
                        cursor = sqlDB.rawQuery("SELECT dt.dtrID, "+
                        "dt.dateIn, "+
                        "dt.dateOut, " +
                        "dt.addressIn, " +
                        "dt.addressOut, " +
                        "dt.storeLocCode, " +
                        "(SELECT sched.storeName FROM schedule AS sched WHERE sched.storeLocCode = dt.storeLocCode LIMIT 1) AS storeName, "+
                        "CASE WHEN dt.dateOut = '0000-00-00' " +
                        "THEN 'Time In' " +
                        "ELSE 'Time Out' " +
                        "END AS cType, " +
                        "dt.timeIn, " +
                        "dt.timeOut " +
                        "FROM dtr as dt " +
                        "WHERE dt.userCode=? ORDER BY dt.dtrID DESC",new String[]{userCode});

                * */

                cursor = sqlDB.rawQuery("SELECT " +
                        "dtrID, "+
                        "dateIn, "+
                        "dateOut, " +
                        "addressIn, " +
                        "addressOut, " +
                        "storeLocCode, " +
                        "storeName, "+
                        "CASE WHEN dateOut = '0000-00-00' " +
                            "THEN 'Time In' " +
                            "ELSE 'Time Out' " +
                        "END AS cType, " +
                        "timeIn, " +
                        "timeOut " +
                        "FROM dtr " +
                        "WHERE userCode=? ORDER BY dtrID DESC",new String[]{userCode});

                if(cursor.moveToFirst())
                {
                 //GetDataAdapter1.clear();
                    do
                    {
                        GlobalVar globalVar = new GlobalVar();
                        globalVar.dtrID = cursor.getString(0);
                        globalVar.dtrDateIn = cursor.getString(1);
                        globalVar.dtrDateOut = cursor.getString(2);
                        globalVar.dtrCurrentLocationIn = cursor.getString(3);
                        globalVar.dtrCurrentLocationOut = cursor.getString(4);
                        globalVar.dtrStore = cursor.getString(5);
                        globalVar.dtrStoreName = cursor.getString(6);
                        globalVar.cType = cursor.getString(7);
                        globalVar.dtrTimeIn = cursor.getString(8);
                        globalVar.dtrTimeOut = cursor.getString(9);

                        GetDataAdapter1.add(globalVar);

                    }while(cursor.moveToNext());
                }
            }catch (Exception e){

            }
        sqlDB.close();
    }
}