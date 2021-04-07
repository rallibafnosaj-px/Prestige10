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
 * Created by DELL on 11/12/2017.
 */

public class RecyclerViewSyncForExpense extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewSyncForExpense(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.recyclerview_items_sync_expense, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        MyHolder myHolder= (MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);
        String typeOfTransportation = current.expType;

        if (!typeOfTransportation.equalsIgnoreCase("Transportation")){
            myHolder.tvMeansOfTransportation.setVisibility(View.GONE);
        }else if(typeOfTransportation.equalsIgnoreCase("Transportation")) {
            myHolder.tvMeansOfTransportation.setVisibility(View.VISIBLE);
            myHolder.tvMeansOfTransportation.setText("Means of transportation: "+current.expMeansOfTransportation);
        }else
        {

        }

        df = new DecimalFormat("0.00");
        Double y = Double.parseDouble(current.expAmount);
        String x = String.valueOf(df.format(y));

        myHolder.tv_exp_code.setText("Code: "+current.expCode);
        myHolder.tv_exp_type.setText("Expense type: "+current.expType);
        myHolder.tv_exp_amount.setText("Amount: ₱ "+x);
        myHolder.tv_exp_date.setText("Date: "+current.expDate);
        myHolder.tvNote.setText("Note: " + current.expNote);
        myHolder.tvExpTypeTitle.setText(current.expType);

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_exp_date,tv_exp_code,tv_exp_type, tvMeansOfTransportation, tv_exp_amount, tvNote, tvExpTypeTitle;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            tv_exp_date = (TextView) itemView.findViewById(R.id.tv_exp_date);
            tv_exp_code = (TextView) itemView.findViewById(R.id.tv_exp_code);
            tv_exp_type = (TextView) itemView.findViewById(R.id.tv_exp_type);
            tv_exp_amount = (TextView) itemView.findViewById(R.id.tv_exp_amount);
            tvMeansOfTransportation = (TextView) itemView.findViewById(R.id.tvMeansOfTransportation);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvExpTypeTitle = (TextView) itemView.findViewById(R.id.tvExpenseType);

       //     cardView = (CardView) itemView.findViewById(R.id.cardview1);
            //       cardView.setOnClickListener(this);
        }
    }
}

