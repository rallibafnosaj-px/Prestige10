package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DELL on 11/9/2017.
 */

public class SyncForExpense extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    RequestQueue requestQueue ;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    String userCode, companyCode;

    //  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.sync_for_daily_time_record in you classes
        View rootView = inflater.inflate(R.layout.sync_for_expense, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_sync_expense);

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        getItemData(userCode);

        recyclerViewadapter = new RecyclerViewSyncForExpense(GetDataAdapter1,getActivity());

        recyclerView.setAdapter(recyclerViewadapter);



        //needed for rootview ending
        return rootView;

    }

    public void getItemData(final String userCode){
        //Open Or Create Database
        sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        GetDataAdapter1.clear();
        cursor = sqlDB.rawQuery("SELECT date, "+
                                     "expenseCode, "+
                                     "expenseType, " +
                                     "meansOfTransportations, " +
                                     "amount, " +
                                     "notes " +
                                     "FROM expenses " +
                                     "WHERE employeeCode=? AND " +
                                     "syncStatus='not sync' ORDER BY expenseID DESC", new String[]{userCode});
        if(cursor.moveToFirst())
        {
            do
            {
                GlobalVar globalVar = new GlobalVar();
                globalVar.expDate = cursor.getString(0);
                globalVar.expCode = cursor.getString(1);
                globalVar.expType = cursor.getString(2);
                globalVar.expMeansOfTransportation = cursor.getString(3);
                globalVar.expAmount = cursor.getString(4);
                globalVar.expNote = cursor.getString(5);

                GetDataAdapter1.add(globalVar);

            }while(cursor.moveToNext());
        }
        sqlDB.close();
    }
}
