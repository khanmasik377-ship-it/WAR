

publipackage com.example.htmlwallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.webkit.WebView;

public class WebViewWallpaperService extends 
    package com.example.htmlwallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.webkit.WebView;

public class WebViewWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebEngine();
    }

    class WebEngine extends Engine {
        private WebView webView;
        private Handler handler = new Handler(Looper.getMainLooper());
        private boolean visible = true;
        private Runnable drawRunner;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            handler.post(() -> {
                webView = new WebView(getApplicationContext());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.loadUrl("file:///android_asset/wallpaper.html");
                webView.layout(0, 0, 1080, 1920);
            });

            drawRunner = this::drawFrame;
        }

        void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && webView != null) {
                    webView.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 1000 / 30);
            }
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            if (isVisible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.post(() -> {
                if (webView != null) webView.destroy();
            });
        }
    }
}
