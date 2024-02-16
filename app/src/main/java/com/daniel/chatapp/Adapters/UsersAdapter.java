package com.daniel.chatapp.Adapters;
import com.daniel.chatapp.Models.*;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.chatapp.Activities.ChatActivity;
import com.daniel.chatapp.Activities.MainActivity;
import com.daniel.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    DatabaseReference userRef = firebaseDatabase.getReference().child("Users");

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int pos);


}
    List<String> users;
    Context context;

    public UsersAdapter(Context context,List<String> users) {
        this.users = users;
        this.context =  context;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_rv,parent,false);
        return new UserViewHolder(view);
    }


    public  String getUid(int position){
        String uid = users.get(position);
        return uid;
    }

   public  void setClickListener(OnItemClickListener onItemClickListener){

        this.onItemClickListener = onItemClickListener;

   }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
                 String uid = getUid(position);
        userRef.child(uid).child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String image =  snapshot.child("Image").getValue().toString();
                        holder.other.setText(snapshot.child("username").getValue().toString());
                        if(!image.equals("null")){
                            Picasso.get().load(image).into(holder.userImage);
                        }else {
                            holder.userImage.setImageResource(R.drawable.ic_select_image);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class  UserViewHolder extends RecyclerView.ViewHolder {

      private CircleImageView userImage;
       private TextView other;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage =  itemView.findViewById(R.id.userImage);
            other =  itemView.findViewById(R.id.userName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });


        }
    }
}
