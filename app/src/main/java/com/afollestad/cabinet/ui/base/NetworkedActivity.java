package com.afollestad.cabinet.ui.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.afollestad.cabinet.R;
import com.afollestad.cabinet.file.CloudFile;
import com.afollestad.cabinet.file.base.File;
import com.afollestad.cabinet.services.NetworkService;
import com.afollestad.cabinet.utils.ThemeUtils;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class NetworkedActivity extends ThemableActivity {

    private NetworkService mNetworkService;
    private CloudFile mRemoteSwitch; // used by SFTP notification intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processIntent(getIntent(), savedInstanceState);
    }

    public CloudFile getRemoteSwitch() {
        return mRemoteSwitch;
    }

    protected void displayDisconnectPrompt() {
        String host = getString(R.string.unknown);
        if (mRemoteSwitch != null)
            host = mRemoteSwitch.getRemote().getHost();
        new MaterialDialog.Builder(this)
                .positiveColorRes(R.color.cabinet_accent_color)
                .theme(ThemeUtils.getDialogTheme(this))
                .title(R.string.disconnect)
                .content(R.string.disconnect_prompt, host)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        startService(new Intent(NetworkedActivity.this, NetworkService.class)
                                .setAction(NetworkService.DISCONNECT_SFTP));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .build().show();
    }

    protected boolean disconnectOnNotify() {
        return true;
    }

    protected void processIntent(Intent intent, Bundle savedInstanceState) {
        if (intent.hasExtra("remote")) {
            mRemoteSwitch = (CloudFile) intent.getSerializableExtra("remote");
            if (mRemoteSwitch == null) return;
            mRemoteSwitch.setContext(this);
            if (mNetworkService != null) {
                if (disconnectOnNotify()) {
                    switchDirectory(mRemoteSwitch, true);
                    displayDisconnectPrompt();
                    mRemoteSwitch = null;
                }
            }
        }
    }

    public void switchDirectory(File to, boolean clearBackStack) {
        // Only overridden by the DrawerActivity
    }

    //
    // SERVICE CONNECTION
    //

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    public NetworkService getNetworkService() {
        return mNetworkService;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            mNetworkService = binder.getService();
            if (mRemoteSwitch != null) {
                if (disconnectOnNotify()) {
                    switchDirectory(mRemoteSwitch, true);
                    displayDisconnectPrompt();
                    mRemoteSwitch = null;
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
