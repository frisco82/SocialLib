/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.expertiseandroid.lib.sociallib.messages.HttpResponseWrapper;

/**
 * Utility class supporting the Facebook Object.
 *
 * @author ssoneff@facebook.com
 *
 */
public final class Util {

    /**
     * Set this to true to enable log output.  Remember to turn this back off
     * before releasing.  Sending sensitive data to log is a security risk.
     */
    private static boolean ENABLE_LOG = false;

    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            if (!(parameter instanceof String)) {
                continue;
            }

            if (first) first = false; else sb.append("&");
            sb.append(URLEncoder.encode(key) + "=" +
                      URLEncoder.encode(parameters.getString(key)));
        }
        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (v.length == 2) {
                    params.putString(URLDecoder.decode(v[0]),
                                     URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     *
     * @param url the URL to parse
     * @return a dictionary bundle of keys and values
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("fbconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    
    /**
     * Connect to an HTTP URL and return the response as a string.
     *
     * Note that the HTTP method override is used on non-GET requests. (i.e.
     * requests are made as "POST" with method specified in the body).
     *
     * @param url - the resource to open: must be a welformed URL
     * @param method - the HTTP method to use ("GET", "POST", etc.)
     * @param params - the query parameter for the URL (e.g. access_token=foo)
     * @return the URL contents as a String
     * @throws MalformedURLException - if the URL format is invalid
     * @throws IOException - if a network problem occurs
     */
    
    
    public static HttpResponseWrapper openUrl(String url, String method, Bundle params)
          throws MalformedURLException, IOException {
        // random string as boundary for multi-part http post
    	if (method.equals("GET")) {
    	  url = url + "?" + encodeUrl(params);
    	}
        Util.logd("Facebook-Util", method + " URL: " + url);
        HttpClient client = new DefaultHttpClient();
        HttpRequestBase request;
        if (method.equals("GET")) {
        	request = new HttpGet(url);
        } else {
        	HttpPost requestTemp = new HttpPost(url);
        	MultipartEntity reqEntity = new MultipartEntity();
            if (!params.containsKey("method")) {
                params.putString("method", method);
            }
            if (params.containsKey("access_token")) {
                String decoded_token =
                    URLDecoder.decode(params.getString("access_token"));
                params.putString("access_token", decoded_token);
            }
        	for (String key : params.keySet()) {
                Object parameter = params.get(key);
                if (parameter instanceof byte[]) {
                	ByteArrayBody bin = new ByteArrayBody((byte[])parameter, key);
                    reqEntity.addPart(key, bin);
                }
                if (parameter instanceof String) {
                    StringBody body = new StringBody((String)parameter);
                    reqEntity.addPart(key, body);                    
                }
        	}
            request = requestTemp;
    	}
        request.setHeader("User-Agent", System.getProperties().
                 getProperty("http.agent") + " FacebookAndroidSDK");
        Log.d("Encode", encodeUrl(params));
        HttpResponse response = client.execute(request);
        return new HttpResponseWrapper(response);
    }

    public static void clearCookies(Context context) {
        // Edge case: an illegal state exception is thrown if an instance of
        // CookieSyncManager has not be created.  CookieSyncManager is normally
        // created by a WebKit view, but this might happen if you start the
        // app, restore saved state, and click logout before running a UI
        // dialog in a WebView -- in which case the app crashes
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr =
            CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * Parse a server response into a JSON Object. This is a basic
     * implementation using org.json.JSONObject representation. More
     * sophisticated applications may wish to do their own parsing.
     *
     * The parsed JSON is checked for a variety of error fields and
     * a FacebookException is thrown if an error condition is set,
     * populated with the error message and error type or code if
     * available.
     *
     * @param response - string representation of the response
     * @return the response as a JSON Object
     * @throws JSONException - if the response is not valid JSON
     * @throws FacebookError - if an error condition is set
     */
    public static JSONObject parseJson(String response)
          throws JSONException, FacebookError {
        // Edge case: when sending a POST request to /[post_id]/likes
        // the return value is 'true' or 'false'. Unfortunately
        // these values cause the JSONObject constructor to throw
        // an exception.
        if (response.equals("false")) {
            throw new FacebookError("request failed");
        }
        if (response.equals("true")) {
            response = "{value : true}";
        }
        JSONObject json = new JSONObject(response);

        // errors set by the server are not consistent
        // they depend on the method and endpoint
        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new FacebookError(
                    error.getString("message"), error.getString("type"), 0);
        }
        if (json.has("error_code") && json.has("error_msg")) {
            throw new FacebookError(json.getString("error_msg"), "",
                    Integer.parseInt(json.getString("error_code")));
        }
        if (json.has("error_code")) {
            throw new FacebookError("request failed", "",
                    Integer.parseInt(json.getString("error_code")));
        }
        if (json.has("error_msg")) {
            throw new FacebookError(json.getString("error_msg"));
        }
        if (json.has("error_reason")) {
            throw new FacebookError(json.getString("error_reason"));
        }
        return json;
    }

    /**
     * Display a simple alert dialog with the given text and title.
     *
     * @param context
     *          Android context in which the dialog should be displayed
     * @param title
     *          Alert dialog title
     * @param text
     *          Alert dialog message
     */
    public static void showAlert(Context context, String title, String text) {
        Builder alertBuilder = new Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.create().show();
    }

    /**
     * A proxy for Log.d api that kills log messages in release build. It
     * not recommended to send sensitive information to log output in
     * shipping apps.
     *
     * @param tag
     * @param msg
     */
    public static void logd(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.d(tag, msg);
        }
    }
}
