package com.example.ishow.justalk.cloud.juscall;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class MiuiUtils {

    public static final String ROM_MIUI_V6 = "V6";

    public static boolean isMiui() {
        return !ROM_UNKNOWN.equals(getRom());
    }

    public static boolean isMiuiV6OrHigher() {
        String rom = getRom();
        if (TextUtils.isEmpty(rom) || !rom.startsWith("V")) {
            return false;
        }
        int version = Integer.valueOf(rom.substring(1));
        return version >= 6;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
        } else {
            return (context.getApplicationInfo().flags & (1 << 27)) != 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method checkOpMethod = appOpsManager.getClass().getMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (Integer) checkOpMethod.invoke(appOpsManager, op,
                        Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getRom() {
        String line;
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = reader.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ROM_UNKNOWN;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0;
    }

    public static void openMiuiPermissionActivity(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        String rom = getRom();

        if (ROM_MIUI_V6.equals(rom)) {
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
        }

        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        }
    }

    public static boolean needShowAlertPermissionDialog(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return (MiuiUtils.isMiuiV6OrHigher()
                && MtcCallDelegate.isCalling()
                && !MiuiUtils.isFloatWindowOpAllowed(context))
                && sp.getInt(KEY_ALERT_PERMISSION_DIALOG_SESS_ID, -1) != MtcCallDelegate.getCallId();
    }

    public static void setAlertPermissionDialogShowed(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(KEY_ALERT_PERMISSION_DIALOG_SESS_ID, MtcCallDelegate.getCallId()).apply();
    }


    public static void openMiuiAutoStartPermissionActivity(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("miui.intent.action.OP_AUTO_START");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean needShowAutoStartPermissionDialog(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return MiuiUtils.isMiuiV6OrHigher() && !sp.getBoolean(KEY_AUTO_START_PERMISSION_SHOWED, false);
    }

    public static void setAutoStartPermissionDialogShowed(Context context, boolean showed) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(KEY_AUTO_START_PERMISSION_SHOWED, showed).apply();
    }

    private static final int OP_SYSTEM_ALERT_WINDOW = 24;

    private static final String KEY_ALERT_PERMISSION_DIALOG_SESS_ID = "key_alert_permission_dialog_sess_id";
    private static final String KEY_AUTO_START_PERMISSION_SHOWED = "key_auto_start_permission_showed";

    private static final String ROM_UNKNOWN = "UNKNOWN";
}
