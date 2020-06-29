package com.ArabProgrammers.CollegeProject.SearchActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadDistanceByKm;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;

public class SearchActivity extends AppCompatActivity {

    private String search_profession_or_shop;


    private ImageView oops_image;
    private TextView txt_select_country;
    private TextView txt_sort;
    private TextView text_no_users;
    private ProgressBar m_progress_bar;
    private SwipeRefreshLayout m_swipe_refresh;

    //for country
    private String[] country_list;
    private int Checked_country = -1;
    private ArrayList<String> selected_list_country;

    //for sort
    private String[] sort_list;
    private int Checked_sort_type = -1;
    private ArrayList<String> selected_list_sort;

    private DatabaseReference m_users_database_ref;
    private FirebaseUser current_user;

    private RecyclerView m_recycle_view;
    private List<uploadUserData> users_list;
    private List<uploadUserData> arranged_users_list;

    private String[] account_type_string_list;

    private Search_Activity_Adapter search_activity_adapter;

    private LocationManager locationManager;
    private CurrentUserLocationListener locationListener;

    private List<uploadDistanceByKm> km_old_list;


    private boolean isCountrySelected = false;
    private boolean isSortSelected = false;

    String key ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        key = appLangPreferences.getString("Language_Key" , "ar");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        km_old_list = new ArrayList<>();


        //get the search profession or shop
        search_profession_or_shop = getIntent().getExtras().getString("SearchFor");


        account_type_string_list = getResources().getStringArray(R.array.account_type_list);
        users_list = new ArrayList<>();
        arranged_users_list = new ArrayList<>();


        //initialize views
        m_progress_bar = findViewById(R.id.m_progress_bar);
        txt_select_country = findViewById(R.id.txt_select_country);
        txt_sort = findViewById(R.id.txt_sort);
        text_no_users = findViewById(R.id.text_no_users);
        m_swipe_refresh = findViewById(R.id.m_swipe_refresh);

        oops_image = findViewById(R.id.oops_image);

        m_progress_bar.setVisibility(View.VISIBLE);

        // initialize firebase
        m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users").child(key);
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        /////////////////////////////////////////////
        /////////////////////////////////////////////
        //getting country string array from string.xml
        country_list = getResources().getStringArray(R.array.country_list);
        selected_list_country = new ArrayList<>();

        //to select country for searching
        txt_select_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCountryAlertDialog();

            }
        });

        //getting sort string array from string.xml
        sort_list = getResources().getStringArray(R.array.sort_type_list);
        selected_list_sort = new ArrayList<>();

        //to select country for searching
        txt_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSortAlertDialog();

            }
        });

        /////////////////////////////////////////////
        /////////////////////////////////////////////

        //recycle view
        m_recycle_view = findViewById(R.id.m_recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        m_recycle_view.setHasFixedSize(true);
        m_recycle_view.setLayoutManager(layoutManager);

        new Handler().post(new Runnable() {
            @Override
            public void run() {

              startSearchingByLocation();

            }
        });

        //for refresh the page
        m_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                m_progress_bar.setVisibility(View.VISIBLE);
                text_no_users.setVisibility(View.GONE);

                if(!isCountrySelected && !isSortSelected){

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            startSearchingByLocation();

                        }
                    });

                }else {

                    if(isCountrySelected ){

                        if(selected_list_country.size() !=0 ){

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {

                                    startSearchingByCountry(selected_list_country.get(0));

                                }
                            });

                        }

                    }

                    if(isSortSelected ){

                        if(selected_list_sort.size() !=0 ){

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {

                                    checkSearchType(selected_list_sort.get(0));

                                }
                            });

                        }

                    }



                }




            }
        });



    }


    //to show alert dialog to choose the country   >> done
    private void showCountryAlertDialog() {

        selected_list_country.clear();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchActivity.this);

        //to change title color
        String titleText = getResources().getString(R.string.country);

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        mBuilder.setTitle(ssBuilder);
        mBuilder.setCancelable(false);

        mBuilder.setSingleChoiceItems(country_list, Checked_country, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Checked_country = which;
                selected_list_country.add(country_list[which]);

            }
        });

        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (selected_list_country.size() != 0) {

                    txt_select_country.setText(selected_list_country.get(0));
                    txt_select_country.setTextColor(getResources().getColor(R.color.black));

                    //for get search result with country
                    startSearchingByCountry(selected_list_country.get(0));

                    isCountrySelected = true;

                    // to set search by rating null
                    Checked_sort_type = -1;

                    txt_sort.setText(getResources().getString(R.string.sort));
                    txt_sort.setTextColor(getResources().getColor(R.color.hint_text_color));

                    isSortSelected = false;

                }

            }
        });

        mBuilder.setNegativeButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        mBuilder.setNeutralButton(getString(R.string.clear), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Checked_country = -1;

                txt_select_country.setText(getResources().getString(R.string.country));
                txt_select_country.setTextColor(getResources().getColor(R.color.hint_text_color));

                // for better using swipe refreshing
                isCountrySelected = false;
                startSearchingByLocation();


            }
        });

        mBuilder.show();

    }


    //searching by country >>>finished
    private void startSearchingByCountry(final String SelectedCountry){

        m_progress_bar.setVisibility(View.VISIBLE);
        m_recycle_view.setVisibility(View.GONE);

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadUserData userData = data.getValue(uploadUserData.class);

                        if (!userData.getUserId().equals(current_user.getUid()) &&
                                !userData.getUserAccountType().equals(account_type_string_list[0])) {

                            if (userData.getUserProfessionTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserProfessionTypeTwo().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeTwo().equals(search_profession_or_shop)) {


                                if (userData.getUserCountry().equals(SelectedCountry)) {

                                    users_list.add(userData);


                                }


                            }

                        }


                    }

                    if(users_list.size()!=0){

                        m_recycle_view.setVisibility(View.VISIBLE);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.GONE);
                        oops_image.setVisibility(View.GONE);

                        m_swipe_refresh.setRefreshing(false);

                        List<uploadDistanceByKm> arr = new ArrayList<>();

                        search_activity_adapter = new Search_Activity_Adapter(users_list, arr, SearchActivity.this);
                        m_recycle_view.setAdapter(search_activity_adapter);

                    }
                    else {

                        m_recycle_view.setVisibility(View.GONE);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.VISIBLE);
                        oops_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.StandUp)
                                .playOn(text_no_users);

                        YoYo.with(Techniques.RubberBand)
                                .playOn(oops_image);

                        m_swipe_refresh.setRefreshing(false);

                    }



                    //Toast.makeText(SearchActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();


                } else {

                    m_recycle_view.setVisibility(View.GONE);
                    m_progress_bar.setVisibility(View.GONE);

                    text_no_users.setVisibility(View.VISIBLE);
                    oops_image.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.StandUp)
                            .playOn(text_no_users);

                    YoYo.with(Techniques.RubberBand)
                            .playOn(oops_image);

                    m_swipe_refresh.setRefreshing(false);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SearchActivity.this, "no result \n" +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                m_progress_bar.setVisibility(View.GONE);


            }
        });

    }

    // searching by location
    // first step  : take current user location
    // second step : get all users distance
    // third step : compare current location with other users locations
    // continue third step : arrange the users by lower distance
    // fourth step : getting the same users in km list but it not arranged
    // continue fourth step here we arrange users
    // last step : display users
    private void startSearchingByLocation() {

        m_progress_bar.setVisibility(View.VISIBLE);
        m_recycle_view.setVisibility(View.GONE);

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadUserData userData = data.getValue(uploadUserData.class);

                        if (!userData.getUserId().equals(current_user.getUid()) &&
                                !userData.getUserAccountType().equals(account_type_string_list[0])) {

                            if (userData.getUserProfessionTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserProfessionTypeTwo().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeTwo().equals(search_profession_or_shop)) {


                                users_list.add(userData);


                            }


                        }


                    }


                    //check if there is data or not
                    if(users_list.size() != 0){

                        m_recycle_view.setVisibility(View.VISIBLE);
                        getCurrentUserLocation(users_list);

                    }else {

                        m_recycle_view.setVisibility(View.GONE);
                        m_swipe_refresh.setRefreshing(false);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.VISIBLE);
                        oops_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.StandUp)
                                .playOn(text_no_users);

                        YoYo.with(Techniques.RubberBand)
                                .playOn(oops_image);


                    }

                } else {


                    m_recycle_view.setVisibility(View.GONE);
                    m_swipe_refresh.setRefreshing(false);
                    m_progress_bar.setVisibility(View.GONE);

                    text_no_users.setVisibility(View.VISIBLE);
                    oops_image.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.StandUp)
                            .playOn(text_no_users);

                    YoYo.with(Techniques.RubberBand)
                            .playOn(oops_image);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SearchActivity.this, "no result \n" +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                m_progress_bar.setVisibility(View.GONE);
                text_no_users.setVisibility(View.VISIBLE);

            }
        });

    }


    //first step
    private void getCurrentUserLocation(List<uploadUserData> users) {

        locationListener = new CurrentUserLocationListener(users);

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
        }

        boolean gps_provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        String searchWay = "";

        if(gps_provider){

            searchWay = LocationManager.GPS_PROVIDER;

        }else {

            searchWay = LocationManager.NETWORK_PROVIDER;
        }


        locationManager.requestSingleUpdate(searchWay , locationListener , null);
       // locationManager.requestLocationUpdates(searchWay
         //       , 0, 0, locationListener);

    }

    //continue with first step
    private class CurrentUserLocationListener implements LocationListener{

        List<uploadUserData> users;

        CurrentUserLocationListener(List<uploadUserData> users){

            this.users = users;
        }

        @Override
        public void onLocationChanged(Location location) {

            locationManager.removeUpdates(locationListener);
            getUsersDistance(users , location.getLatitude() , location.getLongitude());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

   //second step
    private void getUsersDistance(List<uploadUserData> users , double current_user_latitude
            , double current_user_longitude){


        km_old_list.clear();

        for(int i=0 ; i<users.size() ; i++) {

            int user_distance = calculateDistance(current_user_latitude , users.get(i).getUserLocationLatitude(),
                    current_user_longitude , users.get(i).getUserLocationLongitude());


            km_old_list.add(new uploadDistanceByKm(user_distance , users.get(i).getUserId()));

        }


        Collections.sort(km_old_list, new Comparator<uploadDistanceByKm>() {
            @Override
            public int compare(uploadDistanceByKm o1, uploadDistanceByKm o2) {

                return o1.getKm() - o2.getKm();

            }
        });

        getSearchedUsers(km_old_list);


    }

    //continue with second step
    private int calculateDistance(double lat1, double lat2, double lon1, double lon2) {


        // math تحتوي وحدة
        // toRadians على الدالة
        // والتي تحوّل الزوايا من نظام الدرجات إلى نظام نصف القطر
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // صيغة هافرساين
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));  //sqrt الجذر التربيعي

        // نصف قطر الكرة الأرضية بوحدات الكيلومتر
        double r = 6378.8;

        double distance1 = c * r ;

        /////////////////////////////////////////
        ////////////////////////////////////////
        int finalResult;

        double distance2 = distance1 + 1;

        double sub = distance2 - distance1;

        if(sub >=0.6){

            finalResult = (int) distance2;

        }else{

            finalResult = (int) distance1;

        }

        // حساب النتيجة
        return finalResult;
    }


    // fourth step
    private void getSearchedUsers(final List<uploadDistanceByKm> km_list){

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for(DataSnapshot data : dataSnapshot.getChildren()){

                        uploadUserData userData = data.getValue(uploadUserData.class);

                        for (int i = 0; i <km_list.size() ; i++) {

                            if(km_list.get(i).getUserId().equals(userData.getUserId())){


                                users_list.add(userData);

                            }

                        }


                    }

                    arrangeSearchedUsersAndDisplayResult(users_list , km_list);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    // continue fourth step
    // last step
    private void arrangeSearchedUsersAndDisplayResult(List<uploadUserData> list , List<uploadDistanceByKm> km){

        arranged_users_list.clear();
        for (int i = 0; i <km.size() ; i++) {

            for(int x=0 ; x< list.size(); x++){

                if(km.get(i).getUserId().equals(list.get(x).getUserId())){

                    arranged_users_list.add(list.get(x));
                }

            }

        }

        m_progress_bar.setVisibility(View.GONE);
        text_no_users.setVisibility(View.GONE);
        oops_image.setVisibility(View.GONE);
        m_swipe_refresh.setRefreshing(false);

        search_activity_adapter = new Search_Activity_Adapter(arranged_users_list, km, SearchActivity.this);
        m_recycle_view.setAdapter(search_activity_adapter);



        //Toast.makeText(this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();

    }


    //to show alert dialog to choose the country   >> done
    private void showSortAlertDialog() {

        selected_list_sort.clear();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchActivity.this);

        //to change title color
        String titleText = getResources().getString(R.string.sort);

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        mBuilder.setTitle(ssBuilder);
        mBuilder.setCancelable(false);

        mBuilder.setSingleChoiceItems(sort_list, Checked_sort_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Checked_sort_type = which;
                selected_list_sort.add(sort_list[which]);

            }
        });

        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (selected_list_sort.size() != 0) {


                    //for get search result with heights rate
                    checkSearchType(selected_list_sort.get(0));

                    txt_sort.setText(selected_list_sort.get(0));
                    txt_sort.setTextColor(getResources().getColor(R.color.black));


                    isSortSelected = true;

                }

            }
        });

        mBuilder.setNegativeButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        mBuilder.setNeutralButton(getString(R.string.clear), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Checked_sort_type = -1;

                txt_sort.setText(getResources().getString(R.string.sort));
                txt_sort.setTextColor(getResources().getColor(R.color.hint_text_color));

                // for better using swipe refreshing
                isSortSelected = false;
                startSearchingByLocation();


            }
        });

        mBuilder.show();

    }

    // to get the heights rate users
    private void checkSearchType(String sortType) {

        // here if user select by location
        if (sortType.equals(sort_list[0])) {

            // to check if country chosen or not then set it null
            Checked_country = -1;
            txt_select_country.setText(getResources().getString(R.string.country));
            txt_select_country.setTextColor(getResources().getColor(R.color.hint_text_color));
            isCountrySelected = false;

            startSearchingByLocation();

        }
        // here if user select by rate
        else {

            // check if user select country or not
            // user already select country
            if (selected_list_country.size() != 0) {

                startSearchingByCountryUsingHeightsRate();

            }
            // user did not select country
            else {

                startSearchingByHeightsRate();

            }


        }
    }

    // when check only heights rate without select country
    private void startSearchingByHeightsRate(){

        m_progress_bar.setVisibility(View.VISIBLE);

        m_recycle_view.setVisibility(View.GONE);

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadUserData userData = data.getValue(uploadUserData.class);

                        if (!userData.getUserId().equals(current_user.getUid()) &&
                                !userData.getUserAccountType().equals(account_type_string_list[0])) {

                            if (userData.getUserProfessionTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserProfessionTypeTwo().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeTwo().equals(search_profession_or_shop)) {


                                users_list.add(userData);


                            }


                        }


                    }

                    if(users_list.size() !=0){

                        Collections.sort(users_list, new Comparator<uploadUserData>() {
                            @Override
                            public int compare(uploadUserData o1, uploadUserData o2) {

                                return (int) (o2.getUserTotalRate() - o1.getUserTotalRate());
                            }
                        });


                        m_recycle_view.setVisibility(View.VISIBLE);

                        text_no_users.setVisibility(View.GONE);
                        oops_image.setVisibility(View.GONE);

                        m_progress_bar.setVisibility(View.GONE);

                        m_swipe_refresh.setRefreshing(false);

                        List<uploadDistanceByKm> arr = new ArrayList<>();

                        search_activity_adapter = new Search_Activity_Adapter(users_list, arr, SearchActivity.this);
                        m_recycle_view.setAdapter(search_activity_adapter);


                    }

                    else {

                        m_recycle_view.setVisibility(View.GONE);
                        m_swipe_refresh.setRefreshing(false);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.VISIBLE);
                        oops_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.StandUp)
                                .playOn(text_no_users);

                        YoYo.with(Techniques.RubberBand)
                                .playOn(oops_image);

                    }




                } else {

                    m_recycle_view.setVisibility(View.GONE);
                    m_swipe_refresh.setRefreshing(false);
                    m_progress_bar.setVisibility(View.GONE);

                    text_no_users.setVisibility(View.VISIBLE);
                    oops_image.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.StandUp)
                            .playOn(text_no_users);

                    YoYo.with(Techniques.RubberBand)
                            .playOn(oops_image);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SearchActivity.this, "no result \n" +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                m_progress_bar.setVisibility(View.GONE);
                text_no_users.setVisibility(View.VISIBLE);


            }
        });




    }


    // when check only heights rate with select country
    private void startSearchingByCountryUsingHeightsRate(){

        m_progress_bar.setVisibility(View.VISIBLE);

        m_recycle_view.setVisibility(View.GONE);

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadUserData userData = data.getValue(uploadUserData.class);

                        if (!userData.getUserId().equals(current_user.getUid()) &&
                                !userData.getUserAccountType().equals(account_type_string_list[0])) {

                            if (userData.getUserProfessionTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserProfessionTypeTwo().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeOne().equals(search_profession_or_shop) ||
                                    userData.getUserShopTypeTwo().equals(search_profession_or_shop)) {


                                if (userData.getUserCountry().equals(selected_list_country.get(0))) {

                                    users_list.add(userData);


                                }


                            }

                        }


                    }

                    if(users_list.size() !=0){

                        Collections.sort(users_list, new Comparator<uploadUserData>() {
                            @Override
                            public int compare(uploadUserData o1, uploadUserData o2) {

                                return (int) (o2.getUserTotalRate() - o1.getUserTotalRate());
                            }
                        });

                        m_recycle_view.setVisibility(View.VISIBLE);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.GONE);
                        oops_image.setVisibility(View.GONE);

                        m_swipe_refresh.setRefreshing(false);

                        List<uploadDistanceByKm> arr = new ArrayList<>();

                        search_activity_adapter = new Search_Activity_Adapter(users_list, arr, SearchActivity.this);
                        m_recycle_view.setAdapter(search_activity_adapter);

                        //Toast.makeText(SearchActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();

                    }
                    else {

                        m_recycle_view.setVisibility(View.GONE);
                        m_progress_bar.setVisibility(View.GONE);

                        text_no_users.setVisibility(View.VISIBLE);
                        oops_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.StandUp)
                                .playOn(text_no_users);

                        YoYo.with(Techniques.RubberBand)
                                .playOn(oops_image);

                        m_swipe_refresh.setRefreshing(false);

                    }







                }else {

                    m_recycle_view.setVisibility(View.GONE);
                    m_progress_bar.setVisibility(View.GONE);

                    text_no_users.setVisibility(View.VISIBLE);
                    oops_image.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.StandUp)
                            .playOn(text_no_users);

                    YoYo.with(Techniques.RubberBand)
                            .playOn(oops_image);

                    m_swipe_refresh.setRefreshing(false);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SearchActivity.this, "no result \n" +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                m_progress_bar.setVisibility(View.GONE);


            }
        });



    }

}
