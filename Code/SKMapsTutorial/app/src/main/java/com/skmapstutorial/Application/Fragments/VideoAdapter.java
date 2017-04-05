package com.skmapstutorial.Application.Fragments;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.skmapstutorial.R;

import java.util.ArrayList;

/**
 * Created by mkrao on 3/28/2017.
 */

public class VideoAdapter extends BaseAdapter {
    private ArrayList<String>  listData;
    private LayoutInflater layoutInflater;
Context context;
    public VideoAdapter(Context context, ArrayList<String> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
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
            convertView = layoutInflater.inflate(R.layout.video_layout_list_item, null);
            holder.videoView = (VideoView) convertView.findViewById(R.id.video_in_adapter);
            String videoFile = listData.get(position);
            holder.videoView.setId(position * 100);
            // Setting MediaController and URI, then starting the videoView
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.videoView );
            final Uri uri = Uri.parse(videoFile);
            holder.videoView .setMediaController(mediaController);
            holder.videoView .setVideoURI(uri);

            holder.videoView .requestFocus();
            holder.videoView .setLayoutParams(new LinearLayout.LayoutParams(550, 550));
            holder.videoView .setZOrderOnTop(true);
            holder.videoView .setBackgroundResource(R.drawable.media_play);
            holder.videoView .setClickable(true);
            holder.videoView .setId(position * 100);
            holder.videoView .seekTo(1000);
            holder.videoView .pause();

           // holder.videoView= (listData.get(position));
            System.out.println("setting videos in videoview in adapter");
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    static class ViewHolder {

        VideoView videoView;
    }



}
