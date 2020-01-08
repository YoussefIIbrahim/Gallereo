package com.example.youssefiibrahim.gallereo.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.youssefiibrahim.gallereo.R;

public class VideoPlayActivity  extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "VideoPlayActivity";

    private MediaPlayer mMediaPlayer;
    private Uri mVideoUri;
    private ImageButton mPlayPauseButton;
    private SurfaceView mSurfaceView;
    private Toolbar toolbar;
    private MenuItem mPlayPauseMenuItem;
    private BottomNavigationView bottomNavigationView;


    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mControllerTransportControls;
    private MediaControllerCompat.Callback mControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlayPauseMenuItem.setIcon(android.R.drawable.ic_media_pause);
                    mPlayPauseMenuItem.setTitle("Pause");
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mPlayPauseMenuItem.setIcon(android.R.drawable.ic_media_play);
                    mPlayPauseMenuItem.setTitle("Play");

                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mPlayPauseMenuItem.setIcon(android.R.drawable.ic_media_play);
                    mPlayPauseMenuItem.setTitle("Play");

                    break;
            }
        }
    };
    private PlaybackStateCompat.Builder mPBuilder;
    private MediaSessionCompat mSession;


    private class MediaSessionCallback extends MediaSessionCompat.Callback implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,
            AudioManager.OnAudioFocusChangeListener {

        private Context mContext;
        private AudioManager mAudioManager;
        private IntentFilter mNoisyIntentFilter;
        private AudioBecommingNoisy mAudioBecommingNoisy;

        public MediaSessionCallback(Context context) {
            super();

            mContext = context;
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudioBecommingNoisy = new AudioBecommingNoisy();
            mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            mSurfaceView.getHolder().addCallback(this);
        }

        private class AudioBecommingNoisy extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                mediaPause();
            }
        }

        @Override
        public void onPlay() {
            super.onPlay();

            mediaPlay();
        }

        @Override
        public void onPause() {
            super.onPause();

            mediaPause();
        }

        @Override
        public void onStop() {
            super.onStop();

            releaseResources();
        }

        private void releaseResources() {
            mSession.setActive(false);
            if(mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        private void mediaPlay() {
            registerReceiver(mAudioBecommingNoisy, mNoisyIntentFilter);
            int requestAudioFocusResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if(requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mSession.setActive(true);
                mPBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
                mPBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
                mSession.setPlaybackState(mPBuilder.build());
                handleAspectRatio(mSurfaceView, mMediaPlayer);
                mMediaPlayer.start();
            }
        }

        private void mediaPause() {
            mMediaPlayer.pause();
            mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
            mPBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
            mSession.setPlaybackState(mPBuilder.build());
            mAudioManager.abandonAudioFocus(this);
            LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(mAudioBecommingNoisy);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mMediaPlayer = MediaPlayer.create(mContext, mVideoUri, surfaceHolder);
            mMediaPlayer.setOnCompletionListener(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
            mPBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                    mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
            mSession.setPlaybackState(mPBuilder.build());
        }

        @Override
        public void onAudioFocusChange(int audioFocusChanged) {
            switch (audioFocusChanged) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlay();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mediaPause();
                    break;
            }
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        mSurfaceView = (SurfaceView) findViewById(R.id.videoSurfaceView);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.video_relative_layout);
        relativeLayout.setOnLongClickListener(this);
        relativeLayout.setOnClickListener(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                playPauseClick(findViewById(R.id.video_relative_layout));
                return true;
            }
        });
        Menu menu = bottomNavigationView.getMenu();
        mPlayPauseMenuItem = menu.findItem(R.id.videoPlayPauseMenuItem);

        Intent callingIntent = this.getIntent();
        if(callingIntent != null) {
            mVideoUri = callingIntent.getData();
        }

        mSession = new MediaSessionCompat(this, TAG);
        mSession.setCallback(new MediaSessionCallback(this));
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mPBuilder = new PlaybackStateCompat.Builder();
        mController = new MediaControllerCompat(this, mSession);
        mControllerTransportControls = mController.getTransportControls();

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

    public Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mVideoUri);
        return shareIntent;
    }

    @Override
    public void onClick(View v) {
        if (getSupportActionBar().isShowing()) {
//                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            toolbar.animate().translationY(-toolbar.getBottom()).
                    setInterpolator(new AccelerateInterpolator()).start();
            getSupportActionBar().hide();
            bottomNavigationView.getMenu()
                    .findItem(R.id.videoPlayPauseMenuItem)
                    .setVisible(false);
            } else {
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            toolbar.animate().translationY(0).
                    setInterpolator(new DecelerateInterpolator()).start();
            getSupportActionBar().show();
            bottomNavigationView.getMenu()
                    .findItem(R.id.videoPlayPauseMenuItem)
                    .setVisible(true);
            }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent shareIntent = createShareIntent();
        startActivity(Intent.createChooser(shareIntent, "Send To"));
        return true;
    }

    public void playPauseClick(View view) {
        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        } else if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE) {
            mControllerTransportControls.play();
        }

    }

    @Override
    protected void onStop() {

        mController.unregisterCallback(mControllerCallback);
        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
            mControllerTransportControls.stop();
        }

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mController.registerCallback(mControllerCallback);
        mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mSession.setPlaybackState(mPBuilder.build());
    }

    @Override
    protected void onPause() {

        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSession.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleAspectRatio(View surfaceView, MediaPlayer mediaPlayer) {
        int surfaceView_Width = surfaceView.getWidth();
        int surfaceView_Height = surfaceView.getHeight();

        float video_Width = mediaPlayer.getVideoWidth();
        float video_Height = mediaPlayer.getVideoHeight();

        float ratio_width = surfaceView_Width/video_Width;
        float ratio_height = surfaceView_Height/video_Height;
        float aspectratio = video_Width/video_Height;

        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();

        if (ratio_width > ratio_height){
            layoutParams.width = (int) (surfaceView_Height * aspectratio);
            layoutParams.height = surfaceView_Height;
        }else{
            layoutParams.width = surfaceView_Width;
            layoutParams.height = (int) (surfaceView_Width / aspectratio);
        }

        surfaceView.setLayoutParams(layoutParams);
    }
}