package com.android.pathmazing.video360sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.pathmazing.video360sample.renderer.Mesh;

import java.io.File;

/**
 * @author Sopheak Tuon
 * @created on 29-Jan-18
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickVideo360(View view) {
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        intent.putExtra(
                MediaLoader.MEDIA_FORMAT_KEY,
                getIntent().getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC));
        intent.setData(createUri());
        intent.setData(createUri());
        startActivity(intent);
    }

    public void onClickVideoVR(View view) {
        Intent intent = new Intent(MainActivity.this, VrVideoActivity.class);
        intent.putExtra(
                MediaLoader.MEDIA_FORMAT_KEY,
                getIntent().getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC));
        intent.setData(createUri());
        startActivity(intent);
    }

    public static Uri createUri() {
        File dir = Environment.getExternalStorageDirectory();
        File yourFile = new File(dir, "/video360/Clash of Clans.mp4");
        Uri uri = Uri.fromFile(yourFile);
        return uri;
    }
}
