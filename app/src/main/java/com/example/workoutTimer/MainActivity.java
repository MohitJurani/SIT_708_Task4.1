package com.example.workoutTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView tvRemainingTime, tvRemainingRelaxTime;
    private Button btnStart, btnStop;

    private EditText etTimeInMin, etTimeInRest;
    private ProgressBar progressBar;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        tvRemainingTime = findViewById(R.id.tvRemainingTime);
        progressBar = findViewById(R.id.progressbar);
        etTimeInMin = findViewById(R.id.et_1);
        etTimeInRest= findViewById(R.id.et_2);
        tvRemainingRelaxTime = findViewById(R.id.tvRemainingRelaxTime);
        btnStart.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etTimeInMin.getText().toString())){
                Toast.makeText(this, "Please enter time in minutes", Toast.LENGTH_LONG).show();
            } else {
                int time = Integer.parseInt(etTimeInMin.getText().toString());
                setCounterTimer(time * 60 * 1000);
            }
        });
        btnStop.setOnClickListener(v -> {
            stopTimer();
        });
    }

    private void stopTimer() {
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
            //notifyStartStop("Timer stopped", "Get Relax..");
        }
    }

    private void setCounterTimer(long interval) {
        vibrateDevice(1000);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        progressBar.setMax((int) interval);

        mCountDownTimer = new CountDownTimer(interval, 1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long minutes = (leftTimeInMilliseconds / 1000) / 60;
                long seconds = (leftTimeInMilliseconds / 1000) % 60;
                progressBar.setProgress((int) (interval - leftTimeInMilliseconds));
                tvRemainingTime.setText("Exercise Time remaining:"+String.format("%02d", minutes) +
                        ":" + String.format("%02d",
                        seconds));
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                tvRemainingTime.setText("Done");
                vibrateDevice(2000);
                //notifyStartStop("Timer stopped", "Get Relax..");
                int time = Integer.parseInt(etTimeInRest.getText().toString());
                setRelaxCounterTimer(time * 60 * 1000);
            }
        }.start();

        notifyStartStop("Timer Started", "Get Ready..");
    }

    private void vibrateDevice(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // For devices running Android Oreo or newer
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // For devices running older versions of Android
                vibrator.vibrate(milliseconds);
            }
        }
    }

    private void notifyStartStop(String title, String msg) {
        // Create a notification channel
        String channelId = "my_channel_id";
        String channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
        }

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Register the notification channel
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(1, builder.build());
    }


    private void setRelaxCounterTimer(long interval) {
        //vibrateDevice(1000);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        progressBar.setMax((int) interval);

        mCountDownTimer = new CountDownTimer(interval, 1000) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long minutes = (leftTimeInMilliseconds / 1000) / 60;
                long seconds = (leftTimeInMilliseconds / 1000) % 60;
                progressBar.setProgress((int) (interval - leftTimeInMilliseconds));
                tvRemainingRelaxTime.setText("Rest/Relax Time remaining:"+String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }


            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                tvRemainingTime.setText("Done");
                vibrateDevice(2000);
                notifyStartStop("Relax time over..", "Get Ready..");
            }
        }.start();

        notifyStartStop("Relax time Started", "Get Relax..");
    }
}