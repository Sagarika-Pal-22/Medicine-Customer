package myrehabcare.in.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityAppointmentConfirmedBinding;


public class AppointmentConfirmedActivity extends AppCompatActivity {

    private ActivityAppointmentConfirmedBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentConfirmedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mma");

        Intent intent = getIntent();
        //final HashMap<String, Object> p = (HashMap<String, Object>) intent.getSerializableExtra("a");

        String name = intent.getStringExtra("full_name");
        String date = intent.getStringExtra("date");
        Doctors doctors = (Doctors) getIntent().getSerializableExtra("doctors");

        binding.dateTv.setText(date);

        binding.nameTv.setText(name);
        binding.ageTv.setVisibility(View.GONE);
        //binding.ageTv.setText((String)p.get("patient_age")+" yrs, "+(String)p.get("patient_gender"));

        binding.viewDrProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity , DrProfileActivity.class).putExtra("doctors", doctors ));
            }
        });



    }
}