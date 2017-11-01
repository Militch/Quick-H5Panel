package me.militch.quick.h5panel.conf;

import android.content.Context;
import android.os.Build;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import me.militch.quick.h5panel.conf.impl.WebChromeClientImpl;
import me.militch.quick.h5panel.conf.impl.WebViewClientImpl;
import me.militch.quick.h5panel.conf.impl.WebViewDownloadImpl;
import me.militch.quick.h5panel.event.H5PanelEventListener;

public class H5PanelSetup {
    private Builder builder;
    private H5PanelSetup(Builder builder){
        this.builder = builder;
    }
    public static Builder builder(Context context){
        return new Builder(context);
    }
    public void setup(WebView webView){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(builder.javaScriptEnabled);
        webSettings.setGeolocationEnabled(builder.geolocationEnabled);
        webSettings.setDomStorageEnabled(builder.domStorageEnabled);
        webSettings.setAllowFileAccess(builder.allowFileAccess);
        webSettings.setSupportZoom(builder.supportZoom);
        webSettings.setUseWideViewPort(builder.useWideViewPort);
        webSettings.setSupportMultipleWindows(builder.supportMultipleWindows);
        webSettings.setAppCacheEnabled(builder.appCacheEnabled);
        webSettings.setAppCacheMaxSize(builder.appCacheMaxSize);
        webSettings.setAppCachePath(builder.appCachePath);
        webSettings.setBlockNetworkImage(builder.blockNetworkImage);
        webSettings.setDatabaseEnabled(builder.databaseEnabled);
        webSettings.setDatabasePath(builder.databasePath);
        webSettings.setGeolocationDatabasePath(builder.geolocationDatabasePath);
        webSettings.setCacheMode(builder.cacheMode);
        webSettings.setNeedInitialFocus(builder.needInitialFocus);
        webSettings.setLoadsImagesAutomatically(builder.loadsImagesAutomatically);
        webSettings.setDisplayZoomControls(builder.displayZoomControls);
        webSettings.setLayoutAlgorithm(builder.layoutAlgorithm);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClientImpl(builder.h5PanelEventListener));
        webView.setWebChromeClient(new WebChromeClientImpl(builder.h5PanelEventListener));
        webView.setDownloadListener(new WebViewDownloadImpl(builder.h5PanelEventListener));
    }
    public static class Builder{
        private H5PanelEventListener h5PanelEventListener;
        private boolean javaScriptEnabled = true;
        private boolean geolocationEnabled = true;
        private boolean domStorageEnabled = true;
        private boolean allowFileAccess = true;
        private boolean supportZoom = true;
        private boolean appCacheEnabled = true;
        private long appCacheMaxSize;
        private boolean useWideViewPort = true;
        private boolean supportMultipleWindows = false;
        private boolean blockNetworkImage = false;
        private boolean databaseEnabled = true;
        private String appCachePath;
        private String databasePath;
        private String geolocationDatabasePath;
        private int cacheMode;
        private boolean needInitialFocus = true;
        private boolean loadsImagesAutomatically = true;
        private boolean displayZoomControls = false;
        private WebSettings.LayoutAlgorithm layoutAlgorithm;
        private Builder(Context context){
            appCachePath = context.getDir("appCache", 0).getPath();
            databasePath = context.getDir("databases", 0).getPath();
            geolocationDatabasePath = context.getDir("geolocation", 0).getPath();
            appCacheMaxSize  = Long.MAX_VALUE;
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;

        }

        /**
         * 设置事件监听器
         * @param h5PanelEventListener 事件监听器
         * @return 构造器
         */
        public Builder setH5PanelEventListener(H5PanelEventListener h5PanelEventListener) {
            this.h5PanelEventListener = h5PanelEventListener;
            return this;
        }

        /**
         * 设置 Application 缓存路径
         * @param appCachePath 缓存路径；默认为：appCache
         * @return 构造器
         */
        public Builder setAppCachePath(String appCachePath) {
            this.appCachePath = appCachePath;
            return this;
        }

        /**
         * 设置位置信息数据库存储路径
         * @param geolocationDatabasePath 存储路径；默认为：geolocation
         * @return 构造器
         */
        public Builder setGeolocationDatabasePath(String geolocationDatabasePath) {
            this.geolocationDatabasePath = geolocationDatabasePath;
            return this;
        }

        /**
         * 否允许访问文件
         * @param allowFileAccess 是否允许；默认允许
         * @return 构造器
         */
        public Builder setAllowFileAccess(boolean allowFileAccess) {
            this.allowFileAccess = allowFileAccess;
            return this;
        }

        /**
         * 设置是否支持缩放
         * @param supportZoom 是否支持；默认支持
         * @return 构造器
         */
        public Builder setSupportZoom(boolean supportZoom) {
            this.supportZoom = supportZoom;
            return this;
        }

        /**
         * 设置是否启用 Application Cache
         * @param appCacheEnabled 是否启用；默认启用
         * @return 构造器
         */
        public Builder setAppCacheEnabled(boolean appCacheEnabled) {
            this.appCacheEnabled = appCacheEnabled;
            return this;
        }

        /**
         * 设置是否支持多窗口
         * @param supportMultipleWindows 是否支持；默认不支持
         * @return 构造器
         */
        public Builder setSupportMultipleWindows(boolean supportMultipleWindows) {
            this.supportMultipleWindows = supportMultipleWindows;
            return this;
        }

        /**
         * 是否支持 ViewPort
         * @param useWideViewPort 是否支持；默认支持
         * @return 构造器
         */
        public Builder setUseWideViewPort(boolean useWideViewPort) {
            this.useWideViewPort = useWideViewPort;
            return this;
        }

        /**
         * 设置 Application 缓存最大大小
         * @param appCacheMaxSize 缓存大小；默认为 9223372036854775807L
         * @return 构造器
         */
        public Builder setAppCacheMaxSize(long appCacheMaxSize) {
            this.appCacheMaxSize = appCacheMaxSize;
            return this;
        }

        /**
         * 是否执行JavaScript脚本
         * @param javaScriptEnabled 是否执行；默认执行
         * @return 构造器
         */
        public Builder setJavaScriptEnabled(boolean javaScriptEnabled) {
            this.javaScriptEnabled = javaScriptEnabled;
            return this;
        }

        /**
         * 是否打开地理定位
         * @param geolocationEnabled 是否打开；默认打开
         * @return 构造器
         */
        public Builder setGeolocationEnabled(boolean geolocationEnabled) {
            this.geolocationEnabled = geolocationEnabled;
            return this;
        }

        /**
         * 是否开启本地DOM存储
         * @param domStorageEnabled 是否开启；默认开启
         * @return 构造器
         */
        public Builder setDomStorageEnabled(boolean domStorageEnabled) {
            this.domStorageEnabled = domStorageEnabled;
            return this;
        }

        /**
         * 是否加载网络图像
         * @param blockNetworkImage 是否加载；默认加载
         * @return 构造器
         */
        public Builder setBlockNetworkImage(boolean blockNetworkImage) {
            this.blockNetworkImage = blockNetworkImage;
            return this;
        }

        /**
         * 是否启用数据库存储
         * @param databaseEnabled 是否启用；默认启用
         * @return 构造器
         */
        public Builder setDatabaseEnabled(boolean databaseEnabled) {
            this.databaseEnabled = databaseEnabled;
            return this;
        }

        /**
         * 设置存储数据库路径
         * @param databasePath 数据库路径
         * @return 构造器
         */
        public Builder setDatabasePath(String databasePath) {
            this.databasePath = databasePath;
            return this;
        }

        /**
         * 设置缓存模式
         * @param cacheMode 缓存模式
         * @return 构造器
         */
        public Builder setCacheMode(int cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        /**
         * 当 WebView 调用 requestFocus 时为 WebView 设置节点
         * @param needInitialFocus 是否设置；默认为True
         * @return 构造器
         */
        public Builder setNeedInitialFocus(boolean needInitialFocus){
            this.needInitialFocus = needInitialFocus;
            return this;
        }

        /**
         * 是否支持自动加载图片
         * @param loadsImagesAutomatically 是否支持；默认支持
         * @return 构造器
         */
        public Builder setLoadsImagesAutomatically(boolean loadsImagesAutomatically) {
            this.loadsImagesAutomatically = loadsImagesAutomatically;
            return this;
        }

        /**
         *是否显示缩放控制按钮
         * @param displayZoomControls 是否显示；默认不显示
         * @return 构造器
         */
        public Builder setDisplayZoomControls(boolean displayZoomControls) {
            this.displayZoomControls = displayZoomControls;
            return this;
        }

        /**
         * 设置布局方式
         * @param layoutAlgorithm 布局方式；默认为：SINGLE_COLUMN
         * @return 构造器
         */
        public Builder setLayoutAlgorithm(WebSettings.LayoutAlgorithm layoutAlgorithm) {
            this.layoutAlgorithm = layoutAlgorithm;
            return this;
        }
        public H5PanelSetup build(){
            return new H5PanelSetup(this);
        }
    }
}
