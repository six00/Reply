package com.example.six.reply;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Six on 2018/3/13.
 */
public class AutoReplyService extends AccessibilityService {
    private static final String TAG = "AutoReplyService";
    private Handler mHandler = new Handler();

    public static final String RED_ENVELOP_SP = "RED_ENVELOP_SP";
    public static final String SET_DELAY_TIME = "SET_DELAY_TIME";
    public static final String SET_LOCATION_X = "SET_LOCATION_X";
    public static final String SET_LOCATION_Y = "SET_LOCATION_Y";


    @Override
    public void onCreate() {
        super.onCreate();
        sCurState = DEFAULT;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 页面变化回调事件
     *
     * @param event event.getEventType() 当前事件的类型;
     *              event.getClassName() 当前类的名称;
     *              event.getSource() 当前页面中的节点信息；得到的是被点击的单体对象
     *              event.getPackageName() 事件源所在的包名
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("six", "ok");
        if (sCurState != STOP_RED) {
            Log.d(TAG, "onAccessibilityEvent: sCurState=>" + sCurState);
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();

            //event.getSource:得到的是被点击的单体对象
            //getRootInActiveWindow():整个窗口的对象
            switch (event.getEventType()) {
//                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                    clickNotification(rootInActiveWindow, event);
////                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                    if (event.getPackageName().equals("com.tencent.mm")) {
                    reply(rootInActiveWindow);
                    Log.i("six", "good to go");
//                    }
                    break;
                default:
                    return;
            }
        }
//        showAllText(getRootInActiveWindow());
    }

    public static int sCurState;
    public static final int DEFAULT = 0;
    public static final int CHECK_RED = 1;
    public static final int GET_RED = 2;
    public static final int STOP_RED = 3;

//    private boolean isNeedBack = true;

    /**
     * 点击
     *
     * @throws Exception
     */
    private void reply(final AccessibilityNodeInfo rootInActiveWindow) {

        if (rootInActiveWindow != null) {
            final SharedPreferences sp = getSharedPreferences(AutoReplyService.RED_ENVELOP_SP, MODE_PRIVATE);
            //点击红包
            if (sCurState == DEFAULT) {

//                List<AccessibilityNodeInfo> checkRedNodeInfos = null;
//six
                List<AccessibilityNodeInfo> msgNode = rootInActiveWindow.findAccessibilityNodeInfosByText("");

                if (msgNode != null && msgNode.size() > 0) {
                    Log.i("six", "bingo!");
                    //发送对应消息的代码
                    CharSequence msgChar = msgNode.get(2).getText();
                    String msg = msgChar + "";
                    int begin = msg.indexOf("“") + 1;
                    int end = msg.indexOf("”");
                    String toSend = msg.substring(begin, end);
                    Log.i("six", toSend);

                    List<AccessibilityNodeInfo> editTextNodeInfo = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/et_sendmessage");
                    List<AccessibilityNodeInfo> sendNodeInfo = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/btn_send");

                    if (!editTextNodeInfo.isEmpty() && editTextNodeInfo != null) {
//                        editTextNodeInfo.get(0).setText(toSend);
                        setTextToView(editTextNodeInfo.get(0), toSend);
                    } else
                        return;

                    if (!sendNodeInfo.isEmpty() && sendNodeInfo != null) {
                        sendNodeInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else
                        return;
                    sCurState = STOP_RED;
                } else {
                    return;
                }
//six
            }
        }
    }

    private void setTextToView(AccessibilityNodeInfo node, String text) {
        Bundle arguments = new Bundle();
        arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
        arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                true);
        node.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                arguments);
        /*判断下当前版本*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {
            ClipData data = ClipData.newPlainText("reply", text);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }

    }

    /**
     * 点击通知栏
     *
     * @param rootInActiveWindow
     * @param event
     */
//    private void clickNotification(AccessibilityNodeInfo rootInActiveWindow, AccessibilityEvent event) {
//        List<CharSequence> eventText = event.getText();
//        if (eventText != null) {
//            for (CharSequence key : eventText) {
//                Log.d(TAG, "clickNotification: key=>" + key);
//                if (((String) key).contains("[微信]")) {
//                    Notification notification = (Notification) event.getParcelableData();
//                    try {
//                        notification.contentIntent.send();//点击通知栏
//                    } catch (PendingIntent.CanceledException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }



    /**
     * 显示当前信息
     *
     * @param accessibilityNodeInfo
     */
//    private void showAllText(AccessibilityNodeInfo accessibilityNodeInfo) {
//        if (accessibilityNodeInfo != null) {
//            if (accessibilityNodeInfo.getChildCount() == 0) {
//                Log.v(TAG, "找到了view的文本是:" + accessibilityNodeInfo.getText());
//                Log.v(TAG, "当前应用的包名是:" + accessibilityNodeInfo.getPackageName() + "  "
//                        + accessibilityNodeInfo.getClassName());
//                Log.v(TAG, "当前的getViewIdResourceName：" + accessibilityNodeInfo.getViewIdResourceName());
//                Log.v(TAG, "当前的getWindowId：" + accessibilityNodeInfo.getWindowId());
//            } else {
//                for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
//                    showAllText(accessibilityNodeInfo.getChild(i));
//                }
//            }
//        }
//    }

    /**
     * 中断AccessibilityService的反馈时调用
     */
    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

}
