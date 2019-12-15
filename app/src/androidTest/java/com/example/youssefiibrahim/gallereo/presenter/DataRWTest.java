package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;
import com.example.youssefiibrahim.gallereo.view.FullScreenImageActivity;
import com.example.youssefiibrahim.gallereo.view.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DataRWTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private Activity mainActivity;


    @Before
    public void setUp() throws Exception {
        mainActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getImages() throws IOException {
        ImageStructuresWrapper wrapper = DataRW.getImages(mainActivity);
        String json = Processing.toJson(wrapper);
        assertTrue(wrapper != null);
//        ArrayList<String> all = DataRW.getImagesPath(mainActivity);
//        System.out.println("PATHS = " + all);
//        System.out.println("JSON = " + json);
//
//        ResponseWrapper responseWrapper = communication.requestLabels(wrapper);
//        System.out.println("RESPONSE = " + Processing.toJson(responseWrapper));
    }

    @Test
    public void getImagesUri() throws FileNotFoundException {
        ArrayList<String> all = DataRW.getImagesPath(mainActivity);
        assertTrue(!all.isEmpty());

    }


    @Test
    public void writeToFile() {
        String expected = "Hello my friend";

        DataRW.writeToFile(expected, mainActivity);

        String actual = DataRW.readFromFile(mainActivity);

        assertEquals(expected, actual);
    }

    @Test
    public void readFromFile() {
        String expected = "Hello my friend";

        DataRW.writeToFile(expected, mainActivity);

        String actual = DataRW.readFromFile(mainActivity);

        assertEquals(expected, actual);
    }
}