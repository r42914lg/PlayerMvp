package com.r42914lg.broadcastandplay.mvp;

public interface Contract {

    interface View {
        void init();
        void showProgress(int percentReady);
        void showToast(String text);
        void enableEdit(boolean enableFlag);
        void enablePlay(boolean enableFlag);
        void enableCancel(boolean enableFlag);
        void startDownloadService(String urlToLoad);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void onPlayCurrent();
        void onCancel();
        void onInitiateDownload(String urlText);
        void setFileLength(int fileLength);
        void setDownloadedLength(int downloadedLength);
        void setDownloadCompleted();
        void setDownloadCanceled();
    }

    interface Model {
        void setFileSize(int total);
        int getFileSize();
        boolean downloadInProgress();
        void setDownLoadInProgress(boolean flag);
    }
}
