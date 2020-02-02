package com.example.youssefiibrahim.gallereo.presenter;

import android.util.Log;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Semaphore;


public class communication {


    public static final String LABELER_URL = "https://europe-west1-festive-athlete-249218.cloudfunctions.net/classifyer-2";
    public static final String HANDLER_URL = "https://europe-west1-festive-athlete-249218.cloudfunctions.net/handler";
    private static String toSendString;
    private static Semaphore available = new Semaphore(1, true);

    public communication() {

    }
    public static String sendHttpRequest(String jsonObject, String endpoint, String contentType, byte[] bts) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(1000000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", contentType);

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);


        OutputStream out = null;
        urlConnection.connect();
        out = new BufferedOutputStream(urlConnection.getOutputStream());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write(jsonObject);
        writer.flush();
        writer.close();
        out.close();
        available.release();

        Integer responseCode = urlConnection.getResponseCode();

        if (responseCode != 200) {
            Log.e("ResponseCode", "response code is " + responseCode);
            return "";
        }
        InputStream in = urlConnection.getInputStream();
        String response = new String();
        int count = 0;
        while ((count = in.read(bts)) > 0) { response += new String(bts, 0, count); }
        urlConnection.getInputStream().read(bts);

        urlConnection.disconnect();
        return response;
    }


    public static ResponseWrapper requestLabels(ImageStructuresWrapper wrapper) throws IOException, InterruptedException {
        available.acquire();
        while (true) {
            try{
                toSendString = Processing.toJson(wrapper);
            } catch (OutOfMemoryError e) {
                Thread.sleep(500);
                continue;
            }
            break;
        }
        byte[] bts = new byte[10000];
        String response = sendHttpRequest(toSendString, LABELER_URL, "application/json", bts).trim();

        Log.d("LABELS: " , "T" +response);
        if (response.isEmpty()) {
            return null;
        }
        return (ResponseWrapper) Processing.fromJson(response, ResponseWrapper.class);
    }

    public static PairWrapper processInput(String input) throws IOException {
        byte[] bts = new byte[100];
        String response = sendHttpRequest(input, HANDLER_URL, "text/plain", bts).trim();


        return (PairWrapper) Processing.fromJson(response, PairWrapper.class);
    }

}
