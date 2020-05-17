package com.example.blogapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.blogapp.R;
import com.example.blogapp.databinding.ActivityHomeBinding;
import com.example.blogapp.databinding.RegisterBinding;

public class HomeActivity extends AppCompatActivity {

     ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }
}
