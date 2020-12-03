package com.zkml.meetingtablecard.utils.update;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.PackageUtil;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.utils.httpserver.download.DownloadInfo;
import com.zkml.meetingtablecard.utils.httpserver.download.DownloadManager;
import com.zkml.meetingtablecard.utils.httpserver.download.DownloadService;
import com.zkml.meetingtablecard.utils.httpserver.listener.DownloadListener;

import java.io.File;
import java.lang.ref.SoftReference;

//import pub.devrel.easypermissions.EasyPermissions;

import static com.zkml.meetingtablecard.utils.httpserver.download.DownloadManager.DM_TARGET_FOLDER;

/**
 * @author: zzh
 * data : 2020/8/10
 * description：
 */
public class VersionUpdateManager implements View.OnClickListener {
    /**
     * 上下文
     */
    private Activity mContext;
    /**
     * 是否是第一次点击更新
     */
    private boolean mIsFirst = false;
    /**
     * 是否再次点击了检查更新
     */
    private boolean mIsClickUpdated = false;
    /**
     * 版本名称
     */
    private String mUrl;
    /**
     * 版本名称
     */
    private String mVersionName;
    /**
     * 更新提示与内容
     */
    private String mUpdateTip;
    private String mUpdateContent;
    /**
     * 版本号
     */
    private int mVersioncode;
    /**
     * 下载文件大小
     */
    private TextView mTvDownloadSize;
    /**
     * 下载进度
     */
    private TextView mTvProgress;
    /**
     * 网速显示
     */
    private TextView mTvNetSpeed;
    /**
     * 进度条
     */
    private ProgressBar mProgressBar;
    private LinearLayout mLlShowDownload;

    private DownloadManager mDownloadManager;
    /**
     * 下载进度监听器
     */
    private DownloadListener mDownloadListener;
    /**
     * 文件路径
     */
    private String mTargetPath;
    /**
     * 检查更新Handler
     */
    private UpdateHandler mUpdateHandler = new UpdateHandler(this);
    /**
     * 释放资源,如果是增量更新，退出更新界面后，下载完成，不进行差分包的合成
     */
    private boolean isRelease = false;

    /**
     * 更新弹框
     */
    private Dialog mUpdateDialog;
    private View mUpdateDialogView;
    private View mViewLine;
    private Button mBtLater;
    private Button mBtNow;
    private TextView mTvUpdateDesc;

    /**
     * 权限弹框
     */
    private Dialog mAlertDialog;
    private View mAlertDialogView;
    private Button mBtCancel;
    private Button mBtOpen;

    /**
     * 构造函数
     *
     * @param context Activity
     */
    public VersionUpdateManager(Activity context) {
        mContext = context;
    }

    /**
     * 获取安装文件路径
     *
     * @return
     */
    public String getTargetPath() {
        if (StringUtils.isStrEmpty(mTargetPath)) {
            //初始化目标Download保存目录
            String folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
            if (!new File(folder).exists()) {
                new File(folder).mkdirs();
            }
            File file = null;
//            if (null != mUpdataInfo && !StringUtils.isStrEmpty(mUpdataInfo.getUrl())) {
//                file = new File(folder, DownloadTask.getUrlFileName(mUpdataInfo.getUrl()));
//            }
            if (null != file && file.exists()) {
                mTargetPath = file.getAbsolutePath();
            } else {
                mTargetPath = "";
            }
        }
        return mTargetPath;
    }

    /**
     * 初始化数据
     */
    public void initData() {
        mVersioncode = PackageUtil.getAppVersionCode(mContext);
        mDownloadManager = DownloadService.getDownloadManager(mContext);
        mDownloadListener = new MyListener();
        LogUtil.i("zzz1", "mDownloadListener == " + mDownloadListener);
    }

    /**
     * 释放监听
     */
    public void release() {
        mDownloadListener = null;
        if (null != mDownloadManager) {
            mDownloadManager.getHandler().removeCallbacksAndMessages(null);
        }
//        if(null != mUpdataInfo && mUpdataInfo.isUpdatePatch()) {
//            mDownloadManager.stopAllTask();
//        }
        isRelease = true;
    }

    //版本更新
    private class MyListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            refreshUi(downloadInfo);
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            LogUtil.i("zzz1", "mDownloadListener == " + mDownloadListener);
            if (mUpdateDialog != null) {
                mUpdateDialog.dismiss();
            }
            mIsClickUpdated = false;

            ActivityUtils.toast(mContext, mContext.getString(R.string.download_success));
//            mTargetPath = downloadInfo.getTargetPath();
            ActivityUtils.installApk(new File(downloadInfo.getTargetPath()), mContext);
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            mIsClickUpdated = false;
            if (errorMsg != null) {
                ActivityUtils.toast(mContext, errorMsg);
            }
        }
    }

    /**
     * 刷新界面
     *
     * @param downloadInfo
     */
    private void refreshUi(DownloadInfo downloadInfo) {
        String downloadLength = Formatter.formatFileSize(mContext, downloadInfo.getDownloadLength());
        String totalLength = Formatter.formatFileSize(mContext, downloadInfo.getTotalLength());
        // 下载文件大小
        mTvDownloadSize.setText(downloadLength + "/" + totalLength);
        String networkSpeed = Formatter.formatFileSize(mContext, downloadInfo.getNetworkSpeed());
        // 下载速度
        mTvNetSpeed.setText(mContext.getString(R.string.download_speed) + networkSpeed + "/s");
        // 下载进度
        mTvProgress.setText(mContext.getString(R.string.download_progress)
                + (Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
        mProgressBar.setMax((int) downloadInfo.getTotalLength());
        mProgressBar.setProgress((int) downloadInfo.getDownloadLength());
    }

    /**
     * Handler
     */
    public static class UpdateHandler extends Handler {
        private final SoftReference<VersionUpdateManager> mManagerSoftReference;

        public UpdateHandler(VersionUpdateManager versionUpdateManager) {
            mManagerSoftReference = new SoftReference<>(versionUpdateManager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == mManagerSoftReference.get()) {
                return;
            }
            switch (msg.what) {
                case Constant.UPDATA_CLIENT:
                    if (mManagerSoftReference.get().mIsClickUpdated) {
                        ActivityUtils.toast(mManagerSoftReference.get().mContext,
                                mManagerSoftReference.get().mContext.getString(R.string.update_loading));
                    } else {
                        mManagerSoftReference.get().showUpdateDialog();
                    }
                    break;
                case Constant.GET_UNDATAINFO_ERROR:
                    ActivityUtils.toast((Activity) mManagerSoftReference.get().mContext,
                            mManagerSoftReference.get().mContext.getString(R.string.update_errorservice));
                    break;
                case Constant.DOWN_ERROR:
                    ActivityUtils.toast((Activity) mManagerSoftReference.get().mContext,
                            mManagerSoftReference.get().mContext.getString(R.string.dowload_failed));
                    break;
                case Constant.NEW_VERSION:
                    if (mManagerSoftReference.get().mIsFirst) {
                        ActivityUtils.toast((Activity) mManagerSoftReference.get().mContext,
                                mManagerSoftReference.get().mContext.getString(R.string.new_version_now));
                        mManagerSoftReference.get().mIsFirst = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 下载app
     */
    public void downLoadApp() {
        // 检查外置存储器写权限
        //首先判断版本是否小于23，小于23则直接通过权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            LogUtil.i("zzz1", "hasPermissions < 23 ");
            mDownloadManager.addSingleTask(mUrl, mDownloadListener);
        } else {
//            if (!EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // ask permission with ReasonOfPermission, RequestCord and PermissionName
//                EasyPermissions.requestPermissions(mContext, mContext.getString(R.string.need_storage), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
////                LogUtil.i("zzz1", "requestPermissions ");
//            } else {
//                LogUtil.i("zzz1", "hasPermissions ");
                mDownloadManager.addSingleTask(mUrl, mDownloadListener);
//            }
        }
    }

    /**
     * 版本更新
     */
    public void startVersionUpdate() {
        mIsFirst = true;
//        update();
    }

    /**
     * 版本更新
     */
    public void update(String url, String version, String updateTip, String updateContent) {
        mUrl = url;
        mVersionName = version;
        mUpdateTip = updateTip;
        mUpdateContent = updateContent;
        //有新版本
        Message msg = new Message();
        msg.what = Constant.UPDATA_CLIENT;
        mUpdateHandler.sendMessage(msg);
    }

    // 更新弹框
    private void showUpdateDialog() {
        if (mUpdateDialog == null) {
            setDialog();
        } else {
            mUpdateDialog.dismiss();
            mUpdateDialog = null;
            setDialog();
        }
    }

    private void setDialog() {
        mUpdateDialogView = View.inflate(mContext, R.layout.update_dialog, null);
        mUpdateDialog = ActivityUtils.showDialogByUpate(mContext, mUpdateDialogView, 0.6f, 0.5f);
        mUpdateDialog.setCancelable(true);
        mUpdateDialog.setCanceledOnTouchOutside(true);
        // 弹框标题
        TextView tvTitleVersion = (TextView) mUpdateDialogView.findViewById(R.id.tv_title_version);
        tvTitleVersion.setText(mContext.getString(R.string.new_version) + "(" + mVersionName + ")");
        // 提示
        TextView tvTip = (TextView) mUpdateDialogView.findViewById(R.id.tv_tip);
        tvTip.setText(mUpdateTip);
        // 取消按钮
        ImageView ivCancel = (ImageView) mUpdateDialogView.findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(this);
        // 更新内容
        mTvUpdateDesc = (TextView) mUpdateDialogView.findViewById(R.id.tv_update_desc);
        mTvUpdateDesc.setText(mUpdateContent);
        mViewLine = (View) mUpdateDialogView.findViewById(R.id.view_line);
        // 稍后更新按钮
        mBtLater = (Button) mUpdateDialogView.findViewById(R.id.bt_later);
        // 立即更新按钮
        mBtNow = (Button) mUpdateDialogView.findViewById(R.id.bt_now);
        mBtLater.setOnClickListener(this);
        mBtNow.setOnClickListener(this);

        // 下载文件大小
        mTvDownloadSize = (TextView) mUpdateDialogView.findViewById(R.id.tv_download_size);
        // 下载进度
        mTvProgress = (TextView) mUpdateDialogView.findViewById(R.id.tv_progress);
        // 下载速度
        mTvNetSpeed = (TextView) mUpdateDialogView.findViewById(R.id.tv_net_speed);
        // 进度条
        mProgressBar = (ProgressBar) mUpdateDialogView.findViewById(R.id.pb_progress);
        mLlShowDownload = (LinearLayout) mUpdateDialogView.findViewById(R.id.ll_show_download);
    }

    // 权限弹框
    public void showAlertDialog() {
        if (mUpdateDialog != null) { // 关闭更新弹框
            mUpdateDialog.dismiss();
        }
        if (mAlertDialog == null) {
            setAlertDialog();
        } else {
            mAlertDialog.dismiss();
            mAlertDialog = null;
            setAlertDialog();
        }
    }

    private void setAlertDialog() {
        mAlertDialogView = View.inflate(mContext, R.layout.alert_dialog, null);
        mAlertDialog = ActivityUtils.showDialogByUpate(mContext, mAlertDialogView, 0.6f, 0.5f);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(true);
        // 取消按钮
        ImageView ivCancel = (ImageView) mAlertDialogView.findViewById(R.id.iv_alert_cancel);
        ivCancel.setOnClickListener(this);
        // 取消按钮
        mBtCancel = (Button) mAlertDialogView.findViewById(R.id.bt_cancel);
        // 开启按钮
        mBtOpen = (Button) mAlertDialogView.findViewById(R.id.bt_open);
        mBtCancel.setOnClickListener(this);
        mBtOpen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bt_later) {
            if (mUpdateDialog != null) { // 关闭更新弹框
                mUpdateDialog.dismiss();
            }
        } else if (i == R.id.bt_now) { // 下载更新
            mIsClickUpdated = true;
            mLlShowDownload.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mBtLater.setVisibility(View.GONE);
            mBtNow.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
            downLoadApp();
        } else if (i == R.id.iv_cancel) { // 关闭更新弹框
            if (mUpdateDialog != null) {
                mUpdateDialog.dismiss();
            }
        } else if (i == R.id.iv_alert_cancel) { // 关闭权限弹框
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        } else if (i == R.id.bt_cancel) { // 关闭权限弹框
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        } else if (i == R.id.bt_open) { // 开启权限弹框
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
            mContext.startActivity(intent);
        }
    }


}
