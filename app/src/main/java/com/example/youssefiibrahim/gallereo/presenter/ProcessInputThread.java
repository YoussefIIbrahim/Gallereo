package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;

public class ProcessInputThread extends Thread {

    Activity activity;
    String input;

    public ProcessInputThread (Activity activity, String input) {
        this.activity = activity;
        this.input = input;
    }

    public void run() {

    }
}
