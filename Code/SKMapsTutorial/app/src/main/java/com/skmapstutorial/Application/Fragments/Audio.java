package com.skmapstutorial.Application.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skmapstutorial.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Audio extends Fragment {

    String fileName;
    int numberOfAudioFiles;
    boolean hasAudio;
    LinearLayout audioImageHolder;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<File> listItems = new ArrayList<>();
    ListView lv;
    View v;
    boolean isPaused = false;
    AudioAdapter audioAdapter;
    View previouslyPlayingImageView ;
    File previouslyPlayingFile;
    public Audio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listItems.clear();
        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        this.v = v;
        fileName = ((Gallery) getActivity()).getBuildignName().replace(" ", "");
        lv = (ListView) v.findViewById(R.id.audio_list_view);

        audioAdapter = new AudioAdapter(getActivity(), listItems);
        // audioImageHolder = (LinearLayout) v.findViewById(R.id.audio_images_holder);
        try {
            numberOfAudioFiles = (new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Audio")).listFiles().length;
        } catch (NullPointerException e) {
            numberOfAudioFiles = 0;
        }
        hasAudio = numberOfAudioFiles > 0 ? true : false;
        if (hasAudio) {
            System.out.println("Has videos");
            for (int i = 1; i <= numberOfAudioFiles; i++) {
                System.out.println("Audio FIles Being Created: "+ i);
                System.out.println("Files in Fragmets: " + getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Audio/" + i + ".3gpp");
                // File imgFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Audio/" + i + ".jpg");

                //  System.out.println("Files in Fragmets: " + getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Images/" + i + ".jpg");
                File audioFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Audio/" + i + ".3gpp");
                listItems.add(audioFile);


                ImageView imageView = new ImageView((Gallery) getActivity());

                imageView.setId(i * 300);
                imageView.setImageResource(R.drawable.media_play);



            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    try {
                        if (previouslyPlayingFile == null) {
                            showStop(v);
                            intializeAndRunMediaFIles(listItems.get(position));
                        }
                        else{

                            if(previouslyPlayingFile == listItems.get(position)){
                                if(!mediaPlayer.isPlaying()){
                                    mediaPlayer.start();
                                    showStop(v);
                                }
                                else {
                                    mediaPlayer.pause();
                                    isPaused = true;
                                    showStart(v);
                                }
                            }else {

                                mediaPlayer.reset();
                                showStart(previouslyPlayingImageView);
                                showStop(v);
                                intializeAndRunMediaFIles(listItems.get(position));
                            }
                        }

                    }catch (IOException e){
                        System.out.println("IO Exception caught");
                    }

                    previouslyPlayingFile = listItems.get(position);
                    previouslyPlayingImageView = v;

                }
                public void showStop(View v){
                    ((ImageView)  v.findViewById(R.id.audio_list_view_image_view)).setImageResource(R.drawable.stop);
                }
                public void showStart(View v){
                    ((ImageView)  v.findViewById(R.id.audio_list_view_image_view)).setImageResource(R.drawable.media_play);
                }

            });
            lv.setAdapter(audioAdapter);
        } else {
            displayNoFiles(v);
        }
        return v;
    }


    public void intializeAndRunMediaFIles(File fileName) throws IOException{
        File audioFile = fileName;
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Uri uri = Uri.parse(audioFile.getAbsolutePath());
        mediaPlayer.setDataSource(getActivity(), uri);
        mediaPlayer.prepare();
        mediaPlayer.start();

    }

    public void displayNoFiles(View v) {
        ((ImageView) v.findViewById(R.id.no_audio_placeholder)).setVisibility(View.VISIBLE);
        ((ListView) v.findViewById(R.id.audio_list_view)).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        mediaPlayer.reset();
        super.onPause();
    }
}
