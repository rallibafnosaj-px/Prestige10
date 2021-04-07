package com.quaditsolutions.mmreport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by DELL on 11/10/2017.
 */

public class RequestPanelPager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public RequestPanelPager(FragmentManager fm, int tabCount) {
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
                RequestOvertimeTab tab1 = new RequestOvertimeTab();
                return tab1;
            case 1:
                RequestDeviationTab tab2 = new RequestDeviationTab();
                return tab2;
            case 2:
                RequestLeaveTab tab3 = new RequestLeaveTab();
                return tab3;
            case 3:
                RequestChangeSchedule tab4 = new RequestChangeSchedule();
                return tab4;

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