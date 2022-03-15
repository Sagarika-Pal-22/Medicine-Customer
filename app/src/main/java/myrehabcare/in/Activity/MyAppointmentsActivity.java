package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import myrehabcare.in.Adapters.MyAppointmentsAdapter;
import myrehabcare.in.Adapters.MyDoctorsAdapter;
import myrehabcare.in.Classes.Appointments;
import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityMyAppointmentsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAppointmentsActivity extends AppCompatActivity implements MyAppointmentsAdapter.ItemClickListener{

    private ActivityMyAppointmentsBinding binding;
    private Activity activity;
    private Jsb jsb;
    MyAppointmentsAdapter myAppointmentsAdapter;
    List<Appointments> appointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        appointmentsList = new ArrayList<>();


        setList("1");

       /* appointmentsList.add(new Appointments(new Doctors("Dr. Jatin Kulkarni", "Physiotherapist", 2f, "https://res.cloudinary.com/dnmstiszt/image/upload/v1601467819/notmy/download_6_mtpbxz.jpg","J.P Nagar"),new Date(),"Completed"));
        appointmentsList.add(new Appointments(new Doctors("Dr. Ashfaque Khan", "Dentist", 3f, "https://res.cloudinary.com/dnmstiszt/image/upload/v1601467819/notmy/download_6_mtpbxz.jpg","J.P Nagar"),new Date(),"Completed"));
        appointmentsList.add(new Appointments(new Doctors("Dr. Shivraj Deshmukh", "Dentist", 2.4f, "https://res.cloudinary.com/dnmstiszt/image/upload/v1601467819/notmy/download_6_mtpbxz.jpg","J.P Nagar"),new Date(),"Completed"));
        appointmentsList.add(new Appointments(new Doctors("Dr. Jatin Kulkarni", "Dentist", 3f, "https://res.cloudinary.com/dnmstiszt/image/upload/v1601467819/notmy/download_6_mtpbxz.jpg","J.P Nagar"),new Date(),"Completed"));
        appointmentsList.add(new Appointments(new Doctors("Dr. Jatin Kulkarni", "Dentist", 3f, "https://res.cloudinary.com/dnmstiszt/image/upload/v1601467819/notmy/download_6_mtpbxz.jpg","J.P Nagar"),new Date(),"Completed"));

        Collections.sort(appointmentsList, new Comparator<Appointments>() {
            @Override
            public int compare(Appointments o1, Appointments o2) {
                return o1.getAppointmentsDate().compareTo(o2.getAppointmentsDate());
            }
        });*/

        binding.onlineBt.setBackgroundTintList(getColorStateList(R.color.blue));
        binding.homeBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
        binding.clinicBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));

        binding.onlineBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList("1");
                binding.onlineBt.setBackgroundTintList(getColorStateList(R.color.blue));
                binding.homeBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
                binding.clinicBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
            }
        });

        binding.homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList("2");
                binding.onlineBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
                binding.homeBt.setBackgroundTintList(getColorStateList(R.color.blue));
                binding.clinicBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
            }
        });

        binding.clinicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList("3");
                binding.onlineBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
                binding.homeBt.setBackgroundTintList(getColorStateList(R.color.colorLightGreen));
                binding.clinicBt.setBackgroundTintList(getColorStateList(R.color.blue));
            }
        });


    }

    private void setList(String s) {
        binding.listView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBarTv.setVisibility(View.GONE);
        Log.e("a", jsb.getUser().getUser_id());
        Log.i("a", jsb.getUser().getUser_id());
        Log.w("a", jsb.getUser().getUser_id());
        RequestBody body = new FormBody.Builder()
                .add("customer_id", jsb.getUser().getUser_id())
                .add("service_type",s)
                .build();
        Log.d("DSdsdsds",s);

        jsb.post(getString(R.string.baseUrl) + "api/appointmentlist_forcustomer", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                setError("Something went wrong");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String b = response.body().string();
                    Log.e("a", b);
                    Log.i("a", b);
                    Log.w("a", b);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(b);
                                Log.d("DSdsdsds",jsonObject.toString());
                                if (jsonObject.getString("auth-status").equals("true")){

                                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                                    appointmentsList.clear();
                                    for(int i=0; i<jsonArray.length(); i++){
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        String dddd = object.getString("updated_at");
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

                                        Date date = jsb.stringToDate(simpleDateFormat, dddd);
                                        Log.e("a", object.getString("document"));
                                        Log.i("a", object.getString("document"));
                                        Log.w("a", object.getString("document"));
                                        Doctors doctors = new Doctors(object.getString("full_name"),
                                                object.getString("role_id"), 2f,
                                                object.getString("document"),
                                                object.getString("patient_problem"),
                                                object.getString("upload_prescription"),
                                                object.getString("service_type"),
                                                object.getString("service_type"),
                                                object.getString("category"),
                                                object.getString("patient_name"),
                                                object.getString("patient_age"),
                                                object.getString("patient_gender"),
                                                object.getString("patient_problem"),
                                                object.getString("visit_type"),
                                                object.getString("schedule_date"),
                                                "",
                                                object.getString("fees"),
                                                "",
                                                object.getString("patient_address"),
                                                null,
                                                object.getString("try_again"));
                                        Appointments appointments = new Appointments(doctors, date,"");//"Completed");
                                        appointmentsList.add(appointments);
                                    }
                                    if (jsonArray.length() == 0){
                                        setError("Appointments not found");
                                    }else {
                                        binding.progressBarTv.setVisibility(View.GONE);
                                        binding.progressBar.setVisibility(View.GONE);
                                        binding.listView.setVisibility(View.VISIBLE);

                                        binding.listView.setLayoutManager(new LinearLayoutManager(activity));
                                        myAppointmentsAdapter = new MyAppointmentsAdapter(activity,appointmentsList,MyAppointmentsActivity.this);
                                        binding.listView.setAdapter(myAppointmentsAdapter);

                                    }

                                }else {
                                    setError(jsonObject.getString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                setError("Appointments not found");
                            }
                        }
                    });

                }else {
                    setError("Something went wrong");
                }
            }
        });
    }

    private void setError(String error){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.listView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.progressBarTv.setVisibility(View.VISIBLE);
                binding.progressBarTv.setText(error);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position,Doctors appointments,String tryAgain_Status) {
        if(tryAgain_Status.equals("0")){
            startActivity(new Intent(activity, LoaderActivity.class)
                    .putExtra("service_type", appointments.getPatient_serviceType())
                    .putExtra("category", appointments.getPatient_category())
                    .putExtra("name", appointments.getPatient_name())
                    .putExtra("age", appointments.getPatient_age())
                    .putExtra("gender", appointments.getPatient_gender())
                    .putExtra("problem", appointments.getPatient_problem())
                    .putExtra("visit_type", appointments.getPatient_visitType())
                    .putExtra("schedule_date", appointments.getPatient_scheduleDate())
                    .putExtra("schedule_time", "")
                    .putExtra("fees", appointments.getPatient_fees())
                    .putExtra("discount", "")
                    .putExtra("address", appointments.getPatient_address())
                    .putExtra("prescriptionImagePath",appointments.getPatient_prescriptionImage()));
        }else{
            startActivity(new Intent(this, DrProfileActivity.class).putExtra("doctors", appointments));
        }

    }
}