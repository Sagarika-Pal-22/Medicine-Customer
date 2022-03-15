package myrehabcare.in.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import myrehabcare.in.MainActivity;
import myrehabcare.in.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFire";

    private NotificationUtils notificationUtils;
    SharedPreference sharedPreference;
    private static int count = 0;
    Bitmap bitmap = null;

   /* @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPreference = SharedPreference.getInstance(this);
        Log.d("NEW_TOKEN", s);
       *//* Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        sharedPreference.putString(Config.TOKEN_NOTIFICATION,s);*//*
    }*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        sharedPreference = SharedPreference.getInstance(this);
        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getTitle());
            sendNotification( remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),
                    "",null);
          //  handleNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
               // handleDataMessage(json);
                sendNotification("Helloo", json.getString("message"),
                        "",null);
                Log.d(TAG,"Data Payload: "+ json.toString());
                Log.d(TAG,"Data Payload Message: "+ json.getString("message"));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    /*private void handleNotification(String title,String body) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            sendNotification(title,body);
            Log.d("INMyFiresadasdas","if");

        }else{
            Log.d("INMyFiresadasdas","if");

            sendNotification(title,body);
            // If the app is in background, firebase itself handles the notification
        }
    }*/

    private void handleDataMessage(JSONObject json) {
        try {
            /*JSONObject jsonObjectData = json.getJSONObject("data");
            JSONObject jsonObjectDataNew = jsonObjectData.getJSONObject("data");*/
            String title = json.getString("title");
            String body = json.getString("message");
            String image = json.getString("image");
            String timestamp = json.getString("timestamp");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

                new downloadImage(this, json)
                        .execute(title, body, image, timestamp);
                Log.d(TAG, "if");

            } else {
                Log.d(TAG, "else");
                new downloadImage(this, json)
                        .execute(title, body, image, timestamp);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void sendNotification(String title, String messageBody, String timeStamp, Bitmap bitmap) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("myNotChannel", "myNotChannel", importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotifyManager.createNotificationChannel(mChannel);
        }
     /*  RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.view_expanded_notification);
       expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
       expandedView.setTextViewText(R.id.notification_message, messageBody);*/
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.custom_push_notification);
        collapsedView.setTextViewText(R.id.title_cus_not, title);
        collapsedView.setTextViewText(R.id.text_cus_not, messageBody);
        if(bitmap==null){
            collapsedView.setTextViewText(R.id.time_custPushNotification,DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
            collapsedView.setImageViewResource(R.id.image_cus_not, R.drawable.ic_logo);
        }else{
            collapsedView.setTextViewText(R.id.time_custPushNotification,/* DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME)*/timeStamp);
            collapsedView.setImageViewBitmap(R.id.image_cus_not, bitmap);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myNotChannel")
                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.ic_logo)
                //.setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                // tapping notification will open MainActivity
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                // setting the custom collapsed and expanded views
                .setContent(collapsedView)
                // .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.notify(count, builder.build());
        count++;
    }

    private class downloadImage extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String title, message, timestamp;
        JSONObject jsonObject;

        public downloadImage(Context context, JSONObject jsonObject) {
            super();
            this.ctx = context;
            this.jsonObject = jsonObject;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            title = params[0];
            message = params[1];
            timestamp = params[3];
            try {

                in = new URL(params[2]).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
                sendNotification(title, message, timestamp, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}