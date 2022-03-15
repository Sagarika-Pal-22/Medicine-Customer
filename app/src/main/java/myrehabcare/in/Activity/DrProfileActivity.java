package myrehabcare.in.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import myrehabcare.in.Classes.Doctors;
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
import myrehabcare.in.databinding.ActivityDrProfileBinding;

public class DrProfileActivity extends AppCompatActivity {

    private ActivityDrProfileBinding binding;
    private Activity activity;
    private Jsb jsb;
    private Doctors doctors;
    private PermissionsChecker checker;
    private QBUser currentUser;
    List<QBUser> userList = new ArrayList<>();
    List<QBUser> selectedUsers = new ArrayList<>();
    protected SharedPrefsHelper sharedPrefsHelper;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_DESC_UPDATED = "desc date updated_at";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        checker = new PermissionsChecker(getApplicationContext());
        currentUser = SharedPrefsHelper.getInstance(this).getQbUser();
        doctors = (Doctors) getIntent().getSerializableExtra("doctors");

        Glide.with(activity).load(doctors.getImage()).placeholder(R.drawable.ic_default_user).into(binding.drProfileImage);

        if (doctors.getAbout_us() != null && !doctors.getAbout_us().isEmpty()){
            binding.aboutUsTv.setText(doctors.getAbout_us());
        }else {
            binding.aboutUsTv.setText("About doctors not available");
        }

        binding.drName.setText(doctors.getName());
        try {
            binding.drType.setText(doctors.getType().split(":")[0]);
        }catch (Exception e){}

        binding.starRating.setRating(doctors.getRating());
        Log.d("jnnjnjnjnjn", String.valueOf(doctors.getType()));
        if(doctors.getService_type().equals("1")){
            binding.linCoonnectBy.setVisibility(View.VISIBLE);
        }else{
            binding.linCoonnectBy.setVisibility(View.GONE);
        }
        binding.videoCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkIsLoggedInChat()) {
                        startCall(true,doctors.getId());
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                        startPermissionsActivity(false);
                    }
                }catch (Exception e){}
               //startActivity(new Intent(activity, VideoCallScreen.class).putExtra("id",doctors.getId()).putExtra("name", doctors.getName()));
            }
        });


        binding.voiceCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkIsLoggedInChat()) {
                        startCall(false,doctors.getId());
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                        startPermissionsActivity(true);
                    }
                }catch (Exception e){}
                //startActivity(new Intent(activity, VoiceCallScreen.class).putExtra("id",doctors.getId()).putExtra("name", doctors.getName()));
            }
        });
        binding.chatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ChatActivity.class).putExtra("chatId", doctors.getId()));
            }
        });
        boolean isIncomingCall = SharedPrefsHelper.getInstance(this).get(Consts.EXTRA_IS_INCOMING_CALL, false);
        if (isCallServiceRunning(CallService.class)) {
            Log.d("TAG", "CallService is running now");
            CallActivity.start(this, isIncomingCall);
        }
        clearAppNotifications();
        loadUsers();
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
    public void next_bt(View view) {
        startActivity(new Intent(activity, DrFeedbackActivity.class).putExtra("doctors", doctors));
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
        if(opponentsList.isEmpty()){
            Toast.makeText(DrProfileActivity.this,"You are not Register for Audio and Video Call. kindly Login again !",Toast.LENGTH_LONG).show();
        }else {
            Log.d("OponentList", opponentsList.toString());
            QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

            // Make Users FullName Strings and ID's list for iOS VOIP push
            String newSessionID = newQbRtcSession.getSessionID();
            ArrayList<String> opponentsIDsList = new ArrayList<>();
            ArrayList<String> opponentsNamesList = new ArrayList<>();
            List<QBUser> usersInCall = getSelectedUsers(opponentUserIds);

            // the Caller in exactly first position is needed regarding to iOS 13 functionality
            usersInCall.add(0, currentUser);
            Log.d("CurrentList", currentUser.toString());
            Log.d("FinalList", usersInCall.toString());
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
}