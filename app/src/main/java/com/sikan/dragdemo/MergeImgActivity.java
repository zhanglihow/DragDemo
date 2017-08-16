package com.sikan.dragdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import static com.sikan.dragdemo.MainActivity.mergeBitmap;

public class MergeImgActivity extends AppCompatActivity {


    ImageView mergeImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merge_layout);

        mergeImg= (ImageView) findViewById(R.id.image);
        mergeImg.setImageBitmap(mergeBitmap);

    }
}
