package com.rstudio.electra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoardLoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private EditText etEmail, etPass;
    private TextView tvNewReg;
    private Button btLogin;
    private ProgressDialog pgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_login);

        btLogin = findViewById(R.id.bt_adminLogin);
        etEmail = findViewById(R.id.et_AdminLoginEmail);
        etPass = findViewById(R.id.et_AdminLoginPassword);
        tvNewReg = findViewById(R.id.tv_boardRegitration);
        mAuth = FirebaseAuth.getInstance();
        pgDialog = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.tb_adminLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Board Login");

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    pgDialog.setMessage("Logging in ");
                    pgDialog.show();
                    String email = etEmail.getText().toString();
                    String pass = etPass.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pgDialog.dismiss();
                            if (task.isSuccessful()) {
                                checkAdmin();
                            } else {
                                Toast.makeText(BoardLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        tvNewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BoardLoginActivity.this, BoardRegistrationActivity.class));
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
                    Toast.makeText(BoardLoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BoardLoginActivity.this, BoardHomeActivity.class));
                } else {
                    Toast.makeText(BoardLoginActivity.this, "Not Found As Admin", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgDialog.dismiss();
            }
        });
    }

    private boolean checkInput() {
        if (etEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError("Enter Valid Email");
        } else if (etPass.getText().toString().length() < 6) {
            etPass.setError("Invalid Password");
        } else {
            return true;
        }
        return false;
    }
}
