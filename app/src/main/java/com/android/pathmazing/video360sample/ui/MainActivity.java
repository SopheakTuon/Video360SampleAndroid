package com.android.pathmazing.video360sample.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.pathmazing.video360sample.Constants;
import com.android.pathmazing.video360sample.MediaLoader;
import com.android.pathmazing.video360sample.R;
import com.android.pathmazing.video360sample.renderer.Mesh;
import com.android.pathmazing.video360sample.utils.OnLoadYouTubeResponse;
import com.android.pathmazing.video360sample.utils.YouTubeUtil;
import com.android.pathmazing.video360sample.utils.YoutubeDownloader;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

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
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                String youTubeUrl = "https://youtu.be/wczdECcwRw0";
                onLoadYouTubeVideo(youTubeUrl, new OnLoadYouTubeResponse() {
                    @Override
                    public void onSuccess(String streamingUrl) {
                        startNewActivity(VideoActivity.class, streamingUrl);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Cannot load video.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, 300);
    }

    public void onClickVideoVR(View view) {
        String youTubeUrl = "https://youtu.be/wczdECcwRw0";
        onLoadYouTubeVideo(youTubeUrl, new OnLoadYouTubeResponse() {
            @Override
            public void onSuccess(String streamingUrl) {
                startNewActivity(VrVideoActivity.class, streamingUrl);
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "Cannot load video.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startNewActivity(Class activity, String streamingUrl) {
        Intent intent = new Intent(MainActivity.this, activity);
        intent.putExtra(MediaLoader.MEDIA_FORMAT_KEY,
                getIntent().getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC));
        intent.setData(Uri.parse(streamingUrl));
        startActivity(intent);
    }


    private void onLoadYouTubeVideo(String youTubeUrl, final OnLoadYouTubeResponse onLoadYouTubeResponse) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_video));
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
            YoutubeDownloader youtubeDownloader = new YoutubeDownloader(this);
            youtubeDownloader.setOnResponse(new YoutubeDownloader.OnResponse() {
                @Override
                public void onResponse(final JSONObject video) {
                    try {
                        progressDialog.dismiss();
                        JSONArray jsonArrayVideos = video.getJSONArray(Constants.videos);
                        if (jsonArrayVideos.length() > 0) {
                            JSONObject jsonObjectVideo = jsonArrayVideos.getJSONObject(0);
                            String url = jsonObjectVideo.getString(Constants.url);
                            onLoadYouTubeResponse.onSuccess(url);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        progressDialog.dismiss();
                        onLoadYouTubeResponse.onError();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            String videoId = YouTubeUtil.getYoutubeVideoIdFromUrl(youTubeUrl);
            if (videoId != null)
                youtubeDownloader.download(videoId);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

//    public static Uri createUri() {
//        File dir = Environment.getExternalStorageDirectory();
//        File yourFile = new File(dir, "/video360/Clash of Clans.mp4");
//        Uri uri = Uri.fromFile(yourFile);
//        return uri;
//    }
}
