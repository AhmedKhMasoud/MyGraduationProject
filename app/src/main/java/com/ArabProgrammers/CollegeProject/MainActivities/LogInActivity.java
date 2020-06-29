package com.ArabProgrammers.CollegeProject.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ArabProgrammers.CollegeProject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Locale;


public class LogInActivity extends AppCompatActivity {

    private Button b_log_in, b_current_account, b_new_account;
    private EditText et_email, et_password;
    private TextView txt_forget_password;
    private ProgressBar m_progress_bar;

    private TextView txt_app_language;

    private FirebaseAuth mAuth;


    public static SharedPreferences appLangPreferences ;

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

        // for change the layout direction
        Configuration conf = getResources().getConfiguration();
        conf.setLayoutDirection(new Locale(key));


        setContentView(R.layout.activity_log_in);

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();

        //initialize views
        m_progress_bar = findViewById(R.id.m_progress_bar);

        b_log_in = findViewById(R.id.b_log_in);
        b_current_account = findViewById(R.id.b_current_account);
        b_new_account = findViewById(R.id.b_new_account);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        txt_forget_password = findViewById(R.id.txt_forget_password);

        txt_app_language = findViewById(R.id.txt_app_language);


        //for setting button log in enable
        et_email.addTextChangedListener(m_watcher);
        et_password.addTextChangedListener(m_watcher);

        //when user click sign up button
        b_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSignUpActivity();

            }
        });

        //when user click log in button
        b_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIntoHomeActivity();

            }
        });

        //called when user forget password
        txt_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openForgotPasswordActivity();

            }
        });


        // to change app language
        txt_app_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // openLanguageActivity();
                changeAppLang();

            }
        });


    }


    // to change app language
    private void changeAppLang() {
        String key = appLangPreferences.getString("Language_Key", "ar");

        if (key.equals("ar")) {

            key = "en";

        } else if (key.equals("en")) {

            key = "ar";

        }

        Toast.makeText(this, "تم تغيير اللغه بنجاح", Toast.LENGTH_SHORT).show();


        SharedPreferences.Editor editor = appLangPreferences.edit();
        editor.putString("Language_Key", key);
        editor.apply();
        editor.commit();

        setAppLang(key);

        Intent refresh = new Intent(this, LogInActivity.class);
        startActivity(refresh);
        finish();
    }

    private void setAppLang(String languageKey) {

        Locale mLocal = new Locale(languageKey);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration conf = getResources().getConfiguration();

        conf.locale = mLocal;
        getResources().updateConfiguration(conf, metrics);


    }


    //to check if edit text is empty or not
    private TextWatcher m_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String email = et_email.getText().toString();
            String password = et_password.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {

                b_log_in.setEnabled(true);
                b_log_in.setTextColor(getResources().getColor(R.color.black));
            } else {

                b_log_in.setEnabled(false);
                b_log_in.setTextColor(getResources().getColor(R.color.white_text_color));
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //to open sign up activity
    private void openSignUpActivity() {

        startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
    }

    //when user sign in to open home activity
    private void signIntoHomeActivity() {

        m_progress_bar.setVisibility(View.VISIBLE);

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            m_progress_bar.setVisibility(View.GONE);
            et_email.setError("email address is not valid");
            et_email.requestFocus();
            return;
        }


        if (password.length() < 6) {

            m_progress_bar.setVisibility(View.GONE);
            et_password.setError("password should be more 6 character");
            et_password.requestFocus();
            return;
        }


        logInWithEmailandPasswordAuthentication(email, password);

    }

    //to log in withe phone
    private void logInWithEmailandPasswordAuthentication(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Intent i = new Intent(LogInActivity.this, HomeActivity.class);

                        SystemClock.sleep(1000);

                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();


                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        m_progress_bar.setVisibility(View.GONE);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {

                            et_password.setError("password is invalid");
                            et_password.requestFocus();
                            return;
                        }

                        if (e instanceof FirebaseAuthInvalidUserException) {

                            et_email.setError("this e-mail does not exist or has been disabled");
                            et_email.requestFocus();
                            return;
                        }

                        Toast.makeText(LogInActivity.this, "cant sign in >>>>" +
                                e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }


    //when user forget password this method called to let him get new one
    private void openForgotPasswordActivity() {

        startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class));
    }


}
