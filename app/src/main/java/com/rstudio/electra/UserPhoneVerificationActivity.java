package com.rstudio.electra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class UserPhoneVerificationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar pgbar;
    EditText etPhone, etOtp;
    String mVerificationId, mResendToken;
    ImageButton btSumbit;
    Button btVerify;
    TextView tvPhone;
    LinearLayout llEnterPhone, llEnterOTP,adminlogin;
    ProgressDialog pgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_phone_verfication);

        pgDialog = new ProgressDialog(this);
        setValues();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Auto Login ,Please wait... ", Toast.LENGTH_SHORT).show();
            checkAdmin();

        }
        btSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = etPhone.getText().toString();
                if (num.length() == 10) {
                    pgbar.setVisibility(View.VISIBLE);
                    btSumbit.setVisibility(View.GONE);
                    sendVerificationCode(num);
                    tvPhone.setText("+91-" + num);

                } else {
                    etPhone.setError("Enter Valid Phone");
                }

            }
        });

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etOtp.getText().toString();
                if (code.length() > 5) {
                    verifyVerificationCode(code);
                }
            }
        });
    }

    private void setValues() {
        btSumbit = findViewById(R.id.bt_submitPhone);
        etPhone = findViewById(R.id.et_phoneLogin);
        llEnterOTP = findViewById(R.id.ll_enterOtp);
        llEnterPhone = findViewById(R.id.ll_enterPhone);
        pgbar = findViewById(R.id.pgBar_UserLogin);
        etOtp = findViewById(R.id.et_verificationCode);
        btVerify = findViewById(R.id.bt_verifyOtp);
        tvPhone = findViewById(R.id.tv_phoneNumberStart);
        mAuth = FirebaseAuth.getInstance();
adminlogin= findViewById(R.id.ll_adminLoginBt);

        Toolbar toolbar = findViewById(R.id.tb_customerReg);
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Consumer Login");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        adminlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserPhoneVerificationActivity.this,BoardLoginActivity.class));

            }
        });
    }


    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            btSumbit.setVisibility(View.VISIBLE);
            if (code != null) {
                //verifying the code
                verifyVerificationCode(code);
            }
            pgbar.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(UserPhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            pgbar.setVisibility(View.GONE);

            btSumbit.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            pgbar.setVisibility(View.GONE);
            mVerificationId = s;
            // mResendToken = forceResendingToken;
            Toast.makeText(UserPhoneVerificationActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            llEnterOTP.setVisibility(View.VISIBLE);
            llEnterPhone.setVisibility(View.GONE);
            btSumbit.setVisibility(View.VISIBLE);
        }
    };

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(UserPhoneVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            loadData();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                        }
                    }
                });
    }

    private void loadData() {
        pgDialog.setMessage("Auto Login");
        pgDialog.setCancelable(false);
        pgDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pgDialog.dismiss();
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(UserPhoneVerificationActivity.this, UserHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent i = new Intent(UserPhoneVerificationActivity.this, ReadUserDetails.class);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgDialog.dismiss();
            }
        });


    }

    private void checkAdmin() {
        pgDialog.setMessage("Validating User");
        pgDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins").child(mAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pgDialog.dismiss();
                if (dataSnapshot.exists()) {
                    Toast.makeText(UserPhoneVerificationActivity.this, "Admin Account", Toast.LENGTH_SHORT).show();

                } else {
                    loadData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgDialog.dismiss();
            }
        });
    }


}
