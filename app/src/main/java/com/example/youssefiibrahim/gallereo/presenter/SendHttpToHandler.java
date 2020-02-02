package com.example.youssefiibrahim.gallereo.presenter;

import android.os.AsyncTask;

import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.view.MediaStorageAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class SendHttpToHandler extends AsyncTask<String, String, PairWrapper> {

    private MediaStorageAdapter mediaStorageAdapter;

    public SendHttpToHandler(MediaStorageAdapter mediaStorageAdapter) {
        this.mediaStorageAdapter = mediaStorageAdapter;
    }

    @Override
    protected PairWrapper doInBackground(String... inputs) {

        try {
            PairWrapper pairWrapper = communication.processInput(inputs[0]);
            return pairWrapper;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onProgressUpdate(String... progress) {

    }


    protected void onPostExecute(PairWrapper pairWrapper) {

        ArrayList<String> filteredImages = CoreAlgorithms.filterImages(pairWrapper);
        this.mediaStorageAdapter.handler(filteredImages);
    }
}
