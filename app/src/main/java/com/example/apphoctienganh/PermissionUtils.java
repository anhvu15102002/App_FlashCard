package com.example.apphoctienganh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    // Kiểm tra xem có cần yêu cầu quyền thông báo hay không
    public static boolean checkNotificationPermission(Context context) {
        // Với Android 13 trở lên, cần yêu cầu quyền POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        // Với Android 12 trở xuống, không cần yêu cầu quyền này
        return true;
    }

    // Yêu cầu quyền thông báo
    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE);
        }
    }
}
