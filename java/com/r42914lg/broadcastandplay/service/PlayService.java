package com.r42914lg.broadcastandplay.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.r42914lg.broadcastandplay.Constants;
import com.r42914lg.broadcastandplay.R;
import com.r42914lg.broadcastandplay.mvp.Controls;
import com.r42914lg.broadcastandplay.mvp.MainActivity;

import java.io.File;

public class PlayService extends Service implements Controls {
    public static final int ONGOING_NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "my_service";
    public static final String CHANNEL_NAME = "My Background Service";
    public static final String ACTION_PLAY = "P_PLAY";
    public static final String ACTION_STOP = "P_STOP";
    public static final String ACTION_RESUME = "P_RESUME";

    public class MyBinder extends Binder {
        public Controls getService() {
            return PlayService.this;
        }
    }

    private final MyBinder binder = new MyBinder();

    private MediaPlayer mp;
    private boolean isPlaying;
    private boolean isOnPause;

    @Override
    public void onCreate() {
        initMediaPlayer();
        initForeground(CHANNEL_ID,CHANNEL_NAME);
        isPlaying = false;
    }

    private void initMediaPlayer() {
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onTerminate();
            }
        });
    }

    private void initForeground(String channelId, String channelName) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, PlayService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent resumeIntent = new Intent(this, PlayService.class);
        resumeIntent.setAction(ACTION_RESUME);
        PendingIntent resumePendingIntent =
                PendingIntent.getService(this, 0, resumeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.notification_title))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.action_stop), stopPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.action_pause_resume), resumePendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case ACTION_PLAY:
                onPlay();
                break;
            case ACTION_STOP:
                onTerminate();
                break;
            case ACTION_RESUME:
                pauseOrResume();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return START_STICKY;
    }

    private void onPlay() {
        if (isPlaying) {
            mp.stop();
            mp.release();
            initMediaPlayer();
        }

        try {
            File file = new File(getExternalCacheDir(), Constants.FILE_NAME);

            mp.setDataSource(file.getCanonicalPath());
            mp.prepare();
            mp.start();

            isPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTerminate() {
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public void pauseOrResume() {
        if (isOnPause) {
            mp.start();
        } else {
            mp.pause();
        }
        isOnPause = !isOnPause;
    }
}
