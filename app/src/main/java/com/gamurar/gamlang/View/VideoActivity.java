package com.gamurar.gamlang.View;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardViewModel;
import com.gamurar.gamlang.utilities.WordClick;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ClickableWords;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

public class VideoActivity extends AppCompatActivity implements WordTranslation {
    private static final String TAG = "VideoActivity";

    private PlayerView mPlayerView;
    private SimpleExoPlayer player;
    private ComponentListener componentListener;
    private ClickableWords mSubtitles;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;
    private EasyPopup mCirclePop;
    private TextView mTranslateionTV;
    private ActionBar mActionBar;
    private CardViewModel mViewModel;
    private String mWord;
    private String mTranslation;
    private VideoActivity mThisFragment;
    private int mX, mY;
    private int deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window mWindow = this.getWindow();
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.watch_video);
        mThisFragment = this;
        componentListener = new ComponentListener();
        mPlayerView = findViewById(R.id.video_view);
        SubtitleView exoSubtitles = findViewById(R.id.exo_subtitles);
        exoSubtitles.setVisibility(View.INVISIBLE);
        initExoPlayer();

        mCirclePop = EasyPopup.create()
                .setContentView(this, R.layout.popup_menu)
//                .setAnimationStyle(R.style.RightPopAnim)
                .setOutsideTouchable(true)
                .setFocusAndOutsideEnable(true)
                .apply();
        mTranslateionTV = mCirclePop.findViewById(R.id.tv_popup_translation);

        mSubtitles = findViewById(R.id.tv_subtitles);
        mSubtitles.setTextColorWhite();
        mSubtitles.setWordClickCallback(new WordClick() {
            @Override
            public void onClick(String word) {
                mWord = word;
                mViewModel.translateWordOnly(mThisFragment, mWord);
                player.setPlayWhenReady(false);

            }
        });
        mSubtitles.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCirclePop.isShowing()) mCirclePop.dismiss();
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    deviceWidth = mSubtitles.getWidth();
                    mX = (int) ((event.getX()) + (deviceWidth / 4));
                    if (mX > deviceWidth) mX = deviceWidth;
                    mY = (int) ((event.getY()) - (deviceWidth / 100));
                    Log.d(TAG, "onTouch: X: " + mX + ", Y: " + mY);
                }
                mSubtitles.onTouchEvent(event);
                mSubtitles.performClick();
                return true;
            }


        });

        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
    }

    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        mPlayerView.setPlayer(player);

        Uri videoUri = Uri.parse(getString(R.string.media_url_video));
        Log.d(TAG, "video uri: " + videoUri.toString());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Gamlang"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        Uri subtitleUri = Uri.parse(getString(R.string.media_url_sub));
        Format subtitleFormat = Format.createTextSampleFormat(
                null, // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                Format.NO_VALUE, // Selection flags for the track.
                null); // The subtitle language. May be null.

        MediaSource subtitleSource =
                new SingleSampleMediaSource.Factory(dataSourceFactory)
        .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);

        MergingMediaSource mergedSource =
                new MergingMediaSource(videoSource, subtitleSource);


        player.addTextOutput(componentListener);
        player.prepare(mergedSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeTextOutput(componentListener);
            player.release();
            player = null;
        }
    }

    @Override
    public void onLoadTranslation() {

    }

    @Override
    public void onLoadTranslationFinished() {

    }

    @Override
    public void setTranslation(String translation) {
        mTranslateionTV.setText(translation);
        mCirclePop.showAtAnchorView(mSubtitles, YGravity.ABOVE, XGravity.LEFT, mX, mY);
    }

    @Override
    public void showTranslationErrorMessage() {

    }

    private class ComponentListener implements TextOutput {
        @Override
        public void onCues(List<Cue> cues) {
            if (cues != null && !cues.isEmpty()) {
                String cue = cues.get(0).text.toString();
                mSubtitles.setText(cue);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }
}
