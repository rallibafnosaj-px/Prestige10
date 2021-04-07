package com.quaditsolutions.mmreport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Khyrz on 11/13/2017.
 */

public class SyncDataService extends Service
{
    private Handler handler = new Handler();
    private String userCode;
    String TAG = "Response";
    @Override
    public void onCreate()
    {
        super.onCreate();

        IntentFilter ScreenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        ScreenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new SyncDataReceiver();
        registerReceiver(mReceiver, ScreenStateFilter);

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handler.post(runnable);

        boolean screenOn = intent.getBooleanExtra("screenState", false);
        if (!screenOn)
        {
            return START_NOT_STICKY;
        }

        if(userCode.equals("")||userCode.isEmpty())
        {
            Log.i(TAG, "No user data. Stopping service.");
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(haveNetworkConnection(getApplicationContext()))
            {
                SyncData syncData = new SyncData();
                syncData.onStart(SyncDataService.this);

                //Toast.makeText(getApplicationContext(), "Data Sync...", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable, 9999000); // every 2 hrs
                //handler.postDelayed(runnable, 240000); 8 hours 28,800,000
                //handler.postDelayed(runnable, 28800000);

            }
            else{
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "The service was stop");
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                haveConnectedMobile = true;
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
        }

        return haveConnectedWifi || haveConnectedMobile;
    }
}