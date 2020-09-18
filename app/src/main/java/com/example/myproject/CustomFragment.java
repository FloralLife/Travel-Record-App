package com.example.myproject;

import android.content.ContentUris;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class CustomFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView = null;
    private GoogleMap mMap;
    private List<ListViewItem> listViewItem = new ArrayList<>();
    private List<LatLng> latLng = new ArrayList<>();
    Realm realm;
    TextView positionText;
    TextView titleText;
    TextView contentText;
    TextView addressText;
    ImageView imageView;
    Button next;
    Button back;
    int jungsoo = 0;

    public CustomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);


        positionText = view.findViewById(R.id.position);
        titleText = view.findViewById(R.id.title);
        contentText = view.findViewById(R.id.content);
        addressText = view.findViewById(R.id.address);
        imageView = view.findViewById(R.id.image);


        next = view.findViewById(R.id.nextBtn);
        back = view.findViewById(R.id.backBtn);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //realm을 초기화합니다.
        Realm.init(getContext());
        //realm에 대한 설정정보를 설정합니다.
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);
        //저장된 목록을 불러옵니다.
        RealmHelper helper = new RealmHelper(realm);
        listViewItem = helper.retrive();


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        PolylineOptions polylineOptions = new PolylineOptions();
        //좌표에 해당하는 주소를 얻고싶어서 DetailActivity안의 getAddress()를 사용하였습니다.
        final DetailActivity detailActivity = new DetailActivity();
        final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        //아무정보도 추가하지 않았을 때 상태입니다.
        if (listViewItem.size() == 0) {
            positionText.setText(String.valueOf(0));
            titleText.setText("");
            contentText.setText("");
            addressText.setText("");
            imageView.setImageResource(R.drawable.icon);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37, 126)));
        }
        //저장된 항목이 있을 때 상태입니다.
        else {
            for (int i = 0; i < listViewItem.size(); i++) {
                LatLng l = new LatLng(listViewItem.get(i).getLatitude(),
                        listViewItem.get(i).getLongitude());
                latLng.add(l);
                mMap.addMarker(new MarkerOptions().position(l).title(listViewItem.get(i).getTitle()).
                        snippet(listViewItem.get(i).getContent()).zIndex((float) i));
                polylineOptions.add(l);
            }
            mMap.addPolyline(polylineOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng.get(0), 13));

            positionText.setText(String.valueOf(1));
            titleText.setText(listViewItem.get(0).getTitle());
            contentText.setText(listViewItem.get(0).getContent());
            addressText.setText(detailActivity.getAddress(latLng.get(0).latitude, latLng.get(0).longitude, geocoder));

            imageView.setImageURI(Uri.parse(listViewItem.get(0).getPicture()));

            //다음 항목의 정보를 띄우는 버튼입니다.
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jungsoo++;
                    if (jungsoo == listViewItem.size()) {
                        jungsoo = 0;
                    }
                    positionText.setText(String.valueOf(jungsoo + 1));
                    titleText.setText(listViewItem.get(jungsoo).getTitle());
                    contentText.setText(listViewItem.get(jungsoo).getContent());
                    addressText.setText(detailActivity.getAddress(
                            latLng.get(jungsoo).latitude, latLng.get(jungsoo).longitude, geocoder));
                    imageView.setImageURI(Uri.parse(listViewItem.get(jungsoo).getPicture()));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng.get(jungsoo), 13));

                }
            });
            //이전 항목의 정보를 띄우는 버튼입니다.
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jungsoo--;
                    if (jungsoo == -1) {
                        jungsoo = listViewItem.size() - 1;
                    }
                    positionText.setText(String.valueOf(jungsoo + 1));
                    titleText.setText(listViewItem.get(jungsoo).getTitle());
                    contentText.setText(listViewItem.get(jungsoo).getContent());
                    addressText.setText(detailActivity.getAddress(
                            latLng.get(jungsoo).latitude, latLng.get(jungsoo).longitude, geocoder));
                    imageView.setImageURI(Uri.parse(listViewItem.get(jungsoo).getPicture()));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng.get(jungsoo), 13));
                }
            });
        }
    }
}