package com.hfad.gamlang.View;

import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.exoplayer2.text.webvtt.WebvttCssStyle;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.hfad.gamlang.R;
import com.hfad.gamlang.views.ClickableWords;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {
    private static final String TAG = "VideoFragment";

    private PlayerView mPlayerView;
    private SimpleExoPlayer player;
    private ComponentListener componentListener;
    private ClickableWords mSubtitles;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        componentListener = new ComponentListener();
        mPlayerView = view.findViewById(R.id.video_view);
        mSubtitles = view.findViewById(R.id.tv_subtitles);
        SubtitleView exoSubtitles = view.findViewById(R.id.exo_subtitles);
        exoSubtitles.setVisibility(View.INVISIBLE);

        player = ExoPlayerFactory.newSimpleInstance(getContext());
        mPlayerView.setPlayer(player);

        Uri videoUri = Uri.parse(getString(R.string.video_url_mr_robot));
        Log.d(TAG, "video uri: " + videoUri.toString());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "Gamlang"));
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

        super.onViewCreated(view, savedInstanceState);
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

    private class ComponentListener implements TextOutput {
        @Override
        public void onCues(List<Cue> cues) {
            if (cues != null && !cues.isEmpty()) {
                String cue = cues.get(0).text.toString();
                Log.d(TAG, "onCues: " + cue);
                mSubtitles.setText(cue);
            }
        }
    }
}
