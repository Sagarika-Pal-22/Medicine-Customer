package myrehabcare.in.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";


    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "FCM Registration Token: " + token);
    }
}