package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.Response;
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
    public void shouldFilterPaths() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Tareq"); list.add("Habbab"); list.add("Marwan"); list.add("Helwani");
        ResponseWrapper.singleton = new ResponseWrapper();
        ResponseWrapper.singleton.add(new Response("Marwan"));
        ResponseWrapper.singleton.add(new Response("Tareq"));

        ArrayList<String> actual = DataRW.filterPaths(list);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Habbab"); expected.add("Helwani");
        System.out.println("expected = " + expected);
        System.out.println("actual = " + actual);
        assertEquals(expected, actual);
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
        String expected = "{\"responses\":[{\"data\":[{\"isProcessed\":true,\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"isProcessed\":true,\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"1\"},{\"data\":[{\"isProcessed\":true,\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"isProcessed\":true,\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"2\"}]}";

        DataRW.writeToFile(expected, mainActivity);

        String actual = DataRW.readFromFile(mainActivity);

        assertEquals(expected, actual);
    }

    @Test
    public void readFromFile() {
        String expected = "{\"responses\":[{\"data\":[{\"isProcessed\":true,\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"isProcessed\":true,\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"1\"},{\"data\":[{\"isProcessed\":true,\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"isProcessed\":true,\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"2\"}]}";

        DataRW.writeToFile(expected, mainActivity);

        String actual = DataRW.readFromFile(mainActivity);

        assertEquals(expected, actual);
    }
}