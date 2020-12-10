package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.DiscussItemAdapter;
import com.zkml.meetingtablecard.application.MyApplication;
import com.zkml.meetingtablecard.bean.DiscussItemBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.constant.IPConfig;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.JsonUtil;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.Utils;
import com.zkml.meetingtablecard.utils.asynctask.HttpGetAsyncTask;
import com.zkml.meetingtablecard.utils.asynctask.HttpPostAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.view.dialog.CommentDialog;
import com.zkml.meetingtablecard.view.dragbutton.FloatDragView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author: zzh
 * data : 2020/12/08
 * description：互动讨论
 */
public class DiscussActivity extends AppCompatActivity {

    /**
     * 会议信息列表
     */
    private RecyclerView recyclerView;
    private List<DiscussItemBean> discussList = new ArrayList<>();
    private DiscussItemAdapter adapter;
    private SharedPreferences spf;
    /**
     * 会议id, userId, commentId
     */
    private String meetingApplyId, userId, commentId, comment;
    private RelativeLayout relativeLayout;
    private CommentDialog dialog;
    private LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        ActivityManager.getInstance().addActivity(this);
        initView();
        initEvent();
        getApplyCommentList();
    }

    /**
     * 初始化view
     */
    private void initView() {
        dialog = new CommentDialog(this);
        spf = ActivityUtils.selSharedPreferencesData(this, MyApplication.USER_INFO_NAME);
        userId = spf.getString("userId", "");
        meetingApplyId = getIntent().getStringExtra("meetingApplyId");
        // 连接并接收服务器推送消息
        connectMqtt();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        relativeLayout = findViewById(R.id.relativeLayout);
//        mRootView 就是要出现悬浮按钮的界面的根view。就是setContentView的View。

        FloatDragView.addFloatDragView(this, relativeLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击事件
                if (dialog == null){
                    dialog = new CommentDialog(DiscussActivity.this);
                } else {
                    dialog.show();
                }
            }
        });
    }

    private void initEvent() {
        llBack = findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.setOnCommitListener(new CommentDialog.OnCommitListener() {
            @Override
            public void onCommit(EditText et, View v) {
                comment = et.getText().toString().trim();
                sendComment();
            }
        });

        adapter = new DiscussItemAdapter(this, userId, discussList);
        adapter.setOnItemMyClickListener(new DiscussItemAdapter.OnItemMyClickListener() {
            @Override
            public void onItemMyClick(View view, String id, boolean currentLikeFlag, int pos) {
                if (!currentLikeFlag) {
                    likeComment(id, currentLikeFlag, pos);
                } else {
                    unLikeComment(id, currentLikeFlag, pos);
                }
            }
        });

        adapter.setOnItemOtherClickListener(new DiscussItemAdapter.OnItemOtherClickListener() {
            @Override
            public void onItemOtherClick(View view, String id, boolean currentLikeFlag, int pos) {
                if (!currentLikeFlag) {
                    likeComment(id, currentLikeFlag, pos);
                } else {
                    unLikeComment(id, currentLikeFlag, pos);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取会议互动讨论列表
     */
    private void getApplyCommentList() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("meetingApplyId", meetingApplyId);
            HttpGetAsyncTask commAsyncTask = new HttpGetAsyncTask(this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setGetCompleteListener(new HttpGetAsyncTask.ReqGetCompleteListener() {
                @Override
                public void reqGetComplete(Map<String, Object> resultMap) {
//                    String resultStr = Utils.readAssert(DiscussActivity.this, "test.txt");
//                    resultMap = StringUtils.transResultJsonToMap(resultStr);
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            String data = (String) resultMap.get("data");
                            if (!StringUtils.isStrEmpty(data)) {
                                setDiscussList(data);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
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
            commAsyncTask.execute(Constant.GET_COMMENT_LIST, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    private void setDiscussList(String data) {
        discussList.clear();
        TypeToken<List<DiscussItemBean>> typeToken = new TypeToken<List<DiscussItemBean>>() {
        };
        List<DiscussItemBean> modelBeanList = (List<DiscussItemBean>) StringUtils.convertMapToList(data, typeToken);
        if (modelBeanList != null && modelBeanList.size() > 0) {
            discussList.addAll(modelBeanList);
        }
    }

    /**
     * 点赞
     */
    private void likeComment(String id, boolean currentLikeFlag, int pos) {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("commentId", id);
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(this, null);
            commAsyncTask.setShowDialog(2);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            DiscussItemBean bean = discussList.get(pos);
                            bean.setCurrentLikeFlag(true);
                            bean.setTotalLikeCount(bean.getTotalLikeCount() + 1);
                            adapter.notifyDataSetChanged();
                        } else {
                            String message = (String) resultMap.get("message");
                            showCustomToast(message);
                        }
                    } else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), Constant.GET_LIKE_COMMENT, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    /**
     * 取消点赞
     */
    private void unLikeComment(String id, boolean currentLikeFlag, int pos) {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("commentId", id);
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(this, null);
            commAsyncTask.setShowDialog(2);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            DiscussItemBean bean = discussList.get(pos);
                            bean.setCurrentLikeFlag(false);
                            bean.setTotalLikeCount(bean.getTotalLikeCount() - 1);
                            adapter.notifyDataSetChanged();
                        } else {
                            String message = (String) resultMap.get("message");
                            showCustomToast(message);
                        }
                    } else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), Constant.GET_UNLIKE_COMMENT, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    //

    /**
     * 连接并接收服务器推送消息
     * topic是每个会议的id
     */
    private void connectMqtt() {
        // 读取订阅toplist
        String serverURI = IPConfig.SERVER_URI;
        String clientId = userId + "_8700@3400@000000";
        String username = "{'routerIdentification':'8700@3400@000000'}";
        String password = "";

        LogUtil.i("zzz1", "clientId " + clientId);
        int qos = 1;
        try {
            final MqttClient client = new MqttClient(serverURI, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            options.setMqttVersion(4);
            options.setAutomaticReconnect(true);
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    // 回调即连接成功
//                    log.info("connectComplete {} {}", reconnect, serverURI);
//                    LogUtil.i("connectComplete", "reconnect " + reconnect + "serverURI " + serverURI);
                    LogUtil.i("zzz1", "connectComplete " + "reconnect " + reconnect + " serverURI " + serverURI);
                }

                //                @SneakyThrows
                @Override
                public void connectionLost(Throwable cause) {
//                    log.info("this method is called when the connection to the server is lost");
//                    LogUtil.i("connectionLost", "this method is called when the connection to the server is lost ");
                    LogUtil.i("zzz1", "connectionLost");

                    try {
                        Thread.sleep(500);
                        LogUtil.i("zzz1", "reconnect");
                        client.reconnect();
                    } catch (Exception e) {

                    }
                }

                @Override

                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    String message = new String(mqttMessage.getPayload(), Charset.defaultCharset());
//                    log.info(message);
//                    LogUtil.i("messageArrived", "" + message);
                    LogUtil.i("zzz1", "messageArrived " + message);

                    if (!StringUtils.isStrEmpty(message)) {
                        getApplyCommentList();
                    }
                }

                @Override

                public void deliveryComplete(IMqttDeliveryToken token) {
//                    log.info("Called when delivery for a message has been completed");
//                    LogUtil.i("deliveryComplete", "" + token);
                    LogUtil.i("zzz1", "deliveryComplete " + token);
                }
            });

            try {
                client.connect(options);
                LogUtil.i("zzz1", "start connect");
                client.subscribe(meetingApplyId, qos);
            } catch (Exception ex) {
//                log.error("Connection Cause Exception {}", ex.toString());
//                LogUtil.i("Connection Cause Exception {}", "" + ex.toString());
                LogUtil.i("zzz1", "connect Exception" + ex.toString());
            }
        } catch (Exception e) {
            LogUtil.i("zzz1", "Exception1  " + e.toString());
        }

    }

    /**
     * 发表互动聊天内容
     */
    private void sendComment() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            if (StringUtils.isStrEmpty(comment)){
                showCustomToast(getString(R.string.send_tip));
                return;
            }
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("comment", comment);
            reqParamMap.put("meetingApplyId", meetingApplyId);
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            if (dialog != null){
                                dialog.clearText();
                                dialog.dismiss();
                            }
                            getApplyCommentList();
                        } else {
                            String message = (String) resultMap.get("message");
                            showCustomToast(message);
                        }
                    } else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.execute(Constant.GET_SEND_COMMENT, null, JsonUtil.toJson(reqParamMap));
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
