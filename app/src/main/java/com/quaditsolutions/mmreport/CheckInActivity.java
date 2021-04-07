package com.quaditsolutions.mmreport;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Khyrz on 9/9/2017.
 */

public class CheckInActivity extends AppCompatActivity {

    SoapPrimitive resultCheckInOut;
    private int flagRetryVal = 0;
    ProgressDialog progressDialog;

    ImageView imgViewCapture;
    TextView txtTimein, txtDate, txtLocation, lblTimeIn;
    ImageButton btnCamera;
    Button btnSave, btnCancel;
    EditText etOtherStore;

    private Uri fileUri;
    private String imageName;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

    String fullAddress, userCode, dateIn, timeIn, storeLocation, companyCode,
            fullname, storeLocCode, spinnerSelectedItem, dtrID, encodedImage, dayNow,
            storeNameFromSpinner, selfieStatus = "", flagSelfieNetworkStatus = "", currentIPAddress;

    double latitude, longitude;
    int inout = -1;

    TextView txtLatLong;
    TextView t_longtitude, t_latitude;

    Geocoder geocoder;
    List<Address> addressList;
    Context mContext;
    GPSTracker gps = new GPSTracker();

    Spinner spinner;

    SQLiteDatabase sqlDB;
    Cursor cursor;

    String TAG = "Respond";
    GlobalVar gv = new GlobalVar();
//    GetLocation getLocation = new GetLocation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkCompany();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_layout);

        typcast();
        animateButtonCamera();

        getLatiLongi(); // gets address on load of activty
//        Toast.makeText(getApplicationContext(), "" + getLocation.address , Toast.LENGTH_SHORT).show();


        //SPINNER ITEMS
        getAllSpinnerContent();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = store.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);
        dayNow = dayFormat.format(now);
        String[] dtSplit = dateTime.split(" ");
        dateIn = dtSplit[0];
        timeIn = dtSplit[1] + " " + dtSplit[2];


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
                        "ORDER BY " +
                        "dtrID DESC LIMIT 1",
                new String[]{userCode, timeOutStatic, dateOutStatic});

        if (cursor.getCount() == 0) {
            lblTimeIn.setText("Time In:");
            inout = 0;
        } else {
            if (cursor.moveToLast()) {
                String timeOut = cursor.getString(1);
                // if (timeOut == null || timeOut.equals("00:00:00")) {
                if (timeOut == null || timeOut.equals("00:00:00")) {
                    lblTimeIn.setText("Time Out:");
                    dtrID = cursor.getString(2);
                    inout = 1;
                } else {
                    lblTimeIn.setText("Time In:");
                    inout = 0;
                }
            }
        }

        // refreshLocation();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String checkInOutStatus = lblTimeIn.getText().toString().trim();
                //Toast.makeText(mContext, checkInOutStatus, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
                if (checkInOutStatus.equals("Time Out:")) {
                    b.setTitle("Time Out");
                    b.setMessage("Are you sure you want to Time Out?");
                    b.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkInOutCamera();
                        }
                    });
                } else if (checkInOutStatus.equals("Time In:")) {
                    b.setTitle("Time In");
                    b.setMessage("Are you sure you want to Time In?");
                    b.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkInOutCamera();
                        }
                    });
                } else {

                }
                b.setNegativeButton("CANCEL", null);
                b.show();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String testValueOfLocation = txtLocation.getText().toString().trim();

                    if (testValueOfLocation.equals("")) {

                        AlertDialog.Builder d = new AlertDialog.Builder(CheckInActivity.this);
                        d.setTitle("No Location");
                        d.setMessage("Current location not found. \nPlease try again.");
                        d.setCancelable(false);
                        d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                /*
                                String imageFolderPath = Environment.getExternalStorageDirectory().toString() + "/.khyrz";
                                File imageFolder = new File(imageFolderPath);
                                imageFolder.mkdirs();

                                imageName = new Date().toString() + ".png";
                                fileUri = Uri.fromFile(new File(imageFolderPath, imageName));

                                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                startActivityForResult(openCamera, 111);
                                getLatiLongi();
                                getAddress();x

                                */

                                Log.i("TAG", "Flag Retry Value: " + flagRetryVal);

                                if (flagRetryVal < 1) { // 2 and 1 only
                                    flagRetryVal = flagRetryVal + 1;
                                } else {

                                    flagRetryVal = flagRetryVal + 1;
                                    Toast.makeText(CheckInActivity.this, flagRetryVal + "Attempts", Toast.LENGTH_SHORT).show();

                                    final SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                                    fullname = sp.getString("fullname", null);

                                    AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
                                    b.setTitle("GPS Problem");
                                    b.setMessage(fullname + " for persistent GPS problems, please screenshot this for proof."
                                            + "\n\n" + "Time: " + timeIn + "" +
                                            "\n" + "Date: " + dateIn);

                                    b.setPositiveButton("SAVE W/OUT ADDRESS", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            fullAddress = "GPS Problem";
                                            switch (inout) {
                                                case 0:
                                                    checkIn();
                                                    break;
                                                case 1:
                                                    checkOut();
                                                    break;
                                                default:
                                                    checkIn();
                                            }
                                        }
                                    });
                                    b.setNegativeButton("CANCEL", null);
                                    b.show();

                                }

                                // refreshLocation();

                            }
                        });
                        d.setNegativeButton("No", null);
                        d.show();
                    } else {
                        switch (inout) {
                            case 0:
                                checkIn();
                                break;
                            case 1:
                                checkOut();
                                break;
                            default:
                                checkIn();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Error Saving DTR: " + e, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                txtTimein.setText("hh:mm a");
                txtLocation.setText("Location");
                txtDate.setText("yyyy-MM-dd");
                imgViewCapture.setImageResource(R.drawable.defaultphoto);
                spinner.setEnabled(true);
                imgViewCapture.setBackgroundResource(R.drawable.circle_defaulticon);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /*
    private void refreshLocation() {
        progressDialog = new ProgressDialog(CheckInActivity.this);
        progressDialog.setTitle("Loading location.");
        progressDialog.setMessage("Please wait . . .");
        progressDialog.show();

        // get location
        //getLatiLongi();
        getAddress();
        try {

            Log.i(TAG, "full address - " + fullAddress);

            if (fullAddress != null) {
                Log.i(TAG, "full address - " + fullAddress);
                txtLocation.setText(fullAddress);
            } else {
                checkGPS();
                txtLocation.setText(fullAddress);
            }
            progressDialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            progressDialog.dismiss();
        }
        // end location
    }
    */

    private void typcast() {
        imgViewCapture = (ImageView) findViewById(R.id.imgViewCapture);
        txtTimein = (TextView) findViewById(R.id.txtTimein);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        lblTimeIn = (TextView) findViewById(R.id.lblTimeIn);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        spinner = (Spinner) findViewById(R.id.spinner);
        geocoder = new Geocoder(this, Locale.getDefault());
        mContext = this;
        btnCamera.setVisibility(View.VISIBLE);
        etOtherStore = (EditText) findViewById(R.id.etOtherStore);
        btnSave.setEnabled(true);
        txtLatLong = (TextView)findViewById(R.id.txtLatLong);
    }

    private void checkCompany() {
        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);
        currentIPAddress = sp.getString("ipAddress", null);

        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                //imgViewHome.setImageResource(R.drawable.prestige);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                }
                break;

            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                //imgViewHome.setImageResource(R.drawable.regcris);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                }
                break;

            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                //imgViewHome.setImageResource(R.drawable.tmarks);
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                }
                break;

            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }
    private void animateButtonCamera() {
        // animate btncamera
        Animation anim = AnimationUtils.loadAnimation(CheckInActivity.this, R.anim.bounce);
        btnCamera.setAnimation(anim);
    }
    public void checkInOutCamera() {

        String imageFolderPath = Environment.getExternalStorageDirectory().toString() + "/.khyrz";
        File imageFolder = new File(imageFolderPath);
        imageFolder.mkdirs();

        imageName = new Date().toString() + ".png";
        fileUri = Uri.fromFile(new File(imageFolderPath, imageName));

        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(openCamera, 111);
        // getLatiLongi();
        getAddress();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        getLatiLongi();
        getAddress();
        */

    }

    public void getAllSpinnerContent() {

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(mContext, R.layout.spinnner_bg, spinnerContent);
        spinner.setAdapter(adapter);
        //spinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            Log.i(TAG, "full address - " + fullAddress);

            if (fullAddress != null) {
                Log.i(TAG, "full address - " + fullAddress);
                txtLocation.setText(fullAddress);
            } else {
                // checkGPS();
                if (fullAddress.equalsIgnoreCase("")) {
                    txtLocation.setText("No Address Found.");
                } else {
                    txtLocation.setText(fullAddress);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {

            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                final Bitmap photo = BitmapFactory.decodeFile(fileUri.getPath(), options);

                ExifInterface ei = new ExifInterface((fileUri.getPath()));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap;  // = null;
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(photo, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(photo, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(photo, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = photo;
                }

                imgViewCapture.setImageBitmap(rotatedBitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Log.i("TAG", "Base64" + encodedImage);

            } catch (Exception e) {
                Log.e(TAG, "IMAGE NOT SAVE - " + e.toString());
            }

            try {

                File fdelete = new File(fileUri.getPath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.i(TAG, "PHOTO DELETED!");
                    } else {
                        Log.i(TAG, "NOT PHOTO DELETED!");
                    }
                }

            } catch (Exception e) {

            }

            imgViewCapture.setBackgroundColor(000);
            txtTimein.setText(timeIn);
            txtDate.setText(dateIn);

            btnCancel.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setEnabled(true);
            btnCancel.setEnabled(true);

            btnCamera.setVisibility(View.INVISIBLE);
            spinner.setEnabled(false);

        } else {
            Toast.makeText(CheckInActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // fix auto rotate in some camera
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void getLatiLongi() {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckInActivity.this,
                    new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA
                            }, 1);
        } else {
            checkGPS();
        }

    }

    private void checkGPS() {
        gps = new GPSTracker(CheckInActivity.this, CheckInActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        /*else if(latitude == 0.0 || longitude == 0.0)
        {
            // Get the location manager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                fullAddress = "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
            }
            else
            {
                fullAddress = "Location not available.";
            }
        }*/
        else {
            // Ask user to enable GPS/network in settings.
            //gps.showSettingsAlert();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
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
    private void getAddress() {

        GetLocation getLocation = new GetLocation();

        try {
            if (latitude != 0.0 && longitude != 0.0) {

                /*addressList = geocoder.getFromLocation(latitude, longitude, 1);
                String addressStr = addressList.get(0).getAddressLine(0);
                fullAddress = addressStr;*/

                if (haveNetworkConnection()) {
                    // glagSelfieNetworkStatus = "Online";
                    //Toast.makeText(CheckInActivity.this, flagSelfieNetworkStatus, Toast.LENGTH_SHORT).show();
//                    addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String addressStr = addressList.get(0).getAddressLine(0);
                    //String areaStr = addressList.get(0).getLocality();
                    //String cityStr = addressList.get(0).getAdminArea();
                    //String countryStr = addressList.get(0).getCountryName();
                    //String postalcodeStr = addressList.get(0).getPostalCode();
                    fullAddress = addressStr;//+", "+areaStr+", "+cityStr+", "+countryStr +", "+postalcodeStr;

                        txtLatLong.setText("Latitude: " + latitude + " " + "Longitude: " + longitude);

                    /*if (fullAddress.equalsIgnoreCase("") || fullAddress == null){
                        fullAddress = "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
                    }else{
                        fullAddress = addressStr;//+", "+areaStr+", "+cityStr+", "+countryStr +", "+postalcodeStr;
                    }*/
                } else {
                    //flagSelfieNetworkStatus = "Offline";
                    //Toast.makeText(CheckInActivity.this, flagSelfieNetworkStatus, Toast.LENGTH_SHORT).show();
                    fullAddress = "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
//                    t_longtitude.setText("" + longitude);
//                    t_latitude.setText("" + latitude);
                    /*
                    addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String addressStr = addressList.get(0).getAddressLine(0);
                    fullAddress = addressStr;//+", "+areaStr+", "+cityStr+", "+countryStr +", "+postalcodeStr;
                    */
                }
                /*
                try {
                    addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String addressStr = addressList.get(0).getAddressLine(0);
                    fullAddress = addressStr;
                } catch (Exception e) {
                    fullAddress = "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
                    Log.i("TAG", "Error in comverting longlat to address: " + e);
                }
                */
            } else {
                getLatiLongi();
            }
        } catch (Exception e) {
            Log.i("TAG", "Error in getting address: " + e);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGPS();
                } else {
                    // permission denied
                    Toast.makeText(CheckInActivity.this, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ("WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void syncDTRonSaved() {
        if (haveNetworkConnection()) {
            AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
            b.setTitle("Sync DTR to Server");
            b.setMessage("Do you want to sync this DTR?");
            b.setPositiveButton("SYNC", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    geocoder = new Geocoder(CheckInActivity.this, Locale.getDefault());
                    syncDTR mat = new syncDTR();
                    mat.execute();
                }
            });
            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(CheckInActivity.this, MainActivity.class));
                    finish();
                }
            });
            b.show();
        }
    }

    private void checkIn() {
        try {

            AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
            b.setTitle("Are you sure you want to save this DTR?");
            b.setMessage(""+lblTimeIn.getText().toString()+" "+txtTimein.getText().toString().trim());
            b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("TAG", "Full Address: " + fullAddress);

                    storeNameFromSpinner = spinner.getSelectedItem().toString();

                    if (storeNameFromSpinner.equals("")) {
                        storeNameFromSpinner = etOtherStore.getText().toString().trim();
                    } else {

                    }

                    Time timenow = new Time(System.currentTimeMillis());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String stringFormat = timeFormat.format(timenow);

                    if (fullAddress == null || fullAddress.equalsIgnoreCase("null")){
                        fullAddress = "No Address Found.";
                    }

                    sqlDB.execSQL("INSERT INTO dtr" +
                            "(userCode, " +
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
                            "addressIn, " +
                            "addressOut, " +
                            "statusIN, " +
                            "storeLocCode," +
                            "storeName," +
                            "imageIn, " +
                            "imageOut) " +
                            "VALUES" +
                            "('" + userCode + "', '"
                            + flagSelfieNetworkStatus + "', '"
                            + " ', '"
                            + stringFormat + "', "
                            + "'00:00:00', '" // static 00:00:00 this is time in without out
                            + dateIn + "', " +
                            "'0000-00-00', '"
                            + latitude + "', " +
                            "'', '"
                            + longitude + "', " +
                            "'', '"
                            + fullAddress + "', " +
                            "'', "
                            + "'not sync', " +
                            "'" + spinnerSelectedItem + "', " +
                            "'" + storeNameFromSpinner + "', " +
                            "'" + encodedImage + "', " +
                            "'')");

                    SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("storeLocCode", spinnerSelectedItem);
                    editor.putString("storeLocName", storeNameFromSpinner);
                    editor.apply();

                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface alertDialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    syncDTRonSaved();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder mValid = new AlertDialog.Builder(CheckInActivity.this);
                    mValid.setTitle("Time In Process");
                    mValid.setMessage("Saved!");
                    mValid.setCancelable(true);
                    mValid.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(CheckInActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                    mValid.show();
                }
            });
            b.setNegativeButton("CANCEL",null);
            b.show();

        } catch (Exception e) {
            Log.i("TAG", "Insert Error" + e);
            // Toast.makeText(CheckInActivity.this, "Store "+storeNameFromSpinner, Toast.LENGTH_SHORT).show();
        }
    }
    private void checkOut() {

        AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
        b.setTitle("Are you sure you want to save this DTR?");
        b.setMessage(""+lblTimeIn.getText().toString()+" "+txtTimein.getText().toString().trim());
        b.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Time timenow = new Time(System.currentTimeMillis());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String stringFormat = timeFormat.format(timenow);

                        if (fullAddress == null || fullAddress.equalsIgnoreCase("null")){
                            fullAddress = "No Address Found.";
                        }

                        sqlDB.execSQL("UPDATE dtr SET " +
                                "selfieNetworkStatusOUT = '" + flagSelfieNetworkStatus + "', " +
                                "timeOut = '" + stringFormat + "', " +
                                "dateOut = '" + dateIn + "', " +
                                "locationLongOut= '" + longitude + "', " +
                                "locationLatOut= '" + latitude + "', " +
                                "addressOut= '" + fullAddress + "', " +
                                "imageOut= '" + encodedImage + "', " +
                                "statusOUT='not sync' " +
                                "WHERE " +
                                "userCode='" + userCode + "' " +
                                "AND dtrID='" + dtrID + "';");

                        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        //editor.remove("storeLocCode").apply();
                        editor.putString("storeLocCode", "");
                        editor.apply();

                        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface alertDialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        syncDTRonSaved();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder mValid = new AlertDialog.Builder(CheckInActivity.this);
                        mValid.setTitle("Time Out Process");
                        mValid.setMessage("Saved!");
                        mValid.setCancelable(true);
                        mValid.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(CheckInActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                        mValid.show();
                    }
                });
        b.setNegativeButton("CANCEL",null);
        b.show();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        startActivity(new Intent(CheckInActivity.this, MainActivity.class));
        finish();

        //startActivity(new Intent(CheckInActivity.this, MainActivity.class));
        //finish();

        /*
        if (haveNetworkConnection()) {
            AlertDialog.Builder b = new AlertDialog.Builder(CheckInActivity.this);
            b.setTitle("Sync DTR to Server");
            b.setMessage("Are you sure you want to sync DTR?");
            b.setPositiveButton("SYNC", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    geocoder = new Geocoder(CheckInActivity.this, Locale.getDefault());
                    syncDTR mat = new syncDTR();
                    mat.execute();
                }
            });
            b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(CheckInActivity.this, MainActivity.class));
                    finish();
                }
            });
            b.show();
        } else {
            Toast.makeText(CheckInActivity.this, "Poor Internet Connection.", Toast.LENGTH_SHORT).show();
        }
        */

        return super.onOptionsItemSelected(item);
    }

    // DTR SYNCING
    public class syncDTR extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(CheckInActivity.this);
            progressDialog.setTitle("Syncing DTR to server");
            progressDialog.setMessage("Please wait while syncing . . .");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CheckInOut();
            } catch (Exception e) {
                Log.i("TAG", "DTR Module: Error in syncing DTR to server: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            startActivity(new Intent(CheckInActivity.this, MainActivity.class));
            finish();
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
                        "ORDER BY dateIn, timeIn",
                new String[]{"not sync", "not sync", userCode});

        if (curs.getCount() != 0) {
            progressDialog.setMessage("Syncing DTR . . .");
            //Log.d(TAG, "CheckInOut: "+curs.getCount());
            if (curs.moveToFirst()) {
                do {
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
            }

            progressDialog.dismiss();
        } else {
            Log.d(TAG, "CheckInOut: No record(s) to sync.");
            Toast.makeText(CheckInActivity.this, "No record(s) to sync.", Toast.LENGTH_SHORT).show();
        }
        curs.close();

    }

}