package me.militch.quick.h5panel.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于请求权限的 fragment
 * Created by 叶开 on 2017/7/13.
 */

public class Permission extends Fragment {

    public Permission() {
    }

    private static final String TAG = "PermissionFragment";

    /**
     * 初始化方法
     *
     * @param activity 宿主activity（注：不推荐自行构造然后让它可以传入fragment，因为内存）
     */
    public static Permission init(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        Permission fragment = (Permission) manager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new Permission();
            manager.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            manager.executePendingTransactions();
        }
        return fragment;
    }

    private Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private int REQUEST_CODE = 0X00;//请求码
    private String[] requestPermissions;//申请的权限

    private Listener.ResultListener resultListener; //结果监听接口
    private Listener.InstructionListener instructionListener;//说明监听接口

    /**
     * 设置权限
     *
     * @param permissions 想要检查的权限
     */
    public Permission want(@NonNull String... permissions) {
        requestPermissions = permissions;
        return this;
    }

    /**
     * 注：此方法应先于
     * {@link #check(int, Listener.ResultListener)}
     * 方法调用
     *
     * @param instructionListener 说明监听接口
     */
    public Permission explain(Listener.InstructionListener instructionListener) {
        this.instructionListener = instructionListener;
        return this;
    }

    /**
     * 检查
     * <p>
     * 注：此方法为申请权限推荐调用方法
     *
     * @param requestCode 申请码
     * @param listener    监听接口
     */
    public Permission check(int requestCode,
                            Listener.ResultListener listener) {
        //获取需要申请的权限
        List<String> shouldRequestList = getDeniedPermissions(requestPermissions);
        int size = shouldRequestList.size();
        if (size == 0 && listener != null) {
            //没有需要申请的权限了
            listener.onPermissionsGranted(requestCode);
            requestPermissions = null;
            return this;
        }

        //有需要申请的权限
        this.resultListener = listener;
        this.requestPermissions = shouldRequestList.toArray(new String[size]);

        if (!shouldShowRationaleUI(requestCode, shouldRequestList)) {
            //没有选择给用户展示提示信息
            //此时直接开始申请权限
            request(requestCode);
        }
        return this;
    }

    /**
     * 正式开始申请
     * <p>
     * 注：此方法不推荐直接调用。
     */
    public void request(int requestCode) {
        this.REQUEST_CODE = requestCode;
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(requestPermissions, REQUEST_CODE);
        } else {
            resultListener.onPermissionsGranted(REQUEST_CODE);
        }
    }

    /**
     * 权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (resultListener != null && REQUEST_CODE == requestCode) {
            if (isVerifyPermissions(grantResults)) {
                //确认所有申请的权限都被允许了，
                resultListener.onPermissionsGranted(requestCode);
            } else {
                //存在一个权限被拒绝了，
                resultListener.onPermissionsDenied(requestCode);
            }
        }
        REQUEST_CODE = 0X00;
        requestPermissions = null;
    }

    //======================================== 工具方法 ===========================================

    /**
     * 判断是否有需要显示说明的权限
     *
     * @param permissions 需要检查的权限
     * @return 是否给用户展示了说明
     */
    private boolean shouldShowRationaleUI(int requestCode,
                                          List<String> permissions) {
        if (instructionListener == null) {
            return false;
        }
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                list.add(permission);
            }
        }
        int size = list.size();
        //如果有需要显示的说明的权限……
        if (size > 0) {
            instructionListener.showPermissionInstruction(
                    requestCode,
                    list.toArray(new String[size])
            );
            return true;
        }
        return false;
    }

    /**
     * @param permissions 想要申请的权限
     * @return 被拒绝了的权限
     */
    private List<String> getDeniedPermissions(String... permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_DENIED ==
                    ContextCompat.checkSelfPermission(activity, permission)) {
                list.add(permission);
            }
        }
        return list;
    }

    /**
     * @param grantResults 系统回调结果集
     * @return 确认所有申请的权限都被准许了
     */
    private boolean isVerifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (PackageManager.PERMISSION_DENIED == result) {
                return false;
            }
        }
        //再次检测以确认
        if (requestPermissions != null && requestPermissions.length > 0) {
            for (String permission : requestPermissions) {
                if (PackageManager.PERMISSION_DENIED ==
                        ContextCompat.checkSelfPermission(activity, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 权限申请接口
     * Created by April on 2017/6/15.
     */
    public static interface Listener {

        /**
         * 结果接口
         * 警告：此接口回调之后，请求码、想要申请的权限 会被重置
         */
        interface ResultListener {
            /**
             * 申请的权限全都被准许了
             *
             * @param requestCode 申请码
             */
            void onPermissionsGranted(int requestCode);

            /**
             * 申请的权限有一个被拒绝了
             *
             * @param requestCode 申请码
             */
            void onPermissionsDenied(int requestCode);
        }

        /**
         * 说明接口
         */
        interface InstructionListener {

            /**
             * 给用户展示为什么需要申请这些权限。
             * 比如：弹出弹窗告诉用户，等等。
             * 如果用户明确了说明，可以选择调用
             * {@link #request(int)}
             *
             * @param requestCode 申请码
             * @param permissions 应该显示申请权限的说明信息的权限
             */
            void showPermissionInstruction(int requestCode, String[] permissions);
        }

        ///////////////////////////////////  以下为危险权限组  //////////////////////////////////////
        /**
         * 警告：申请权限不推荐直接使用权限组，组只是作为参考，为了应对变化，每次申请权限最好是只申请具体的某一条。
         */

        //通讯
        String[] CONTACTS = new String[]{
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS
        };

        //手机
        String[] PHONE = new String[]{
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.USE_SIP,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.ADD_VOICEMAIL
        };

        //日历
        String[] CALENDAR = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        };

        //相机
        String[] CAMERA = new String[]{
                Manifest.permission.CAMERA
        };

        //传感器
        @SuppressLint("InlinedApi")
        String[] SENSORS = new String[]{
                Manifest.permission.BODY_SENSORS
        };

        //定位
        String[] LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        //存储
        String[] STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        //麦克风，扩音器，话筒
        String[] MICROPHONE = new String[]{
                Manifest.permission.RECORD_AUDIO
        };

        //短信
        String[] SMS = new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECEIVE_MMS
        };

    }
}
