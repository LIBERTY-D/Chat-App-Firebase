package com.daniel.chatapp.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daniel.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
     CircleImageView profileImage;
     EditText profileUserName,profilePassword,emailProfile;

     Button profileButton;


     ProgressBar progressProfile;


    ActivityResultLauncher<Intent> imageLaucher;

    FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    FirebaseUser user =  firebaseAuth.getCurrentUser();

    FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users");
    String username;
    String email;


    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        registerImageLaucher();


        getSupportActionBar().setTitle("Profile");

        profileImage= (CircleImageView) findViewById(R.id.profileImage);

        profileUserName = (EditText) findViewById(R.id.profileUserName);

        profilePassword = (EditText) findViewById(R.id.profilePassword );

        emailProfile  = (EditText) findViewById(R.id.emailProfile);

        profileButton = (Button) findViewById(R.id.profileButton);


        progressProfile = (ProgressBar) findViewById(R.id.progressProfile);

        progressProfile.setVisibility(View.INVISIBLE);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imageLaucher.launch(intent);
            }
        });




        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public  void registerImageLaucher(){
        imageLaucher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent i =  result.getData();
                if(result.getResultCode()==RESULT_OK && i!=null){
                    Uri imageuri =  i.getData();
                    Picasso.get().load(imageuri).into(profileImage);

                }else{
                    Toast.makeText(getApplicationContext(), "You didnt choose an image", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public  void setProfile(String email,String username,String image){
        profileUserName.setText(username);
        emailProfile.setText(email);
       if(!image.equals("null")){
           Picasso.get().load(image).into(profileImage);
       }else{
           profileImage.setImageResource(R.drawable.ic_select_image);
       }
        emailProfile.setEnabled(false);
        profileUserName.setEnabled(false);
    }
    @Override
    protected void onStart() {
        super.onStart();
        progressProfile.setVisibility(View.VISIBLE);
        String uid = user.getUid();
         databaseReference.child(uid).child("user").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 username =  snapshot.child("username").getValue().toString();
                 email =  snapshot.child("email").getValue().toString();
                  image =  snapshot.child("Image").getValue().toString();
                  setProfile(email,username,image);

                 progressProfile.setVisibility(View.INVISIBLE);


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }
}