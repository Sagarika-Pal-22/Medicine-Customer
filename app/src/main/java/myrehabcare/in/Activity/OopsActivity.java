package myrehabcare.in.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import myrehabcare.in.R;

public class OopsActivity extends AppCompatActivity {
    TextView tv_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oops);
        tv_home = findViewById(R.id.tv_home);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OopsActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}