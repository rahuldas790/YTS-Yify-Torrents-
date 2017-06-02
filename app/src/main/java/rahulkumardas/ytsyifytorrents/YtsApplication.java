package rahulkumardas.ytsyifytorrents;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.v4.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Rahul Kumar Das on 01-06-2017.
 */

public class YtsApplication extends Application {
    public static boolean AtoSearchEnabled = false;
    public static int Span = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        AtoSearchEnabled = getSharedPreferences("settings",MODE_PRIVATE).getBoolean("auto", false);
        Span = getSharedPreferences("settings", MODE_PRIVATE).getInt("span", 0)+2;
        Fabric.with(this, new Crashlytics());
    }
}
