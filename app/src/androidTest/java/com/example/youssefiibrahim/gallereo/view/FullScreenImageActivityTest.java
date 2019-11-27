package com.example.youssefiibrahim.gallereo.view;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.youssefiibrahim.gallereo.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class FullScreenImageActivityTest {

    @Rule
    public ActivityTestRule<FullScreenImageActivity> mActivityTestRule = new ActivityTestRule<FullScreenImageActivity>(FullScreenImageActivity.class);

    private Activity fullScreenActivity;

    @Before
    public void setUp() throws Exception {
        fullScreenActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        fullScreenActivity = null;

    }

    @Test
    public void test_onCreate() {

        assertNotNull(fullScreenActivity.findViewById(R.id.fullScreenImageView));
        assertNotNull(fullScreenActivity.findViewById(R.id.full_screen_relative_layout));
        assertNotNull(fullScreenActivity.findViewById(R.id.toolbar1));

    }

    @Test
    public void test_onCreateOptionsMenu() {

        assertNotNull(fullScreenActivity.getMenuInflater());
    }

    @Test
    public void test_onOptionsItemSelected() {
        assertNotNull(android.R.id.home);
    }

    @Test
    public void test_onClick() {
        assertNotNull(mActivityTestRule.getActivity().getToolbar());
        assertNotNull(mActivityTestRule.getActivity().getSupportActionBar());
    }
}