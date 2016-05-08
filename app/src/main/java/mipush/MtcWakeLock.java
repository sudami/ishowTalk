package mipush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

public class MtcWakeLock {

    @SuppressLint("Wakelock")
    public static void acquire(Context context, long timeout) {
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        } else if (sWakeLock.isHeld()) {
            sWakeLock.release();
        }
        if (timeout < 0) {
            sWakeLock.acquire();
        } else {
            sWakeLock.acquire(timeout);
        }
    }
    
    public static void release() {
        if (sWakeLock != null && sWakeLock.isHeld()) {
            sWakeLock.release();
        }
    }
    
    private static PowerManager.WakeLock sWakeLock;
}
