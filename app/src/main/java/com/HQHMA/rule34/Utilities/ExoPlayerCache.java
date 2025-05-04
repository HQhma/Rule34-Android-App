package com.HQHMA.rule34.Utilities;

import android.content.Context;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DataSource;

import java.io.File;

@UnstableApi
public class ExoPlayerCache {

    private static SimpleCache simpleCache;

    public static SimpleCache getInstance(Context context) {
        if (simpleCache == null) {
            File cacheDir = new File(context.getCacheDir(), "media_cache");
            long cacheSize = 100 * 1024 * 1024; // 100 MB
            simpleCache = new SimpleCache(cacheDir, new LeastRecentlyUsedCacheEvictor(cacheSize));
        }
        return simpleCache;
    }

    public static DataSource.Factory getDataSourceFactory(Context context) {
        return new CacheDataSource.Factory()
                .setCache(getInstance(context))
                .setUpstreamDataSourceFactory(new DefaultDataSource.Factory(context));
    }
}
