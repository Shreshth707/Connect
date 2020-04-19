package com.example.whatsapp.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<MessageObject> messageList;
    private static final int Message_Left = 0;
    private static final int Message_Right = 1;
    public MessageAdapter(ArrayList<MessageObject> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = null;
        if (viewType == Message_Left) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }
        else if (viewType == Message_Right) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, final int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderName());
        if (messageList.get(position).getCreatorProfile()!=""){
            Glide.with(holder.itemView).load(messageList.get(position).getCreatorProfile()).into(holder.mProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

     class MessageViewHolder extends RecyclerView.ViewHolder{
         TextView mMessage,mSender;
         LinearLayout mLayout;
         CircleImageView mProfileImage;
         MessageViewHolder (View view){
            super(view);
            mProfileImage = view.findViewById(R.id.profileImage);
            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            mLayout = view.findViewById(R.id.layout);

        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageList.get(position).getSenderId().equals(fUser.getUid()))
            return Message_Right;
        else
            return Message_Left;
    }
}
