package com.r42914lg.broadcastandplay.service;

import static com.r42914lg.broadcastandplay.Constants.URL_TO_LOAD;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.r42914lg.broadcastandplay.Constants;
import com.r42914lg.broadcastandplay.MyApp;
import com.r42914lg.broadcastandplay.mvp.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
    public static final String SERVICE_NAME = "AudioDownloader";

    public class StopReceiver extends BroadcastReceiver {
        public static final String ACTION_STOP = "stop";
        @Override
        public void onReceive(Context context, Intent intent) {
            cancelFlag = true;
        }
    }

    private boolean cancelFlag;
    private StopReceiver receiver;
    private Handler mHandler;
    private Contract.Presenter presenter;

    public DownloadService() {
        super(SERVICE_NAME);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mHandler = new Handler();
        presenter = ((MyApp) getApplication()).getPresenter();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        IntentFilter filter = new IntentFilter(StopReceiver.ACTION_STOP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new StopReceiver();
        registerReceiver(receiver, filter);

        downloadFile(intent.getStringExtra(URL_TO_LOAD));
    }

    private void downloadFile(String urlText) {

        try {
            URL url = new URL(urlText);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();

            File file = new File(getExternalCacheDir(), Constants.FILE_NAME);
            FileOutputStream fileOutput = new FileOutputStream(file);

            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();

            mHandler.post(new Runnable() {
                public void run() {
                    presenter.setFileLength(totalSize);
                }
            });

            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            int downloadedSize = 0;
            boolean stop =  false;

            while ((bufferLength = inputStream.read(buffer)) > 0 && !stop) {

                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

                int finalDownloadedSize = downloadedSize;
                mHandler.post(new Runnable() {
                    public void run() {
                        presenter.setDownloadedLength(finalDownloadedSize);
                    }
                });

                if (cancelFlag) {
                    stop = true;
                    stopSelf();
                    mHandler.post(new Runnable() {
                        public void run() {
                            presenter.setDownloadCanceled();
                        }
                    });
                }
            }

            fileOutput.close();

            if (!stop) {
                mHandler.post(new Runnable() {
                    public void run() {
                        presenter.setDownloadCompleted();
                    }
                });
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
