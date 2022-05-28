package com.example.expensemanager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends AppCompatActivity {
    private TextView signupBtn, google_tv,forgot_password,msg;
    private EditText usernameEt, passwordEt;
    private Button loginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    CardView cardViewAlertError;
    ImageView cancelAlert;
    public static final int RC_SIGN_IN = 12;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        initUI();
        cancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAlertError.setVisibility(View.GONE);
            }
        });
        if(fAuth.getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() ){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = usernameEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    usernameEt.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    passwordEt.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    passwordEt.setError("Password Must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        if(task.isSuccessful()){
                            if(user.isEmailVerified())
                            {
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                            else {
                                FirebaseAuth.getInstance().signOut();
                                msg.setText("Please Verify you e-mail...");
                                msg.setTextSize(15);
                                cardViewAlertError.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }else {
                            msg.setText(task.getException().getMessage());
                            msg.setTextSize(15);
                            cardViewAlertError.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });



        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email-id To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                msg.setText("Reset Link Sent To Your Email.");
                                cardViewAlertError.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                msg.setText("Reset Link Sent To Your Email."+e.getMessage());
                                cardViewAlertError.setVisibility(View.VISIBLE);
                                //Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });






    }


    private void initUI() {
        signupBtn = findViewById(R.id.signUp);
        usernameEt = findViewById(R.id.usernamelogin);
        passwordEt = findViewById(R.id.passwordlogin);
        loginBtn = findViewById(R.id.loginBtn);
        fAuth = FirebaseAuth.getInstance();
        msg=findViewById(R.id.msg);
        forgot_password=findViewById(R.id.textView);
        progressBar=findViewById(R.id.progressBar);
        cancelAlert=findViewById(R.id.cancelAlert);
        cardViewAlertError=findViewById(R.id.cardViewAlertError);
    }


    public void signUpActivity(View view) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        finish();
    }
}