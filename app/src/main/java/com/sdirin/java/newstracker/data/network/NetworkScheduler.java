package com.sdirin.java.newstracker.data.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkScheduler extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.d(MainActivity.TAG, "Internet Loader broadcast received");
        Intent service = new Intent(context, InternetLoaderService.class);
        context.startService(service);
    }
}
