package com.r42914lg.broadcastandplay;

import android.app.Application;

import com.r42914lg.broadcastandplay.mvp.Contract;
import com.r42914lg.broadcastandplay.mvp.MainPresenter;
import com.r42914lg.broadcastandplay.mvp.ModelImpl;

public class MyApp extends Application {
    private Contract.Presenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();
        presenter = new MainPresenter(
                new ModelImpl(),
                this);
    }

    public Contract.Presenter getPresenter() {
        return presenter;
    }
}
