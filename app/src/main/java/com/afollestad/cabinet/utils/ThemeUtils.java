package com.afollestad.cabinet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.afollestad.cabinet.R;
import com.afollestad.materialdialogs.Theme;

public class ThemeUtils {

    public ThemeUtils(Activity context) {
        mContext = context;
        isChanged(); // invalidate stored booleans
    }

    private Context mContext;
    private boolean darkMode;
    private boolean trueBlack;

    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("dark_mode", false);
    }

    public static boolean isTrueBlack(Context context) {
        if (!isDarkMode(context)) return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("true_black", false);
    }

    public static Theme getDialogTheme(Context context) {
        if (isDarkMode(context) || isTrueBlack(context)) return Theme.DARK;
        else return Theme.LIGHT;
    }

    public boolean isChanged() {
        boolean darkTheme = isDarkMode(mContext);
        boolean blackTheme = isTrueBlack(mContext);

        boolean changed = darkMode != darkTheme || trueBlack != blackTheme;
        darkMode = darkTheme;
        trueBlack = blackTheme;
        return changed;
    }

    public int getCurrent() {
        if (trueBlack) {
            return R.style.Theme_CabinetTrueBlack;
        } else if (darkMode) {
            return R.style.Theme_CabinetDark;
        } else {
            return R.style.Theme_Cabinet;
        }
    }
}
