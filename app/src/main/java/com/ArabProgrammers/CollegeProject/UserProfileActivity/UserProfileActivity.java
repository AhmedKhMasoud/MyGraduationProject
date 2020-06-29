package com.ArabProgrammers.CollegeProject.UserProfileActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ArabProgrammers.CollegeProject.OpenImageActivity;
import com.ArabProgrammers.CollegeProject.MainActivities.SignUpActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadJobPic;
import com.ArabProgrammers.CollegeProject.uploadUserRating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;

public class UserProfileActivity extends AppCompatActivity {

    private String userId , userName , userPhone1 , userPhone2 , userImage ;
    private double userRate;

    private CircleImageView profile_image;
    private TextView txt_user_name , text_no_image_yet;
    private TextView txt_user_rate_num;
    private TextView rated_text;
    private ProgressBar m_progress_bar;
    private RecyclerView m_recycle_view;
    private ImageView img_bookmark , img_call , oops_no_image;
    private CardView card_add_rate;

    private List<uploadJobPic> image_list;

    private FirebaseUser current_user;
    private DatabaseReference m_users_job_pic_database_ref;
    private DatabaseReference m_users_bookmark_database_ref;
    private DatabaseReference m_users_rating_database_ref;
    private DatabaseReference m_users_ratedMe_database_ref;
    private DatabaseReference m_users_database_ref;

    private User_Profile_Activity_Adapter user_profile_activity_adapter;

    private Dialog rateDialog;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        key = appLangPreferences.getString("Language_Key" , "ar");


        image_list = new ArrayList<>();

        // get all extras data
        Bundle extras = getIntent().getExtras();

        userId = extras.getString("userId");
        userName = extras.getString("userName");
        userPhone1 = extras.getString("userPhone1");
        userPhone2 = extras.getString("userPhone2");
        userImage = extras.getString("userImage");
        userRate = extras.getDouble("userRate");

        //initialize fire base
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users");

        m_users_job_pic_database_ref = FirebaseDatabase.getInstance().getReference("UsersJobPic")
                .child(userId);


        m_users_bookmark_database_ref = FirebaseDatabase.getInstance()
                .getReference("BookMarks").child(current_user.getUid());

        m_users_rating_database_ref = FirebaseDatabase.getInstance().getReference("Rating");

        m_users_ratedMe_database_ref = FirebaseDatabase.getInstance().getReference("RatedMe").child(userId);


        //initialize views
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_user_rate_num = findViewById(R.id.txt_user_rate_num);
        rated_text = findViewById(R.id.rated_text);

        profile_image = findViewById(R.id.profile_image);

        m_progress_bar = findViewById(R.id.m_progress_bar);

        img_call = findViewById(R.id.img_call);
        img_bookmark = findViewById(R.id.img_bookmark);

        card_add_rate = findViewById(R.id.card_add_rate);

        oops_no_image = findViewById(R.id.oops_no_image);
        text_no_image_yet = findViewById(R.id.text_no_image_yet);

        //initialize recycle view
        m_recycle_view = findViewById(R.id.m_recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this , RecyclerView.VERTICAL , false);

        m_recycle_view.setLayoutManager(layoutManager);

        m_recycle_view.setHasFixedSize(true);

        // for loading user information
        loadUserInformation();


        // to know is user already bookmark or no
        isUserBookmark();


        // for check if current user has already rate this user or not
        isCurrentUserAlreadyRateThisUserOrNot();

        // listener for calling
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED){


                    openPhoneNumber(userPhone1 , userPhone2);


                }else {

                    ActivityCompat.requestPermissions(UserProfileActivity.this
                            ,new String[]{Manifest.permission.CALL_PHONE} , 0);
                }


            }
        });

        // listener for bookmark
        img_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookMarkUser(userId);

            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenImageActivity image = new OpenImageActivity(UserProfileActivity.this , userImage);
                image.displayImage();

            }
        });


        // to add rating to any users
        card_add_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rated_text.getText().equals(getString(R.string.rated))){

                    Toast.makeText(UserProfileActivity.this, "لقد قمت بتقييمه بالفعل سابقا", Toast.LENGTH_SHORT).show();
                }else {

                    openRatingDialog();

                }


            }
        });


    }


    //to load user information
    private void loadUserInformation(){

        m_progress_bar.setVisibility(View.VISIBLE);

        txt_user_name.setText(userName);

        txt_user_rate_num.setText(""+userRate);

        Picasso.with(UserProfileActivity.this).load(userImage)
                .placeholder(R.drawable.default_profile_image)
                .into(profile_image);




        m_users_job_pic_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {

                    for(DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadJobPic image = data.getValue(uploadJobPic.class);

                        image_list.add(image);

                    }

                    if(image_list.size() !=0){

                        text_no_image_yet.setVisibility(View.GONE);
                        oops_no_image.setVisibility(View.GONE);

                        m_progress_bar.setVisibility(View.GONE);

                        m_recycle_view.setVisibility(View.VISIBLE);

                        user_profile_activity_adapter = new User_Profile_Activity_Adapter(image_list , UserProfileActivity.this);
                        m_recycle_view.setAdapter(user_profile_activity_adapter);

                    }else{

                        m_progress_bar.setVisibility(View.GONE);
                        m_recycle_view.setVisibility(View.GONE);

                        text_no_image_yet.setVisibility(View.VISIBLE);
                        oops_no_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.Pulse)
                                .playOn(text_no_image_yet);

                        YoYo.with(Techniques.RotateIn)
                                .playOn(oops_no_image);

                    }





                }else{

                    m_progress_bar.setVisibility(View.GONE);
                    m_recycle_view.setVisibility(View.GONE);

                    text_no_image_yet.setVisibility(View.VISIBLE);
                    oops_no_image.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.Pulse)
                            .playOn(text_no_image_yet);

                    YoYo.with(Techniques.RotateIn)
                            .playOn(oops_no_image);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                m_progress_bar.setVisibility(View.GONE);
                Toast.makeText(UserProfileActivity.this, "failed to read user image\n"+
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    //to call
    private String selected_phone_number ;
    private String second_phone;
    private void openPhoneNumber(final String phone_one , final String phone_two){

        second_phone = phone_two;

        if( second_phone.equals(SignUpActivity.DEFAULT_ANOTHER_PHONE_NUMBER)){

            second_phone = getString(R.string.no_another_phone_number_yet);

        }

        CharSequence[] phones = {phone_one , second_phone};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.calling))
                .setCancelable(false)
                .setItems(phones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {

                            selected_phone_number = phone_one;

                        } else {

                            selected_phone_number = second_phone;


                        }

                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:"+selected_phone_number));
                        startActivity(call);

                    }
                })
                .setNeutralButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create().show();

    }


    //to bookmark user
    private void bookMarkUser(String user) {

        m_users_bookmark_database_ref.child(user).child("BookMark").setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(UserProfileActivity.this, "bookmark successfully"
                                    , Toast.LENGTH_SHORT).show();


                        } else {

                            Toast.makeText(UserProfileActivity.this, "failed to bookmark\n" +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }


    //check if user is already bookmarked or not
    private void isUserBookmark(){

        //check if this user already bookmarked or not
        m_users_bookmark_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null ) {

                    if(dataSnapshot.hasChild(userId)){

                        img_bookmark.setVisibility(View.GONE);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    // check if current user already rate this user or not
    private void isCurrentUserAlreadyRateThisUserOrNot(){

        m_users_ratedMe_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){

                    if (dataSnapshot.hasChild(current_user.getUid())){

                        rated_text.setText(getString(R.string.rated));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //to give user rate
    private void openRatingDialog(){

        rateDialog = new Dialog(this);
        rateDialog.setContentView(R.layout.custom_dialog_rate_user);

        final RatingBar rate = rateDialog.findViewById(R.id.rate);
        Button b_save = rateDialog.findViewById(R.id.b_save);
        Button b_dismiss = rateDialog.findViewById(R.id.b_dismiss);


        rateDialog.setCancelable(false);

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               double rateNum = rate.getRating();

               if(rateNum == 0.0){

                   Toast.makeText(UserProfileActivity.this, "لا يمكن التقييم بصفر", Toast.LENGTH_SHORT).show();

               }
               else {

                   String rate = "";

                   if(rateNum > 0 && rateNum <=1){

                       rate = "usersRatedOneStar";
                       getRatedUsers(rate);

                   }
                   else if(rateNum > 1 && rateNum <=2){

                       rate = "usersRatedTwoStar";
                       getRatedUsers(rate);

                   }
                   else if(rateNum > 2 && rateNum <=3){

                       rate = "usersRatedThreeStar";
                       getRatedUsers(rate);

                   }
                   else if(rateNum > 3 && rateNum <=4){

                       rate = "usersRatedFourStar";
                       getRatedUsers(rate);

                   }
                   else{

                       rate = "usersRatedFiveStar";
                       getRatedUsers(rate);

                   }

                   m_progress_bar.setVisibility(View.VISIBLE);

                   rateDialog.dismiss();

               }


            }
        });

        b_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rateDialog.dismiss();

            }
        });

        rateDialog.show();

    }


    // 1- here we check if current user has already rated from other people or not
    private void getRatedUsers(final String rateStar){

        m_users_rating_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){

                    //Toast.makeText(UserProfileActivity.this, "database", Toast.LENGTH_SHORT).show();
                    rateUser(rateStar);

                }else {

                    //Toast.makeText(UserProfileActivity.this, "no database", Toast.LENGTH_SHORT).show();
                    firstOneRateThisUser(rateStar);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    // already user has been rated from other people
    private void rateUser(final String rateStar){

        m_users_ratedMe_database_ref.child(current_user.getUid()).child("rateMe").setValue("yes")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){


                            m_users_rating_database_ref.child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.getValue() != null) {

                                        uploadUserRating userRating = dataSnapshot.getValue(uploadUserRating.class);


                                        // to get the old users numbers and increess one to them

                                        int newNum = 0;

                                        if(rateStar.equals("usersVotedOneStar")){

                                            int usersVotedOneStar = userRating.getUsersRatedOneStar();

                                            newNum = usersVotedOneStar+1;

                                        }
                                        else if(rateStar.equals("usersVotedTwoStar")){

                                            int usersVotedTwoStar = userRating.getUsersRatedTwoStar();

                                            newNum = usersVotedTwoStar + 1;
                                        }
                                        else if(rateStar.equals("usersVotedThreeStar")){

                                            int usersVotedThreeStar = userRating.getUsersRatedThreeStar();

                                            newNum = usersVotedThreeStar + 1;
                                        }
                                        else if(rateStar.equals("usersVotedFourStar")){

                                            int usersVotedFourStar = userRating.getUsersRatedFourStar();

                                            newNum = usersVotedFourStar + 1;
                                        }
                                        else {

                                            int usersVotedFiveStar = userRating.getUsersRatedFiveStar();

                                            newNum = usersVotedFiveStar + 1;
                                        }


                                        // to update the  total rating
                                        m_users_rating_database_ref.child(userId)
                                                .child(rateStar).setValue(newNum)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                            updateNewValueToTotalRating();


                                                        }

                                                    }
                                                });


                                    }else {
                                        m_progress_bar.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    m_progress_bar.setVisibility(View.GONE);

                                }
                            });


                        }

                    }
                });

    }

    // when you are the first one to rate this user
    private void firstOneRateThisUser(final String rateStar) {

        m_users_ratedMe_database_ref.child(current_user.getUid()).child("rateMe").setValue("yes")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            int zero = 0;

                            Map map = new HashMap();
                            map.put("usersRatedFiveStar" , zero);
                            map.put("usersRatedFourStar" , zero);
                            map.put("usersRatedThreeStar" , zero);
                            map.put("usersRatedTwoStar" , zero);
                            map.put("usersRatedOneStar" , zero);


                            m_users_rating_database_ref.child(userId).setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                m_users_rating_database_ref.child(userId).child(rateStar).setValue(1.0)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                    updateNewValueToTotalRating();

                                                                }else {
                                                                    m_progress_bar.setVisibility(View.GONE);
                                                                }


                                                            }
                                                        });

                                            }else {
                                                m_progress_bar.setVisibility(View.GONE);
                                            }

                                        }
                                    });

                        }else {
                            m_progress_bar.setVisibility(View.GONE);
                        }


                    }
                });


    }

    // to update the new total rating
    private void updateNewValueToTotalRating(){

        // get all values
        m_users_rating_database_ref.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){

                    uploadUserRating userRating = dataSnapshot.getValue(uploadUserRating.class);


                    int usersVotedOneStar = userRating.getUsersRatedOneStar();
                    int usersVotedTwoStar = userRating.getUsersRatedTwoStar();
                    int usersVotedThreeStar = userRating.getUsersRatedThreeStar();
                    int usersVotedFourStar = userRating.getUsersRatedFourStar();
                    int usersVotedFiveStar = userRating.getUsersRatedFiveStar();


                    int users_X_rateSum = (usersVotedFiveStar * 5) + (usersVotedFourStar * 4) + (usersVotedThreeStar * 3) +
                            (usersVotedTwoStar * 2) + (usersVotedOneStar * 1);

                    int usersSum = usersVotedOneStar + usersVotedTwoStar + usersVotedThreeStar +
                            usersVotedFourStar + usersVotedFiveStar;

                    final double total =  (double) users_X_rateSum / usersSum ;


                    m_users_database_ref.child(key).child(userId).child("userTotalRate")
                            .setValue(total)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if(task.isSuccessful()){

                                        if(key.equals("ar")){

                                            key = "en";
                                        }else {
                                            key = "ar";
                                        }

                                        m_users_database_ref.child(key).child(userId).child("userTotalRate")
                                                .setValue(total)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){


                                                            Toast.makeText(UserProfileActivity.this, "تم التقييم بنجاح"
                                                                    , Toast.LENGTH_SHORT).show();


                                                            // to update the new rate in screen
                                                            txt_user_rate_num.setText(""+total);

                                                            m_progress_bar.setVisibility(View.GONE);

                                                        }

                                                    }
                                                });



                                    }

                                }
                            });


                }else {

                    m_progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(UserProfileActivity.this, "failed to rate \n"+
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                m_progress_bar.setVisibility(View.GONE);

            }
        });


    }

}
