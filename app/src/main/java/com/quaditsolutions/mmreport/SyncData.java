package com.quaditsolutions.mmreport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Khyrz on 11/13/2017.
 */

public class SyncData extends Activity {

    SQLiteDatabase sqlDB;
    Cursor cursor;
    GlobalVar gv;
    String userCode, dtrID, storeLocation, TAG = "Response", osaID, fullAddress, sRTVID,
            currentDateTime, currentIPAddress;
    SoapPrimitive resultCheckInOut, resultLocalExpense, resultInventory, resultOSA,
            resultFreshness, resultNearly;
    Geocoder geocoder;
    List<Address> addressList;
    double longitude, latitude;
    ProgressDialog progressDialog;

    public void onStart(Context context) {

        SharedPreferences sp = context.getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        storeLocation = sp.getString("storeLocation", null);
        currentIPAddress = sp.getString("ipAddress",null);
        //Log.i(TAG, userCode);

        //Open database
        sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        //Log.d(TAG,sqlDB.toString());

        try {
            geocoder = new Geocoder(context, Locale.getDefault());

            //Sync...
            mainAsyncTask mat = new mainAsyncTask();
            mat.execute();
        } catch (Exception e) {

        }
    }

    //Check in/out
    public class mainAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(SyncData.this);
            progressDialog.setTitle("Syncing data to server");
            progressDialog.setMessage("Please wait while syncing . . .");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(TAG, "doInBackground");

            try {

                CheckInOut();
                Expense();
                Inventory();
                OSA();
                Freshness();
                NearlyExpired(); // RTV

            } catch (Exception e) {

                // Record Error Logs
                /*Date currentDateTime = Calendar.getInstance().getTime();

                String errMsg = e.toString().replaceAll("`"," ");

                sqlDB.execSQL("INSERT INTO logsTbl" +
                        "(empNo, " +
                        "dateTime, " +
                        "log) " +
                        "VALUES" +
                        "('"+userCode+"','"+currentDateTime+"'," +
                        "'"+errMsg+"');");
                */
                // End error logs record

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Log.i(TAG, "onPostExecute-" + resultCheckInOut.toString());
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                if (resultCheckInOut.toString().isEmpty()) {
                    Log.d(TAG, "NULLLLLLLLL");
                } else if (resultCheckInOut.toString().equals("SuccessIN")) {
                    sqlDB.execSQL("UPDATE dtr " +
                            "SET statusIN='sync' " +
                            "WHERE userCode='" + userCode + "' " +
                            "AND dtrID='" + dtrID + "' ");
                    Log.i(TAG, "CheckInOut SuccessIN - UPDATED");
                } else if (resultCheckInOut.toString().equals("SuccessOUT")) {
                    sqlDB.execSQL("DELETE FROM dtr  " +
                            "WHERE dtrID='" + dtrID + "' ");

                    Log.i(TAG, "CheckInOut SuccessOUT - DELETED " + dtrID);

                 /*   sqlDB.execSQL("UPDATE dtr " +
                            "SET statusOUT='sync' " +
                            "WHERE userCode='"+userCode+"' " +
                            "AND dtrID='"+dtrID+"'");

                    Log.i(TAG, "SuccessOUT - UPDATED"); */
                } else if (resultCheckInOut.toString().equals("SuccessINOUT")) {
                    sqlDB.execSQL("DELETE FROM dtr  " +
                            "WHERE dtrID='" + dtrID + "' ");

                    Log.i(TAG, "CheckInOut SuccessINOUT - DELETED" + dtrID);
                } else if (resultLocalExpense.toString().equals("Success")) {
                    Log.i(TAG, "Expense - UPDATED");
                } else if (resultInventory.toString().equals("Success")) {
                    Log.i(TAG, "Inventory - UPDATED");
                } else if (resultOSA.toString().equals("Success")) {
                    sqlDB.execSQL("DELETE FROM osa  " +

                            "WHERE osaID='" + osaID + "' ");
                    Log.i(TAG, "OSA - UPDATED" + osaID);
                } else if (resultFreshness.toString().equals("Success")) {
                    Log.i(TAG, "Freshness - UPDATED");
                } else if (resultNearly.toString().equals("Success")) {
                    sqlDB.execSQL("UPDATE nearlytoexpired " +
                            "SET syncStatus='sync' " +
                            "WHERE nteID ='" + sRTVID + "'");

                    Log.i(TAG, "Nearly Expired - UPDATED");
                } else {
                    Log.i(TAG, "FAILED TO UPDATE");
                }
            } catch (Exception e) {
                Log.d(TAG + " - All", e.toString());
            }
        }
    }

    public void CheckInOut() {

        String SOAP_ACTION = "http://tempuri.org/syncDateTimeDataJSON";
        String METHOD_NAME = "syncDateTimeDataJSON";
        String NAMESPACE = "http://tempuri.org/";


        Cursor curs = sqlDB.rawQuery("SELECT " +
                "selfieNetworkStatusIN, " +
                "selfieNetworkStatusOUT, " +
                "timeIn, " +
                "timeOut, " +
                "dateIn, " +
                "dateOut, " +
                "locationLatIn, " +
                "locationLatOut, " +
                "locationLongIn, " +
                "locationLongOut, " +
                "dtrID, " +
                "addressIn, " +
                "addressOut, " +
                "storeLocCode, " +
                "imageIn, " +
                "imageOut " +
                "FROM dtr " +
                "WHERE (statusIN=? " +
                "OR statusOUT=?) " +
                "AND userCode=?" +
                "ORDER BY dateIn, timeIn", new String[]{"not sync", "not sync", userCode});


        if (curs.getCount() != 0) {
            progressDialog.setMessage("Syncing DTR . . .");
            //Log.d(TAG, "CheckInOut: "+curs.getCount());
            if (curs.moveToFirst()) {
                do {
                    /**String timeIn =  curs.getString(curs.getColumnIndex("timeIn"));
                     //Toast.makeText(this, ""+timeIn, Toast.LENGTH_SHORT).show();
                     String timeOut = "";
                     String dateIn = "";
                     String dateOut = "";
                     String locationLatIn = "";
                     String locationLatOut = "";
                     String locationLongIn = "";
                     String locationLongOut = "";
                     dtrID = "0";
                     String AddressIn = "";
                     String AddressOut = "";
                     String storeLocCode = "";
                     String imageIn = "";
                     String imageOut = "";
                     //String syncStatus = curs.getString(curs.getColumnIndex("syncStatus"));
                     **/

                    try {
                        String selfieNetworkStatusIN = curs.getString(curs.getColumnIndex("selfieNetworkStatusIN"));
                        String selfieNetworkStatusOUT = curs.getString(curs.getColumnIndex("selfieNetworkStatusOUT"));
                        String timeIn = curs.getString(curs.getColumnIndex("timeIn"));
                        String timeOut = curs.getString(curs.getColumnIndex("timeOut"));
                        String dateIn = curs.getString(curs.getColumnIndex("dateIn"));
                        String dateOut = curs.getString(curs.getColumnIndex("dateOut"));
                        String locationLatIn = curs.getString(curs.getColumnIndex("locationLatIn"));
                        String locationLatOut = curs.getString(curs.getColumnIndex("locationLatOut"));
                        String locationLongIn = curs.getString(curs.getColumnIndex("locationLongIn"));
                        String locationLongOut = curs.getString(curs.getColumnIndex("locationLongOut"));
                        dtrID = curs.getString(curs.getColumnIndex("dtrID"));
                        String AddressIn = curs.getString(curs.getColumnIndex("addressIn"));
                        String AddressOut = curs.getString(curs.getColumnIndex("addressOut"));
                        String storeLocCode = curs.getString(curs.getColumnIndex("storeLocCode"));
                        String imageIn = curs.getString(curs.getColumnIndex("imageIn"));
                        String imageOut = curs.getString(curs.getColumnIndex("imageOut"));
                        //String syncStatus = curs.getString(curs.getColumnIndex("syncStatus"));

                        // send data to database

                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        try {
                            addressList = geocoder.getFromLocation
                                    (Double.parseDouble(locationLatIn), Double.parseDouble(locationLongIn), 1);

                            String addressStr = addressList.get(0).getAddressLine(0);
                            fullAddress = addressStr;

                        } catch (Exception e) {
                            Log.i("TAG", "Error in converting LongLat to address: " + e);
                        }
                        if (fullAddress.equalsIgnoreCase("")) {
                            Request.addProperty("addressIn", AddressIn);
                        } else {
                            Request.addProperty("addressIn", fullAddress);
                            Log.i("TAG", "ADDRESS IN: " + fullAddress);
                        }

                        //Request.addProperty("email", "");
                        Request.addProperty("employeeNo", userCode);
                        Request.addProperty("selfieNetworkStatusIN", selfieNetworkStatusIN);
                        Request.addProperty("selfieNetworkStatusOUT", selfieNetworkStatusOUT);
                        Request.addProperty("logInDate", dateIn);
                        Request.addProperty("logOutDate", dateOut);
                        Request.addProperty("logInTime", timeIn);
                        Request.addProperty("logOutTime", timeOut);

                        try {
                            addressList = geocoder.getFromLocation
                                    (Double.parseDouble(locationLatOut), Double.parseDouble(locationLongOut), 1);
                            String addressStr = addressList.get(0).getAddressLine(0);
                            fullAddress = addressStr;
                        } catch (Exception e) {
                            Log.i("TAG", "Error in converting LongLat to address: " + e);
                        }
                        if (fullAddress.equalsIgnoreCase("")) {
                            Request.addProperty("addressOut", AddressOut);
                        } else {
                            Request.addProperty("addressOut", fullAddress);
                            Log.i("TAG", "ADDRESS OUT: " + fullAddress);
                        }

                        Request.addProperty("locationLatIn", locationLatIn);
                        Request.addProperty("locationLongIn", locationLongIn);
                        Request.addProperty("locationLatOut", locationLatOut);
                        Request.addProperty("locationLongOut", locationLongOut);

                        Request.addProperty("storeLocCode", storeLocCode);
                        Request.addProperty("imageIn", imageIn);
                        Request.addProperty("imageOut", imageOut);

                        Log.i(TAG, "CheckInOut: " + dtrID);
                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
                        transport.debug = true;
                        transport.call(SOAP_ACTION, soapEnvelope);

                        resultCheckInOut = (SoapPrimitive) soapEnvelope.getResponse();

                        //SoapObject result = (SoapObject) soapEnvelope.getResponse();
                        //sqlDB.rawQuery("UPDATE dtr SET syncStatus='sync' WHERE dtrID=?", new String[]{dtrID});
                        //Log.i(TAG, dateOut+" / "+timeOut+" / "+locationLatOut+" / "+locationLongOut+" / "+AddressOut+" / "+imageOut);
                        Log.i(TAG, "Result CheckInOut: " + resultCheckInOut.toString());
                    } catch (Exception ex) {
                        Log.e(TAG, "Error CheckInOut: " + ex.getMessage());
                    }
                }
                while (curs.moveToNext());
            }
            progressDialog.dismiss();
        } else {
            Log.d(TAG, "CheckInOut: No record(s) to sync.");
            //Toast.makeText(this, ""+d, Toast.LENGTH_SHORT).show();
        }
        curs.close();

    }

    // sync data expense to webservice online
    public void Expense() {
        String SOAP_ACTION = "http://tempuri.org/syncExpenseJSON";
        String METHOD_NAME = "syncExpenseJSON";
        String NAMESPACE = "http://tempuri.org/";

        Cursor cursor = sqlDB.rawQuery("SELECT expenseID," +
                "employeeCode , " +
                "date , " +
                "meansOfTransportations, " +
                "client , " +
                "storeLocation , " +
                "time , " +
                "amount , " +
                "receipt , " +
                "notes , " +
                "attachments, " +
                "expenseCode," +
                "storeLocCode " +
                "FROM expenses " +
                "WHERE syncStatus=?", new String[]{"not sync"});
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                //    txtPreviousPrice.setText(cursor.getString(1));
                do {

                    String post_employeeCode = cursor.getString(cursor.getColumnIndex("employeeCode"));
                    String post_date = cursor.getString(cursor.getColumnIndex("date"));
                    String means = cursor.getString(cursor.getColumnIndex("meansOfTransportations"));
                    String post_client = cursor.getString(cursor.getColumnIndex("client"));
                    String post_storeLocation = cursor.getString(cursor.getColumnIndex("storeLocation"));
                    String post_time = cursor.getString(cursor.getColumnIndex("time"));
                    String post_amount = cursor.getString(cursor.getColumnIndex("amount"));
                    String post_receipt = cursor.getString(cursor.getColumnIndex("receipt"));
                    String post_notes = cursor.getString(cursor.getColumnIndex("notes"));
                    String post_attachments = cursor.getString(cursor.getColumnIndex("attachments"));
                    String post_expenseCode = cursor.getString(cursor.getColumnIndex("expenseCode"));
                    String post_storeLocCode = cursor.getString(cursor.getColumnIndex("storeLocCode"));

                    try {
                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("expenseCode", post_expenseCode);
                        Request.addProperty("employeeNo", post_employeeCode);
                        Request.addProperty("means", means);
                        Request.addProperty("customerCode", post_client);
                        Request.addProperty("storeLocationCode", post_storeLocation);
                        Request.addProperty("date", post_date);
                        Request.addProperty("time", post_time);
                        Request.addProperty("amount", post_amount);
                        Request.addProperty("notes", post_notes);
                        Request.addProperty("receipt", post_receipt);
                        Request.addProperty("attachments", post_attachments);
                        Request.addProperty("storeLocationCode", post_storeLocCode);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
                        transport.debug = true;
                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultLocalExpense = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result Expense: " + resultLocalExpense);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Expense: " + ex.getMessage());
                    }

                    sqlDB.execSQL("UPDATE expenses SET " +
                            "syncStatus='SYNC' " +
                            "WHERE expenseID='" + cursor.getString(0) + "';");

                } while (cursor.moveToNext());

            }
        }
        cursor.close();
    }

    public void Inventory() {

        String SOAP_ACTION = "http://tempuri.org/insertInventoryDataJSON";
        String METHOD_NAME = "insertInventoryDataJSON";
        String NAMESPACE = "http://tempuri.org/";

        Cursor cursor = sqlDB.rawQuery("SELECT " +
                "inventoryID, " +
                "itemCode, " +
                "storeCode, " +
                "beginInventory, " +
                "endInventory, " +
                "delivery, " +
                "offTake, " +
                "weekNo, " +
                "userCode, " +
                "expiredItems, " +
                "damagedItems, " +
                "deliveryReturns, " +
                "customerReturns " +
                "FROM inventory " +
                "WHERE syncStatus=?", new String[]{"not sync"});

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {

                    String itemCode, storeCode, beginVal, endVal, deliVal, userCode, offTake, weekNo,
                            expiredItems, damagedItems, deliveryReturns, customerReturns;

                    itemCode = cursor.getString(cursor.getColumnIndex("itemCode"));
                    storeCode = cursor.getString(cursor.getColumnIndex("storeCode"));
                    beginVal = cursor.getString(cursor.getColumnIndex("beginInventory"));
                    endVal = cursor.getString(cursor.getColumnIndex("endInventory"));
                    deliVal = cursor.getString(cursor.getColumnIndex("delivery"));
                    offTake = cursor.getString(cursor.getColumnIndex("offTake"));
                    weekNo = cursor.getString(cursor.getColumnIndex("weekNo"));
                    userCode = cursor.getString(cursor.getColumnIndex("userCode"));
                    expiredItems = cursor.getString(cursor.getColumnIndex("expiredItems"));
                    damagedItems = cursor.getString(cursor.getColumnIndex("damagedItems"));
                    deliveryReturns = cursor.getString(cursor.getColumnIndex("deliveryReturns"));
                    customerReturns = cursor.getString(cursor.getColumnIndex("customerReturns"));

                    try {

                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("itemCode", itemCode);
                        Request.addProperty("storeLocCode", storeCode);
                        Request.addProperty("beginIn", beginVal);
                        Request.addProperty("endIn", endVal);
                        Request.addProperty("delivery", deliVal);
                        Request.addProperty("offTake", offTake);
                        Request.addProperty("weekNo", weekNo);
                        Request.addProperty("employeeNo", userCode);
                        Request.addProperty("expiredItems", expiredItems);
                        Request.addProperty("damageItems", damagedItems);
                        Request.addProperty("deliveryReturns", deliveryReturns);
                        Request.addProperty("customerReturns", customerReturns);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultInventory = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result Inventory: " + resultInventory);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Inventory: " + ex.getMessage());
                    }

                    /*
                    sqlDB.execSQL("UPDATE headerinventory " +
                            "SET status='sync' " +
                            "WHERE id='"+cursor.getString(0)+"'");
                    */
                    sqlDB.execSQL("UPDATE inventory SET " +
                            "syncStatus = 'SYNC' " +
                            "WHERE inventoryID = '" + cursor.getString(0) + "';");

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    public void OSA() {

        String SOAP_ACTION = "http://tempuri.org/insertOSADataJSON";
        String METHOD_NAME = "insertOSADataJSON";
        String NAMESPACE = "http://tempuri.org/";

        Cursor cursor = sqlDB.rawQuery("SELECT " +
                        "osaID, " +
                        "userCode, " +
                        "itemCode," +
                        "storeCode," +
                        "osa," +
                        "facing," +
                        "maxCapacity, " +
                        "weekNo, " +
                        "homeShelfPcs, " +
                        "secondShelfPcs " +
                        "FROM osa " +
                        "WHERE syncStatus=?",
                new String[]{"not sync"});

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    String itemCode = cursor.getString(cursor.getColumnIndex("itemCode"));
                    String storeCode = cursor.getString(cursor.getColumnIndex("storeCode"));
                    String osa = cursor.getString(cursor.getColumnIndex("osa"));
                    String facing = cursor.getString(cursor.getColumnIndex("facing"));
                    String maxCapacity = cursor.getString(cursor.getColumnIndex("maxCapacity"));
                    String weekNo = cursor.getString(cursor.getColumnIndex("weekNo"));
                    String sUserCode = cursor.getString(cursor.getColumnIndex("userCode"));
                    String sHomeShelfPcs = cursor.getString(cursor.getColumnIndex("homeShelfPcs"));
                    String sSecShelfPcs = cursor.getString(cursor.getColumnIndex("secondShelfPcs"));

                    try {
                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("itemCode", itemCode);
                        Request.addProperty("storeLocCode", storeCode);
                        Request.addProperty("osa", osa);
                        Request.addProperty("facing", facing);
                        Request.addProperty("weekNo", weekNo);
                        Request.addProperty("employeeNo", sUserCode);
                        Request.addProperty("maxCapacity", maxCapacity);
                        Request.addProperty("homeShelfPcs", sHomeShelfPcs);
                        Request.addProperty("secondaryShelfPcs", sSecShelfPcs);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
                        transport.debug = true;
                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultOSA = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result OSA: " + resultOSA);

                    } catch (Exception ex) {
                        Log.e(TAG, "Error OSA: " + ex.getMessage());
                    }
                    sqlDB.execSQL("UPDATE osa SET " +
                            "syncStatus = 'SYNC' " +
                            "WHERE osaID='" + cursor.getString(0) + "';");

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    public void Freshness() {
        String SOAP_ACTION = "http://tempuri.org/insertFreshnessDataJSON";
        String METHOD_NAME = "insertFreshnessDataJSON";
        String NAMESPACE = "http://tempuri.org/";

        Cursor cursor = sqlDB.rawQuery("SELECT " +
                "deliveryID, " +
                "userCode, " +
                "itemCode, " +
                "quantity, " +
                "storeLocCode, " +
                "expirationDate, " +
                "unitOfMeasure, " +
                "lotNumber, " +
                "productionDate " +
                "FROM delivery " +
                "WHERE syncStatus=?", new String[]{"not sync"});

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    String employeeNo = cursor.getString(cursor.getColumnIndex("userCode"));
                    String itemCode = cursor.getString(cursor.getColumnIndex("itemCode"));
                    String qty = cursor.getString(cursor.getColumnIndex("quantity"));
                    String storeLocCode = cursor.getString(cursor.getColumnIndex("storeLocCode"));
                    String expirationDate = cursor.getString(cursor.getColumnIndex("expirationDate"));
                    String unitOfMeasure = cursor.getString(cursor.getColumnIndex("unitOfMeasure"));
                    String lotNumber = cursor.getString(cursor.getColumnIndex("lotNumber"));
                    String productionDate = cursor.getString(cursor.getColumnIndex("productionDate"));

                    try {
                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("employeeNo", employeeNo);
                        Request.addProperty("itemCode", itemCode);
                        Request.addProperty("quantity", qty);
                        Request.addProperty("storeLocCode", storeLocCode);
                        Request.addProperty("expirationDate", expirationDate);
                        Request.addProperty("unitOfmeasure", unitOfMeasure);
                        Request.addProperty("lotNumber", lotNumber);
                        Request.addProperty("productionDate", productionDate);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
                        transport.debug = true;
                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultFreshness = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result Freshness: " + resultFreshness);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Freshness: " + ex.getMessage());
                    }

                    sqlDB.execSQL("UPDATE delivery " +
                            "SET syncStatus='sync' " +
                            "WHERE deliveryID='" + cursor.getString(0) + "'");

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    public void NearlyExpired() {
        String SOAP_ACTION = "http://tempuri.org/insertReturnToVendor";
        String METHOD_NAME = "insertReturnToVendor";
        String NAMESPACE = "http://tempuri.org/";

        Cursor cursor = sqlDB.rawQuery("SELECT nteID," +
                "userCode," +
                "itemCode," +
                "storeLocCode," +
                "remarks," +
                "rtvNo," +
                "status," +
                "quantity," +
                "lotNo," +
                "expirationDate," +
                "dateRecorded " +
                "FROM nearlytoexpired " +
                "WHERE syncStatus=?", new String[]{"not sync"});

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {

                    String employeeNo, itemCode, storeLocCode, remarks, rtvNo, status, lotNo,
                            expirationDate, quantity, dateRecorded;

                    employeeNo = cursor.getString(cursor.getColumnIndex("userCode"));
                    itemCode = cursor.getString(cursor.getColumnIndex("itemCode"));
                    storeLocCode = cursor.getString(cursor.getColumnIndex("storeLocCode"));
                    status = cursor.getString(cursor.getColumnIndex("status"));
                    remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                    rtvNo = cursor.getString(cursor.getColumnIndex("rtvNo"));
                    status = cursor.getString(cursor.getColumnIndex("status"));
                    quantity = cursor.getString(cursor.getColumnIndex(String.valueOf("quantity")));
                    lotNo = cursor.getString(cursor.getColumnIndex("lotNo"));
                    expirationDate = cursor.getString(cursor.getColumnIndex("expirationDate"));
                    dateRecorded = cursor.getString(cursor.getColumnIndex("dateRecorded"));

                    try {
                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("employeeNo", employeeNo);
                        Request.addProperty("itemCode", itemCode);
                        Request.addProperty("storeLocCode", storeLocCode);
                        Request.addProperty("remarks", remarks);
                        Request.addProperty("rtvNo", rtvNo);
                        Request.addProperty("status", status);
                        Request.addProperty("quantity", quantity);
                        Request.addProperty("lotNo", lotNo);
                        Request.addProperty("expirationDate", expirationDate);
                        Request.addProperty("dateRecorded", dateRecorded);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultNearly = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result RTV: " + resultNearly);

                    } catch (Exception ex) {
                        Log.e(TAG, "Error RTV: " + ex.getMessage());
                    }

                    sqlDB.execSQL("UPDATE nearlytoexpired " +
                            "SET syncStatus='sync' " +
                            "WHERE nteID='" + cursor.getString(0) + "'");

                    sRTVID = cursor.getString(cursor.getColumnIndex("nteID"));
                    Log.i("TAG", "Value of nteID: " + sRTVID);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

}