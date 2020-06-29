package com.ArabProgrammers.CollegeProject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DisplayImageActivity extends AppCompatActivity {

    private ImageView img_display_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);


        img_display_image = findViewById(R.id.img_display_image);

        Picasso.with(this).load(getIntent().getExtras().getString("ImageUrl"))
                .into(img_display_image);

    }

}
