package com.skmapstutorial.Application.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.VideoView;

import com.skmapstutorial.R;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Videos extends Fragment {
    int numberOfImageFIles;

    boolean hasVIdeo;
    String fileName;
    int numberOfVideoFIles;
    ListView lv;
    ArrayList<String> listItems = new ArrayList<String>();
    VideoAdapter adapter;
    VideoView videoView;
    LinearLayout videoLayout;

    public Videos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_videos, container, false);
        fileName = ((Gallery) getActivity()).getBuildignName().replace(" ", "");

        lv = (ListView) v.findViewById(R.id.video_list_view);
        adapter = new VideoAdapter(((Gallery) getActivity()),
                listItems);

        videoLayout = (LinearLayout) v.findViewById(R.id.video_slider);
        // Inflate the layout for this fragment
        try {
            numberOfVideoFIles = (new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Video")).listFiles().length;
        } catch (NullPointerException e) {
            numberOfVideoFIles = 0;
        }


        hasVIdeo = numberOfVideoFIles > 0 ? true : false;


        System.out.println(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + " has video ? " + hasVIdeo);
        System.out.println("NUmber of videos: " + numberOfVideoFIles);
        if (hasVIdeo) {
            System.out.println("Has videos");
            for (int i = 1; i <= numberOfVideoFIles; i++) {
                System.out.println("Entered For loop to loop on files");


                //  final LinearLayout videoWrapper = new LinearLayout(getActivity());

//                videoView = new VideoView(getActivity());
//                // Creating MediaController
//                MediaController mediaController = new MediaController(getActivity());
//                mediaController.setAnchorView(videoView);
//
//                //specify the location of media file
            String videoFile = getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Video/" + i + ".mp4";
//                System.out.println("Video File Name :" + videoFile);
//                final Uri uri = Uri.parse(videoFile);
//                videoView.setId(i * 100);
//                // Setting MediaController and URI, then starting the videoView
//                videoView.setMediaController(mediaController);
//                videoView.setVideoURI(uri);
//
//                videoView.requestFocus();
//                videoView.setLayoutParams(new LinearLayout.LayoutParams(550, 550));
//                videoView.setZOrderOnTop(true);
//                videoView.setBackgroundResource(R.drawable.media_play);
//                videoView.setClickable(true);
//                videoView.setId(i * 100);
//                videoView.seekTo(1000);
//                videoView.pause();
                listItems.add(videoFile);
//                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    public void onCompletion(MediaPlayer mp) {
//
//                        videoView.setVideoURI(uri);
//                    }
//                });
//               // videoView.start();
//                videoWrapper.addView(videoView);
//                videoLayout.addView(videoWrapper);
//
//                videoView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        playVideo(v);
//                    }
//                });
//
//            }
//        }
//        else {
//            displayNoFiles(v);
            }
            lv.setAdapter(adapter);
        }
        else{

           ImageView iv= (ImageView) v.findViewById(R.id.no_video_place_holder);
            iv.setImageResource(R.drawable.novideo);
            iv.setVisibility(View.VISIBLE);
            lv.setVisibility(GONE);

        }

        return v;
    }


    public void playVideo(View v) {

        System.out.println("Entered For loop to loop on files");
        videoView = (VideoView) v;
        if (videoView.isPlaying()) {

            videoView.pause();
            videoView.setBackgroundResource(R.drawable.media_play);
        } else {
            videoView.setBackground(null);
            videoView.start();
        }
        //videoLayout.addView(videoView);


    }

    public void displayNoFiles(View v) {
        // ((ImageView) v.findViewById(R.id.no_video_place_holder)).setImageResource(R.drawable.novideo);

    }


}
