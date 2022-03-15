package myrehabcare.in.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import myrehabcare.in.Activity.DrProfileActivity;
import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.Classes.Transaction;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyDoctorsAdapter extends RecyclerView.Adapter<MyDoctorsAdapter.ViewHolder> {
    private Context mContext;

    private List<Doctors> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Jsb jsb;

    // data is passed into the constructor
    public MyDoctorsAdapter(Context context, List<Doctors> data, Jsb jsb) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
        this.jsb = jsb;
        setClickListener(mClickListener);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.each_my_doctors, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Doctors p = mData.get(position);

        holder.dr_name.setText(p.getName());
        holder.dr_type.setText(p.getType().split(":")[0]);
        Log.d("sdfdsfdsdfs",p.getType());
        Glide.with(mContext).load(p.getImage()).into(holder.dr_image);
        holder.dr_rating.setRating(p.getRating());

        if (p.getType().split(":")[1].equals("0")){
            holder.star_button.setLiked(false);
        }else {
            holder.star_button.setLiked(true);
        }

        holder.cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, DrProfileActivity.class).putExtra("doctors", p));
                    }
                }
        );

        holder.star_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                ProgressDialog progressDialog = jsb.getProgressDialog();
                progressDialog.show();

                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", jsb.getUser().getUser_id())
                        .add("doctor_id", p.getId())
                        .build();

                jsb.post(mContext.getString(R.string.baseUrl) + "api/add_to_fav", requestBody, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        jsb.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.star_button.setLiked(false);
                                jsb.toastLong(e.getMessage());
                                progressDialog.dismiss();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body = "";
                        if (response.isSuccessful()){
                            body = response.body().string();
                        }
                        String finalBody = body;
                        jsb.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()){
                                    try {
                                        JSONObject jsonObject = new JSONObject(finalBody);

                                        jsb.toastLong(jsonObject.getString("message"));
                                        progressDialog.dismiss();
                                        Log.d("sdfdsfdsdfs",jsonObject.getString("message"));
                                        p.setType(p.getType().split(":")[0]+":1");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                        holder.star_button.setLiked(false);
                                        jsb.toastLong(e.getMessage());
                                    }

                                }else {
                                    progressDialog.dismiss();
                                    holder.star_button.setLiked(false);
                                    jsb.toastLong(response.message());
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ProgressDialog progressDialog = jsb.getProgressDialog();
                progressDialog.show();

                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", jsb.getUser().getUser_id())
                        .add("doctor_id", p.getId())
                        .build();

                jsb.post(mContext.getString(R.string.baseUrl) + "api/remove_from_fav", requestBody, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        jsb.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.star_button.setLiked(false);
                                jsb.toastLong(e.getMessage());
                                progressDialog.dismiss();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body = "";
                        if (response.isSuccessful()){
                            body = response.body().string();
                        }
                        String finalBody = body;
                        jsb.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()){
                                    try {
                                        JSONObject jsonObject = new JSONObject(finalBody);

                                        jsb.toastLong(jsonObject.getString("message"));
                                        progressDialog.dismiss();
                                        Log.d("sdfdsfdsdfs",jsonObject.getString("message"));
                                        p.setType(p.getType().split(":")[0]+":0");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                        holder.star_button.setLiked(false);
                                        jsb.toastLong(e.getMessage());
                                    }

                                }else {
                                    progressDialog.dismiss();
                                    holder.star_button.setLiked(false);
                                    jsb.toastLong(response.message());
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dr_name,dr_type;
        ImageView dr_image;
        RatingBar dr_rating;
        LikeButton star_button;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dr_name = (TextView) itemView.findViewById(R.id.dr_name_tv);
            dr_type = (TextView) itemView.findViewById(R.id.dr_type_tv);
            dr_image = (ImageView) itemView.findViewById(R.id.dr_image_iv);
            dr_rating = (RatingBar) itemView.findViewById(R.id.dr_rating_rb);
            star_button = (LikeButton) itemView.findViewById(R.id.star_button);
            cardView = (CardView) itemView.findViewById(R.id.card_bg);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(),mData.get(getAdapterPosition()));
        }
    }

    // convenience method for getting data at click position
    Doctors getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position,Doctors doctors);
    }


    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public void updateList(List<Doctors> list){
        mData = list;
        notifyDataSetChanged();
    }

}
