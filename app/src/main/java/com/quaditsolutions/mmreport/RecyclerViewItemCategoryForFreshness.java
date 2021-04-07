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
 * Created by Khyrz on 9/23/2017.
 */

public class RecyclerViewItemCategoryForFreshness extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewItemCategoryForFreshness(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recycler_view_layout, parent,false);
        RecyclerViewItemCategoryForFreshness.MyHolder holder=new RecyclerViewItemCategoryForFreshness.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        RecyclerViewItemCategoryForFreshness.MyHolder myHolder= (RecyclerViewItemCategoryForFreshness.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.txtItemList.setText(current.categoryName);
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
            GlobalVar globalVar = getDataAdapter.get(index);
            //String itemCode = String.valueOf(globalVar.ItemCode);
            String categoryName = String.valueOf(globalVar.categoryName);

            Intent in = new Intent(itemView.getContext(), FreshnessItemList.class);
            //in.putExtra("itemCode", itemCode);
            in.putExtra("categoryName", categoryName);
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

