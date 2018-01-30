package com.android.pathmazing.video360sample.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sopheak Tuon
 * @created on 29-Jun-17
 */

public final class YouTubeUtil {
    public static String getYoutubeThumbnailUrlFromVideoUrl(String videoUrl) {
        return "http://img.youtube.com/vi/" + getYoutubeVideoIdFromUrl(videoUrl) + "/0.jpg";
    }

    public static String getYoutubeVideoIdFromUrl(String inUrl) {
        if (inUrl.toLowerCase().contains("youtu.be"))
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find())
            return matcher.group();
        return null;
    }

    public static void onThumbnailYoutubeClick(final Context mContext, String youTubeUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading video...");
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
            YoutubeDownloader youtubeDownloader = new YoutubeDownloader(mContext);
            youtubeDownloader.setOnResponse(new YoutubeDownloader.OnResponse() {
                @Override
                public void onResponse(final JSONObject video) {
                    try {
                        progressDialog.dismiss();
                        JSONArray jsonArrayVideos = video.getJSONArray("videos");
                        if (jsonArrayVideos.length() > 0) {
                            JSONObject jsonObjectVideo = jsonArrayVideos.getJSONObject(0);
                            String url = jsonObjectVideo.getString("url");
//                            if (!TextUtils.isEmpty(url)) {
//                                MediaViewer.startActivity(mContext, url);
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        progressDialog.dismiss();
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
}
