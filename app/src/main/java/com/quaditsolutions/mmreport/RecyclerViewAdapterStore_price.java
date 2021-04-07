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

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAdapterStore_price extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewAdapterStore_price(List<GlobalVar> getDataAdapter, Context context){
        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.recycler_view_layout, parent,false);
        RecyclerViewAdapterStore_price.MyHolder holder=new RecyclerViewAdapterStore_price.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewAdapterStore_price.MyHolder myHolder= (RecyclerViewAdapterStore_price.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtItemList.setText(current.storeNamePrice + " " +current.storeLocationPrice);

    }

    @Override
    public int getItemCount() { return getDataAdapter.size(); }

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
            GlobalVar globalVar = getDataAdapter.get(index);
            String storeCode = String.valueOf(globalVar);

            Intent in = new Intent(itemView.getContext(), PriceItemActivity.class);
            //in.putExtra("storeCodePrice", storeCode);
            globalVar.storeCodePrice = storeCode;
            itemView.getContext().startActivity(in);

        }
    }
}