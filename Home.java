package com.example.headache_classification.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.headache_classification.Auth.Login;
import com.example.headache_classification.Client.RetrofitClient;
import com.example.headache_classification.Modals.UploadImageModal;
import com.example.headache_classification.R;
import com.example.headache_classification.RealPathUtil;
import com.example.headache_classification.SharedPreferenceManager.SharedPref;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    Button upload_img, save_img;
    ImageView view_img, logout_user;
    String path;
    SharedPref pref;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = new SharedPref(getApplicationContext());
        upload_img = findViewById(R.id.upload_image);
        view_img = findViewById(R.id.view_image);
        save_img = findViewById(R.id.save_image);
        upload_img.setOnClickListener(v -> pickImage());
        logout_user = findViewById(R.id.logout_user);
        progressBar = findViewById(R.id.progressBar);
        logout_user.setOnClickListener(v -> {
            Logout();
        });
        save_img.setOnClickListener(v -> saveImage());
    }

    private void saveImage() {
        save_img.setVisibility(View.GONE);
        if (validate(path)) {
            progressBar.setVisibility(View.VISIBLE);
            File file = new File(path);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part user_image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            Call<UploadImageModal> call = RetrofitClient.getInstance().getApi().save_image(user_image);
            call.enqueue(new Callback<UploadImageModal>() {
                @Override
                public void onResponse(Call<UploadImageModal> call, Response<UploadImageModal> response) {
                    progressBar.setVisibility(View.GONE);
                    UploadImageModal uploadImageModal = response.body();
                    assert uploadImageModal != null;
                    if (uploadImageModal.getStatus().equals("OK")) {

                        Intent intent = new Intent(Home.this, ImageResult.class);
                        intent.putExtra("result", uploadImageModal.getData());
                        intent.putExtra("image", path);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Home.this, uploadImageModal.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadImageModal> call, Throwable t) {

                }
            });
        }
    }

    private boolean validate(String path) {
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(Home.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void pickImage() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 10) {
            assert data != null;
            Uri uri = data.getData();
            Context context = Home.this;
            path = RealPathUtil.getRealPath(context, uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            view_img.setImageBitmap(bitmap);
            view_img.setImageURI(uri);
            upload_img.setVisibility(View.VISIBLE);
            save_img.setVisibility(View.VISIBLE);

        }
    }

    private void Logout() {
        pref.userLogOut();
        Toast.makeText(Home.this, "Logout successfully...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Home.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with picking an image
                pickImage();
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "Permission denied. Unable to pick an image.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}