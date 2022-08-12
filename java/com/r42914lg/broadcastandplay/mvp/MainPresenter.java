package com.r42914lg.broadcastandplay.mvp;

import android.content.Context;
import android.content.Intent;
import android.webkit.URLUtil;

import com.r42914lg.broadcastandplay.R;
import com.r42914lg.broadcastandplay.service.DownloadService;
import com.r42914lg.broadcastandplay.service.PlayService;

public class MainPresenter implements Contract.Presenter {

    private Contract.View mainView;
    private final Contract.Model model;
    private final Context context;

    public MainPresenter(Contract.Model model, Context context) {
        this.model = model;
        this.context = context;
    }

    @Override
    public void attachView(Contract.View view) {
        this.mainView = view;
        mainView.init();
    }

    @Override
    public void detachView() {
        this.mainView = null;
    }

    @Override
    public void onPlayCurrent() {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(PlayService.ACTION_PLAY);
        context.startForegroundService(intent);
    }

    @Override
    public void onCancel() {
        Intent sIntent = new Intent();
        sIntent.setAction(DownloadService.StopReceiver.ACTION_STOP);
        context.sendBroadcast(sIntent);
    }

    @Override
    public void onInitiateDownload(String urlText) {
        if (mainView == null)
            return;

        if (!URLUtil.isValidUrl(urlText)) {
            mainView.showToast(context.getString(R.string.wrong_url_message));
            return;
        }

        if (model.downloadInProgress()) {{
            mainView.showToast(context.getString(R.string.download_in_progress_msg));
            return;
        }}

        model.setDownLoadInProgress(true);
        mainView.enableEdit(false);
        mainView.enablePlay(false);
        mainView.enableCancel(true);
        mainView.startDownloadService(urlText);
    }

    @Override
    public void setFileLength(int fileLength) {
        model.setFileSize(fileLength);
    }

    @Override
    public void setDownloadedLength(int downloadedLength) {
        if (mainView == null)
            return;

        mainView.showProgress((int) ((1f * downloadedLength / model.getFileSize()) * 100));
    }

    @Override
    public void setDownloadCompleted() {
        model.setDownLoadInProgress(false);

        if (mainView != null) {
            mainView.enableEdit(true);
            mainView.enablePlay(true);
            mainView.enableCancel(false);
            mainView.showToast(context.getString(R.string.download_complete_msg));
        }

        onPlayCurrent();
    }

    @Override
    public void setDownloadCanceled() {
        model.setDownLoadInProgress(false);

        if (mainView == null)
            return;

        mainView.enableEdit(true);
        mainView.enablePlay(false);
        mainView.enableCancel(false);
        mainView.showProgress(0);
    }
}
