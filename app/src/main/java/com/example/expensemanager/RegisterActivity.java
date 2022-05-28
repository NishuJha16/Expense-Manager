package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {
    EditText firstNameEt,lastNameEt,emailEt,passwordEt,confirm_passwordEt,phoneEt;
    TextView loginBtn;
    MaterialButton signupBtn;
    FirebaseAuth fAuth;
    //private DatabaseReference userRef;
    TextView errorTv, successTv ;
    ImageView cancelAlert, cancelAlertSuccess;
    CardView cardViewAlertError, cardViewAlertSuccess;
    ProgressBar progressBar;
    CheckBox checkBox;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstNameEt=findViewById(R.id.firstname);
        lastNameEt=findViewById(R.id.lastname);
        emailEt=findViewById(R.id.email);
        passwordEt=findViewById(R.id.password);
        confirm_passwordEt=findViewById(R.id.confirm_password);
        loginBtn=findViewById(R.id.loginBtn);
        signupBtn=findViewById(R.id.signupbtn);
        checkBox=findViewById(R.id.checkBox);
        errorTv=findViewById(R.id.errorTv);
        successTv =findViewById(R.id.successTv);
        cardViewAlertError=findViewById(R.id.cardViewAlertError);
        cardViewAlertSuccess=findViewById(R.id.cardViewSuccess);
        cancelAlert=findViewById(R.id.cancelAlert);
        cancelAlertSuccess=findViewById(R.id.cancelAlertSuccess);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() ){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        cancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAlertError.setVisibility(View.GONE);
            }
        });

        cancelAlertSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAlertSuccess.setVisibility(View.GONE);
            }
        });


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEt.getText().toString().trim();
                final String password = passwordEt.getText().toString().trim();
                final String confirm_password = confirm_passwordEt.getText().toString().trim();
                final String firstName = firstNameEt.getText().toString();
                final String lastName    = lastNameEt.getText().toString();

                if(TextUtils.isEmpty(email)){
                    emailEt.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    passwordEt.setError("Password is Required.");
                    return;
                }
                if(TextUtils.isEmpty(confirm_password)){
                    confirm_passwordEt.setError("Confirm Password is Required.");
                    return;
                }


                if(password.length() < 6){
                    passwordEt.setError("Password Must be >= 6 Characters");
                    return;
                }

                if(!(password.equals(confirm_password)))
                {
                    errorTv.setText("Both Passwords should match");
                    cardViewAlertError.setVisibility(View.VISIBLE);
                    return;
                }
                if(!checkBox.isChecked())
                {
                    errorTv.setText("Please agree to the Terms and Conditions");
                    cardViewAlertError.setVisibility(View.VISIBLE);
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link

                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    successTv.setText("Verification Email Has been Sent. Please check your mail");
                                    cardViewAlertSuccess.setVisibility(View.VISIBLE);
                                    cardViewAlertError.setVisibility(View.GONE);
                                    userID = fAuth.getCurrentUser().getUid();
                                    final Handler handler=new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    },4000);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                                    errorTv.setText("Email not sent ! " + e.getMessage());
                                    cardViewAlertError.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });



                        }else {
                            errorTv.setText("Error ! " + task.getException().getMessage());
                            cardViewAlertError.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }

}
