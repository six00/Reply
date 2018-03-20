package com.example.six.reply;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Switch mStartSwitch;
    private EditText mDelayTimeEd;
    private EditText mCatchContent;
    private EditText mCutStart;
    private EditText mCutEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartSwitch = findViewById(R.id.start);
        mDelayTimeEd = findViewById(R.id.delay_timte);
        mCatchContent = findViewById(R.id.catchContent);
        mCutEnd = findViewById(R.id.cutEnd);
        mCutStart = findViewById(R.id.cutStart);
        init();
        mStartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    if (!isAccessibilitySettingsOn(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "请前往辅助功能给予权限", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                    AutoReplyService.sCurState = AutoReplyService.DEFAULT;
                } else {
                    AutoReplyService.sCurState = AutoReplyService.STOP;
                }
            }
        });

        findViewById(R.id.set_catchContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catchContent = mCatchContent.getText().toString();
                if(!TextUtils.isEmpty(catchContent)){
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                    sp.edit().putString(AutoReplyService.SET_CATCH_CONTENT,catchContent).apply();
                    Toast.makeText(MainActivity.this, "成功设置自定义检测内容", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.set_cutStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cutStart = mCutStart.getText().toString();
//                if(!TextUtils.isEmpty(cutStart)){
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                    sp.edit().putString(AutoReplyService.SET_CUT_START,cutStart).apply();
                    Toast.makeText(MainActivity.this, "成功设置截断起始字符", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        findViewById(R.id.set_cutEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cutEnd = mCutEnd.getText().toString();
//                if(!TextUtils.isEmpty(cutEnd)){
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                    sp.edit().putString(AutoReplyService.SET_CUT_END,cutEnd).apply();
                    Toast.makeText(MainActivity.this, "成功设置截断结束字符", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                sp.edit().putString(AutoReplyService.SET_CATCH_CONTENT,"").apply();
                sp.edit().putString(AutoReplyService.SET_CUT_START,"").apply();
                sp.edit().putString(AutoReplyService.SET_CUT_END,"").apply();
                mCatchContent.setText("");
                mCutStart.setText("");
                mCutEnd.setText("");
                Toast.makeText(MainActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
            }
        });
        //设置延迟时间
//        findViewById(R.id.set_delay_time).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String delayTime = mDelayTimeEd.getText().toString();
//                if (!TextUtils.isEmpty(delayTime)) {
//                    SharedPreferences sp = getSharedPreferences(AutoReplyService.RED_ENVELOP_SP, MODE_PRIVATE);
//                    Integer time = Integer.valueOf(delayTime);
//                    if (time < 0) {
//                        time = 0;
//                    }
//                    sp.edit().putInt(AutoReplyService.SET_DELAY_TIME, time).apply();
//                    Toast.makeText(MainActivity.this, "成功设置延迟:" + time + "毫秒", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    private void init() {
        SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP, MODE_PRIVATE);
        String content = sp.getString(AutoReplyService.SET_CATCH_CONTENT,"");
        String start = sp.getString(AutoReplyService.SET_CUT_START,"");
        String end = sp.getString(AutoReplyService.SET_CUT_END,"");
        if(content.isEmpty()){
            Toast.makeText(this, "请先设置检测内容", Toast.LENGTH_LONG).show();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(mCatchContent,0);
        }else{
            mCatchContent.setText(content);
        }
        if(!start.isEmpty())
            mCutStart.setText(start);
        if(!end.isEmpty())
            mCutEnd.setText(end);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(isServiceRunning()){
//            Intent i = new Intent(this, AutoReplyService.class);
//            getApplicationContext().startService(i);
//
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccessibilitySettingsOn(this) && AutoReplyService.sCurState != AutoReplyService.STOP) {
            mStartSwitch.setChecked(true);
        } else {
            mStartSwitch.setChecked(false);
        }

    }

    /**
     * 此方法用来判断当前应用的辅助功能服务是否开启
     */
    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    /**
     * 判断自己的应用的AccessibilityService是否在运行
     *
     * @return
     */
    /**
     * 判断服务是否正在运行
     * @return
     */
    public boolean isServiceRunning() {
        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Short.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo runningService : runningServices) {
                if (runningService.service.getClassName().equalsIgnoreCase(AutoReplyService.class.getName())) {
                    ret = true;
                }
            }
        }
        return ret;
    }
}