package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAdapterItem_OSA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    public List<GlobalVar> getDataAdapter;

    public RecyclerViewAdapterItem_OSA(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recycler_view_layout, parent,false);
        RecyclerViewAdapterItem_OSA.MyHolder holder=new RecyclerViewAdapterItem_OSA.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
        RecyclerViewAdapterItem_OSA.MyHolder myHolder= (RecyclerViewAdapterItem_OSA.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtItemList.setText(current.ItemName);
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
            String itemCat = current.categoryName;

            //Toast.makeText(context, "Hi "+itemCode+" "+itemName, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(itemView.getContext(), OSAActivity.class);
            i.putExtra("itemCategory", itemCat);
            i.putExtra("itemNameOSA", itemName);
            i.putExtra("itemCodeOSA", itemCode);
            itemView.getContext().startActivity(i);

            /*
            Intent in = new Intent(itemView.getContext(), OSAActivity.class);
            in.putExtra("itemCodeOSA", itemCode);
            in.putExtra("itemNameOSA", itemName);
            */

            //Toast.makeText(context, itemCode, Toast.LENGTH_SHORT).show();
            //itemView.getContext().startActivity(in);

        }
    }
    public void setFilter(List<GlobalVar> gv)
    {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(gv);
        notifyDataSetChanged();
    }
}