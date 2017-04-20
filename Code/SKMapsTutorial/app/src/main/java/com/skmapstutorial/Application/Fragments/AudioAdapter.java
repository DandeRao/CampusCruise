package com.skmapstutorial.Application.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skmapstutorial.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mkrao on 3/28/2017.
 */

public class AudioAdapter extends BaseAdapter{
    private ArrayList<File>  listData;
    private LayoutInflater layoutInflater;

    public AudioAdapter(Context context, ArrayList<File> listData) {
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
            convertView = layoutInflater.inflate(R.layout.audio_layout_list_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.audio_list_view_image_view);
            holder.audioFileName = (TextView) convertView.findViewById(R.id.audio_list_view_text_box) ;
            holder.imageView.setImageResource(R.drawable.media_play);
            holder.audioFileName.setText(listData.get(position).getName());



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }



    static class ViewHolder {

        ImageView imageView;
        TextView audioFileName;
    }



}
