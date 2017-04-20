package com.skmapstutorial.Application.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skmapstutorial.R;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Images extends Fragment {
    int numberOfImageFIles;
    LinearLayout imagesLayout;
    boolean hasImages;
    String fileName;
    ListView lv;
    ArrayList<Bitmap> listItems = new ArrayList<Bitmap>();
    ImageAdapter adapter;

    public Images() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_images, container, false);
        fileName = ((Gallery) getActivity()).getBuildignName().replace(" ", "");
        listItems.clear();
        lv = (ListView) v.findViewById(R.id.images_listView);
        adapter = new ImageAdapter(((Gallery) getActivity()),
                listItems);

        //imagesLayout = (LinearLayout) v.findViewById(R.id.images_listView);
        try {
            numberOfImageFIles = (new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Images")).listFiles().length;
        } catch (NullPointerException e) {
            numberOfImageFIles = 0;
        }
        hasImages = numberOfImageFIles > 0 ? true : false;
        if (hasImages) {
            System.out.println("Has videos");
            for (int i = 1; i <= numberOfImageFIles; i++) {
                System.out.println("Files in Fragmets: " + getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Images/" + i + ".jpg");
                File imgFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null) + "/" + fileName + "/Images/" + i + ".jpg");
                ImageView imageView = new ImageView((Gallery) getActivity());
                // imageView.setId(i);
                imageView.setPadding(2, 2, 2, 2);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                listItems.add(myBitmap);

            }
        } else {
            ImageView imageView = new ImageView((Gallery) getActivity());
            // imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageResource(R.drawable.noimages);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Bitmap myBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimages);
            imageView.setImageBitmap(myBitmap);

            listItems.add(myBitmap);
        }


        lv.setAdapter(adapter);
        return v;
    }

}
