package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ListFragment extends Fragment {

    List<ListViewItem> listViewItems;
    ListAdapter adapter;
    private Realm realm;

    public ListFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        listViewItems = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView);

        //realm을 초기화합니다.
        Realm.init(getContext());
        //realm에 대한 설정정보를 설정합니다.
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm=Realm.getInstance(configuration);

        //데이터에 저장된 목록을 불러와서 리스트뷰에 어댑터로 넣습니다.
        RealmHelper helper = new RealmHelper(realm);
        listViewItems=helper.retrive();
        adapter = new ListAdapter(getActivity(),listViewItems);
        listView.setAdapter(adapter);

        //아이템을 클릭하면 아이템의 정보를 자세히 볼 수 있는 DetailActivity가 실행됩니다.
        //인텐트에 position 정보를 같이 넣어서 보냅니다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        return view;
    }
}