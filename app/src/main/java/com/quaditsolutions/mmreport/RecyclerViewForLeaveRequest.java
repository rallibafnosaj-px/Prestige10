package com.quaditsolutions.mmreport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Rozz on 26/07/2018.
 */

public class RecyclerViewForLeaveRequest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewForLeaveRequest(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cardview_leave_request, parent,false);
        RecyclerViewForLeaveRequest.MyHolder holder=new RecyclerViewForLeaveRequest.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewForLeaveRequest.MyHolder myHolder= (RecyclerViewForLeaveRequest.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.tv_leave_status.setText("Status: "+current.reqleaveStatus);
        myHolder.tv_date_from.setText("Date From: "+current.reqleaveDateFrom);
        myHolder.tv_date_to.setText("Date To: "+current.reqleaveDateTo);
        myHolder.tv_days.setText("Day(s): "+current.reqleaveDays);
        myHolder.tv_leave_reason.setText("Reason: "+current.reqleaveReason);

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_leave_status, tv_date_from, tv_date_to, tv_days, tv_leave_reason;

        public MyHolder(View itemView) {

            super(itemView);

            tv_leave_status = (TextView) itemView.findViewById(R.id.tv_status_leave);
            tv_date_from = (TextView) itemView.findViewById(R.id.tv_date_from);
            tv_date_to = (TextView) itemView.findViewById(R.id.tv_date_to);
            tv_days = (TextView) itemView.findViewById(R.id.tv_days);
            tv_leave_reason = (TextView) itemView.findViewById(R.id.tv_reason_leave);

        }
    }
}
