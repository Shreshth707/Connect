package com.example.whatsapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = null;
        if (viewType == 0){
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_red,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }else if(viewType == 1){
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_yellow,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }else if (viewType == 2){
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_light_green,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }else if (viewType == 3){
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_green,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }else if(viewType == 4) {
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_light_blue,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }else if (viewType == 5) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_blue, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        } else if(viewType == 6){
            layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_chat_purple,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
        }


        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        holder.mTitle.setText(chatList.get(position).getName());
        if (chatList.get(position).getChatIconLink()!=""){
            Glide.with(holder.itemView).load(chatList.get(position).getChatIconLink()).into(holder.mChatIcon);
        }
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("chatObject",chatList.get(holder.getAdapterPosition()));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

     class ChatListViewHolder extends RecyclerView.ViewHolder{
         TextView mTitle;
         CircleImageView mChatIcon;
         LinearLayout mLayout;
         ChatListViewHolder (View view){
            super(view);
            mChatIcon = view.findViewById(R.id.profileImage);
            mTitle = view.findViewById(R.id.title);
            mLayout = view.findViewById(R.id.layout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 7);
    }
}
