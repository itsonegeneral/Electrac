package com.rstudio.electra.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.rstudio.electra.GPSTracker;
import com.rstudio.electra.R;
import com.rstudio.electra.Request;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment implements LocationListener, OnMapReadyCallback {


    RelativeLayout layout;
    private MapView mapView;
    private GoogleMap map;
    private String consumernostring, address, complaint, remark, playerId, userPlayerId;
    private String district, division;
    private TextView tvLat, tvLong;
    private EditText etConsumerNo, etAddress, etSpecifyMore, etName;
    private RadioGroup rgComplaintType;
    private Button btSend;
    private double currentLatitude = 9.155416;
    private double currentLongitude = 76.72991;
    private ProgressBar pgBar;
    private long consumerno, phone;
    private LinearLayout llMapView;
    private CheckBox cbAddMap;
    private static final String TAG = "RequestFragment";
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("adminids");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean isMapAttached = false, isSpecifyMoreAttached = false;
    GPSTracker gpsTracker;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String onesignalid = "";

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_request, container, false);
        gpsTracker = new GPSTracker(getContext());


        setValues();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        loadLocation();
        loadData();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    sendNotification();

            }
        });

        return layout;
    }


    private void loadData() {
        try {
            preferences = getActivity().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
            consumernostring = preferences.getString("CONSUMERID", "");
            division = preferences.getString("DIVISION", "");
            district = preferences.getString("DISTRICT", "");
            phone = preferences.getLong("PHONE", 0);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("onesignalids").child(district).child(division);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onesignalid = dataSnapshot.getValue(String.class);
                }
                else {
                    Toast.makeText(getContext(), "No Boards Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification() {
        String requestID = UUID.randomUUID().toString();
        String senderID = FirebaseAuth.getInstance().getUid();
        Request request = new Request();
        request.setSenderconsumerno(Long.valueOf(consumernostring));
        String senderNo = etConsumerNo.getText().toString();
        request.setConsumerno(Long.valueOf(senderNo));
        request.setMapAvailable(isMapAttached);
        request.setSpecifyMoreAvailable(isSpecifyMoreAttached);
        request.setRequesttype(complaint);
        request.setSenderId(senderID);
        Date currentTime = Calendar.getInstance().getTime();
        request.setTime(currentTime.toString());
        request.setSenderName(etName.getText().toString());
        request.setSenderPhone(phone);
        request.setStatus("Incomplete");

        if (isMapAttached) {
            request.setPositionX(currentLatitude);
            request.setPositionY(currentLongitude);
        }
        if (isSpecifyMoreAttached) {
            request.setSenderMessage(etSpecifyMore.getText().toString());
        }

        sendNot(onesignalid, "New Complaint Received");

        request.setRequestId(requestID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usernotifications").child(senderID).child(requestID);
        ref.setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        DatabaseReference bref = FirebaseDatabase.getInstance().getReference("notificationtoboard").child(district)
                .child(division).child(requestID);
        bref.setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Complaint Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void loadLocation() {

        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude = gpsTracker.getLongitude();
            String lat = "Lat : " + String.valueOf(currentLatitude);
            String log = "Long : " + String.valueOf(currentLongitude);
            tvLat.setText(lat);
            tvLong.setText(log);
        } else {
            Log.d(TAG, "onCreate: Error");
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "onMapReady: Map Ready");
        LatLng position = new LatLng(currentLatitude, currentLongitude);
        map.addMarker(new MarkerOptions().position(position).title("Current Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        pgBar.setVisibility(View.GONE);
    }

    private boolean checkInput() {
        if (etConsumerNo.getText().toString().isEmpty() || etConsumerNo.getText().toString().length() != 12) {
            etConsumerNo.setError("Enter Valid Consumer No");
        } else if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Enter Address");
        } else if (rgComplaintType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Select complaint type", Toast.LENGTH_SHORT).show();
        } else if (isSpecifyMoreAttached) {
            if (etSpecifyMore.getText().toString().isEmpty()) {
                etSpecifyMore.setError("Cannot be empty");
            }
        } else if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter Name");
        } else {
            return true;

        }
        return false;
    }

    private void sendNot(final String playerID, final String message) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + playerID + "']}"), new OneSignal.PostNotificationResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, "Notification onSuccess: " + response.toString());
                }

                @Override
                public void onFailure(JSONObject response) {
                    Log.d(TAG, "Notification onFailure : " + response.toString());
                }
            });


        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private void setValues() {
        mapView = layout.findViewById(R.id.mapViewUser);
        etConsumerNo = layout.findViewById(R.id.et_consumerNo);
        etAddress = layout.findViewById(R.id.et_address);
        pgBar = layout.findViewById(R.id.pgBar_mapViewUser);
        pgBar.setVisibility(View.VISIBLE);
        tvLat = layout.findViewById(R.id.tv_sendRequestLat);
        tvLong = layout.findViewById(R.id.tv_sendRequestLong);
        btSend = layout.findViewById(R.id.bt_sendRequest);
        rgComplaintType = layout.findViewById(R.id.rg_complaintType);
        etSpecifyMore = layout.findViewById(R.id.et_specifyMore);
        llMapView = layout.findViewById(R.id.ll_mapviewContainer);
        cbAddMap = layout.findViewById(R.id.cb_giveDirection);
        etName = layout.findViewById(R.id.et_consumerName);
        rgComplaintType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb_others: {
                        RadioButton bt = layout.findViewById(checkedId);
                        complaint = bt.getText().toString();
                        Toast.makeText(getContext(), complaint, Toast.LENGTH_SHORT).show();
                        etSpecifyMore.setVisibility(View.VISIBLE);
                        isSpecifyMoreAttached = true;
                        break;
                    }
                    case R.id.rb_noElectricity: {
                        RadioButton bt = layout.findViewById(checkedId);
                        complaint = bt.getText().toString();
                        Toast.makeText(getContext(), complaint, Toast.LENGTH_SHORT).show();
                        etSpecifyMore.setVisibility(View.GONE);
                        isSpecifyMoreAttached = false;
                        break;
                    }
                    case R.id.rb_lowVoltage: {
                        RadioButton bt = layout.findViewById(checkedId);
                        complaint = bt.getText().toString();
                        Toast.makeText(getContext(), complaint, Toast.LENGTH_SHORT).show();
                        etSpecifyMore.setVisibility(View.GONE);
                        isSpecifyMoreAttached = false;
                        break;
                    }
                }

            }
        });


        cbAddMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMapAttached = isChecked;
                if (isChecked) {
                    llMapView.setVisibility(View.VISIBLE);
                } else {
                    llMapView.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}
