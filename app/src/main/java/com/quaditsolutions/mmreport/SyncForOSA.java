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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 11/9/2017.
 */

public class SyncForOSA extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    RequestQueue requestQueue;
    List<GlobalVar> GetDataAdapter1;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    String userCode, companyCode;

    //  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.sync_for_daily_time_record in you classes
        View rootView = inflater.inflate(R.layout.sync_for_osa, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences("mainInfo", Context.MODE_PRIVATE);

        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_sync_osa);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        getItemData(userCode);
        recyclerViewadapter = new RecyclerViewSyncForOSA(GetDataAdapter1, getActivity());
        recyclerView.setAdapter(recyclerViewadapter);

        //needed for rootview ending
        return rootView;

    }

    public void getItemData(final String userCode) {

        //Open Or Create Database
        sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        GetDataAdapter1.clear();
        cursor = sqlDB.rawQuery("SELECT DISTINCT " +
                "osa.itemCode, " +
                "osa.osa, " +
                "osa.facing, " +
                "osa.weekNo, " +
                "osa.itemName, " +
                "osa.maxCapacity," +
                "osa.homeShelfPcs," +
                "osa.secondShelfPcs " +
                "FROM osa " +
                "WHERE osa.userCode=? " +
                "AND syncStatus='not sync' " +
                "ORDER BY osaID DESC", new String[]{userCode});

        if (cursor.moveToFirst()) {
            do {

                GlobalVar globalVar = new GlobalVar();
                globalVar.osaItemCode = cursor.getString(0);
                globalVar.osaOsa = cursor.getString(1);
                globalVar.osaFacing = cursor.getString(2);
                globalVar.osaWeekNo = cursor.getString(3);
                globalVar.osaItemName = cursor.getString(4);
                globalVar.osaMaxCapacity = cursor.getString(5);
                globalVar.osaHomeShelf = cursor.getString(6);
                globalVar.osaSecShelf = cursor.getString(7);

                GetDataAdapter1.add(globalVar);

            } while (cursor.moveToNext());
        }
        sqlDB.close();
    }
}
