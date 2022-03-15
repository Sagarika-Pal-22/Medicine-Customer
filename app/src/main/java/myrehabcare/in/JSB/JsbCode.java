package myrehabcare.in.JSB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import myrehabcare.in.Callback.EaSaGetUser;
import myrehabcare.in.Classes.User;
import myrehabcare.in.MainActivity;
import myrehabcare.in.R;
import myrehabcare.in.Service.RegistrationIntentService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsbCode extends JsbUi {

    private Activity activity;
    private User user;
    private SharedPreferences preferences;
    private Gson gson = new Gson();

    public JsbCode(Activity activity){
        super(activity);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        updateUser();
        updateToken();
    }

    public void updateToken(){
        Intent intent = new Intent(activity, RegistrationIntentService.class);
        activity.startService(intent);

       /* if (user != null){
            String refreshToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseDatabase.getInstance().getReference("Tokens").child(user.getUser_id()).setValue(refreshToken);
            Log.e("FMC Token",refreshToken);
        }*/
    }


    public Date stringToDate(SimpleDateFormat simpleDateFormat, String date){
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  new Date();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public User getUser() {
        return user;
    }

    public void updateUser(User user1){
        String json = gson.toJson(user1);
        preferences.edit().putString("User", json).apply();
        updateUser();

    }

    public void updateUser(){
        String json = preferences.getString("User", "");
        Log.e("User",json);
        if (json.equals("")){
            user = null;
        }else {
            try {
                user = gson.fromJson(json, User.class);
            }catch (Exception e){
                user = null;
            }
        }
    }

    public void Logout(){
        final ProgressDialog progressDialog = getProgressDialog();
        progressDialog.show();

        post("http://mrcadmin.in/api/logout?user_id="+getUser().getUser_id(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        toastShort("Something went wrong");
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        final JSONObject jsonObject = new JSONObject(body);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    progressDialog.dismiss();
                                    toastLong(jsonObject.getString("message"));
                                    if (!jsonObject.getBoolean("auth-status")){

                                        preferences.edit().putString("User", "").apply();
                                        preferences.edit().clear().apply();
                                        activity.startActivity(new Intent(activity, MainActivity.class));
                                        updateUser();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            toastShort("Something went wrong");
                                        }
                                    });
                                }

                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                toastShort("Something went wrong");
                            }
                        });
                    }
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            toastShort("Something went wrong");
                        }
                    });
                }
            }
        });


    }

    OkHttpClient client = new OkHttpClient();

    public Call post(String url, RequestBody body, Callback callback) {
        /*RequestBody body = new FormBody.Builder()
                .add("apikey", "gwtqs+F2wfs-6wzxRzPZy23Z1I0FIciP34CTRkWSim")
                .add("message",message)
                .add("sender","TXTLCL")
                .add("numbers","91"+number)
                .build();*/
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public Call post(String url, Callback callback) {
        Request request;
        if (user == null){
            request = new Request.Builder()
                    .url(url)
                    .header("Content-Type","application/json")
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .header("Content-Type","application/json")
                    .header("Authorization",user.getToken())
                    .build();
        }


        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void post(String url) {
        post(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /*if (response.isSuccessful()){
                    final String body = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, body, Toast.LENGTH_SHORT).show();
                        }
                    });
                }*/
            }
        });
    }

    public String getText(EditText editText){
        return editText.getText().toString();
    }

    public void getUserDataFromServer(Email email, Password password, final EaSaGetUser eaSaGetUser){
        RequestBody body = new FormBody.Builder()
                .add("username", email.getEmail())
                .add("password", password.getPassword())
                .build();

        post(activity.getString(R.string.baseUrl)+"api/login", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eaSaGetUser.Error("Something went wrong");
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        final JSONObject jsonObject = new JSONObject(body);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (jsonObject.getString("auth-status").equals("true")){

                                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                        User user = new User();
                                        user.setUser_id(jsonObject1.getString("user_id"));
                                        user.setFull_name(jsonObject1.getString("full_name"));
                                        user.setEmail(jsonObject1.getString("email"));
                                        user.setPassword(jsonObject1.getString("password"));
                                        user.setPhone(jsonObject1.getString("phone"));
                                        user.setBlood_group(jsonObject1.getString("blood_group"));
                                        user.setCity(jsonObject1.getString("city"));
                                        user.setStatus(jsonObject1.getString("status"));
                                        user.setToken(jsonObject1.getString("token"));
                                        if (jsonObject1.has("profile_image")){
                                            user.setProfile_image(jsonObject1.getString("profile_image"));
                                        }else {
                                            user.setProfile_image("");
                                        }
                                        user.setDevice_type(jsonObject1.getString("device_type"));
                                        user.setCreated_at(jsonObject1.getString("created_at"));
                                        user.setUpdated_at(jsonObject1.getString("updated_at"));

                                        updateUser(user);

                                        eaSaGetUser.Success(user,jsonObject.getString("message"));
                                    }else if (jsonObject.getString("auth-status").equals("false")){
                                        eaSaGetUser.Error("Invalid username and password");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    eaSaGetUser.Error("Something went wrong");
                                }

                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eaSaGetUser.Error("Something went wrong");
                            }
                        });
                    }
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eaSaGetUser.Error("Something went wrong");
                        }
                    });
                }
            }
        });
    }



    public String toFirstLatterUperCase(String v){
        return v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase();
    }

    public String random(int len) {
        int min = 0;
        int max = 9;
        Random r = new Random();
        String randomid = "";
        for (int i = 0; i < len; i++){
            randomid += r.nextInt(max - min + 1) + min;
        }
        return randomid;
    }

    public String random(int len,boolean string) {
        if (string) {
            String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890123456789012345678901234567890abcdefghijklmnqpurstuvwxyz";
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();
            while (salt.length() < len) {
                int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                salt.append(SALTCHARS.charAt(index));
            }
            String saltStr = salt.toString();
            return saltStr;
        }else {
            return random(len);
        }
    }


}
