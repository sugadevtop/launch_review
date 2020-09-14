package com.iyaffle.launchreview;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class LaunchReviewMethodCallHandler implements MethodChannel.MethodCallHandler {
    
    private final Context mContext;

    public LaunchReviewMethodCallHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("launch")) {
            String appId = call.argument("android_id");

            if (appId == null) {
                appId = mContext.getPackageName();
            }

            Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appId));
            boolean marketFound = false;

            // find all applications able to handle our rateIntent
            final List<ResolveInfo> otherApps =  mContext.getPackageManager()
                    .queryIntentActivities(rateIntent, 0);
            for (ResolveInfo otherApp: otherApps) {
                // look for Google Play application
                if (otherApp.activityInfo.applicationInfo.packageName
                        .equals("com.android.vending")) {

                    ActivityInfo otherAppActivity = otherApp.activityInfo;
                    ComponentName componentName = new ComponentName(
                            otherAppActivity.applicationInfo.packageName,
                            otherAppActivity.name
                    );
                    // make sure it does NOT open in the stack of your activity
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // task reparenting if needed
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    // if the Google Play was already open in a search result
                    //  this make sure it still go to the app page you requested
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // this make sure only the Google Play app is allowed to
                    // intercept the intent
                    rateIntent.setComponent(componentName);
                    Toast.makeText(mContext, "Please Rate Application", Toast.LENGTH_SHORT).show();

                    mContext.startActivity(rateIntent);
                    marketFound = true;
                    break;

                }
            }

            // if GP not present on device, open web browser
            if (!marketFound) {
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appId)));
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appId)));
                }
            }
            result.success(null);
        }  else {
            result.notImplemented();
        }
    }
}
