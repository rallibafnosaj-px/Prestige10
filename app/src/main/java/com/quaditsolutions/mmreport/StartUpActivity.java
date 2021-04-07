package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Khyrz on 9/10/2017.
 */

public class StartUpActivity extends AppCompatActivity {

    SQLiteDatabase sqlDB;
    Cursor cursor;


    /**
     * CREATE TABLES
     **/

    // Create Table Request Overtime Status
    private static final String REQ_OT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "req_ot(reqotID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "storeName VARCHAR," +
            "dateIn VARCHAR," +
            "dateOut VARCHAR," +
            "timeIn VARCHAR," +
            "timeOut VARCHAR," +
            "reqOTStatus);";

    // Create Table Request Deviation Status
    private static final String REQ_DEVIATION_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "req_deviation(reqdevID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "storeName VARCHAR," +
            "date VARCHAR," +
            "timeIn VARCHAR," +
            "timeOut VARCHAR," +
            "reason VARCHAR," +
            "reqDeviationStatus);";

    // Create Table Request Deviation Status
    private static final String REQ_LEAVE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "req_leave(reqleaveID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "dateFrom VARCHAR," +
            "dateTo VARCHAR," +
            "days VARCHAR," +
            "reason VARCHAR," +
            "reqLeaveStatus);";

    // Create table request change schedule
    private static final String REQ_CHANGE_SCHEDULE = "CREATE TABLE IF NOT EXISTS " +
            "req_change_schedule(" +
            "reqCSID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "reqCSStatus," +
            "reqDateRequestedCS," +
            "reqCSReason," +
            "reqStoreLocationName," +
            "reqStoreLocCode," +
            "reqStartTime," +
            "reqEndTime," +
            "reqWorkingDays," +
            "effectivityDate);";

    //CREATE Table users
    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "users(userID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "userName VARCHAR, " +
            "passWord VARCHAR, " +
            "firstName VARCHAR, " +
            "middleName VARCHAR, " +
            "lastName VARCHAR, " +
            "customerCode VARCHAR, " +
            "companyCode VARCHAR);";

    //CREATE Table dtr
    private static final String DTR_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "dtr(dtrID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "selfieNetworkStatusIN, " +
            "selfieNetworkStatusOUT, " +
            "storeLocCode VARCHAR, " +
            "storeName VARCHAR, " +
            "timeIn TEXT, " +
            "timeOut TEXT, " +
            "dateIn TEXT, " +
            "dateOut TEXT, " +
            "locationLatIn VARCHAR, " +
            "locationLatOut VARCHAR, " +
            "locationLongIn VARCHAR, " +
            "locationLongOut VARCHAR, " +
            "addressIn VARCHAR, " +
            "addressOut VARCHAR, " +
            "imageIn TEXT, " +
            "imageOut TEXT, " +
            "syncStatus VARCHAR, " +
            "statusIn VARCHAR, " +
            "statusOut VARCHAR)";

    //CREATE Table dtr review
    private static final String DTR_REVIEW_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "reviewDtr(dtrID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "storeLocCode VARCHAR, " +
            "storeName VARCHAR, " +
            "timeIn VARCHAR, " +
            "timeOut VARCHAR, " +
            "dateIn VARCHAR, " +
            "dateOut VARCHAR, " +
            "locationLatIn VARCHAR, " +
            "locationLatOut VARCHAR, " +
            "locationLongIn VARCHAR, " +
            "locationLongOut VARCHAR, " +
            "addressIn VARCHAR, " +
            "addressOut VARCHAR)";

    //CREATE Table stores
    private static final String STORES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "stores(storeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "storeCode VARCHAR, " +
            "storeName VARCHAR);";

    //CREATE Table storelocation
    private static final String STORELOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "storelocation(storeLocID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "storeName VARCHAR, " +
            "areaName VARCHAR);";

    //CREATE Table items
    private static final String ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "items(itemID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "itemCode VARCHAR, " +
            "itemName VARCHAR);";

    //CREATE Table inventory
    private static final String INVENTORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "inventory(" +

            "inventoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "itemCode VARCHAR, " +
            "itemName VARCHAR, " +

            "itemOfftake VARCHAR, " +
            "itemTypeOfReturn VARCHAR, " +
            "deliveryValue VARCHAR, " +

            "storeCode VARCHAR, " +

            "beginInventory INTEGER, " +
            "endInventory INTEGER, " +

            "delivery INTEGER, " +

            "expiredItems INTEGER, " +
            "damagedItems INTEGER, " +
            "deliveryReturns INTEGER, " +
            "customerReturns INTEGER, " +

            "weekNo INTEGER," +

            "syncStatus VARCHAR," +

            // beginning
            "inventoryDate VARCHAR," +
            "sellingAreaPcs VARCHAR," +
            "warehousePcs VARCHAR," +
            "warehouseCases VARCHAR," +

            // deliver
            "deliveryPcs VARCHAR," +
            "deliveryCases VARCHAR," +
            "adjustmentPcs VARCHAR," +

            // returns
            "pulloutPcs VARCHAR," +
            "badOrderPcs VARCHAR," +
            "damagedItemPcs VARCHAR," +
            "expiredItemsPcs VARCHAR," +

            // ending
            "endSellingAreaPcs VARCHAR," +
            "endWarehousePcs VARCHAR," +
            "endWarehouseCases VARCHAR," +
            "offtake VARCHAR," +

            // max cap
            "daysOutOfStocks VARCHAR," +
            "homeShelf VARCHAR," +
            "secondaryDisplay);";

    //CREATE Table osa
    private static final String OSA_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "osa(osaID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "itemCode VARCHAR, " +
            "storeCode VARCHAR, " +
            "osa VARCHAR, " +
            "facing INTEGER, " +
            "itemName VARCHAR, " +
            "maxCapacity VARCHAR, " +
            "weekNo INTEGER, " +
            "syncStatus VARCHAR," +
            "homeShelfPcs VARCHAR," +
            "secondShelfPcs VARCHAR);";

    //CREATE Table pricechanged
    private static final String PRICECHANGED_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "pricechanged(priceID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "itemName VARCHAR, " +
            "itemPrice DOUBLE, " +
            "lastPrice DOUBLE, " +
            "weekNo INTEGER, " +
            "storeLocation VARCHAR, " +
            "dateAdded TEXT, " +
            "userCode VARCHAR);";

    //CREATE Table headerinventory
    private static final String HEADERINVENTORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "headerinventory(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "weekNo VARCHAR, " +
            "status VARCHAR, " +
            "invStatus VARCHAR, " +
            "sentDate TEXT, " +
            "userCode VARCHAR);";


    //CREATE Table headerosa
    private static final String HEADEROSA_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "headerosa(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "weekNo VARCHAR, " +
            "status VARCHAR, " +
            "sentDate TEXT, " +
            "userCode VARCHAR);";


    //CREATE Table headerprice
    private static final String HEADERPRICECHANGED_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "headerprice(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "weekNo VARCHAR, " +
            "status VARCHAR, " +
            "sentDate TEXT, " +
            "userCode VARCHAR);";

    //CREATE Table assignment / freshness /inventory
    private static final String ASSIGNMENT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "assignment(" +
            "assignmentID INTEGER PRIMARY KEY AUTOINCREMENT, " + // 0
            "assignedDate TEXT, " + // 1
            "userCode VARCHAR, " + // 2
            "itemCode VARCHAR, " + // 3
            "storeCode VARCHAR," + // 4
            "itemName VARCHAR, " + // 5
            "categoryName VARCHAR, " + // 6
            "tag VARCHAR, " + // 7
            "freshness VARCHAR, " + // 8
            "shelfLife INTEGER," + // 9

            // INVENTORY
            "caseQty VARCHAR, " + // base qty
            "beginningWHPcs VARCHAR, " +
            "beginningSA VARCHAR," + // 10
            "beginningWH VARCHAR," + // 11

            "deliveryInPcs VARCHAR," + // 12
            "deliveryAdjustment VARCHAR," + // 13

            "returnsPullOut VARCHAR," + // 14
            "returnsBO VARCHAR," + // 15
            "returnsDamaged VARCHAR," + // 16
            "returnsExpired VARCHAR," + // 17

            "endingSA VARCHAR," + // 18
            "endginWHPcs VARCHAR, " + // 19
            "endingWH VARCHAR," + // 20 cases
            "offtake VARCHAR, " + // 21
            "outOfStocks VARCHAR," +

            "customerCode VARCHAR," +
            "customerName VARCHAR);";

    //CREATE Table schedule
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "schedule(" +
            "scheduleID VARCHAR, " +
            "userCode VARCHAR, " +
            "workingDays VARCHAR, " +
            "startTime TEXT, " +
            "endTime TEXT, " +
            "storeName VARCHAR, " +
            "storeLocCode VARCHAR," +

            "reqCSStatus VARCHAR, " +
            "reqDateRequestedCS VARCHAR, " +
            "reqCSReason VARCHAR," +
            "reqStoreLocationName VARCHAR," +
            "reqStoreLocCode VARCHAR," +
            "reqStartTime VARCHAR," +
            "reqEndTime VARCHAR," +
            "reqWorkingDays VARCHAR," +
            "effectivityDate VARCHAR);";

    //CREATE Table announcement
    private static final String ANNOUNCEMENT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "announcement(announceID INTEGER, " +
            "announceTitle VARCHAR , " +
            "announceMessage VARCHAR, " +
            "announcePostedBy VARCHAR, " +
            "announceDateCreated VARCHAR, " +
            "seenStatus VARCHAR);";

    private static final String ANNOUNCEMENT_UNIQUE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS announcement_idx " +
            "ON announcement(announceDateCreated)";

    //CREATE Table delivery

    private static final String DELIVERY_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "delivery(deliveryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "itemCode VARCHAR, " +
            "itemName VARCHAR, " +
            "quantity VARCHAR, " +
            "unitOfMeasure VARCHAR, " +
            "listStatus VARCHAR, " +
            "deliveryDate TEXT, " +
            "productionDate TEXT, " +
            "expirationDate TEXT, " +
            "storeLocCode VARCHAR, " +
            "storeName VARCHAR, " +
            "boxCase VARCHAR, " +
            "lotNumber VARCHAR, " +
            "tag VARCHAR, " +
            "postStatus VARCHAR, " +
            "listtag INTEGER, " +
            "popupStatus VARCHAR, " +
            "syncStatus VARCHAR, " +
            "dateRecorded VARCHAR, " +
            "shelfLife VARCHAR);";

    //CREATE Table expenses
    private static final String EXPENSES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "expenses(expenseID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "employeeCode VARCHAR, " +
            "firstName VARCHAR, " +
            "middleName VARCHAR, " +
            "lastName VARCHAR, " +
            "date TEXT, " +
            "expenseCode VARCHAR, " +
            "expenseType VARCHAR, " +
            "meansOfTransportations VARCHAR, " +
            "client VARCHAR, " +
            "storeLocation VARCHAR, " +
            "time TEXT, " +
            "amount DOUBLE, " +
            "receipt VARCHAR, " +
            "reimbursable DOUBLE, " +
            "notes VARCHAR, " +
            "status VARCHAR, " +
            "syncStatus VARCHAR, " +
            "attachments TEXT," +
            "storeLocCode VARCHAR);";

    //CREATE Table expensetype
    private static final String EXPENSE_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "expensetype(expenseCode VARCHAR, " +
            "expenseType VARCHAR);";

    //CREATE Table nearlyToExpired
    private static final String NEARLY_TO_EXPIRED_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "nearlytoexpired(nteID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userCode VARCHAR, " +
            "itemCode VARCHAR, " +
            "storeLocCode VARCHAR, " +
            "remarks VARCHAR, " +
            "rtvNo VARCHAR, " +
            "status VARCHAR, " +
            "quantity INTEGER, " +
            "lotNo VARCHAR, " +
            "expirationDate VARCHAR, " +
            "dateRecorded VARCHAR, " +
            "syncStatus VARCHAR, " +
            "tag VARCHAR, " +
            "postStatus VARCHAR, " +
            "listtag INTEGER, " +
            "popupStatus VARCHAR, " +
            "nearlyExpiredRemarks VARCHAR);";

    //CREATE Table customeraccess
    private static final String CUSTOMER_ACCESS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "customeraccess(customeraccessID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "customerCode VARCHAR, " +
            "appsModuleID INTEGER, " +
            "isVisible INTEGER);";

    private static final String EMPLOYEE_PAYROLL_HEADER = "CREATE TABLE IF NOT EXISTS employeepayrollheader" +
            "(eprhID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "eprheaderID VARCHAR, " +
            "payrollPeriodID VARCHAR, " +
            "batchID VARCHAR, " +
            "batchNo VARCHAR, " +
            "userCode VARCHAR, " +
            "totalBasicPay VARCHAR, " +
            "totalOT VARCHAR, " +
            "totalLate VARCHAR, " +
            "totalWorkingHours VARCHAR, " +
            "sssEmployer VARCHAR, " +
            "philhealthEmployer VARCHAR, " +
            "pagibigEmployer VARCHAR, " +
            "grossPay VARCHAR, " +
            "totalDeduction VARCHAR, " +
            "netPay VARCHAR, " +
            "batchTrigger VARCHAR, " +
            "sssEmployee VARCHAR, " +
            "philhealthEmployee VARCHAR, " +
            "pagibigEmployee VARCHAR, " +
            "taxPay VARCHAR, " +
            "status VARCHAR, " +
            "ssecer VARCHAR, " +
            "startDate TEXT, " +
            "endDate TEXT);";

    private static final String EMPLOYEE_PAYROLL_LINE = "CREATE TABLE IF NOT EXISTS employeepayrollline" +
            "(eprlID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "eprlineID VARCHAR, " +
            "payrollPeriodID VARCHAR, " +
            "batchID VARCHAR, " +
            "batchNo VARCHAR, " +
            "payrollItemID VARCHAR, " +
            "payrollItemName VARCHAR, " +
            "payrollItemType VARHCAR, " +
            "amount INTEGER, " +
            "userCode VARCHAR);";

    private static final String APP_VERSION = "CREATE TABLE IF NOT EXISTS appversion" +
            "(appv DOUBLE PRIMARY KEY);";

    private static final String LOGS_TBL = "CREATE TABLE IF NOT EXISTS logsTbl" +
            "(logsID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "empNo VARCHAR, " +
            "dateTime VARCHAR, " +
            "log VARCHAR);";

    /*
    //INSERT INTO stores
    private static final String INSERT_STORES = "INSERT INTO stores" +
            "(storeName, storeLocation, userCode)" +
            "VALUES" +
            "('SM', 'San Lazaro', 1), " +
            "('SM', 'Mega Mall', 1), " +
            "('SM', 'Valenzuela', 1), " +
            "('SM', 'Marilao', 1);";

    //INSERT INTO items
    private static final String INSERT_ITEMS = "INSERT INTO items" +
            "(itemName, storeCode)" +
            "VALUES" +
            "('Item San Lazaro', 1), " +
            "('Item Mega Mall', 2), " +
            "('Item Valenzuela', 3), " +
            "('Item Marilao', 4);";

    //INSERT INTO inventory
    private static final String INSERT_INVENTORY = "INSERT INTO inventory" +
            "(itemCode, " +
            "itemName, " +
            "storeCode, " +
            "osa, " +
            "facing, " +
            "beginInventory, " +
            "endInventory, " +
            "delivery, " +
            "returns, " +
            "offTake)" +
            "VALUES" +
            "(1, 1, 'On Shelf', 10, 20, 10, 5, 2, 17);";
    */

    private static final String SELECT_USERNAME = "SELECT userName FROM users";
    String title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Open Or Create Database
        sqlDB = openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

        sqlDB.execSQL(USERS_TABLE);
        sqlDB.execSQL(STORES_TABLE);
        sqlDB.execSQL(INVENTORY_TABLE);
        sqlDB.execSQL(ITEMS_TABLE);
        sqlDB.execSQL(PRICECHANGED_TABLE);
        sqlDB.execSQL(DTR_TABLE);
        sqlDB.execSQL(HEADERINVENTORY_TABLE);
        sqlDB.execSQL(HEADEROSA_TABLE);
        sqlDB.execSQL(HEADERPRICECHANGED_TABLE);
        sqlDB.execSQL(ASSIGNMENT_TABLE);
        sqlDB.execSQL(STORELOCATION_TABLE);
        sqlDB.execSQL(SCHEDULE_TABLE);
        sqlDB.execSQL(ANNOUNCEMENT_TABLE);
        sqlDB.execSQL(OSA_TABLE);
        sqlDB.execSQL(DELIVERY_TABLE);
        sqlDB.execSQL(EXPENSES_TABLE);
        sqlDB.execSQL(EXPENSE_TYPE_TABLE);
        sqlDB.execSQL(NEARLY_TO_EXPIRED_TABLE);
        sqlDB.execSQL(CUSTOMER_ACCESS_TABLE);
        sqlDB.execSQL(EMPLOYEE_PAYROLL_HEADER);
        sqlDB.execSQL(EMPLOYEE_PAYROLL_LINE);
        sqlDB.execSQL(APP_VERSION);
        sqlDB.execSQL(DTR_REVIEW_TABLE);
        sqlDB.execSQL(REQ_OT_TABLE);
        sqlDB.execSQL(REQ_DEVIATION_TABLE);
        sqlDB.execSQL(REQ_LEAVE_TABLE);
        sqlDB.execSQL(REQ_CHANGE_SCHEDULE);
        sqlDB.execSQL(ANNOUNCEMENT_UNIQUE_INDEX);
        sqlDB.execSQL(LOGS_TBL);

        cursor = sqlDB.rawQuery(SELECT_USERNAME, null);
        if (cursor.getCount() == 0) {
            Reconnect();
        } else {
            SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
            String username = sp.getString("userName", "");

            //check username
            if (username == null || username.equals("")) {
                sqlDB.close();
                Intent in = new Intent(StartUpActivity.this, LogInActivity.class);
                //Intent in = new Intent(StartUpActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            } else {
                sqlDB.close();
                Intent in = new Intent(StartUpActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        }
    }

    private void Reconnect() {
        // if have internet go to login
        if (haveNetworkConnection(this)) {
            Intent in = new Intent(StartUpActivity.this, LogInActivity.class);
            startActivity(in);
            finish();
        } else {
            title = "Connection timeout";
            message = "Please check your internet connection.";
            //startActivity(new Intent(StartUpActivity.this, MainActivity.class));
            alertDialog(title, message);
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
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                haveConnectedMobile = true;
                //              Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    private void alertDialog(String title, String message) {
        DialogInterface.OnClickListener recon = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Reconnect();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent in = new Intent(StartUpActivity.this, LogInActivity.class);
                        startActivity(in);
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder mExit = new AlertDialog.Builder(this);
        mExit.setTitle(title);
        mExit.setMessage(message);
        mExit.setCancelable(false);
        mExit.setPositiveButton("Reconnect", recon);
        mExit.setNegativeButton("Offline", recon);
        mExit.show();
    }
}