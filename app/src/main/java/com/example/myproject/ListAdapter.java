package com.example.myproject;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private List<ListViewItem> listViewItems;
    private Activity context;



    public ListAdapter(Activity activity, List<ListViewItem> list){
        listViewItems = list;
        context = activity;
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            ListViewItem listViewItem = listViewItems.get(position);
            convertView = inflater.inflate(R.layout.layout_list_view_item, parent, false);
            TextView titleText = convertView.findViewById(R.id.title);
            TextView contentText = convertView.findViewById(R.id.content);
            TextView positionText = convertView.findViewById(R.id.position);
            ImageView imageView = convertView.findViewById(R.id.image);

            titleText.setText(listViewItem.getTitle());
            contentText.setText(listViewItem.getContent());
            positionText.setText(String.valueOf(position+1));
            imageView.setImageURI(Uri.parse(listViewItem.getPicture()));

        }
        return convertView;
    }
}
