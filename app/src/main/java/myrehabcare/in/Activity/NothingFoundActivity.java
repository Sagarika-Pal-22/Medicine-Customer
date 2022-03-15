package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityNothingFoundBinding;

public class NothingFoundActivity extends AppCompatActivity {

    private ActivityNothingFoundBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNothingFoundBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

    }
}