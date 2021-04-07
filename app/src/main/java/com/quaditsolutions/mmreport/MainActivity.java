package com.quaditsolutions.mmreport;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /// for syncing
    SoapPrimitive resultCheckInOut, resultLocalExpense, resultInventory, resultOSA,
            resultFreshness, resultNearly;
    String userCode, dtrID, storeLocation, TAG = "Response", osaID, fullAddress, sRTVID, currentDateTime;
    List<Address> addressList;
    Geocoder geocoder;// end syncing button

    private boolean doubleBackToExitPressedOnce = false;
    ProgressDialog progressDialog;
    ImageView imgViewHome, mCounter;
    TextView txtFullName, txtEmail, numAnnouncement, tvEmpNo;
    SoapPrimitive resultSchedule, resultDelivery, resultExpenseType,
            resultAssignment, resultAppModule, resultAppVersion, resultRequestOT,
            resultRequestDeviation, resultRequestLeave, resultRequestCS, announcementResult;
    int inout = -1;
    int a = 0;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    GlobalVar gv = new GlobalVar();
    String companyCode, customerCode;
    private ProgressDialog prgsDlg;
    GridView gridView;
    ArrayList<String> moduleArr = new ArrayList<>();

    String hours, days;
    String URL;

    int check = 0;

    int syncstatus = 0;
    Boolean syncing = true;

    Handler handler;
    Runnable r;

    String dateIn, timeIn, storeLocCode, dayNow, currentIPAddress;
    Integer countAnnouncement = 0;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

    Context mContext;
    GPSTracker gps = new GPSTracker();
    double latitude;
    double longitude;

    AlertDialog.Builder mValid;

    LinearLayout linearLayoutBg;
    ScrollView scrollViewBg;
    GridView gridView1;
    CardView cardViewBg;

    TextView tvAnnouncementBadge, tvPendingSyncBadge;
    ImageView imgAnnouncement;

    int messages = 0;
    int pendingSyncCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Permissions();
        //getIP();

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        customerCode = sp.getString("customerCode", null);
        currentIPAddress = sp.getString("ipAddress", null);

        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                //getSupportActionBar().setDisplayShowHomeEnabled(true);
                //getSupportActionBar().setIcon(R.drawable.regcris);
                //imgViewHome.setImageResource(R.drawable.prestige);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                    //getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
                }
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                //getSupportActionBar().setDisplayShowHomeEnabled(true);
                //getSupportActionBar().setIcon(R.drawable.prestige);
                //imgViewHome.setImageResource(R.drawable.regcris);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                    //getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
                }
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                //getSupportActionBar().setDisplayShowHomeEnabled(true);
                //getSupportActionBar().setIcon(R.drawable.tmarks);
                //imgViewHome.setImageResource(R.drawable.tmarks);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                    //getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
                }
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        AlertDialog.Builder bb = new AlertDialog.Builder(MainActivity.this);
        bb.setTitle("Current Address");
        bb.setMessage(currentIPAddress);
        bb.show();
        bb.setPositiveButton("OK",null);
        */
//       GetLocation getLocation = new GetLocation();
//       getLocation.getCurrLoc(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);
        dayNow = dayFormat.format(now);
        String[] dtSplit = dateTime.split(" ");
        dateIn = dtSplit[0];
        timeIn = dtSplit[1] + " " + dtSplit[2];

        // REMINDER FOR DTR
        //Open Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        String timeOutStatic = "00:00:00";
        String dateOutStatic = "0000-00-00";

        cursor = sqlDB.rawQuery("SELECT " +
                        "timeIn, " +
                        "timeOut, " +
                        "dtrID " +
                        "FROM dtr " +
                        "WHERE userCode=? " +
                        "AND timeOut=? " +
                        "AND dateOut=? " +
                        "ORDER BY dtrID DESC " +
                        "LIMIT 1",
                new String[]{userCode, timeOutStatic, dateOutStatic});

        if (cursor.getCount() == 1) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("REMINDER");
            b.setMessage("Please Don`t Forget to Time Out.");
            b.setPositiveButton("OK", null);
            b.show();
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("REMINDER");
            b.setMessage("Please Don`t Forget to Time In.");
            b.setPositiveButton("OK", null);
            b.show();
        }

        cursor.close();
        sqlDB.close();

        checkGPS();

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.small_automat_logo);

        imgViewHome = (ImageView) findViewById(R.id.imgViewHome);
        scrollViewBg = (ScrollView) findViewById(R.id.scrollViewBg);
        linearLayoutBg = (LinearLayout) findViewById(R.id.linearLayoutBg);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        cardViewBg = (CardView) findViewById(R.id.cardviewMain);

        scrollViewBg.setBackgroundColor(Color.parseColor("#212121"));
        linearLayoutBg.setBackgroundColor(Color.parseColor("#212121"));
        gridView1.setBackgroundColor(Color.parseColor("#212121"));
        cardViewBg.setCardBackgroundColor(Color.parseColor("#212121"));

        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                //imgViewHome.setImageResource(R.drawable.prestige);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                }
                /*scrollViewBg.setBackgroundColor(Color.parseColor("#ffffff"));
                linearLayoutBg.setBackgroundColor(Color.parseColor("#ffffff"));
                gridView1.setBackgroundColor(Color.parseColor("#ffffff"));
                cardViewBg.setCardBackgroundColor(Color.parseColor("#ffffff"));*/
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                //imgViewHome.setImageResource(R.drawable.regcris);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                }
                /*scrollViewBg.setBackgroundColor(Color.parseColor("#212121"));
                linearLayoutBg.setBackgroundColor(Color.parseColor("#212121"));
                gridView1.setBackgroundColor(Color.parseColor("#212121"));
                cardViewBg.setCardBackgroundColor(Color.parseColor("#212121"));*/
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                //imgViewHome.setImageResource(R.drawable.tmarks);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                }
                /*scrollViewBg.setBackgroundColor(Color.parseColor("#222c65"));
                linearLayoutBg.setBackgroundColor(Color.parseColor("#222c65"));
                gridView1.setBackgroundColor(Color.parseColor("#222c65"));
                cardViewBg.setCardBackgroundColor(Color.parseColor("#222c65"));*/
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        /*font family*/
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        navigationView.setNavigationItemSelectedListener(this);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logOut).setVisible(false);
        nav_Menu.findItem(R.id.nav_update_appdata).setVisible(false);

        //These lines should be added in the OnCreate() of your main activity
        tvAnnouncementBadge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_announcement));
        tvPendingSyncBadge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_logs));

        tvEmpNo = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmpNo);
        txtFullName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtFullname);
        txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtEmail);

        // change nav color
        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    View header = navigationView.getHeaderView(0);
                    header.setBackgroundColor(Color.parseColor("#e9986f"));
                }
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    View header = navigationView.getHeaderView(0);
                    header.setBackgroundColor(Color.parseColor("#6ab1c5"));
                }
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    View header = navigationView.getHeaderView(0);
                    header.setBackgroundColor(Color.parseColor("#222c65"));
                }
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }

        tvEmpNo.setText("Employee No.: " + userCode);

        // open db
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        prgsDlg = new ProgressDialog(MainActivity.this);
        prgsDlg.setMessage("Please wait...");
        prgsDlg.setCancelable(false);
        prgsDlg.show();

        if (haveNetworkConnection(this)) {
            try {
                //AppVersion appVersion = new AppVersion();
                //appVersion.execute();
                getData();
            } catch (Exception e) {
                Log.i(TAG, "Error in getting app version.");
            }

        } else {
            /*AppVersion appVersion = new AppVersion();
            appVersion.execute();
            prgsDlg.dismiss();*/
            Toast.makeText(MainActivity.this, "Please check internet connection.", Toast.LENGTH_SHORT).show();
        }

        gridView = (GridView) findViewById(R.id.gridView1);

        // load home page icon
        HomeIcons();

        Cursor c = sqlDB.rawQuery("SELECT " +
                "userName, " +
                "firstName, " +
                "middleName, " +
                "lastName " +
                "FROM users " +
                "WHERE userCode=?", new String[]{userCode});

        if (c.moveToFirst()) {
            do {
                String firstName = c.getString(c.getColumnIndex("firstName"));
                String middleName = c.getString(c.getColumnIndex("middleName"));
                String lastName = c.getString(c.getColumnIndex("lastName"));
                String email = c.getString(c.getColumnIndex("userName"));
                String fullName = firstName + " " + middleName + " " + lastName;

                txtFullName.setText(fullName);

                /*
                String uname = "Welcome "+firstName+"!";
                Toast.makeText(MainActivity.this, uname, Toast.LENGTH_LONG).show();
                */
                txtEmail.setText(email);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("firstName", firstName);
                editor.putString("middleName", middleName);
                editor.putString("lastName", lastName);
                editor.putString("fullname", fullName);
                editor.apply();

            } while (c.moveToNext());
            prgsDlg.dismiss();
        }

        c.close();

        //Gravity property aligns the text
        tvAnnouncementBadge.setGravity(Gravity.CENTER_VERTICAL);
        tvAnnouncementBadge.setTypeface(null, Typeface.BOLD);
        tvAnnouncementBadge.setTextColor(getResources().getColor(R.color.colorAccent));
        String x = String.valueOf(messages);
        tvAnnouncementBadge.setText(x);

        getItemData(); // get announcement

        // Pending Sync
        tvPendingSyncBadge.setGravity(Gravity.CENTER_VERTICAL);
        tvPendingSyncBadge.setTypeface(null, Typeface.BOLD);
        tvPendingSyncBadge.setTextColor(getResources().getColor(R.color.colorAccent));

        countUnsyncData();

    }

    public void countUnsyncData() {

        // open db
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        Cursor curSyncStats = sqlDB.rawQuery("SELECT dtrID " +
                "FROM dtr " +
                "WHERE statusIn ='not sync' ", null);

        Cursor curSyncStatsOut = sqlDB.rawQuery("SELECT dtrID " +
                "FROM dtr " +
                "WHERE statusOut = 'not sync' ", null);

        Cursor curSyncStatsExpense = sqlDB.rawQuery("SELECT expenseID " +
                "FROM expenses " +
                "WHERE syncStatus = 'not sync' ", null);

        Cursor curSyncFreshness = sqlDB.rawQuery("SELECT deliveryID " +
                "FROM delivery WHERE syncStatus = 'not sync' ", null);

        Cursor curSyncOSA = sqlDB.rawQuery("SELECT osaID " +
                "FROM osa WHERE syncStatus = 'not sync' ", null);

        Cursor curSyncInv = sqlDB.rawQuery("SELECT inventoryID " +
                "FROM inventory WHERE syncStatus = 'not sync' ", null);

        int dtrIn = curSyncStats.getCount();
        int dtrOut = curSyncStatsOut.getCount();
        int iSyncStats = curSyncStatsExpense.getCount();
        int iFreshness = curSyncFreshness.getCount();
        int iOSA = curSyncOSA.getCount();
        int iInventory = curSyncInv.getCount();

        int totalUnsyncVal = dtrIn + dtrOut + iSyncStats + iFreshness + iOSA + iInventory;

        if (totalUnsyncVal != 0) {
            tvPendingSyncBadge.setText(String.valueOf(totalUnsyncVal));
        } else {
            tvPendingSyncBadge.setText("0");
        }

        //sqlDB.close();
        curSyncFreshness.close();
        curSyncInv.close();
        curSyncOSA.close();
        curSyncStats.close();
        curSyncStatsOut.close();
        curSyncStatsExpense.close();

    }

    @Override
    protected void onPostResume() {
        checkGPS();
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);
        //MenuItem item = menu.findItem(R.id.menuSearch);
        //SearchView searchView = (SearchView) item.getActionView();

        //badgeLayout = (RelativeLayout) menu.findItem(R.id.menu_announcement_badge).getActionView();
        //mCounter = (TextView) badgeLayout.findViewById(R.id.counterBadge);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle item selection
        switch (item.getItemId()) {
            /*case R.id.menu_announcement_badge:
                    startActivity(new Intent(MainActivity.this, AnnouncementActivity.class));
                return true;
            case R.id.pending_sync:
                    startActivity(new Intent(MainActivity.this, SyncPanel2.class));
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void HomeIcons() {
        try {

            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            Cursor csr = sqlDB.rawQuery("SELECT appsModuleID " +
                    "FROM customeraccess " +
                    "WHERE customerCode=? " +
                    "AND isVisible=1", new String[]{customerCode});

            if (csr.getCount() != 0) {
                if (csr.moveToFirst()) {
                    moduleArr.clear();
                    do {
                        moduleArr.add(csr.getString(csr.getColumnIndex("appsModuleID")));

                            /*if(csr.getString(csr.getColumnIndex("appsModuleID")).equals("4"))
                            {
                                moduleArr.add("n");
                            }*/

                    } while (csr.moveToNext());
                }
            }

            gridView.setAdapter(new IconAdapter(this, moduleArr));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    String module = ((TextView) v.findViewById(R.id.txtView)).getText().toString();

                    switch (module) {

                        // 1
                        case "Time In / Out":
                            Intent cio = new Intent(MainActivity.this, CheckInActivity.class);
                            startActivity(cio);

                            break;

                        // 2
                        case "Inventory":
                            Intent in = new Intent(MainActivity.this, WeeklyInventoryStatus.class);
                            startActivity(in);

                            //startActivity(new Intent(MainActivity.this, WeeklyInventoryStatus.class));
                            //startActivity(new Intent(MainActivity.this, ItemCategoryActivity.class));
                            break;

                        // 3
                        case "OSA":
                            startActivity(new Intent(MainActivity.this, ItemCategoryOSA.class));

                            break;

                        // 4
                        case "Freshness":
                            Intent fns = new Intent(MainActivity.this, FreshnessItem.class);
                            startActivity(fns);

                            break;

                        // 5
                        case "Expenses":
                            Intent ex = new Intent(MainActivity.this, Expenses.class);
                            startActivity(ex);

                            break;

                        // 6
                        case "Announcement":
                            Intent ann = new Intent(MainActivity.this, AnnouncementActivity.class);
                            startActivity(ann);

                            break;

                        // 7
                        case "Schedule":
                            Intent mySched = new Intent(MainActivity.this, EmployeeScheduleActivity.class);
                            startActivity(mySched);

                            break;

                        // 8
                        case "Review DTR":
                            Intent revDTR = new Intent(MainActivity.this, DTRReviewActivity.class);
                            startActivity(revDTR);

                            break;

                        // 9
                        case "Pending Sync":
                            Intent pendingSync = new Intent(MainActivity.this, SyncPanel2.class);
                            startActivity(pendingSync);

                            break;

                        default:
                            break;
                    }
                }
            });

            csr.close();

        } catch (Exception e) {
            Log.d("TAG", "Error fetching home icons");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Double tap to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_ip_address) {
            startActivity(new Intent(MainActivity.this, ChangeIPAddressActivity.class));
            finish();
        } else if (id == R.id.nav_priceChange) {
            Intent in = new Intent(MainActivity.this, PriceItemActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_my_schedule) {
            Intent in = new Intent(MainActivity.this, EmployeeScheduleActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_changePassword) {
            Intent in = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_logs) {
            Intent in = new Intent(MainActivity.this, SyncPanel2.class);
            startActivity(in);
        } else if (id == R.id.nav_request_status) {

            getOTRequestClass ot = new getOTRequestClass();
            getDeviationClass deviation = new getDeviationClass();
            getLeaveClass leave = new getLeaveClass();
            getChangeSchedule changeSchedule = new getChangeSchedule();

            ot.execute();
            deviation.execute();
            leave.execute();
            changeSchedule.execute();

            Intent i = new Intent(MainActivity.this, RequestPanel.class);
            startActivity(i);

        } else if (id == R.id.nav_payslip) {
            showInputDialog();
        } else if (id == R.id.nav_logOut) {

            /*
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

            Date now = new Date(System.currentTimeMillis());
            String dateTime = dateFormat.format(now);
            dayNow = dayFormat.format(now);
            String[] dtSplit = dateTime.split(" ");
            dateIn = dtSplit[0];
            timeIn = dtSplit[1] + " " + dtSplit[2];

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("SELECT " +
                    "timeIn, " +
                    "timeOut, " +
                    "dtrID " +
                    "FROM dtr " +
                    "WHERE userCode=? " +
                    "AND dateIn=?", new String[]{userCode, dateIn});

            if (cursor.getCount() == 0) {
                askLogoutConfirmation();
            } else {
                if (cursor.moveToLast()) {
                    String timeOut = cursor.getString(1);
                    if (timeOut == null || timeOut.equals("00:00:00")) {
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                        b.setTitle("Warning");
                        b.setMessage("You are currently checked in.\nPlease checkout first.");
                        b.setPositiveButton("OK",null);
                        b.show();
                    } else {
                        askLogoutConfirmation();
                    }
                }
            }
            */

        } else if (id == R.id.nav_checkinout) {
            startActivity(new Intent(MainActivity.this, CheckInActivity.class));
        } else if (id == R.id.nav_inventory) {
            startActivity(new Intent(MainActivity.this, WeeklyInventoryStatus.class));
        } else if (id == R.id.nav_review_dtr) {
            startActivity(new Intent(MainActivity.this, DTRReviewActivity.class));
        } else if (id == R.id.nav_osa) {
            startActivity(new Intent(MainActivity.this, ItemCategoryOSA.class));
        } else if (id == R.id.nav_freshness) {
            startActivity(new Intent(MainActivity.this, FreshnessItem.class));
        } else if (id == R.id.nav_expenses) {
            startActivity(new Intent(MainActivity.this, Expenses.class));
        } else if (id == R.id.nav_nearly_expired) {
            startActivity(new Intent(MainActivity.this, NearlyToExpiredList.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(MainActivity.this, RequestDeviationActivity.class));
        } else if (id == R.id.nav_request_deviation) {
            startActivity(new Intent(MainActivity.this, RequestOneDayDeviationActivity.class));
        } else if (id == R.id.nav_leave_request) {
            startActivity(new Intent(MainActivity.this, LeaveRequestActivity.class));
        } else if (id == R.id.nav_request_overtime) {
            startActivity(new Intent(MainActivity.this, OvertimeRequestActivity.class));
        } else if (id == R.id.nav_announcement) {
            tvAnnouncementBadge.setText("0" + "");
            startActivity(new Intent(MainActivity.this, AnnouncementActivity.class));
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
        } else if (id == R.id.nav_sync_data) {

            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("select time(sum(strftime('%s', timeOut) - strftime('%s', timeIn)),'unixepoch') AS TotalHours from dtr where timeOut not like '00:00:00'", null);
            if (cursor.moveToFirst()) {


                if (cursor.getString(cursor.getColumnIndex("TotalHours")) == null) {
                    hours = "0";
                } else {
                    hours = cursor.getString(cursor.getColumnIndex("TotalHours"));
                }


            }

            cursor.close();


            cursor = sqlDB.rawQuery("select count(DISTINCT dateIn) as TotalDays from dtr where timeOut not like '00:00:00'", null);
            if (cursor.moveToFirst()) {
                days = cursor.getString(cursor.getColumnIndex("TotalDays"));
            }

            cursor.close();

            mValid = new AlertDialog.Builder(this);
            mValid.setTitle("Your Daily Time Record");
            mValid.setMessage("Your total number of Hours: " + hours + "\n" + "Your total number of Days: " + days);


            if (haveNetworkConnection(getApplicationContext())) {

                mValid.setCancelable(true);
                mValid.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        syncDTRtoLive();
                        syncDataToServer();
                    }
                });
                mValid.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mValid.show();


            } else {


            }


        } else if (id == R.id.nav_about_app) {
            startActivity(new Intent(MainActivity.this, AutomatAPKDownloadWebview.class));

        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(MainActivity.this, FAQActivity.class));
            finish();
        }
//        else if (id == R.id.nav_restartIcons) {
//            AppModule();
//            startActivity(getIntent());
//            HomeIcons();
//        }

        /*else if(id == R.id.nav_update_appdata){

            if (haveNetworkConnection(this)){
                AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
                d.setTitle("Update App Data");
                d.setMessage("Are you sure you want to update? \n" +
                        "Updating app data will delete\n" +
                        "all your current data.");
                d.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog p = new ProgressDialog(MainActivity.this);
                        p.setMessage("This may take some time\nUpdating all data Please wait.");
                        p.show();
                        deleteAllData();
                        p.hide();
                    }
                });
                d.setNegativeButton("CANCEL",null);
                d.show();
            }

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void syncDTRtoLive() {

        URL = "http://51.79.223.162:8080/automatsyncnew/SyncData.php";

        handler = new Handler();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Syncing Daily Time Record to Server.");
        progressDialog.setMessage("Preparing data for syncing...");

        progressDialog.show();
        r = new Runnable() {
            @Override
            public void run() {

                if (syncing) {
                    handler.postDelayed(this, 3000);
                    String query = "SELECT * from dtr where (statusIN=? and statusOUT=?) AND userCode =? limit 1";

                    Cursor cursor = sqlDB.rawQuery(query, new String[]{"not sync", "not sync", userCode});

                    if (syncstatus == 0) {
                        if (cursor.moveToFirst()) {
                            if (cursor.getCount() > 0) {

                                syncstatus = 1;

                                final String db_id = cursor.getString(0);
                                final String db_userCode = cursor.getString(1);
                                final String db_storeLocCode = cursor.getString(4);
                                final String db_storeName = cursor.getString(5);
                                final String db_timeIn = cursor.getString(6);
                                final String db_timeOut = cursor.getString(7);
                                final String db_dateIn = cursor.getString(8);
                                final String db_dateOut = cursor.getString(9);
                                final String db_locationLatIn = cursor.getString(10);
                                final String db_locationLatOut = cursor.getString(11);
                                final String db_locationLongIn = cursor.getString(12);
                                final String db_locationLongOut = cursor.getString(13);
                                final String db_addressIn = cursor.getString(14);
                                final String db_addressOut = cursor.getString(15);
                                final String db_imageIn = cursor.getString(16);
                                final String db_imageOut = cursor.getString(17);

                                progressDialog.setMessage("Syncing Daily Time Record \n" + "Date: " + db_dateIn + "\nDate out: " + db_dateOut + "\nTime in: " +  db_timeIn + "\nTime out: " + db_timeOut);


                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        if (response.equals("0")) {
                                            progressDialog.setMessage("Successfully Sync Daily Time Record \n" + "Date: " + db_dateIn + "\nDate out: " + db_dateOut + "\nTime in: " +  db_timeIn + "\nTime out: " + db_timeOut);
                                            String query_deleteSyncData = "Delete from dtr where (statusIn='not sync' and statusOut = 'not sync') and dtrID='" + db_id + "'";
                                            sqlDB.execSQL(query_deleteSyncData);
                                            syncstatus = 0;
                                        } else if(response.equals("3")) {
                                            progressDialog.setMessage("Skipping... \n" + "Date: " + db_dateIn + "\nDate out: " + db_dateOut + "\nTime in: " +  db_timeIn + "\nTime out: " + db_timeOut);
                                            String query_deleteSyncData = "Delete from dtr where (statusIn='not sync' and statusOut = 'not sync') and dtrID='" + db_id + "'";
                                            sqlDB.execSQL(query_deleteSyncData);
                                            syncstatus = 0;
                                        }
                                        else
                                        {
                                            syncstatus = 0;
                                            progressDialog.setMessage("Daily Time Record \n" + "Date: " + db_dateIn + "\nDate out: " + db_dateOut + "\nTime in: " +  db_timeIn + "\nTime out: " + db_timeOut + "\nUnsuccessfully Synced. Retrying....");
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        syncing = false;
                                        syncstatus = 0;
                                        Toast.makeText(MainActivity.this, "Error encountered while syncing. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        Map<String, String> params = new HashMap<>();

                                        params.put("userCode", db_userCode);
                                        params.put("storeLocCode", db_storeLocCode);
                                        params.put("storeName", db_storeName);
                                        params.put("timeIn", db_timeIn);
                                        params.put("timeOut", db_timeOut);
                                        params.put("dateIn", db_dateIn);
                                        params.put("dateOut", db_dateOut);
                                        params.put("locationLatIn", db_locationLatIn);
                                        params.put("locationLatOut", db_locationLatOut);
                                        params.put("locationLongIn", db_locationLongIn);
                                        params.put("locationLongOut", db_locationLongOut);
                                        params.put("addressIn", db_addressIn);
                                        params.put("addressOut", db_addressOut);
                                        params.put("imageIn", db_imageIn);
                                        params.put("imageOut", db_imageOut);

                                        return params;

                                    }
                                };

                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                                        -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                Volley.newRequestQueue(MainActivity.this).add(stringRequest);


                            }


                        } else {
                            syncstatus = 0;
                            progressDialog.dismiss();
                            syncing = false;
                        }

                    } else {
                        progressDialog.setMessage("Waiting Daily Time Record to be synced...");
                    }


                }
            }

        };
        handler.postDelayed(r, 3000);


    }

    private void syncDataToServer() {

        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        mainAsyncTask mat = new mainAsyncTask();
        mat.execute();
    }

    /*
    public void deleteAllData(){
        ProgressDialog p = new ProgressDialog(MainActivity.this);
        p.setMessage("Deleting all data...");
        p.show();
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            sqlDB.execSQL("DELETE FROM assignment WHERE userCode=?", new String[]{userCode});
            Toast.makeText(this, "Items now deleted.", Toast.LENGTH_SHORT).show();
        p.hide();
        ProgressDialog p2 = new ProgressDialog(MainActivity.this);
        p2.setMessage("Updating all data...");
        p2.show();
            getData();
        Toast.makeText(this, "Items now updated.", Toast.LENGTH_SHORT).show();
        p2.hide();
    }*/

    /*
    public void askLogoutConfirmation() {
        DialogInterface.OnClickListener exit = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();

                        stopService(new Intent(getBaseContext(), NotificationService.class));
                        stopService(new Intent(getBaseContext(), SyncDataService.class));

                        Intent in = new Intent(MainActivity.this, LogInActivity.class);
                        startActivity(in);
                        
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
            AlertDialog.Builder mExit = new AlertDialog.Builder(this);
            mExit.setTitle("Confirm Log out");
            mExit.setMessage("Are you sure you want to log out?\n" +
                             "All your data will be deleted when\n" +
                             "you log out. Please sync first\n" +
                             "before logging out.");
            mExit.setCancelable(false);
            mExit.setPositiveButton("Yes", exit);
            mExit.setNegativeButton("No", exit);
            mExit.show();
    }*/

    @Override
    protected void onResume() {

        countUnsyncData();

        super.onResume();
    }

    public void getData() {
        try {
            loadAllDate();
        } catch (Exception e) {
            Log.d("TAG", "Error fetching schedule");
        }
    }

    public void loadAllDate() {

        // GET SCHEDULE FROM DB
        Cursor cur = sqlDB.rawQuery("SELECT " +
                "scheduleID " +
                "FROM schedule " +
                "WHERE userCode=?", new String[]{userCode});

        if (cur.getCount() == 0) {
            try {
                prgsDlg.setMessage("Getting schedule data...");

                getSchedule schedule = new getSchedule();
                schedule.execute();

            } catch (Exception e) {
                Log.i(TAG, e.toString());

                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
        } else {

        }
        cur.close();

        // GET ASSIGNMENT FROM DB (Item for inventory, osa, freshness)
        cur = sqlDB.rawQuery("SELECT " +
                "assignmentID " +
                "FROM assignment " +
                "WHERE userCode=?", new String[]{userCode});

        if (cur.getCount() == 0) {
            try {
                prgsDlg.setMessage("Getting assignment data...");

                getAssignment assign = new getAssignment();
                assign.execute();

            } catch (Exception e) {
                Log.i(TAG, e.toString());

                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
            //   prgsDlg.dismiss();
        } else if (cur.getCount() > 1) {
            /*try
            {
                prgsDlg.setMessage("Getting assignment data...");

                getAssignment assign = new getAssignment();
                assign.execute();

            }catch (Exception e)
            {
                Log.i(TAG, e.toString());

                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
            */
        }
        cur.close();

        // GET DELIVERY FROM DB -> this data is used for freshness module
        /*
        cur = sqlDB.rawQuery("SELECT " +
                "deliveryID " +
                "FROM delivery " +
                "WHERE userCode=?", new String[]{userCode});
        if(cur.getCount()==0)
        {

            try
            {
                prgsDlg.setMessage("Getting delivery data...");

                getDelivery deli = new getDelivery();
                deli.execute();

            }catch (Exception e)
            {
                Log.i(TAG, e.toString());

                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
        }
        cur.close();
        */

        // GET EXPENSE DATA FROM DB
        cur = sqlDB.rawQuery("SELECT " +
                "expenseCode " +
                "FROM expenseType", null);
        if (cur.getCount() == 0) {
            try {
                prgsDlg.setMessage("Getting expense data...");

                getExpenseType expenseT = new getExpenseType();
                expenseT.execute();

            } catch (Exception e) {
                Log.i(TAG, e.toString());

                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
        }
        cur.close();

        // GET CUSTOMERACESSID FROM DB -> to know what module can be access by the user
        cur = sqlDB.rawQuery("SELECT " +
                "customeraccessID " +
                "FROM customeraccess " +
                "WHERE isVisible = '1' ", null);

        if (cur.getCount() == 0) {

            try {
                prgsDlg.setMessage("Getting app module data...");

                getAppModule appModule = new getAppModule();
                appModule.execute();

            } catch (Exception e) {
                Log.i(TAG, e.toString());
                AlertDialog.Builder mValid = new AlertDialog.Builder(this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage("Check your internet connection.");
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
        }
        cur.close();

        // this is for notifying user and syncing data when you open the app
        // off data sync data consuption in the background -> i changed this to manual sync
        /*
        boolean runningSDS = isMyServiceRunning(SyncDataService.class),
                runningNotif = isMyServiceRunning(NotificationService.class);
        if (!runningSDS && !runningNotif) {
            startService(new Intent(getBaseContext(), SyncDataService.class));
            startService(new Intent(getBaseContext(), NotificationService.class));
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // schedule class
    private class getSchedule extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            schedule();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            sqlDB.beginTransaction();
            try {
                if (resultSchedule != null) {
                    JSONArray jArray = new JSONArray(resultSchedule.toString());
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json_data = jArray.getJSONObject(i);
                        gv.workingDays = json_data.getString("workingDays");
                        gv.startTime = json_data.getString("startTime");
                        gv.endTime = json_data.getString("endTime");
                        gv.storeName = json_data.getString("storeName");
                        gv.storeLocCode = json_data.getString("storeLocCode");
                        gv.scheduleID = json_data.getString("schedHeadId");
                        GlobalVar.storeLoc = json_data.getString("storeLocCode");

                        sqlDB.execSQL("INSERT OR REPLACE " +
                                "INTO schedule" +
                                "(scheduleID, " +
                                "userCode, " +
                                "workingDays, " +
                                "startTime, " +
                                "endTime, " +
                                "storeLocCode, " +
                                "storeName) " +
                                "VALUES" +
                                "('" + gv.scheduleID +
                                "', '" + userCode +
                                "', '" + gv.workingDays +
                                "', '" + gv.startTime +
                                "', '" + gv.endTime +
                                "', '" + gv.storeLocCode +
                                "', '" + gv.storeName + "');");
                    }
                    Log.i(TAG, "success SCHEDULE");
                } else {
                    Log.i(TAG, "Schedule is NULL");
                }
            } catch (JSONException e) {
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "ERROR: SCHEDULE");
                }
            }
            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
            sqlDB.close();
        }
    }

    // schedule ksoap for webservice
    public void schedule() {

        String SOAP_ACTION = "http://tempuri.org/getScheduleJSON";
        String METHOD_NAME = "getScheduleJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);
            transport.call(SOAP_ACTION, soapEnvelope);
            resultSchedule = (SoapPrimitive) soapEnvelope.getResponse();
            Toast.makeText(this, "url2", Toast.LENGTH_SHORT).show();

            Log.i(TAG, "Result Schedule: " + resultSchedule);
        } catch (Exception ex) {
            Log.e(TAG, "Error Schedule: " + ex.getMessage());
        }
    }

    // assignment class
    private class getAssignment extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackgroundAssignmentSync");
            assignment();
            try {
                ProgressDialog p = new ProgressDialog(MainActivity.this);
                p.setTitle("IMPORTANT");
                p.setMessage("Getting assigned items ready for offline mode.\nPlease wait...");
                p.show();

                p.setCancelable(false);
                p.hide();
            } catch (Exception e) {
                Log.d("TAG", "Problem fetching and inserting too many items from portal");
                Log.d("TAG", "Found error: " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            sqlDB.beginTransaction();

            try {
                if (resultAssignment != null) {
                    JSONArray jArray = new JSONArray(resultAssignment.toString());
                    //for(int i=0;i<jArray.length();i++)
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        gv.aItemCode = json_data.getString("itemCode");
                        gv.aItemName = json_data.getString("itemName");
                        gv.aCategoryName = json_data.getString("categoryName");
                        gv.aFreshness = json_data.getString("freshness");
                        gv.aShelfLife = json_data.getString("shelfLife");
                        gv.invOutOfStocks = "NO";

                        gv.itemCustomerCode = json_data.getString("customerCode");
                        gv.itemCustomerName = json_data.getString("customerName");

                        sqlDB.execSQL("INSERT INTO assignment" +
                                "(itemCode, " +
                                "itemName, " +
                                "categoryName, " +
                                "userCode, " +
                                "freshness, " +
                                "tag, " +
                                "shelfLife," +
                                "outOfStocks," +
                                "customerCode," +
                                "customerName) " +
                                "VALUES" +
                                "('" + gv.aItemCode +
                                "', '" + gv.aItemName +
                                "', '" + gv.aCategoryName +
                                "', '" + userCode +
                                "', '" + gv.aFreshness +
                                "', '0'" +
                                ", '" + gv.aShelfLife + "'," +
                                "'" + gv.invOutOfStocks + "' , " +
                                "'" + gv.itemCustomerCode + "', " +
                                "'" + gv.itemCustomerName + "' );");

                    }

                    Log.i(TAG, "success ASSIGNMENT");

                } else {
                    Log.i(TAG, "ASSIGNMENT is NULL");
                }
            } catch (JSONException e) {

                if (!"null".equals(e) || !"".equals(e)) {
                    //Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "ERROR: ASSIGNMENT");
                }
            }

            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
            sqlDB.close();
        }
    }

    // assignment ksoap for webservice
    public void assignment() {
        String SOAP_ACTION = "http://tempuri.org/getAssignmentJSON";
        String METHOD_NAME = "getAssignmentJSON";
        String NAMESPACE = "http://tempuri.org/";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultAssignment = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Assignment: " + resultAssignment);
        } catch (Exception ex) {
            Log.e(TAG, "Error Assignment: " + ex.getMessage());
        }
    }

    private class getChangeSchedule extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            request_change_schedule_status();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            sqlDB.beginTransaction();

            Cursor cursorDel = sqlDB.rawQuery("DELETE FROM req_change_schedule", null);
            if (cursorDel.getCount() == 0) {
                Log.i(TAG, "Del count: 0");
            } else {
                Log.i(TAG, "Del count: " + cursorDel.getCount());
            }

            try {
                if (resultRequestCS != null) {
                    JSONArray jArray = new JSONArray(resultRequestCS.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        gv.reqCSStatus = json_data.getString("status");
                        gv.reqDateRequestedCS = json_data.getString("dateRequested");
                        gv.reqCSReason = json_data.getString("reason");
                        gv.reqStoreLocationName = json_data.getString("storeLocationName");
                        gv.reqStoreLocCode = json_data.getString("storeLocCode");
                        gv.reqStartTime = json_data.getString("starttime");
                        gv.reqEndTime = json_data.getString("endtime");
                        gv.reqWorkingDays = json_data.getString("workingDays");
                        gv.reqEffectivityDate = json_data.getString("dateEffective");

                        sqlDB.execSQL("INSERT INTO req_change_schedule" +
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
                    }
                    Log.i(TAG, "success request change schedule");
                } else {
                    Log.i(TAG, "request is NULL");
                }
            } catch (JSONException e) {
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error: Change Schedule Request" + e);
                }
            }

            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
            sqlDB.close();
        }
    }

    // assignment ksoap for webservice
    public void request_change_schedule_status() {

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

            Log.i(TAG, "Result Request Change Schedule Status: " + resultRequestCS);
        } catch (Exception e) {
            Log.i(TAG, "Error Request Change Schedule Status: " + e);
        }

    }

    // request overtime class
    private class getOTRequestClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            request_overtime_status();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            sqlDB.beginTransaction();

            Cursor cursorDel = sqlDB.rawQuery("DELETE FROM req_ot", null);
            if (cursorDel.getCount() == 0) {
                Log.i(TAG, "Del count: 0");
            } else {
                Log.i(TAG, "Del count: " + cursorDel.getCount());
            }

            try {
                if (resultRequestOT != null) {
                    JSONArray jArray = new JSONArray(resultRequestOT.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        gv.reqotStoreName = json_data.getString("storeLocationName");
                        gv.reqotTimeIn = json_data.getString("otIn");
                        gv.reqotTimeOut = json_data.getString("otOut");
                        gv.reqotDateIn = json_data.getString("dateIn");
                        gv.reqotDateOut = json_data.getString("dateOut");
                        gv.reqotStatus = json_data.getString("status");

                        sqlDB.execSQL("INSERT INTO req_ot" +
                                "(storeName," +
                                "dateIn, " +
                                "dateOut, " +
                                "timeIn, " +
                                "timeOut, " +
                                "reqOTStatus) " +
                                "VALUES" +
                                "('" + gv.reqotStoreName +
                                "', '" + gv.reqotDateIn +
                                "', '" + gv.reqotDateOut +
                                "', '" + gv.reqotTimeIn +
                                "', '" + gv.reqotTimeOut +
                                "', '" + gv.reqotStatus + "');");
                    }
                    Log.i(TAG, "success request overtime");
                } else {
                    Log.i(TAG, "request is NULL");
                }
            } catch (JSONException e) {
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error: Overtime Request" + e);
                }
            }

            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
            sqlDB.close();

        }
    }

    // assignment ksoap for webservice
    public void request_overtime_status() {
        String SOAP_ACTION = "http://tempuri.org/getRequestOvertime";
        String METHOD_NAME = "getRequestOvertime";
        String NAMESPACE = "http://tempuri.org/";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultRequestOT = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Request Overtime Status: " + resultRequestOT);
        } catch (Exception e) {
            Log.i(TAG, "Error Request OT: " + e);
        }
    }

    private class getDeviationClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            request_deviation_status();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            Cursor cursorDel = sqlDB.rawQuery("DELETE FROM req_deviation", null);
            if (cursorDel.getCount() == 0) {
                Log.i(TAG, "Del count: 0");
            } else {
                Log.i(TAG, "Del count: " + cursorDel.getCount());
            }

            try {
                if (resultRequestDeviation != null) {
                    JSONArray jArray = new JSONArray(resultRequestDeviation.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        gv.reqdevStoreName = json_data.getString("storeLocationName");
                        gv.reqdevTimeIn = json_data.getString("selectedTimeIn");
                        gv.reqdevTimeOut = json_data.getString("selectedTimeOut");
                        gv.reqdevDate = json_data.getString("selectedDate");
                        gv.reqdevStatus = json_data.getString("status");
                        gv.reqdevReason = json_data.getString("reason");

                        sqlDB.execSQL("INSERT INTO req_deviation" +
                                "(storeName," +
                                "date, " +
                                "timeIn, " +
                                "timeOut, " +
                                "reason, " +
                                "reqDeviationStatus) " +
                                "VALUES" +
                                "('" + gv.reqdevStoreName +
                                "', '" + gv.reqdevDate +
                                "', '" + gv.reqdevTimeIn +
                                "', '" + gv.reqdevTimeOut +
                                "', '" + gv.reqdevReason +
                                "', '" + gv.reqdevStatus + "');");
                    }
                    Log.i(TAG, "success request deviation");
                } else {
                    Log.i(TAG, "request is null");
                }
            } catch (JSONException e) {
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Deviation to show!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error: Deviation Request" + e);
                }
            }
        }
    }

    // assignment ksoap for webservice
    public void request_deviation_status() {
        String SOAP_ACTION = "http://tempuri.org/getRequestDeviation";
        String METHOD_NAME = "getRequestDeviation";
        String NAMESPACE = "http://tempuri.org/";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultRequestDeviation = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Request Deviation Status: " + resultRequestDeviation);
        } catch (Exception e) {
            Log.i(TAG, "Error Request Deviation: " + e);
        }
    }

    private class getLeaveClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            request_leave_status();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            Cursor cursorDel = sqlDB.rawQuery("DELETE FROM req_leave", null);
            if (cursorDel.getCount() == 0) {
                Log.i(TAG, "Del count: 0");
            } else {
                Log.i(TAG, "Del count: " + cursorDel.getCount());
            }

            try {
                if (resultRequestLeave != null) {
                    JSONArray jArray = new JSONArray(resultRequestLeave.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        gv.reqleaveDateFrom = json_data.getString("dateFrom");
                        gv.reqleaveDateTo = json_data.getString("dateTo");
                        gv.reqleaveDays = json_data.getString("days");
                        gv.reqleaveReason = json_data.getString("reason");
                        gv.reqleaveStatus = json_data.getString("status");

                        sqlDB.execSQL("INSERT INTO req_leave" +
                                "(dateFrom," +
                                "dateTo, " +
                                "days, " +
                                "reason, " +
                                "reqLeaveStatus) " +
                                "VALUES" +
                                "('" + gv.reqleaveDateFrom +
                                "', '" + gv.reqleaveDateTo +
                                "', '" + gv.reqleaveDays +
                                "', '" + gv.reqleaveReason +
                                "', '" + gv.reqleaveStatus + "');");
                    }
                    Log.i(TAG, "success request leave");
                } else {
                    Log.i(TAG, "request is null");
                }
            } catch (JSONException e) {
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Leave list to show!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error: Leave Request" + e);
                }
            }
        }
    }

    // assignment ksoap for webservice
    public void request_leave_status() {
        String SOAP_ACTION = "http://tempuri.org/getRequestAbsent";
        String METHOD_NAME = "getRequestAbsent";
        String NAMESPACE = "http://tempuri.org/";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultRequestLeave = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Request Leave Status: " + resultRequestLeave);
        } catch (Exception e) {
            Log.i(TAG, "Error Request Leave: " + e);
        }
    }

    // delivery class
    private class getDelivery extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            delivery();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            //Open Database
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
            try {
                if (resultDelivery != null) {

                    JSONArray jArray = new JSONArray(resultDelivery.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        gv.dItemCode = json_data.getString("itemCode");
                        gv.dQuantity = json_data.getString("quantity");
                        gv.dDeliveryDate = json_data.getString("deliveryDate");
                        gv.dExpirationDate = json_data.getString("expirationDate");
                        gv.dBoxCase = json_data.getString("boxCase");
                        gv.dLotNumber = json_data.getString("lotNumber");
                        gv.dTag = json_data.getString("tag");

                        sqlDB.execSQL("INSERT INTO delivery" +
                                "(userCode, " +
                                "itemCode, " +
                                "quantity, " +
                                "deliveryDate, " +
                                "expirationDate, " +
                                "boxCase, " +
                                "lotNumber, " +
                                "tag, " +
                                "popupStatus) " +
                                "VALUES" +
                                "('" + userCode +
                                "', '" + gv.dItemCode +
                                "', '" + gv.dQuantity +
                                "', '" + gv.dDeliveryDate +
                                "', '" + gv.dExpirationDate +
                                "', '" + gv.dBoxCase +
                                "', '" + gv.dLotNumber + "', '" + gv.dTag + "'" +
                                ", '1');");
                    }
                    // prgsDlg.dismiss();
                    Log.i(TAG, "success DELIVERY");
                } else {
                    Log.i(TAG, "DELIVERY is NULL");
                }
            } catch (JSONException e) {
                //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "ERROR: DELIVERY");
                }
            }
            //Toast.makeText(MainActivity.this, "" + resultAssignment.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // delivery ksoap for webservice
    public void delivery() {
        String SOAP_ACTION = "http://tempuri.org/getDeliveryJSON";
        String METHOD_NAME = "getDeliveryJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);


            transport.call(SOAP_ACTION, soapEnvelope);
            resultDelivery = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Delivery: " + resultDelivery);
        } catch (Exception ex) {
            Log.e(TAG, "Error Delivery: " + ex.getMessage());
        }
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
            //Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder p = new AlertDialog.Builder(MainActivity.this);
            p.setTitle("No Internet Connection");
            p.setMessage("Please connect to internet\nor mobile data.");
            p.setPositiveButton("OK", null);
            p.show();
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    // expense class
    private class getExpenseType extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            expenseType();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            try {
                //Open Database
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                if (resultExpenseType != null) {
                    JSONArray jArray = new JSONArray(resultExpenseType.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        gv.expenseCode = json_data.getString("expenseCode");
                        gv.expenseType = json_data.getString("expenseType");

                        sqlDB.execSQL("INSERT INTO expensetype" +
                                "(expenseCode, " +
                                "expenseType) " +
                                "VALUES" +
                                "('" + gv.expenseCode +
                                "', '" + gv.expenseType + "');");

                    }
                    // prgsDlg.dismiss();
                    Log.i(TAG, "success EXPENSETYPE");
                } else {
                    Log.i(TAG, "EXPENSETYPE is NULL");
                }
            } catch (JSONException e) {
                //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                }
            }

            //Toast.makeText(MainActivity.this, "" + resultExpenseType.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // expense ksoap for webservice
    public void expenseType() {

        String SOAP_ACTION = "http://tempuri.org/getExpenseTypeJSON";
        String METHOD_NAME = "getExpenseTypeJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            //Request.addProperty("employeeNo", userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultExpenseType = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result ExpenseType: " + resultExpenseType);
        } catch (Exception ex) {
            Log.e(TAG, "Error Expense Type: " + ex.getMessage());
        }
    }

    // app module update class
    private class getAppModule extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            AppModule();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);

            try {
                //Open Database
                sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                if (resultAppModule != null) {

                    JSONArray jArray = new JSONArray(resultAppModule.toString());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        gv.appsModuleID = json_data.getInt("appsModuleID");
                        gv.isVisible = json_data.getInt("isVisible");

                        sqlDB.execSQL("INSERT OR " +
                                "REPLACE INTO customeraccess" +
                                "(customerCode, " +
                                "appsModuleID," +
                                "isVisible) " +
                                "VALUES" +
                                "('" + customerCode +
                                "', '" + gv.appsModuleID +
                                "', '" + gv.isVisible + "');");

                    }
                    HomeIcons();
                    // prgsDlg.dismiss();
                    Log.i(TAG, "success APPSMODULE");
                } else {
                    Log.i(TAG, "APPSMODULE is NULL");
                }
            } catch (JSONException e) {
                //  Toast.makeText(getApplication(),"Error"+ e,Toast.LENGTH_SHORT).show();
                if (!"null".equals(e) || !"".equals(e)) {
                    Toast.makeText(getApplication(), "No Event to show!", Toast.LENGTH_SHORT).show();
                }
            }

            //Toast.makeText(MainActivity.this, "" + resultExpenseType.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // app module ksoap for webservice
    public void AppModule() {
        String SOAP_ACTION = "http://tempuri.org/getAppsModuleJSON";
        String METHOD_NAME = "getAppsModuleJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("customerCode", customerCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultAppModule = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result AppModule: " + resultAppModule);
        } catch (Exception ex) {
            Log.e(TAG, "Error App Module: " + ex.getMessage());
        }
    }

    // permission request
    private void Permissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
        }
    }

    // show input dialog for password before viewing payslip
    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText edtPassword = (EditText) promptView.findViewById(R.id.edtPassword);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // if failled
                        if (edtPassword.getText().toString().trim().length() == 0) {
                            DialogInterface.OnClickListener post = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            showInputDialog();
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder mValid = new AlertDialog.Builder(MainActivity.this);
                            mValid.setTitle("Process failed");
                            mValid.setMessage("Password is required.");
                            mValid.setCancelable(true);
                            mValid.setPositiveButton("Ok", post);
                            mValid.show();
                        } else {
                            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                            Cursor cursor = sqlDB.rawQuery("SELECT " +
                                    "passWord " +
                                    "FROM users " +
                                    "WHERE userCode=?", new String[]{userCode});

                            if (cursor.getCount() != 0) {
                                if (cursor.moveToFirst()) {
                                    // if password is correct
                                    if (cursor.getString(0).equals(edtPassword.getText().toString())) {
                                        //Intent in = new Intent(MainActivity.this, PayslipViewDateActivity.class);
                                        //startActivity(in);
                                        Intent i = new Intent(MainActivity.this, PayslipWebView.class);
                                        startActivity(i);
                                    } else {
                                        DialogInterface.OnClickListener post = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        showInputDialog();
                                                        break;
                                                }
                                            }
                                        };
                                        AlertDialog.Builder mValid = new AlertDialog.Builder(MainActivity.this);
                                        mValid.setTitle("Process failed");
                                        mValid.setMessage("Incorrect password. Please Try Again.");
                                        mValid.setCancelable(true);
                                        mValid.setPositiveButton("Ok", post);
                                        mValid.show();
                                    }
                                }
                            } else {
                                Log.i(TAG, "No user data. - Payslip");
                            }
                            sqlDB.close();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // app version class
    private class AppVersion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            GetAppVer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (resultAppVersion.toString().equals("Outdated")) {
                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface alertDialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    String url = "http://google.com.ph";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    System.exit(0);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder mValid = new AlertDialog.Builder(MainActivity.this);
                    mValid.setTitle(resultAppVersion.toString());
                    mValid.setMessage("Version Outdated. Please download and re-install Automat.");
                    mValid.setCancelable(false);
                    mValid.setPositiveButton("Download", ok);
                    mValid.setNegativeButton("Cancel", ok);
                    mValid.show();
                } else {
                    getData();
                }
            } catch (Exception e) {
                String msg = "Please try again later.";
                AlertDialog.Builder mValid = new AlertDialog.Builder(MainActivity.this);
                mValid.setTitle("Connection timeout");
                mValid.setMessage(msg);
                mValid.setCancelable(true);
                mValid.setPositiveButton("Ok", null);
                mValid.show();
            }
            //Toast.makeText(MainActivity.this, "" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // get app new version ksoap for webservice
    public void GetAppVer() {
        String SOAP_ACTION = "http://tempuri.org/getAppVersion";
        String METHOD_NAME = "getAppVersion";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("appversion", GlobalVar.appVersion);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultAppVersion = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result AppVersion: " + resultAppVersion);
        } catch (Exception ex) {
            Log.e(TAG, "Error App Version: " + ex.getMessage());
        }
        prgsDlg.dismiss();
    }

    private void checkGPS() {
        gps = new GPSTracker(mContext, MainActivity.this);

        // Check if GPS enabled
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);

        if (gpsStatus) {

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS REQUIRED");
            builder.setMessage("For optimum usage of the app. Please make sure GPS is turned on.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();

        }

    }

    // ANNOUNCEMENT START
    private void getItemData() {
        try {
            sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

            cursor = sqlDB.rawQuery("SELECT " +
                    " announceID, " +
                    " announceTitle, " +
                    " announceMessage, " +
                    " announcePostedBy, " +
                    " announceDateCreated " +
                    " FROM announcement", null);

            if (cursor.getCount() == 0) {
                if (haveNetworkConnection(this)) {
                    prgsDlg = new ProgressDialog(MainActivity.this);
                    prgsDlg.setMessage("Please wait...");
                    prgsDlg.setCancelable(true);
                    prgsDlg.show();

                    runAnnouncement Announcement = new runAnnouncement();
                    Announcement.execute();
                } else {
                    Toast.makeText(this, "No connection...", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (cursor.getCount() != 0) {
                    String x = String.valueOf(cursor.getCount());
                    tvAnnouncementBadge.setText(x);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Error in announcment:" + e);
            Toast.makeText(this, "No connection...", Toast.LENGTH_SHORT).show();
        }
    }

    private class runAnnouncement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            annoncementEvent(); // ACTIVITY

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute" + result);
            try {
                if (announcementResult.toString().equals("Failed")) {
                    Toast.makeText(MainActivity.this, "No Announcement Found.", Toast.LENGTH_SHORT).show();
                } else {
                    sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
                    try {
                        JSONArray jArray = new JSONArray(announcementResult.toString());
                        for (int i = 0; i < jArray.length(); i++) {
                            // values
                            JSONObject json_data = jArray.getJSONObject(i);
                            gv.announceID = json_data.getString("announceID");
                            gv.announceTitle = json_data.getString("title");
                            gv.announceMessage = json_data.getString("message");
                            gv.announcePostedBy = json_data.getString("postedBy");
                            gv.announceDateCreated = json_data.getString("dateCreated");

                            messages = messages + 1;

                            try {
                                sqlDB.execSQL("INSERT OR " +
                                        "REPLACE INTO announcement " +
                                        "(announceID, " +
                                        "announceTitle, " +
                                        "announceMessage, " +
                                        "announcePostedBy, " +
                                        "announceDateCreated, " +
                                        "seenStatus) " +
                                        "VALUES " +
                                        "('" + gv.announceID +
                                        "', '" + gv.announceTitle +
                                        "', '" + gv.announceMessage +
                                        "', '" + gv.announcePostedBy +
                                        "', '" + gv.announceDateCreated +
                                        "','UNSEEN');");

                                Log.i("TAG", "Rows affected: " + i + "\n");

                            } catch (Exception e) {
                                Log.i("TAG", "Error in inserting item list data: " + i + "\n" + e);
                            }

                        }

                        String x = String.valueOf(messages);
                        if (messages < 100) {
                            tvAnnouncementBadge.setText(x);
                        } else {
                            tvAnnouncementBadge.setText(x + "+");
                        }

                        prgsDlg.dismiss();
                        sqlDB.close();
                        getItemData();
                    } catch (Exception e) {
                        Log.i("TAG", "Error in inserting announcements: " + e);
                    }
                }
                prgsDlg.dismiss();
            } catch (Exception e) {
                Log.i("TAG", "Error in fetching announcement: " + e);
            }
        }
    }

    public void annoncementEvent() {
        String SOAP_ACTION = "http://tempuri.org/getAnnouncementJSON";
        String METHOD_NAME = "getAnnouncementJSON";
        String NAMESPACE = "http://tempuri.org/";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("employeeNo", userCode);
            Log.i("TAG", "User Code: " + userCode);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

            transport.call(SOAP_ACTION, soapEnvelope);
            announcementResult = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Announcement: " + announcementResult);

        } catch (Exception ex) {

            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
    // ANNOUNCEMENT END CODE

    // check service if running
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /******************************** SYNCING START *********************************/
    //Check in/out
    public class mainAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Syncing data to server");
            progressDialog.setMessage("Please wait while syncing . . .");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(TAG, "doInBackground");

            try {

                Inventory();
//                CheckInOut();
//                syncToLive();
                Expense();
                OSA();
                Freshness();
                NearlyExpired();
                // RTV

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

            Toast.makeText(MainActivity.this, "Syncing finished!", Toast.LENGTH_SHORT).show();

            if (progressDialog.isShowing()) {
                progressDialog.setMessage("DTR Synced!");
                progressDialog.dismiss();
            }
        }
    }

    private void syncToLive() {

    }


    public void CheckInOut() {

        String SOAP_ACTION = "http://tempuri.org/syncDateTimeDataJSON";
        String METHOD_NAME = "syncDateTimeDataJSON";
        String NAMESPACE = "http://tempuri.org/";
        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);


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


                        transport.debug = true;
                        transport.call(SOAP_ACTION, soapEnvelope);

                        resultCheckInOut = (SoapPrimitive) soapEnvelope.getResponse();


                        //SoapObject result = (SoapObject) soapEnvelope.getResponse();
                        //sqlDB.rawQuery("UPDATE dtr SET syncStatus='sync' WHERE dtrID=?", new String[]{dtrID});
                        //Log.i(TAG, dateOut+" / "+timeOut+" / "+locationLatOut+" / "+locationLongOut+" / "+AddressOut+" / "+imageOut);

                        Log.i(TAG, "Result CheckInOut: " + resultCheckInOut.toString());

                        // result of dtr sync
                        try {
                            if (resultCheckInOut.toString().isEmpty()) {
                                Log.d(TAG, "Empty Result");
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

                    } catch (Exception ex) {
                        Log.e(TAG, "Error CheckInOut: " + ex.getMessage());
                    }

                }
                while (curs.moveToNext());

//                if (transport != null) {
//                    transport.reset();
//                    try {
//                        transport.getConnection().disconnect();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }


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

        Cursor cursor = sqlDB.rawQuery("" +
                "SELECT " +
                "expenseID," +
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

                // new inventory
                "inventoryID, " +
                "itemCode, " +
                "storeCode, " +
                "offtake, " +
                "weekNo, " +
                "userCode, " +
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
                "endWarehouseCases, " +
                // max cap
                "daysOutOfStocks," +
                "homeShelf," +
                "secondaryDisplay " +

                "FROM inventory " +
                "WHERE syncStatus=?", new String[]{"not sync"});

        if (cursor.getCount() != 0) {
            progressDialog.setMessage("Syncing inventory ...");
            if (cursor.moveToFirst()) {
                do {

                    String itemCode, storeCode, beginVal, endVal, deliVal, userCode, offTake, weekNo,
                            expiredItems, damagedItems, deliveryReturns, customerReturns, sinventoryID;

                    sinventoryID = cursor.getString(cursor.getColumnIndex("inventoryID"));
                    itemCode = cursor.getString(cursor.getColumnIndex("itemCode"));
                    storeCode = cursor.getString(cursor.getColumnIndex("storeCode"));
                    offTake = cursor.getString(cursor.getColumnIndex("offtake"));
                    weekNo = cursor.getString(cursor.getColumnIndex("weekNo"));
                    userCode = cursor.getString(cursor.getColumnIndex("userCode"));

                    // new fields
                    // beginning
                    String inventoryDate = cursor.getString(cursor.getColumnIndex("inventoryDate"));
                    String sellingAreaPcs = cursor.getString(cursor.getColumnIndex("sellingAreaPcs"));
                    String warehousePcs = cursor.getString(cursor.getColumnIndex("warehousePcs"));
                    String warehouseCases = cursor.getString(cursor.getColumnIndex("warehouseCases"));
                    // deliver
                    String deliveryPcs = cursor.getString(cursor.getColumnIndex("deliveryPcs"));
                    String deliveryCases = cursor.getString(cursor.getColumnIndex("deliveryCases"));
                    String adjustmentPcs = cursor.getString(cursor.getColumnIndex("adjustmentPcs"));
                    // returns
                    String pulloutPcs = cursor.getString(cursor.getColumnIndex("pulloutPcs"));
                    String badOrderPcs = cursor.getString(cursor.getColumnIndex("badOrderPcs"));
                    String damagedItemPcs = cursor.getString(cursor.getColumnIndex("damagedItemPcs"));
                    String expiredItemsPcs = cursor.getString(cursor.getColumnIndex("expiredItemsPcs"));
                    // ending
                    String endSellingAreaPcs = cursor.getString(cursor.getColumnIndex("endSellingAreaPcs"));
                    String endWarehousePcs = cursor.getString(cursor.getColumnIndex("endWarehousePcs"));
                    String endWarehouseCases = cursor.getString(cursor.getColumnIndex("endWarehouseCases"));
                    // max cap
                    String daysOutOfStocks = cursor.getString(cursor.getColumnIndex("daysOutOfStocks"));
                    String homeShelf = cursor.getString(cursor.getColumnIndex("homeShelf"));
                    String secondaryDisplay = cursor.getString(cursor.getColumnIndex("secondaryDisplay"));

                    try {

                        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                        Request.addProperty("itemCode", itemCode);
                        Request.addProperty("storeLocCode", storeCode);
                        Request.addProperty("beginIn", "0");
                        Request.addProperty("endIn", "0");
                        Request.addProperty("delivery", "0");
                        Request.addProperty("offTake", offTake);
                        Request.addProperty("weekNo", weekNo);
                        Request.addProperty("employeeNo", userCode);
                        Request.addProperty("expiredItems", "0");
                        Request.addProperty("damageItems", "0");
                        Request.addProperty("deliveryReturns", "0");
                        Request.addProperty("customerReturns", "0");

                        // new fields
                        Log.i("TAG", "Inventory Date: " + inventoryDate);
                        Request.addProperty("inventoryDate", inventoryDate);
                        Request.addProperty("sellingAreaPcs", sellingAreaPcs);
                        Request.addProperty("warehousePcs", warehousePcs);
                        Request.addProperty("warehouseCases", warehouseCases);
                        Request.addProperty("deliveryPcs", deliveryPcs);
                        Request.addProperty("deliveryCases", deliveryCases);
                        Request.addProperty("adjustmentPcs", adjustmentPcs);
                        Request.addProperty("pulloutPcs", pulloutPcs);
                        Request.addProperty("badOrderPcs", badOrderPcs);
                        Request.addProperty("damagedItemPcs", damagedItemPcs);
                        Request.addProperty("expiredItemsPcs", expiredItemsPcs);
                        Request.addProperty("endingSellingAreaPcs", endSellingAreaPcs);
                        Request.addProperty("endingWarehousePcs", endWarehousePcs);
                        Request.addProperty("endingWarehouseCases", endWarehouseCases);
                        Request.addProperty("daysOS", daysOutOfStocks);
                        Request.addProperty("homeShelf", homeShelf);
                        Request.addProperty("secondaryDisplay", secondaryDisplay);

                        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        soapEnvelope.dotNet = true;
                        soapEnvelope.setOutputSoapObject(Request);

                        HttpTransportSE transport = new HttpTransportSE(currentIPAddress);

                        transport.call(SOAP_ACTION, soapEnvelope);
                        resultInventory = (SoapPrimitive) soapEnvelope.getResponse();

                        Log.i(TAG, "Result Inventory: " + resultInventory);
                        sqlDB.execSQL("DELETE FROM inventory  " +
                                "WHERE inventoryID = '" + sinventoryID + "' ");

                    } catch (Exception ex) {
                        Log.e(TAG, "Error Inventory: " + ex.getMessage());
                    }

                    /*
                    sqlDB.execSQL("UPDATE headerinventory " +
                            "SET status='sync' " +
                            "WHERE id='"+cursor.getString(0)+"'");
                    */
                    /*
                    sqlDB.execSQL("UPDATE inventory SET " +
                            "syncStatus = 'SYNC' " +
                            "WHERE inventoryID = '" + cursor.getString(0) + "';");
                            */

                } while (cursor.moveToNext());
            }
            progressDialog.dismiss();
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
                "productionDate, " +
                "dateRecorded, " +
                "shelfLife " +
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
                    String shelfLife = cursor.getString(cursor.getColumnIndex("shelfLife"));
                    String dateRecorded = cursor.getString(cursor.getColumnIndex("dateRecorded"));

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
                        Request.addProperty("dateRecorded", dateRecorded);
                        Request.addProperty("shelfLife", shelfLife);

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

    /************************* END SYNCING ******************************************/

    /*font family for nav*/
    private void applyFontToMenuItem(MenuItem mi) {
        //Typeface font = Typeface.createFromAsset(getAssets(), "ds_digi_b.TTF");
        Typeface font = Typeface.SERIF;
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

}