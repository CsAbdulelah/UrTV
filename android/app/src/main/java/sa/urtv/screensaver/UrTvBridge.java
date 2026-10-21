package sa.urtv.screensaver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.JavascriptInterface;

/** JavaScript interface exposed to the screensaver's WebView as `UrTvHost`.
 *  Forwards Quran play/stop requests to the launcher's QuranService so the
 *  recitation keeps running after the Dream is dismissed. */
public class UrTvBridge {
    private final Context ctx;

    public UrTvBridge(Context c) { this.ctx = c.getApplicationContext(); }

    @JavascriptInterface
    public boolean playQuran(String reciter, int surah) {
        try {
            Intent i = new Intent("sa.urtv.home.QURAN_PLAY");
            i.setComponent(new ComponentName("sa.urtv.home", "sa.urtv.home.QuranService"));
            i.putExtra("reciter", reciter);
            i.putExtra("surah", surah);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ctx.startForegroundService(i);
            else ctx.startService(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @JavascriptInterface
    public boolean stopQuran() {
        try {
            Intent i = new Intent("sa.urtv.home.QURAN_STOP");
            i.setComponent(new ComponentName("sa.urtv.home", "sa.urtv.home.QuranService"));
            ctx.startService(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
