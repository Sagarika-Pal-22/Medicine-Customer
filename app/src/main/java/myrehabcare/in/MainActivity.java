package myrehabcare.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import myrehabcare.in.Activity.HomeActivity;
import myrehabcare.in.Activity.LoginActivity;
import myrehabcare.in.Callback.EaSaGetUser;
import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.JSB.JsbValidations;
import myrehabcare.in.databinding.ActivityMainBinding;

    public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Activity activity;
    Jsb jsb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        //216, 39,-770

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 154);
        }else {
            startt();
        }


    }


    private boolean hasPermissions(String[] permissions) {
        if (activity != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(PERMISSIONS)) {
            finish();
            Toast.makeText(activity, "Permissions required", Toast.LENGTH_SHORT).show();
        }else {
            startt();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startt() {
        if (jsb.getUser() != null){
            jsb.getUserDataFromServer(new JsbValidations.Email(jsb.getUser().getEmail()), new JsbValidations.Password(jsb.getUser().getPassword()), new EaSaGetUser() {
                @Override
                public void Error(String error) {
                    jsb.toastLong(error);
                }

                @Override
                public void Success(User user, String message) {
                    jsb.updateUser(user);
                    Log.e("tasting", "Success: "+ user.toString());
                    startActivity(new Intent(activity, HomeActivity.class));
                }
            });
        }else startActivity(new Intent(activity, LoginActivity.class));
    }
}