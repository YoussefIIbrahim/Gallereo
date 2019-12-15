package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import com.example.youssefiibrahim.gallereo.view.FullScreenImageActivity;
import com.example.youssefiibrahim.gallereo.view.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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