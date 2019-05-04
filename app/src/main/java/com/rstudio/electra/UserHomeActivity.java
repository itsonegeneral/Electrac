package com.rstudio.electra;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;
import com.rstudio.electra.Fragments.RequestFragment;
import com.rstudio.electra.Fragments.UserHomeFragment;

public class UserHomeActivity extends AppCompatActivity {

    BottomNavigationView navView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mAuth = FirebaseAuth.getInstance();
        navView = findViewById(R.id.navView);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragmentUserHome, new UserHomeFragment()).commit();
        navView.setOnNavigationItemSelectedListener(listener);

        Toolbar toolbar = findViewById(R.id.tb_userHome);
        setSupportActionBar(toolbar);
        TextView tv = findViewById(R.id.tv_toolbarTitle);
        getSupportActionBar().setTitle("");
        tv.setText("Consumer Dashboard");

        getPermissions();

    }

    private void getPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            switch (menuItem.getItemId()) {

                case R.id.menu_home: {
                    fragment = new UserHomeFragment();
                    break;
                }
                case R.id.menu_requests: {
                    fragment = new RequestFragment();
                    break;
                }default: fragment = new UserHomeFragment();
            }
            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragmentUserHome, fragment).commit();
            }catch (NullPointerException e){
                ;
            }
            return true;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your Location , App Will not Work", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history: {
                startActivity(new Intent(UserHomeActivity.this,UserSendHistory.class));
                break;
            }
            case R.id.menu_logoout :{
                mAuth.signOut();
                finish();
                startActivity(new Intent(UserHomeActivity.this,UserPhoneVerificationActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
