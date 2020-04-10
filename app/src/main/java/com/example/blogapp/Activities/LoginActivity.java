package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blogapp.database.FireBaseConexion;
import com.example.blogapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding  binding;

    // I created an variable  of  type HomeActivity
    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To create the viuw binding of my register class
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // I created an instance of HomeActivity
        HomeActivity = new Intent(this, HomeActivity.class);


        binding.loginProgress.setVisibility(View.INVISIBLE);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  binding.loginProgress.setVisibility(View.INVISIBLE);
                  binding.loginBtn.setVisibility(View.INVISIBLE);

                final String  email =   binding.userMail.getText().toString().trim();
                final String  password =  binding.userPassword.getText().toString().trim();

                if(email.isEmpty() ||  password.isEmpty()  ){
                    Toast.makeText(LoginActivity.this, "Please Verify all fields", Toast.LENGTH_SHORT).show();
                    binding.loginProgress.setVisibility(View.INVISIBLE);
                    binding.loginBtn.setVisibility(View.VISIBLE);


                } else {
                    signIn(email,password);
                }
            }
        });

        binding.loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
            }
        });

    }

    private void signIn(String email, String password) {

        FireBaseConexion.authConexion().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    binding.loginProgress.setVisibility(View.VISIBLE);
                    binding.loginBtn.setVisibility(View.INVISIBLE);

                    updateUI();
                } else {
                    Toast.makeText(LoginActivity.this, "Account created faild" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.loginBtn.setVisibility(View.VISIBLE);
                    binding.loginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
       startActivity(HomeActivity);
       finish();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FireBaseConexion.authConexion().getCurrentUser();  // To get the information of the current user

        if(user != null){
            // user is already connected so we need to redirect him to home page
            updateUI();
        }


    }
}
