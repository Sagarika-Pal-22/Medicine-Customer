package myrehabcare.in.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import myrehabcare.in.Callback.EaSaGetUser;
import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.JsbValidations;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityMyProfileBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);


        User user = jsb.getUser();

        binding.bloodGroupTv.setText(user.getBlood_group());
        binding.cityTv.setText(user.getCity());
        binding.emailTv.setKeyListener(null);
        binding.emailTv.setText(user.getEmail());
        binding.phoneTv.setText(user.getPhone());
        binding.nameTv.setText(user.getFull_name());
      /*  binding.passwordTv.setText(jsb.getUser().getPassword());
        binding.cnfPasswordTv.setText(jsb.getUser().getPassword());*/

        Glide.with(activity).load(/*getString(R.string.baseUrl)+"my-assets/image/"+*/user.getProfile_image()).placeholder(R.drawable.ic_def_user).into(binding.profileImage);
        Log.d("ffdsdf",getString(R.string.baseUrl)+"my-assets/image/"+user.getProfile_image());
        binding.profileImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileImage.callOnClick();
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(activity)
                        .crop()                  //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();

            startProfileUpdating(fileUri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }



    }

    private void startProfileUpdating(Uri fileUri) {

        ProgressDialog progressDialog = jsb.getProgressDialog();
        progressDialog.show();

        File profile = new File(fileUri.getPath());
        //binding.profileImage.setImageURI(fileUri);
        OkHttpClient client;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(2, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(2, TimeUnit.MINUTES) // write timeout
                .readTimeout(2, TimeUnit.MINUTES); // read timeout

        client = builder.build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id", jsb.getUser().getUser_id())
                .addFormDataPart("full_name", jsb.getUser().getFull_name())
                .addFormDataPart("phone", jsb.getUser().getPhone())
                .addFormDataPart("blood_group", jsb.getUser().getBlood_group())
                .addFormDataPart("city", jsb.getUser().getCity())
                .addFormDataPart("password", binding.passwordTv.getText().toString())
                .addFormDataPart("profile_image", profile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), profile))
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.baseUrl) + "api/edit_customer_profile")
                .method("POST", body)
                .addHeader("Cookie", "PHPSESSID=e176fe871b9bd4068a4e3bc47cef005574ab648a")
                .build();
        /*Response response =*/
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jsb.toastLong(e.getMessage());
                        progressDialog.dismiss();
                        Log.e("tasting", "run: ",e );
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()){

                    final String bb = response.body().string();
                    Log.e("tasting", "run: "+bb );

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(bb);
                                jsb.toastLong(jsonObject.getString("message"));

                                Log.e("tasting", "run: "+jsonObject.getString("message") );

                                jsb.getUserDataFromServer(new JsbValidations.Email(jsb.getUser().getEmail()), new JsbValidations.Password(jsb.getUser().getPassword()), new EaSaGetUser() {
                                    @Override
                                    public void Error(String error) {
                                        jsb.toastLong(error);
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void Success(User user, String message) {
                                        jsb.updateUser(user);
                                        progressDialog.dismiss();
                                        Glide.with(activity).load(/*getString(R.string.baseUrl)+"my-assets/image/"+*/user.getProfile_image()).placeholder(R.drawable.ic_def_user).into(binding.profileImage);
                                        Log.d("ffdsdf",/*getString(R.string.baseUrl)+"my-assets/image/"+*/user.getProfile_image());
                                        Log.e("tasting", "Success: "+ user.toString());
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                jsb.toastLong(e.getMessage());
                                progressDialog.dismiss();
                                Log.e("tasting", "run: ",e );
                            }
                        }
                    });
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jsb.toastLong(response.message());
                            progressDialog.dismiss();
                        }
                    });
                    Log.e("tasting", response.message());
                }
            }
        });



    }

    public void submittt(View view) {

        JsbValidations.Phone phone = new JsbValidations.Phone(binding.phoneTv);
        JsbValidations.Name name = new JsbValidations.Name(binding.nameTv);
        JsbValidations.Email email = new JsbValidations.Email(binding.emailTv);
        JsbValidations.CnfPassword cnfPassword = new JsbValidations.CnfPassword(binding.passwordTv,binding.cnfPasswordTv);


        final String bloodGroup = binding.bloodGroupTv.getText().toString();
        final String city = binding.cityTv.getText().toString();

        if (email.isValidEmail() && name.isValidName() && phone.isValidPhone() && !bloodGroup.isEmpty()
                && !city.isEmpty() && cnfPassword.isCheckPassword()) {

            ProgressDialog progressDialog = jsb.getProgressDialog();
            progressDialog.show();

            User user = jsb.getUser();

            user.setBlood_group(bloodGroup);
            user.setCity(city);
            user.setPhone(phone.getPhone());
            user.setPassword(binding.passwordTv.toString());
            user.setFull_name(name.getName());
            //user.setEmail(email.getEmail());
            jsb.updateUser(user);

            RequestBody requestBody = new FormBody.Builder()
                    .add("user_id", user.getUser_id())
                    .add("full_name", user.getFull_name())
                    .add("phone", user.getPhone())
                    .add("blood_group", user.getBlood_group())
                    .add("city", user.getCity())
                    .add("password", binding.passwordTv.getText().toString())
                    //.add("profile_image",user.getProfile_image())
                    .build();

            jsb.post(getString(R.string.baseUrl) + "api/edit_customer_profile", requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            jsb.toastLong(e.getMessage());
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        String body = response.body().string();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    jsb.toastLong(jsonObject.getString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    jsb.toastLong(e.getMessage());
                                }
                            }
                        });
                    }else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                jsb.toastLong(response.message());
                            }
                        });
                    }
                }
            });

        }else {
            if(!email.isValidEmail()){
                binding.emailTv.setError(email.getError());
            }
            if (!name.isValidName()){
                binding.nameTv.setError(name.getError());
            }
            if (!phone.isValidPhone()){
                binding.phoneTv.setError(phone.getError());
            }
            if (bloodGroup.isEmpty()){
                binding.bloodGroupTv.setError("Please Enter Blood Group");
            }
            if (city.isEmpty()){
                binding.cityTv.setError("Please Enter City");
            }
            if(!cnfPassword.isCheckPassword()){
                binding.passwordTv.setError(cnfPassword.getError());
                binding.passwordTv.requestFocus();
            }
        }

    }
}