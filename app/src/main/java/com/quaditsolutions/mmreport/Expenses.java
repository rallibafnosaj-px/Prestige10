package com.quaditsolutions.mmreport;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.Manifest;
import android.app.Activity;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Array;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quaditsolutions.mmreport.CheckInActivity.rotateImage;

public class Expenses extends AppCompatActivity {

    RequestQueue requestQueue;

    DecimalFormat df;

    ImageView imgViewCapture;
    EditText editTextLocation, editTextAmount, editTextReimbursable, ediTextNotes, edtOtherExpenseType,
            etTo, etFrom;
    TextView tvMeansOfTransportation, tvAmount, tvOtherExpenseType, tvFrom, tvTo, tvNoteLbl;
    Button btnSubmit, btnCamera, btnReviewExpense;
    Double amount, reimbursable;
    String receipt, notes, companyCode, storeLocation, spinnerSelectedItem,
            spinnerSelectedItem2, userCode, firstName, lastName, middleName,
            expenseCode, date, time, customerCode, encodedImage = "", employeeCode, extype = "",
            meanstype = "", withReceipt = "", withMeansOrNone = "", withMeansTransportation = "",
            receiptImage = "empty", strOtherExpenseType, storeLocCode, spinnerSelectedStore;
    SQLiteDatabase sqlDB;
    Cursor cursor, cursor2;
    Spinner expense_type, store_location, meansOfTransportation;
    Switch switch_receipt;
    Context mContext;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    //  List<String> categories_positon,getCategories_partylist;
    private String imageName;
    private Uri fileUri;
    final int PIC_CROP = 2;
    ProgressDialog pDialog;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Permission();

        final SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        customerCode = sp.getString("customerCode", null);

        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        expense_type = (Spinner) findViewById(R.id.spinnerExpenseType);
        meansOfTransportation = (Spinner) findViewById(R.id.spinnerMeansOfTransportation);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        ediTextNotes = (EditText) findViewById(R.id.ediTextNotes);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        switch_receipt = (Switch) findViewById(R.id.switchReceipt);
        imgViewCapture = (ImageView) findViewById(R.id.imgViewCapture);
        //btnReviewExpense = (Button) findViewById(R.id.btnReviewExpense);
        tvAmount = (TextView) findViewById(R.id.lblAmount);
        edtOtherExpenseType = (EditText) findViewById(R.id.edtOtherExpenseType);
        tvOtherExpenseType = (TextView) findViewById(R.id.tvOtherExpenseType);
        spinner = (Spinner) findViewById(R.id.spinner);
        tvFrom = (TextView) findViewById(R.id.tvFromLbl);
        tvTo = (TextView) findViewById(R.id.tvToLbl);
        etFrom = (EditText) findViewById(R.id.etFrom);
        etTo = (EditText) findViewById(R.id.etTo);
        tvNoteLbl = (TextView) findViewById(R.id.tvNoteLbl);

        spinnerLoad();

        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);
        String[] dtSplit = dateTime.split(" ");
        date = dtSplit[0];

        tvMeansOfTransportation = (TextView) findViewById(R.id.tvMeansOfTransportation);
        df = new DecimalFormat("0.00");

        getAllSpinnerContent();
        getSpinnerExpenseType();

        // spinner action on item selected listener
        expense_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                SpinnerKeyValue store = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedItem = store.getId();

                extype = expense_type.getSelectedItem().toString();

                if (extype.equalsIgnoreCase("Transportation")) {
                    meansOfTransportation.setVisibility(View.VISIBLE);
                    tvMeansOfTransportation.setVisibility(View.VISIBLE);

                    // from and to
                    tvFrom.setVisibility(View.VISIBLE);
                    tvTo.setVisibility(View.VISIBLE);
                    etFrom.setVisibility(View.VISIBLE);
                    etTo.setVisibility(View.VISIBLE);
                    tvNoteLbl.setVisibility(View.GONE);
                    ediTextNotes.setVisibility(View.GONE);

                    withMeansOrNone = meanstype;
                } else if (extype != "Transportation") {

                    // from and to
                    tvFrom.setVisibility(View.GONE);
                    tvTo.setVisibility(View.GONE);
                    etFrom.setVisibility(View.GONE);
                    etTo.setVisibility(View.GONE);

                    edtOtherExpenseType.setVisibility(View.GONE);
                    tvOtherExpenseType.setVisibility(View.GONE);

                    ediTextNotes.setVisibility(View.VISIBLE);
                    tvNoteLbl.setVisibility(View.VISIBLE);

                    notes = ediTextNotes.getText().toString();

                    withMeansOrNone = "None";
                    meansOfTransportation.setVisibility(View.GONE);
                    tvMeansOfTransportation.setVisibility(View.GONE);

                    if (extype.equalsIgnoreCase("Other")) {
                        withMeansOrNone = "None";
                        edtOtherExpenseType.setVisibility(View.VISIBLE);
                        tvOtherExpenseType.setVisibility(View.VISIBLE);
                    } else if (extype != "Other") {
                        withMeansOrNone = "None";
                        edtOtherExpenseType.setVisibility(View.GONE);
                        tvOtherExpenseType.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        meansOfTransportation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String means = meansOfTransportation.getSelectedItem().toString();
                if (means.equalsIgnoreCase("Walking")) {
                    Toast.makeText(Expenses.this, "Receipt not required!", Toast.LENGTH_SHORT).show();
                    // hide fields not needed for walking
                    editTextAmount.setVisibility(View.GONE);
                    tvAmount.setVisibility(View.GONE);
                    switch_receipt.setVisibility(View.GONE);
                    imgViewCapture.setVisibility(View.GONE);
                    btnCamera.setVisibility(View.GONE);
                } else {
                    Toast.makeText(Expenses.this, "Receipt required!", Toast.LENGTH_SHORT).show();
                    // hide fields not needed for walking
                    editTextAmount.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.VISIBLE);
                    switch_receipt.setVisibility(View.VISIBLE);
                    imgViewCapture.setVisibility(View.VISIBLE);
                    btnCamera.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        store_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerKeyValue2 store = (SpinnerKeyValue2) parent.getSelectedItem();
                spinnerSelectedItem2 = store.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch_receipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_receipt.isChecked()) {
                    receipt = switch_receipt.getTextOn().toString();
                    btnCamera.setVisibility(View.VISIBLE);
                    imgViewCapture.setVisibility(View.VISIBLE);
                    //btnReviewExpense.setVisibility(View.VISIBLE);
                    withReceipt = "1";
                } else {
                    receipt = switch_receipt.getTextOff().toString();
                    btnCamera.setVisibility(View.GONE);
                    imgViewCapture.setVisibility(View.GONE);
                    //btnReviewExpense.setVisibility(View.GONE);
                    withReceipt = "0";
                }
            }
        });

        //btnReviewExpense.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        startActivity(new Intent(Expenses.this, ReviewExpenseBeforeGoingToPendingSync.class));
        //    }
        //});

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String error = "Required field";
                if (extype.equalsIgnoreCase("Transportation")) {
                    notes = "From: " + etFrom.getText().toString() + "" +
                            "\nTo: " + etTo.getText().toString();
                } else {
                    notes = ediTextNotes.getText().toString();
                }

                if (ediTextNotes.getText().toString().equalsIgnoreCase("")) {
                    ediTextNotes.setError(error);
                    ediTextNotes.requestFocus();
                } else if (etFrom.getText().toString().equalsIgnoreCase("")) {
                    etFrom.setError(error);
                    etFrom.requestFocus();
                } else if (etTo.getText().toString().equalsIgnoreCase("")) {
                    etTo.requestFocus();
                    etTo.setError(error);
                }

                if (editTextAmount.getText().toString().equalsIgnoreCase("0") ||
                        editTextAmount.getText().toString().equalsIgnoreCase("") ||
                        editTextAmount.getText().toString().equalsIgnoreCase("00") ||
                        editTextAmount.getText().toString().equalsIgnoreCase("000") ||
                        editTextAmount.getText().toString().equalsIgnoreCase("0000") ||
                        editTextAmount.getText().toString().equalsIgnoreCase("00000")) {
                    editTextAmount.setError(error);
                    editTextAmount.requestFocus();
                } else if (switch_receipt.isChecked() && receiptImage.equalsIgnoreCase("empty")) {
                    Toast.makeText(Expenses.this, "Please take picture of your receipt.", Toast.LENGTH_LONG).show();
                } else {
                    Date now = new Date(System.currentTimeMillis());
                    Time timenow = new Time(System.currentTimeMillis());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    time = timeFormat.format(timenow);
                    String dateTime = dateFormat.format(now);
                    String[] dtSplit = dateTime.split(" ");
                    date = dtSplit[0];

                    amount = Double.parseDouble(editTextAmount.getText().toString());
                    //expenseCode = spinnerSelectedItem;
                    // location=editTextLocation.getText().toString();
                    //notes = ediTextNotes.getText().toString();
                    //storeLocation = GlobalVar.storeLoc;
                    meanstype = meansOfTransportation.getSelectedItem().toString();
                    strOtherExpenseType = edtOtherExpenseType.getText().toString().trim();

                    //       sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                    if (withMeansOrNone == "None") {

                        if (strOtherExpenseType.equalsIgnoreCase("")) {
                            withMeansTransportation =
                                    "Expense Type: " + extype + "\n" +
                                            "Amount: " + String.valueOf(df.format(amount)) + "\n" +
                                            "Note: " + notes + "\n";
                            meanstype = "";
                        } else {
                            withMeansTransportation =
                                    "Expense Type: " + extype + "\n" +
                                            "Other Expense Type: " + strOtherExpenseType + "\n" +
                                            "Amount: " + String.valueOf(df.format(amount)) + "\n" +
                                            "Note: " + notes + "\n";
                            meanstype = "";
                        }

                    } else {

                        if (strOtherExpenseType.equalsIgnoreCase("")) {

                            meanstype = meansOfTransportation.getSelectedItem().toString();
                            //Toast.makeText(Expenses.this, meanstype, Toast.LENGTH_SHORT).show();
                            withMeansTransportation =
                                    "Expense Type: " + extype + "\n" +
                                            "Means: " + meanstype + "\n" +
                                            "Amount: " + String.valueOf(df.format(amount)) + "\n" +
                                            "Note: " + notes + "\n";

                        } else {

                            meanstype = meansOfTransportation.getSelectedItem().toString();
                            //Toast.makeText(Expenses.this, meanstype, Toast.LENGTH_SHORT).show();
                            withMeansTransportation =
                                    "Expense Type: " + extype + "\n" +
                                            "Other Expense Type: " + strOtherExpenseType + "\n" +
                                            "Means: " + meanstype + "\n" +
                                            "Amount: " + String.valueOf(df.format(amount)) + "\n" +
                                            "Note: " + notes + "\n";

                        }

                    }

                    AlertDialog.Builder expDg = new AlertDialog.Builder(Expenses.this);
                    expDg.setTitle("Review Expense Details");
                    expDg.setMessage(withMeansTransportation);
                    expDg.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlDB.execSQL("INSERT INTO expenses" +
                                    "(employeeCode, " +
                                    "date, " +
                                    "expenseCode, " +
                                    "expenseType, " +
                                    "meansOfTransportations, " +
                                    "client, " +
                                    "storeLocation, " +
                                    "time, " +
                                    "amount, " +
                                    "receipt, " +
                                    "notes, " +
                                    "status, " +
                                    "syncStatus, " +
                                    "attachments," +
                                    "storeLocCode) " +
                                    "VALUES" +
                                    "('" + userCode +
                                    "','" + date +
                                    "','" + spinnerSelectedItem +
                                    "','" + extype +
                                    "','" + meanstype +
                                    "','" + customerCode +
                                    "','" + spinnerSelectedItem2 +
                                    "','" + time +
                                    "','" + String.valueOf(df.format(amount)) +
                                    "','" + withReceipt +
                                    "','" + notes +
                                    "','OPEN','not sync', " +
                                    "'" + encodedImage + "'," +
                                    "'" + spinnerSelectedStore + "');");

                            AlertDialog.Builder mExit = new AlertDialog.Builder(Expenses.this);
                            mExit.setTitle("Process completed");
                            mExit.setMessage("Saved!");
                            mExit.setCancelable(false);
                            mExit.setPositiveButton("Ok", null);
                            mExit.show();

                            editTextAmount.setText("");
                            editTextAmount.requestFocus();
                            ediTextNotes.setText("");
                            imgViewCapture.setVisibility(View.VISIBLE);
                            if (switch_receipt.isChecked()) {
                                imgViewCapture.setImageResource(R.drawable.defaultphoto);
                            } else {
                                imgViewCapture.setVisibility(View.GONE);
                            }

                            receiptImage = "empty";
                        }
                    });
                    expDg.setNegativeButton("CANCEL", null);
                    expDg.show();

                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                /*
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, 1888);
                */
                    String imageFolderPath = Environment.getExternalStorageDirectory().toString() + "/.khyrz";
                    File imageFolder = new File(imageFolderPath);
                    imageFolder.mkdirs();

                    imageName = new Date().toString() + ".png";
                    // get the uri for the captured image
                    fileUri = Uri.fromFile(new File(imageFolderPath, imageName));

                    // open the camera
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    openCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // handle return
                    startActivityForResult(openCamera, 1888);

                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(Expenses.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {

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
                    receiptImage = "good";

                } catch (Exception e) {
                    Log.i("TAG", "Error in rotating image: " + e);
                }

            } else {
                Toast.makeText(Expenses.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("ExpenseError", "" + e);
        }

    }

    public void getSpinnerExpenseType() {

        cursor = sqlDB.rawQuery("SELECT expenseCode, " +
                "expenseType " +
                "FROM expensetype", null);
        expense_type = (Spinner) findViewById(R.id.spinnerExpenseType);
        //  expense_type.setOnItemSelectedListener(this);
        List<SpinnerKeyValue> expense_type_spinner = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                expense_type_spinner.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
                //expense_type_spinner.add(cursor.getString(0));

            } while (cursor.moveToNext());

            ArrayAdapter<SpinnerKeyValue> dataAdapter = new ArrayAdapter<>(this,
                    R.layout.spinnner_bg, expense_type_spinner);
            dataAdapter.setDropDownViewResource(R.layout.spinnner_bg);
            expense_type.setAdapter(dataAdapter);

        }
    }

    private void Permission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Expenses.this,
                    new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA
                            }, 1);
        } else {
        }
    }

    public void getAllSpinnerContent() {
        //sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor2 = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});
        store_location = (Spinner) findViewById(R.id.spinnerStoreName);
        ArrayList<SpinnerKeyValue2> spinnerContent = new ArrayList<>();
        if (cursor2.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue2(cursor2.getString(0), cursor2.getString(1)));
            } while (cursor2.moveToNext());
        }

        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue2> adapter2 = new ArrayAdapter<>(this, R.layout.spinnner_bg, spinnerContent);
        store_location.setAdapter(adapter2);
        //spinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.
    }

    // get store location spinner
    private void spinnerLoad() {

        final List<String> meansOfTranspo = new ArrayList<>();
        meansOfTranspo.add("Tricycle");
        meansOfTranspo.add("Motorcycle");
        meansOfTranspo.add("Jeep");
        meansOfTranspo.add("Bus");
        meansOfTranspo.add("Taxi");
        meansOfTranspo.add("Ferry");
        meansOfTranspo.add("MRT");
        meansOfTranspo.add("LRT");
        meansOfTranspo.add("PNR");
        meansOfTranspo.add("UV Express");
        meansOfTranspo.add("Walking");
        ArrayAdapter<String> meansOfTranspoAdapter = new ArrayAdapter<>(
                Expenses.this, R.layout.spinnner_bg, meansOfTranspo);
        meansOfTransportation.setAdapter(meansOfTranspoAdapter);

        getAllSpinnerContentStoreLocation();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(DeliveryActivity.this, spinnerStoreLocation
                //        .getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();

                SpinnerKeyValue storeName = (SpinnerKeyValue) parent.getSelectedItem();
                spinnerSelectedStore = storeName.getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // get LocCode and storeName
    public void getAllSpinnerContentStoreLocation() {
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);
        cursor = sqlDB.rawQuery("SELECT DISTINCT storeLocCode, storeName " +
                "FROM schedule " +
                "WHERE userCode=? ", new String[]{userCode});

        // for location name spinner
        ArrayList<SpinnerKeyValue> spinnerContent = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                spinnerContent.add(new SpinnerKeyValue(cursor.getString(0), cursor.getString(1)));
                //Toast.makeText(this, cursor.getString(0) + " " + cursor.getString(1), Toast.LENGTH_SHORT).show();

            } while (cursor.moveToNext());
        }
        //fill data in spinner
        ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(
                Expenses.this, R.layout.spinnner_bg, spinnerContent);
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(Expenses.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}