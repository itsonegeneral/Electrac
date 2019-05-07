package com.rstudio.electra;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BoardSentHistory extends AppCompatActivity {

    private NotificationAdapter mAdaptor;
    private ProgressBar pgbar;
    private ArrayList<Request> notificationList;
    private String district,division;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_sent_history);

        pgbar = findViewById(R.id.pgBar_boardHistory);
        notificationList = new ArrayList<>();
        setToolbar();
        loadData();
        loadRequests();
    }

    private void loadData() {
        district = getIntent().getStringExtra("district");
        division = getIntent().getStringExtra("division");


    }

    private void loadRequests() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("boardnotifications").child(district).child(division);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pgbar.setVisibility(View.GONE);
                notificationList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Request request = snapshot.getValue(Request.class);
                        notificationList.add(request);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
                Collections.sort(notificationList, new Comparator<Request>() {
                    @Override
                    public int compare(Request o1, Request o2) {
                        return o1.getTime().compareTo(o2.getTime());
                    }
                });
                mAdaptor = new NotificationAdapter(notificationList, BoardSentHistory.this);
                RecyclerView recyclerView = findViewById(R.id.rView_boardHistory);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(BoardSentHistory.this));
                recyclerView.setAdapter(mAdaptor);

                if(mAdaptor.getItemCount() ==0){
                    RelativeLayout rl  = findViewById(R.id.rl_empty);
                    rl.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_boardHistory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Board History");
    }
}
