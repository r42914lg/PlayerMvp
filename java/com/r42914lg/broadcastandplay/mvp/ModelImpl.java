package com.r42914lg.broadcastandplay.mvp;

public class ModelImpl implements Contract.Model {

    private int fileSize;
    private boolean downloadInProgress;

    @Override
    public void setFileSize(int total) {
        fileSize = total;
    }

    @Override
    public int getFileSize() {
        return fileSize;
    }

    @Override
    public boolean downloadInProgress() {
        return downloadInProgress;
    }

    @Override
    public void setDownLoadInProgress(boolean flag) {
        downloadInProgress = flag;
    }
}
