package myrehabcare.in.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import myrehabcare.in.Classes.Doctors;
import myrehabcare.in.JSB.CheckMultiplePermission;
import myrehabcare.in.JSB.DownloadTask;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityDrFeedbackBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrFeedbackActivity extends AppCompatActivity implements DownloadTask.DownloadTaskInterface{
    CheckMultiplePermission checkMultiplePermission;
    DownloadTask downloadTask;
    ProgressDialog mProgressDialog;
    private ActivityDrFeedbackBinding binding;
    private Activity activity;
    private Jsb jsb;
    private Doctors doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);


        doctors = (Doctors) getIntent().getSerializableExtra("doctors");

        Glide.with(activity).load(doctors.getImage()).placeholder(R.drawable.ic_default_user).into(binding.drProfileImage);


        binding.drName.setText(doctors.getName());
        binding.drType.setText(doctors.getType().split(":")[0]);

        binding.drRating.setRating(doctors.getRating());




        binding.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = jsb.getProgressDialog();
                progressDialog.show();

                float rating = binding.giveRating.getRating();
                if (rating < 0.1){
                    Toast.makeText(activity, "Please give rating", Toast.LENGTH_SHORT).show();
                }else {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("doctor_id", doctors.getId())
                            .add("reviewer_id", jsb.getUser().getUser_id())
                            .add("comments", binding.revEt.getText().toString())
                            .add("rate", rating+"")
                            .build();

                    jsb.post(getString(R.string.baseUrl) + "api/write_review", requestBody, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    jsb.toastLong(e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()){
                                String body = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    String message = jsonObject.getString("message");
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            jsb.toastLong(message);
                                            binding.giveRating.setIsIndicator(true);
                                            binding.revEt.setKeyListener(null);
                                            binding.submitBt.setEnabled(false);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            jsb.toastLong(e.getMessage());
                                        }
                                    });
                                    finish();
                                }

                            }else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        jsb.toastLong(response.message());
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });
       binding.btnDownloadPrescription.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startDownloading(doctors.getUploadPrescription());
           }
       });

       if(doctors.getUploadPrescription().equals("")){
           binding.btnDownloadPrescription.setVisibility(View.GONE);
       }else{
           binding.btnDownloadPrescription.setVisibility(View.VISIBLE);
       }
    }
    private void startDownloading(String url) {
        /*  val request = DownloadManager.Request(Uri.parse(url))
          request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
          request.setTitle("Download")
          request.setDescription("The file is downloading...")
          request.allowScanningByMediaScanner()
          request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
          request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}")

          val manager = mContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
          manager.enqueue(request)
  */
        /*  if (checkMultiplePermission!!.checkAndRequestPermissions()) {
              checkMultiplePermission!!.checkAndRequestPermissions()
              Log.d("msg", "if")
          } else {*/
        mProgressDialog =new  ProgressDialog(DrFeedbackActivity.this);
        mProgressDialog.setMessage("Downloading Prescription...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        downloadTask =new DownloadTask(
                DrFeedbackActivity.this,
                mProgressDialog,
                ".png",
                String.valueOf(System.currentTimeMillis()),
                DrFeedbackActivity.this
        );
        downloadTask.execute(url);
        Log.d("msg", "else");
        //    }
    }
    @Override
    public void buttonPressed(String result) {
        Snackbar.make(binding.nestedScroll, "File Download Successfully", Snackbar.LENGTH_LONG)
                .setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File file = new File(result);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri = FileProvider.getUriForFile(DrFeedbackActivity.this,"myrehabcare.in.provider", file);
                        intent.setDataAndType(uri, "image/*");
                        PackageManager pm = getPackageManager();
                        if (intent.resolveActivity(pm) != null) {
                            startActivity(intent);
                        }
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }

}