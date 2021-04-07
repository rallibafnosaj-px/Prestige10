package com.quaditsolutions.mmreport;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class GlobalVar{

    // prestige public ip

    // SharedPreferences sp = getSharedPreferences("mainInfo", MODE_PRIVATE);
    // public String URL = sp.getString("ipAddress", null);

    String publicIP = "http://51.79.223.162/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
    String localIP = "payroll/PrestigeV3/WebService/Mobile/MobileWebService.asmx";

    // public String URL = "http://203.160.168.60/PrestigeV3/WebService/Mobile/MobileWebService.asmx";

    //public static String URL = "http://203.160.168.60/PrestigeV3/WebService/Mobile/MobileWebService.asmx";
    // jm local
    //public static String URL = "http://192.168.10.30/PrestigeV3/WebService/Mobile/MobileWebService.asmx";

    //App Version
    public static String appVersion = "2";

    // this is to get and set value of number of announcement fetch from database
    private String numberOfAnnouncement;

    public String getNumberOfAnnouncement() {
        return this.numberOfAnnouncement;
    }

    public void setNumberOfAnnouncement(String numberOfAnnouncement) {
        this.numberOfAnnouncement = numberOfAnnouncement;
    }

    public List<GlobalVar> announcementList;
    //public static String URL = "";

    //UserData
    public String firstName, middleName, lastName, password, email, userCode, storeLocation, customerCode, companyCode;

    //StoreData
    public String StsoreCode;

    //ItemData
    public String ItemCode, ItemName, categoryName, rtvExpDate;

    //Price Var
    public String storeCodePrice, storeNamePrice, storeLocationPrice, weekNoPrice;

    //Announcement
    public String announceID, announceTitle, announceMessage, announcePostedBy, announceDateCreated;

    //User Schedule
    public String workingDays, startTime, endTime, storeName, scheduleID, storeLocCode;

    //User Assignment
    public String aItemCode, aItemName, aCategoryName, aFreshness, aShelfLife;

    //Price Change
    public String pcItemID, pcItemName;

    public Double pcLastPrice2;

    //User Delivery
    public String dItemCode, dQuantity, dDeliveryDate, dExpirationDate, dBoxCase, dLotNumber, dTag;

    //Expenses
    public static String storeLoc;
    public String expenseCode, expenseType;

    //CustomerAccess
    public int appsModuleID, isVisible;

    //Sync DTR
    public String dtrDateIn, dtrDateOut, dtrTimeIn, dtrTimeOut, cType, dtrStoreName, dtrStore,
            dtrCurrentLocationIn, dtrCurrentLocationOut, dtrID;

    //Sync ReviewDTR
    public String rdtrEmployeeNo, rdtrDateIn, rdtrDateOut, rdtrTimeIn, rdtrTimeOut, rcType, rdtrStoreName, rdtrStoreCode, rdtrCurrentLocationIn,
            rdtrCurrentLocationOut, rdtrLongIn, rdtrLongOut, rdtrLatIn, rdtrLatOut;

    //Sync Expense
    public String expDate, expCode, expType, expMeansOfTransportation, expAmount, expNote;

    //Sync Freshness / Delivery
    public String freshness_itemCode, freshness_quantity, freshness_deliveryDate, product_name, freshness_production_date,
            freshness_expiration_date, freshness_store_name, freshness_unit_of_measure, freshness_lot_no;

    //Sync OSA
    public String osaItemCode, osaOsa, osaFacing, osaWeekNo, osaMaxCapacity, osaItemName, osaHomeShelf, osaSecShelf;

    //Sync inventory
    public String iInventoryID, tv_inventory_weekno, tv_inventory_item_name, tvBegSA, tvWHPcs, tvWHCases,
            tvDeliveryPcs, tvDeliveryCases, tvAdjustmentPcs, tvPullOut, tvBadOrder, tvDamagedItemPcs,
            tvExpiredItemsPcs, tvEndSellingAreaPcs, tvEndWHPcs, tvEndWHCases, tvDaysOS, tvOfftake,
            tvHomeShelf, tvSecondaryDisplay, tvInvDate;

    //Payslip Header
    public String eprheaderID, payrollPeriodID, batchID, batchNo, totalBasicPay, totalOT, totalWorkingHours,
            totalLate, sssEmployer, philhealthEmployer, pagibigEmployer, grosspay, totalDeduction,
            netpay, sssEmployee, philhealthEmployee, pagibigEmployee, taxpay, ssecer, startDate, endDate;

    //Payslip Line
    public String eprLineID, payrollPeriodIDL, batchIDL, batchNoL, payrollItemID, payrollItemName, payrollItemType, amount;

    public String startDatePS, endDatePS;

    // Request OT status
    public String reqotStoreName, reqotDateIn, reqotDateOut, reqotTimeIn, reqotTimeOut, reqotStatus;

    // Request Deviation Status
    public String reqdevStoreName, reqdevDate, reqdevReason, reqdevTimeIn, reqdevTimeOut, reqdevStatus;

    // Request Leave Status
    public String reqleaveDateFrom, reqleaveDateTo, reqleaveReason, reqleaveDays, reqleaveStatus;

    // Request Change Schedule
    public String reqCSStatus, reqDateRequestedCS, reqCSReason, reqStoreLocationName, reqStoreLocCode,
            reqStartTime, reqEndTime, reqWorkingDays, reqEffectivityDate;

    // Schedule Change
    public String schedWorkingDay, schedStartTime, schedEndTime, schedStoreName, schedStoreLocCode,
            schedscheduleID;

    public String selectedSchedCheckedID;

    // Items list with inventory
    public String invItemName, invItemCode, invCategoryName, invBegSA, invBegWH, invDelAddPcs, invBegWHPcs,
            invDelAdj, invRetPullout, invRetBO, invRetDamaged, invRetExp, invEndSA, invEndWH, invOfftake,
            invCaseQty, invStoreCode, invStoreName, invWeekNo, invUserCode, invInventoryDate, invOutOfStocks,
            itemCustomerCode, itemCustomerName;

}