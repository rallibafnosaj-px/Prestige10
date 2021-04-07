package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAdapterItem_nearlyExpired extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    public List<GlobalVar> getDataAdapter;

    public RecyclerViewAdapterItem_nearlyExpired(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recycler_view_layout, parent,false);
        RecyclerViewAdapterItem_nearlyExpired.MyHolder holder=new RecyclerViewAdapterItem_nearlyExpired.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewAdapterItem_nearlyExpired.MyHolder myHolder= (RecyclerViewAdapterItem_nearlyExpired.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtItemList.setText(current.ItemName+"\nExp. Date: "+current.rtvExpDate);
    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtItemList,tvRTVExpDate;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            txtItemList = (TextView) itemView.findViewById(R.id.txtItemList);
            tvRTVExpDate = (TextView) itemView.findViewById(R.id.tvRTVExpDate);
            cardView = (CardView) itemView.findViewById(R.id.cardview1);
            cardView.setOnClickListener(this);
        }

        public void onClick(View view) {
            int index = getAdapterPosition();

            GlobalVar current = getDataAdapter.get(index);
            String itemCode = current.ItemCode;
            String itemName = current.ItemName;
            String rtvExpDate = current.rtvExpDate;

            Intent in = new Intent(itemView.getContext(), NearlyToExpiredActivity.class);
            in.putExtra("itemCodeNE", itemCode);
            in.putExtra("itemNameNE", itemName);
            in.putExtra("itemRTVExpDate", rtvExpDate);
            itemView.getContext().startActivity(in);
        }
    }
    public void setFilter(List<GlobalVar> countryModels)
    {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(countryModels);
        notifyDataSetChanged();
    }
}
