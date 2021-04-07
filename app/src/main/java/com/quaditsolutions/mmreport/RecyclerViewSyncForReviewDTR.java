package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by DELL on 11/10/2017.
 */

public class RecyclerViewSyncForReviewDTR extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    public View layout;
    SQLiteDatabase sqlDB;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewSyncForReviewDTR(List<GlobalVar> getDataAdapter, Context context) {

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recyclerview_items_sync_dtr, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final MyHolder myHolder = (MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);
        Integer lineNum = position + 1;

        final String sDtrDateIn = current.rdtrDateIn,
                sDtrTimeIn = current.rdtrTimeIn,
                sDtrStore = current.rdtrStoreName,
                sDtrCurrentLocationIn = current.rdtrCurrentLocationIn,
                sDtrDateOut = current.rdtrDateOut,
                sDtrTimeOut = current.rdtrTimeOut,
                sDtrCurrentLocationOut = current.rdtrCurrentLocationOut,
                sDtrID = current.dtrID;

        myHolder.tv_type.setText(lineNum + ". TIME IN");
        myHolder.tv_date.setText("Date: " + sDtrDateIn);
        myHolder.tv_time.setText("Time: " + sDtrTimeIn);
        myHolder.tv_store.setText("Store: " + sDtrStore);
        myHolder.tv_current_location.setText("Address: " + sDtrCurrentLocationIn);

        if (sDtrDateOut.equalsIgnoreCase("0000-00-00") ||
                sDtrDateOut.equals("")) {
            myHolder.tv_type_out.setText("TIME OUT");
            myHolder.tv_date_out.setText("No Date Out Found.");
            myHolder.tv_time_out.setText("No Time Out Found.");
            myHolder.tv_store_out.setText("No Store Out Found.");
            myHolder.tv_current_location_out.setText("No Address Found.");
        } else {
            myHolder.tv_type_out.setText("TIME OUT");
            myHolder.tv_date_out.setText("Date: " + sDtrDateOut);
            myHolder.tv_store_out.setText("Store: " + sDtrStore);
            myHolder.tv_time_out.setText("Time: " + sDtrTimeOut);
            myHolder.tv_current_location_out.setText("Address : " + sDtrCurrentLocationOut);
        }
        
    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_date, tv_type, tv_time, tv_store, tv_current_location,
                tv_date_out, tv_type_out, tv_time_out, tv_store_out, tv_current_location_out;

        public CardView cardView;

        public View v1;
        public TextView btnDeleteDTR;

        public MyHolder(View itemView) {

            super(itemView);

            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            cardView = (CardView) itemView.findViewById(R.id.cardview1);
            tv_store = (TextView) itemView.findViewById(R.id.tv_store);
            tv_current_location = (TextView) itemView.findViewById(R.id.tv_current_location);

            tv_date_out = (TextView) itemView.findViewById(R.id.tv_date_out);
            tv_type_out = (TextView) itemView.findViewById(R.id.tv_type_out);
            tv_time_out = (TextView) itemView.findViewById(R.id.tv_time_out);
            tv_store_out = (TextView) itemView.findViewById(R.id.tv_store_out);
            tv_current_location_out = (TextView) itemView.findViewById(R.id.tv_current_location_out);

            btnDeleteDTR = (TextView) itemView.findViewById(R.id.btnDeleteDTR);
            btnDeleteDTR.setVisibility(View.GONE);
            v1 = itemView.findViewById(R.id.v1);
            v1.setVisibility(View.GONE);

        }
    }
}