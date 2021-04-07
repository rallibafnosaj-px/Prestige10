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

public class SyncForFreshness extends Fragment {

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

        View rootView = inflater.inflate(R.layout.sync_for_freshness, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_sync_freshness);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        getItemData(userCode);
        recyclerViewadapter = new RecyclerViewSyncForFreshness(GetDataAdapter1,getActivity());
        recyclerView.setAdapter(recyclerViewadapter);

        //needed for rootview ending
        return rootView;

    }

    public void getItemData(final String userCode){
        //     try {
        //Open Or Create Database
        sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        GetDataAdapter1.clear();
        cursor = sqlDB.rawQuery(
                "SELECT itemCode, "+
                     "itemName, "+
                     "quantity, "+
                     "deliveryDate, " +
                     "productionDate, " +
                     "expirationDate, " +
                     "storeName, " +
                     "unitOfMeasure, "+
                     "lotNumber, " +
                     "boxCase, " +
                     "tag, " +
                     "postStatus, " +
                     "listtag, " +
                     "popupStatus " +
                "FROM delivery " +
                "WHERE userCode=? AND syncStatus='not sync' ORDER BY deliveryID DESC",new String[]{userCode});

        if(cursor.moveToFirst())
        {
            do
            {

                GlobalVar globalVar = new GlobalVar();
                globalVar.freshness_itemCode = cursor.getString(0);
                globalVar.product_name = cursor.getString(1);
                globalVar.freshness_quantity = cursor.getString(2);
                globalVar.freshness_deliveryDate = cursor.getString(3);
                globalVar.freshness_production_date = cursor.getString(4);
                globalVar.freshness_expiration_date = cursor.getString(5);
                globalVar.freshness_store_name = cursor.getString(6);
                globalVar.freshness_unit_of_measure = cursor.getString(7);
                globalVar.freshness_lot_no = cursor.getString(8);

                GetDataAdapter1.add(globalVar);

            }while(cursor.moveToNext());
        }

        //           recyclerViewadapter.notifyDataSetChanged();
        sqlDB.close();
        //    } catch (Exception e) {
        //          Toast.makeText(getActivity(),"Error"+ e,Toast.LENGTH_SHORT).show();
       /*     if (!"null".equals(e) || !"".equals(e)) {
                Toast.makeText(getActivity(), "No Event to show!", Toast.LENGTH_SHORT).show();
            } */
        //     }
    }
}