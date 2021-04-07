package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewRequestDeviation extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private Context context;
    private LayoutInflater inflater;
    public View layout;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewRequestDeviation(List<GlobalVar> getDataAdapter, Context context)
    {

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view=inflater.inflate(R.layout.recycler_view_store_name, parent,false);
        RecyclerViewRequestDeviation.MyHolder holder=new RecyclerViewRequestDeviation.MyHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position)
    {

        RecyclerViewRequestDeviation.MyHolder myHolder= (RecyclerViewRequestDeviation.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtStoreLocCode.setText(current.storeLocCode);
        myHolder.txtStoreName.setText(current.storeName);
    }

    @Override
    public int getItemCount()
    {
        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView txtStoreLocCode, txtStoreName;
        public CardView cardviewStoreView;

        public MyHolder(View itemView)
        {
            super(itemView);

            txtStoreLocCode = (TextView) itemView.findViewById(R.id.txtStoreLocCode);
            txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);

            cardviewStoreView = (CardView) itemView.findViewById(R.id.cardviewStoreView);
            cardviewStoreView.setOnClickListener(this);
        }

        public void onClick(View view)
        {
            int index = getAdapterPosition();
            GlobalVar globalVar = getDataAdapter.get(index);

            String workingDays = String.valueOf(globalVar.workingDays);
            String storeLocCode = String.valueOf(globalVar.storeLocCode);
            String storeName = String.valueOf(globalVar.storeName);
            String startTime = String.valueOf(globalVar.startTime);
            String endTime = String.valueOf(globalVar.endTime);

            Intent in = new Intent(itemView.getContext(), RequestDeviationChangeActivity.class);

            in.putExtra("workingDays", workingDays);
            in.putExtra("startTime", startTime);
            in.putExtra("endTime", endTime);
            in.putExtra("storeName", storeName);
            in.putExtra("storeLocCode", storeLocCode);

            itemView.getContext().startActivity(in);
        }
    }
}

