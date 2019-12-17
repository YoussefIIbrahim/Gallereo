package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.example.youssefiibrahim.gallereo.model.Request;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SendHttpToLabeler extends AsyncTask<Request, String, String> {



    @Override
    protected String doInBackground(Request... Requests) {
        Request req = Requests[0];

        URL url = null;
        try {
            url = new URL(req.endpoint);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        try {
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        urlConnection.setRequestProperty("Content-Type", req.contentType);

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);


        OutputStream out = null;

        try {
            urlConnection.connect();
            out = new BufferedOutputStream(urlConnection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String response = "";
        try {
            writer.write(req.jsonObject);
            writer.flush();
            writer.close();
            out.close();
            Integer responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                return "";
            }

            byte[] bts = new byte[10000];

            urlConnection.getInputStream().read(bts);
            response = new String(bts).trim();
            System.out.println("Response = " + new String(bts));
            urlConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("FINISHED EXECUTING");
        ResponseWrapper responseWrapper = (ResponseWrapper) Processing.fromJson(result, ResponseWrapper.class);
        if (ResponseWrapper.singleton == null) {
            ResponseWrapper.singleton = new ResponseWrapper();
        }
        ResponseWrapper.singleton.add(responseWrapper);
        // now commit to device
        String json = Processing.toJson(ResponseWrapper.singleton);
//        writeToFile(json, context);
    }
}
