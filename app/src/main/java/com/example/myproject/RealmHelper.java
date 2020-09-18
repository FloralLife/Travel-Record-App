package com.example.myproject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {
    Realm realm;

    public  RealmHelper (Realm realm){
        this.realm = realm;
    }

    //데이터베이스에 저장하는 함수입니다.
    public void save(final ListViewItem listViewItem){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ListViewItem l = realm.copyToRealm(listViewItem);
            }
        });
    }

    //데이터베이스에 저장된 ListViewItem 객체를 리스트로 반환합니다.
    public List<ListViewItem> retrive(){
       ArrayList<ListViewItem> l = new ArrayList<>();
        RealmResults<ListViewItem> listViewItems = realm.where(ListViewItem.class).findAll();
        for(ListViewItem v:listViewItems)
        {
            l.add(v);
        }
        return l;
    }
}
