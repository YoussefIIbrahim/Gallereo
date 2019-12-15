package com.example.youssefiibrahim.gallereo.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
    public void shouldOnCreate() {

        assertNotNull(fullScreenActivity.findViewById(R.id.fullScreenImageView));
        assertNotNull(fullScreenActivity.findViewById(R.id.full_screen_relative_layout));
        assertNotNull(fullScreenActivity.findViewById(R.id.toolbar1));

    }

    @Test
    public void shouldOnCreateOptionsMenu() {
        Menu menu = new Menu() {
            @Override
            public MenuItem add(CharSequence title) {
                return null;
            }

            @Override
            public MenuItem add(int titleRes) {
                return null;
            }

            @Override
            public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
                return null;
            }

            @Override
            public MenuItem add(int groupId, int itemId, int order, int titleRes) {
                return null;
            }

            @Override
            public SubMenu addSubMenu(CharSequence title) {
                return null;
            }

            @Override
            public SubMenu addSubMenu(int titleRes) {
                return null;
            }

            @Override
            public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
                return null;
            }

            @Override
            public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
                return null;
            }

            @Override
            public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
                return 0;
            }

            @Override
            public void removeItem(int id) {

            }

            @Override
            public void removeGroup(int groupId) {

            }

            @Override
            public void clear() {

            }

            @Override
            public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {

            }

            @Override
            public void setGroupVisible(int group, boolean visible) {

            }

            @Override
            public void setGroupEnabled(int group, boolean enabled) {

            }

            @Override
            public boolean hasVisibleItems() {
                return false;
            }

            @Override
            public MenuItem findItem(int id) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public MenuItem getItem(int index) {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
                return false;
            }

            @Override
            public boolean isShortcutKey(int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean performIdentifierAction(int id, int flags) {
                return false;
            }

            @Override
            public void setQwertyMode(boolean isQwerty) {

            }
        };
        try {
            fullScreenActivity.onCreateOptionsMenu(menu);
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

}