package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import myrehabcare.in.Callback.EaSaGetUser;
import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.JsbValidations;
import myrehabcare.in.QuickBlox.QuickBloxUtils.App;
import myrehabcare.in.QuickBlox.QuickBloxUtils.Consts;
import myrehabcare.in.QuickBlox.QuickBloxUtils.SharedPrefsHelper;
import myrehabcare.in.QuickBlox.QuickBloxUtils.ToastUtils;
import myrehabcare.in.QuickBlox.Service.LoginService;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityRegisterBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private Activity activity;
    private Jsb jsb;
    private SharedPrefsHelper sharedPrefsHelper;
    private QBUser userForSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding.signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
            }
        });

    }

    private void Signup() {

        final JsbValidations.Email email = new JsbValidations.Email(binding.emailAddressEt);
        final JsbValidations.Password password = new JsbValidations.Password(binding.passwordEt);
        final JsbValidations.Name name = new JsbValidations.Name(binding.fullNameEt);
        final JsbValidations.Phone phone = new JsbValidations.Phone(binding.phoneNumberEt);
        final String bloodGroup = binding.bloodGroupEt.getText().toString();
        final String city = binding.cityEt.getText().toString();

        if (email.isValidEmail() && password.isValidPassword() && name.isValidName() && phone.isValidPhone() && !bloodGroup.isEmpty() && !city.isEmpty()){


            final ProgressDialog progressDialog = jsb.getProgressDialog();
            progressDialog.show();
            RequestBody body = new FormBody.Builder()
                    .add("password", password.getPassword())
                    .add("phone",phone.getPhone())
                    .add("blood_group",bloodGroup)
                    .add("city",city)
                    .add("email",email.getEmail())
                    .add("full_name",name.getName())
                    .add("device_type","1")
                    .build();

            jsb.post("http://mrcadmin.in/api/register", body, new Callback() {
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
                                        if (jsonObject.getString("auth-status").equals("false")) {
                                            jsb.toastShort(jsonObject.getString("message"));
                                            progressDialog.dismiss();
                                            return;
                                        }else if (jsonObject.getString("auth-status").equals("true")) {

                                            JSONArray jsonArray = jsonObject.getJSONArray("results");

                                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                            User user = new User();
                                            user.setBlood_group(bloodGroup);
                                            user.setCity(city);
                                            user.setCreated_at(jsonObject1.getString("created_at"));
                                            user.setDevice_type(jsonObject1.getString("device_type"));
                                            user.setUser_id(jsonObject1.getString("user_id"));
                                            user.setFull_name(jsonObject1.getString("full_name"));
                                            user.setEmail(jsonObject1.getString("email"));
                                            user.setPassword(jsonObject1.getString("password"));
                                            user.setPhone(jsonObject1.getString("phone"));
                                            user.setStatus(jsonObject1.getString("status"));
                                            user.setToken(jsonObject1.getString("token"));
                                            user.setUpdated_at(jsonObject1.getString("updated_at"));

                                            jsb.updateUser(user);

                                            jsb.toastShort(jsonObject.getString("message"));
                                            progressDialog.dismiss();
                                            startActivity(new Intent(activity,HomeActivity.class));
                                            userForSave = createQBUserWithCurrentData(user.getUser_id(),user.getFull_name());
                                            startSignUpNewUser(userForSave);
                                            /*jsb.getUserDataFromServer(email, password, new EaSaGetUser() {
                                                @Override
                                                public void Error(String error) {
                                                    try {
                                                        jsb.toastShort(jsonObject.getString("message"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    startActivity(new Intent(activity,LoginActivity.class).putExtra("email",email.getEmail()).putExtra("password",password.getPassword()));
                                                    progressDialog.dismiss();
                                                }

                                                @Override
                                                public void Success(User user, String message) {
                                                    try {
                                                        jsb.toastShort(jsonObject.getString("message"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(activity,HomeActivity.class));
                                                }
                                            });*/
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
            if (!email.isValidEmail()){
                binding.emailAddressEt.setError(email.getError());
            }
            if (!password.isValidPassword()){
                binding.passwordEt.setError(password.getError());
            }
            if (!name.isValidName()){
                binding.fullNameEt.setError(name.getError());
            }
            if (!phone.isValidPhone()){
                binding.phoneNumberEt.setError(phone.getError());
            }
            if (bloodGroup.isEmpty()){
                binding.bloodGroupEt.setError("Please Enter Blood Group");
            }
            if (city.isEmpty()){
                binding.cityEt.setError("Please Enter City");
            }

        }
        //startActivity(new Intent(activity,HomeActivity.class));
    }

    public void signin(View view) {
        onBackPressed();
    }
    private QBUser createQBUserWithCurrentData(String userLogin, String userFullName) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userLogin) && !TextUtils.isEmpty(userFullName)) {
            qbUser = new QBUser();
            qbUser.setLogin(userLogin);
            qbUser.setFullName(userFullName);
            qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        }
        return qbUser;
    }
    private void startSignUpNewUser(final QBUser newUser) {
        Log.d("TAG", "SignUp New User");
        signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d("SIGNUP", "SignUp Successful");
                        saveUserData(newUser);
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.d("SIGNUP Error", "Error SignUp" + e.getMessage());
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser);
                        } else {
                            ToastUtils.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }
    public void signUpNewUser(final QBUser newQbUser, final QBEntityCallback<QBUser> callback) {
        QBUsers.signUp(newQbUser).performAsync(callback);
    }

    public void signInUser(final QBUser currentQbUser, final QBEntityCallback<QBUser> callback) {
        QBUsers.signIn(currentQbUser).performAsync(callback);
    }
    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        userForSave = qbUser;
        startLoginService(qbUser);
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private void signInCreatedUser(final QBUser qbUser) {
        Log.d("SIGNIN", "SignIn Started");
        signInUser(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle params) {
                Log.d("SIGNIn Success", "SignIn Successful");
                sharedPrefsHelper.saveQbUser(userForSave);
                updateUserOnServer(qbUser);
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.d("SIGNIn Error", "Error SignIn" + responseException.getMessage());
                ToastUtils.longToast(R.string.sign_in_error);
            }
        });
    }

    private void updateUserOnServer(QBUser user) {
        user.setPassword(null);
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                //   OpponentsActivity.start(OtpActivity.this);
                //  finish();
            }

            @Override
            public void onError(QBResponseException e) {
                ToastUtils.longToast(R.string.update_user_error);
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }
}