package com.daniel.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.chatapp.Adapters.MessageAdapter;
import com.daniel.chatapp.Models.Message;
import com.daniel.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;

    DatabaseReference reference;

    FirebaseAuth firebaseAuth;


    RecyclerView chat_rv;
    ImageView back_arrow_image;
    EditText editTextUserMessage;

    TextView usernameTextViewChat;
    String otherId;
    String otherName;

    String ownerName;

    List<Message> messages;
    MessageAdapter messageAdapter;

    FloatingActionButton sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseApp.initializeApp(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        messages =  new ArrayList<>();
        reference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser owner = firebaseAuth.getCurrentUser();
        DatabaseReference  userRef = firebaseDatabase.getReference().child("Users");

        back_arrow_image = (ImageView) findViewById(R.id.back_arrow_image);


        editTextUserMessage = (EditText) findViewById(R.id.editTextUserMessage);

        usernameTextViewChat = (TextView) findViewById(R.id.usernameTextViewChat);

        sendMessageButton = (FloatingActionButton) findViewById(R.id.sendMessageButton);

        chat_rv = (RecyclerView) findViewById(R.id.chat_rv);


        Intent i = getIntent();
        otherId = i.getStringExtra("uid");
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              //current owner
                ownerName = snapshot.child(owner.getUid()).child("user").
                        child("username").getValue().toString();


             //   other
                otherName = snapshot.child(otherId).child("user").child("username").getValue().toString();
                if (owner.getUid().equals(otherId)) {
                    usernameTextViewChat.setText("Me");
                } else {
                    usernameTextViewChat.setText(otherName);
                }

             getMessages();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        back_arrow_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextUserMessage.getText().toString();
                if (!message.equals("")) {
                    sendMessage(message);
                    editTextUserMessage.setText(" ");

                } else {
                    Toast.makeText(ChatActivity.this, "Can't send an empty message", Toast.LENGTH_SHORT).show();
                }


            }

        });


    }

    private void getMessages() {

        reference.child("Messages").child(ownerName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                messageAdapter.notifyDataSetChanged();
                chat_rv.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageAdapter = new MessageAdapter(ownerName, messages);
        chat_rv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chat_rv.setAdapter(messageAdapter);

}


    public  void sendMessage(String message){


         String key =  reference.child("Messages").push().getKey();
         Map<String,Object> map = new HashMap<>();
         map.put("message",message);
         map.put("from", ownerName);
        reference.child("Messages").child(ownerName).child(otherName).child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(ownerName).child(key).setValue(map);
                }
            }
        });



    }
}