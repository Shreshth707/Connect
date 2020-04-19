package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.example.whatsapp.Chat.ChatObject;
import com.example.whatsapp.Chat.MessageAdapter;
import com.example.whatsapp.Chat.MessageObject;
import com.example.whatsapp.User.UserObject;
import com.example.whatsapp.Utilis.SendNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    ChatObject mChatObject;
    ArrayList<MessageObject> messageList;
    DatabaseReference mChatMessagesDb;
    UserObject currentUser = new UserObject(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");
        Button mSend = findViewById(R.id.send);
        mChatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        initialiseRecyclerView();
        getChatMessages();
        getCurrentUserInfo(FirebaseAuth.getInstance().getUid());

    }

    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()){
                            String creatorID = "",message = "",creatorName = "";
                            if (dataSnapshot.child("message").getValue() != null){
                                message = dataSnapshot.child("message").getValue().toString();
                            }
                            if (dataSnapshot.child("creatorId").getValue() != null) {
                                creatorID = dataSnapshot.child("creatorId").getValue().toString();
                            }
                            if (dataSnapshot.child("creatorName").getValue()!=null){
                                creatorName = dataSnapshot.child("creatorName").getValue().toString();
                            }
                            MessageObject mMessageObject = new MessageObject(creatorID,message,creatorName);
                            messageList.add(mMessageObject);
                            mChatLayoutManager.scrollToPosition(messageList.size()-1);
                            mChatAdapter.notifyDataSetChanged();
                        }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCurrentUserInfo(final String creatorId){
        DatabaseReference creatorDb = FirebaseDatabase.getInstance().getReference().child("user").child(creatorId);
        creatorDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("name").getValue()!=null)
                        currentUser.setName(dataSnapshot.child("name").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(){
        EditText mMessage = findViewById(R.id.Message);
        String messageId = mChatMessagesDb.push().getKey();
        if (!mMessage.getText().toString().isEmpty()){
            final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);

            final Map newMessageMap = new HashMap();
            newMessageMap.put("creatorId", FirebaseAuth.getInstance().getUid());
            newMessageMap.put("creatorName",currentUser.getName());
            newMessageMap.put("message",mMessage.getText().toString());
            newMessageDb.updateChildren(newMessageMap);
            mMessage.setText(null);

            String message = "";
            if (newMessageMap.get("message").toString()!=null){
                message = newMessageMap.get("message").toString();
            }
                for (UserObject mUser: mChatObject.getUserObjectArrayList()){
                    if (!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())){
                        new SendNotification(message,"New Message",mUser.getNotificationKey());
                    }
                }

        }


    }

    @SuppressLint("WrongConstant")
    private void initialiseRecyclerView() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

}
