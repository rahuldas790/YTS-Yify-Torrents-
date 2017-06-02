package rahulkumardas.ytsyifytorrents.Utils;

import android.content.Context;

/**
 * Created by Rahul Kumar Das on 08-05-2017.
 */

public class UnitUtils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}