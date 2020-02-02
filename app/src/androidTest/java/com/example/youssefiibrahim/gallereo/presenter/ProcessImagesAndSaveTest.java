package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.example.youssefiibrahim.gallereo.view.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class ProcessImagesAndSaveTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private Activity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void shouldProcessImagesAndSave() {
        ArrayList<String> allPaths = DataRW.getImagesPath(mainActivity);
        ArrayList<String> imagesToProcess = DataRW.filterPaths(allPaths);
        new DecodeImages(mainActivity).execute(imagesToProcess);
    }
}