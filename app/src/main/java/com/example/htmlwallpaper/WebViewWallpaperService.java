package com.example.htmlwallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewWallpaperService extends WallpaperService {

    private static final int FRAME_DELAY_MS = 66;

    @Override
    public Engine onCreateEngine() {
        return new WebEngine();
    }
