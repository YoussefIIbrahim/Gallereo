package com.example.youssefiibrahim.gallereo.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;
import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class communication {


    public static final String LABELER_URL = "https://europe-west1-festive-athlete-249218.cloudfunctions.net/classifyer-2";
    public static final String HANDLER_URL = "https://us-central1-festive-athlete-249218.cloudfunctions.net/handler";
    public communication() {

    }
    public static Integer sendHttpRequest(String jsonObject, String endpoint, String contentType, byte[] bts) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000);
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

        Integer responseCode = urlConnection.getResponseCode();

        urlConnection.getInputStream().read(bts);
        System.out.println("Response = " + new String(bts));
        urlConnection.disconnect();
        return responseCode;
    }


    public static ResponseWrapper requestLabels(ImageStructuresWrapper wrapper) throws IOException {
        String json = Processing.toJson(wrapper);
        byte[] bts = new byte[10000];
        Integer responseCode = sendHttpRequest(json, LABELER_URL, "application/json", bts);
        if (responseCode != 200) {
            return null;
        }
        String response = new String(bts).trim();
        return (ResponseWrapper) Processing.fromJson(response, ResponseWrapper.class);
    }

    public static PairWrapper processInput(String input) throws IOException {
        byte[] bts = new byte[100];
        Integer responseCode = sendHttpRequest(input, HANDLER_URL, "text/plain", bts);
        if (responseCode != 200) {
            return null;
        }
        String response = new String(bts).trim();
        return (PairWrapper) Processing.fromJson(response, PairWrapper.class);
    }

}
