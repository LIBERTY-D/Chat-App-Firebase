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
import android.widget.Toast;

import com.daniel.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {


    ActivityResultLauncher<Intent> imageLaucher;

    CircleImageView profile_image;

    EditText emailSignUp,passwordSignUp,usernameSignUp;
    Button SignUpButton;


    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();;
    DatabaseReference databaseReference;

    StorageReference storageRef;

    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();;



    boolean imageControl=false;

    boolean button_status= false;
    String useremail;
    String userpassword;

    String username;
    Uri imageuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        registerImageLaucher();


        databaseReference = firebaseDatabase.getReference().child("Users");
        storageRef = firebaseStorage.getReference().child("Images");


        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        getSupportActionBar().setTitle("Sign Up");

        emailSignUp = (EditText) findViewById(R.id.emailSignUp);
        passwordSignUp= (EditText) findViewById(R.id.passwordSignUp);
        usernameSignUp = (EditText) findViewById(R.id.usernameSignUp);
        SignUpButton = (Button) findViewById(R.id.SignUpButton);





        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imageLaucher.launch(intent);


            }
        });


        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                button_status =true;
                SignUpButton.setClickable(false);

                useremail = emailSignUp.getText().toString();
                userpassword =  passwordSignUp.getText().toString();
                username  =  usernameSignUp.getText().toString();

                if(!useremail.equals("") && !userpassword.equals("")&&!username.equals("")){
                    signUpUserFireBase(useremail,userpassword,username);
                }else{
                    Toast.makeText(SignUp.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public  void registerImageLaucher(){
        imageLaucher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent i =  result.getData();
                if(result.getResultCode()==RESULT_OK && i!=null){
                    imageControl = true;
                     imageuri =  i.getData();
                    Picasso.get().load(imageuri).into(profile_image);

                }else{
                    Toast.makeText(getApplicationContext(), "You didnt choose an image", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public  void noImageSignUp(DatabaseReference userref,String email,String username){
        userref.child("username").setValue(username);
        userref.child("email").setValue(email);
        userref.child("Image").setValue("null");
        Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SignUp.this, LoginActivity.class);
        startActivity(i);
        imageControl =false;
    }

    public  void imageSignUp(DatabaseReference userref,String email,String username,String uid){
        userref.child("username").setValue(username);
        userref.child("email").setValue(email);
        storageRef.child(uid).putFile(imageuri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(uid).getDownloadUrl().addOnCompleteListener(SignUp.this,new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri image =  task.getResult();
                                    userref.child("Image").setValue(image.toString());
                                    Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUp.this, LoginActivity.class);
                                    startActivity(i);
                                    imageControl =false;
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }});

    }
    public  void signUpUserFireBase(String email, String password,String username){
           firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {

                   if(task.isSuccessful()){
                       String uid =  task.getResult().getUser().getUid();
                       DatabaseReference userref = databaseReference.child(uid).child("user");
                       if(imageControl==false){
                           noImageSignUp(userref,email,username);

                           SignUpButton.setClickable(true);

                       }else{
                           imageSignUp(userref,email,username,uid);

                           SignUpButton.setClickable(true);
                       }

                   }else{
                       Toast.makeText(SignUp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                       SignUpButton.setClickable(true);

                   }
               }
           });


    }


}