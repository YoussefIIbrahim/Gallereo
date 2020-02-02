package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class ProcessAndSaveThread  extends Thread{

    ArrayList<String> paths;
    Context context;

    public ProcessAndSaveThread(ArrayList<String> paths, Context context) {
        this.paths = paths;
        this.context = context;
    }

    public void run() {
        try {
            DataRW.processAndSave(paths, context);
        } catch (IOException | InterruptedException e) {
            Log.e("Exception", "Thread failed " + e.toString());
            e.printStackTrace();
        }
    }
}
