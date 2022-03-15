package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityCuntactUsBinding;

public class CuntactUsActivity extends AppCompatActivity {

    private ActivityCuntactUsBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCuntactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
    }
}