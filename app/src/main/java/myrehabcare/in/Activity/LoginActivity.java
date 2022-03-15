package myrehabcare.in.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import myrehabcare.in.Callback.EaSaGetUser;
import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.JsbValidations;
import myrehabcare.in.QuickBlox.QuickBloxUtils.App;
import myrehabcare.in.QuickBlox.QuickBloxUtils.Consts;
import myrehabcare.in.QuickBlox.QuickBloxUtils.PermissionsChecker;
import myrehabcare.in.QuickBlox.QuickBloxUtils.SharedPrefsHelper;
import myrehabcare.in.QuickBlox.QuickBloxUtils.ToastUtils;
import myrehabcare.in.QuickBlox.Service.LoginService;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityLoginBinding;
import myrehabcare.in.databinding.DialogLocationBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Activity activity;
    private Jsb jsb;
    String token="";
    private static final String OVERLAY_PERMISSION_CHECKED_KEY = "overlay_checked";
    private static final String MI_OVERLAY_PERMISSION_CHECKED_KEY = "mi_overlay_checked";

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1764;

    private SharedPrefsHelper sharedPrefsHelper;
    private QBUser userForSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.d("Sdcdscsd",token);
                        //      sharedPreference.putString(Config.TOKEN_NOTIFICATION,token);
                    }
                });
        if (getIntent().getExtras() != null){
            binding.emailAddressEt.setText(getIntent().getExtras().getString("email"));
            binding.passwordEt.setText(getIntent().getExtras().getString("password"));
            binding.signInBt.callOnClick();
        }

        binding.signInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signin();
            }
        });
        checkOverlayPermissions();
    }

    public void signup(View view) {

        startActivity(new Intent(activity,RegisterActivity.class));
    }

    public void Signin() {
        JsbValidations.Email email = new JsbValidations.Email(binding.emailAddressEt);
        final JsbValidations.Password password = new JsbValidations.Password(binding.passwordEt);
        if (email.isValidEmail() && password.isValidPassword()){

            final ProgressDialog progressDialog = jsb.getProgressDialog();
            progressDialog.show();

            jsb.getUserDataFromServer(email, password, new EaSaGetUser() {
                @Override
                public void Error(String error) {
                    jsb.toastShort(error);
                    progressDialog.dismiss();
                    Log.d("jhnnnnj",error.toString());
                }

                @Override
                public void Success(User user, String message) {
                    Log.d("jhnnnnj",message);
                    progressDialog.dismiss();
                    jsb.toastShort(message);
                    startActivity(new Intent(activity,HomeActivity.class));
                    userForSave = createQBUserWithCurrentData(user.getUser_id()/*"1"*/,user.getFull_name());
                    startSignUpNewUser(userForSave);
                }
            });

        }else {
            if (!email.isValidEmail()){
                binding.emailAddressEt.setError(email.getError());
            }
            if (!password.isValidPassword()){
                binding.passwordEt.setError(password.getError());
            }
        }

    }


    public void forgot_password(View view) {
        startActivity(new Intent(activity,ForgotPasswordActivity.class));
    }

    private boolean checkOverlayPermissions() {
        Log.e("TAG", "Checking Permissions");
        boolean overlayChecked = sharedPrefsHelper.get(OVERLAY_PERMISSION_CHECKED_KEY, false);
        boolean miOverlayChecked = sharedPrefsHelper.get(MI_OVERLAY_PERMISSION_CHECKED_KEY, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this) && !overlayChecked) {
                Log.e("TAG", "Android Overlay Permission NOT Granted");
                buildOverlayPermissionAlertDialog();
                return false;
            } else if (PermissionsChecker.isMiUi() && !miOverlayChecked) {
                Log.e("TAG", "Xiaomi Device. Need additional Overlay Permissions");
                buildMIUIOverlayPermissionAlertDialog();
                return false;
            }
        }
        Log.e("TAG", "All Overlay Permission Granted");
        sharedPrefsHelper.save(OVERLAY_PERMISSION_CHECKED_KEY, true);
        sharedPrefsHelper.save(MI_OVERLAY_PERMISSION_CHECKED_KEY, true);
        return true;
    }
    private void buildOverlayPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.overlayPermissionRequired));
        builder.setIcon(R.drawable.ic_error_outline_gray_24dp);
        builder.setMessage(getResources().getString(R.string.toReceiveCallsBackgroundPermission));
        builder.setCancelable(false);

       /* builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.longToast("You might miss calls while your application in background");
                sharedPrefsHelper.save(OVERLAY_PERMISSION_CHECKED_KEY, true);
                if (CheckNetwork.isInternetAvailable(LoginActivity.this)) //returns true if internet available
                {
                    hitUrlForCountryCodeApi(sharedPreference.getString(Config.LANGUAGE, "1"));
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });*/

        builder.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAndroidOverlayPermissionsSettings();
            }
        });

        AlertDialog alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void showAndroidOverlayPermissionsSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(LoginActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            Log.d("TAG", "Application Already has Overlay Permission");
        }
    }

    private void buildMIUIOverlayPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.additionalOverlayPermissionReq));
        builder.setIcon(R.drawable.ic_error_outline_orange_24dp);
        builder.setMessage(getResources().getString(R.string.pleaseMakeSureThatAllAdditionalPermissionGranted));
        builder.setCancelable(false);

        builder.setNeutralButton(getResources().getString(R.string.iamSure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedPrefsHelper.save(MI_OVERLAY_PERMISSION_CHECKED_KEY, true);
               /* if (CheckNetwork.isInternetAvailable(LoginActivity.this)) //returns true if internet available
                {
                    hitUrlForCountryCodeApi(sharedPreference.getString(Config.languageId,"1"));
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }*/
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.miSettings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMiUiPermissionsSettings();
            }
        });

        AlertDialog alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void showMiUiPermissionsSettings() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getPackageName());
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
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
   public void actionTerms(View v){
        try {
            String url = "http://www.myrehabcare.in/privacy-policy/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }catch (Exception e){}
   }
    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }
    public void showDataSecureDialog(View v){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.data_secure_dialog_lay);

        final Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
