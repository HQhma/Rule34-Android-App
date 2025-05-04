package com.HQHMA.rule34;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.HQHMA.rule34.Utilities.ExoPlayerCache;
import com.HQHMA.rule34.Utilities.FileManager;
import com.HQHMA.rule34.Utilities.Utilities;

public class VideoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    /*ImageButton saveBtn;*/
    ImageButton downloadBtn;

    int height,width;
    ExoPlayer player;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
        player = null;
        finish();
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerView = findViewById(R.id.playerView);
/*
        saveBtn = findViewById(R.id.saveBtn);
*/
        downloadBtn = findViewById(R.id.downloadBtn);

        Bundle b = getIntent().getExtras();
        String url, name;
        if (b != null) {
            url = b.getString("video_url");
            name = b.getString("video_name");
            height = b.getInt("height");
            width = b.getInt("width");

            boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            if (isLandscape) {
                enterFullScreenMode();
                findViewById(R.id.bottomLayout).setVisibility(View.GONE);
            } else {
                exitFullScreenMode();
                findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
            }
            setVideoViewHeightAndWidth(playerView,height,width,isLandscape);

            DataSource.Factory dataSourceFactory = ExoPlayerCache.getDataSourceFactory(this);

            player = new ExoPlayer.Builder(this)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory)) // for cache
                    .build();

            playerView.setPlayer(player);
            playerView.setControllerShowTimeoutMs(2000);
            MediaItem mediaItem = MediaItem.fromUri(url);

            player.setMediaItem(mediaItem);

            player.setRepeatMode(Player.REPEAT_MODE_ALL);

            player.prepare();
            player.play();

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.pause();
                    FileManager.downloadFile(url,name,VideoPlayerActivity.this);
                }
            });

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Set bottomLayout visibility based on orientation
        if (isLandscape) {
            enterFullScreenMode();
            findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        } else {
            exitFullScreenMode();
            findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
        }

        // Adjust the video view size when orientation changes
        setVideoViewHeightAndWidth(playerView, height, width,isLandscape);
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView = findViewById(R.id.playerView);
        playerView.getPlayer().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void enterFullScreenMode() {
        // Hide the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Enable immersive full-screen mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        // Set the activity to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void exitFullScreenMode() {
        // Show the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        // Exit immersive full-screen mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        // Exit the activity to fullscreen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setVideoViewHeightAndWidth(PlayerView playerView, int height, int width,boolean isLandscape){
        int displayHeight = Utilities.getDisplayHeight(this);
        int displayWidth = Utilities.getDisplayWidth(this);

        ViewGroup.LayoutParams params = playerView.getLayoutParams();

        float ratio = (float) width / displayWidth;
        int final_height = (int) (height / ratio);
        int finalwidth = displayWidth;

        int target_height;

        if (isLandscape)
            target_height = displayHeight;
        else
            target_height = (displayHeight/4)*3;

        if (final_height > target_height){
            float ratio2 = (float) final_height / target_height;
            finalwidth = (int) (finalwidth / ratio2);
            final_height = target_height;
        }

        params.height = final_height;
        params.width = finalwidth;

        playerView.setLayoutParams(params);
    }
}