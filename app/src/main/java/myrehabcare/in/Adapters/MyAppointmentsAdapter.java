package myrehabcare.in.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import myrehabcare.in.Classes.Appointments;
import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.R;

public class MyAppointmentsAdapter extends RecyclerView.Adapter<MyAppointmentsAdapter.ViewHolder> {
    private Context mContext;

    private List<Appointments> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyAppointmentsAdapter(Context context, List<Appointments> data,ItemClickListener clickListener) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
        this.mClickListener = clickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.each_my_appointments, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Appointments p = mData.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy, hh:mm a");
        if(p.getDoctors().getPatient_tryAgain().equals("0")){
            holder.dr_name.setText("No MRC");
            holder.dr_type.setText("N/A");
            holder.status.setText(p.getStatus());
            holder.time.setText(dateFormat.format(p.getAppointmentsDate()));
            holder.address.setText(p.getDoctors().getPatient_address());
            Glide.with(mContext).load(R.drawable.ic_logo).into(holder.dr_image);
            holder.tv_tryAgain.setVisibility(View.VISIBLE);
        }else {
            holder.dr_name.setText(p.getDoctors().getName());
            holder.dr_type.setText(p.getDoctors().getType());
            holder.status.setText(p.getStatus());
            holder.time.setText(dateFormat.format(p.getAppointmentsDate()));
            holder.address.setText(p.getDoctors().getAddress());

            Glide.with(mContext).load(p.getDoctors().getImage()).into(holder.dr_image);
            holder.tv_tryAgain.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dr_name,dr_type,status,time,address,tv_tryAgain;
        ImageView dr_image;
        RatingBar dr_rating;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dr_name = (TextView) itemView.findViewById(R.id.dr_name_tv);
            dr_type = (TextView) itemView.findViewById(R.id.dr_type_tv);
            status = (TextView) itemView.findViewById(R.id.status_tv);
            time = (TextView) itemView.findViewById(R.id.time_tv);
            address = (TextView) itemView.findViewById(R.id.address_tv);
            dr_image = (ImageView) itemView.findViewById(R.id.dr_image_iv);
            dr_rating = (RatingBar) itemView.findViewById(R.id.dr_rating_rb);
            tv_tryAgain = (TextView) itemView.findViewById(R.id.tv_tryAgain);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(),mData.get(getAdapterPosition()).getDoctors(),mData.get(getAdapterPosition()).getDoctors().getPatient_tryAgain());
        }
    }

    // convenience method for getting data at click position
    Appointments getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position,Doctors mData,String tryAgain_Status);
    }


    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public void updateList(List<Appointments> list){
        mData = list;
        notifyDataSetChanged();
    }

}
