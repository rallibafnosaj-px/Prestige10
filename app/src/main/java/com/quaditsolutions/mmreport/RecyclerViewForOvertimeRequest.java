package com.quaditsolutions.mmreport;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Rozz on 25/07/2018.
 */

public class RecyclerViewForOvertimeRequest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewForOvertimeRequest(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=inflater.inflate(R.layout.cardview_overtime_request, parent,false);
        RecyclerViewForOvertimeRequest.MyHolder holder=new RecyclerViewForOvertimeRequest.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewForOvertimeRequest.MyHolder myHolder= (RecyclerViewForOvertimeRequest.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.tvStatus.setText("Status: "+current.reqotStatus);
        myHolder.tv_store.setText("Store: "+current.reqotStoreName);
        myHolder.tv_time_in.setText("Time In: "+current.reqotTimeIn);
        myHolder.tv_time_out.setText("Time Out: "+current.reqotTimeOut);
        myHolder.tv_date_in.setText("Date In: "+current.reqotDateIn);
        myHolder.tv_date_out.setText("Date Out: "+current.reqotDateOut);

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_time_out,tv_time_in,tv_date_out,tv_date_in,tv_store,tvStatus;

        public MyHolder(View itemView) {

            super(itemView);

            tv_time_out = (TextView) itemView.findViewById(R.id.tv_time_out);
            tv_time_in = (TextView) itemView.findViewById(R.id.tv_time_in);
            tv_date_in = (TextView) itemView.findViewById(R.id.tv_date_in);
            tv_date_out = (TextView) itemView.findViewById(R.id.tv_date_out);
            tv_store = (TextView) itemView.findViewById(R.id.tv_store);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status_ot_request);

        }
    }
}
