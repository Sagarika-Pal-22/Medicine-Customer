package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.JsbValidations;
import myrehabcare.in.databinding.ActivityForgotPasswordBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsbValidations.Email email = new JsbValidations.Email(binding.emailAddressEt);
                if (email.isValidEmail()){
                    final ProgressDialog progressDialog = jsb.getProgressDialog();
                    progressDialog.show();


                    jsb.post("http://mrcadmin.in/api/forget_password?email="+email.getEmail(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    jsb.toastShort("Something went wrong");
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
                                                jsb.toastLong(jsonObject.getString("message"));
                                               /* if (jsonObject.getBoolean("auth-status")){
                                                    jsb.toastLong(jsonObject.getString("message"));
                                                }else {

                                                }*/
                                               progressDialog.dismiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        jsb.toastShort("Something went wrong");
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
                                            jsb.toastShort("Something went wrong");
                                        }
                                    });
                                }
                            }else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        jsb.toastShort("Something went wrong");
                                    }
                                });
                            }
                        }
                    });


                }else {
                    binding.emailAddressEt.setError(email.getError());
                }
            }
        });

        binding.resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.submitBt.callOnClick();
            }
        });


    }
}