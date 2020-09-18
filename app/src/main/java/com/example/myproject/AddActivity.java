package com.example.myproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AddActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Point point;
    EditText editTitle;
    EditText editContent;
    LatLng latLng;
    String picture;
    ImageButton imageButton;
    Uri imageUri;
    Realm realm;
    PickImageHelper pickImageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Realm.init(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageHelper.selectImage(AddActivity.this);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37, 126), 4));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng l) {
                point = mMap.getProjection().toScreenLocation(l);
                latLng = mMap.getProjection().fromScreenLocation(point);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
        //마커가 여러개 찍히기 때문에 찍힌 마커들중 원하는 마커를 고를 수 있도록 마커클릭이벤트를 설정했습니다
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                latLng = marker.getPosition();

                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = pickImageHelper.getPickImageResultUri(this, data);
            imageButton.setImageURI(imageUri);
            picture = imageUri.toString();
        }
    }

    public void post(View view) {
        if (editTitle != null && editContent != null && point != null && imageUri != null) {
            //입력된 값들을 ListViewItem 타입의 변수에 집어넣습니다.
            ListViewItem l = new ListViewItem();
            l.setTitle(editTitle.getText().toString());
            l.setContent(editContent.getText().toString());
            l.setPicture(picture);
            l.setLatitude(latLng.latitude);
            l.setLongitude(latLng.longitude);


            RealmConfiguration configuration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(configuration);
            //값을 넣은 ListViewItem 변수를 realm 데이터에 저장합니다.
            RealmHelper helper = new RealmHelper(realm);
            helper.save(l);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        //사진, 제목, 내용, 위치 정보를 모두 입력하지 않으면 다이얼로그를 띄워 입력하라고 알려줍니다.
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("모든 항복을 입력하세요");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
