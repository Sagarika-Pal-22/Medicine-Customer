package myrehabcare.in.QuickBlox.Service.fcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;

import java.io.IOException;
import java.util.Map;

import myrehabcare.in.QuickBlox.QuickBloxUtils.SharedPrefsHelper;
import myrehabcare.in.QuickBlox.Service.LoginService;
import myrehabcare.in.Service.Config;
import myrehabcare.in.Service.NotificationUtils;


public class PushListenerService extends QBFcmPushListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        Log.d("INMyFiresadasdas","Message Recived");
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            if (sharedPrefsHelper.hasQbUser()) {
                QBUser qbUser = sharedPrefsHelper.getQbUser();
                Log.d(TAG, "App has logged user" + qbUser.getId());
                LoginService.start(this, qbUser);
            }
            Log.d("INMyFiresadasdas","if");

        }else {
            if (sharedPrefsHelper.hasQbUser()) {
                QBUser qbUser = sharedPrefsHelper.getQbUser();
                Log.d(TAG, "App has logged user" + qbUser.getId());
                LoginService.start(this, qbUser);
                try {
                    Config.notificationAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                    //audioManager.setMode(AudioManager.MODE_IN_CALL);
                    Config.notificationAudioManager.setSpeakerphoneOn(true);
                    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    try {
                        Config.notificationMediaPlayer = new MediaPlayer();
                        Config.notificationMediaPlayer.setDataSource(this, alert);
                    } catch (IOException e) {
                    }
                    Config.notificationMediaPlayer.setLooping(true);
                    Config.notificationMediaPlayer.prepare();
                    Config.notificationMediaPlayer.start();
                  Vibrator  vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] vibrationCycle = {0, 1000, 1000};
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(vibrationCycle, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("INMyFiresadasdas","else");
        }
    }
    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        boolean tokenRefreshed = true;
        SubscribeService.subscribeToPushes(this, tokenRefreshed);
    }
}