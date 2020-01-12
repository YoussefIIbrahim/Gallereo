package com.example.youssefiibrahim.gallereo.view;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.method.Touch;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.youssefiibrahim.gallereo.R;
import com.google.gson.Gson;


public class FullScreenImageActivity extends AppCompatActivity implements
        View.OnLongClickListener, View.OnClickListener {

    private Uri mImageUri;
    private MediaStorageAdapter mainMemberMediaStoreAdapter;
    private Toolbar toolbar;
    private GestureDetector gdt;
    private static final int MIN_SWIPPING_DISTANCE = 25;
    private static final int THRESHOLD_VELOCITY = 25;
    private TouchImageView fullScreenImageView;
    private boolean safe2Swipe;
    private int currentApiVersion;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
        fullScreenImageView = (TouchImageView) findViewById(R.id.fullScreenImageView);
//        fullScreenImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        mainMemberMediaStoreAdapter = MainActivity.memberMediaStoreAdapter;

        gdt = new GestureDetector(new GestureListener());

        fullScreenImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                safe2Swipe = true;
                gdt.onTouchEvent(event);
                return true;
            }

        });

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.full_screen_relative_layout);
        relativeLayout.setOnLongClickListener(this);
        relativeLayout.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            mImageUri = callingActivityIntent.getData();
            if (mImageUri != null && fullScreenImageView != null) {

                RequestOptions options = new RequestOptions();
                options.fitCenter();
                options.format(DecodeFormat.PREFER_ARGB_8888);
                options.override(Target.SIZE_ORIGINAL);

                Glide.with(this)
                        .load(mImageUri)
                        .apply(options)
                        .into(fullScreenImageView);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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

    @VisibleForTesting
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
        return shareIntent;
    }

    @Override
    public boolean onLongClick(View v) {
//        Toast.makeText(this, findViewById(android.R.id.content).getId(), Toast.LENGTH_SHORT).show();
        Intent shareIntent = createShareIntent();
        startActivity(Intent.createChooser(shareIntent, "Send To"));
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            if (!mainMemberMediaStoreAdapter.searchMode) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
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
    }

    private void changeImage(Uri uri) {
        RequestOptions options = new RequestOptions();
        options.fitCenter();
        options.format(DecodeFormat.PREFER_ARGB_8888);
        options.override(Target.SIZE_ORIGINAL);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this)
                .load(uri)
                .apply(options)
                .into(fullScreenImageView);
        safe2Swipe = false;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println("Inside onFlign " + safe2Swipe);
            if (!fullScreenImageView.isZoomed() &&
                    !mainMemberMediaStoreAdapter.searchMode &&
                    e2.getX() - e1.getX() > MIN_SWIPPING_DISTANCE &&
                    Math.abs(velocityX) > THRESHOLD_VELOCITY &&
                    mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().moveToPrevious()) {
                mainMemberMediaStoreAdapter.getOnClickUri(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition());
//                mImageUri = Uri.parse(String.valueOf(mainMemberMediaStoreAdapter.getUriFromMediaStore(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition())).substring("file://".length()));
//                changeImage(mainMemberMediaStoreAdapter.getUriFromMediaStore(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition()));
                return true;

            } else if (!fullScreenImageView.isZoomed() &&
                    !mainMemberMediaStoreAdapter.searchMode &&
                    e1.getX() - e2.getX() > MIN_SWIPPING_DISTANCE &&
                    Math.abs(velocityX) > THRESHOLD_VELOCITY &&
                    mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().moveToNext()) {
                mainMemberMediaStoreAdapter.getOnClickUri(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition());
//                mImageUri = Uri.parse(String.valueOf(mainMemberMediaStoreAdapter.getUriFromMediaStore(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition())).substring("file://".length()));
//                changeImage(mainMemberMediaStoreAdapter.getUriFromMediaStore(mainMemberMediaStoreAdapter.getMemberMediaStoreCursor().getPosition()));
                return true;
            }
            safe2Swipe = false;
            return false;
        }
    }
}