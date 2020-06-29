package com.ArabProgrammers.CollegeProject.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ArabProgrammers.CollegeProject.Fragment_EditProfile.EditProfileFragment;
import com.ArabProgrammers.CollegeProject.Fragment_Bookmark.BookMarkFragment;
import com.ArabProgrammers.CollegeProject.Fragment_Home.HomeFragment;
import com.ArabProgrammers.CollegeProject.Fragment_Profile.ProfileFragment;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.TestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;
import static com.ArabProgrammers.CollegeProject.MainActivities.SplashScreenActivity.switchPreferences;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String HOME_TAG = "HOME";
    private final String PROFILE_TAG = "PROFILE";
    private final String BOOKMARK_TAG = "BOOKMARK";
    private final String EDIT_PROFILE_TAG = "EDIT_PROFILE";

    private ImageView img_show_popup_menu;
    private ProgressBar m_progress_bar;
    private TextView txt_app_language;

    private PopupWindow m_popup_window;

    private FirebaseAuth mAuth;
    private DatabaseReference m_users_database_ref;
    private FirebaseUser current_user;

    private LocationManager locationManager;
    private UserLocationListener userLocation ;

    public static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION

            , Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int REQUEST_CODE_PERMISSION = 123;


    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;


    //public static SharedPreferences switchPreferences;
    //public static SharedPreferences appLangPreferences ;


    private ImageView img_myProfile , img_myHome , img_myBookmark;
    private CardView card_profile, card_home, card_bookmark;

    String language_key ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to exit app when the internet is off
        if(getIntent().getBooleanExtra("EXIT" , false)){
            finish();
        }

        // get language state
        appLangPreferences = getSharedPreferences("AppLang" , 0);

        String key = appLangPreferences.getString("Language_Key" , "ar");
        setAppLang(key);

        Configuration conf = getResources().getConfiguration();
        conf.setLayoutDirection(new Locale(key));

        // shared Preferences to save switch button
        switchPreferences = getSharedPreferences("THEME" , MODE_PRIVATE);
        // get switch state
        boolean darkMode = switchPreferences.getBoolean("IsChecked" , false);
        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);


        language_key = appLangPreferences.getString("Language_Key" , "ar");

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();

        m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users");

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        //initialize views
        img_show_popup_menu = findViewById(R.id.img_show_popup_menu);

        txt_app_language = findViewById(R.id.txt_app_language);

        img_myProfile = findViewById(R.id.img_myProfile);
        img_myHome = findViewById(R.id.img_myHome);
        img_myBookmark = findViewById(R.id.img_myBookmark);

        card_profile = findViewById(R.id.card_profile);
        card_home = findViewById(R.id.card_home);
        card_bookmark = findViewById(R.id.card_bookmark);

        m_progress_bar = findViewById(R.id.m_progress_bar);

        //to get user location
        checkPermission();


        //for when selecting item in bottom navigation view
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_container , new HomeFragment() , HOME_TAG );
        fragmentTransaction.commit();



        // to show popup menu
        openPopUpWindow();

        img_show_popup_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // m_popup_window.showAsDropDown(v , 70 , 20);

                m_popup_window.showAsDropDown(v , 15 , 7);


            }
        });


        // to change app language
        txt_app_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //openLanguageActivity();
                changeAppLang();

            }
        });


        // on click for home button
        card_profile.setOnClickListener(this);
        card_home.setOnClickListener(this);
        card_bookmark.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {

        fragment = fragmentManager.findFragmentById(R.id.frame_layout_container);

        if(fragment != null  && !fragment.getTag().equals(HOME_TAG)){

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_container , new HomeFragment() , HOME_TAG);
            fragmentTransaction.commit();

            card_home.setCardBackgroundColor(getResources().getColor(R.color.color_press));
            img_myHome.setImageResource(R.drawable.home_icon_home_show);

            card_profile.setCardBackgroundColor(getResources().getColor(R.color.color_show));
            img_myProfile.setImageResource(R.drawable.home_icon_profile_press);

            card_bookmark.setCardBackgroundColor(getResources().getColor(R.color.color_show));
            img_myBookmark.setImageResource(R.drawable.home_icon_bookmark_press);

        }
        else {

            super.onBackPressed();

        }

    }

    //to open popup window
    private void openPopUpWindow(){

        TextView txt_edit_profile , txt_sign_out;

        Switch m_switch;

        View v = getLayoutInflater().inflate(R.layout.custom_pop_up_window , null);

        //initialize view in custom pop up
        txt_edit_profile = v.findViewById(R.id.txt_edit_profile);
        txt_sign_out = v.findViewById(R.id.txt_sign_out);

        m_switch = v.findViewById(R.id.m_switch);



        // to check if user has check switch button or not
        boolean switchState = switchPreferences.getBoolean("IsChecked" , false);

        if(switchState){

            m_switch.setChecked(true);

        }else {

            m_switch.setChecked(false);
        }





        m_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                changeAppTheme(isChecked);

            }
        });




        txt_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openEditProfileFragment();

            }
        });

        txt_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });


        m_popup_window = new PopupWindow(v , LinearLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT , true);

    }


    // to check if user enable dark mode or not
    private void changeAppTheme(boolean checked){

        if(checked){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveSwitchState(true);

        }else{

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveSwitchState(false);

        }

        Intent refresh = new Intent(HomeActivity.this , HomeActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refresh);
        finish();


    }


    private void saveSwitchState(boolean isChecked){

        SharedPreferences.Editor editor = switchPreferences.edit();
        editor.putBoolean("IsChecked" , isChecked);
        editor.apply();
        editor.commit();

    }

    ////////////////////////////////////////////////
    ///////////////////////////////////////////////

    // to open test activity
    private void openLanguageActivity(){

        Intent i = new Intent(HomeActivity.this , TestActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        m_popup_window.dismiss();

    }

    // to open profile fragment
    private void openEditProfileFragment(){

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_container , new EditProfileFragment() , EDIT_PROFILE_TAG);
        fragmentTransaction.commit();

        card_home.setCardBackgroundColor(getResources().getColor(R.color.color_show));
        img_myHome.setImageResource(R.drawable.home_icon_home_press);

        card_profile.setCardBackgroundColor(getResources().getColor(R.color.color_show));
        img_myProfile.setImageResource(R.drawable.home_icon_profile_press);

        card_bookmark.setCardBackgroundColor(getResources().getColor(R.color.color_show));
        img_myBookmark.setImageResource(R.drawable.home_icon_bookmark_press);

        m_popup_window.dismiss();


    }


    // to sign out and open log in activity
    private void signOut() {


        mAuth.signOut();

        FirebaseUser current_user = mAuth.getCurrentUser();

        current_user = null;

        Intent x = new Intent(HomeActivity.this, LogInActivity.class);

        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(x);
        finish();

        m_popup_window.dismiss();


    }


    //for check permissions
    private void checkPermission(){

        //check if permission is granted or not
        if(ContextCompat.checkSelfPermission(getApplicationContext() , PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext() , PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {

            //to get user location
            checkIfUserHasAlreadyTakeLocationOrNot();

        }
        //called if the permission not granted
        else {

            //for asking for permission
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSION);

            }


        }

    }

    //this method called when permission is accepted or denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSION) {

            for (int i = 0; i < permissions.length; i++) {

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this, getString(R.string.permission_accepted), Toast.LENGTH_SHORT).show();
                    checkIfUserHasAlreadyTakeLocationOrNot();

                } else {

                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();

                    //for asking for permission
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                        requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSION);
                        //checkIfUserHasAlreadyTakeLocationOrNot();


                    }



                }

            }

        }

    }

    //to check if the user has already take his location or not
    private void checkIfUserHasAlreadyTakeLocationOrNot(){

        m_users_database_ref.child(language_key).child(current_user.getUid())
                .child("userLocationLatitude")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() != null){

                            double value = dataSnapshot.getValue(double.class);

                            if(value == 0.0){

                                getCurrentUserLocation();

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    //for getting user location longitude and latitude
    public void getCurrentUserLocation() {


        if(ContextCompat.checkSelfPermission(getApplicationContext() , PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext() , PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {


            m_progress_bar.setVisibility(View.VISIBLE);

            //to get user location
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //check if gps is enabled or not
            boolean gps_provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            String searchWay = "";

            if(gps_provider){

                searchWay = LocationManager.GPS_PROVIDER;

            }else {

                searchWay = LocationManager.NETWORK_PROVIDER;
            }



            userLocation = new UserLocationListener();

            locationManager.requestSingleUpdate(searchWay , userLocation , null);
            //locationManager.requestLocationUpdates(searchWay
              //      , 0 , 0 ,userLocation );

        }

    }


    public class UserLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            //locationManager.removeUpdates(userLocation);
            uploadCurrentUserLocation(location.getLatitude() , location.getLongitude());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    //to save the user location on firebase


    public void uploadCurrentUserLocation(final double latitude, final double longitude) {

        m_users_database_ref.child(language_key).child(current_user.getUid())
                .child("userLocationLatitude")
                .setValue(latitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            m_users_database_ref.child(language_key).child(current_user.getUid())
                                    .child("userLocationLongitude")
                                    .setValue(longitude)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            /*if(language_key.equals("ar")){

                                                language_key = "en";

                                            }else {

                                                language_key = "ar";
                                            }


                                            // save data in other language
                                            m_users_database_ref.child(language_key).child(current_user.getUid())
                                                    .child("userLocationLatitude")
                                                    .setValue(latitude)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                m_users_database_ref.child(language_key).child(current_user.getUid())
                                                                        .child("userLocationLongitude").setValue(longitude)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                if (task.isSuccessful()) {

                                                                                    m_progress_bar.setVisibility(View.GONE);

                                                                                    Toast.makeText(HomeActivity.this, "تم تحديث الموقع بنجاح"
                                                                                            , Toast.LENGTH_SHORT).show();


                                                                                }

                                                                            }
                                                                        });


                                                            }

                                                        }
                                                    });
*/

                                            uploadCurrentUserLocationInOtherLanguage(latitude , longitude);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            m_progress_bar.setVisibility(View.GONE);

                                            Toast.makeText(HomeActivity.this, "failed to update location\n"
                                                    + e.getMessage(), Toast.LENGTH_SHORT).show();


                                        }
                                    });

                        } else {

                            m_progress_bar.setVisibility(View.GONE);

                        }

                    }
                });

    }


    // to save data in other language
    private void uploadCurrentUserLocationInOtherLanguage(final double latitude, final double longitude) {

        if (language_key.equals("ar")) {

            language_key = "en";

        } else {

            language_key = "ar";
        }


        // save data in other language
        m_users_database_ref.child(language_key).child(current_user.getUid())
                .child("userLocationLatitude")
                .setValue(latitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            m_users_database_ref.child(language_key).child(current_user.getUid())
                                    .child("userLocationLongitude").setValue(longitude)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()) {

                                                m_progress_bar.setVisibility(View.GONE);

                                                Toast.makeText(HomeActivity.this, "تم تحديث الموقع بنجاح"
                                                        , Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });


                        }

                    }
                });


    }


    // to change app language
    private void changeAppLang(){
        String key = appLangPreferences.getString("Language_Key" , "ar");

        if(key.equals("ar")){

            key = "en";

        }
        else if(key.equals("en")){

            key = "ar";

        }

        Toast.makeText(this, "تم تغيير اللغه بنجاح", Toast.LENGTH_SHORT).show();


        SharedPreferences.Editor editor = appLangPreferences.edit();
        editor.putString("Language_Key" , key);
        editor.apply();
        editor.commit();

        setAppLang(key);

        Intent refresh = new Intent(this , HomeActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refresh);
        finish();
    }

    private void setAppLang(String languageKey){

        Locale mLocal = new Locale(languageKey);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration conf = getResources().getConfiguration();

        conf.locale = mLocal;
        getResources().updateConfiguration(conf , metrics);


    }


    @Override
    public void onClick(View v) {

        String tag ="";

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()){

            case R.id.card_profile :

                fragment = new ProfileFragment();

                tag = PROFILE_TAG;

                card_profile.setCardBackgroundColor(getResources().getColor(R.color.color_press));
                img_myProfile.setImageResource(R.drawable.home_icon_profile_show);

                card_home.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myHome.setImageResource(R.drawable.home_icon_home_press);

                card_bookmark.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myBookmark.setImageResource(R.drawable.home_icon_bookmark_press);

                break;

            case R.id.card_home :

                fragment = new HomeFragment();

                tag = HOME_TAG;

                card_home.setCardBackgroundColor(getResources().getColor(R.color.color_press));
                img_myHome.setImageResource(R.drawable.home_icon_home_show);

                card_profile.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myProfile.setImageResource(R.drawable.home_icon_profile_press);

                card_bookmark.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myBookmark.setImageResource(R.drawable.home_icon_bookmark_press);

                break;

            case R.id.card_bookmark :

                fragment = new BookMarkFragment();

                tag = BOOKMARK_TAG;

                card_bookmark.setCardBackgroundColor(getResources().getColor(R.color.color_press));
                img_myBookmark.setImageResource(R.drawable.home_icon_bookmark_show);

                card_profile.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myProfile.setImageResource(R.drawable.home_icon_profile_press);

                card_home.setCardBackgroundColor(getResources().getColor(R.color.color_show));
                img_myHome.setImageResource(R.drawable.home_icon_home_press);

                break;


        }


        fragmentTransaction.replace(R.id.frame_layout_container , fragment , tag);
        fragmentTransaction.commit();


    }



}
