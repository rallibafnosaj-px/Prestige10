package com.quaditsolutions.mmreport;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IconAdapter extends BaseAdapter {
    private Context context;
    private final ArrayList<String> mobileValues;

    public IconAdapter(Context context, ArrayList<String> mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.home_icons, null);
            //gridView.setBackgroundColor(Color.parseColor("#222c65"));

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.txtView);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.imgView);

            String mobile = mobileValues.get(position);

            if (mobile.equals("1"))
            {
                textView.setText("Time In / Out");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconcheckinout);
                //imageView.setBackgroundResource(R.drawable.circle_checkin);
            }
            else if (mobile.equals("2"))
            {
                textView.setText("Inventory");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconinventory);
                //imageView.setBackgroundResource(R.drawable.circle_inventory);
            }
            else if (mobile.equals("3"))
            {
                textView.setText("OSA");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconosa);
                //imageView.setBackgroundResource(R.drawable.circle_osa);
            }
            else if(mobile.equals("4"))
            {
                textView.setText("Freshness");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconfreshness);
                //imageView.setBackgroundResource(R.drawable.circle_freshness);
            }
            else if(mobile.equals("5"))
            {
                textView.setText("Expenses");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconexpenses);
                //imageView.setBackgroundResource(R.drawable.circle_expenses);
            }
            else if(mobile.equals("6"))
            {
                textView.setText("Announcement");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconannouncement);
                //imageView.setBackgroundResource(R.drawable.circle_nearlyexpire);
            }
            else if(mobile.equals("7"))
            {
                textView.setText("Schedule");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconmyschedule);
                //imageView.setBackgroundResource(R.drawable.circle_nearlyexpire);
            }
            else if(mobile.equals("8"))
            {
                textView.setText("Pending Sync");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconpendingsync);
                //imageView.setBackgroundResource(R.drawable.circle_nearlyexpire);
            }
            else if(mobile.equals("9"))
            {
                textView.setText("Review DTR");
                textView.setTextSize(16);
                imageView.setImageResource(R.drawable.iconreviewdtr);
                //imageView.setBackgroundResource(R.drawable.circle_nearlyexpire);
            }

        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return mobileValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
