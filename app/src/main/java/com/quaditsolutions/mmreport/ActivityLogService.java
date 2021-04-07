package com.quaditsolutions.mmreport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Khyrz on 11/13/2017.
 */

public class ActivityLogService extends Service
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
        //isRunning=true;

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
            if(haveNetworkConnection(getApplicationContext())) {
                //checkExpired();
                Log.i(TAG, "Starting Activity Log service...");
                handler.postDelayed(runnable, 60000);
            }
            else
            {
          //      isRunning = false;
                Log.i(TAG, "No user data. Stopping service.");
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //isRunning = false;
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