package myrehabcare.in.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.databinding.ActivityScheduleBinding;

public class ScheduleActivity extends AppCompatActivity {

    private ActivityScheduleBinding binding;
    private Activity activity;
    private Jsb jsb;
    private Date date;
    String timeStr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        date = new Date();
        binding.calendarView.setMinDate(new Date().getTime());

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth);

                date = calendar.getTime();

            }
        });

        binding.nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("date",date.getTime());
                returnIntent.putExtra("time",timeStr);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
      //  binding.time.setKeyListener(null);
        binding.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final String time = selectedHour+":"+selectedMinute;
                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                            final Date dateObj = sdf.parse(time);
                            binding.time.setText(new SimpleDateFormat("K:mm a").format(dateObj));
                            timeStr = new SimpleDateFormat("HH:mm:ss").format(dateObj);
                            Log.d("sdsdfdsf",timeStr);
                        } catch (final ParseException e) {
                            e.printStackTrace();
                            jsb.toastShort("Something went wrong");
                        }

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });
    }
}