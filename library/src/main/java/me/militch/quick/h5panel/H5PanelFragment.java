package me.militch.quick.h5panel;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.joker.api.Permissions4M;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import me.militch.quick.h5panel.conf.H5PanelSetup;
import me.militch.quick.h5panel.event.H5PanelEventListener;
import me.militch.quick.h5panel.util.Permission;
import me.militch.quick.h5panel.util.PermissionsContract;

public class H5PanelFragment extends Fragment implements
        DialogInterface.OnClickListener,DialogInterface.OnCancelListener,
        PermissionsContract.PermissionListener,
        Permission.Listener.ResultListener{
    private WebView mWebView;
    private boolean isAvailable;
    private boolean isLock;
    private ReceivedTitleListener receivedTitleListener;
    private LoadingListener loadingListener;
    private AlertDialog choiceDialog;
    private static final String[] CHOICE_ITEMS = {"相机","相册"};
    private ValueCallback<Uri> valueCallback;
    private ValueCallback<Uri[]> valueCallback2;
    public static H5PanelFragment newInstance() {
        Bundle args = new Bundle();
        H5PanelFragment fragment = new H5PanelFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void initChoiceDialog(){
        choiceDialog =  new AlertDialog.Builder(getContext()).
                setTitle("请选择打开方式：")
                .setItems(CHOICE_ITEMS,this)
                .setOnCancelListener(this).create();
    }


    /**
     * 设置标题栏监听器
     * @param receivedTitleListener 题栏监听器
     */
    public void setReceivedTitleListener(ReceivedTitleListener receivedTitleListener) {
        this.receivedTitleListener = receivedTitleListener;
    }

    /**
     * 设置加载进度拦截器
     * @param loadingListener 加载进度拦截器
     */
    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getActivity());
        isAvailable = true;
        return mWebView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化配置 WebView
        H5PanelSetup.builder(getActivity()).setH5PanelEventListener(
                new H5PanelEventListener(getContext()) {
                    @Override
                    public void onReceivedTitle(String title) {
                        super.onReceivedTitle(title);
                        if (receivedTitleListener != null) {
                            receivedTitleListener.onReceivedTitle(title);
                        }
                    }

                    @Override
                    public void onLoading(int v) {
                        super.onLoading(v);
                        if (loadingListener != null) {
                            loadingListener.onLoading(v);
                        }
                    }
                    @Override
                    public void onOpenFile(boolean is, ValueCallback<Uri[]> valueCallback,
                                           ValueCallback<Uri> callback2) {
                        super.onOpenFile(is, valueCallback, callback2);
                        if (is) {
                            valueCallback2 = valueCallback;
                        } else {
                            H5PanelFragment.this.valueCallback = callback2;
                        }
                        choiceDialog.show();
                    }
                }
        ).build().setup(mWebView);
        initChoiceDialog();
    }


    /**
     * 加载 URL
     * @param url url地址
     */
    public void loadUrl(String url) {
        if (isAvailable && !isLock) {
            mWebView.loadUrl(url);
            isLock = true;
        } else if (!isAvailable) {
            Log.e(getClass().getSimpleName(), "WebView初始化未完成");
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    /**
     * 获取WebView
     * @return WebView
     */
    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 是否能后退
     * @return 是否
     */
    public boolean canGoBack() {
        return isAvailable && mWebView.canGoBack();
    }

    /**
     * 后退
     */
    public void goBack() {
        if (isAvailable) {
            mWebView.goBack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri == null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
                }
                if (valueCallback2 != null && uri != null) {
                    Uri[] uris = new Uri[]{uri};
                    valueCallback2.onReceiveValue(uris);
                    valueCallback2 = null;
                } else if (valueCallback != null && uri != null) {
                    valueCallback.onReceiveValue(uri);
                    valueCallback = null;
                }
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case 0:
                //TODO 感觉权限处理这块需要修改..有空再说吧...
                Permission.init(getActivity())
                        .want(Manifest.permission.CAMERA, //相机权限
                                Manifest.permission.WRITE_EXTERNAL_STORAGE //写入存储卡权限
                        ).check(1,this);
                break;
            case 1:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permissions4M.onRequestPermissionsResult(this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (valueCallback != null) {
            valueCallback = null;
        }
        if (valueCallback2 != null) {
            valueCallback2 = null;
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode) {
        if (choiceDialog.isShowing()) {
            choiceDialog.dismiss();
        }
        switch (requestCode) {
            case 1:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android的相机
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        if (choiceDialog.isShowing()) {
            choiceDialog.dismiss();
        }
        //从相机或相册取出照片后如果重新选择需要制空变量
        if (valueCallback2 != null) {
            valueCallback2 = null;
        }
        if (valueCallback != null) {
            valueCallback = null;
        }
        switch (requestCode) {
            case 1:
                Toast.makeText(getContext(), "您拒绝了打开相机权限", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface ReceivedTitleListener {
        void onReceivedTitle(String title);
    }

    public interface LoadingListener {
        void onLoading(int v);
    }
}
