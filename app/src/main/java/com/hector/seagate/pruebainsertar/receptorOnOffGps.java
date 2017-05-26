package com.hector.seagate.pruebainsertar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by seagate on 15/05/2017.
 */
public class receptorOnOffGps extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        {
            // react on GPS provider change action
            //start activity
            Intent i = new Intent();
            i.setClassName("com.hector.seagate.pruebainsertar", "com.hector.seagate.pruebainsertar.MainActivity");

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }

    }
}
