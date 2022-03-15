package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myrehabcare.in.Adapters.MyDoctorsAdapter;
import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityFavBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavActivity extends AppCompatActivity {

    private ActivityFavBinding binding;
    private Activity activity;
    private Jsb jsb;
    List<Doctors> doctorsList;
    MyDoctorsAdapter myDoctorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        doctorsList = new ArrayList<>();

        binding.listView.setLayoutManager(new LinearLayoutManager(activity));
        setList();

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void setList(){
        binding.progBr.setVisibility(View.VISIBLE);
        binding.progBrTv.setVisibility(View.GONE);
        binding.listView.setVisibility(View.GONE);
        RequestBody body = new FormBody.Builder()
                .add("user_id", jsb.getUser().getUser_id())
                .build();

        Log.e("tasting", "setList: "+jsb.getUser().getUser_id());

        jsb.post("http://mrcadmin.in/api/fav_doctor_list", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setError("Something went wrong");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String b = response.body().string();
                    Log.e(activity.getPackageName(), b);
                    Log.w(activity.getPackageName(), b);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(b);


                                doctorsList.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("results");

                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objects = jsonArray.getJSONObject(i);

                                    Doctors  doctors = new Doctors();
                                    doctors.setName(objects.getString("full_name"));
                                    doctors.setAddress(objects.getString("email"));
                                    if (objects.has("about")){
                                        doctors.setAbout_us(objects.getString("about"));
                                    }else {
                                        doctors.setAbout_us("");
                                    }
                                    doctors.setContact(objects.getString("phone"));
                                    doctors.setDate(objects.getString("qualification"));
                                    doctors.setId(objects.getString("user_id"));
                                    doctors.setImage(objects.getString("document").replace("BaseURL/",getString(R.string.baseUrl)));
                                    Log.e("image",doctors.getImage());
                                    doctors.setStatus(objects.getString("status"));

                                    String aa = "0";
                                    try {
                                        aa = objects.getString("rating");
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    float a = 0;
                                    try {
                                        a = Float.parseFloat(aa);
                                    }catch (NumberFormatException e){
                                        e.printStackTrace();
                                    }

                                    doctors.setRating(a);
                                    doctors.setType(objects.getString("role")+":1");//+objects.getString("is_fav"));

                                    doctorsList.add(doctors);
                                }

                             /*   Collections.sort(doctorsList, new Comparator<Doctors>() {
                                    @Override
                                    public int compare(Doctors o1, Doctors o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });*/



                                if (doctorsList.size() == 0){
                                    setError("Doctors not found");
                                }else {
                                    myDoctorsAdapter = new MyDoctorsAdapter(activity, doctorsList, jsb);
                                    binding.listView.setAdapter(myDoctorsAdapter);
                                    binding.listView.setVisibility(View.VISIBLE);
                                    binding.progBr.setVisibility(View.GONE);
                                    binding.progBrTv.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                setError("Doctors not found");
                            }
                        }
                    });
                }
            }
        });

    }


    private void setError(String error) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.progBr.setVisibility(View.GONE);
                binding.progBrTv.setVisibility(View.VISIBLE);
                binding.listView.setVisibility(View.GONE);
                binding.progBrTv.setText(error);
            }
        });

    }

    void filter(String text){
        try {
            List<Doctors> temp = new ArrayList();
            for(Doctors d: doctorsList){
                text = text.toLowerCase();
                if(d.getName().toLowerCase().contains(text)){
                    temp.add(d);
                }else if(d.getType().toLowerCase().contains(text)){
                    temp.add(d);
                }else if(String.valueOf(d.getRating()).toLowerCase().contains(text)){
                    temp.add(d);
                }
            }
            //update recyclerview
            myDoctorsAdapter.updateList(temp);
        }catch (Exception e){

        }

    }

    private boolean byname = true;

    public void filter_by_name(View view) {
        try {
            if (byname){
                byname = false;
                binding.byname.setImageResource(R.drawable.ic_arrow_downward);

                Collections.sort(doctorsList, new Comparator<Doctors>() {
                    @Override
                    public int compare(Doctors o1, Doctors o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                });
            }else {
                byname = true;
                binding.byname.setImageResource(R.drawable.ic_arrow_upward);
                Collections.reverse(doctorsList);
            }

            myDoctorsAdapter.updateList(doctorsList);

        }catch (Exception e){

        }
    }
}