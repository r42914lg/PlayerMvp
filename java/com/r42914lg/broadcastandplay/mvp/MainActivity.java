package com.r42914lg.broadcastandplay.mvp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.r42914lg.broadcastandplay.Constants;
import com.r42914lg.broadcastandplay.MyApp;
import com.r42914lg.broadcastandplay.PermissionsHelper;
import com.r42914lg.broadcastandplay.R;
import com.r42914lg.broadcastandplay.service.DownloadService;
import com.r42914lg.broadcastandplay.service.PlayService;

public class MainActivity extends AppCompatActivity implements Contract.View {

    private Contract.Presenter presenter;

    private Button playButton;
    private Button cancelButton;
    private EditText editText;
    private ProgressBar progressBar;

    private boolean bound = false;
    private ServiceConnection sConn;
    private Controls playerControls;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new PermissionsHelper(this);

        attachPresenter();
        prepareToBindPlayerService();
    }

    private void attachPresenter() {
        presenter = ((MyApp) getApplication()).getPresenter();
        presenter.attachView(this);
    }

    private void prepareToBindPlayerService() {
        intent = new Intent(this, PlayService.class);

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                bound = true;
                playerControls = ((PlayService.MyBinder) binder).getService();
            }
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(intent,sConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(sConn);
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void init() {

        playButton = findViewById(R.id.play_button);
        cancelButton = findViewById(R.id.cancel_button);
        editText = findViewById(R.id.url_text);
        progressBar = findViewById(R.id.progress_horizontal);
        Button pauseResumeButton = findViewById(R.id.resume_pause_button);
        Button goButton = findViewById(R.id.download_button);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onInitiateDownload(editText.getText().toString());
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenter.onPlayCurrent();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onCancel();
            }
        });

        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerControls.pauseOrResume();
            }
        });

        editText.setEnabled(true);
        playButton.setEnabled(false);
        cancelButton.setEnabled(false);
        progressBar.setProgress(0);
    }

    @Override
    public void startDownloadService(String urlToLoad) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(Constants.URL_TO_LOAD, urlToLoad);
        startService(intent);
    }

    @Override
    public void showProgress(int percentReady) {
        progressBar.setProgress(percentReady);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enableEdit(boolean enableFlag) {
        editText.setEnabled(enableFlag);
    }

    @Override
    public void enablePlay(boolean enableFlag) {
        playButton.setEnabled(enableFlag);
    }

    @Override
    public void enableCancel(boolean enableFlag) {
        cancelButton.setEnabled(enableFlag);
    }
}