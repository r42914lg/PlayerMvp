package com.r42914lg.broadcastandplay;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Map;


public class PermissionsHelper {
    public static final String TAG = "LG> PermissionsHelper";

    private final AppCompatActivity appCompatActivity;

    private final String [] permissions = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };

    private final ActivityResultLauncher<String[]> requestPermissionLauncher;

    public PermissionsHelper(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;

        requestPermissionLauncher =  appCompatActivity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if (result.containsValue(false)) {
                            Log.d(TAG, ".onActivityResult --> NOT ALL permissions granted");
                        } else {
                            Log.d(TAG, ".onActivityResult --> ALL permissions granted");
                        }
                    }
                });

        Log.d(TAG, "  Permission helper instance created");
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, ".checkPermissions PASSED, calling onPermissionsCheckPassed() on VM");

        } else {
                Log.d(TAG, ".checkPermissions --> requesting permissions via activity");
            requestPermissionLauncher.launch(permissions);
        }
    }
}

