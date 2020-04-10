package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogapp.R;
import com.example.blogapp.database.FireBaseConexion;
import com.example.blogapp.databinding.RegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

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


        // Button Events
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.regBtn.setOnClickListener(this);
        //binding.regUserPhoto.setOnClickListener(this);

        binding.regUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // To check the permision or open the gallery
                if (Build.VERSION.SDK_INT >= 23) {

                    checkAndRequestForPermission();
                }
            }
        });

        // Logic for authetication

        // To not show the progress bar



    }

    @Override
    public void onClick(View v) {

        if(binding.regBtn == binding.regBtn){

            register();
            //Toast.makeText(RegisterActivity.this, "Butoon" , Toast.LENGTH_SHORT).show();
        }
/*
        if(binding.regUserPhoto == binding.regUserPhoto){
            // To check the permision or open the gallery
            if (Build.VERSION.SDK_INT >= 23) {

                checkAndRequestForPermission();
            }
        }

 */

    }

    private void register() {
        binding.regBtn.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
         final String email  =  binding.regEmail.getText().toString().trim();
         final String name  =  binding.regName.getText().toString().trim();
         final String password  =  binding.Password.getText().toString().trim();
         final String password2  =  binding.regPasswordConfirmd.getText().toString().trim();
         final ImageView userPhoto = binding.regUserPhoto;




         if(email.isEmpty() ||  name.isEmpty() || password.isEmpty() || !password2.equals(password) || userPhoto.getDrawable() == null ){
             Toast.makeText(RegisterActivity.this, "Please Verify all fields", Toast.LENGTH_SHORT).show();
             binding.regBtn.setVisibility(View.VISIBLE);
             binding.progressBar.setVisibility(View.INVISIBLE);


         } else {
            CreateUserAccount(email,name,password);
         }
    }

    private void CreateUserAccount(String email, final String name, String password) {
        // This method creates user account with specific email and name
        FireBaseConexion.authConexion().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();

                            // After we created user account we need to pudate his profile picture and name
                            updateUserInfo(name,pickedImgUri,FireBaseConexion.authConexion().getCurrentUser());
                        } else {
                            Toast.makeText(RegisterActivity.this, "Account created faild" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.regBtn.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                        }
                    }


                });

    }

    // update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        //first we need to upload user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();  // I create an instance
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment()); // getting the path of the image
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   //  image uploared succesfully and i can get the image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                          // uri contain user image uri and call a method to update the user
                        UserProfileChangeRequest  profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Register Complete", Toast.LENGTH_SHORT).show();
                                            updateUI();

                                        }
                                    }
                                });

                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
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
