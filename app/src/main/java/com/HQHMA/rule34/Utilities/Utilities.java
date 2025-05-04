package com.HQHMA.rule34.Utilities;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.HQHMA.rule34.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utilities {

    public static int postLimit = 100;

    private static DisplayMetrics getDisplayMetrics(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getDisplayHeight(Activity activity){
        return getDisplayMetrics(activity).heightPixels;
    }
    public static int getDisplayHeight(Context context){
        // get window managers
        WindowManager manager =  (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        // get width and height
        //int width = point.x;
        int height = point.y;

        return height;
    }
    public static int getDisplayWidth(Activity activity){
        return getDisplayMetrics(activity).widthPixels;
    }

    public static int getLocationOfView(View view,byte zeroForX_oneForY){
        int[] locationValues = new int[2];
        view.getLocationInWindow(locationValues);
        return locationValues[zeroForX_oneForY];
    }
    public static int getViewHeight(View view){
        return view.getHeight();
    }

    public static Fragment openFragment(FragmentManager fragmentManager, int fragmentContainerView_id, Fragment newfragment, Fragment lastFragment){
        Log.i("Utilities", "openFragment: open fragment: " + newfragment + "\nlast fragment was: " + lastFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (lastFragment != null)
            fragmentTransaction.hide(lastFragment);

        if (fragmentManager.getFragments().contains(newfragment))
            fragmentTransaction.show(newfragment);
        else
            fragmentTransaction.add(fragmentContainerView_id,newfragment);

        lastFragment = newfragment;
        fragmentTransaction.commit();

        return lastFragment;
    }

    public static void hideKeyboard(View view,Activity activity) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showAppUpdateDialog(String text,boolean forceUpdate, Activity activity,ShowAppUpdateDialogCallback showAppUpdateDialogCallback){
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.app_update_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        builder.setCancelable(false);

        Button updateBtn = dialogView.findViewById(R.id.updateBtn);
        Button notNowBtn = dialogView.findViewById(R.id.notNowBtn);
        TextView textView = dialogView.findViewById(R.id.textView);

        textView.setText(text);
        if(forceUpdate)
            notNowBtn.setVisibility(View.INVISIBLE);

        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAppUpdateDialogCallback.onUpdate();
                progressDialog.dismiss();
            }
        });

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAppUpdateDialogCallback.onSkip();
                progressDialog.dismiss();
            }
        });
    }
    public interface ShowAppUpdateDialogCallback {
        void onUpdate();
        void onSkip();
    }

    public static void showDownloadProgressDialog(long downloadId,String filename, DownloadManager manager, Activity activity) {
        // Create a dialog to show the download progress
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.downloading_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        builder.setCancelable(false);

        Button cancelBTN = dialogView.findViewById(R.id.notNowBtn);
        Button continueBTN = dialogView.findViewById(R.id.updateBtn);
        TextView filenameTV = dialogView.findViewById(R.id.filenameTV);
        TextView downloadingTV = dialogView.findViewById(R.id.textView);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);

        progressBar.setMax(100);

        filenameTV.setText(filename);

        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.remove(downloadId);
                progressDialog.dismiss();
            }
        });
        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.dismiss();
            }
        });

        // Use a Handler to periodically check the download progress
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);

                Cursor cursor = manager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (bytesTotal > 0) {
                        int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                        progressBar.setProgress(progress);
                        downloadingTV.setText("Downloading "+ progress + "%");

                        // If download is complete, dismiss the dialog
                        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {

                            String status_str;
                            if (status == DownloadManager.STATUS_SUCCESSFUL) status_str = "Download Successful.";
                            else status_str = "Download Failed.";

                            Toast.makeText(activity,status_str , Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            cursor.close();
                            return;
                        }
                    }
                    cursor.close();
                }

                // Repeat the check after a delay
                handler.postDelayed(this, 500); // Check every 500ms
            }
        };

        // Start checking the download progress
        handler.post(runnable);
    }

    public static void getImageSizeOnline(String path, ImageSizeCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    long contentLength = connection.getContentLengthLong();

                    if (contentLength >= 0) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onSuccess(contentLength)
                        );
                    } else {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError("Content-Length not available")
                        );
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError("Failed to retrieve image headers. Response code: " + responseCode)
                    );
                }

                connection.disconnect();
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage())
                );
            }
        }).start();
    }

    public interface ImageSizeCallback {
        void onSuccess(long sizeInBytes);
        void onError(String errorMessage);
    }

}
