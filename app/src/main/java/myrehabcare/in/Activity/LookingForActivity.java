package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityLookingForBinding;
import myrehabcare.in.databinding.EachLoockingForBinding;
import myrehabcare.in.databinding.SimpelButtonBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LookingForActivity extends AppCompatActivity {

    private ActivityLookingForBinding binding;
    private Activity activity;
    private Jsb jsb;
    private String service_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLookingForBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

        service_type = getIntent().getStringExtra("service_type");
        binding.pgBar.setVisibility(View.VISIBLE);
        binding.pgTv.setVisibility(View.GONE);


/*

        RequestBody body = new FormBody.Builder()
                .add("apikey", "gwtqs+F2wfs-6wzxRzPZy23Z1I0FIciP34CTRkWSim")
                .build();
*/

        RequestBody body = new FormBody.Builder()
                .add("service_type", service_type)
                .build();


        jsb.post(getString(R.string.baseUrl) + "api/mrc_services", body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                error("Something went wrong");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String bb = response.body().string();
                    Log.e(activity.getPackageName(),bb);
                    Log.e(activity.getPackageName(),bb);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(bb);

                                if (jsonObject.getString("auth-status").equals("true")){
                                    JSONArray jsonArray  = jsonObject.getJSONArray("results");

                                    binding.pgBar.setVisibility(View.GONE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject row = jsonArray.getJSONObject(i);
                                        String id = row.getString("id");
                                        String name = row.getString("cat_name");
                                        String cat_image = row.getString("cat_image");
                                        Log.e("tasting", "run: " +cat_image);
                                        addL(name, service_type, id, cat_image);
                                    }

                                }else {
                                    error("Something went wrong");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                error("Something went wrong");
                            }

                        }
                    });
                }else {
                    error("Something went wrong");
                }
            }
        });


        //SimpelButtonBinding button = SimpelButtonBinding.inflate(getLayoutInflater());
        //binding.layoutt.addView(button.nextBt);


    }

    private void addL(final String text, final String service_type, final String id, String cat_image){
        EachLoockingForBinding eachLoockingForBinding = EachLoockingForBinding.inflate(getLayoutInflater());
        eachLoockingForBinding.text.setText(text);
        if (cat_image != null && !cat_image.isEmpty()){
            Glide.with(activity).load(cat_image).placeholder(R.drawable.download).into(eachLoockingForBinding.catImg);

        }else {
            eachLoockingForBinding.catImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.download,activity.getTheme()));
        }
        eachLoockingForBinding.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, FormActivity.class).putExtra("value", text).putExtra("category", id).putExtra("service_type", service_type));
            }
        });
        binding.layoutt.addView(eachLoockingForBinding.getRoot());
    }


    private void error(final String error){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsb.toastLong(error);
                binding.pgTv.setVisibility(View.VISIBLE);
                binding.pgTv.setText(error);
                binding.pgBar.setVisibility(View.GONE);
            }
        });
    }

}