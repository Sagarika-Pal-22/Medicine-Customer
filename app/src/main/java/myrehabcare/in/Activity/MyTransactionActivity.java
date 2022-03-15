package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import myrehabcare.in.Adapters.TransactionAdapter;
import myrehabcare.in.Classes.Transaction;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityMyTransactionBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyTransactionActivity extends AppCompatActivity {

    private ActivityMyTransactionBinding binding;
    private Activity activity;
    private Jsb jsb;
    List<Transaction> transactions = new ArrayList<>();
    TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);


        binding.prLl.setVisibility(View.VISIBLE);
        binding.prrr.setVisibility(View.VISIBLE);
        binding.PrTv.setVisibility(View.GONE);



        RequestBody requestBody = new FormBody.Builder()
                .add("customer_id",jsb.getUser().getUser_id())
                .build();


        jsb.post(getString(R.string.baseUrl) + "api/mytransaction", requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                error(e.getMessage(),e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(body);

                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        transactions.clear();

                        int i = 0;
                        for (int length = jsonArray.length(); length > 0; length--) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            i++;

                            String patient_id = jsonObject1.getString("doctor_id");
                            String patient_name = jsonObject1.getString("doctor_name");
                            String fees = jsonObject1.getString("fees");
                            String created_at = jsonObject1.getString("created_at");


                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM ,yyyy hh:mm a");

                            Date date = new Date();
                            try {
                                date = simpleDateFormat1.parse(created_at);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (patient_name != null){
                                transactions.add(new Transaction(patient_name,fees,date,"2"));
                            }

                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.sort(transactions, new Comparator<Transaction>() {
                                    @Override
                                    public int compare(Transaction o1, Transaction o2) {
                                        return o1.getDate().compareTo(o2.getDate());
                                    }
                                });
                                binding.prLl.setVisibility(View.GONE);
                                binding.prrr.setVisibility(View.GONE);
                                binding.PrTv.setVisibility(View.GONE);

                                binding.listView.setLayoutManager(new LinearLayoutManager(activity));
                                transactionAdapter = new TransactionAdapter(activity, transactions);
                                binding.listView.setAdapter(transactionAdapter);

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
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error("No Transaction Found",e);
                    }

                }else {
                    error("Something went wrong",null);
                }
            }
        });





    }

    private void error(@NotNull final String message, Exception e) {
        e.printStackTrace();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                binding.prLl.setVisibility(View.VISIBLE);
                binding.prrr.setVisibility(View.GONE);
                binding.PrTv.setVisibility(View.VISIBLE);
                binding.PrTv.setText(message);
            }
        });
    }

    void filter(String text){
        List<Transaction> temp = new ArrayList();
        for(Transaction d: transactions){
            text = text.toLowerCase();
            if(d.getAmount().toLowerCase().contains(text)){
                temp.add(d);
            }else if(d.getStatus().toLowerCase().contains(text)){
                temp.add(d);
            }else if(d.getToName().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        transactionAdapter.updateList(temp);
    }
}