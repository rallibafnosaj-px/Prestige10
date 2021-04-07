package com.quaditsolutions.mmreport;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by DELL on 11/12/2017.
 */

public class RecyclerViewSyncForOSA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewSyncForOSA (List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.recyclerview_items_sync_osa, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        MyHolder myHolder= (MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.tv_osa_item_code.setText(current.osaItemName);
        myHolder.tv_osa.setText("OSA: "+current.osaOsa);
        myHolder.tv_osa_facing.setText("Facing: "+current.osaFacing);
        myHolder.tv_osa_weekNo.setText("Week No: "+current.osaWeekNo);
        myHolder.tvMaxCapacity.setText("Max Capacity: "+current.osaMaxCapacity);

        if (current.osaHomeShelf.equalsIgnoreCase("")){
            myHolder.tv_osa_home_shelf.setText("Home Shelf: 0");
        }else{
            myHolder.tv_osa_home_shelf.setText("Home Shelf: "+current.osaHomeShelf);
        }

        if (current.osaSecShelf.equalsIgnoreCase("")){
            myHolder.tv_osa_secondary_shelf.setText("Secondary Shelf: 0");
        }else{
            myHolder.tv_osa_secondary_shelf.setText("Secondary Shelf: "+current.osaSecShelf);
        }

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_osa_item_code,tv_osa,tv_osa_facing,tv_osa_weekNo,tvMaxCapacity,
                tv_osa_secondary_shelf,tv_osa_home_shelf;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            tv_osa_item_code = (TextView) itemView.findViewById(R.id.tv_osa_item_code);
            tv_osa = (TextView) itemView.findViewById(R.id.tv_osa);
            tv_osa_facing = (TextView) itemView.findViewById(R.id.tv_osa_facing);
            tv_osa_weekNo = (TextView) itemView.findViewById(R.id.tv_osa_weekno);
            tvMaxCapacity = (TextView) itemView.findViewById(R.id.tv_osa_max);
            tv_osa_home_shelf = (TextView) itemView.findViewById(R.id.tv_osa_home_shelf);
            tv_osa_secondary_shelf = (TextView) itemView.findViewById(R.id.tv_osa_secondary_shelf);

            //     cardView = (CardView) itemView.findViewById(R.id.cardview1);
            //       cardView.setOnClickListener(this);
        }
    }
}