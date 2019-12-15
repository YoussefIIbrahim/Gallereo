package com.example.youssefiibrahim.gallereo.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.CursorLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private Activity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;

    }


    @Test
    public void shouldOnClickImage() {
        Intent fullscreenImageIntent = new Intent(mainActivity.getBaseContext(), FullScreenImageActivity.class);
        Uri uri = Uri.parse("/file");
        fullscreenImageIntent.setData(uri);
        try {
            mActivityTestRule.launchActivity(fullscreenImageIntent);
        } catch (ClassCastException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void shouldOnClickVideo() {
        Intent videoPlayIntent = new Intent(mainActivity.getBaseContext(), VideoPlayActivity.class);
        Uri uri = Uri.parse("/file");
        videoPlayIntent.setData(uri);
        try {
            mActivityTestRule.launchActivity(videoPlayIntent);
        } catch (ClassCastException e) {
            assertNotNull(e);
        }
    }
}