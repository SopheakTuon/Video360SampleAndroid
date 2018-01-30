package com.android.pathmazing.video360sample.utils;

import android.content.Context;
import android.util.Log;

import com.android.pathmazing.video360sample.Constants;
import com.android.pathmazing.video360sample.volley.VolleyRequestQueue;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class YoutubeDownloader {

    private static final Pattern commaPattern = Pattern.compile(",");
    private boolean isFilterBadData = true;

    private Context context;
    private OnResponse onResponse;
    private RequestQueue mRequestQueue;

    public YoutubeDownloader(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
    }

    private OnResponse getOnResponse() {
        return onResponse;
    }

    public void setOnResponse(OnResponse onResponse) {
        this.onResponse = onResponse;
    }

    private String videoId;

    public void download(String videoId) {
        this.videoId = videoId;
        String mUrl = "http://www.youtube.com/get_video_info?video_id=%s&el=detailpage&asv=3&hl=en_US&sts=16136&fmt=34";
        String url = String.format(mUrl, videoId);
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                try {
                    if (response != null) {
                        JSONObject relult = new JSONObject();
                        try {
                            JSONArray videojs = new JSONArray();
                            String token;
                            String reason;
                            String formater;
                            String status;
                            String statusCode = "";
                            String title;
                            String thumbnail_url;
                            String videoInfo = new String(response);
                            HashMap<String, String> videosInfo = toHashMap(videoInfo);
                            status = videosInfo.get("status");
                            title = videosInfo.get("title");
                            thumbnail_url = videosInfo.get("thumbnail_url");
                            Log.d("YoutubeDownloader", "status>>>>" + status);
                            try {
                                token = videosInfo.get("token");
                            } catch (Exception e) {
                                token = "";
                            }
                            try {
                                reason = videosInfo.get("reason");
                            } catch (Exception e) {
                                reason = "";
                            }
                            try {
                                formater = videosInfo.get("url_encoded_fmt_stream_map");
                                String[] formats = commaPattern.split(formater);
                                for (String tmp : formats) {
                                    HashMap<String, String> video = toHashMap(tmp);
                                    String v = videojs.toString().replaceAll(" ", "").trim();
                                    String qa = "\"quality\":\"" + video.get("quality") + "\"";
                                    if ((null == video.get("stereo3d") && !v.contains(qa)) && isFilterBadData) {
                                        video.put("url", video.get("url") + "&t=" + token);
                                        JSONObject tmpJSONObject = new JSONObject();
                                        for (String tmpString : video.keySet())
                                            tmpJSONObject.put(tmpString, video.get(tmpString));
                                        videojs.put(tmpJSONObject);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            relult.put(Constants.status, status);
                            relult.put(Constants.title, title);
                            relult.put(Constants.thumbnail_url, thumbnail_url);
                            relult.put(Constants.videoId, YoutubeDownloader.this.videoId);
                            relult.put(Constants.token, token);
                            relult.put(Constants.reason, reason);
                            relult.put(Constants.statusCode, statusCode);
                            relult.put(Constants.videos, videojs);
                            OnResponse on = getOnResponse();
                            if (on != null)
                                on.onResponse(relult);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                OnResponse on = getOnResponse();
                if (on != null)
                    on.onErrorResponse(error);
            }
        }, null);
        request.setRetryPolicy(new DefaultRetryPolicy(60*1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(videoId);
        mRequestQueue.add(request);
    }

    private HashMap<String, String> toHashMap(String str) {
        HashMap<String, String> results = new HashMap<>();
        String[] strs = str.split("&");
        for (String tmp : strs) {
            String[] keyValue = tmp.split("=", 2);
            try {
                results.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

//    public void enableFilterBadData(boolean status) {
//        isFilterBadData = status;
//    }

    public void cancel() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(videoId);
            mRequestQueue.getCache().remove(videoId);
        }
    }

    private class InputStreamVolleyRequest extends Request<byte[]> {
        private final Response.Listener<byte[]> mListener;
        private Map<String, String> mParams;

        Map<String, String> responseHeaders;

        InputStreamVolleyRequest(int method, String mUrl, Response.Listener<byte[]> listener, Response.ErrorListener errorListener, HashMap<String, String> params) {
            super(method, mUrl, errorListener);
            setShouldCache(false);
            mListener = listener;
            mParams = params;
        }

        @Override
        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
            return mParams;
        }

        @Override
        protected void deliverResponse(byte[] response) {
            mListener.onResponse(response);
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
            responseHeaders = response.headers;
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    public interface OnResponse {
        void onResponse(JSONObject video);

        void onErrorResponse(VolleyError error);
    }

    public static void download(String url, Context context, final OnResponse onResponse) {
        String requestUrl = "https://www.googleapis.com/youtube/v3/videos?key=" + Constants.GOOGLE_DEVELOPER_KEY + "&part=snippet&id=" + YouTubeUtil.getYoutubeVideoIdFromUrl(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (onResponse != null)
                    onResponse.onResponse(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (onResponse != null)
                            onResponse.onErrorResponse(error);
                    }
                } );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestQueue.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }


}