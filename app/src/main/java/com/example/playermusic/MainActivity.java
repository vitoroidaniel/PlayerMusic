package com.example.playermusic;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private final int[] songs = {
            R.raw.music1, R.raw.music2,
            R.raw.music3, R.raw.chernieglaza,
            R.raw.kurwabober, R.raw.moscowneversleep,
            R.raw.nicolaegutanunta};
    private int currentSongIndex = 0;
    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvCurrentMusic; // New TextView for current music name
    private Handler handler = new Handler();
    private ImageButton playButton;
    private ImageButton pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex]);

        playButton = findViewById(R.id.btnPlay);
        pauseButton = findViewById(R.id.btnPause);
        ImageButton nextButton = findViewById(R.id.btnNext);
        ImageButton prevButton = findViewById(R.id.btnPrevious);

        seekBar = findViewById(R.id.seekBar); // Seek Bar
        tvCurrentTime = findViewById(R.id.tvCurrentTime); // Shows the current music time
        tvCurrentMusic = findViewById(R.id.tvCurrentMusic); // Initialize the TextView
        seekBar.setMax(mediaPlayer.getDuration()); // get the duration of the music

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

        handler.postDelayed(updateSeekBar, 1000); // Start updating seek bar every second
    }

    // Runnable to update seek bar and check for music completion
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                tvCurrentTime.setText(formatTime(currentPosition));

                // Check if the music has completed
                if (currentPosition >= mediaPlayer.getDuration()) {
                    stop();
                    next();
                }
            }
            handler.postDelayed(this, 1000); // Schedule the next update
        }
    };

    private void play() {
        if (!mediaPlayer.isPlaying()) {
            if (mediaPlayer.getCurrentPosition() > 0) {
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex]);
                mediaPlayer.start();
            }
            playButton.setVisibility(View.INVISIBLE);
            pauseButton.setVisibility(View.VISIBLE);

            // Set the current music name in the TextView
            tvCurrentMusic.setText(getCurrentMusicName());
        }
    }

    private void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
        }
    }

    private void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private void next() {
        if (currentSongIndex < songs.length - 1) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0;
        }
        stop();
        play();
    }

    private void previous() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            currentSongIndex = songs.length - 1;
        }
        stop();
        play();
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String getCurrentMusicName() {
        String[] musicNames = {"Music 1", "Music 2", "Cola x Tequila", "Chernie Glaza", "Kurwa Bober", "Moscow Never Sleep", "Nicolae Guta - Nunta"};
        return musicNames[currentSongIndex];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar); // Remove the updateSeekBar Runnable
        mediaPlayer.release();
    }
}
