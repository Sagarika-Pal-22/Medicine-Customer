package myrehabcare.in.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import myrehabcare.in.Classes.Details;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.QuickBlox.Activity.CallActivity;
import myrehabcare.in.QuickBlox.Activity.PermissionsActivity;
import myrehabcare.in.QuickBlox.QuickBloxUtils.CollectionsUtils;
import myrehabcare.in.QuickBlox.QuickBloxUtils.Consts;
import myrehabcare.in.QuickBlox.QuickBloxUtils.PermissionsChecker;
import myrehabcare.in.QuickBlox.QuickBloxUtils.PushNotificationSender;
import myrehabcare.in.QuickBlox.QuickBloxUtils.SharedPrefsHelper;
import myrehabcare.in.QuickBlox.QuickBloxUtils.ToastUtils;
import myrehabcare.in.QuickBlox.QuickBloxUtils.WebRtcSessionManager;
import myrehabcare.in.QuickBlox.Service.CallService;
import myrehabcare.in.QuickBlox.Service.LoginService;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityHomeBinding;
import myrehabcare.in.databinding.DialogLocationBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends BaseActivity{

    private ActivityHomeBinding binding;
    private Activity activity;
    private Jsb jsb;
    private ProgressDialog progressDialog;
    private boolean abbm = false;
    private FusedLocationProviderClient client;
    private boolean djldls = false;
    private ProgressDialog mSpinner;
    private SharedPrefsHelper sharedPrefsHelper;
    private static final String OVERLAY_PERMISSION_CHECKED_KEY = "overlay_checked";
    private static final String MI_OVERLAY_PERMISSION_CHECKED_KEY = "mi_overlay_checked";
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1764;
    private PermissionsChecker checker;
    private QBUser currentUser;
    List<QBUser> userList = new ArrayList<>();
    List<QBUser> selectedUsers = new ArrayList<>();
    private static final String ORDER_RULE = "order";
    private static final String ORDER_DESC_UPDATED = "desc date updated_at";
   /* private static ViewPager mPager;
    private CirclePageIndicator indicator;*/
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 4000; // time in milliseconds between successive task executions.
    private ArrayList<String> arrayListImageSlider = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        checker = new PermissionsChecker(getApplicationContext());
        currentUser = SharedPrefsHelper.getInstance(this).getQbUser();
        progressDialog = jsb.getProgressDialog();
        client = LocationServices.getFusedLocationProviderClient(activity);

        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

       /* jsb.post("http://mrcadmin.in/api/sliders", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jsb.toastShort("Something went wrong");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();

                    try {
                        final JSONObject jsonObject = new JSONObject(body);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });*/
        hitUrlForSliderImage();
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == arrayListImageSlider.size()) {
                    currentPage = 0;
                }
                binding.pagerSlidder.setCurrentItem(currentPage++, false);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        binding.pagerSlidder.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
               /* mPager.setCurrentItem(currentPage++, true);
                timer = new Timer(); // This will create a new Thread
                timer.schedule(new TimerTask() { // task to be scheduled
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY_MS, PERIOD_MS);*/
                Log.d("dfdsfdsfds","onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                //Log.d("dfdsfdsfds","onPageSelected");

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //  Log.d("dfdsfdsfds","onPageScrollStateChanged");

            }
        });
        address = jsb.getPreferences().getString("address", null);
//        if (address == null) {
            location(binding.address);
  //      }


        binding.OnlineConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LookingForActivity.class).putExtra("service_type", "1"));
            }
        });

        binding.ClinicVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LookingForActivity.class).putExtra("service_type", "3"));
            }
        });

        binding.HomeVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LookingForActivity.class).putExtra("service_type", "2"));
            }
        });


        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        }
        if (checkOverlayPermissions()) {
            if (sharedPrefsHelper.hasQbUser()) {
                LoginService.start(HomeActivity.this, sharedPrefsHelper.getQbUser());
            }
        }
        /*binding.audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (checkIsLoggedInChat()) {
                        startCall(false,"17");
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                        startPermissionsActivity(true);
                    }
                }catch (Exception e){}

            }
        });
        binding.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (checkIsLoggedInChat()) {
                        startCall(true,"17");
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                        startPermissionsActivity(false);
                    }
                }catch (Exception e){}
            }
        });*/
        boolean isIncomingCall = SharedPrefsHelper.getInstance(this).get(Consts.EXTRA_IS_INCOMING_CALL, false);
        if (isCallServiceRunning(CallService.class)) {
            Log.d("TAG", "CallService is running now");
            CallActivity.start(this, isIncomingCall);
        }
        clearAppNotifications();
        loadUsers();
    }

    private void hitUrlForSliderImage(){
        binding.pbProgressSlider.setVisibility(View.VISIBLE);
       // String urlString ="http://mrcadmin.in/api/sliders";
        String urlString = getResources().getString(R.string.baseUrl)+"api/sliders";
        Log.d("dssdfds",urlString);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,urlString, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ResponseSearchPager",response);
                try {
                    JSONObject jsonObject  = new JSONObject(response);
                    arrayListImageSlider.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    //   final String image = jsonArray.getJSONObject(0).getString("slider_image").replace("BaseURL", getString(R.string.baseUrl));

                   /*     activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(getApplicationContext()).load(image).into(binding.imageBanner);
                            }
                        });*/
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        arrayListImageSlider.add(jsonObject1.getString("slider_image"));
                    }
                    binding.pagerSlidder.setAdapter(new MySliderAdapter(HomeActivity.this,arrayListImageSlider));
                    binding.indicator.setViewPager(binding.pagerSlidder);
                    final float density = getResources().getDisplayMetrics().density;
                    //Set circle indicator radius
                    binding.indicator.setRadius(5 * density);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.pbProgressSlider.setVisibility(View.GONE);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.pbProgressSlider.setVisibility(View.GONE);
            }
        })
        {};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean isCallServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void startCall(boolean isVideoCall,String opponentUserIds) {
       /* Log.d(TAG, "Starting Call");
        if (usersAdapter.getSelectedUsers().size() > Consts.MAX_OPPONENTS_COUNT) {
            ToastUtils.longToast(String.format(getString(R.string.error_max_opponents_count),
                    Consts.MAX_OPPONENTS_COUNT));
            return;
        }
*/
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        //Log.d(TAG, "conferenceType = " + conferenceType);
        ArrayList<Integer> opponentsList = CollectionsUtils.getIdsSelectedOpponents(getSelectedUsers(opponentUserIds));
        Log.d("OponentList",opponentsList.toString());
        Log.d("OponentListIDMAIN",opponentUserIds);
        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        // Make Users FullName Strings and ID's list for iOS VOIP push
        String newSessionID = newQbRtcSession.getSessionID();
        ArrayList<String> opponentsIDsList = new ArrayList<>();
        ArrayList<String> opponentsNamesList = new ArrayList<>();
        List<QBUser> usersInCall =getSelectedUsers(opponentUserIds);

        // the Caller in exactly first position is needed regarding to iOS 13 functionality
        usersInCall.add(0, currentUser);
        Log.d("CurrentList",currentUser.toString());
        Log.d("FinalList",usersInCall.toString());
        for (QBUser user : usersInCall) {
            String userId = user.getId().toString();
            String userName = "";
            if (TextUtils.isEmpty(user.getFullName())) {
                userName = user.getLogin();
            } else {
                userName = user.getFullName();
            }

            opponentsIDsList.add(userId);
            opponentsNamesList.add(userName);
        }

        String opponentsIDsString = TextUtils.join(",", opponentsIDsList);
        String opponentNamesString = TextUtils.join(",", opponentsNamesList);

        Log.d("FINAL", "New Session with ID: " + newSessionID + "\n Users in Call: " + "\n" + opponentsIDsString + "\n" + opponentNamesString);
        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName(), newSessionID, opponentsIDsString, opponentNamesString, isVideoCall);
        CallActivity.start(this, false);
    }
    private void clearAppNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private boolean checkIsLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            startLoginService();
            ToastUtils.shortToast(R.string.dlg_relogin_wait);
            return false;
        }
        return true;
    }

    private void startLoginService() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            LoginService.start(this, qbUser);
        }
    }

    private void loadUsers() {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_DESC_UPDATED));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);

        QBUsers.getUsers(qbPagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.d("LOAD_USER", "Successfully loaded users");
                userList.clear();
                addUsers(qbUsers);
                Log.d("LOAD_USER",  qbUsers.toString());
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("TAG", "Error load users" + e.getMessage());
            }
        });
    }
    public void addUsers(List<QBUser> users) {
        for (QBUser user : users) {
            if (!userList.contains(user)) {
                userList.add(user);
            }
        }
    }
    public List<QBUser> getSelectedUsers(String oponentUserId) {
        selectedUsers.clear();
        for(int i=0;i<userList.size();i++){
            if(oponentUserId.equals(userList.get(i).getLogin())){
                selectedUsers.add(userList.get(i));
            }
        }
        return selectedUsers;
    }

    @Override
    protected void onPause() {
/*        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        mSpinner.dismiss();*/
        super.onPause();
    }




    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int resultLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED &&
                resultLocation == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION},
                11);
    }


    public void logout(View view) {
        jsb.Logout();
    }

    public void my_profile(View view) {
        startActivity(new Intent(activity, MyProfileActivity.class));
    }

    public void my_transaction(View view) {
        startActivity(new Intent(activity, MyTransactionActivity.class));
    }

    public void contact_us(View view) {
        startActivity(new Intent(activity, CuntactUsActivity.class));
        //startActivity(new Intent(activity,BoockAppointmentForActivity.class));
    }

    public void my_doctors(View view) {
        startActivity(new Intent(activity, MyDoctorsActivity.class));
    }

    public void my_appointments(View view) {
        startActivity(new Intent(activity, MyAppointmentsActivity.class));
    }

    public void about_us(View view) {
        //startActivity(new Intent(activity, AbotUsActivity.class));
        String url = "http://www.myrehabcare.in/";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

       /* startActivity(new Intent(activity, DrProfileActivity.class));
        startActivity(new Intent(activity, DrFeedbackActivity.class));
        startActivity(new Intent(activity, NothingFoundActivity.class));
*/
        //startActivity(new Intent(activity, ScheduleActivity.class));

    }

    public void view_all(View view) {
        startActivity(new Intent(activity, LookingForActivity.class).putExtra("service_type", "2"));
    }

    public void Doctorssss(View view) {
        startActivity(new Intent(activity, FormActivity.class).putExtra("value", "Doctor").putExtra("category", "25").putExtra("service_type", "2"));
    }

    public void nuressssss(View view) {
        startActivity(new Intent(activity, FormActivity.class).putExtra("value", "Nurses").putExtra("category", "30").putExtra("service_type", "2"));
    }

    public void labbb(View view) {
        startActivity(new Intent(activity, FormActivity.class).putExtra("value", "Physiotherapist").putExtra("category", "26").putExtra("service_type", "2"));
    }

    public void my_product(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://d-myrehabcare.dotpe.in"));
        startActivity(browserIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (abbm) {
            progressDialog.dismiss();
            abbm = false;
        }

        if (jsb.getPreferences().getString("address", null) != null) {
            address = jsb.getPreferences().getString("address", null);
            binding.address.setText(address);
        }

        if (djldls) {

            LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Task<Location> task = client.getLastLocation();

                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(activity, Locale.getDefault());
                            String address;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();

                                //setDialog(address);
                                binding.address.setText(address);
                                onSendLatLngApi(address,new LatLng(location.getLatitude(), location.getLongitude()));
                            } catch (IOException e) {
                                e.printStackTrace();
                                address = "Error";
                                setDialog(address);
                            }
                        }else {
                            address = "Error";
                            setDialog(address);
                        }
                    }
                });

            }

            djldls = false;
        }

    }

    @Override
    public void onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();
        }
    }
    String address;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1111){
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                location(binding.address);
            }else {
                finish();
            }
        }else {
            finish();
        }
    }

    private void onSendLatLngApi(String addresss, LatLng mLatLong){
        if (mLatLong != null){

            //progressDialog.show();

            RequestBody requestBody = new FormBody.Builder()
                    .add("doctor_id", jsb.getUser().getUser_id())
                    .add("latitude", String.valueOf(mLatLong.latitude))
                    .add("longitude", String.valueOf(mLatLong.longitude))
                    .build();

            jsb.getPreferences().edit().putString("address", addresss).apply();

            jsb.post(getString(R.string.baseUrl) + "api/customer_location", requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        //finish();
                        Log.d("dddfdfdfdf",response.toString());
                        progressDialog.dismiss();
                    }else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    private void setDialog(String address){
        binding.address.setText(address);
        DialogLocationBinding dialogLocationBinding = DialogLocationBinding.inflate(getLayoutInflater());
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(dialogLocationBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();
        dialogLocationBinding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialogLocationBinding.addresss.setText(address);
        dialogLocationBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                location(v);
            }
        });


        dialogLocationBinding.changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressDialog.show();
                abbm = true;
                startActivityForResult(new Intent(activity , LocationActivity.class), 111);
            }
        });
    }

    private boolean isLocationEnabled() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(gps_enabled && network_enabled) {
                return true;
            }
            return false;

        } else {
            int mode = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }


    public void location(View view) {
        address = jsb.getPreferences().getString("address", null);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           // if (address == null){

                //LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
                //locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isLocationEnabled()) {
                    Task<Location> task = client.getLastLocation();

                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(activity, Locale.getDefault());
                                String address;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();

                               //     setDialog(address);
                                    binding.address.setText(address);
                                    onSendLatLngApi(address,new LatLng(location.getLatitude(), location.getLongitude()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    address = "Error: "+e.getMessage();
                                    setDialog(address);
                                }
                            }else {

                                address = "Error: Location is null";
                                setDialog(address);
                            }
                        }
                    });

                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                    // Setting DialogHelp Title
                    alertDialog.setTitle("GPS is settings");


                    // Setting DialogHelp Message
                    alertDialog
                            .setMessage("GPS is not enabled. Do you want to go to settings menu?");

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    djldls = true;
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivity(intent);
                                }
                            });

                    // on pressing cancel button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });

                    // Showing Alert Message
                    alertDialog.show();
                }


/*
            }else {
                setDialog(address);
            }*/
        }else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1111);
        }


    }

    public void location2(View view) {
        address = jsb.getPreferences().getString("address", null);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (address == null){

                //LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
                //locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isLocationEnabled()) {
                    Task<Location> task = client.getLastLocation();

                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(activity, Locale.getDefault());
                                String address;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();

                                    setDialog(address);
                                    onSendLatLngApi(address,new LatLng(location.getLatitude(), location.getLongitude()));
                                    //  binding.address.setText(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    address = "Error: "+e.getMessage();
                                    setDialog(address);
                                }
                            }else {
                                address = "Error: Location is null";
                                setDialog(address);
                            }
                        }
                    });

                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                    // Setting DialogHelp Title
                    alertDialog.setTitle("GPS is settings");


                    // Setting DialogHelp Message
                    alertDialog
                            .setMessage("GPS is not enabled. Do you want to go to settings menu?");

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    djldls = true;
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivity(intent);
                                }
                            });

                    // on pressing cancel button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });

                    // Showing Alert Message
                    alertDialog.show();
                }



            }else {
                setDialog(address);
            }
        }else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1111);
        }


    }

    public void fav(View view) {
        startActivity(new Intent(activity, FavActivity.class));
    }

    public void PayNow(View view) {
        String url = "https://www.paypal.me/MYREHABACADEMY";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void Chat(View view) {
        String url = "http://wa.me/+919066990059";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
    public void MrcRehabGym(View view) {
        String url = "https://www.easymapmaker.com/map/f5fa680d5cd01fe77faea0250ca79ad1";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void Disclaimer(View view) {
        startActivity(new Intent(activity, DisclaimerActivity.class));
    }

    private boolean checkOverlayPermissions() {
        Log.e("TAG", "Checking Permissions");
        boolean overlayChecked = sharedPrefsHelper.get(OVERLAY_PERMISSION_CHECKED_KEY, false);
        boolean miOverlayChecked = sharedPrefsHelper.get(MI_OVERLAY_PERMISSION_CHECKED_KEY, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this) && !overlayChecked) {
                Log.e("TAG", "Android Overlay Permission NOT Granted");
                buildOverlayPermissionAlertDialog();
                return false;
            } else if (PermissionsChecker.isMiUi() && !miOverlayChecked) {
                Log.e("TAG", "Xiaomi Device. Need additional Overlay Permissions");
                buildMIUIOverlayPermissionAlertDialog();
                return false;
            }
        }
        Log.e("TAG", "All Overlay Permission Granted");
        sharedPrefsHelper.save(OVERLAY_PERMISSION_CHECKED_KEY, true);
        sharedPrefsHelper.save(MI_OVERLAY_PERMISSION_CHECKED_KEY, true);
        return true;
    }

    private void buildOverlayPermissionAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.overlayPermissionRequired));
        builder.setIcon(R.drawable.ic_error_outline_gray_24dp);
        builder.setMessage(getResources().getString(R.string.toReceiveCallsBackgroundPermission));
        builder.setCancelable(false);

        builder.setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.longToast(getResources().getString(R.string.youMightMissCallsWhileAppBack));
                sharedPrefsHelper.save(OVERLAY_PERMISSION_CHECKED_KEY, true);
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAndroidOverlayPermissionsSettings();
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void showAndroidOverlayPermissionsSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            Log.d("TAG", "Application Already has Overlay Permission");
        }
    }

    private void buildMIUIOverlayPermissionAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.additionalOverlayPermissionReq));
        builder.setIcon(R.drawable.ic_error_outline_orange_24dp);
        builder.setMessage(getResources().getString(R.string.pleaseMakeSureThatAllAdditionalPermissionGranted));
        builder.setCancelable(false);

        builder.setNeutralButton(getResources().getString(R.string.iamSure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedPrefsHelper.save(MI_OVERLAY_PERMISSION_CHECKED_KEY, true);
                //runNextScreen();
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.miSettings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMiUiPermissionsSettings();
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void showMiUiPermissionsSettings() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getPackageName());
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }
    private class MySliderAdapter extends PagerAdapter {

        private ArrayList<String> images;
        private Context context;

        public MySliderAdapter(Context context, ArrayList<String> images) {
            this.context = context;
            this.images=images;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View myImageLayout = LayoutInflater.from(context).inflate(R.layout.img_slider_item,view,false);
            ImageView myImage = (ImageView) myImageLayout
                    .findViewById(R.id.image_slider_item_lay);
            //myImage.setImageResource(images.get(position));
            Glide.with(context).load(images.get(position)).into(myImage);
            view.addView(myImageLayout, 0);
            return myImageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}