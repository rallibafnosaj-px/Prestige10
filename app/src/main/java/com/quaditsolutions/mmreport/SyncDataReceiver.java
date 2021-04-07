package com.quaditsolutions.mmreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Khyrz on 11/16/2017.
 */

public class SyncDataReceiver extends BroadcastReceiver
{
    private boolean screenOff;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            screenOff = false;
        }

        Intent ServiceReceiver = new Intent(context, SyncDataService.class);
        intent.putExtra("screenState", screenOff);
        context.startService(ServiceReceiver);

    }
}
