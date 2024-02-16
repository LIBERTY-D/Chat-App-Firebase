package com.daniel.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    TextView signupText;
    Button loginButton;
    EditText emailLogin, passwordLogin;


    FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    FirebaseUser user =  firebaseAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setTitle("Login");
        signupText  = (TextView) findViewById(R.id.LoginText);

        loginButton = (Button) findViewById(R.id.loginButton);

        emailLogin = (EditText) findViewById(R.id.emailLogin);

        passwordLogin = (EditText) findViewById(R.id.passwordLogin);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();
                signInUserFireBase(email, password);

            }
        });


        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent  intent =  new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

            }
        });



    }
    private void  signInUserFireBase(String email, String password){

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Intent i =  new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }else{

                    Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(user!=null){
            Intent i =  new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}