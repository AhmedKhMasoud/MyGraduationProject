package com.ArabProgrammers.CollegeProject.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ArabProgrammers.CollegeProject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button b_send_reset_link, b_new_account , b_current_account;
    private EditText et_email;
    private ProgressBar m_progress_bar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();

        //initialize views
        b_send_reset_link = findViewById(R.id.b_send_reset_link);
        b_new_account = findViewById(R.id.b_new_account);
        b_current_account = findViewById(R.id.b_current_account);

        et_email = findViewById(R.id.et_email);

        m_progress_bar = findViewById(R.id.m_progress_bar);


        //enter the information to make verify button visible
        et_email.addTextChangedListener(m_watcher);


        //for opening the login again
        b_current_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLogInActivity();

            }
        });

        //for opening the sign up again
        b_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSignUpActivity();

            }
        });

        //for opening the sign up again
        b_send_reset_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyCodeAndOpenHomeActivity();

            }
        });


    }

    //to make verify button visible
    private TextWatcher m_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String email = et_email.getText().toString().trim();

            if(!email.isEmpty()){

                b_send_reset_link.setEnabled(true);
                b_send_reset_link.setTextColor(getResources().getColor(R.color.black_text_color));

            }else{

                b_send_reset_link.setEnabled(false);
                b_send_reset_link.setTextColor(getResources().getColor(R.color.white_text_color));

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //when user click on current account it take him to log in activity
    private void openLogInActivity(){

        Intent i = new Intent(this , LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }

    //when user click on new account it take him to log in activity
    private void openSignUpActivity(){

        Intent i = new Intent(this , SignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }

    //to check if the code is true or false and open home activity
    private void verifyCodeAndOpenHomeActivity(){

        m_progress_bar.setVisibility(View.VISIBLE);

        String email = et_email.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            m_progress_bar.setVisibility(View.GONE);
            et_email.setError("email address is not valid");
            et_email.requestFocus();
            return;
        }


        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        m_progress_bar.setVisibility(View.GONE);

                        if(task.isSuccessful()){

                            Toast.makeText(ForgotPasswordActivity.this
                                    , "please check your mail messages"
                                    , Toast.LENGTH_LONG).show();

                            Intent i = new Intent(ForgotPasswordActivity.this , LogInActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        m_progress_bar.setVisibility(View.GONE);
                        Toast.makeText(ForgotPasswordActivity.this
                                , e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }

}
