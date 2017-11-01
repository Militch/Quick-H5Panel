package me.militch.quick.h5panel.util;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请
 * Created by April丶 on 2017/3/15.
 */

public class PermissionSupport extends Fragment implements PermissionsContract {

    private int PERMISSION_REQUEST_CODE = 0X01;
    private PermissionListener listener;

    @Override
    public void doRequestPermissions(@NonNull String[] permissions,
                                     int requestCode,
                                     @Nullable PermissionListener listener) {
        this.PERMISSION_REQUEST_CODE = requestCode;
        this.listener = listener;

        if (myCheckPermissions(permissions)) {
            if (listener != null) {
                listener.onPermissionsGranted(requestCode);
            }
        } else {
            List<String> needPermission = getDeniedPermissions(permissions);
            if (needPermission.size() < 1) {
                if (listener != null) {
                    listener.onPermissionsDenied(requestCode);
                }
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        needPermission.toArray(new String[needPermission.size()]),
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * 系统请求权限回调
     *
     * @param requestCode  请求码
     * @param permissions  权限列表
     * @param grantResults 请求回复
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (listener != null && PERMISSION_REQUEST_CODE == requestCode) {
            if (isVerifyPermissions(grantResults)) {
                //确认所有申请的权限都被允许了，
                listener.onPermissionsGranted(requestCode);
            } else {
                //存在一个权限被拒绝了，
                listener.onPermissionsDenied(requestCode);
            }
        }
    }

    //检测该权限列表所有的权限是否已被授权
    private boolean myCheckPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            //android版本小于6.0
            return true;
        }
        //android版本大于等于6.0
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(getActivity(), permission)) {
                return false;
            }
        }
        return true;
    }

    //获取权限集中需要申请的权限列表
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(getActivity(), permission)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }

    //确认所有权限已被授权
    private boolean isVerifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false;
            }
        }
        return true;
    }

}
