package com.example.whatsapp.User;

import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.MainPage;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private ArrayList<UserObject> UserList;

    public UserListAdapter(ArrayList<UserObject> UserList){
        this.UserList = UserList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent .getContext()).inflate(R.layout.item_user,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, int position) {
        holder.mName.setText(UserList.get(position).getName());
        holder.mPhone.setText(UserList.get(position).getPhone());
        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

    }


    @Override
    public int getItemCount() {
        return UserList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder{
        TextView mName,mPhone;
        CheckBox mAdd;
        LinearLayout mLayout;
        UserListViewHolder (View view){
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mAdd = view.findViewById(R.id.add);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}
