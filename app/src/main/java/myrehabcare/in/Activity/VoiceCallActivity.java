package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityVoiceCallBinding;

public class VoiceCallActivity extends AppCompatActivity {

    private ActivityVoiceCallBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);

    }
}