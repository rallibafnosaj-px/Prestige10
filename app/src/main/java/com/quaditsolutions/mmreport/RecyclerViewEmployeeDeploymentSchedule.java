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

public class RecyclerViewEmployeeDeploymentSchedule extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewEmployeeDeploymentSchedule(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cardview_deployment_schedule_viewing, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        try {

            final MyHolder myHolder= (MyHolder) holder;
            final GlobalVar current = getDataAdapter.get(position);

            if (current.schedStoreName.equalsIgnoreCase("Rest Day") ||
                current.schedStoreName.equalsIgnoreCase("")){
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
                myHolder.tv_store_schedview.setText("Store: "+current.schedStoreName);

                // CONVERT TIME IN OUT 24Hrs format to 12hrs format
                String sDtrTimeIn = current.schedStartTime, sDtrTimeOut = current.schedEndTime;

                // get first 2 digits of time in
                String first2Digits = sDtrTimeIn.substring(0,2),
                        timeSet="",
                        minLast2Digits = sDtrTimeIn.substring(3,5); // hours

                int first2DigitsIntVal = Integer.parseInt(first2Digits);
                int last2DigitsIntVal = Integer.parseInt(minLast2Digits);

                String first2DigitsOut = sDtrTimeOut.substring(0,2), minLast2DigitsOut = sDtrTimeOut.substring(3,5), outtimeSet;
                int first2DigitsOutVal = Integer.parseInt(first2DigitsOut);
                int last2DigitsOutVal = Integer.parseInt(minLast2DigitsOut);

                // convert to 12 hrs format for Time In
                if(first2DigitsIntVal > 12){
                    timeSet = "PM";
                    first2DigitsIntVal -= 12;
                }else if (first2DigitsIntVal == 0) {
                    first2DigitsIntVal += 12;
                    timeSet = "AM";
                }else if (first2DigitsIntVal == 12){
                    timeSet = "PM";
                }else{
                    timeSet = "AM";
                }

                // convert to 12 hrs format for Time Out
                if(first2DigitsOutVal > 12){
                    outtimeSet = "PM";
                    first2DigitsOutVal -= 12;
                }else if (first2DigitsOutVal == 0) {
                    first2DigitsOutVal += 12;
                    outtimeSet = "AM";
                }else if (first2DigitsOutVal == 12){
                    outtimeSet = "PM";
                }else{
                    outtimeSet = "AM";
                }

                String min = "", outMin = "";
                String hr = "", outHr = "";

                //Toast.makeText(context, minLast2DigitsOut, Toast.LENGTH_SHORT).show();

                // for time in
                if (last2DigitsIntVal < 10)
                    min = "0" + last2DigitsIntVal ;
                else
                    min = String.valueOf(last2DigitsIntVal);
                if (first2DigitsIntVal < 10)
                    hr = "0" + first2DigitsIntVal;
                else
                    hr = String.valueOf(first2DigitsIntVal);

                // for time out
                if (last2DigitsOutVal < 10)
                    outMin = "0" + last2DigitsOutVal ;
                else
                    outMin = String.valueOf(last2DigitsOutVal);
                if (first2DigitsOutVal < 10)
                    outHr = "0" + first2DigitsOutVal;
                else
                    outHr = String.valueOf(first2DigitsOutVal);

                String tIn, tOut;
                tIn = "Time In: "+ hr + ":" + min + " " + timeSet;
                tOut = "Time Out: "+ outHr + ":" + outMin + " " + outtimeSet;

                myHolder.tv_time_in_schedview.setText(tIn);
                myHolder.tv_time_out_schedview.setText(tOut);

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

        public MyHolder(View itemView) {

            super(itemView);

            tv_day_schedview = (TextView) itemView.findViewById(R.id.tv_day_schedview);
            tv_store_schedview = (TextView) itemView.findViewById(R.id.tv_store_schedview);
            tv_time_in_schedview = (TextView) itemView.findViewById(R.id.tv_time_in_schedview);
            tv_time_out_schedview = (TextView) itemView.findViewById(R.id.tv_time_out_schedview);
            tv_schedule_id = (TextView) itemView.findViewById(R.id.changeSchedID);

        }
    }
}