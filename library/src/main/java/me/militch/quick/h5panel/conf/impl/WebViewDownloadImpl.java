package me.militch.quick.h5panel.conf.impl;

import com.tencent.smtt.sdk.DownloadListener;

import me.militch.quick.h5panel.event.H5PanelEventListener;

public class WebViewDownloadImpl implements DownloadListener {
    private H5PanelEventListener listener;
    public WebViewDownloadImpl(H5PanelEventListener listener) {
        this.listener = listener;
    }
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if(listener != null){
            listener.onDownload(url);
        }
    }
}
