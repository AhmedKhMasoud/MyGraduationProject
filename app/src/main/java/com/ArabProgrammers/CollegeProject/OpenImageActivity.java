package com.ArabProgrammers.CollegeProject;

import android.content.Context;
import android.content.Intent;

public class OpenImageActivity {

    private Context context;
    private String imageUrl;

    public OpenImageActivity() {
    }

    public OpenImageActivity(Context context, String imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    public void displayImage(){

        Intent i = new Intent(context , DisplayImageActivity.class);
        i.putExtra("ImageUrl" , imageUrl);
        context.startActivity(i);

    }
}
