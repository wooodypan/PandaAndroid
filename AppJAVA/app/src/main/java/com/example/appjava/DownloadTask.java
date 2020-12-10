package com.example.appjava;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//D001 第一个泛型参数指定为String ，表示在执行AsyncTask的时候需要传入一个字符串参数给后台任务；
// 第二个泛型参数指定为Integer ，表示使用整型数据来作为进度显示单位；
// 第三个泛型参数指定为Integer ，则表示使用整型数据来反馈执行结果。
public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    //D002 下载的状态
    //TYPE_SUCCESS 表示下载成功，TYPE_FAILED 表示下载失败，TYPE_PAUSED 表示暂停下载，TYPE_CANCELED 表示取消下载
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;


    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    //D003-1 在后台执行具体的下载逻辑
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0; // 记录已下载的文件长度
            //首先我们从参数中获取到了下载的URL地址，并根据URL地址解析出了下载的文件名
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            //然后指定将文件下载到Environment.DIRECTORY_DOWNLOADS目录下，也就是SD卡的Download目录
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            //我们还要判断一下Download目录中是不是已经存在要下载的文件，
            // 如果已经存在的话则读取已下载的字节数，这样就可以在后面启用断点续传的功能。
            if (file.exists()) {
                downloadedLength = file.length();
            }
            //获取待下载文件的总长度，如果文件长度等于0则说明文件有问题，直接返回TYPE_FAILED
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                // 已下载字节和文件总字节相等，说明已经下载完成了
                return TYPE_SUCCESS;
            }
            //紧接着使用OkHttp来发送一条网络请求
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                //接下来读取服务器响应的数据，并使用Java的文件流方式，不断从网络上读取数据，不断写入到本地，一直到文件全部下载完成为止。
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength); // 跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    //在这个过程中，我们还要判断用户有没有触发暂停或者取消的操作，如果有的话则返回TYPE_PAUSED 或TYPE_CANCELED 来中断下载，
                    // 如果没有的话则实时计算当前的下载进度，然后调用publishProgress() 方法进行通知
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if(isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }
    //D003-2 在界面上更新当前的下载进度
    // 这个方法就简单得多了，它首先从参数中获取到当前的下载进度，然后和上一次的下载进度进行对比，
    // 如果有变化的话则调用DownloadListener的onProgress() 方法来通知下载进度更新。
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    //D003-3 通知最终的下载结果
    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }
    //暂停和取消操作都是使用一个布尔型的变量来进行控制的，调用pauseDownload() 或cancelDownload() 方法即可更改变量的值。
    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }


}
