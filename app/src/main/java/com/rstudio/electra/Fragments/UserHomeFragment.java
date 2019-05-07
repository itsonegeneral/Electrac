package com.rstudio.electra.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rstudio.electra.Consumer;
import com.rstudio.electra.NotificationAdapter;
import com.rstudio.electra.R;
import com.rstudio.electra.Request;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHomeFragment extends Fragment {

    RelativeLayout layout;
    NotificationAdapter mAdaptor;
    ArrayList<Request> notificationList;
    ProgressBar pgbar;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    TextView tvDistrict, tvDivision, tvConsumerId;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public UserHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_user_home, container, false);

        pgbar = layout.findViewById(R.id.pgBar_homeFrag);
        tvDistrict = layout.findViewById(R.id.tv_consumerDistrictHome);
        tvDivision = layout.findViewById(R.id.tv_consumerDivisionHome);
        tvConsumerId = layout.findViewById(R.id.tv_consumerIdUserHome);
        try {

            preferences = getActivity().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
            String cId = preferences.getString("CONSUMERID", "");
            String division = preferences.getString("DIVISION", "");
            String district = preferences.getString("DISTRICT", "");
            tvDivision.setText(division);
            tvConsumerId.setText(cId);
            tvDistrict.setText(district);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        notificationList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Consumer consumer = dataSnapshot.getValue(Consumer.class);
                    String district = consumer.getDistrict();
                    String board = consumer.getDivision();

                    tvConsumerId.setText(String.valueOf(consumer.getConsumerno()));
                    tvDistrict.setText(consumer.getDistrict());
                    tvDivision.setText(consumer.getDivision());
                    //
                    editor = preferences.edit();
                    editor.putString("CONSUMERID", String.valueOf(consumer.getConsumerno()));
                    editor.putString("DIVISION", consumer.getDivision());
                    editor.putString("DISTRICT", consumer.getDistrict());
                    editor.putLong("PHONE",consumer.getPhone());
                    editor.apply();


                    loadCurrentNotifications(district, board);
                } else {
                    pgbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgbar.setVisibility(View.GONE);
            }
        });

        return layout;
    }

    private void loadCurrentNotifications(String district, String board) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("boardnotifications").child(district)
                .child(board);
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
                    Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
                Collections.sort(notificationList, new Comparator<Request>() {
                    @Override
                    public int compare(Request o1, Request o2) {
                        return o1.getTime().compareTo(o2.getTime());
                    }
                });
                mAdaptor = new NotificationAdapter(notificationList, getContext());
                RecyclerView recyclerView = layout.findViewById(R.id.rView_boardNotices);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(mAdaptor);


                if(mAdaptor.getItemCount() ==0){
                    RelativeLayout rl  = layout.findViewById(R.id.rl_empty);
                    rl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
