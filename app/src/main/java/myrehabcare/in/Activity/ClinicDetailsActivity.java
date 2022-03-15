package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityClinicDetailsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClinicDetailsActivity extends AppCompatActivity {

    private ActivityClinicDetailsBinding binding;
    private Activity activity;
    private Jsb jsb;
    String phone="",bookId="";
    JSONObject hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClinicDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

        try {
            hashMap = new JSONObject(getIntent().getStringExtra("hashMap"));
            bookId = getIntent().getStringExtra("book_id");

            Log.d("dsdsds",hashMap.toString());
            binding.clinicName.setText(hashMap.getString("clinic_name"));
            Glide.with(activity).load(hashMap.getString("clinic_image")).into(binding.clinicImage);
            binding.clinicAddress.setText(hashMap.getString("clinic_address"));
            binding.clinicDoctors.setText( hashMap.getString("full_name"));
            binding.clinicVisitingHours.setText( hashMap.getString("open_time") +"-"+ hashMap.getString("close_time"));
             phone = hashMap.getString("phone");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.clinicCall.setText(phone);
       binding.clinicCall.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               sendContactToDialer(phone);
           }
       });
        binding.clinicBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             bookAppointment("",bookId);
            }
        });
    }
    private void sendContactToDialer(String number){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }
    private void bookAppointment(String userId,String bookId){
        final ProgressDialog progressDialog = jsb.getProgressDialog();
        progressDialog.show();
        Log.d("dsdsds", jsb.getUser().getUser_id());

        RequestBody body = new FormBody.Builder()
                .add("user_id", jsb.getUser().getUser_id())
                .add("book_id",bookId)
                .add("status", "1")
                .build();

        jsb.post("http://mrcadmin.in/api/confirm_reject_appointment", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                jsb.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        jsb.toastLong("Something went wrong");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String bb = response.body().string();
                    jsb.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(bb);
                                Log.d("dddddd",jsonObject.toString());
                                if (jsonObject.getString("auth-status").equals("true")){
                                    progressDialog.dismiss();
                                    jsb.toastLong(jsonObject.getString("message"));
                                    Doctors doctors = new Doctors();
                                    doctors.setAbout_us(hashMap.getString("about"));
                                    doctors.setAddress(hashMap.getString("email"));
                                    doctors.setContact(hashMap.getString("email"));
                                    doctors.setDate(hashMap.getString("created_at"));
                                    doctors.setId(hashMap.getString("user_id"));
                                    doctors.setImage(hashMap.getString("clinic_image"));
                                    doctors.setName(hashMap.getString("full_name"));
                                   /* float a = 0f;
                                    if (!hashMap.getString("doctor_rating").isEmpty()){
                                        a = Float.parseFloat(hashMap.getString("doctor_rating"));
                                    }
                                    doctors.setRating(a);
                                    doctors.setStatus("");
                                    doctors.setType(hashMap.getString("role_name"));*/
                                    startActivity(new Intent(activity, AppointmentConfirmedActivity.class)
                                            .putExtra("doctors", doctors)
                                            .putExtra("full_name",hashMap.getString("full_name"))
                                            .putExtra("date",hashMap.getString("created_at")));
                                }else {
                                    progressDialog.dismiss();
                                    jsb.toastLong(jsonObject.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                jsb.toastLong("Something went wrong");
                            }
                        }
                    });
                }else {
                    jsb.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            jsb.toastLong("Something went wrong");
                        }
                    });
                }
            }
        });
    }
}