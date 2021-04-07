package com.quaditsolutions.mmreport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;

public class AutomatAPKDownloadWebview extends AppCompatActivity {

    String url = "http://203.160.168.60/AutoMat/";
    EditText ed1;
    private WebView wv1;
    ProgressDialog p;
    String companyCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        companyCode = sp.getString("companyCode", null);
        checkCompany();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.automat_apk_download_view);

        ed1 = (EditText) findViewById(R.id.editText);
        wv1 = (WebView) findViewById(R.id.webView);

        p = new ProgressDialog(AutomatAPKDownloadWebview.this);
        p.setTitle("Download APK");
        p.setMessage("Downloading Please wait . . .");
        p.setCancelable(false);
        p.show();

        downloadAPK();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void checkCompany() {
        switch (companyCode) {
            case "CMPNY01":
                setTheme(R.style.RegcrisTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryReg));
                }
                break;
            case "CMPNY02":
                setTheme(R.style.PrestigeTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkPres));
                }
                break;
            case "CMPNY03":
                setTheme(R.style.TmarksTheme);
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkTM));
                }
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    private void downloadAPK() {

        // webview settings
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setDomStorageEnabled(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setDomStorageEnabled(true);
        wv1.getSettings().setLoadWithOverviewMode(true);
        wv1.getSettings().setUseWideViewPort(true);
        wv1.getSettings().setBuiltInZoomControls(true);
        wv1.getSettings().setDisplayZoomControls(true);
        wv1.getSettings().setSupportZoom(true);
        wv1.getSettings().setDefaultTextEncodingName("utf-8");
        wv1.loadUrl(url);

        p.dismiss();
        p.hide();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(AutomatAPKDownloadWebview.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}
