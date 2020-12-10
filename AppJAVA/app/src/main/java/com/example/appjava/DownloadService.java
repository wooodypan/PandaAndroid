package com.example.appjava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
public class DownloadService extends Service {
//    public DownloadService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    private DownloadTask downloadTask;

    private String downloadUrl;

    //001 首先这里创建了一个DownloadListener 的匿名类实例，
// 并在匿名类中实现了onProgress() 、onSuccess() 、onFailed() 、onPaused() 和onCanceled() 这5个方法
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            //002 构建了一个用于显示下载进度的通知，然后调用NotificationManager的notify() 方法去触发这个通知
            // 这样就可以在下拉状态栏中实时看到当前下载的进度了
            getNotificationManager().notify(1, getNotification("PW Downloading..."+Integer.toString(progress),
                    progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            // 003 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 004 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
        }

    };
// 005 为了要让DownloadService可以和活动进行通信，我们又创建了一个DownloadBinder。
// DownloadBinder中提供了startDownload() 、pauseDownload() 和cancelDownload() 这3个方法
// 分别用于开始下载、暂停下载和取消下载
    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class DownloadBinder extends Binder {

        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                //006 调用execute() 方法开启下载，并将下载文件的URL地址传入到execute() 方法中
                downloadTask.execute(downloadUrl);
                //007 同时，为了让这个下载服务成为一个前台服务，我们还调用了startForeground() 方法，这样就会在系统状态栏中创建一个持续运行的通知了。
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }
        //008 暂停下载
        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }
        // 009 取消下载。
        // 取消下载的时候我们需要将正在下载的文件删除掉，这一点和暂停下载是不同的
        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title, int progress) {
        Log.d("====下载进度：",Integer.toString(progress));

        Intent intent = new Intent(this, DownloadActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //https://stackoverflow.com/a/51281297
        // Java 解决方案(android9.0，api28)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String NOTIFICATION_CHANNEL_ID = "com.example.appjava";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            //NotificationCompat.Builder 构造函数要求您提供渠道 ID。
            // 这是兼容 Android 8.0（API 级别 26）及更高版本所必需的，但会被较旧版本忽略
            // https://developer.android.com/training/notify-user/build-notification?hl=zh-cn
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        }







        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            //setProgress() 方法接收3个参数，第一个参数传入通知的最大进度，第二个参数传入通知的当前进度，
            // 第三个参数表示是否使用模糊进度条，这里传入false 。设置完setProgress() 方法，通知上就会有进度条显示出来了。
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }



}
