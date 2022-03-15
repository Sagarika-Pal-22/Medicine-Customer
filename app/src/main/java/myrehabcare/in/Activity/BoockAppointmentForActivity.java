package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.google.android.material.shape.CornerFamily;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityBoockAppointmentForBinding;

public class BoockAppointmentForActivity extends AppCompatActivity {

    private ActivityBoockAppointmentForBinding binding;
    private Activity activity;
    private Jsb jsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoockAppointmentForBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        jsb = new Jsb(activity);


        float radius = 50;
        binding.card.setShapeAppearanceModel(binding.card.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                .build());

        binding.card2.setShapeAppearanceModel(binding.card2.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                .build());

    }



}