

package com.quaditsolutions.mmreport;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyrz on 9/11/2017.
 */

public class RecyclerViewAnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    public TextView content;
    public View layout;
    public List<GlobalVar> getDataAdapter;
    SQLiteDatabase sqlDB;

    public RecyclerViewAnnouncementAdapter(List<GlobalVar> getDataAdapter, Context context){

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.announcement_layout, parent,false);
        RecyclerViewAnnouncementAdapter.MyHolder holder=new RecyclerViewAnnouncementAdapter.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        final GlobalVar item = getDataAdapter.get(position);
        RecyclerViewAnnouncementAdapter.MyHolder myHolder= (RecyclerViewAnnouncementAdapter.MyHolder) holder;
        GlobalVar current = getDataAdapter.get(position);

        String title = current.announceTitle;
        String noTitle= "No Title";

        Log.i("TAG", "Title: "+title);

        if (title.equalsIgnoreCase("")){
            myHolder.tvAnnouncementTitle.setText("No Title");
            myHolder.tvAnnouncementDate.setText(current.announceDateCreated);
            myHolder.tvAnnouncementDate.setVisibility(View.VISIBLE);

            myHolder.tvAnnouncementBy.setVisibility(View.GONE);
            myHolder.tvAnnouncementDesc.setVisibility(View.GONE);
            myHolder.vLine1.setVisibility(View.GONE);
            myHolder.vLine2.setVisibility(View.GONE);
        }else{
            myHolder.tvAnnouncementTitle.setText(current.announceTitle);
            myHolder.tvAnnouncementDate.setText(current.announceDateCreated);
            myHolder.tvAnnouncementDate.setVisibility(View.VISIBLE);

            myHolder.tvAnnouncementBy.setVisibility(View.GONE);
            myHolder.tvAnnouncementDesc.setVisibility(View.GONE);
            myHolder.vLine1.setVisibility(View.GONE);
            myHolder.vLine2.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return getDataAdapter.size();
    }

    public void removeItem(int position){
        getDataAdapter.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(GlobalVar item, int position){
        getDataAdapter.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvAnnouncementTitle, tvAnnouncementDate, tvAnnouncementDesc, tvAnnouncementBy;
        public View vLine1, vLine2;
        public CardView cardView;

        public MyHolder(View itemView) {

            super(itemView);

            tvAnnouncementTitle = (TextView) itemView.findViewById(R.id.tvAnnouncementTitle);
            tvAnnouncementDate =  (TextView) itemView.findViewById(R.id.tvAnnouncementDate);
            tvAnnouncementDesc = (TextView) itemView.findViewById(R.id.tvAnnouncementDesc);
            tvAnnouncementBy = (TextView) itemView.findViewById(R.id.tvAnnouncementBy);
            vLine1 = itemView.findViewById(R.id.vLine1);
            vLine2 = itemView.findViewById(R.id.vLine2);

            cardView = (CardView) itemView.findViewById(R.id.cardAnnouncement);
            cardView.setOnClickListener(this);

        }

        public void onClick(View view) {

            int index = getAdapterPosition();
            GlobalVar current = getDataAdapter.get(index);
            String annID = current.announceID;
            String annTitle = current.announceTitle;

            Intent i = new Intent(itemView.getContext(), announcementDetails.class);
            i.putExtra("annID", annID);
            i.putExtra("annTitle", annTitle);
            itemView.getContext().startActivity(i);

            sqlDB = context.openOrCreateDatabase("mobileprestige", Context.MODE_PRIVATE, null);

                sqlDB.execSQL("UPDATE announcement " +
                        "SET seenStatus = 'SEEN' "   +
                        "WHERE announceID = '"+annID+"' ");

            sqlDB.close();

            /*GlobalVar current = getDataAdapter.get(index);
            String itemCode = current.ItemCode;
            String itemName = current.ItemName;

            Intent in = new Intent(itemView.getContext(), OSAActivity.class);
            in.putExtra("itemCodeOSA", itemCode);
            in.putExtra("itemNameOSA", itemName);

            Toast.makeText(context, itemCode, Toast.LENGTH_SHORT).show();
            itemView.getContext().startActivity(in);*/
        }
    }
    public void setFilter(List<GlobalVar> gv)
    {
        /*
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(gv);
        notifyDataSetChanged();
        */
    }
}