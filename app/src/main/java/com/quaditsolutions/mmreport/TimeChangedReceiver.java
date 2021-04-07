package com.quaditsolutions.mmreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Do whatever you need to

        Date changedTime = Calendar.getInstance().getTime();

        Toast.makeText(context, "" +
                "Date/Time had been Modified! " +
                "\nThis action is being recorded.", Toast.LENGTH_LONG).show();

        Log.d("TimeChangedReceiver", "Time Current Time: " + changedTime);
    }

}
