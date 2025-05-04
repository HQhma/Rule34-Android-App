package com.HQHMA.rule34.Utilities;

import static com.HQHMA.rule34.Utilities.Utilities.showDownloadProgressDialog;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;


public class FileManager {

    public static void downloadFile(String url, String fileName, Activity activity){
        Context context = activity.getApplicationContext();
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"rule34/" + fileName);
        long reference = manager.enqueue(request);

        showDownloadProgressDialog(reference,fileName, manager, activity);
    }


    public static boolean deleteFile(String fileName,Context context){
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),fileName);
        return file.delete();
    }

    public static File readFile(String fileName,Context context){
        File file= new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),fileName);
        return file;
    }
}
