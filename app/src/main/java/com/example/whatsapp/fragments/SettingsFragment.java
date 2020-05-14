package com.example.whatsapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.AddUserDetailsActivity;
import com.example.whatsapp.LoginActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.User.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    View SettingsFragment;
    private Button mLogout;
    private View mUpdateProfile;
    private TextView mName,mPhone;
    private CircleImageView mProfileImage;
    UserObject currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SettingsFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        //Code
        mName = SettingsFragment.findViewById(R.id.name);
        mPhone = SettingsFragment.findViewById(R.id.phone);
        mUpdateProfile = SettingsFragment.findViewById(R.id.updateProfile);
        mLogout = SettingsFragment.findViewById(R.id.logout);
        mProfileImage = SettingsFragment.findViewById(R.id.profileImage);

        mUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddUserDetailsActivity.class);
                startActivity(intent);
            }
        });


        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        getCurrentUserInfo();
        return SettingsFragment;
    }
    private void getCurrentUserInfo() {
        DatabaseReference UserDb  = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {
                    String profileImageLink = "",phone ="",name="";
                    if (dataSnapshot.child("image").getValue() != null) {
                        profileImageLink = dataSnapshot.child("image").getValue().toString();
                    }
                    if (dataSnapshot.child("phone").getValue() != null) {
                        phone = dataSnapshot.child("phone").getValue().toString();
                    }
                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    Log.e("Name",name + " " + phone);
                    /**mName.setText(name);
                     mPhone.setText(phone);
                     **/
                    if (profileImageLink != "") {
                        Glide.with(getContext())
                                .load(profileImageLink)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
