/*
 * @(#)VolleyRequestQueue.java 1.0 23/10/2015
 *
 * Copyright
 *
 *
 */
package com.android.pathmazing.video360sample.volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * This class for use Volley as Singleton Pattern.
 *
 * @version 1.0 23 Oct  2015
 * @author Peter
 */
public class VolleyRequestQueue {
    private static VolleyRequestQueue mInstance;
    private Context mCtx;
    private RequestQueue mRequestQueue;

    /**
     * This is constructor of class , but we protected from other class to access as private.
     * @param context
     */
    private VolleyRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * This function for other can get constance class from this class.
     * And make sure this function return mInstance only one time and only one instance of class.
     * @param context
     * @return mInstance type CustomVolleyRequestQueue.
     */
    public static synchronized VolleyRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRequestQueue(context);
        }
        return mInstance;
    }

    /**
     * This function other class after get constance of class after that class can request queue
     * function to request data from server.
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

}
