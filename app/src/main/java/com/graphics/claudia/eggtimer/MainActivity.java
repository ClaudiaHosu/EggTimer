package com.graphics.claudia.eggtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private static final String TIME_FORMAT = "mm:ss";

    //default is set to 5 mins
    private static final int DEFAULT_TIMER_IN_SECONDS = 5 * 60;
    private static final int MAX_TIMER_IN_SECONDS = 15 * 60;

    private int countDownInSecs = DEFAULT_TIMER_IN_SECONDS;
    private CountDownTimer countDownTimer;
    private MediaPlayer alarmPlayer;
    private boolean timerReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();
    }


    public void setUp() {
        setUpSeekBar();
        setUpTimer();
        setUpTimerStarter();
    }

    public void setUpTimer() {
        refreshTimer();
    }


    public void setUpSeekBar() {
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(MAX_TIMER_IN_SECONDS);
        seekBar.setProgress(DEFAULT_TIMER_IN_SECONDS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean manuallyHandled;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (manuallyHandled) {
                    countDownInSecs = seekBar.getProgress();
                    refreshTimer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                manuallyHandled = true;
                cancelTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manuallyHandled = false;
                countDownInSecs = seekBar.getProgress();
                refreshTimer();
            }
        });
    }

    public void setUpTimerStarter() {
        final Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerStarted();
            }
        });
    }

    private void timerStarted() {
        if (timerReady) {
            startTimer();
        } else {
            cancelTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(countDownInSecs * 1000, 1000) {
            @Override
            public void onTick(long l) {
                countDownInSecs = (int) l / 1000;
                refreshCountDowners();
            }

            @Override
            public void onFinish() {
                timerComplete();
            }
        };

        countDownTimer.start();
        toggleButton(false);
    }


    private void cancelTimer() {
        toggleButton(true);
        if (alarmPlayer != null) {
            alarmPlayer.stop();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void timerComplete() {
        alarmPlayer = MediaPlayer.create(this, R.raw.alarm);
        alarmPlayer.start();
    }

    private void toggleButton(boolean toggle) {
        Button startButton = findViewById(R.id.startButton);
        timerReady = toggle;
        startButton.setText(toggle ? R.string.start:R.string.stop);
    }

    private void refreshCountDowners() {
        refreshTimer();
        refreshSeekBar();
    }

    private void refreshTimer() {
        String defaultStartTime = formatTime(countDownInSecs);
        TextView timerView = findViewById(R.id.timerView);
        timerView.setText(defaultStartTime);
    }

    private void refreshSeekBar() {
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(countDownInSecs);
    }


    private static String formatTime(long seconds) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        LocalTime localTime = LocalTime.ofSecondOfDay(seconds);
        return localTime.format(timeFormatter);
    }
}