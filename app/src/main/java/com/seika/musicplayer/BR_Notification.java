package com.seika.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.File;

/**
 * Created by Chou Seika on 1/8/2017.
 */

public class BR_Notification extends BroadcastReceiver {

    static int id = 70000;

    @Override
    public void onReceive(Context context, Intent intent) {

        String msg = intent.getStringExtra("KEY_MSG");
        int progressMax = intent.getIntExtra("progressMax", 1);
        int progress = intent.getIntExtra("progress", 1);
        boolean indeterminate = false;
        String tmp = intent.getStringExtra("cover");
        File imgFile = new File(tmp);
        /*if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        } else {
            myBitmap = R.drawable.music;
        }*/
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder(context.getApplicationContext())
                .setSmallIcon(R.drawable.music)
                .setContentTitle(msg)
                .setContentText("MusicPlayer")
                .setProgress(progressMax, progress, indeterminate).build();;
        notificationManager.notify(id, notify);
    }
}
