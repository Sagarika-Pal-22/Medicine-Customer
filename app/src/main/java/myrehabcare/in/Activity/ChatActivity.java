package myrehabcare.in.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import myrehabcare.in.Classes.ChatUser;
import myrehabcare.in.Classes.Chats;
import myrehabcare.in.Classes.User;
import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private Activity activity;//6506
    private Jsb jsb;
    private String chatId;
    private DatabaseReference myRef;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        activity = this;
        jsb = new Jsb(activity);
        gson = new Gson();
        chatId  = getIntent().getExtras().getString("chatId");

        myRef = database.getReference("UserChats").child(chatId);
        myRef.child("chats").keepSynced(true);
        myRef.child("userStatus").setValue("online");
        myRef.child("userStatus").onDisconnect().setValue(ServerValue.TIMESTAMP);

        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back, getTheme()));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("userDetails").exists()) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(jsb.getUser().getEmail());
                    chatUser.setId(jsb.getUser().getUser_id());
                    chatUser.setName(jsb.getUser().getFull_name());
                    chatUser.setPhone(jsb.getUser().getPhone());
                    chatUser.setType("USER");
                    String details = gson.toJson(chatUser);
                    myRef.child("userDetails").setValue(details);
                }else {
                    ChatUser chatUser2 = gson.fromJson(snapshot.child("userDetails").getValue(String.class), ChatUser.class);
                    if (!chatUser2.getId().equals(jsb.getUser().getUser_id())){
                        ChatUser chatUser = new ChatUser();
                        chatUser.setEmail(jsb.getUser().getEmail());
                        chatUser.setId(jsb.getUser().getUser_id());
                        chatUser.setName(jsb.getUser().getFull_name());
                        chatUser.setPhone(jsb.getUser().getPhone());
                        chatUser.setType("USER");
                        String details = gson.toJson(chatUser);
                        myRef.child("userDetails").setValue(details);
                        myRef.child("chats").removeValue();
                    }
                }

                String refreshToken = FirebaseInstanceId.getInstance().getToken();
                myRef.child("userFmc").setValue(refreshToken);

                if (!snapshot.child("chatCreatedTime").exists()) {
                    myRef.child("chatCreatedTime").setValue(ServerValue.TIMESTAMP);
                }

                myRef.child("chats").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ChatMessage chatMessage;
                        Chats chats = snapshot.getValue(Chats.class);
                /*if (!finalChatsList.contains(chats)){
                    finalChatsList.add(chats);
                    String json = gson.toJson(finalChatsList);
                    jsb.getPreferences().edit().putString("chats_data"+chatId, json).apply();
                    if (chats.isToDr()) {
                        chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.SENT);
                    } else {
                        chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.RECEIVED);
                    }
                    binding.chatView.addMessage(chatMessage);
                }*/

                        //finalChatsList.add(chats);
                        //String json = gson.toJson(finalChatsList);
                        //jsb.getPreferences().edit().putString("chats_data"+chatId, json).apply();
                        if (chats.isToDr()) {
                            chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.SENT);
                        } else {
                            chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.RECEIVED);
                        }
                        binding.chatView.addMessage(chatMessage);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        myRef.child("drStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() instanceof String) {
                    String status = snapshot.getValue(String.class);
                    binding.toolbar.setSubtitle(status);
                } else if (snapshot.getValue() instanceof Long) {
                    long status = snapshot.getValue(Long.class);
                    //last seen today at 00:00 am
                    binding.toolbar.setSubtitle(getFormattedDate(status));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        String json = jsb.getPreferences().getString("chats_data"+chatId, "");
        Type type = new TypeToken<List<Chats>>() {}.getType();
        List<Chats> chatsList = gson.fromJson(json, type);

        if (chatsList == null){
            chatsList = new ArrayList<>();
        }

        for (Chats chats : chatsList){
            ChatMessage chatMessage;
            if (chats.isToDr()) {
                chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.SENT);
            } else {
                chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.RECEIVED);
            }
            //binding.chatView.addMessage(chatMessage);
        }

        final List<Chats> finalChatsList = chatsList;
        /*myRef.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage;
                Chats chats = snapshot.getValue(Chats.class);
                *//*if (!finalChatsList.contains(chats)){
                    finalChatsList.add(chats);
                    String json = gson.toJson(finalChatsList);
                    jsb.getPreferences().edit().putString("chats_data"+chatId, json).apply();
                    if (chats.isToDr()) {
                        chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.SENT);
                    } else {
                        chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.RECEIVED);
                    }
                    binding.chatView.addMessage(chatMessage);
                }*//*

                finalChatsList.add(chats);
                String json = gson.toJson(finalChatsList);
                jsb.getPreferences().edit().putString("chats_data"+chatId, json).apply();
                if (chats.isToDr()) {
                    chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.SENT);
                } else {
                    chatMessage = new ChatMessage(chats.getMessage(), chats.getTime(), ChatMessage.Type.RECEIVED);
                }
                binding.chatView.addMessage(chatMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/
        binding.progress.setVisibility(View.GONE);
        binding.chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Chats chat = new Chats();
                chat.setToDr(true);
                chat.setMessage(chatMessage.getMessage());
                chat.setTime(chatMessage.getTimestamp());
                binding.progress.setVisibility(View.VISIBLE);
                myRef.child("chats").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        binding.progress.setVisibility(View.GONE);
                        binding.chatView.getInputEditText().setText("");
                    }
                });
                return false;
            }
        });


        binding.chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {
                myRef.child("userStatus").setValue("typing..");
            }

            @Override
            public void userStoppedTyping() {
                myRef.child("userStatus").setValue("online");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        myRef.child("userStatus").setValue("online");
        myRef.child("userStatus").onDisconnect().setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onDestroy() {
        myRef.child("userStatus").setValue(ServerValue.TIMESTAMP);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        myRef.child("userStatus").setValue(ServerValue.TIMESTAMP);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        myRef.child("userStatus").setValue(ServerValue.TIMESTAMP);
        super.onPause();
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";


        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "last seen today at " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "last seen yesterday at " + DateFormat.format(timeFormatString, smsTime);
        } else {
            return "last seen " + DateFormat.format("dd/MM/yyyy", smsTime).toString() + " at " + DateFormat.format("h:mm aa", smsTime).toString();
        }
    }
}