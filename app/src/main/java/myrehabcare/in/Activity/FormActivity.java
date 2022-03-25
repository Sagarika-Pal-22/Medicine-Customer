package myrehabcare.in.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityFormBinding;
import myrehabcare.in.databinding.DialogCouponCodeBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity implements PaymentResultListener {

    private ActivityFormBinding binding;
    private Activity activity;
    private Jsb jsb;
    private ProgressDialog progressDialog;
    private String service_type, visit_type, category;
    private String name, age, gender, problem, schedule_date,schedule_time, address;
    private String fees = "600"/*"1"*/;
    int LAUNCH_SECOND_ACTIVITY = 1;
    private String discount = "0";
    private int GALLERY = 111,CAMERA=222;
    Bitmap bitmap;
    Uri image_uri;
    private String prescriptionImagePath;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        progressDialog = jsb.getProgressDialog();

        Checkout.preload(getApplicationContext());
        service_type = getIntent().getStringExtra("service_type");
        category = getIntent().getStringExtra("category");

        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setActiv();
            }
        });

        binding.problemEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setActiv();
            }
        });

        binding.ageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setActiv();
            }
        });

        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setActiv();
            }
        });

        binding.genderEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setActiv();
            }
        });

        binding.addressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setActiv();
            }
        });


        binding.ImmediateVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = binding.nameEt.getText().toString();
                age = binding.ageEt.getText().toString();
                gender = binding.genderEt.getText().toString().toLowerCase();
                problem = binding.problemEt.getText().toString();
                address = binding.addressEt.getText().toString();
                schedule_date = "";

                visit_type = "1";
//                startPayment();
                startActivity(new Intent(activity, LoaderActivity.class).putExtra("service_type", service_type).putExtra("category", category).putExtra("name", name).putExtra("age", age).putExtra("gender", gender).putExtra("problem", problem).putExtra("visit_type", visit_type).putExtra("schedule_date", schedule_date).putExtra("schedule_time", schedule_time).putExtra("fees", fees).putExtra("discount", discount).putExtra("address", address).putExtra("prescriptionImagePath",prescriptionImagePath));

            }
        });


        binding.ScheduleVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = binding.nameEt.getText().toString();
                age = binding.ageEt.getText().toString();
                gender = binding.genderEt.getText().toString().toLowerCase();
                problem = binding.problemEt.getText().toString();
                address = binding.addressEt.getText().toString();
                visit_type = "2";

                Intent i = new Intent(activity, ScheduleActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });

        binding.uploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMultiplePermissions();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                long result = data.getLongExtra("date", new Date().getTime());
                String time = data.getStringExtra("time");
                Date d = new Date(result);
                final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
                Toast.makeText(activity, time, Toast.LENGTH_SHORT).show();

                schedule_date = dateFormat.format(d);
                schedule_time = time;
                startPayment();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }


        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    binding.im1.setImageBitmap(bitmap);
                    prescriptionImagePath = getRealPathFromURI(contentURI);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if(requestCode == CAMERA){
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                binding.im1.setImageBitmap(bitmap);
                prescriptionImagePath = getRealPathFromURI(image_uri);
            }catch(Exception e){}
        }
    }//onActivityResult
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void setActiv() {
        if (!binding.ageEt.getText().toString().isEmpty() && !binding.addressEt.getText().toString().isEmpty() && !binding.genderEt.getText().toString().isEmpty() && !binding.nameEt.getText().toString().isEmpty() && binding.problemEt.getText().length() >= 5 && binding.checkbox.isChecked()) {
            binding.ImmediateVisit.setBackground(getResources().getDrawable(R.drawable.bt_bg2));
            binding.ScheduleVisit.setBackground(getResources().getDrawable(R.drawable.bt_bg2_red));
            binding.ScheduleVisit.setEnabled(true);
            binding.ImmediateVisit.setEnabled(true);
        } else {
            binding.ImmediateVisit.setBackground(getResources().getDrawable(R.drawable.bt_bg2_light));
            binding.ScheduleVisit.setBackground(getResources().getDrawable(R.drawable.bt_bg2_red_light));
            binding.ScheduleVisit.setEnabled(false);
            binding.ImmediateVisit.setEnabled(false);
        }
    }


    public void startPayment() {

        DialogCouponCodeBinding couponCodeBinding = DialogCouponCodeBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(couponCodeBinding.getRoot());

        couponCodeBinding.closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getOrderId(Integer.parseInt(fees));
               // startActivity(new Intent(activity, LoaderActivity.class).putExtra("service_type", service_type).putExtra("category", category).putExtra("name", name).putExtra("age", age).putExtra("gender", gender).putExtra("problem", problem).putExtra("visit_type", visit_type).putExtra("schedule_date", schedule_date).putExtra("schedule_time", schedule_time).putExtra("fees", fees).putExtra("discount", discount).putExtra("address", address).putExtra("prescriptionImagePath",prescriptionImagePath));
            }
        });


        couponCodeBinding.couponCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    couponCodeBinding.messaageTv.setText("Apply discount code");
                    couponCodeBinding.messaageTv.setTextColor(Color.rgb(0, 153, 255));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        couponCodeBinding.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = couponCodeBinding.couponCodeEt.getText().toString();
                if (code.isEmpty()) {
                    jsb.toastLong("Please enter code");
                    couponCodeBinding.couponCodeEt.setError("Please enter code");
                } else {
                    progressDialog.show();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id", jsb.getUser().getUser_id())
                            .add("coupon_code", code)
                            .build();

                    jsb.post(getString(R.string.baseUrl) + "api/apply_couponcode", requestBody, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    e.printStackTrace();
                                    jsb.toastLong(e.getMessage());

                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.isSuccessful()) {
                                        try {
                                            String body = response.body().string();

                                            JSONObject jsonObject = new JSONObject(body);

                                            couponCodeBinding.messaageTv.setText(jsonObject.getString("message"));
                                            progressDialog.dismiss();
                                            if (jsonObject.has("isValidCode")) {
                                                if (jsonObject.getBoolean("isValidCode")) {
                                                    couponCodeBinding.messaageTv.setTextColor(Color.rgb(103, 224, 117));

                                                    int dd = Integer.parseInt(fees);
                                                    String off = jsonObject.getString("off");
                                                    int of = Integer.parseInt(off.replaceAll("[\\D]", ""));
                                                    int o;
                                                    if (off.contains("%")) {
                                                        o = dd - ((dd / 100) * of);
                                                    } else {
                                                        o = dd - of;
                                                    }

                                                 Log.d("tastingg1",off);
                                                 Log.d("tastingg2", String.valueOf(of));
                                                 Log.d("tastingg3", String.valueOf(o));
                                                    couponCodeBinding.submitBt.setText("Ok");
                                                    couponCodeBinding.submitBt.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                            getOrderId(o);
                                                        }
                                                    });

                                                    return;
                                                }
                                            }

                                            couponCodeBinding.messaageTv.setTextColor(Color.rgb(184, 35, 62));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            jsb.toastLong(e.getMessage());
                                            progressDialog.dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            jsb.toastLong(e.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        jsb.toastLong(response.message());
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        dialog.show();


    }
    //start checkout with razor pay payment setup....
    public void startPayment(String orderId,int totalPrice) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setKeyID("rzp_live_binIcbIEayWnM8");
        //checkout.setKeyID("rzp_test_qB6SWBnLEZrCe7");

        int amount2 = totalPrice*100;
        String finalPrice = String.valueOf(amount2);

        checkout.setImage(R.drawable.ic_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", getString(R.string.app_name));
            options.put("description", "Order Id. #" +orderId);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderId);
            options.put("theme.color", "#800099FF");
            options.put("currency", "INR");
            options.put("prefill.email", jsb.getUser().getEmail());
            options.put("prefill.contact", jsb.getUser().getPhone());
            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount",finalPrice);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("FormAct", "Error in starting Razorpay Checkout", e);
        }
         progressDialog.dismiss();
    }
    OkHttpClient client = new OkHttpClient();

   /* private void startPayment(int amount) {
        progressDialog.show();
        Checkout checkout = new Checkout();
        //checkout.setKeyID("rzp_live_binIcbIEayWnM8");
        checkout.setKeyID("rzp_test_qB6SWBnLEZrCe7");
        //rzp_test_qB6SWBnLEZrCe7
        int amount2 = amount * 100;
        String aa = String.valueOf(amount2);


        try {
            //JSONObject jsonObject = new JSONObject(body);


            checkout.setImage(R.drawable.ic_logo);

            //String order_id = jsonObject.getString("id");


            JSONObject options = new JSONObject();

            options.put("name", getString(R.string.app_name));
            options.put("description", "Order Id. #" );
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", order_id);
            options.put("theme.color", "#800099FF");
            options.put("currency", "INR");
            options.put("amount", "100");//pass amount in currency subunits
            options.put("prefill.email", jsb.getUser().getEmail());
            options.put("prefill.contact", jsb.getUser().getPhone());
            checkout.open(activity, options);
            return;

        } catch (JSONException e) {
            progressDialog.dismiss();
            jsb.toastLong(e.getMessage());
            e.printStackTrace();
            finish();

        }




        // https://api.razorpay.com/v1/orders?amount=50000&currency=INR&receipt=order_rcptid_11




        *//*checkout.setImage(R.drawable.ic_logo);

        String order_id = jsb.random(10, true);

        try {
            JSONObject options = new JSONObject();

            options.put("name", getString(R.string.app_name));
            options.put("description", "Order Id. #" + order_id);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", order_id);
            options.put("theme.color", "#800099FF");
            options.put("currency", "INR");
            options.put("amount", aa);//pass amount in currency subunits
            options.put("prefill.email", jsb.getUser().getEmail());
            options.put("prefill.contact", jsb.getUser().getPhone());
            checkout.open(activity, options);
        } catch (Exception e) {
            progressDialog.dismiss();
            jsb.toastLong("Something went wrong");
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }*//*
    }*/

  private void getOrderId(int amount){
      progressDialog.show();
      RequestBody requestBody = new FormBody.Builder()
              .add("amount", String.valueOf(amount*100))
              .add("currency", "INR")
              .add("receipt", "order_rcptid_11")
              .build();

      Request request = new Request.Builder()
              .url("https://api.razorpay.com/v1/orders")
              .post(requestBody)
              .addHeader("Authorization", "Basic cnpwX2xpdmVfYmluSWNiSUVheVduTTg6OWxWZ2djWnhwRTR3a2g4cHIyekNDRHFy")
              .build();
      Call call = client.newCall(request);
      call.enqueue(new Callback() {
          @Override
          public void onFailure(@NotNull Call call, @NotNull IOException e) {
              e.printStackTrace();
                      jsb.toastLong(e.getMessage());
                      progressDialog.dismiss();
                      Log.e("tastinggFail", "run1: " + e.getMessage());
          }

          @Override
          public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
              if (response.isSuccessful()) {
                  String body = response.body().string();
                  Log.d("tastinggOrderId" , response.toString());
                  try {
                              JSONObject jsonObject = new JSONObject(body);
                              String order_id = jsonObject.getString("id");
                              startPayment(order_id,amount);
                              Log.d("tastinggSuc" , jsonObject.toString());
                              Log.d("tastinggOrderId" , order_id);
                          } catch (JSONException e) {
                              progressDialog.dismiss();
                              jsb.toastLong(e.getMessage());
                              e.printStackTrace();
                          }
              } else {
                  Log.d("tastinggError" , "Fail Else");
                  jsb.toastLong(response.message());
                          progressDialog.dismiss();
              }
          }
      });

  }
    @Override
    public void onPaymentSuccess(String s) {

        //jsb.toastLong(s);

        progressDialog.dismiss();
        Log.e(activity.getPackageName(), s);
        jsb.toastLong("Payment Successful");
        startActivity(new Intent(activity, LoaderActivity.class).putExtra("service_type", service_type).putExtra("category", category).putExtra("name", name).putExtra("age", age).putExtra("gender", gender).putExtra("problem", problem).putExtra("visit_type", visit_type).putExtra("schedule_date", schedule_date).putExtra("schedule_time", schedule_time).putExtra("fees", fees).putExtra("discount", discount).putExtra("address", address).putExtra("prescriptionImagePath",prescriptionImagePath));
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(FormActivity.this,s,Toast.LENGTH_LONG).show();
    }


    private void error(final String error) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsb.toastShort(error);
                progressDialog.dismiss();
            }
        });
    }
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                            selectImage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        //requestMultiplePermissions();
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            int hasPerm1 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED && hasPerm1 == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            captureImage();
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            pickImageGallery();
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }// else
            //requestMultiplePermissions();
            //  Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void captureImage(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent =new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, CAMERA);
    }
    private void pickImageGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }
    public byte[] convertBitmapToByteArray(Context context, Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

}