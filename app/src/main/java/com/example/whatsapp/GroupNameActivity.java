package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupNameActivity extends AppCompatActivity {

    Button mConfirm;
    EditText mGroupName;
    ImageView mAddGroupIcon;
    CircleImageView mGroupIcon;
    private static final int GalleryPick = 1;
    private StorageReference ChatIconRef;
    private DatabaseReference UserchatDb;
    private String chatID,chatIconUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        chatIconUrl = "";
        Intent intent = getIntent();
        chatID = intent.getStringExtra("chatId");
        mConfirm = findViewById(R.id.confirmName);
        mGroupName = findViewById(R.id.groupName);
        mAddGroupIcon = findViewById(R.id.addGroupIcon);
        mGroupIcon = findViewById(R.id.groupIcon);
        ChatIconRef = FirebaseStorage.getInstance().getReference().child("GroupChatIcon");
        UserchatDb = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatID);


        mAddGroupIcon.setOnClickListener(new View.OnClickListener() {
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
                String groupName = mGroupName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("groupName",groupName);
                intent.putExtra("groupChatIcon",chatIconUrl);
                setResult(1,intent);
                finish();
            }
        });

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
                final StorageReference filePath = ChatIconRef.child(chatID + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                chatIconUrl = downloadUrl;
                                Map UserMap = new HashMap();
                                UserMap.put("chatIcon",downloadUrl);
                                UserchatDb.updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                            Glide.with(GroupNameActivity.this).load(chatIconUrl).placeholder(R.drawable.ic_launcher_background).into(mGroupIcon);
                                        }
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

}
