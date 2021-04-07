package com.quaditsolutions.mmreport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Rozz on 31/07/2018.
 */

public class RecyclerViewScheduleView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewScheduleView(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cardview_schedule_view_layout, parent,false);
            MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        try {

            final MyHolder myHolder= (MyHolder) holder;
            final GlobalVar current = getDataAdapter.get(position);

            myHolder.chkSchedID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if (myHolder.chkSchedID.isChecked()){

                            SQLiteDatabase sqlDB;
                            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                            String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                                    "scheduleCheckedID(scheduleCheckedID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "chkID);";
                            sqlDB.execSQL(SCHEDULE_TABLE);

                            sqlDB.execSQL("INSERT INTO scheduleCheckedID " +
                                    "(chkID) "      +
                                    "VALUES "             +
                                    "('" + current.schedscheduleID + "');");
                            sqlDB.close();

                            //Toast.makeText(context, current.schedscheduleID, Toast.LENGTH_SHORT).show();

                        }else{

                            SQLiteDatabase sqlDB;
                            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                            String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                                    "scheduleCheckedID(scheduleCheckedID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "chkID);";
                            sqlDB.execSQL(SCHEDULE_TABLE);

                            sqlDB.execSQL("DELETE FROM scheduleCheckedID WHERE chkID = '"+ current.schedscheduleID +"'");

                            sqlDB.close();

                        }
                    }catch (Exception e){
                        Log.i("TAG","Error "+e);
                    }
                }
            });

            if (current.schedStoreName.equalsIgnoreCase("Rest Day")){
                myHolder.tv_day_schedview.setText(current.schedWorkingDay);
                myHolder.tv_store_schedview.setText(current.schedStoreName);
                myHolder.tv_time_in_schedview.setVisibility(View.GONE);
                myHolder.tv_time_out_schedview.setVisibility(View.GONE);
                myHolder.tv_schedule_id.setText(current.schedscheduleID);
            }else{
                myHolder.tv_time_in_schedview.setVisibility(View.VISIBLE);
                myHolder.tv_time_out_schedview.setVisibility(View.VISIBLE);
                myHolder.tv_schedule_id.setText(current.schedscheduleID);
                myHolder.tv_day_schedview.setText(current.schedWorkingDay);
                myHolder.tv_store_schedview.setText(current.schedStoreName);
                myHolder.tv_time_in_schedview.setText("Time In: "+current.schedStartTime);
                myHolder.tv_time_out_schedview.setText("Time Out: "+current.schedEndTime);
            }

        }catch(Exception e){
            Log.i("TAG","Error in formatting time: "+e);
        }
    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_day_schedview,tv_store_schedview,tv_time_in_schedview,
                tv_time_out_schedview, tv_schedule_id;

        public CheckBox chkSchedID;

        public MyHolder(View itemView) {

            super(itemView);

            tv_day_schedview = (TextView) itemView.findViewById(R.id.tv_day_schedview);
            tv_store_schedview = (TextView) itemView.findViewById(R.id.tv_store_schedview);
            tv_time_in_schedview = (TextView) itemView.findViewById(R.id.tv_time_in_schedview);
            tv_time_out_schedview = (TextView) itemView.findViewById(R.id.tv_time_out_schedview);
            tv_schedule_id = (TextView) itemView.findViewById(R.id.changeSchedID);

            chkSchedID = (CheckBox) itemView.findViewById(R.id.chkSchedID);
        }
    }
}