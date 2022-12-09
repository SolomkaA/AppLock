package com.example.applock;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.os.Build;

import android.util.Log;

public class RestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("Broadcast Listened", "Service tried to stop");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ForegroundService.class));
            } else {
                context.startService(new Intent(context, ForegroundService.class));
            }
        }
        Log.i("Broadcast Listened", "Service tried to stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ForegroundService.class));
        } else {
            context.startService(new Intent(context, ForegroundService.class));
        }
    }
}