package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAdapterItem_freshness extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    public List<GlobalVar> getDataAdapter;

    public RecyclerViewAdapterItem_freshness(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recycler_view_layout, parent,false);
        RecyclerViewAdapterItem_freshness.MyHolder holder=new RecyclerViewAdapterItem_freshness.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
        RecyclerViewAdapterItem_freshness.MyHolder myHolder= (RecyclerViewAdapterItem_freshness.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        //myHolder.txtItemList.setText(current.ItemName);

        String itemName = current.categoryName + " " + current.ItemName;
        myHolder.txtItemList.setText(itemName);

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtItemList;
        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            txtItemList = (TextView) itemView.findViewById(R.id.txtItemList);
            cardView = (CardView) itemView.findViewById(R.id.cardview1);
            cardView.setOnClickListener(this);
        }

        public void onClick(View view) {
            int index = getAdapterPosition();

            GlobalVar current = getDataAdapter.get(index);
            String itemCode = current.ItemCode;
            String itemName = current.ItemName;

            Intent in = new Intent(itemView.getContext(), DeliveryActivity.class);
            in.putExtra("itemCode", itemCode);
            in.putExtra("itemName", itemName);
            itemView.getContext().startActivity(in);
        }
    }
    public void setFilter(List<GlobalVar> gv)
    {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(gv);
        notifyDataSetChanged();
    }
}
