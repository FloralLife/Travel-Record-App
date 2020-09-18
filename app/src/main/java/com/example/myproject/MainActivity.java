package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private static String TAG = "TAG";
    Intent intent;

    //키해시를 얻어옵니다.
    public static String getKeyHash(final Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        if (packageInfo == null) return null;
        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            Log.d(TAG, "Key hash is " + getKeyHash(this));
        } catch (PackageManager.NameNotFoundException ex) {
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, selectedFragment).commit();

        intent = new Intent(this, AddActivity.class);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    //하단 탭을 누르면 실행되는 결과입니다.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new ListFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new MarkerFragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new CustomFragment();
                    break;
            }
            //화면이 클릭한 탭에 해당되는 프래그먼트로 교체됩니다.
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment, selectedFragment).commit();
            return true;
        }


    };
    //추가하는 액티비티로 넘어갑니다.
    public void addMarker(View view) {
        startActivity(intent);
    }
}