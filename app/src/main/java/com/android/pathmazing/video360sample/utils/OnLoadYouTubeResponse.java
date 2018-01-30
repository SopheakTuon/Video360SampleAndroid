package com.android.pathmazing.video360sample.utils;

/**
 * @author Sopheak Tuon
 * @created on 30-Jan-18
 */

public interface OnLoadYouTubeResponse {
    void onSuccess(String streamingUrl);

    void onError();
}
