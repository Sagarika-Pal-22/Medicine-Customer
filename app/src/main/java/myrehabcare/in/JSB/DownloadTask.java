package myrehabcare.in.JSB;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import myrehabcare.in.MainActivity;
import myrehabcare.in.R;


public class DownloadTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private ProgressDialog mProgressDialog;
    private String extension,fileName;
    private PowerManager.WakeLock mWakeLock;
    public File f;
    public DownloadTaskInterface downloadTaskInterface;
    private int count=0;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    //  private SharedPreference sharedPreference;
    public DownloadTask(Context context, ProgressDialog mProgressDialog, String extension, String fileName, DownloadTaskInterface downloadTaskInterface) {
        this.context = context;
        this.mProgressDialog = mProgressDialog;
        this.extension = extension;
        this.fileName = fileName;
        this.downloadTaskInterface = downloadTaskInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
        //sendNotification(f,progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();

        if (result != null) {
            Log.d("fdvdfvd", result);
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "File Successfully Downloaded", Toast.LENGTH_SHORT).show();
            downloadTaskInterface.buttonPressed(f.getAbsolutePath());

        }
    }
    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
          /*  connection.setRequestMethod("GET");
            connection.setDoOutput(true);*/
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();

            }
            Log.d("fdvdfvd", connection.getResponseMessage());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
               /* f= new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "shubiiii.pdf" );
                f=new File(Environment.getDataDirectory(),"namePdfFile.pdf");*/
            File wallpaperDirectory = new File(
                    Environment.getExternalStorageDirectory() + "/MRC/Downloaded Prescription");
               /* File file = new File(
                        Environment.getExternalStorageDirectory() + "/YesGds/"+fileName+extension);*/
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if(extension.equals("")){
                f = new File(wallpaperDirectory, fileName);
            }else {
                f = new File(wallpaperDirectory, fileName +/* Calendar.getInstance()
                        .getTimeInMillis()+ */extension);
            }
            f.createNewFile();
            output = new FileOutputStream(f);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    public interface DownloadTaskInterface {
        void buttonPressed(String result);
    }
    private void updateProgressNotification(final int progress){
        //sharedPreference=SharedPreference.getInstance(context);
        try {
            final int progressMax = 100;
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    if(progress==progressMax){
                        mBuilder.setContentText("Download Finished")
                                .setProgress(0, 0, false)
                                .setOngoing(false)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        mNotifyManager.notify(count, mBuilder.build());
                        mNotifyManager.deleteNotificationChannel("Sesames");
                        Log.d("fdvdfvd", String.valueOf(count));
                    }
                }
            }).start();
        }catch (Exception e){}
    }

    private void sendNotification(File file, int progress) {
        final int progressMax=100;
        try {
            Intent intent = new Intent(context, MainActivity.class);

        /*    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//For Android Version Orio and greater than orio.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel("Sesames", "Sesames", importance);
                mChannel.setDescription("Video Downloading...");
                mChannel.setName(file.getName());
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                mNotifyManager.createNotificationChannel(mChannel);
            }
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

//For Android Version lower than oreo.
            mBuilder = new NotificationCompat.Builder(context, "Sesames");
            mBuilder.setContentText("File Downloading...")
                    .setContentTitle(file.getName())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.small_app_logo))
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setSound(defaultSoundUri)
                    .setColor(Color.parseColor("#FFD600"))
                    .setContentIntent(pendingIntent)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setChannelId("Sesames")
                    .setOnlyAlertOnce(true)
                    .setContentText(String.valueOf(progress+"%"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setProgress(progressMax,progress,false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mNotifyManager.notify(count, mBuilder.build());
            updateProgressNotification(progress);
            //count++;
        }catch (Exception e){}
    }
    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void sendNotification2(String path) {
        try {
            File file = new File(path);
            /*
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context,"com.tycho.myVu.provider", file);
            intent.setDataAndType(uri, "video/mp4");
            PackageManager pm = context.getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                context.startActivity(intent);
            }*/
          /*  Intent intent = new Intent(context,MainActivity.class);
            context.startActivity(intent);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//For Android Version Orio and greater than orio.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel("Sesame", "Sesame", importance);
                mChannel.setDescription(file.getName());
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                mNotifyManager.createNotificationChannel(mChannel);
            }
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

//For Android Version lower than oreo.
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Sesame");
            mBuilder.setContentTitle(file.getName())
                    .setContentText("Download Finished")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setColor(Color.parseColor("#FFD600"))
                    //.setContentIntent(pendingIntent)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setChannelId("Sesame")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            mNotifyManager.notify(count, mBuilder.build());
            count++;
        }catch (Exception e){}
    }
}