package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rozz on 26/07/2018.
 */

public class RequestDeviationTab extends Fragment {

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
        getItemData();
        recyclerViewadapter = new RecyclerViewForDeviationRequest(GetDataAdapter1,getActivity());
        recyclerView.setAdapter(recyclerViewadapter);

        return rootView;

    }

    public void getItemData()
    {

        //Open Or Create Database
        sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        GetDataAdapter1.clear();

        try {

            cursor = sqlDB.rawQuery("SELECT storeName, "+
                    "date, "+
                    "timeIn, " +
                    "timeOut, " +
                    "reason, " +
                    "reqDeviationStatus "+
                    "FROM req_deviation",null);

            if(cursor.moveToFirst())
            {
                do
                {
                    GlobalVar globalVar = new GlobalVar();
                    globalVar.reqdevStoreName = cursor.getString(0);
                    globalVar.reqdevDate = cursor.getString(1);
                    globalVar.reqdevTimeIn = cursor.getString(2);
                    globalVar.reqdevTimeOut = cursor.getString(3);
                    globalVar.reqdevReason = cursor.getString(4);
                    globalVar.reqdevStatus = cursor.getString(5);

                    GetDataAdapter1.add(globalVar);

                }while(cursor.moveToNext());
            }else{
                Toast.makeText(getContext(), "No Result Found.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
        sqlDB.close();
    }
}