package com.sdirin.java.newstracker.data.network;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PersistableBundle;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.data.SelectedSources;

/**
 * Created by User on 14.02.2018.
 */

public class InternetCommon {

    public static final String SELECTED_SOURCES = "selected_sources";
    public static final int INTERNET_LOADER_ID = 2;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static void startInternetLoader(Context context) {
        if (isInternetAvailable(context)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                PersistableBundle extra = null;
                extra = new PersistableBundle();
                extra.putString(SELECTED_SOURCES,new SelectedSources(context).getSelectedSources());
                jobScheduler.schedule(new JobInfo.Builder(
                        INTERNET_LOADER_ID,
                        new ComponentName(context, InternetLoader.class)
                ).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPeriodic(60 * 60 * 1000) //every hour
                        .setPersisted(true)
                        .setExtras(extra)
                        .build());
            } else {
                Intent service = new Intent(context, InternetLoaderService.class);
                context.startService(service);
            }
        }
    }


    public static boolean isInternetAvailable(Context context) {

        if (getConnectivityStatus(context) == TYPE_NOT_CONNECTED){
            Toast.makeText(context, R.string.unavailable_network, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getConnectivityStatus(context) == TYPE_MOBILE) {
            Toast.makeText(context, R.string.wifi_not_available, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }
}
