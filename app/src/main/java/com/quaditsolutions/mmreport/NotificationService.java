package com.quaditsolutions.mmreport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Khyrz on 11/13/2017.
 */

public class NotificationService extends Service {
    private Handler handler = new Handler();
    private String userCode;
    String TAG = "Response";
    BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            IntentFilter ScreenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            ScreenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
            mReceiver = new SyncDataReceiver();
            registerReceiver(mReceiver, ScreenStateFilter);
            //isRunning=true;

            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
            userCode = sp.getString("userCode", null);
        } catch (Exception e) {
            Log.i("TAG", "Error in notification service: " + e);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);

        boolean screenOn = intent.getBooleanExtra("screenState", false);
        if (!screenOn) {
            return START_NOT_STICKY;
        }

        if (userCode.equals("") || userCode.isEmpty()) {
            Log.i(TAG, "No user data. Stopping service.");
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (haveNetworkConnection(getApplicationContext())) {
                checkExpired();
                Log.i(TAG, "Starting Notification service...");
                //60k 1 min
                handler.postDelayed(runnable, 28800000);
            } else {
                //      isRunning = false;
                Log.i(TAG, "No user data. Stopping service.");
                stopSelf();
            }
        }
    };

    private void checkExpired() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);
        String[] dtSplit = dateTime.split("-");

        int year, month, day, exYear, exMonth, exDay;
        year = Integer.parseInt(dtSplit[0]);
        month = Integer.parseInt(dtSplit[1]);
        day = Integer.parseInt(dtSplit[2]);

        SQLiteDatabase sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        Cursor cursor = sqlDB.rawQuery("SELECT DISTINCT " +
                "itemCode, " +
                "expirationDate, " +
                "popupStatus, " +
                "deliveryID " +
                "FROM delivery " +
                "WHERE userCode=?", new String[]{userCode});
        if (cursor.getCount() == 0) {
            //nothing
        } else {
            if (cursor.moveToFirst()) {
                do {
                    String exDate = cursor.getString(1);
                    String[] dtExSplit = exDate.split("-");
                    exYear = Integer.parseInt(dtExSplit[0]);
                    exMonth = Integer.parseInt(dtExSplit[1]);
                    exDay = Integer.parseInt(dtExSplit[2]);

                    int currentTimeStamp = timeStamp(year, month, day);
                    int expirationTimeStamp = timeStamp(exYear, exMonth, exDay);

                    currentTimeStamp += 2592000;

                    if (currentTimeStamp >= expirationTimeStamp) {
                        if (cursor.getString(2).equals("1")) {
                            alertExpire(cursor.getString(0), exYear, exMonth, exDay, cursor.getInt(3));
                        } else {
                            //nothing
                        }

                    } else {
                        //nothing
                    }

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        sqlDB.close();
    }

    private void alertExpire(final String itemCode, int year, int month, int day, int deliveryID) {
        final SQLiteDatabase sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        Cursor cursor = sqlDB.rawQuery("SELECT DISTINCT itemName " +
                "FROM assignment " +
                "WHERE itemCode=?", new String[]{itemCode});
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    sqlDB.execSQL("UPDATE delivery SET " +
                            "tag='nearToExpire' " +
                            "WHERE itemCode='" + itemCode + "' " +
                            "AND userCode='" + userCode + "'");

                    String title = "Nearly Expired";
                    //String message = cursor.getString(0)+" will be expiring in "+year+"-"+month+"-"+day;
                    String message = cursor.getString(0) + "\nExp. Date: " + year + "-" + month + "-" + day;

                    //long when = System.currentTimeMillis(); //now
                    int icon = R.drawable.ic_notif;

                    Intent i = new Intent(this, NearlyToExpiredList.class);
                    //Intent j = new Intent(this, MainActivity.class);

                    //PendingIntent pi = PendingIntent.getActivity(this, 1, pi, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent p = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


                    Notification mNotif = new android.support.v4.app.NotificationCompat.Builder(this)
                            .setSmallIcon(icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setTicker(title)
                            .setContentIntent(p)
                            .setAutoCancel(true)
                            .setPriority(2)
                            //.addAction(R.drawable.ic_snooze, "SNOOZE", p)
                            //.addAction(R.drawable.ic_snooze, "STOP", p)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .build();

                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    int min = 0;
                    int max = 9999999;

                    Random r = new Random();
                    int il = r.nextInt(max - min + 1) + min;

                    mNotifyMgr.notify(il, mNotif);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        sqlDB.close();
    }

    int timeStamp(int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (int) (c.getTimeInMillis() / 1000L);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
        //isRunning = false;
        Log.i(TAG, "The service was stop");
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
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
