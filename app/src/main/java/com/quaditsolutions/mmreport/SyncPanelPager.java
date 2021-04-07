package com.quaditsolutions.mmreport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by DELL on 11/10/2017.
 */

public class SyncPanelPager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public SyncPanelPager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                SyncForDailyTimeRecord tab1 = new SyncForDailyTimeRecord();
                return tab1;
            case 1:
                SyncForExpense tab2 = new SyncForExpense();
                return tab2;
            case 2:
                SyncForFreshness tab3 = new SyncForFreshness();
                return tab3;
            case 3:
                SyncForOSA tab4 = new SyncForOSA();
                return tab4;
            case 4:
                SyncForInventory tab5 = new SyncForInventory();
                return tab5;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}