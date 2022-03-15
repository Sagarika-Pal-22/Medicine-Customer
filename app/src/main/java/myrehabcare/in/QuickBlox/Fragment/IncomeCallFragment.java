package myrehabcare.in.QuickBlox.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.AppRTCAudioManager;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import myrehabcare.in.QuickBlox.QuickBloxUtils.CollectionsUtils;
import myrehabcare.in.QuickBlox.QuickBloxUtils.IncomeCallFragmentCallbackListener;
import myrehabcare.in.QuickBlox.QuickBloxUtils.UiUtils;
import myrehabcare.in.QuickBlox.QuickBloxUtils.UsersUtils;
import myrehabcare.in.QuickBlox.QuickBloxUtils.WebRtcSessionManager;
import myrehabcare.in.QuickBlox.Service.CallService;
import myrehabcare.in.QuickBlox.db.QbUsersDbManager;
import myrehabcare.in.R;
import myrehabcare.in.Service.Config;

/**
 * QuickBlox team
 */
public class IncomeCallFragment extends Fragment implements Serializable, View.OnClickListener {
    private static final String TAG = IncomeCallFragment.class.getSimpleName();

    private static final int PER_PAGE_SIZE_100 = 100;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_DESC_UPDATED = "desc string updated_at";

    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private TextView callTypeTextView;
    private ImageButton rejectButton;
    private ImageButton takeButton;
    private TextView callerNameTextView;
    private ProgressBar progressUserName;

    private List<Integer> opponentsIds;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType conferenceType;
    private long lastClickTime = 0l;
    private MediaPlayer ringtonePlayer;
    private IncomeCallFragmentCallbackListener incomeCallFragmentCallbackListener;
    private QBRTCSession currentSession;
    private QbUsersDbManager qbUserDbManager;
    private TextView alsoOnCallText;
    AudioManager audioManager;
    boolean isVideoCall = false;
    private AppRTCAudioManager appRTCAudioManager;
    private CallService callService;
    int mediaPlayerDuration;
    PowerManager pm;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            incomeCallFragmentCallbackListener = (IncomeCallFragmentCallbackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCallEventsController");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        Log.d(TAG, "onCreate() from IncomeCallFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_call, container, false);
        appRTCAudioManager = AppRTCAudioManager.create(getContext());
        initFields();
        hideToolBar();
        initUI(view);
        setDisplayedTypeCall(conferenceType);
        initButtonsListener();
        pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtonePlayer =new MediaPlayer();
        try {
            ringtonePlayer.setDataSource(getContext(), alert);
        } catch (IOException e) {
        }
        return view;
    }

    private void initFields() {
        currentSession = WebRtcSessionManager.getInstance(getActivity()).getCurrentSession();
        qbUserDbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        if (currentSession != null) {
            opponentsIds = currentSession.getOpponents();
            conferenceType = currentSession.getConferenceType();
            Log.d(TAG, conferenceType.toString() + "From onCreateView()");
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
            Log.d(TAG, "Current session is not exist. Pop BackStack");
        }
    }

    public void hideToolBar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_call);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            startCallNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtonsListener() {
        rejectButton.setOnClickListener(this);
        takeButton.setOnClickListener(this);
    }

    private void initUI(View view) {
        callTypeTextView = view.findViewById(R.id.call_type);
        progressUserName = view.findViewById(R.id.progress_bar_opponent_name);
        ImageView callerAvatarImageView = view.findViewById(R.id.image_caller_avatar);
        callerNameTextView = view.findViewById(R.id.text_caller_name);
        TextView otherIncUsersTextView = view.findViewById(R.id.text_other_inc_users);
        alsoOnCallText = view.findViewById(R.id.text_also_on_call);
        rejectButton = view.findViewById(R.id.image_button_reject_call);
        takeButton = view.findViewById(R.id.image_button_accept_call);

        if (currentSession != null) {
            callerAvatarImageView.setBackgroundDrawable(getBackgroundForCallerAvatar(currentSession.getCallerID()));

            QBUser callerUser = qbUserDbManager.getUserById(currentSession.getCallerID());
            if (callerUser != null && !TextUtils.isEmpty(callerUser.getFullName())) {
                callerNameTextView.setText(callerUser.getFullName());
            } else {
                callerNameTextView.setText(String.valueOf(currentSession.getCallerID()));
                updateUserFromServer();
            }

            otherIncUsersTextView.setText(getOtherIncUsersNames());
            setVisibilityAlsoOnCallTextView();
        }
    }

    private void updateUserFromServer() {
        progressUserName.setVisibility(View.VISIBLE);
        Integer callerID = currentSession.getCallerID();
        QBUsers.getUser(callerID).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                if (qbUser != null) {
                    qbUserDbManager.saveUser(qbUser);
                    String callerName = TextUtils.isEmpty(qbUser.getFullName())? qbUser.getLogin() : qbUser.getFullName();
                    callerNameTextView.setText(callerName);
                }
                progressUserName.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                progressUserName.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponentsIds.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private Drawable getBackgroundForCallerAvatar(int callerId) {
        return UiUtils.getColorCircleDrawable(callerId);
    }

    private void startCallNotification() throws IOException {
        try {
            if (pm.isScreenOn()) {
                if (Config.notificationMediaPlayer != null && Config.notificationMediaPlayer.isPlaying())
                    Config.notificationMediaPlayer.pause();
                mediaPlayerDuration = Config.notificationMediaPlayer.getDuration();
                Log.d("TAGIIIII", "Power");
            }
        }catch (Exception e){}
        Log.d("TAGIIIII", "startCallNotification()");
        //ringtonePlayer.play(true);
        ringtonePlayer.setLooping(true);
        ringtonePlayer.prepare();
        ringtonePlayer.start();
      //  ringtonePlayer.setVolume(500,500);
       vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }
       /* NotificationUtils notificationUtils = new NotificationUtils(getContext());
        notificationUtils.playNotificationRingtone(getContext(), Config.notificationVibrator,Config.notificationMediaPlayer);*/
    }

    private void stopCallNotification() {
        Log.d("TAGIIIII", "stopCallNotification()");
        if (Config.notificationMediaPlayer != null) {
            Config.notificationMediaPlayer.stop();
        }
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
           // Config.notificationMediaPlayer.release();
          //  ringtonePlayer.reset();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
        if(Config.isVideoCall){
          //  audioManager.setSpeakerphoneOn(true);
            switchAudio();
        }else{
           audioManager.setSpeakerphoneOn(false);
            switchAudio();
        }
    }
    public void switchAudio() {
        Log.v(TAG, "onSwitchAudio(), SelectedAudioDevice() = " + appRTCAudioManager.getSelectedAudioDevice());
        if (appRTCAudioManager.getSelectedAudioDevice() != AppRTCAudioManager.AudioDevice.SPEAKER_PHONE) {
            appRTCAudioManager.selectAudioDevice(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
        } else {
            if (appRTCAudioManager.getAudioDevices().contains(AppRTCAudioManager.AudioDevice.BLUETOOTH)) {
                appRTCAudioManager.selectAudioDevice(AppRTCAudioManager.AudioDevice.BLUETOOTH);
            } else if (appRTCAudioManager.getAudioDevices().contains(AppRTCAudioManager.AudioDevice.WIRED_HEADSET)) {
                appRTCAudioManager.selectAudioDevice(AppRTCAudioManager.AudioDevice.WIRED_HEADSET);
            } else {
                appRTCAudioManager.selectAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE);
            }
        }
    }
    private String getOtherIncUsersNames() {
        ArrayList<QBUser> usersFromDb = qbUserDbManager.getUsersByIds(opponentsIds);
        ArrayList<QBUser> opponents = new ArrayList<>();
        opponents.addAll(UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds));

        opponents.remove(QBChatService.getInstance().getUser());
        Log.d(TAG, "opponentsIds = " + opponentsIds);
        return CollectionsUtils.makeStringFromUsersFullNames(opponents);
    }

    private void setDisplayedTypeCall(QBRTCTypes.QBConferenceType conferenceType) {
        boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        callTypeTextView.setText(isVideoCall ? R.string.text_incoming_video_call : R.string.text_incoming_audio_call);
        takeButton.setImageResource(isVideoCall ? R.drawable.ic_video_white : R.drawable.ic_call);
    }

    @Override
    public void onStop() {
        stopCallNotification();
        super.onStop();
        Log.d(TAG, "onStop() from IncomeCallFragment");
    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (v.getId()) {
            case R.id.image_button_reject_call:
                reject();
                break;

            case R.id.image_button_accept_call:
                accept();
                break;

            default:
                break;
        }
    }

    private void accept() {
        enableButtons(false);
        stopCallNotification();

        incomeCallFragmentCallbackListener.onAcceptCurrentSession();
        Log.d(TAG, "Call is started");
    }

    private void reject() {
        enableButtons(false);
        stopCallNotification();

        incomeCallFragmentCallbackListener.onRejectCurrentSession();
        Log.d(TAG, "Call is rejected");
    }

    private void enableButtons(boolean enable) {
        takeButton.setEnabled(enable);
        rejectButton.setEnabled(enable);
    }
}