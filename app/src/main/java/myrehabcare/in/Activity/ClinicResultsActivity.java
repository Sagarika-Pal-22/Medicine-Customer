package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myrehabcare.in.Adapters.ClinicAdapter;
import myrehabcare.in.Adapters.TransactionAdapter;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityClinicResultsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClinicResultsActivity extends AppCompatActivity {

    private ActivityClinicResultsBinding binding;
    private Activity activity;
    private Jsb jsb;
    List<JSONObject> data = new ArrayList<>();
    ClinicAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClinicResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);


        binding.prLl.setVisibility(View.VISIBLE);
        binding.prr.setVisibility(View.VISIBLE);
        binding.prTv.setVisibility(View.VISIBLE);

        binding.listView.setLayoutManager(new LinearLayoutManager(activity));
        dataAdapter = new ClinicAdapter(activity, data);
        binding.listView.setAdapter(dataAdapter);

        String book_id = getIntent().getStringExtra("book_id");
        Log.d("ghghhh",book_id);
        hitUrlForClinicList(book_id);
        /* RequestBody requestBody = new FormBody.Builder()
                .add("book_id",book_id)
                .build();

        jsb.post(getString(R.string.baseUrl) + "api/cliniclist", requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                error(e.getMessage(), e);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    Log.e("TAGTAGgg", "onResponse: "+body );
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        data.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        Log.e("TAGTAGgg12",jsonObject.toString());

                        for (int j=0;j<jsonArray.length();j++) {
                            JSONObject object = jsonArray.getJSONObject(j);
                            data.add(object);
                        }
                        dataAdapter.notifyDataSetChanged();

                        binding.prLl.setVisibility(View.GONE);
                        binding.prr.setVisibility(View.GONE);
                        binding.prTv.setVisibility(View.GONE);


                        dataAdapter.setClickListener(new ClinicAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(activity, ClinicDetailsActivity.class);
                                intent.putExtra("hashMap", dataAdapter.getItem(position).toString());
                                startActivity(intent);
                            }
                        });
                    } catch (JSONException e) {
                        error("Clinic Not Found",e);
                    }
                   // binding.prLl.setVisibility(View.GONE);
                }else {
                    binding.prLl.setVisibility(View.GONE);
                    error(response.message(),null);
                }
            }
        });
*/
     /*   data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
        data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
        data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
        data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
        data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
        data.add(getHashMap("My Dental Clinic","134, Mulund, Mumbai", "https://res.cloudinary.com/dnmstiszt/image/upload/v1603882044/The-Health-Clinic-of-the-Philippines_truhk8.jpg", "Jatin Kulkarni, Raj Shah"));
*/






    }

    private void error(String message, Exception e) {
        if (e != null){
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.prTv.setVisibility(View.VISIBLE);
                binding.prTv.setText(message);
                binding.prr.setVisibility(View.GONE);
                binding.prLl.setVisibility(View.VISIBLE);
            }
        });
    }
/*

    HashMap<String, Object> getHashMap(String name, String address, String image,String doctors){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("image", image);
        hashMap.put("address", address);
        hashMap.put("full_address", "M-104, Sai building, Mulund west, Mumbai-67");
        hashMap.put("doctors", doctors);
        hashMap.put("id", "2332121");
        hashMap.put("visiting_hours", "10:00 am - 6:00pm");
        hashMap.put("phone", "32123321");
        //name  image   address  full_address   doctors   visiting_hours   phone   id
        return hashMap;

        
    }
*/
private void hitUrlForClinicList(String bookId){
    String urlString = getString(R.string.baseUrl) + "api/cliniclist";
    Log.d("ResponseLogin",urlString);
    StringRequest stringRequest = new StringRequest(Request.Method.POST,urlString, new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("ResponseLogin",response);
            try {
                JSONObject jsonObject  = new JSONObject(response);
                data.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                Log.e("TAGTAGgg12",jsonObject.toString());

                for (int j=0;j<jsonArray.length();j++) {
                    JSONObject object = jsonArray.getJSONObject(j);
                    data.add(object);
                }
                dataAdapter.notifyDataSetChanged();

                binding.prLl.setVisibility(View.GONE);
                binding.prr.setVisibility(View.GONE);
                binding.prTv.setVisibility(View.GONE);


                dataAdapter.setClickListener(new ClinicAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(activity, ClinicDetailsActivity.class);
                        intent.putExtra("hashMap", dataAdapter.getItem(position).toString());
                        intent.putExtra("book_id",bookId);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error("Clinic Not Found",error);
            binding.prLl.setVisibility(View.GONE);
            binding.prr.setVisibility(View.GONE);
            binding.prTv.setVisibility(View.GONE);
        }
    })
    {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> params = new HashMap<String, String>();
            params.put("book_id",bookId);
            return params;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
}