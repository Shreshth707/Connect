package com.example.whatsapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.whatsapp.Chat.ChatListAdapter;
import com.example.whatsapp.Chat.ChatObject;
import com.example.whatsapp.R;
import com.example.whatsapp.User.UserObject;
import com.example.whatsapp.Utilis.SpacingDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class chatFragment extends Fragment {

    public chatFragment() {
        // Required empty public constructor
    }

    View ChatFragment;
    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;
    ArrayList<ChatObject> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChatFragment = inflater.inflate(R.layout.fragment_chat, container, false);
        //Code
        OneSignal.startInit(getContext()).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        getPermissions();
        initialiseRecyclerView();
        getUserChatList();
        return ChatFragment;
    }

    private void getUserChatList(){
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String chatIconLink = "";
                        if (childSnapshot.hasChild("chatIcon")) {
                            if (childSnapshot.child("chatName").getValue() != null) {
                                if (childSnapshot.child("chatIcon").getValue() != null)
                                    chatIconLink = childSnapshot.child("chatIcon").getValue().toString();
                                ChatObject mChat = new ChatObject(childSnapshot.getKey(), childSnapshot.child("chatName").getValue().toString());
                                mChat.setChatIconLink(chatIconLink);
                                boolean exists = false;
                                for (ChatObject mChatIterator : chatList) {
                                    if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                        exists = true;
                                }
                                if (exists)
                                    continue;
                                chatList.add(mChat);
                                mChatListAdapter.notifyDataSetChanged();
                                getChatData(mChat.getChatId());
                            }
                        }else {
                            if (childSnapshot.child("chatName").getValue() != null) {
                                ChatObject mChat = new ChatObject(childSnapshot.getKey(), childSnapshot.child("chatName").getValue().toString());
                                mChat.setChatIconLink(chatIconLink);
                                boolean exists = false;
                                for (ChatObject mChatIterator : chatList) {
                                    if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                        exists = true;
                                }
                                if (exists)
                                    continue;
                                chatList.add(mChat);
                                mChatListAdapter.notifyDataSetChanged();
                                getChatData(mChat.getChatId());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatData(String chatId) {
        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("info");
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String chatId = "";
                    if (dataSnapshot.child("id").getValue()!=null){
                        chatId = dataSnapshot.child("id").getValue().toString();
                    }
                    for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()){
                        for (ChatObject mChat : chatList){
                            if(mChat.getChatId().equals(chatId)){
                                UserObject mUser = new UserObject(userSnapshot.getKey());
                                mChat.addUserToArrayList(mUser);
                                getUserData(mUser);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData(UserObject mUser) {
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("user").child(mUser.getUid());
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject mUser = new UserObject(dataSnapshot.getKey());

                if (dataSnapshot.child("notificationKey").getValue()!=null){
                    mUser.setNotificationKey(dataSnapshot.child("notificationKey").getValue().toString());
                }
                for (ChatObject mChat : chatList){
                    for (UserObject mUserIterator : mChat.getUserObjectArrayList()){
                        if (mUserIterator.getUid().equals(mUser.getUid())){
                            mUserIterator.setNotificationKey(mUser.getNotificationKey());
                        }
                    }
                }
                mChatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("WrongConstant")
    private void initialiseRecyclerView() {
        chatList = new ArrayList<>();
        mChatList = ChatFragment.findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        SpacingDecorator itemDecorator = new SpacingDecorator(4);
        mChatList.addItemDecoration(itemDecorator);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},1);
        }
    }

}
