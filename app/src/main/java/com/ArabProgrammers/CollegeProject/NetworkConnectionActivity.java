package com.ArabProgrammers.CollegeProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ArabProgrammers.CollegeProject.MainActivities.HomeActivity;
import com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NetworkConnectionActivity extends AppCompatActivity {

    private ProgressBar m_progress_bar;
    private CardView card_reload;

    private int i = 0;

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection);

        // initialize firebase
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // initialize views
        m_progress_bar = findViewById(R.id.m_progress_bar);

        card_reload = findViewById(R.id.card_reload);

        // disable broadcast for not duplicate
        disableBroadcast();


        // to reload internet connection
        card_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_progress_bar.setVisibility(View.VISIBLE);

                if (isNetworkAvailable()) {

                    Toast.makeText(NetworkConnectionActivity.this, getString(R.string.network_enabled)
                            , Toast.LENGTH_SHORT).show();


                    enableBroadcast();
                    finish();

                    m_progress_bar.setVisibility(View.GONE);

                } else {

                    Toast.makeText(NetworkConnectionActivity.this, getString(R.string.please_check_your_internet_connection)
                            , Toast.LENGTH_SHORT).show();

                    m_progress_bar.setVisibility(View.GONE);
                }


            }
        });


    }

    @Override
    public void onBackPressed() {


        if (i == 0) {

            Toast.makeText(this, getString(R.string.click_twice_to_exit), Toast.LENGTH_SHORT).show();

            i++;

            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int j = 0; j < 5; j++) {

                        SystemClock.sleep(1000);

                    }

                    i = 0;


                }
            });
            thread.start();

        } else {

            Intent exitAllActivity = new Intent();

            if(current_user != null){

                exitAllActivity.setClass(this , HomeActivity.class);

            }else {

                exitAllActivity.setClass(this , LogInActivity.class);
            }


            exitAllActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            exitAllActivity.putExtra("EXIT", true);
            startActivity(exitAllActivity);

            enableBroadcast();

        }


    }

    // to check internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // set on broadcast receiver
    private void enableBroadcast() {

        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(NetworkConnectionActivity.this, NetworkConnectionBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_LONG).show();

    }

    // set off broadcast receiver
    private void disableBroadcast() {

        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(NetworkConnectionActivity.this, NetworkConnectionBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();

    }

}
