package com.ArabProgrammers.CollegeProject.MainActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ArabProgrammers.CollegeProject.NetworkConnectionBroadcastReceiver;
import com.ArabProgrammers.CollegeProject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreenActivity extends AppCompatActivity {


    private FirebaseUser current_user;

    private TextView txt_app_text;
    private ImageView img_app_logo;

    public static SharedPreferences switchPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // shared Preferences to save switch button
        switchPreferences = getSharedPreferences("THEME", MODE_PRIVATE);
        boolean darkMode = switchPreferences.getBoolean("IsChecked", false);

        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash_screen);


        // to enable broadcast receiver
        enableBroadcast();

        //initialize views
        img_app_logo = findViewById(R.id.img_app_logo);
        txt_app_text = findViewById(R.id.txt_app_text);


        YoYo.with(Techniques.ZoomInUp)
                .duration(3000)
                .playOn(img_app_logo);

        YoYo.with(Techniques.FadeOut)
                .duration(3000)
                .playOn(txt_app_text);

        /*YoYo.with(Techniques.RotateInUpLeft)
                .duration(3000)
                .playOn(img_app_logo);

        YoYo.with(Techniques.SlideInUp)
                .duration(3000)
                .playOn(txt_app_text);*/


        current_user = FirebaseAuth.getInstance().getCurrentUser();

        if(isNetworkAvailable()){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(3000);


                        if (current_user == null) {

                            Intent i = new Intent(SplashScreenActivity.this, LogInActivity.class);
                            startActivity(i);
                            finish();

                        } else {

                            Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();

                        }




                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        }else{

            createAlert();
        }



    }


    private void createAlert(){
        new AlertDialog.Builder(SplashScreenActivity.this)
                .setTitle(getString(R.string.internet_connections))
                .setMessage(getString(R.string.please_check_your_internet_connection))
                .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();


                    }
                })
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // set on broadcast receiver
    private void enableBroadcast() {

        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, NetworkConnectionBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_LONG).show();

    }

}
