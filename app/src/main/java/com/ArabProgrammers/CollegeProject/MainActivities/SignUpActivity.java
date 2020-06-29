package com.ArabProgrammers.CollegeProject.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadIndexAndName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    public static final String DEFAULT_PROFESSION_TYPE_ONE = "no profession one until now";
    public static final String DEFAULT_PROFESSION_TYPE_TWO = "no profession two until now";
    public static final String DEFAULT_SHOP_TYPE_ONE = "no shop type one until now";
    public static final String DEFAULT_SHOP_TYPE_TWO = "no shop type two until now";
    public static final String DEFAULT_IMAGE = "no image until now";
    public static final String DEFAULT_THUMBNAILS = "no thumbnails until now";
    public static final String DEFAULT_ANOTHER_PHONE_NUMBER = "no another phone number yet";

    private TextView txt_account_type , txt_profession_type , txt_shop_type , txt_country;
    private EditText et_name, et_email , et_password , et_phone;
    private Button b_sign_up , b_current_account;
    private ProgressBar m_progress_bar;

    //for account type
    private String[] account_type_string_list;
    private int Checked_account_type = -1;
    private ArrayList<uploadIndexAndName> selected_list_account_type;

    //for profession type
    private String[] profession_type_string_list;
    private boolean[] Checked_profession_type ;
    private ArrayList<uploadIndexAndName> selected_list_profession_type;

    //for shop type
    private String[] shop_type_string_list;
    private boolean[] checked_shop_type;
    private ArrayList<uploadIndexAndName> selected_list_shop_type;

    //for country
    private String[] country_list;
    private int Checked_country = -1;
    private List<uploadIndexAndName> selected_list_country;

    //initialize firebase
    private FirebaseAuth mAuth;
    private DatabaseReference m_users_database_ref;


    private Map map;
    private String language_key ;
    private boolean isLanguageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        language_key = LogInActivity.appLangPreferences.getString("Language_Key" , "ar");

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();
        m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users");


        //getting string array from string.xml for account type
        account_type_string_list = getResources().getStringArray(R.array.account_type_list);
        selected_list_account_type = new ArrayList<>();

        //getting string array from string.xml for profession type
        profession_type_string_list = getResources().getStringArray(R.array.profession_type_list);
        Checked_profession_type = new boolean[profession_type_string_list.length];
        selected_list_profession_type = new ArrayList<>();

        //getting string array from string.xml for shop type
        shop_type_string_list = getResources().getStringArray(R.array.shop_type_list);
        checked_shop_type = new boolean[shop_type_string_list.length];
        selected_list_shop_type = new ArrayList<>();

        //getting country string array from string.xml
        country_list = getResources().getStringArray(R.array.country_list);
        selected_list_country = new ArrayList<>();

        //initialize views
        m_progress_bar = findViewById(R.id.m_progress_bar);

        txt_account_type = findViewById(R.id.txt_account_type);
        txt_profession_type = findViewById(R.id.txt_profession_type);
        txt_shop_type = findViewById(R.id.txt_shop_type);
        txt_country = findViewById(R.id.txt_country);

        et_name = findViewById(R.id.et_first_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_phone = findViewById(R.id.et_phone);

        b_sign_up = findViewById(R.id.b_sign_up);
        b_current_account = findViewById(R.id.b_current_account);

        //make account type and profession and shop invisible
        txt_account_type.setVisibility(View.GONE);
        txt_profession_type.setVisibility(View.GONE);
        txt_shop_type.setVisibility(View.GONE);


        //for opening the login again
        b_current_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLogInActivity();

            }
        });

        //enter the information to make account type visible
        et_name.addTextChangedListener(m_watcher);
        et_email.addTextChangedListener(m_watcher);
        et_password.addTextChangedListener(m_watcher);
        txt_country.addTextChangedListener(m_watcher);
        et_phone.addTextChangedListener(m_watcher);


        //to get the user shop type
        txt_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCountryAlertDialog();

            }
        });


        //to get the user account type
        txt_account_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAccountTypeAlertDialog();

            }
        });

        //to get the user profession type
        txt_profession_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfessionTypeAlertDialog();

            }
        });

        //to get the user shop type
        txt_shop_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showShopTypeAlertDialog();

            }
        });

        //to sign up with phone authentication
        b_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                signUpWithEmail_Password();

            }
        });

    }


    //to check is edit text is empty or not
    //to make account type visible
    private TextWatcher m_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String first_name = et_name.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String phone = et_phone.getText().toString().trim();


            if (!first_name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phone.isEmpty()
            && selected_list_country.size()!=0) {


                txt_account_type.setVisibility(View.VISIBLE);
                txt_account_type.setText(getResources().getString(R.string.account_type));
                Checked_account_type = -1;

                //clear data from profession and shop
                clearProfessionData();
                clearShopData();





            }else{

                txt_account_type.setVisibility(View.GONE);
                txt_profession_type.setVisibility(View.GONE);
                txt_shop_type.setVisibility(View.GONE);

                b_sign_up.setEnabled(false);
                b_sign_up.setTextColor(getResources().getColor(R.color.black_text_color));
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    //to show alert dialog to choose the country   >> done
    private void showCountryAlertDialog(){

        selected_list_country.clear();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignUpActivity.this);

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

                selected_list_country.clear();

                Checked_country = which;
                selected_list_country.add(new uploadIndexAndName(country_list[which] , which));

            }
        });

        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(selected_list_country.size() != 0){

                    txt_country.setText(selected_list_country.get(0).getArrName());
                    txt_country.setTextColor(getResources().getColor(R.color.white_text_color));

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

                txt_country.setText(getResources().getString(R.string.country));
                txt_country.setTextColor(getResources().getColor(R.color.hint_text_color));



            }
        });

        mBuilder.show();

    }

    //to show alert dialog to choose the account type
    private void showAccountTypeAlertDialog(){

        selected_list_account_type.clear();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignUpActivity.this);

        //to change title color
        String titleText = getResources().getString(R.string.account_type);

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

        mBuilder.setSingleChoiceItems(account_type_string_list, Checked_account_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                selected_list_account_type.clear();

                Checked_account_type = which;
                selected_list_account_type.add(new uploadIndexAndName(account_type_string_list[which] , which));

            }
        });

        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(selected_list_account_type.size() != 0){

                    txt_account_type.setText(selected_list_account_type.get(0).getArrName());
                    txt_account_type.setTextColor(getResources().getColor(R.color.white_text_color));

                    //when select client
                    if(Checked_account_type == 0){

                        b_sign_up.setEnabled(true);
                        b_sign_up.setTextColor(getResources().getColor(R.color.black_text_color));

                        txt_profession_type.setVisibility(View.GONE);
                        txt_shop_type.setVisibility(View.GONE);

                        clearProfessionData();
                        clearShopData();

                        //to select profession
                    }else if (Checked_account_type == 1){

                        b_sign_up.setEnabled(false);
                        b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));
                        txt_profession_type.setVisibility(View.VISIBLE);

                        txt_shop_type.setVisibility(View.GONE);

                        clearShopData();


                        //to select shop
                    }else{

                        b_sign_up.setEnabled(false);
                        b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));
                        txt_shop_type.setVisibility(View.VISIBLE);

                        txt_profession_type.setVisibility(View.GONE);

                        clearProfessionData();

                    }


                }else {

                    txt_profession_type.setVisibility(View.GONE);
                    txt_shop_type.setVisibility(View.GONE);

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

                Checked_account_type = -1;

                txt_account_type.setText(getResources().getString(R.string.account_type));
                txt_account_type.setTextColor(getResources().getColor(R.color.hint_text_color));

                txt_profession_type.setVisibility(View.GONE);
                txt_shop_type.setVisibility(View.GONE);

                b_sign_up.setEnabled(false);
                b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));

            }
        });

        mBuilder.show();

    }


    //to show alert dialog to choose the account type
    private void showProfessionTypeAlertDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignUpActivity.this);

        //to change title color
        String titleText = getResources().getString(R.string.profession_type);

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

        mBuilder.setMultiChoiceItems(profession_type_string_list, Checked_profession_type
                , new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if(isChecked ){

                    if(!selected_list_profession_type.contains(profession_type_string_list[which])){


                        if(selected_list_profession_type.size() != 2){

                            selected_list_profession_type.add(new uploadIndexAndName(profession_type_string_list[which] , which));

                        }else{

                            Toast.makeText(SignUpActivity.this, "لا يمكن اضافه المزيد", Toast.LENGTH_SHORT).show();
                            Checked_profession_type[which] = false;
                            return;

                        }


                    }


                }
                else {

                    //for not duplicate values
                    for (int i = 0; i <selected_list_profession_type.size() ; i++) {

                        int index = i;

                        if(selected_list_profession_type.get(i).getArrIndex() == which){

                            selected_list_profession_type.remove(index);

                        }

                        Checked_profession_type[which] = false;
                    }

                }


            }
        });


        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(selected_list_profession_type.size() != 0){

                    txt_profession_type.setTextColor(getResources().getColor(R.color.white_text_color));

                    if(selected_list_profession_type.size() == 1){

                        txt_profession_type.setText(selected_list_profession_type.get(0).getArrName() );

                    }

                    if(selected_list_profession_type.size() == 2){

                        txt_profession_type.setText(selected_list_profession_type.get(0).getArrName()
                                + " , "+ selected_list_profession_type.get(1).getArrName()
                        );

                    }

                    b_sign_up.setEnabled(true);
                    b_sign_up.setTextColor(getResources().getColor(R.color.black_text_color));

                }
                //user didnt choose any job
                else{

                    b_sign_up.setEnabled(false);
                    b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));

                    txt_profession_type.setText(getResources().getString(R.string.profession_type));
                    txt_profession_type.setTextColor(getResources().getColor(R.color.hint_text_color));

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

                for(int i =0 ; i <Checked_profession_type.length ; i++){

                    Checked_profession_type[i] = false;

                }

                txt_profession_type.setText(getResources().getString(R.string.profession_type));
                txt_profession_type.setTextColor(getResources().getColor(R.color.hint_text_color));

                b_sign_up.setEnabled(false);
                b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));

            }
        });

        mBuilder.show();

    }


    //to show alert dialog to choose the shop type
    private void showShopTypeAlertDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignUpActivity.this);

        //to change title color
        String titleText = getResources().getString(R.string.shop_type);

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

        mBuilder.setMultiChoiceItems(shop_type_string_list, checked_shop_type, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if(isChecked ){

                    if(!selected_list_shop_type.contains(shop_type_string_list[which])){


                        if(selected_list_shop_type.size() != 2){

                            selected_list_shop_type.add(new uploadIndexAndName(shop_type_string_list[which] , which));

                        }else{

                            Toast.makeText(SignUpActivity.this, "لا يمكن اضافه المزيد", Toast.LENGTH_SHORT).show();
                            checked_shop_type[which] = false;
                            return;

                        }


                    }


                }
                else {

                    //for not duplicate values
                    for (int i = 0; i <selected_list_shop_type.size() ; i++) {

                        int index = i;

                        if(selected_list_shop_type.get(i).getArrIndex() == which){

                            selected_list_shop_type.remove(index);

                        }

                        checked_shop_type[which] = false;
                    }

                }


            }
        });


        mBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(selected_list_shop_type.size() != 0){

                    txt_shop_type.setTextColor(getResources().getColor(R.color.white_text_color));

                    if(selected_list_shop_type.size() == 1){

                        txt_shop_type.setText(selected_list_shop_type.get(0).getArrName() );

                    }

                    if(selected_list_shop_type.size() == 2){

                        txt_shop_type.setText(selected_list_shop_type.get(0).getArrName() +
                                " , "+ selected_list_shop_type.get(1).getArrName());

                    }

                    b_sign_up.setEnabled(true);
                    b_sign_up.setTextColor(getResources().getColor(R.color.black_text_color));

                }
                //user didnt choose any job
                else{

                    b_sign_up.setEnabled(false);
                    b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));

                    txt_shop_type.setText(getResources().getString(R.string.profession_type));
                    txt_shop_type.setTextColor(getResources().getColor(R.color.hint_text_color));

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

                for(int i =0 ; i <checked_shop_type.length ; i++){

                    checked_shop_type[i] = false;

                }

                txt_shop_type.setText(getResources().getString(R.string.shop_type));
                txt_shop_type.setTextColor(getResources().getColor(R.color.hint_text_color));

                b_sign_up.setEnabled(false);
                b_sign_up.setTextColor(getResources().getColor(R.color.white_text_color));

            }
        });

        mBuilder.show();

    }


    //when user click on current account it take him to log in activity
    private void openLogInActivity(){

        Intent i = new Intent(this , LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }


    //for sign up in firebase
    private void signUpWithEmail_Password(){

        m_progress_bar.setVisibility(View.VISIBLE);

        final String name = et_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();

        //check validty for all edit text
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            m_progress_bar.setVisibility(View.GONE);
            et_email.setError("enter valid email");
            et_email.requestFocus();
            return;

        }

        if(password.length() < 6){

            m_progress_bar.setVisibility(View.GONE);
            et_password.setError("password should be more 6 ");
            et_password.requestFocus();
            return;
        }


        if(phone.length() != 11){

            m_progress_bar.setVisibility(View.GONE);
            et_phone.setError("phone number should be 11 digit");
            et_phone.requestFocus();
            return;

        }



        //now sign up
        mAuth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            String userId = task.getResult().getUser().getUid();

                            //for creating user information
                            createUserData(name ,phone ,userId);



                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {



                        if(e instanceof FirebaseAuthUserCollisionException){

                            m_progress_bar.setVisibility(View.GONE);
                            et_email.setError("this email is already exist");
                            et_email.requestFocus();
                            return;

                        }

                        else {

                            m_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "cant create email >>>>>" +
                                    "" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                });




    }


    // to set data to user in two language
    private void setDataInMap(String name , String phone , final String userId){

        // her we check if the other language is saved or not
        if(isLanguageChanged){

            // to change user account type to other language
            account_type_string_list = getResources().getStringArray(R.array.account_type_list);

            for(int i=0 ; i<selected_list_account_type.size() ; i++){


                int old_index = selected_list_account_type.get(i).getArrIndex();

                selected_list_account_type.remove(i);

                selected_list_account_type.add(i , new uploadIndexAndName(account_type_string_list[old_index] , old_index));

            }

            // to change country to other language
            country_list = getResources().getStringArray(R.array.country_list);

            for(int i=0 ; i<selected_list_country.size() ; i++){


                int old_index = selected_list_country.get(i).getArrIndex();

                selected_list_country.remove(i);

                selected_list_country.add(i , new uploadIndexAndName(country_list[old_index] , old_index));

            }

            // to change profession to the other language
            profession_type_string_list = getResources().getStringArray(R.array.profession_type_list);

            for (int i = 0; i <selected_list_profession_type.size() ; i++) {

                int old_index = selected_list_profession_type.get(i).getArrIndex();

                selected_list_profession_type.remove(i);

                selected_list_profession_type.add(i , new uploadIndexAndName(profession_type_string_list[old_index] , old_index));


            }

            // to change shop to the other language
            shop_type_string_list = getResources().getStringArray(R.array.shop_type_list);

            for (int i = 0; i <selected_list_shop_type.size() ; i++) {

                int old_index = selected_list_shop_type.get(i).getArrIndex();

                selected_list_shop_type.remove(i);

                selected_list_shop_type.add(i , new uploadIndexAndName(shop_type_string_list[old_index] ,old_index));

            }



        }



        map = new HashMap();
        map.put("userId" , userId);
        map.put("userName" , name);
        map.put("userPhoneNumber" , phone);
        map.put("userAnotherPhoneNumber" , DEFAULT_ANOTHER_PHONE_NUMBER);
        map.put("userCountry" , selected_list_country.get(0).getArrName());
        map.put("userImageURL" , DEFAULT_IMAGE);
        map.put("userImageThumbnailsURL" , DEFAULT_THUMBNAILS);
        map.put("userAccountType" , selected_list_account_type.get(0).getArrName());
        map.put("userLocationLatitude" , 0.0 );
        map.put("userLocationLongitude" , 0.0);
        map.put("userTotalRate" , 0.0);

        //when user select snai3y
        //if sign up as snai3y with one job
        if(selected_list_profession_type.size() == 1){

            map.put("userProfessionTypeOne" , selected_list_profession_type.get(0).getArrName());
            map.put("userProfessionTypeTwo" , DEFAULT_PROFESSION_TYPE_TWO);


            //if sign up as snai3y with two job
        }else if(selected_list_profession_type.size() == 2) {

            map.put("userProfessionTypeOne" , selected_list_profession_type.get(0).getArrName());
            map.put("userProfessionTypeTwo" , selected_list_profession_type.get(1).getArrName());

            // if sign up as client
        }else{

            map.put("userProfessionTypeOne" , DEFAULT_PROFESSION_TYPE_ONE);
            map.put("userProfessionTypeTwo" , DEFAULT_PROFESSION_TYPE_TWO);

        }


        //when user select shop
        //if sign up as shop with one shop
        if(selected_list_shop_type.size() == 1){

            map.put("userShopTypeOne" , selected_list_shop_type.get(0).getArrName());
            map.put("userShopTypeTwo" , DEFAULT_SHOP_TYPE_TWO);


            //if sign up as shop with two shop
        }else if(selected_list_shop_type.size() == 2){

            map.put("userShopTypeOne" , selected_list_shop_type.get(0).getArrName());
            map.put("userShopTypeTwo" , selected_list_shop_type.get(1).getArrName());


            // if sign up as client
        }else{

            map.put("userShopTypeOne" , DEFAULT_SHOP_TYPE_ONE);
            map.put("userShopTypeTwo" , DEFAULT_SHOP_TYPE_TWO);

        }


    }

    private void createUserData(final String name, final String phone, final String userId) {

        setDataInMap(name , phone , userId);

        // here we save data in current language user using
        m_users_database_ref.child(language_key).child(userId).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // here we change the data to other language
                        isLanguageChanged = true;
                        changeLang(language_key);
                        setDataInMap(name , phone , userId);

                        m_users_database_ref.child(language_key).child(userId).setValue(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            // now lets set the app language to which is user using again
                                            changeLang(language_key);

                                            m_progress_bar.setVisibility(View.GONE);

                                            Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_successfully)
                                                    , Toast.LENGTH_SHORT).show();

                                            //for go to home activity
                                            Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);

                                        }

                                    }
                                });

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        m_progress_bar.setVisibility(View.GONE);

                        Toast.makeText(SignUpActivity.this,
                                "failed to load user information in fire base >>"
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    // to change the current lang to other lang for saving the user
    // data in two language
    private void changeLang(String lang_key){

        if(lang_key.equals("ar")){

            lang_key = "en";

        }else {

            lang_key = "ar";
        }

        Locale locale = new Locale(lang_key);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Configuration conf = getResources().getConfiguration();

        conf.locale = locale;

        getResources().updateConfiguration(conf , displayMetrics);

        language_key = lang_key;

    }


    //to clear previous data
    private void clearProfessionData(){

        //clear data from profession and shop
        selected_list_profession_type.clear();


        for(int i = 0 ; i< Checked_profession_type.length ; i++){

            Checked_profession_type[i] = false;

        }


        txt_profession_type.setText(getResources().getString(R.string.profession_type));
        txt_profession_type.setTextColor(getResources().getColor(R.color.hint_text_color));



    }

    //to clear shop data
    private void clearShopData(){

        selected_list_shop_type.clear();

        for(int i = 0 ; i< checked_shop_type.length ; i++){

            checked_shop_type[i] = false;

        }

        txt_shop_type.setText(getResources().getString(R.string.shop_type));
        txt_shop_type.setTextColor(getResources().getColor(R.color.hint_text_color));


    }



}
