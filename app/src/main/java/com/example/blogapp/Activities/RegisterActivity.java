package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogapp.R;
import com.example.blogapp.databinding.RegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    RegisterBinding binding;
    Context context;
    ImageView ImgUserPhoto;


    static int STORAGE_PERMISSION_CODE = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // To create the viuw binding of my register class
        binding = RegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.regUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // To check the permision or open the gallery
                if (Build.VERSION.SDK_INT >= 23) {

                    checkAndRequestForPermission();
                }
            }
        });
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(RegisterActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
            openGallery();
        } else {
              requestStorgaePermission();
        }

    }



    private void requestStorgaePermission() {
       if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){; // This function ask again for the imprtance of this permission

           new AlertDialog.Builder(this)
                   .setTitle("Permission needed")
                   .setMessage("This permission in needed because of this and that")
                   .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           ActivityCompat.requestPermissions(RegisterActivity.this,
                                   new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                   STORAGE_PERMISSION_CODE);     // STORAGE_PERMISSIO_CODE this identifies my request
                       }
                   })
                   .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   })
                   .create().show();
        } else {
           ActivityCompat.requestPermissions(RegisterActivity.this,
                   new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                   STORAGE_PERMISSION_CODE);     // STORAGE_PERMISSIO_CODE this identifies my request
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {  // To check if the permission was granted or not
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(RegisterActivity.this, "Premission GRANTED!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Premission DENIED!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openGallery() {

        Intent galleryIntent;
        galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);     // To create the intent of the camera
        galleryIntent.setType("image/*");  // To set the file
        startActivityForResult(galleryIntent,REQUESTCODE); // To start the intent
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i( "RequestCode " ,"Code" + requestCode);
        Log.i( "data " ,"data" + data);

        // To validate the result of the activity Gallery
        if(requestCode == RESULT_FIRST_USER && requestCode == REQUESTCODE &&  data != null){

            // the user has successfully picked an image and we need to save the reference to a Uri
            pickedImgUri = data.getData();  // To get the uri of my picked image
            Log.i( "getData " ,"getData" + data.getData());
            binding.regUserPhoto.setImageURI(pickedImgUri); // To save the image


        }
    }


}
