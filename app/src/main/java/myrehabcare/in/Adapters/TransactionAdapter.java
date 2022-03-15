package myrehabcare.in.Adapters;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myrehabcare.in.Classes.Transaction;
import myrehabcare.in.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context mContext;
    private Date date  = null;

    private List<Transaction> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public TransactionAdapter(Context context, List<Transaction> data) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.each_transaction, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction p = mData.get(position);

        if (p != null) {
            holder.tt1.setText(p.getToName());
            holder.tt2.setText("₹" + p.getAmount());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

            Date uperDate = null;

            if (position > 0){
                Transaction p2 = mData.get(position-1);
                uperDate = p2.getDate();
            }else {
                uperDate = null;
            }

            if (DateUtils.isToday(p.getDate().getTime())) {
                holder.date_tv.setText("Today");
            } else {
                holder.date_tv.setText(simpleDateFormat.format(p.getDate()));
            }


            if (uperDate == null) {
                holder.date_tv.setVisibility(View.VISIBLE);

            } else {
                if (isSameDay(uperDate, p.getDate())) {
                    holder.date_tv.setVisibility(View.GONE);
                }else {
                    holder.date_tv.setVisibility(View.VISIBLE);
                }


            }
        }



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tt1,tt2,date_tv;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tt1 = (TextView) itemView.findViewById(R.id.dr_name);
            tt2 = (TextView) itemView.findViewById(R.id.amount);
            date_tv = (TextView) itemView.findViewById(R.id.text1);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Transaction getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public void updateList(List<Transaction> list){
        mData = list;
        notifyDataSetChanged();
    }

}
