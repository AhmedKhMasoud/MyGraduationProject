package com.ArabProgrammers.CollegeProject;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class offlineCapabilities extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
