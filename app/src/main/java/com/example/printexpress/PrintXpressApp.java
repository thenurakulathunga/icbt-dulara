package com.example.printexpress;

import android.app.Application;

import com.example.printexpress.util.NotificationHelper;

/** Runs once when the app process starts. */
public class PrintXpressApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createChannel(this);
    }
}
