package com.quaditsolutions.mmreport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.LightingColorFilter;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewInventoryItemList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String sSellingAreaPcs, sWarehousePcs, sWarehouseCases, sDeliveryPcs,
            sDeliveryCases, sAdjustment, sPulloutPcs, sBadOrderPcs,
            sDamagedItemPcs, sExpiredItemsPcs, sEndSellingAreaPcs,
            sEndWarehousePcs, sEndWarehouseCases, sOfftake, sDaysOutOfStocks,
            sHomeShelf, sSecondaryDisplay, sStoreLocName;

    private RadioButton lastCheckedRB = null;
    private SQLiteDatabase sqlDB;
    private SQLiteStatement sqLiteStatement;

    private Context context;
    private LayoutInflater inflater;

    public TextView content;
    public View layout;

    public List<GlobalVar> getDataAdapter;

    String sedtSellingArea, sedtWarehouseInCases, sedtAdjustment, sedtDelivery, sedtPullout, sedtBO,
            sedtDamagedItems, sedtExpiredItems, sedtEndSellingArea, sedtEndWarehouseInCases, sedtDeliveryCases,
            sedtEndWarehouseInPcs;

    Integer iBegSA, iBegWH, iDel, iAdj, iPO, iBO, iDI, iEI, iEndSA, iEndWH, iDelCases;

    String sOutOfStocks;
    RadioButton rbSelectedStocks;
    private RadioGroup lastCheckedRadioGroup = null;

    public RecyclerViewInventoryItemList(List<GlobalVar> getDataAdapter, Context context) {

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cardview_item_list_with_inventory, parent, false);
        RecyclerViewInventoryItemList.MyHolder holder = new RecyclerViewInventoryItemList.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final RecyclerViewInventoryItemList.MyHolder myHolder = (RecyclerViewInventoryItemList.MyHolder) holder;
        final GlobalVar current = getDataAdapter.get(position);

        final String itemName = String.valueOf(position + 1) + "). " + current.invCategoryName + " " +
                current.invItemName + "\n(" + current.invItemCode + ")";
        myHolder.txtItemList.setText(itemName);

        /*try {
            String outOfStocks = "";
            outOfStocks = current.invOutOfStocks;
            Log.i("TAG", "String Out of Stocks: " + outOfStocks);
            if (outOfStocks.equalsIgnoreCase("null")) {
                sOutOfStocks = "NO";
                myHolder.rdbStocksNo.setChecked(true);
                myHolder.rdbStocksYes.setChecked(false);
            } else if (outOfStocks.equalsIgnoreCase("NO")) {
                sOutOfStocks = "NO";
                myHolder.rdbStocksNo.setChecked(true);
                myHolder.rdbStocksYes.setChecked(false);
            } else if (outOfStocks.equalsIgnoreCase("YES")) {
                sOutOfStocks = "YES";
                myHolder.rdbStocksNo.setChecked(false);
                myHolder.rdbStocksYes.setChecked(true);
            } else {
                sOutOfStocks = "NO";
                myHolder.rdbStocksNo.setChecked(true);
                myHolder.rdbStocksYes.setChecked(false);
            }
        } catch (Exception e) {
            Log.i("TAG", "Error in getting stocks: " + e);
        }*/

        // set beginning inventory
        myHolder.edtSellingArea.setText(current.invBegSA);
        myHolder.edtWarehouseInCases.setText(current.invBegWH);
        myHolder.edtWarehouseInPcs.setText(current.invBegWHPcs);

        myHolder.edtCaseQty.setText(current.invCaseQty);

        // set delivery
        myHolder.edtDelivery.setText("");
        myHolder.edtAdjustment.setText("");

        // set returns
        myHolder.edtPullout.setText("");
        myHolder.edtBO.setText("");
        myHolder.edtDamagedItems.setText("");
        myHolder.edtExpiredItems.setText("");

        myHolder.edtEndSellingArea.setText("");
        myHolder.edtEndWarehouseInPcs.setText("");
        myHolder.edtEndWarehouseInCases.setText("");

        // text change listeners
        myHolder.edtSellingArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myHolder.edtWarehouseInCases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {

                } catch (Exception e) {
                    Log.i("TAG", "Error in computing ending warehouse: " + e);
                }
            }
        });

        myHolder.edtDelivery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        myHolder.edtCaseQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myHolder.edtAdjustment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        myHolder.edtPullout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myHolder.edtBO.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myHolder.edtDamagedItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myHolder.edtExpiredItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*
        myHolder.radioGroupStocks.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = checked_rb;

                Toast.makeText(context, "Out of Stocks: " +
                        lastCheckedRB.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                sOutOfStocks = lastCheckedRB.getText().toString().trim();

            }
        });
        */

        myHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*myHolder.radioGroupStocks.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);
                        if (lastCheckedRB != null) {
                            lastCheckedRB.setChecked(false);
                        }
                        lastCheckedRB = checked_rb;

                        Toast.makeText(context, "Out of Stocks: " + lastCheckedRB, Toast.LENGTH_SHORT).show();

                    }
                });*/

                /*int selectedRadioButtonID = radioGroupStocks.getCheckedRadioButtonId();

                Log.i("TAG","Selected Radio ID: "+selectedRadioButtonID);
                if (selectedRadioButtonID != -1) {

                    RadioButton rbSelectedStock = (RadioButton).findViewById(selectedRadioButtonID);
                    sOutOfStocks = rbSelectedStock.getText().toString().trim();

                    Toast.makeText(context, "Out of Stocks: " + sOutOfStocks, Toast.LENGTH_SHORT).show();

                }*/

                sStoreLocName = current.invStoreName;

                sSellingAreaPcs = myHolder.edtSellingArea.getText().toString().trim();
                sWarehousePcs = myHolder.edtWarehouseInPcs.getText().toString().trim();
                sWarehouseCases = myHolder.edtWarehouseInCases.getText().toString().trim();
                if (sSellingAreaPcs.equalsIgnoreCase("")){
                    sSellingAreaPcs = "0";
                }
                if (sWarehousePcs.equalsIgnoreCase("")){
                    sWarehousePcs = "0";
                }
                if (sWarehouseCases.equalsIgnoreCase("")){
                    sWarehouseCases = "0";
                }

                sDeliveryPcs = myHolder.edtDelivery.getText().toString().trim();
                sDeliveryCases = myHolder.edtDeliveryCases.getText().toString().trim();
                sAdjustment = myHolder.edtAdjustment.getText().toString().trim();
                if (sDeliveryPcs.equalsIgnoreCase("")){
                    sDeliveryPcs = "0";
                }
                if (sDeliveryCases.equalsIgnoreCase("")){
                    sDeliveryCases = "0";
                }
                if (sAdjustment.equalsIgnoreCase("")){
                    sAdjustment = "0";
                }

                sPulloutPcs = myHolder.edtPullout.getText().toString().trim();
                sBadOrderPcs = myHolder.edtBO.getText().toString().trim();
                sDamagedItemPcs = myHolder.edtDamagedItems.getText().toString().trim();
                sExpiredItemsPcs = myHolder.edtExpiredItems.getText().toString().trim();
                if (sPulloutPcs.equalsIgnoreCase("")){
                    sPulloutPcs = "0";
                }
                if (sBadOrderPcs.equalsIgnoreCase("")){
                    sBadOrderPcs = "0";
                }
                if (sDamagedItemPcs.equalsIgnoreCase("")){
                    sDamagedItemPcs = "0";
                }
                if (sExpiredItemsPcs.equalsIgnoreCase("")){
                    sExpiredItemsPcs = "0";
                }

                sEndSellingAreaPcs = myHolder.edtEndSellingArea.getText().toString().trim();
                sEndWarehousePcs = myHolder.edtEndWarehouseInPcs.getText().toString().trim();
                sEndWarehouseCases = myHolder.edtEndWarehouseInCases.getText().toString().trim();
                if (sEndSellingAreaPcs.equalsIgnoreCase("")){
                    sEndSellingAreaPcs = "0";
                }
                if (sEndWarehousePcs.equalsIgnoreCase("")){
                    sEndWarehousePcs = "0";
                }
                if (sEndWarehouseCases.equalsIgnoreCase("")){
                    sEndWarehouseCases = "0";
                }

                sDaysOutOfStocks = myHolder.etDaysOS.getText().toString().trim();
                sHomeShelf = myHolder.edtHomeShelf.getText().toString().trim();
                sSecondaryDisplay = myHolder.edtSecondaryDisplay.getText().toString().trim();
                if (sDaysOutOfStocks.equalsIgnoreCase("")){
                    sDaysOutOfStocks = "0";
                }
                if (sHomeShelf.equalsIgnoreCase("")){
                    sHomeShelf = "0";
                }
                if (sSecondaryDisplay.equalsIgnoreCase("")){
                    sSecondaryDisplay = "0";
                }

                sOfftake = myHolder.etOfftake.getText().toString().trim();
                if (sOfftake.equalsIgnoreCase("")){
                    sOfftake = "0";
                }

                // ending
                sedtEndSellingArea = myHolder.edtEndSellingArea.getText().toString().trim();
                sedtEndWarehouseInCases = myHolder.edtEndWarehouseInCases.getText().toString().trim();
                sedtEndWarehouseInPcs = myHolder.edtEndWarehouseInPcs.getText().toString().trim();
                if (sedtEndSellingArea.equalsIgnoreCase("")){
                    sedtEndSellingArea = "0";
                }
                if (sedtEndWarehouseInCases.equalsIgnoreCase("")){
                    sedtEndWarehouseInCases = "0";
                }
                if (sedtEndWarehouseInPcs.equalsIgnoreCase("")){
                    sedtEndWarehouseInPcs = "0";
                }

                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Confirm Save");
                b.setMessage("Are you sure you want to save this?" + "" +

                        "\n\n" + itemName + "" +

                        "\n\nStore: " + sStoreLocName +

                        "\n\nBEGINNING INVENTORY" + "" +
                        "\nSelling Area (Pcs.): " + sSellingAreaPcs +
                        "\nWarehouse (Pcs): " + sWarehousePcs +
                        "\nWarehouse (Cases): " + sWarehouseCases +

                        "\n\nDELIVERY" +
                        "\nDelivery (Pcs.): " + sDeliveryPcs +
                        "\nDelivery (Cases): " + sDeliveryCases +
                        "\nAdjustment (Pcs): " + sAdjustment +

                        "\n\nRETURNS" +
                        "\nPull-Out (Pcs.): " + sPulloutPcs +
                        "\nBad Order (Pcs): " + sBadOrderPcs +
                        "\nDamaged Item (Pcs): " + sDamagedItemPcs +
                        "\nExpired Item (Pcs): " + sExpiredItemsPcs +

                        "\n\nENDING INVENTORY" + "" +
                        "\nSelling Area (Pcs.): " + sedtEndSellingArea +
                        "\nWarehouse (Pcs): " + sedtEndWarehouseInPcs +
                        "\nWarehouse (Cases): " + sedtEndWarehouseInCases +

                        "\n\nMAX CAPACITY " +
                        "\nHome Shelf: " + sHomeShelf +
                        "\nSecondary Display: " + sSecondaryDisplay +"\n\n");

                //"\nOut of Stocks: " + sOutOfStocks);

                b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                            sqlDB.beginTransaction();

                            // update item tbl list
                            sqlDB.execSQL("UPDATE assignment " +
                                    "SET beginningSA = '" + sedtEndSellingArea + "', " +
                                    "beginningWHPcs = '" + sedtEndWarehouseInPcs + "', " +
                                    "beginningWH = '" + sedtEndWarehouseInCases + "' " +
                                    //"outOfStocks = '" + sOutOfStocks + "' " +
                                    "WHERE itemCode = '" + current.invItemCode + "' ");

                            // record inventory to inventory transaction
                            // Toast.makeText(context, ""+current.invItemCode, Toast.LENGTH_SHORT).show();

                            String insertInventory = "" +
                                    "INSERT INTO inventory " +
                                    "(userCode, " +
                                    "itemCode, " +
                                    "itemName, " +
                                    "storeCode, " +
                                    "weekNo," +

                                    // beginning
                                    "inventoryDate," +
                                    "sellingAreaPcs," +
                                    "warehousePcs," +
                                    "warehouseCases," +

                                    // deliver
                                    "deliveryPcs," +
                                    "deliveryCases," +
                                    "adjustmentPcs," +

                                    // returns
                                    "pulloutPcs," +
                                    "badOrderPcs," +
                                    "damagedItemPcs," +
                                    "expiredItemsPcs," +

                                    // ending
                                    "endSellingAreaPcs," +
                                    "endWarehousePcs," +
                                    "endWarehouseCases," +
                                    "offtake," +
                                    "syncStatus," +

                                    // max cap
                                    "daysOutOfStocks," +
                                    "homeShelf," +
                                    "secondaryDisplay) " +

                                    "VALUES(" +
                                    "?,?,?,?,?," +
                                    "?,?,?,?,?," +
                                    "?,?,?,?,?," +
                                    "?,?,?,?,?," +
                                    "?,?,?,?)";

                            SQLiteStatement s = sqlDB.compileStatement(insertInventory);

                            s.bindString(1, current.invUserCode);
                            s.bindString(2, current.invItemCode);
                            s.bindString(3, current.invCategoryName + " " + current.invItemName);
                            s.bindString(4, current.invStoreCode);
                            s.bindString(5, current.invWeekNo);
                            s.bindString(6, current.invInventoryDate);

                            // Beginning
                            if (sSellingAreaPcs.equalsIgnoreCase("")) {
                                sSellingAreaPcs = "0";
                            }
                            s.bindString(7, sSellingAreaPcs);

                            if (sWarehousePcs.equalsIgnoreCase("")) {
                                sWarehousePcs = "0";
                            }
                            s.bindString(8, sWarehousePcs);

                            if (sWarehouseCases.equalsIgnoreCase("")) {
                                sWarehouseCases = "0";
                            }
                            s.bindString(9, sWarehouseCases);

                            // Delivery
                            if (sDeliveryPcs.equalsIgnoreCase("")) {
                                sDeliveryPcs = "0";
                            }
                            s.bindString(10, sDeliveryPcs);

                            if (sDeliveryCases.equalsIgnoreCase("")) {
                                sDeliveryCases = "0";
                            }
                            s.bindString(11, sDeliveryCases);

                            if (sAdjustment.equalsIgnoreCase("")) {
                                sAdjustment = "0";
                            }
                            s.bindString(12, sAdjustment);

                            // Returns
                            if (sPulloutPcs.equalsIgnoreCase("")) {
                                sPulloutPcs = "0";
                            }
                            s.bindString(13, sPulloutPcs);

                            if (sBadOrderPcs.equalsIgnoreCase("")) {
                                sBadOrderPcs = "0";
                            }
                            s.bindString(14, sBadOrderPcs);

                            if (sDamagedItemPcs.equalsIgnoreCase("")) {
                                sDamagedItemPcs = "0";
                            }
                            s.bindString(15, sDamagedItemPcs);

                            if (sExpiredItemsPcs.equalsIgnoreCase("")) {
                                sExpiredItemsPcs = "0";
                            }
                            s.bindString(16, sExpiredItemsPcs);

                            // Ending Inventory
                            if (sEndSellingAreaPcs.equalsIgnoreCase("")) {
                                sEndSellingAreaPcs = "0";
                            }
                            s.bindString(17, sEndSellingAreaPcs);

                            if (sEndWarehousePcs.equalsIgnoreCase("")) {
                                sEndWarehousePcs = "0";
                            }
                            s.bindString(18, sEndWarehousePcs);

                            if (sEndWarehouseCases.equalsIgnoreCase("")) {
                                sEndWarehouseCases = "0";
                            }
                            s.bindString(19, sEndWarehouseCases);

                            if (sOfftake.equalsIgnoreCase("")) {
                                sOfftake = "0";
                            }
                            s.bindString(20, sOfftake);

                            s.bindString(21, "not sync");

                            // max cap

                            if (sDaysOutOfStocks.equalsIgnoreCase("")) {
                                sDaysOutOfStocks = "0";
                            }
                            s.bindString(22, sDaysOutOfStocks);

                            if (sHomeShelf.equalsIgnoreCase("")) {
                                sHomeShelf = "0";
                            }
                            s.bindString(23, sHomeShelf);

                            if (sSecondaryDisplay.equalsIgnoreCase("")) {
                                sSecondaryDisplay = "0";
                            }
                            s.bindString(24, sSecondaryDisplay);

                            s.executeInsert();

                            Toast.makeText(context, "Item Saved!", Toast.LENGTH_SHORT).show();

                            sqlDB.setTransactionSuccessful();
                            sqlDB.endTransaction();
                            sqlDB.close();
                        } catch (Exception e) {
                            Log.i("TAG", "Error in saving inventory: " + e);
                        }
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtItemList, tvOfftake;
        public CardView cardView;
        EditText edtSellingArea, edtWarehouseInCases, edtDelivery, edtAdjustment, edtPullout, edtBO,
                edtDamagedItems, edtExpiredItems, edtEndSellingArea, edtEndWarehouseInCases,
                edtCaseQty, edtWarehouseInPcs, edtEndWarehouseInPcs, edtDeliveryCases,
                etOfftake, etDaysOS, edtHomeShelf, edtSecondaryDisplay;

        Button btnSave;
        RadioButton rdbStocksYes, rdbStocksNo;
        RadioGroup radioGroupStocks;

        public MyHolder(View itemView) {

            super(itemView);

            txtItemList = (TextView) itemView.findViewById(R.id.txtItemList);
            cardView = (CardView) itemView.findViewById(R.id.cardview1);
            cardView.setOnClickListener(this);

            edtSellingArea = (EditText) itemView.findViewById(R.id.edtSellingArea);
            edtWarehouseInPcs = (EditText) itemView.findViewById(R.id.edtWarehouseInPcs);
            edtWarehouseInCases = (EditText) itemView.findViewById(R.id.edtWarehouseInCases);

            edtDelivery = (EditText) itemView.findViewById(R.id.edtDelivery);
            edtDeliveryCases = (EditText) itemView.findViewById(R.id.edtDeliveryCases);
            edtAdjustment = (EditText) itemView.findViewById(R.id.edtAdjustment);

            edtPullout = (EditText) itemView.findViewById(R.id.edtPullout);
            edtBO = (EditText) itemView.findViewById(R.id.edtBO);
            edtDamagedItems = (EditText) itemView.findViewById(R.id.edtDamagedItems);
            edtExpiredItems = (EditText) itemView.findViewById(R.id.edtExpiredItems);

            edtEndSellingArea = (EditText) itemView.findViewById(R.id.edtEndSellingArea);
            edtEndWarehouseInCases = (EditText) itemView.findViewById(R.id.edtEndWarehouseInCases);
            edtEndWarehouseInPcs = (EditText) itemView.findViewById(R.id.edtEndWarehouseInPcs);
            edtCaseQty = (EditText) itemView.findViewById(R.id.edtDeliveryCases);

            tvOfftake = (TextView) itemView.findViewById(R.id.tvOfftake);
            etOfftake = (EditText) itemView.findViewById(R.id.etOfftake);

            btnSave = (Button) itemView.findViewById(R.id.btnSave);
            etDaysOS = (EditText) itemView.findViewById(R.id.etDaysOS);
            edtHomeShelf = (EditText) itemView.findViewById(R.id.edtHomeShelf);
            edtSecondaryDisplay = (EditText) itemView.findViewById(R.id.edtSecondaryDisplay);

            /*radioGroupStocks = (RadioGroup) itemView.findViewById(R.id.radioGroupStocks);
            rdbStocksYes = (RadioButton) itemView.findViewById(R.id.rdbStocksYes);
            rdbStocksNo = (RadioButton) itemView.findViewById(R.id.rdbStocksNo);
            */

            // get selected stocks
            //int selectedStocksYesNo = radioGroupStocks.getCheckedRadioButtonId();
            //rbSelectedStocks = (RadioButton) itemView.findViewById(selectedStocksYesNo);

            //sOutOfStocks = rbSelectedStocks.getText().toString().trim();

            /*radioGroupStocks.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (lastCheckedRadioGroup != null
                            && lastCheckedRadioGroup.getCheckedRadioButtonId()
                            != radioGroupStocks.getCheckedRadioButtonId()
                            && lastCheckedRadioGroup.getCheckedRadioButtonId() != -1) {
                        lastCheckedRadioGroup.clearCheck();
                        Toast.makeText(context, "Out of Stocks: " +
                                radioGroupStocks.getCheckedRadioButtonId(), Toast.LENGTH_SHORT).show();
                        sOutOfStocks = String.valueOf(radioGroupStocks.getCheckedRadioButtonId());
                    }
                    lastCheckedRadioGroup = radioGroupStocks;
                }
            });*/

        }

        public void onClick(View view) {

        }
    }

    public void setFilter(List<GlobalVar> gv) {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(gv);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
