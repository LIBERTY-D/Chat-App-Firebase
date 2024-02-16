package com.daniel.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.daniel.chatapp.Adapters.UsersAdapter;
import com.daniel.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    DatabaseReference  dbRfef = firebaseDatabase.getReference().child("Users");
    RecyclerView usersRv;

    UsersAdapter usersAdapter;

    List<String> users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        users =  new ArrayList<>();
        dbRfef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                users.add(key);
                usersAdapter.notifyDataSetChanged();

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

        usersAdapter =  new UsersAdapter(MainActivity.this,users);
        usersRv = (RecyclerView) findViewById(R.id.usersRv);
        usersRv.setAdapter(usersAdapter);
        usersRv.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        usersAdapter.setClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                Intent  i =  new Intent(MainActivity.this, ChatActivity.class);
                i.putExtra("uid",usersAdapter.getUid(pos));
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.nav_menu,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int menuId = item.getItemId();

        if(menuId==R.id.profile){
            Intent i   = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(i);

        }
        else if(menuId==R.id.signOut){
            firebaseAuth.signOut();
            Intent i   = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);


        }
        return super.onOptionsItemSelected(item);
    }
}