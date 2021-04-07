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

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 11/13/2017.
 */

public class SyncForInventory extends Fragment {

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
        View rootView = inflater.inflate(R.layout.sync_for_inventory, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_sync_inventory);

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        getItemData(userCode);

        recyclerViewadapter = new RecyclerViewSyncForInventory(GetDataAdapter1, getActivity());

        recyclerView.setAdapter(recyclerViewadapter);

        //needed for rootview ending
        return rootView;

    }

    public void getItemData(final String userCode) {
        //     try {
        //Open Or Create Database
        sqlDB = getActivity().openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        GetDataAdapter1.clear();
        cursor = sqlDB.rawQuery("SELECT DISTINCT " +

                "i.weekNo, " +
                "i.itemName, " +
                "i.inventoryDate," +

                "i.sellingAreaPcs," +
                "i.warehousePcs," +
                "i.warehouseCases," +

                // deliver
                "i.deliveryPcs," +
                "i.deliveryCases," +
                "i.adjustmentPcs," +

                // returns
                "i.pulloutPcs," +
                "i.badOrderPcs," +
                "i.damagedItemPcs," +
                "i.expiredItemsPcs," +

                // ending
                "i.endSellingAreaPcs," +
                "i.endWarehousePcs," +
                "i.endWarehouseCases," +
                "i.offtake," +

                // max cap
                "i.daysOutOfStocks," +
                "i.homeShelf," +
                "i.secondaryDisplay, " +
                "i.userCode," +
                "i.inventoryID " +

                "FROM inventory AS i " +
                "LEFT JOIN headerinventory AS h " +
                "ON i.userCode = h.userCode " +
                "WHERE i.userCode=? " +
                "AND i.syncStatus = 'not sync' " +
                "ORDER BY inventoryID " +
                "DESC", new String[]{userCode});

        if (cursor.moveToFirst()) {
            do {
                // index are connected to your inventoryAdapters
                GlobalVar g = new GlobalVar();

                g.tv_inventory_weekno = cursor.getString(0);
                g.tv_inventory_item_name = cursor.getString(1);
                g.tvInvDate = cursor.getString(2);

                g.tvBegSA = cursor.getString(3);
                g.tvWHPcs = cursor.getString(4);
                g.tvWHCases = cursor.getString(5);

                g.tvDeliveryPcs = cursor.getString(6);
                g.tvDeliveryCases = cursor.getString(7);
                g.tvAdjustmentPcs = cursor.getString(8);

                g.tvPullOut = cursor.getString(9);
                g.tvBadOrder = cursor.getString(10);
                g.tvDamagedItemPcs = cursor.getString(11);
                g.tvExpiredItemsPcs = cursor.getString(12);

                g.tvEndSellingAreaPcs = cursor.getString(13);
                g.tvEndWHPcs = cursor.getString(14);
                g.tvEndWHCases = cursor.getString(15);
                g.tvOfftake = cursor.getString(16);

                g.tvDaysOS = cursor.getString(17);
                g.tvHomeShelf = cursor.getString(18);
                g.tvSecondaryDisplay = cursor.getString(19);

                g.iInventoryID = cursor.getString(21);

                GetDataAdapter1.add(g);
                //recyclerViewadapter.notifyDataSetChanged();

            } while (cursor.moveToNext());
        }

        sqlDB.close();

    }
}