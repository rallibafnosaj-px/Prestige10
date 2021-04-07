package com.quaditsolutions.mmreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAdapterItem_price extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewAdapterItem_price(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recycler_view_item_price_change, parent,false);
        RecyclerViewAdapterItem_price.MyHolder holder=new RecyclerViewAdapterItem_price.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewAdapterItem_price.MyHolder myHolder= (RecyclerViewAdapterItem_price.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtItemName.setText(current.pcItemName);

        DecimalFormat df = new DecimalFormat("0.00");
        String priceWithPeso = "₱ "+String.valueOf(df.format(current.pcLastPrice2));
        myHolder.txtItemPrice.setText(priceWithPeso);

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtItemName;
        public TextView txtItemPrice;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            txtItemName = (TextView) itemView.findViewById(R.id.price_change_item_name);
            txtItemPrice = (TextView) itemView.findViewById(R.id.price_change_item_price);
            cardView = (CardView) itemView.findViewById(R.id.cardview_item);
            cardView.setOnClickListener(this);
        }

        public void onClick(View view) {
            int index = getAdapterPosition();
            GlobalVar globalVar = getDataAdapter.get(index);
            String item_name = String.valueOf(globalVar.pcItemName);
            String item_id = String.valueOf(globalVar.pcItemID);
            double last_price = globalVar.pcLastPrice2;


            Intent in = new Intent(itemView.getContext(), PriceChangeActivity.class);
            in.putExtra("itemID", item_id);
            in.putExtra("itemName", item_name);
            in.putExtra("lastPrice", last_price);

            itemView.getContext().startActivity(in);
            ((Activity)context).finish();
        }
    }

    public void setFilter(List<GlobalVar> gv)
    {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(gv);
        notifyDataSetChanged();
    }
}
