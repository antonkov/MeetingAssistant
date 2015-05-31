package com.gtdev.meetingassistant.utils;

import android.content.Context;
import android.util.Base64;

import com.gtdev.meetingassistant.activites.EventInfoActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class RestClientHelper {
    public static final String BASE_URL = "http://188.166.35.95:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void put(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        client.put(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
    }

    private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    public static void createEvent(Context context, String title, String date, List<String> users,  AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray(users);
        try {
            jsonParams.put("title", title);
            jsonParams.put("date", date);
            jsonParams.put("users", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            System.out.println(jsonParams.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post(context, "events/", entity, "application/json", responseHandler);
    }

    public static void getEvents(String user, AsyncHttpResponseHandler responseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("user", user);
        requestParams.add("data", "true");
        get("events/", requestParams, responseHandler);
    }

    public static void uploadAudio(Context context, String eventId, File file, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonParams = new JSONObject();
        try {
            String audioString = Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT);
            byte[] bytes = Base64.decode(audioString, Base64.DEFAULT);

            try {
                File newFile = new File(EventInfoActivity.getFileName("AUDIO31"));
                FileOutputStream fileOuputStream = new FileOutputStream(newFile);
                fileOuputStream.write(bytes);
                fileOuputStream.flush();
                fileOuputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            jsonParams.put("audio", audioString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            System.out.println(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        put(context, "events/" + eventId, entity, "application/json", responseHandler);
    }

    public static void getAudio(String eventId, AsyncHttpResponseHandler responseHandler) {
        RequestParams requestParams = new RequestParams();
        get("events/" + eventId, requestParams, responseHandler);
    }

    public static void getQueryResult(String eventId, String query, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", eventId);
        requestParams.put("query", query);
        get("events/query/", requestParams, jsonHttpResponseHandler);
    }
}