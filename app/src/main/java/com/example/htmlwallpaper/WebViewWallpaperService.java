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
    }class WebEngine extends Engine {
        private WebView webView;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Runnable drawRunnable = this::drawFrame;
        private boolean visible = true;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            handler.post(() -> {
                webView = new WebView(getApplicationContext());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("file:///android_asset/english-wallpaper.html");
            });
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(drawRunnable);
            if (visible) {
                handler.post(drawRunnable);
            }
        }
    @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (webView != null) {
                webView.measure(
                        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                );
                webView.layout(0, 0, width, height);
            }
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && webView != null) {
                    webView.draw(canvas);
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            if (visible) {
                handler.postDelayed(drawRunnable, FRAME_DELAY_MS);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawRunnable);
        }
    }
}
