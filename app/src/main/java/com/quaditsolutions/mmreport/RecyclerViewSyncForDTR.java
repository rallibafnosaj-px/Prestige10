package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class RecyclerViewSyncForDTR extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SQLiteDatabase sqlDB;
    Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    List<GlobalVar> getDataAdapter;

    int hour_val, minute_val;
    String timeIn24HrsVal = "", timeOut24HrsVal = "";

    public RecyclerViewSyncForDTR(List<GlobalVar> getDataAdapter, Context context) {

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

        /*String store = current.dtrStore, current_location_in = current.dtrCurrentLocationIn,
                       current_location_out = current.dtrCurrentLocationOut;*/

        final String sDtrDateIn = current.dtrDateIn,
                sDtrTimeIn = current.dtrTimeIn,
                sDtrStore = current.dtrStoreName,
                sDtrCurrentLocationIn = current.dtrCurrentLocationIn, sDtrDateOut = current.dtrDateOut,
                sDtrTimeOut = current.dtrTimeOut, sDtrCurrentLocationOut = current.dtrCurrentLocationOut,
                sDtrID = current.dtrID;

        // get first 2 digits of time in
        String first2Digits = sDtrTimeIn.substring(0, 2), timeSet = "", minLast2Digits = sDtrTimeIn.substring(3, 5); // hours
        int first2DigitsIntVal = Integer.parseInt(first2Digits);
        int last2DigitsIntVal = Integer.parseInt(minLast2Digits);

        String first2DigitsOut = sDtrTimeOut.substring(0, 2), minLast2DigitsOut = sDtrTimeOut.substring(3, 5), outtimeSet;
        int first2DigitsOutVal = Integer.parseInt(first2DigitsOut);
        int last2DigitsOutVal = Integer.parseInt(minLast2DigitsOut);

        // convert to 12 hrs format for Time In
        if (first2DigitsIntVal > 12) {
            timeSet = "PM";
            first2DigitsIntVal -= 12;
        } else if (first2DigitsIntVal == 0) {
            first2DigitsIntVal += 12;
            timeSet = "AM";
        } else if (first2DigitsIntVal == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        // convert to 12 hrs format for Time Out
        if (first2DigitsOutVal > 12) {
            outtimeSet = "PM";
            first2DigitsOutVal -= 12;
        } else if (first2DigitsOutVal == 0) {
            first2DigitsOutVal += 12;
            outtimeSet = "AM";
        } else if (first2DigitsOutVal == 12) {
            outtimeSet = "PM";
        } else {
            outtimeSet = "AM";
        }

        String min = "", outMin = "";
        String hr = "", outHr = "";

        //Toast.makeText(context, minLast2DigitsOut, Toast.LENGTH_SHORT).show();

        // for time in
        if (last2DigitsIntVal < 10)
            min = "0" + last2DigitsIntVal;
        else
            min = String.valueOf(last2DigitsIntVal);
        if (first2DigitsIntVal < 10)
            hr = "0" + first2DigitsIntVal;
        else
            hr = String.valueOf(first2DigitsIntVal);

        // for time out
        if (last2DigitsOutVal < 10)
            outMin = "0" + last2DigitsOutVal;
        else
            outMin = String.valueOf(last2DigitsOutVal);
        if (first2DigitsOutVal < 10)
            outHr = "0" + first2DigitsOutVal;
        else
            outHr = String.valueOf(first2DigitsOutVal);

        //if(current.cType.equals("Time In"))
        //{
        myHolder.tv_date.setText("Date In: " + sDtrDateIn);
        myHolder.tv_time.setText("Time In: " + hr + ":" + min + " " + timeSet);
        myHolder.tv_type.setText("Time In");//+current.cType);
        myHolder.tv_store.setText("Store In: " + sDtrStore);
        myHolder.tv_current_location.setText("GPS Location In: " + sDtrCurrentLocationIn);
        //}
        //else // if(current.cType.equals("Time Out"))
        //{
        if (sDtrDateOut.equalsIgnoreCase("0000-00-00")) {
            myHolder.tv_date_out.setVisibility(View.GONE);
            myHolder.tv_store_out.setVisibility(View.GONE);
            myHolder.tv_time_out.setVisibility(View.GONE);
            myHolder.tv_type_out.setText("No Time Out"); // +current.cType);
            myHolder.tv_current_location_out.setVisibility(View.GONE);
        } else {
            myHolder.tv_date_out.setText("Date Out: " + sDtrDateOut);
            myHolder.tv_store_out.setText("Store Out: " + sDtrStore);
            myHolder.tv_time_out.setText("Time Out: " + outHr + ":" + outMin + " " + outtimeSet);
            myHolder.tv_type_out.setText("Time Out");//+current.cType);
            myHolder.tv_current_location_out.setText("GPS Location Out: " + sDtrCurrentLocationOut);
        }

        //}

        myHolder.btnDeleteDTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                    b.setTitle("Confirm Delete");
                    b.setMessage("Are you sure you want to delete this DTR?");
                    b.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                            sqlDB.execSQL("DELETE FROM dtr WHERE dtrID = '" + sDtrID + "' ");
                            Toast.makeText(context, "DTR deleted!", Toast.LENGTH_SHORT).show();
                            sqlDB.close();
                            getDataAdapter.remove(myHolder.getAdapterPosition());
                            notifyDataSetChanged();
                        }
                    });
                    b.setNegativeButton("CANCEL", null);
                    b.show();

                } catch (Exception e) {
                    Log.i("TAG", "Error in deleting DTR");
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_date, tv_type, tv_time, tv_store, tv_current_location,
                tv_date_out, tv_type_out, tv_time_out, tv_store_out, tv_current_location_out,
                btnDeleteDTR;

        public View v1;

        public CardView cardView;

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

            v1 = itemView.findViewById(R.id.v1);
            v1.setVisibility(View.VISIBLE);

            btnDeleteDTR = (TextView) itemView.findViewById(R.id.btnDeleteDTR);
            btnDeleteDTR.setVisibility(View.VISIBLE);

            //       cardView.setOnClickListener(this);
        }
     /*   public void onClick(View view) {
            int index = getAdapterPosition();
            GlobalVar globalVar = getDataAdapter.get(index);
            //String itemCode = String.valueOf(globalVar.ItemCode);
            String categoryName = String.valueOf(globalVar.categoryName);

            Intent in = new Intent(itemView.getContext(), InventoryItem.class);
            //in.putExtra("itemCode", itemCode);
            in.putExtra("categoryName", categoryName);
            itemView.getContext().startActivity(in);

        } */
    }
}

