package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.VolleyMultipartRequest;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityLoaderBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoaderActivity extends AppCompatActivity {

    private ActivityLoaderBinding binding;
    private Activity activity;
    private int counter = 0;
    private int total_minutes = 5;
    private int totalsecond = 0;
    private Jsb jsb;
    int aabb = 0;
    private ProgressDialog progressDialog;
    private String BookId;
    private CountDownTimer countDownTimer;
    private String schedule_time="";
    File precriptionImageFile;
    Bitmap precriptionImageBitmap=null;
    String service_type,category,name,age,gender,problem,visit_type,schedule_date,fees,address,prescriptionImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        progressDialog = jsb.getProgressDialog();

        setPgWidth(0);

        category = getIntent().getStringExtra("category");
        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        gender = getIntent().getStringExtra("gender");
        problem = getIntent().getStringExtra("problem");
        visit_type = getIntent().getStringExtra("visit_type");
        schedule_date = getIntent().getStringExtra("schedule_date");
        schedule_time = getIntent().getStringExtra("schedule_time");
        prescriptionImagePath =  getIntent().getStringExtra("prescriptionImagePath");
        if(prescriptionImagePath!=null){
            File imgFile = new  File(prescriptionImagePath);
            precriptionImageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        if(schedule_time==null){
            schedule_time = "";
        }
        fees = getIntent().getStringExtra("fees");
        address = getIntent().getStringExtra("address");
        service_type = getIntent().getStringExtra("service_type");
        updateee(service_type, category, name, age, gender, problem, visit_type, schedule_date,schedule_time, fees, address);
    }

    private void setPgWidth(int width){
        ViewGroup.LayoutParams layoutParams = binding.pgFg.getLayoutParams();
        layoutParams.width = width;
        binding.pgFg.setLayoutParams(layoutParams);
    }
    private void updateee(String service_type, String category, String name, String age, String gender, String problem, String visit_type, String schedule_date,String schedule_time, String fees, String address) {
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST,getString(R.string.baseUrl)+"api/bookappointment", new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.d("JsonResponsecheck",json);
                    JSONObject jsonObject  = new JSONObject(json);
                    JSONObject object = jsonObject.getJSONObject("results");
                    Log.d("DSfdfsdfs", String.valueOf(jsonObject));
                    if (jsonObject.getString("auth-status").equals("true")){
                        jsb.toastShort(jsonObject.getString("message"));
                        progressDialog.dismiss();
                        totalsecond = total_minutes*60;

                        BookId = jsonObject.getJSONObject("results").getString("book_id");


                        if (service_type.equals("3")){
                            startActivity(new Intent(activity, ClinicResultsActivity.class).putExtra("book_id",BookId));
                            finish();
                        }else {
                            countDownTimer = new CountDownTimer(totalsecond*1000,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int aa = binding.pgBg.getMeasuredWidth()/totalsecond;

                                    counter++;
                                    aabb++;
                                    setPgWidth(aa*counter);

                                    if (aabb <= 30){
                                        aabb = 0;
                                        checkStatus();
                                    }

                                }
                                @Override
                                public void onFinish() {
                                    setPgWidth(binding.pgBg.getMeasuredWidth());
                                    Intent i  = new Intent(LoaderActivity.this,OopsActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }.start();
                        }





                    }else {
                        error(jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }   catch (Exception e) {
                }
                progressDialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { // send string data only...
                Map<String, String> params = new HashMap<String, String>();
                 params.put("category", category);
                        params.put("patient_name",name);
                        params.put("patient_age",age);
                        params.put("patient_id",jsb.getUser().getUser_id());
                        params.put("service_type",service_type);
                        params.put("patient_gender", gender);
                        params.put("patient_problem", problem);
                        params.put("patient_address", address);
                        params.put("visit_type", visit_type);
                        params.put("schedule_date", schedule_date+" "+schedule_time);
                        //.add("schedule_time", schedule_time);
                        params.put("fees",fees);
                return params;
            }
            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError { //method for use send multiple image to server....
                HashMap<String, VolleyMultipartRequest.DataPart> imageParam = new HashMap<>();
                if(precriptionImageBitmap!=null){
                    VolleyMultipartRequest.DataPart dataPart0 = new VolleyMultipartRequest.DataPart(String.valueOf(System.currentTimeMillis()+".png"),convertBitmapToByteArray(LoaderActivity.this,precriptionImageBitmap));
                    imageParam.put("upload_illness",dataPart0);  //send recycler image 1
                    Log.d("dsfsdfsdfsdfsdf",precriptionImageBitmap.toString());
                    //Log.d("dsfsdfsdfsdfsdf", String.valueOf(convertBitmapToByteArray(MyProfileActivity.this,bitmap)));
                }
                return imageParam;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }
  /*  private void updateee(String service_type, String category, String name, String age, String gender, String problem, String visit_type, String schedule_date,String schedule_time, String fees, String address) {
        progressDialog.show();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("category", category)
                .addFormDataPart("patient_name",name)
                .addFormDataPart("patient_age",age)
                .addFormDataPart("patient_id",jsb.getUser().getUser_id())
                .addFormDataPart("service_type",service_type)
                .addFormDataPart("patient_gender", gender)
                .addFormDataPart("patient_problem", problem)
                .addFormDataPart("patient_address", address)
                .addFormDataPart("visit_type", visit_type)
                .addFormDataPart("schedule_date", schedule_date+" "+schedule_time)
                //.add("schedule_time", schedule_time)
                .addFormDataPart("fees",fees)
                .addFormDataPart("upload_illness", precriptionImageFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), precriptionImageFile))
                .build();
*//*
        RequestBody body = new FormBody.Builder()
                .add("category", category)
                .add("patient_name",name)
                .add("patient_age",age)
                .add("patient_id",jsb.getUser().getUser_id())
                .add("service_type",service_type)
                .add("patient_gender", gender)
                .add("patient_problem", problem)
                .add("patient_address", address)
                .add("visit_type", visit_type)
                .add("schedule_date", schedule_date+" "+schedule_time)
                //.add("schedule_time", schedule_time)
                .add("fees",fees)
                .build();
*//*
*//*        Request request = new Request.Builder()
                .url(getString(R.string.baseUrl) + "api/doctor_register")
                .method("POST", body)
                .addHeader("Cookie", "PHPSESSID=e176fe871b9bd4068a4e3bc47cef005574ab648a")
                .build();*//*
        jsb.post(getString(R.string.baseUrl)+"api/bookappointment", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                error(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
               // Log.d("responsesUI",response.body().string());
                if (response.isSuccessful()){
                    final String bb = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(bb);
                                JSONObject object = jsonObject.getJSONObject("results");
                                Log.d("DSfdfsdfs", String.valueOf(jsonObject));
                                if (jsonObject.getString("auth-status").equals("true")){
                                    jsb.toastShort(jsonObject.getString("message"));
                                    progressDialog.dismiss();
                                    totalsecond = total_minutes*60;

                                    BookId = jsonObject.getJSONObject("results").getString("book_id");


                                    if (service_type.equals("3")){
                                        startActivity(new Intent(activity, ClinicResultsActivity.class).putExtra("book_id",BookId));
                                        finish();
                                    }else {
                                        countDownTimer = new CountDownTimer(totalsecond*1000,1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                int aa = binding.pgBg.getMeasuredWidth()/totalsecond;

                                                counter++;
                                                aabb++;
                                                setPgWidth(aa*counter);

                                                if (aabb <= 30){
                                                    aabb = 0;
                                                    checkStatus();
                                                }

                                            }
                                            @Override
                                            public void onFinish() {
                                                setPgWidth(binding.pgBg.getMeasuredWidth());
                                                Intent i  = new Intent(LoaderActivity.this,OopsActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }.start();
                                    }





                                }else {
                                    error(jsonObject.getString("message"));
                                }

                            } catch (JSONException e) {
                                error("Something went wrong");
                            }

                        }
                    });

                }else {
                    error("Something went wrong");
                }
            }
        });
    }*/

    private void checkStatus() {
        RequestBody body = new FormBody.Builder()
                .add("book_id", BookId)
                .build();
        jsb.post(getString(R.string.baseUrl)+"api/appointment_check_status", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String bb = response.body().string();
                    Log.e("tasting_for_loder", "onResponse: "+bb );
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(bb);

                                if (jsonObject.getJSONObject("results").has("full_name")){
                                    JSONObject object = jsonObject.getJSONObject("results");
                                    jsb.toastLong(jsonObject.getString("message"));
                                    Log.e("tasting_for_loder", "onResponse: "+jsonObject.toString() );
                                    if (service_type.equals("3")){

                                    }else {

                                        Doctors doctors = new Doctors();
                                        doctors.setAbout_us(object.getString("about"));
                                        doctors.setAddress(object.getString("email"));
                                        doctors.setContact(object.getString("email"));
                                        doctors.setDate(object.getString("date"));
                                        doctors.setId(object.getString("doctor_id"));
                                        doctors.setImage(object.getString("doctor_image"));
                                        doctors.setName(object.getString("full_name"));
                                        doctors.setService_type(object.getString("service_type"));
                                        doctors.setUploadPrescription(object.getString("upload_prescription"));
                                        float a = 0f;
                                        if (!object.getString("doctor_rating").isEmpty()){
                                            a = Float.parseFloat(object.getString("doctor_rating"));
                                        }
                                        doctors.setRating(a);
                                        doctors.setStatus("");
                                        doctors.setType(object.getString("role_name"));
                                        startActivity(new Intent(activity, AppointmentConfirmedActivity.class).putExtra("doctors", doctors).putExtra("full_name",object.getString("full_name")).putExtra("date",object.getString("date")));
                                    }
                                    countDownTimer.cancel();
                                    finish();
                                }


                               /* {
                                    "message": "Appointment Confirmed",
                                        "statuscode": 200,
                                        "auth-status": "true",
                                        "results": {
                                    "full_name": "Vicky Singh",
                                            "email": "vicky56@gmail.coms",
                                            "role_id": "1",
                                            "role_name": "",
                                            "date": "2020-10-13 12:29:36"
                                }
                                }

                                {
                                    "message": "Please Try Later",
                                        "statuscode": 200,
                                        "auth-status": "true",
                                        "results": ""
                                }*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }


    private void error(final String error){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsb.toastShort(error);
                progressDialog.dismiss();
            }
        });
    }
    public byte[] convertBitmapToByteArray(Context context, Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }
}