package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MarkerFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView = null;
    private GoogleMap mMap;
    private List<ListViewItem> listViewItem = new ArrayList<>();
    private List<LatLng> latLng = new ArrayList<>();
    Realm realm;

    public MarkerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marker, container, false);
        mapView = (MapView)view.findViewById(R.id.map);
        mapView.getMapAsync(this);

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
        if(mapView != null){
            mapView.onCreate(savedInstanceState);
        }
        //realm을 초기화합니다.
        Realm.init(getContext());
        //realm에 대한 설정정보를 설정합니다.
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm=Realm.getInstance(configuration);
        //저장된 목록을 불러옵니다.
        RealmHelper helper = new RealmHelper(realm);
        listViewItem=helper.retrive();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37,126)));

        //불러온 목록을 읽고 좌표정보만 뽑아내고 좌표에 해당하는 마커들을 찍습니다.
        //마커옵션에 zIndex라는 옵션을 이용해서 리스트뷰의 position 역할을 하게 하였습니다.
        //zIndex옵션은 여러 마커가 있을 때 zIndex가 큰 마커를 위쪽에 그리는 옵션입니다.
        for(int i=0;i<listViewItem.size();i++){
            LatLng l = new LatLng(listViewItem.get(i).getLatitude(),
                    listViewItem.get(i).getLongitude());
            latLng.add(l);
            mMap.addMarker(new MarkerOptions().position(l).title(listViewItem.get(i).getTitle()).
                    snippet(listViewItem.get(i).getContent()).zIndex((float)i));
        }

        //마커를 클릭하면 클릭한 마커의 정보를 볼 수 있는 DetailActivity를 실행합니다.
        //클릭한 마커의 zIndex를 position 정보로 간주하고 인텐트에 추가하고 보냅니다.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int)marker.getZIndex();
                Intent intent = new Intent(getContext(),DetailActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
                return false;
            }
        });
    }
}
