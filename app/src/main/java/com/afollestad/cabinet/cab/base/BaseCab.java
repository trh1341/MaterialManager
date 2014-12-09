package com.afollestad.cabinet.cab.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.cabinet.R;
import com.afollestad.cabinet.fragments.DirectoryFragment;
import com.afollestad.cabinet.ui.DrawerActivity;

import java.io.Serializable;

public abstract class BaseCab implements ActionMode.Callback, Serializable {

    public BaseCab() {
    }

    private transient ActionMode mActionMode;
    private transient Activity context;
    private transient DirectoryFragment fragment;

    public final BaseCab start() {
        getContext().startSupportActionMode(this);
        return this;
    }

    public BaseCab setContext(Activity context) {
        this.context = context;
        invalidate();
        return this;
    }

    public BaseCab setFragment(DirectoryFragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        invalidate();
        return this;
    }

    public final boolean isActive() {
        return mActionMode != null;
    }

    public DirectoryFragment getFragment() {
        return fragment;
    }

    public DrawerActivity getContext() {
        return (DrawerActivity) context;
    }

    public abstract int getMenu();

    public abstract CharSequence getTitle();

    public void invalidate() {
        if (mActionMode != null) mActionMode.invalidate();
    }

    public final void finish() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        if (getMenu() != -1)
            actionMode.getMenuInflater().inflate(getMenu(), menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getContext().getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.statusbar_color_cab));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        actionMode.setTitle(getTitle());
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        finish();
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getContext().getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.cabinet_color_darker));
        mActionMode = null;
    }
}
