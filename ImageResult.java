package com.example.headache_classification.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.headache_classification.R;

public class ImageResult extends AppCompatActivity {
    ImageView image_view;
    TextView result;
    Button test_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_result);
        Intent intent = getIntent();
        String res = intent.getStringExtra("result");
        String img = intent.getStringExtra("image");
        test_again = findViewById(R.id.test_again);
        test_again.setOnClickListener(v->startActivity(new Intent(ImageResult.this, Home.class)));
        image_view = findViewById(R.id.image_view);
        result = findViewById(R.id.result);
        result.setText(res);
        Bitmap bitmap = BitmapFactory.decodeFile(img);
        image_view.setImageBitmap(bitmap);
    }
}