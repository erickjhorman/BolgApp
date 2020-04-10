package com.example.blogapp.database;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;

public class FireBaseConexion {


    Context context;

    public  static FirebaseAuth authConexion(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        return mAuth;
    }

}
