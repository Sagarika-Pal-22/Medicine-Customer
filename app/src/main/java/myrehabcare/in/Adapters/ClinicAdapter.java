package myrehabcare.in.Adapters;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import myrehabcare.in.Classes.Transaction;
import myrehabcare.in.R;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {
    private Context mContext;

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public ClinicAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.each_clinic_results, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject p = mData.get(position);

        if (p != null) {
            try {
                holder.namee.setText(p.getString("clinic_name"));
                holder.address.setText(p.getString("clinic_address"));
                Glide.with(mContext).load(p.getString("clinic_image")).into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
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
        TextView address,namee;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            address = (TextView) itemView.findViewById(R.id.addresss);
            namee = (TextView) itemView.findViewById(R.id.nameeee);
            imageView = (ImageView) itemView.findViewById(R.id.imageee);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public JSONObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public  void setClickListener(ItemClickListener itemClickListener) {
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

    public void updateList(List<JSONObject> list){
        mData = list;
        notifyDataSetChanged();
    }

}
