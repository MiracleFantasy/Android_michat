package com.afzal.mi_chat.utils;

import com.loopj.android.http.RequestParams;
import org.apache.http.client.params.ClientPNames;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.XmlHttpResponseHandler;

public class NetUtils {

    private static final String BASE_URI = "http://www.macinsiders.com/chat/?ajax=true";

    public static AsyncHttpClient client;
    private static PersistentCookieStore myCookieStore;

    public static synchronized AsyncHttpClient getClientInstance() {
        if (client == null) {
            client = new AsyncHttpClient();
        }

        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        return client;
    }

    public static synchronized PersistentCookieStore getCookieStoreInstance(Context context) {
        if (myCookieStore == null) {
            myCookieStore = new PersistentCookieStore(context);
        }
        return myCookieStore;
    }

    public static void getPage(XmlHttpResponseHandler myResponseHandler, long lastId) {
        String uri = BASE_URI;
        if (lastId != -1) {
            uri = uri + "&lastID=" + Long.toString(lastId);
        }
        NetUtils.getClientInstance().get(uri, myResponseHandler);
    }

    public static void postMessage(XmlHttpResponseHandler myResponseHandler, String message) {
        String uri = BASE_URI;
        RequestParams params = new RequestParams();
        params.put("text", message);
        NetUtils.getClientInstance().post(uri, params, myResponseHandler);
    }
}
