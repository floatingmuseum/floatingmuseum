package com.floatingmuseum.androidtest.functions.download;

import android.os.Environment;
import android.widget.CheckBox;

import com.floatingmuseum.androidtest.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadThread extends Thread {

    private boolean stopThread = false;

    //下载文件夹路径
    private String dirPath;
    private String fileName;
    private ThreadInfo threadInfo;
    private DBUtil dbUtil;
    private ThreadCallback callback;

    public DownloadThread(ThreadInfo threadInfo, String dirPath, String fileName, DBUtil dbUtil, ThreadCallback callback) {
        this.threadInfo = threadInfo;
        this.dirPath = dirPath;
        this.fileName = fileName;
        this.callback = callback;
        this.dbUtil = dbUtil;
    }

    @Override
    public void run() {
        Logger.d("DownloadService...run:" + threadInfo.getId() + "号线程开始工作");
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        try {
            //获取文件长度
            URL url = new URL(threadInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            long startPosition = threadInfo.getCurrentPosition();
            long currentPosition = threadInfo.getCurrentPosition();

            //起始位置是线程所下载区块的初始位置+已完成大小
            connection.setRequestProperty("Range", "bytes=" + threadInfo.getCurrentPosition() + "-" + threadInfo.getEndPosition());

            long contentLength = -1;
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 206) {
                contentLength = connection.getContentLength();
                Logger.d("获取文件长度...区块长度:" + contentLength);
                if (contentLength <= 0) {
                    return;
                }
                File file = new File(dirPath, fileName);
                randomAccessFile = new RandomAccessFile(file, "rwd");
                //设置写入位置
                //seek方法 在读写的时候跳过设置的字节数,从下一个字节开始读写.例如seek(100),从101字节开始读写
                //在这里也就是从已完成部分的末尾继续写
                randomAccessFile.seek(currentPosition);
                //开始写入
                inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    currentPosition += len;
                    threadInfo.setCurrentPosition(currentPosition);
                    callback.onProgress(threadInfo);
                    if (stopThread) {
                        //更新数据库,停止循环
                        dbUtil.update(threadInfo);
                        Logger.d("DownloadService...run:" + threadInfo.getId() + "号线程暂停工作");
                        return;
                    }
                    if (currentPosition > threadInfo.getEndPosition()) {
                        break;
                    }
                }
                //当前区块下载完成
                Logger.d("DownloadService...run:" + threadInfo.getId() + "号线程完成工作");
                callback.onFinished(threadInfo.getId());
            }
        } catch (Exception e) {
            Logger.d("DownloadService...run:" + threadInfo.getId() + "号线程出现异常");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        stopThread = true;
    }
}
