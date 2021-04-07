package com.quaditsolutions.mmreport;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DELL on 11/12/2017.
 */

// for pending sync card
public class RecyclerViewSyncForFreshness extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    public View layout;
    List<GlobalVar> getDataAdapter;

    public RecyclerViewSyncForFreshness (List<GlobalVar> getDataAdapter, Context context){
        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.recyclerview_items_sync_freshness, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        //myHolder.tv_freshness_delivery_date.setText("Delivery Date: "+current.freshness_deliveryDate);
        myHolder.tv_freshness_item_code.setText("Item Code: "+current.freshness_itemCode);
        myHolder.tv_freshness_quantity.setText("Quantity: "+current.freshness_quantity);
        myHolder.tvProductName.setText(""+current.product_name);
        myHolder.tvStoreName.setText("Store Name: "+current.freshness_store_name);
        myHolder.tvUnitOfMeasure.setText("Unit of Measure: "+current.freshness_unit_of_measure);
        myHolder.tvProductionDate.setText("Production Date: "+current.freshness_production_date);
        myHolder.tvExpirationDate.setText("Expiration Date: "+current.freshness_expiration_date);

        String strLotNo = current.freshness_lot_no;
        if(strLotNo.equalsIgnoreCase("")){

        }else{
            myHolder.tvLotNo.setText("Lot No.: "+current.freshness_lot_no);
        }

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_freshness_delivery_date,tv_freshness_item_code,tv_freshness_quantity,
                tvProductName, tvStoreName, tvQty, tvUnitOfMeasure, tvProductionDate, tvExpirationDate,
                tvLotNo;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            tv_freshness_delivery_date = (TextView) itemView.findViewById(R.id.tv_freshness_delivery_date);
            tv_freshness_item_code = (TextView) itemView.findViewById(R.id.tv_freshness_item_code);
            tv_freshness_quantity = (TextView) itemView.findViewById(R.id.tv_freshness_quantity);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            //tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvUnitOfMeasure = (TextView) itemView.findViewById(R.id.tvUnitOfMeasure);
            tvProductionDate = (TextView) itemView.findViewById(R.id.tvProductionDate);
            tvExpirationDate = (TextView) itemView.findViewById(R.id.tvExpirationDate);
            tvLotNo = (TextView) itemView.findViewById(R.id.tvLotNo);

            //     cardView = (CardView) itemView.findViewById(R.id.cardview1);
            //       cardView.setOnClickListener(this);
        }
    }
}