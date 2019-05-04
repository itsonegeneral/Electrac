package com.rstudio.electra;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoardRegistrationActivity extends AppCompatActivity {

    private EditText etPhone, etSectionId, etEmail, etPass1, etPass2;
    private Spinner spDistrict, spDivision;
    private ProgressDialog pgDialog;
    private Button btRegister;
    private String email, pass1, pass2, phone, sectionId, division, district;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_registration);
        setValues();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    pgDialog.setMessage("Please Wait");
                    pgDialog.show();
                    mAuth.signInWithEmailAndPassword(email, pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pgDialog.dismiss();
                            if (task.isSuccessful()) {
                                validateAdmin();
                            } else {
                                Toast.makeText(BoardRegistrationActivity.this, "Failed 503 ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pgDialog.dismiss();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void validateAdmin() {
        pgDialog.setMessage("Validating Admin...");
        pgDialog.show();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("adminvalidation")
                .child(district).child(division).child(sectionId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pgDialog.dismiss();
                if (dataSnapshot.exists()) {
                    boolean isPresent = (boolean) dataSnapshot.getValue();
                    if (isPresent) {
                        Toast.makeText(BoardRegistrationActivity.this, "Board With Section Id is Already Present", Toast.LENGTH_LONG).show();
                    } else {
                        ref.setValue(true);
                        uploadBoardDetails();
                    }
                } else {
                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(BoardRegistrationActivity.this, "Deleted User", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed Error 34", Toast.LENGTH_SHORT).show();
                pgDialog.dismiss();
            }
        });
    }

    private void uploadBoardDetails() {
        Board board = new Board();
        board.setDistrict(district);
        board.setDivision(division);
        board.setSectionId(Integer.valueOf(sectionId));
        board.setPhone(Long.valueOf(phone));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins").child(district).child(division);
        pgDialog.setMessage("Uploading Details");
        pgDialog.show();
        ref.push().setValue(board).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pgDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(BoardRegistrationActivity.this, "Upload Data Complete", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BoardRegistrationActivity.this, "Upload Data Failed , Register Again", Toast.LENGTH_SHORT).show();
                    mAuth.getCurrentUser().delete();
                }
            }
        });


    }

    private void setValues() {
        etPhone = findViewById(R.id.et_registerBoardPhone);
        etEmail = findViewById(R.id.et_registerBoardEmail);
        etSectionId = findViewById(R.id.et_registerBoardSectionId);
        etPass1 = findViewById(R.id.et_registerBoardPass1);
        etPass2 = findViewById(R.id.et_registerBoardPass2);
        spDistrict = findViewById(R.id.spinner_DistrictNewBoard);
        spDistrict.setPrompt("Select District");
        spDivision = findViewById(R.id.spinner_DivisionNewBoard);
        spDivision.setPrompt("Select Division");
        pgDialog = new ProgressDialog(this);
        btRegister = findViewById(R.id.bt_registerBoard);
        Toolbar toolbar = findViewById(R.id.tb_boardRegistration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Board Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        setSpinner();

    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.district_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrict.setAdapter(adapter);


        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_tvm, android.R.layout.simple_spinner_dropdown_item);
        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(tadapter);
        spDivision.setPrompt("Select Division");

        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_kollam, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDivision.setAdapter(tadapter);
                        spDivision.setPrompt("Select Division");
                        break;
                    }
                    case 0: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_tvm, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDivision.setAdapter(tadapter);
                        spDivision.setPrompt("Select Division");
                        break;
                    }
                    case 2: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_alpy, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDivision.setAdapter(tadapter);
                        spDivision.setPrompt("Select Division");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private boolean checkInput() {
        email = etEmail.getText().toString();
        pass1 = etPass1.getText().toString();
        pass2 = etPass2.getText().toString();
        district = spDistrict.getSelectedItem().toString();
        division = spDistrict.getSelectedItem().toString();
        sectionId = etSectionId.getText().toString();
        phone = etPhone.getText().toString();
        if (phone.length() < 10) {
            etPhone.setError("Invalid Phone");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            etEmail.setError("Enter Valid Email");
        } else if (pass1.isEmpty() || pass1.length() < 6) {
            etPass1.setError("Min 6 Char");
        } else if (!pass2.equals(pass1)) {
            etPass2.setError("Passwords Donot Match");
        } else if (sectionId.isEmpty() || sectionId.length() < 4) {
            etSectionId.setError("Invalid Section ID");
        } else {
            return true;
        }
        return false;
    }
}
