package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Rozz on 26/07/2018.
 */

public class RecyclerViewForChangeScheduleRequest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SQLiteDatabase sqlDB;
    Cursor cursor;
    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    DecimalFormat df;
    public View layout;
    List<GlobalVar> getDataAdapter;
    private ProgressDialog progressDialog;
    private String TAG = "RecyclerViewForChangeScheduleRequest", userCode, currentIPAddress;
    SoapPrimitive resultRequestCS;
    GlobalVar gv = new GlobalVar();

    public RecyclerViewForChangeScheduleRequest(List<GlobalVar> getDataAdapter, Context context) {

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_change_schedule_request, parent, false);
        RecyclerViewForChangeScheduleRequest.MyHolder holder = new RecyclerViewForChangeScheduleRequest.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RecyclerViewForChangeScheduleRequest.MyHolder myHolder = (RecyclerViewForChangeScheduleRequest.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        myHolder.tv_date_effectivity.setText("Effectivity Date: " + current.reqEffectivityDate);
        myHolder.tv_status_change_schedule.setText("Status: " + current.reqCSStatus);
        myHolder.tv_date_requested.setText("Date Requested: " + current.reqDateRequestedCS);
        myHolder.tv_change_schedule_reason.setText("Reason: " + current.reqCSReason);

        final String valOfStatus = current.reqCSStatus;
        if (valOfStatus.equalsIgnoreCase("Accepted")) {
            myHolder.btnUpdateSchedule.setVisibility(View.VISIBLE);
        } else {
            myHolder.btnUpdateSchedule.setVisibility(View.GONE);
        }

        myHolder.btnUpdateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Confirm Update");
                b.setMessage("Are you sure you want to update your schedule.");
                b.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase sqlDB;

                        sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                        sqlDB.beginTransaction();

                        try {

                            progressDialog.setMessage("Deleting old schedule . . .");
                            progressDialog.show();

                            sqlDB.execSQL("DELETE FROM schedule");
                            getNewSchedule a = new getNewSchedule();
                            a.execute();

                                /*Cursor cursorDel = sqlDB.rawQuery("DELETE FROM schedule",null);

                                if (cursorDel.getCount()==0){
                                    Log.i("TAG", "Del count: 0");
                                }else{
                                    Log.i("TAG","Del count: "+cursorDel.getCount());
                                }*/

                                /*AlertDialog.Builder b = new AlertDialog.Builder(context);
                                b.setTitle("Update");
                                b.setMessage("Schedule Updated Successfully!");
                                b.setPositiveButton("OK",null);
                                b.show();*/

                        } catch (Exception e) {

                        }

                        sqlDB.setTransactionSuccessful();
                        sqlDB.endTransaction();
                        sqlDB.close();

                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();
            }
        });

    }

    private class getNewSchedule extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Updating Schedule");
            progressDialog.setMessage("Please wait while schedule is being updated.");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            request_change_schedule_status();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            try {

                //Open Database
                sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                sqlDB.beginTransaction();

                cursor = sqlDB.rawQuery("SELECT scheduleID FROM schedule", null);
                if (cursor.getCount() > 0) {
                    sqlDB.execSQL("DELETE FROM schedule");
                } else {
                    if (resultRequestCS != null) {

                        sqlDB.execSQL("DELETE FROM schedule");

                        JSONArray jsonArray = new JSONArray(resultRequestCS.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json_data = jsonArray.getJSONObject(i);

                            gv.reqCSStatus = json_data.getString("status");
                            gv.reqDateRequestedCS = json_data.getString("dateRequested");
                            gv.reqCSReason = json_data.getString("reason");
                            gv.reqStoreLocationName = json_data.getString("storeLocationName");
                            gv.reqStoreLocCode = json_data.getString("storeLocCode");
                            gv.reqStartTime = json_data.getString("starttime");
                            gv.reqEndTime = json_data.getString("endtime");
                            gv.reqWorkingDays = json_data.getString("workingDays");
                            gv.reqEffectivityDate = json_data.getString("dateEffective");

                            Log.i(TAG, "Day: " + gv.reqWorkingDays + "" +
                                    "\nStatus: " + gv.reqCSStatus);

                            if (gv.reqCSStatus.equalsIgnoreCase("Accepted")) {

                                sqlDB.execSQL("INSERT OR REPLACE " +
                                        "INTO schedule" +
                                        "(scheduleID, " +
                                        "userCode, " +
                                        "workingDays, " +
                                        "startTime, " +
                                        "endTime, " +
                                        "storeLocCode, " +
                                        "storeName," +
                                        "effectivityDate) " +
                                        "VALUES" +
                                        "('" + gv.scheduleID +
                                        "', '" + userCode +
                                        "', '" + gv.reqWorkingDays +
                                        "', '" + gv.reqStartTime +
                                        "', '" + gv.reqEndTime +
                                        "', '" + gv.reqStoreLocCode +
                                        "', '" + gv.reqStoreLocationName + "'" +
                                        ",' "+ gv.reqEffectivityDate +"');");

                                /*
                                    sqlDB.execSQL("INSERT INTO " +
                                        "req_change_schedule" +
                                        "(reqCSStatus," +
                                        "reqDateRequestedCS," +
                                        "reqCSReason," +
                                        "reqStoreLocationName," +
                                        "reqStoreLocCode," +
                                        "reqStartTime," +
                                        "reqEndTime," +
                                        "reqWorkingDays," +
                                        "effectivityDate) " +
                                        "VALUES" +
                                        "('" + gv.reqCSStatus + "'," +
                                        "'" + gv.reqDateRequestedCS + "'," +
                                        "'" + gv.reqCSReason + "'," +
                                        "'" + gv.reqStoreLocationName + "'," +
                                        "'" + gv.reqStoreLocCode + "'," +
                                        "'" + gv.reqStartTime + "'," +
                                        "'" + gv.reqEndTime + "'," +
                                        "'" + gv.reqWorkingDays + "'," +
                                        "'" + gv.reqEffectivityDate + "');");
                                */
                                Toast.makeText(context, "Schedule Updated!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }

                sqlDB.setTransactionSuccessful();
                sqlDB.endTransaction();
                sqlDB.close();

            } catch (Exception e) {
                Log.i(TAG, "Error in fetching new schedule: " + e);
            }

        }
    }

    public void request_change_schedule_status() {

        SharedPreferences sp = context.getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        currentIPAddress = sp.getString("ipAddress", null);

        String SOAP_ACTION = "http://tempuri.org/getRequestChangeSchedule";
        String METHOD_NAME = "getRequestChangeSchedule";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultRequestCS = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Request Change Schedule Status: " + resultRequestCS + "\n\n");
        } catch (Exception e) {
            Log.i(TAG, "Error Request Change Schedule Status: " + e);
        }

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv_status_change_schedule, tv_date_requested, tv_change_schedule_reason,
                tv_date_effectivity;
        Button btnUpdateSchedule;

        public MyHolder(View itemView) {

            super(itemView);

            tv_status_change_schedule = (TextView) itemView.findViewById(R.id.tv_status_change_schedule);
            tv_date_requested = (TextView) itemView.findViewById(R.id.tv_date_requested);
            tv_change_schedule_reason = (TextView) itemView.findViewById(R.id.tv_reason_change_schedule);
            btnUpdateSchedule = (Button) itemView.findViewById(R.id.btnUpdateApprovedSchedule);
            tv_date_effectivity = (TextView) itemView.findViewById(R.id.tv_date_effectivity);
            progressDialog = new ProgressDialog(context);

        }
    }
}
