package com.example.youssefiibrahim.gallereo.view;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.youssefiibrahim.gallereo.R;

public class FullScreenImageActivity extends AppCompatActivity implements
        View.OnLongClickListener {

    private Uri mImageUri;
    private Toolbar toolbar;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);
        fullScreenImageView.setOnLongClickListener(this);
        fullScreenImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getSupportActionBar().isShowing()) {
//                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        toolbar.animate().translationY(-toolbar.getBottom()).
                                setInterpolator(new AccelerateInterpolator()).start();
                        getSupportActionBar().hide();
                    } else {
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        toolbar.animate().translationY(0).
                                setInterpolator(new DecelerateInterpolator()).start();
                        getSupportActionBar().show();
                    }
                    return true;
                } else return false;
            }
        });

        toolbar = (Toolbar)findViewById(R.id.toolbar1);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbarColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null) {
            mImageUri = callingActivityIntent.getData();
            if(mImageUri != null && fullScreenImageView != null) {
                Glide.with(this)
                        .load(mImageUri)
                        .into(fullScreenImageView);
            }
        }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.full_image_share, menu);

        MenuItem menuItem = menu.findItem(R.id.image_share_menu);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        shareActionProvider.setShareIntent(createShareIntent());
        return true;
    }


    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
        return shareIntent;
    }

    @Override
    public boolean onLongClick(View v) {

        Intent shareIntent = createShareIntent();
        startActivity(Intent.createChooser(shareIntent, "send to"));
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getSupportActionBar().isShowing()) {
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                toolbar.animate().translationY(-toolbar.getBottom()).
                        setInterpolator(new AccelerateInterpolator()).start();
                getSupportActionBar().hide();
            } else {
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                toolbar.animate().translationY(0).
                        setInterpolator(new DecelerateInterpolator()).start();
                getSupportActionBar().show();
            }
            return true;
        } else return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    void hideActionBar(final ActionBar actionBar){
//        if (actionBar != null && actionBar.isShowing()) {
//            if(toolbar != null) {
//                toolbar.animate().translationY(-212).alpha(0).setDuration(600L)
//                        .setListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                actionBar.hide();
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        }).start();
//            } else {
//                actionBar.hide();
//            }
//        }
//    }
//
//    void showActionBar(ActionBar actionBar){
//        if (actionBar != null && !actionBar.isShowing()) {
//            actionBar.show();
//            if(toolbar != null) {
//                toolbar.animate().translationY(0).alpha(1).setDuration(600L).start();
//            }
//        }
//    }
}