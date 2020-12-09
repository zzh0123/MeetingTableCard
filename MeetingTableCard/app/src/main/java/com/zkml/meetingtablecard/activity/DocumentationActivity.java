package com.zkml.meetingtablecard.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.reflect.TypeToken;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.FileItemAdapter;
import com.zkml.meetingtablecard.bean.FileItemBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpGetAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author: zzh
 * data : 2020/12/07
 * description：会议资料
 */
public class DocumentationActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    /**
     * 会议信息列表
     */
    private RecyclerView recyclerView;
    private List<FileItemBean> fileList = new ArrayList<>();
    private FileItemAdapter adapter;

    /**
     * 会议id
     */
    private String meetingApplyId;
    /**
     * fileUrl
     */
    private String mFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentation);
        ActivityManager.getInstance().addActivity(this);
        initView();
        initEvent();
        getMeetingMaterials();
    }

    /**
     * 初始化view
     */
    private void initView() {
        meetingApplyId = getIntent().getStringExtra("meetingApplyId");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initEvent() {
        adapter = new FileItemAdapter(this, fileList);
        adapter.setOnItemDownloadClickListener(new FileItemAdapter.OnItemDownloadClickListener() {
            @Override
            public void onItemDownloadClick(View view, String fileUrl, int pos) {

                if (!EasyPermissions.hasPermissions(DocumentationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // ask permission with ReasonOfPermission, RequestCord and PermissionName
                    mFileUrl = fileUrl;
                    EasyPermissions.requestPermissions(DocumentationActivity.this, getString(R.string.need_storage), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                LogUtil.i("zzz1", "requestPermissions ");
                } else {
                    showFile(fileUrl);
                }


            }
        });
//        adapter.setOnItemClickListener(new MeetingItemAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int pos) {
//                MeetingItemBean itemBean = meetingList.get(pos);
//                if (itemBean != null){
//                    Intent intent = new Intent(MeetingListActivity.this, HomeActivity.class);
//                    intent.putExtra("itemBean",itemBean);
//                    startActivity(intent);
//                }
//            }
//        });
        recyclerView.setAdapter(adapter);
    }

    private void showFile(String fileUrl){
        Intent intent = new Intent(DocumentationActivity.this, FileDisplayActivity.class);
        intent.putExtra("fileUrl", fileUrl);
        startActivity(intent);
    }

    /**
     * 获取会议资料
     */
    private void getMeetingMaterials() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("meetingApplyId", meetingApplyId);
            reqParamMap.put("fileName", "");
            HttpGetAsyncTask commAsyncTask = new HttpGetAsyncTask(this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setGetCompleteListener(new HttpGetAsyncTask.ReqGetCompleteListener() {
                @Override
                public void reqGetComplete(Map<String, Object> resultMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            String data = (String) resultMap.get("data");
                            if (!StringUtils.isStrEmpty(data)) {
                                Map<String, Object> dataMap = StringUtils.transJsonToMap(data);
                                String imgFiles = (String) dataMap.get("imgFiles");
                                setFileList(imgFiles);
                                String files = (String) dataMap.get("files");
                                setFileList(files);
                                adapter.notifyDataSetChanged();
                            } else {
                                showCustomToast(getString(R.string.system_error_tip));
                            }
                        } else {
                            String message = (String) resultMap.get("message");
                            showCustomToast(message);
                        }
                    } else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.execute(Constant.GET_MEETING_MATERIALS, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    private void setFileList(String files){
        if (!StringUtils.isStrEmpty(files)){
            TypeToken<List<FileItemBean>> typeToken = new TypeToken<List<FileItemBean>>() {
            };
            List<FileItemBean> modelBeanList = (List<FileItemBean>) StringUtils.convertMapToList(files, typeToken);
            if (modelBeanList != null && modelBeanList.size() > 0) {
                fileList.addAll(modelBeanList);
            }
        }
    }

    // 动态权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // 允许使用权限
        if (requestCode == 1) {
            showFile(mFileUrl);
            LogUtil.i("zzz1", "onPermissionsGranted: ");
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // 拒绝使用权限
        if (requestCode == 1) {
            LogUtil.i("zzz1", "onPermissionsDenied: ");
//            mVersionUpdateManager.showAlertDialog();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        // 再次询问权限，点击确认回调
        if (requestCode == 1) {
            LogUtil.i("zzz1", "onRationaleAccepted: ");
        }
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        // 再次询问权限，点击取消回调
        if (requestCode == 1) {
            showCustomToast(getString(R.string.need_storage));
            LogUtil.i("zzz1", "onRationaleDenied: ");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
