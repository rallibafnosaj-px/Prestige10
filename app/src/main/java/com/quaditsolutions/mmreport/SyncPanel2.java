package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

/**
 * Created by Khyrz on 11/10/2017.
 */

public class SyncPanel2 extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    String companyCode, userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences("mainInfo", Context.MODE_PRIVATE);
        userCode = sp.getString("userCode", null);
        companyCode = sp.getString("companyCode", null);

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
        setContentView(R.layout.syncpanel2);


        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
   //     TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
   //     tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);


        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("DTR"));
        tabLayout.addTab(tabLayout.newTab().setText("Expense"));
        tabLayout.addTab(tabLayout.newTab().setText("Freshness"));
        tabLayout.addTab(tabLayout.newTab().setText("OSA"));
        tabLayout.addTab(tabLayout.newTab().setText("Inventory"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
/**
        //To hide the first tab
        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        //Set the next  tab as selected tab
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
*/
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        SyncPanelPager adapter = new SyncPanelPager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Sets the Fragment or Swiping Action align with tab indicator
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

        //Sets the Fragment or Swiping Action align with tab indicator
        //viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() ==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}