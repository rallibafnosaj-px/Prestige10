package com.quaditsolutions.mmreport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Rozz on 13/07/2018.
 */

public class PayslipWebView extends AppCompatActivity{

    // http://192.168.1.21/Prestige/Transaction/Reports/PaySlip.aspx?a1=23&a2=113&a3=CSTMR042&a4=23&a5=June%2016,%202018&a6=June%2030,%202018&a7=AVELINO%20JR,%20JR,%20ARNALDO%20M.ANGGING&a8=check&a9=C15015&a10=POSTED
    String userCode, storeLocation, companyCode, storeLocCode;
    //String url = "http://192.168.3.184/QPrincipalPortal/PaySlip.aspx?employeeNo=";
    String url = "http://203.160.168.60/QPrincipalPortal/PaySlip.aspx?employeeNo=";
    EditText ed1;
    private WebView wv1;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);
        storeLocation = sp.getString("storeLocation", null);

        switch (companyCode)
        {
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
        setContentView(R.layout.payslip_webview);

        ed1=(EditText)findViewById(R.id.editText);

        wv1=(WebView)findViewById(R.id.webView);
        //wv1.setWebViewClient(new MyBrowser());

        //loadWebPage(); overriding
        //p = new ProgressDialog(PayslipWebView.this);
        //p.setMessage("Please wait...");
        //p.setCancelable(false);
        //p.show();

            if(haveNetworkConnection(this)){
                loadPayslip();
                finish();
            }else{
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("Please connect to internet.");
                b.setTitle("Internet Required");
                b.setPositiveButton("OK",null);
                b.show();
            }

        //p.dismiss();
        //p.hide();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                //   Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
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

    private void loadPayslip(){

        p = new ProgressDialog(PayslipWebView.this);
        p.setMessage("Please wait...");
        p.setCancelable(false);
        p.show();

        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.loadUrl(url+userCode);

        p.dismiss();
        p.hide();

    }

    private void loadWebPage(){

        // get url
        String url = "http://192.168.1.3/QPrincipalPortal/PaySlip.aspx?employeeNo=C14804";

        ed1.setText(url);
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl(url);
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}