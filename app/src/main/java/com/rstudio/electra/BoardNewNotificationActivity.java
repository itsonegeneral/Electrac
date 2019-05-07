package com.rstudio.electra;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class BoardNewNotificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private EditText etTitle, etMessage;
    private String district,division;
    private Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_new_notification);
        setToolbar();
        setValues();

        district= getIntent().getStringExtra("district");
        division = getIntent().getStringExtra("division");

        ref = FirebaseDatabase.getInstance().getReference("boardnotifications").child(district).child(division);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    String title = etTitle.getText().toString();
                    String message = etMessage.getText().toString();
                    Date c = Calendar.getInstance().getTime();
                    String date = c.toString();

                    Request request = new Request();
                    request.setStatus("Not Available");
                    request.setSenderMessage(message);
                    request.setRequesttype(title);
                    request.setSpecifyMoreAvailable(true);
                    request.setTime(date);

                    ref.push().setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BoardNewNotificationActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(BoardNewNotificationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private boolean checkInput(){
        if(etTitle.getText().toString().isEmpty()){
            etTitle.setError("Enter Title");
        }else if(etMessage.getText().toString().isEmpty()){
            etMessage.setError("Enter Message");
        }else{
            return true;
        }
        return false;
    }
    private void setValues() {
        etMessage = findViewById(R.id.et_newNotificationMessage);
        etTitle = findViewById(R.id.et_newNotificationTitle);
        btSend = findViewById(R.id.bt_sendNewNotification);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_newNotificationBoard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Create New Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
