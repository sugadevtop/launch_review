package com.iyaffle.launchreview;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * LaunchReviewPlugin
 */
public class LaunchReviewPlugin implements FlutterPlugin {

    private MethodChannel channel;

    public static void registerWith(Registrar registrar) {
        final LaunchReviewPlugin plugin = new LaunchReviewPlugin();
        plugin.setupChannel(registrar.messenger(), registrar.activeContext());
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        setupChannel(binding.getBinaryMessenger(), binding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        teardownChannel();
    }

    private void setupChannel(BinaryMessenger messenger, Context context) {
        channel = new MethodChannel(messenger, "launch_review");
        channel.setMethodCallHandler(new LaunchReviewMethodCallHandler(context));
    }

    private void teardownChannel() {
        channel.setMethodCallHandler(null);
        channel = null;
    }
}
