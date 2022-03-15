package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityDisclaimerBinding;

public class DisclaimerActivity extends AppCompatActivity {

    private ActivityDisclaimerBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisclaimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

        binding.wenview.loadUrl("file:///android_asset/disclaimer.html");

    }
}