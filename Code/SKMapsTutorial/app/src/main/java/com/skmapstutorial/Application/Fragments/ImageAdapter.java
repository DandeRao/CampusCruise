package com.skmapstutorial.Application.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.skmapstutorial.R;

import java.util.ArrayList;

/**
 * Created by mkrao on 3/28/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private ArrayList<Bitmap>  listData;
    private LayoutInflater layoutInflater;

    public ImageAdapter(Context context, ArrayList<Bitmap> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.image_layout_list_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_in_adapter);

                        holder.imageView.setImageBitmap(listData.get(position));




            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    static class ViewHolder {

        ImageView imageView;
    }



}
