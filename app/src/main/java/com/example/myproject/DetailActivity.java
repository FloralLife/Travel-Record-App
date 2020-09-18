package com.example.myproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<ListViewItem> listViewItems = new ArrayList<>();
    String title;
    String content;
    String uriPath;
    LatLng latLng;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //realm을 초기화하고 저장된 정보를 불러옵니다.
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);
        RealmHelper helper = new RealmHelper(realm);
        listViewItems = helper.retrive();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView titleText = findViewById(R.id.titleText);
        TextView contentText = findViewById(R.id.contentText);
        ImageView imageView = findViewById(R.id.eemage);

        //ListFragment와 MarkerFragment에서 인텐트에 추가한 position정보를 받습니다.
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        //받은 position값을 이용해 제목, 내용, 사진을 출력합니다.
        title = listViewItems.get(position).getTitle();
        content = listViewItems.get(position).getContent();
        uriPath = listViewItems.get(position).getPicture();
        latLng = new LatLng(listViewItems.get(position).getLatitude(),
                listViewItems.get(position).getLongitude());

        titleText.setText(title);
        contentText.setText(content);
        imageView.setImageURI(Uri.parse(uriPath));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //받은 position값을 이용해 마커를 표시합니다.
        mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(content));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
    }

    //카카오톡 공유여부를 묻는 다이얼로그를 띄우고 공유을 누르면 카카오링크 위치템플릿을 공유합니다.
    //국내만 확인이 가능합니다.
    public void share(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("카카오톡 공유를 하시겠습니까?\n(국내만 가능합니다)");
        builder.setPositiveButton("공유", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                LocationTemplate params = LocationTemplate
                        .newBuilder(getAddress(latLng.latitude,latLng.longitude,geocoder),
                        ContentObject.newBuilder(title,
                                "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                                LinkObject.newBuilder()
                                        .setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com")
                                        .build())
                                .setDescrption(content)
                                .build())
                        .setAddressTitle(title)
                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${shared_product_id}");

                KakaoLinkService.getInstance()
                        .sendDefault(getApplicationContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {

                    }
                });
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    //삭제여부를 묻는 다이얼로그를 띄우고 삭제를 누르면
    //사진의 경로, 제목, 내용과 일치하는 데이터들을 모두 삭제합니다.
    //(두 파일의 세가지 값이 모두같은 항목은 없다고 판단)
    public void delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final RealmResults<ListViewItem> results = realm.where(ListViewItem.class)
                        .equalTo("picture", uriPath)
                        .equalTo("title", title)
                        .equalTo("content", content)
                        .findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteAllFromRealm();
                    }
                });
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //좌표를 통해 주소를 얻는 함수입니다. 그냥 주소를 추가하면 지역에따라
    //중간에 null값이 들어가서 null값을 골라냈습니다.
    public  String getAddress(double lat, double lng, Geocoder geocoder){
        String address = null;

        List<Address> addresses =null;

        try{
            addresses = geocoder.getFromLocation(lat,lng,1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(addresses == null){
            Log.e("getAddress","주소데이터얻기 실패");
            return null;
        }

        if(addresses.size() > 0){
            Address addr = addresses.get(0);
            String s[]={"","","","",""};

            if(addr.getCountryName() != null){
                s[0]=addr.getCountryName();
            }
            if(addr.getAdminArea() != null){
                s[1]=addr.getAdminArea();
            }
            if(addr.getLocality() != null){
                s[2]=addr.getLocality();
            }
            if(addr.getThoroughfare() != null){
                s[3]=addr.getThoroughfare();
            }
            if(addr.getFeatureName() != null){
                s[4]=addr.getFeatureName();
            }

            address = s[0]+" "+s[1]+" " +s[2]+" " +s[3]+" "+s[4];
        }
        return address;
    }
}

