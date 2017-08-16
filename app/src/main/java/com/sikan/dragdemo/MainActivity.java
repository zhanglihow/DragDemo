package com.sikan.dragdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    DrawingView mDrawingView;

    public static Bitmap mergeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingView = (DrawingView) findViewById(R.id.drawing_view);

        findViewById(R.id.hecheng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mergeBitmap = getViewBitmap(mDrawingView);
                startActivity(new Intent(MainActivity.this, MergeImgActivity.class));
            }
        });

        findViewById(R.id.up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setOffset(DrawingView.TYPE.UP);

            }
        });

        findViewById(R.id.left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setOffset(DrawingView.TYPE.LEFT);

            }
        });

        findViewById(R.id.right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setOffset(DrawingView.TYPE.RIGHT);

            }
        });

        findViewById(R.id.down_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setOffset(DrawingView.TYPE.DOWN);

            }
        });

        init();
    }

    private void init() {
        for (int i = 0; i < 3; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            CustomBitmap customBitmap = new CustomBitmap(bitmap);
            customBitmap.setId(i);
            customBitmap.scaleW = bitmap.getWidth();
            customBitmap.scaleH = bitmap.getHeight();
            mDrawingView.addBitmap(customBitmap);
        }
    }


    /**
     * 根据view来生成bitmap图片，可用于截图功能
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus(); //
        v.setPressed(false); //
        // 能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

}
