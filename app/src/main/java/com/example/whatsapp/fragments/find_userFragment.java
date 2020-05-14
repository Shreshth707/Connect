package com.example.whatsapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsapp.GroupNameActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.User.UserListAdapter;
import com.example.whatsapp.User.UserObject;
import com.example.whatsapp.Utilis.CountryToPhonePrefix;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class find_userFragment extends Fragment {

    public find_userFragment() {
        // Required empty public constructor
    }

    View FindUserFragment;
    private RecyclerView mUserList;
    private androidx.recyclerview.widget.RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    private TextView mCreate,mCancel;

    DatabaseReference chatInfoDb;
    DatabaseReference userDb;

    String chatId;
    ArrayList<UserObject> userList,contactList;
    ArrayList<UserObject> selectedUsers;
    UserObject currentUser = new UserObject(FirebaseAuth.getInstance().getUid());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FindUserFragment = inflater.inflate(R.layout.fragment_find_user, container, false);
        //Code
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        mCreate = FindUserFragment.findViewById(R.id.create);
        mCancel = FindUserFragment.findViewById(R.id.cancelBtn);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
                getFragmentManager().beginTransaction().replace(R.id.frame_layout,new chatFragment()).commit();            }
        });

        initialiseRecyclerView();
        getContactList();
        getCurrentUserInfo();
        return FindUserFragment;
    }

    private void createChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
        chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        userDb = FirebaseDatabase.getInstance().getReference().child("user");
        chatId = key;
        selectedUsers = new ArrayList<>();

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);


        for(UserObject mUser : userList){
            if(mUser.getSelected()){
                newChatMap.put("users/" + mUser.getUid(), true);
                selectedUsers.add(mUser);
                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }



        if(selectedUsers.size()==1){  //Private chat room
            newChatMap.put("chatType","0");
            chatInfoDb.updateChildren(newChatMap);

            HashMap chatName1 = new HashMap();
            chatName1.put("chatName",currentUser.getName());
            if (currentUser.getProfileImageLink()!="")
                chatName1.put("chatIcon",currentUser.getProfileImageLink());
            userDb.child(selectedUsers.get(0).getUid()).child("chat").child(key).updateChildren(chatName1);

            HashMap chatName2 = new HashMap();


            chatName2.put("chatName" , selectedUsers.get(0).getName());
            if (selectedUsers.get(0).getProfileImageLink()!="")
                chatName2.put("chatIcon",selectedUsers.get(0).getProfileImageLink());
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).updateChildren(chatName2);
        }else if (selectedUsers.size()>1){ // Group chat room
            newChatMap.put("chatType","1");
            chatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatId).setValue(true);
            Intent intent = new Intent(getContext(), GroupNameActivity.class);
            intent.putExtra("chatId",key);
            startActivityForResult(intent,1);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == 1){
                String chatName = data.getStringExtra("groupName");
                String chatIconUrl = data.getStringExtra("groupChatIcon");
                for (UserObject selectedUser : selectedUsers){
                    userDb.child(selectedUser.getUid()).child("chat").child(chatId).setValue(true);
                    userDb.child(selectedUser.getUid()).child("chat").child(chatId).child("chatName").setValue(chatName);
                    if (chatIconUrl!=""){
                        userDb.child(selectedUser.getUid()).child("chat").child(chatId).child("chatIcon").setValue(chatIconUrl);
                    }
                }
                userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatId).child("chatName").setValue(chatName);
            }
        }
    }

    private void getCurrentUserInfo(){
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("name").getValue()!=null) {
                        currentUser.setName(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("image")){
                        if (dataSnapshot.child("image").getValue()!=null){
                            currentUser.setProfileImageLink(dataSnapshot.child("image").getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getContactList(){
        String ISOPrefix = getCountryIso();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ","");
            phone = phone.replace("-","");
            phone = phone.replace("(","");
            phone = phone.replace(")","");
            if (!String.valueOf(phone.charAt(0)).equals("+")){
                phone = ISOPrefix + phone;
            }

            boolean exists = false;
            for (UserObject mContactIterator :contactList){
                if (mContactIterator.getPhone().equals(phone)){
                    exists = true;
                }
            }
            if (exists){
                continue;
            }
            UserObject mContact = new UserObject(name,phone,"");
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }

    private void getUserDetails(final UserObject mContact) {
        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = "",phone = "",profileImageLink = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                        }
                        if (childSnapshot.child("name").getValue() != null) {
                            name = mContact.getName();
                        }
                        if (childSnapshot.hasChild("image")){
                            if (childSnapshot.child("image").getValue()!=null){
                                profileImageLink = childSnapshot.child("image").getValue().toString();
                            }
                        }
                        UserObject mUserObject = new UserObject(name, phone,childSnapshot.getKey());
                        mUserObject.setProfileImageLink(profileImageLink);

                        userList.add(mUserObject);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryIso(){
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null){
            if (!telephonyManager.getNetworkCountryIso().equals("")){
                iso = telephonyManager.getNetworkCountryIso();
            }
        }
        return CountryToPhonePrefix.getPhone(iso);
    }




    @SuppressLint("WrongConstant")
    private void initialiseRecyclerView() {
        mUserList = FindUserFragment.findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }

}
