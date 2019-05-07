package com.rstudio.electra;

import android.content.Intent;
import android.provider.ContactsContract;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReadUserDetails extends AppCompatActivity {

    private EditText etEmail, etConsumerNo, etName, etAddress, etPhone;
    private Spinner sp_district, sp_division;
    private ProgressBar pgBar;
    private Button btSubmit;
    private String name, district, address, email, t, division;
    private long consumerno;
    private long phone;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_user_details);

        setToolbar();
        setValues();
        setSpinner();

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                address = etAddress.getText().toString();
                district = sp_district.getSelectedItem().toString();
                division = sp_division.getSelectedItem().toString();
                email = etEmail.getText().toString();
                t = etConsumerNo.getText().toString();
                phone = Long.valueOf(etPhone.getText().toString());
                consumerno = Long.valueOf(t);
                if (checkUserInput()) {
                    pgBar.setVisibility(View.VISIBLE);
                    submitDetails();
                }
            }
        });
    }

    private boolean checkUserInput() {

        if (name.isEmpty()) {
            etName.setError("Enter Name");
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter Valid Email");
        } else if (address.isEmpty()) {
            etAddress.setError("Enter Address");
        } else if (t.length() < 5) {
            etConsumerNo.setError("Required");
        } else if (etPhone.getText().toString().length() < 10) {
            etPhone.setError("Phone Req");
        } else if(etConsumerNo.getText().toString().length() <12) {
            etConsumerNo.setError("12 Digit Consumer No");
        }else{
            return true;
        }
        return false;
    }

    private void submitDetails() {
        Consumer consumer = new Consumer();
        consumer.setAddress(address);
        consumer.setName(name);
        consumer.setDistrict(district);
        consumer.setDivision(division);
        consumer.setEmail(email);
        consumer.setConsumerno(consumerno);
        consumer.setPhone(phone);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(mAuth.getUid()).setValue(consumer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pgBar.setVisibility(View.GONE);
                    startActivity(new Intent(ReadUserDetails.this, UserHomeActivity.class));
                    Toast.makeText(ReadUserDetails.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                } else {
                    pgBar.setVisibility(View.GONE);
                    Toast.makeText(ReadUserDetails.this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.district_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_district.setAdapter(adapter);

        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_tvm, android.R.layout.simple_spinner_dropdown_item);
        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_division.setAdapter(tadapter);
        sp_division.setPrompt("Select Division");

        sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_kollam, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_division.setAdapter(tadapter);
                        sp_division.setPrompt("Select Division");
                        break;
                    }
                    case 0: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_tvm, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_division.setAdapter(tadapter);
                        sp_division.setPrompt("Select Division");
                        break;
                    }
                    case 2: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_alpy, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_division.setAdapter(tadapter);
                        sp_division.setPrompt("Select Division");
                    }
                    case 3: {
                        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.division_array_pta, android.R.layout.simple_spinner_dropdown_item);
                        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_division.setAdapter(tadapter);
                        sp_division.setPrompt("Select Division");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setValues() {
        etEmail = findViewById(R.id.et_newUserEmail);
        etConsumerNo = findViewById(R.id.et_newUserConsumerNo);
        etName = findViewById(R.id.et_newUserName);
        etAddress = findViewById(R.id.et_newUserAddress);
        sp_district = findViewById(R.id.spinner_NewUserDistrict);
        sp_division = findViewById(R.id.spinner_NewUserBoard);
        btSubmit = findViewById(R.id.bt_submitUserDetails);
        pgBar = findViewById(R.id.pgBar_readUserDetails);
        mAuth = FirebaseAuth.getInstance();
        etPhone = findViewById(R.id.et_newUserPhone);


    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_readUserDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Complete Registration");
    }
}
