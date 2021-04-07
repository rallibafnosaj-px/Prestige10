package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by DELL on 11/13/2017.
 */

public class RecyclerViewSyncForInventory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SQLiteDatabase sqlDB;

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    List<GlobalVar> getDataAdapter;

    public RecyclerViewSyncForInventory(List<GlobalVar> getDataAdapter, Context context) {

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_items_sync_inventory, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final MyHolder myHolder = (MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        //myHolder.tv_inventory_item_code.setText("Item Code: "+current.inventoryItemCode);
        /*myHolder.tv_item_name.setText(current.inventoryItemName);
        myHolder.tv_inventory_item_code.setText("Item code: " + current.inventoryItemCode);
        myHolder.tv_inventory_begin_inventory.setText("Beginning inventory: " + current.inventoryBeginInventory);
        myHolder.tv_delivery.setText("Delivery: " + current.inventoryDelivery);
        myHolder.tv_inventory_end_inventory.setText("Ending inventory: " + current.inventoryEndInventory);
        myHolder.tv_inventory_weekno.setText("Week No: " + current.inventoryWeekNo);
        myHolder.tv_item_offtake.setText("Off take: " + current.inventoryOfftake);
        //myHolder.tv_item_type_of_return.setText("Type of return: "+current.inventoryTypeOfReturn);
        myHolder.tv_expired_items.setText("BO / Expired Items: " + current.inventoryExpiredItems);
        myHolder.tv_damaged_items.setText("Damaged Items: " + current.inventoryDamagedItems);
        myHolder.tv_delivery_return.setText("Delivery Returns: " + current.inventoryDeliveryReturns);
        myHolder.tv_customer_returns.setText("Customer Returns: " + current.inventoryCustomerReturns);*/

        myHolder.tv_inventory_weekno.setText("Week No.: "+current.tv_inventory_weekno);
        myHolder.tv_inventory_item_name.setText(""+current.tv_inventory_item_name);
        myHolder.tvBegSA.setText("Beg. Selling Area: "+current.tvBegSA);
        myHolder.tvWHPcs.setText("Beg. Warehouse (Pcs): "+current.tvWHPcs);
        myHolder.tvWHCases.setText("Beg. Warehouse (Cases): "+current.tvWHCases);
        myHolder.tvDeliveryPcs.setText("Delivery (Pcs): "+current.tvDeliveryPcs);
        myHolder.tvDeliveryCases.setText("Delivery (Cases): "+current.tvDeliveryCases);
        myHolder.tvAdjustmentPcs.setText("Adjustment (Pcs): "+current.tvAdjustmentPcs);
        myHolder.tvPullOut.setText("Pull-Out: "+current.tvPullOut);
        myHolder.tvBadOrder.setText("Bad Order: "+current.tvBadOrder);
        myHolder.tvDamagedItemPcs.setText("Damaged Item (Pcs): "+current.tvDamagedItemPcs);
        myHolder.tvExpiredItemsPcs.setText("Expired Item (Pcs): "+current.tvExpiredItemsPcs);
        myHolder.tvEndSellingAreaPcs.setText("Ending Selling Area (Pcs): "+current.tvEndSellingAreaPcs);
        myHolder.tvEndWHPcs.setText("Ending Warehouse (Pcs): "+current.tvEndWHPcs);
        myHolder.tvEndWHCases.setText("Ending Warehouse (Cases): "+current.tvEndWHCases);
        myHolder.tvDaysOS.setText("Days out of Stocks: "+current.tvDaysOS);
        myHolder.tvOfftake.setText("Offtake: "+current.tvOfftake);
        myHolder.tvHomeShelf.setText("Home Shelf: "+current.tvHomeShelf);
        myHolder.tvSecondaryDisplay.setText("Secondary Display: "+current.tvSecondaryDisplay);
        final String sDtrID = current.iInventoryID;
        
        myHolder.btnDeleteDTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                    b.setTitle("Confirm Delete");
                    b.setMessage("Are you sure you want to delete this DTR?");
                    b.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                            sqlDB.execSQL("DELETE FROM dtr WHERE dtrID = '" + sDtrID + "' ");
                            Toast.makeText(context, "DTR deleted!", Toast.LENGTH_SHORT).show();
                            sqlDB.close();
                            getDataAdapter.remove(myHolder.getAdapterPosition());
                            notifyDataSetChanged();
                        }
                    });
                    b.setNegativeButton("CANCEL", null);
                    b.show();

                } catch (Exception e) {
                    Log.i("TAG", "Error in deleting DTR");
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_inventory_weekno, tv_inventory_item_name, tvBegSA, tvWHPcs, tvWHCases,
                tvDeliveryPcs, tvDeliveryCases, tvAdjustmentPcs, tvPullOut, tvBadOrder, tvDamagedItemPcs,
                tvExpiredItemsPcs, tvEndSellingAreaPcs, tvEndWHPcs, tvEndWHCases, tvDaysOS, tvOfftake,
                tvHomeShelf, tvSecondaryDisplay, btnDeleteDTR;

        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            /*tv_inventory_item_code = (TextView) itemView.findViewById(R.id.tv_inventory_item_code);
            tv_inventory_begin_inventory = (TextView) itemView.findViewById(R.id.tv_inventory_begin_inventory);
            tv_inventory_end_inventory = (TextView) itemView.findViewById(R.id.tv_inventory_end_inventory);
            tv_inventory_weekno = (TextView) itemView.findViewById(R.id.tv_inventory_weekno);
            tv_item_name = (TextView) itemView.findViewById(R.id.tv_inventory_item_name);
            tv_item_offtake = (TextView) itemView.findViewById(R.id.tv_inventory_offtake);
            //tv_item_type_of_return = (TextView) itemView.findViewById(R.id.tv_type_of_return);
            tv_delivery = (TextView) itemView.findViewById(R.id.tv_inventory_delivery_inventory);
            //     cardView = (CardView) itemView.findViewById(R.id.cardview1);
            //       cardView.setOnClickListener(this);
            tv_expired_items = (TextView)itemView.findViewById(R.id.tv_expired_items);
            tv_damaged_items = (TextView)itemView.findViewById(R.id.tv_damage_items);
            tv_delivery_return = (TextView)itemView.findViewById(R.id.tv_delivery_returns);
            tv_customer_returns = (TextView)itemView.findViewById(R.id.tv_customer_returns);*/

            tv_inventory_weekno = (TextView) itemView.findViewById(R.id.tv_inventory_weekno);
            tv_inventory_item_name = (TextView) itemView.findViewById(R.id.tv_inventory_item_name);
            tvBegSA = (TextView) itemView.findViewById(R.id.tvBegSA);
            tvWHPcs = (TextView) itemView.findViewById(R.id.tvWHPcs);
            tvWHCases = (TextView) itemView.findViewById(R.id.tvWHCases);
            tvDeliveryPcs = (TextView) itemView.findViewById(R.id.tvDeliveryPcs);
            tvDeliveryCases = (TextView) itemView.findViewById(R.id.tvDeliveryCases);
            tvAdjustmentPcs = (TextView) itemView.findViewById(R.id.tvAdjustmentPcs);
            tvPullOut = (TextView) itemView.findViewById(R.id.tvPullOut);
            tvBadOrder = (TextView) itemView.findViewById(R.id.tvBadOrder);
            tvDamagedItemPcs = (TextView) itemView.findViewById(R.id.tvDamagedItemPcs);
            tvExpiredItemsPcs = (TextView) itemView.findViewById(R.id.tvExpiredItemsPcs);
            tvEndSellingAreaPcs = (TextView) itemView.findViewById(R.id.tvEndSellingAreaPcs);
            tvEndWHPcs = (TextView) itemView.findViewById(R.id.tvEndWHPcs);
            tvEndWHCases = (TextView) itemView.findViewById(R.id.tvEndWHCases);
            tvDaysOS = (TextView) itemView.findViewById(R.id.tvDaysOS);
            tvOfftake = (TextView) itemView.findViewById(R.id.tvOfftake);
            tvHomeShelf = (TextView) itemView.findViewById(R.id.tvHomeShelf);
            tvSecondaryDisplay = (TextView) itemView.findViewById(R.id.tvSecondaryDisplay);

            btnDeleteDTR = (TextView) itemView.findViewById(R.id.btnDeleteDTR);
            btnDeleteDTR.setVisibility(View.VISIBLE);

        }
    }
}