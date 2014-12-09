package com.afollestad.cabinet.fragments;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.cabinet.R;
import com.afollestad.cabinet.sftp.SftpClient;
import com.afollestad.cabinet.ui.DrawerActivity;
import com.afollestad.cabinet.utils.Pins;
import com.afollestad.cabinet.utils.ThemeUtils;
import com.afollestad.cabinet.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class RemoteConnectionDialog implements SftpClient.CompletionCallback {

    public RemoteConnectionDialog(Activity context) {
        mContext = context;
    }

    private Activity mContext;
    private SftpClient client;
    private MaterialDialog dialog;
    private Button testConnection;
    private TextView host;
    private TextView port;
    private TextView user;
    private TextView pass;

    public void show() {
        Utils.lockOrientation(mContext);
        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_add_remote, null);
        testConnection = (Button) view.findViewById(R.id.testConnection);
        host = (TextView) view.findViewById(R.id.host);
        port = (TextView) view.findViewById(R.id.port);
        user = (TextView) view.findViewById(R.id.user);
        pass = (TextView) view.findViewById(R.id.pass);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (host.getText().toString().trim().length() > 0 &&
                        port.getText().toString().trim().length() > 0 &&
                        user.getText().toString().trim().length() > 0 &&
                        pass.getText().toString().trim().length() > 0) {
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    testConnection.setEnabled(true);
                } else {
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    testConnection.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        host.addTextChangedListener(watcher);
        port.addTextChangedListener(watcher);
        user.addTextChangedListener(watcher);
        pass.addTextChangedListener(watcher);
        testConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                host.setEnabled(false);
                port.setEnabled(false);
                user.setEnabled(false);
                pass.setEnabled(false);
                client = new SftpClient()
                        .setHost(host.getText().toString().trim(), Integer.parseInt(port.getText().toString().trim()))
                        .setUser(user.getText().toString())
                        .setPass(pass.getText().toString())
                        .connect(RemoteConnectionDialog.this);
            }
        });

        dialog = new MaterialDialog.Builder(mContext)
                .positiveColorRes(R.color.cabinet_accent_color)
                .theme(ThemeUtils.getDialogTheme(mContext))
                .title(R.string.new_remote_connection)
                .customView(view)
                .cancelable(false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Utils.unlockOrientation(mContext);
                        onSubmit();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Utils.unlockOrientation(mContext);
                        if (client != null && client.isConnected()) client.disconnect();
                        client = null;
                    }
                }).build();
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        dialog.show();
    }

    @Override
    public void onComplete() {
        if (client == null) return;
        client.disconnect();
        client = null;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testConnection.setEnabled(true);
                host.setEnabled(true);
                port.setEnabled(true);
                user.setEnabled(true);
                pass.setEnabled(true);
                testConnection.setText(R.string.connection_successful);
            }
        });
    }

    @Override
    public void onError(final Exception e) {
        if (client == null) return;
        client = null;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testConnection.setEnabled(true);
                host.setEnabled(true);
                port.setEnabled(true);
                user.setEnabled(true);
                pass.setEnabled(true);
                testConnection.setText(e.getMessage());
            }
        });
    }

    protected void onSubmit() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        Pins.add(mContext, new Pins.Item(
                host.getText().toString().trim(),
                Integer.parseInt(port.getText().toString().trim()),
                user.getText().toString().trim(),
                pass.getText().toString().trim(),
                "/"
        ));
        ((DrawerActivity) mContext).reloadNavDrawer(true);
    }
}
