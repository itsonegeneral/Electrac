package com.rstudio.electra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class BoardNewNotificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private EditText etTitle,etMessage;
    private Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_new_notification);
        setToolbar();
        setValues();

    }

    private void setValues(){
        etMessage =findViewById(R.id.et_newNotificationMessage);
        etTitle = findViewById(R.id.et_newNotificationTitle);
        btSend = findViewById(R.id.bt_sendNewNotification);
    }
    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.tb_newNotificationBoard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Create New Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
