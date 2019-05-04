package com.rstudio.electra;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BoardHomeActivity extends AppCompatActivity {

    private TextView tvSectionId, tvDistrict, tvDivision, tvPhone;
    private NotificationAdapter mAdaptor;
    private ArrayList<Request> notificationList;
    private String UserID, district, division, sectionId;
    private ProgressBar pgBar;
    String onesignalid;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_home);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setValues();
        loadBoardData();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void loadBoardData() {
        UserID = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins").child(FirebaseAuth.getInstance().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Board board = dataSnapshot.getValue(Board.class);
                    district = board.getDistrict();
                    division = board.getDivision();
                    sectionId = String.valueOf(board.getSectionId());
                    loadCurrentNotifications(district, division);
                    updateOnesignal();

                    tvDivision.setText(division);
                    tvDistrict.setText(district);
                    tvPhone.setText(String.valueOf(board.getPhone()));
                    tvSectionId.setText(sectionId);

                } else {
                    Toast.makeText(BoardHomeActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCurrentNotifications(final String district, String board) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificationtoboard").child(district)
                .child(board);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pgBar.setVisibility(View.GONE);
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
                        return o2.getTime().compareTo(o1.getTime());
                    }
                });
                mAdaptor = new NotificationAdapter(notificationList, getApplicationContext());
                RecyclerView recyclerView = findViewById(R.id.rView_complaintBoardHome);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(mAdaptor);

                if(mAdaptor.getItemCount() ==0){
                    RelativeLayout rl  = findViewById(R.id.rl_empty);
                    rl.setVisibility(View.VISIBLE);
                }

                mAdaptor.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent i = new Intent(BoardHomeActivity.this, ViewRequestActivity.class);
                        Request req = notificationList.get(position);
                        i.putExtra("phone", req.getSenderPhone());
                        i.putExtra("name", req.getSenderName());
                        i.putExtra("hasmap", req.isMapAvailable());
                        i.putExtra("hasmore", req.isSpecifyMoreAvailable());
                        i.putExtra("senderid", req.getSenderId());
                        i.putExtra("senderconsumerid", req.getSenderconsumerno());
                        i.putExtra("consumerid", req.getConsumerno());
                        i.putExtra("requestid", req.getRequestId());
                        i.putExtra("requesttype", req.getRequesttype());
                        i.putExtra("onesignalid", onesignalid);
                        i.putExtra("district", district);
                        i.putExtra("division", division);
                        if (req.isSpecifyMoreAvailable()) {
                            i.putExtra("message", req.getSenderMessage());
                        }
                        if (req.isMapAvailable()) {
                            i.putExtra("lat", req.getPositionX());
                            i.putExtra("log", req.getPositionY());
                        }
                        startActivity(i);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateOnesignal() {
        //Get user ID and update
        //Get User ID
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        onesignalid = status.getSubscriptionStatus().getUserId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("onesignalids");
        ref.child(district).child(division).setValue(onesignalid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history: {
                Intent i = new Intent(BoardHomeActivity.this,BoardSentHistory.class);
                i.putExtra("district",district);
                i.putExtra("division",division);
                startActivity(i);
                break;
            }
            case R.id.menu_logoout: {
                mAuth.signOut();
                finish();
                startActivity(new Intent(BoardHomeActivity.this, BoardLoginActivity.class));
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    private void setValues() {
        tvSectionId = findViewById(R.id.tv_sectionIDBoardHome);
        tvDistrict = findViewById(R.id.tv_districtBoardHome);
        tvDivision = findViewById(R.id.tv_divisionBoardHome);
        tvPhone = findViewById(R.id.tv_phoneBoardHome);
        pgBar = findViewById(R.id.pgBar_boardHome);
        fab = findViewById(R.id.fab_newNotification);
        notificationList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.tb_boardHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        tv.setText("Board Home");
    }
}
