package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserDetailsActivity extends AppCompatActivity {

    private Button mConfirm;
    private TextView mCancel,mPhone;
    private EditText mName;
    private CircleImageView mProfileImage;
    private ImageView mEditProfileImage;
    private DatabaseReference mUserDb;
    private StorageReference UserProfileImageRef;
    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        mPhone = findViewById(R.id.phoneno);
        mCancel =findViewById(R.id.cancelBtn);
        mEditProfileImage = findViewById(R.id.editProfileImage);
        mProfileImage = findViewById(R.id.profileImage);
        mName = findViewById(R.id.username);
        mConfirm = findViewById(R.id.confirm);
        mUserDb = FirebaseDatabase.getInstance().getReference().child("user");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        retrieveUserInfo();
        mEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);

            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNameToUserDataBase();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addNameToUserDataBase() {
        DatabaseReference mCurrentUserDb = mUserDb.child(FirebaseAuth.getInstance().getUid());
        Map mUser = new HashMap();
        mUser.put("name",mName.getText().toString());
        mCurrentUserDb.updateChildren(mUser);
        Intent intent = new Intent();
        setResult(1,intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageRef.child(FirebaseAuth.getInstance().getUid() + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                Map UserMap = new HashMap();
                                UserMap.put("image",downloadUrl);
                                mUserDb.child(FirebaseAuth.getInstance().getUid()).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(getApplicationContext(), "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                        else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(getApplicationContext(), "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }


    public void retrieveUserInfo(){
        mUserDb.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String UserName = "",profileImage="";
                    if (dataSnapshot.child("name").getValue()!=null){
                        UserName = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("image").getValue()!=null){
                        profileImage = dataSnapshot.child("image").getValue().toString();
                    }
                    mName.setText(UserName);
                    //Picasso.get().load(profileImage).into(mProfileImage);
                    Glide.with(AddUserDetailsActivity.this).load(profileImage).placeholder(R.drawable.ic_launcher_background).into(mProfileImage);
                }else if (dataSnapshot.exists()){
                    String UserName = "",phone = "";
                    if (dataSnapshot.child("name").getValue()!=null){
                        UserName = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("phone").getValue()!=null){
                        phone = dataSnapshot.child("phone").getValue().toString();
                    }
                    mName.setText(UserName);
                    mPhone.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);

    }
}
