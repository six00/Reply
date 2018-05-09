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
import android.widget.Button;
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
    private Switch mModeSwitch;
    private EditText mSelfContent;
    private Button mCutStartB;
    private Button mCutEndB;
    private Button mSetCatchContentB;
    private Button mSetSelfContentB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartSwitch = findViewById(R.id.start);
        mDelayTimeEd = findViewById(R.id.delay_timte);
        mCatchContent = findViewById(R.id.catchContent);
        mCutEnd = findViewById(R.id.cutEnd);
        mCutStart = findViewById(R.id.cutStart);
        mModeSwitch = findViewById(R.id.mode);
        mSelfContent = findViewById(R.id.selfContent);
        mCutStartB = findViewById(R.id.set_cutStart);
        mCutEndB = findViewById(R.id.set_cutEnd);
        mSetCatchContentB = findViewById(R.id.set_catchContent);
        mSetSelfContentB = findViewById(R.id.set_selfContent);
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
//                    Intent i = new Intent(MainActivity.this,AutoReplyService.class);
//                    startService(i);
                } else {
                    AutoReplyService.sCurState = AutoReplyService.STOP;
                }
            }
        });

        mModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mCutStart.setEnabled(false);
                    mCutEnd.setEnabled(false);
                    mCutStartB.setEnabled(false);
                    mCutEndB.setEnabled(false);
                    mSelfContent.setEnabled(true);
                    mSetSelfContentB.setEnabled(true);
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                    sp.edit().putInt("rMode",1).apply();
                    AutoReplyService.rMode = 1;
                }else{
                    mCutStart.setEnabled(true);
                    mCutEnd.setEnabled(true);
                    mCutStartB.setEnabled(true);
                    mCutEndB.setEnabled(true);
                    mSelfContent.setEnabled(false);
                    mSetSelfContentB.setEnabled(false);
                    AutoReplyService.rMode = 0;
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                    sp.edit().putInt("rMode",0).apply();
                }
            }
        });
        //设置自定义检测内容按钮
        mSetCatchContentB.setOnClickListener(new View.OnClickListener() {
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
        //设置截断起始字符按钮
        mCutStartB.setOnClickListener(new View.OnClickListener() {
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
        //设置截断结束字符
        mCutEndB.setOnClickListener(new View.OnClickListener() {
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
        //设置自定义回复内容按钮
        mSetSelfContentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selfContent = mSelfContent.getText().toString();
                SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                sp.edit().putString(AutoReplyService.SET_SELF_CONTENT,selfContent).apply();
                Toast.makeText(MainActivity.this, "成功设置自定义回复内容", Toast.LENGTH_SHORT).show();
            }
        });
        //重置按钮
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP,MODE_PRIVATE);
                sp.edit().putString(AutoReplyService.SET_CATCH_CONTENT,"").apply();
                sp.edit().putString(AutoReplyService.SET_CUT_START,"").apply();
                sp.edit().putString(AutoReplyService.SET_CUT_END,"").apply();
                sp.edit().putString(AutoReplyService.SET_SELF_CONTENT,"").apply();
                mSelfContent.setText("");
                mCatchContent.setText("");
                mCutStart.setText("");
                mCutEnd.setText("");
                Toast.makeText(MainActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
            }
        });
        //设置延迟时间
        findViewById(R.id.set_delay_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String delayTime = mDelayTimeEd.getText().toString();
                if (!TextUtils.isEmpty(delayTime)) {
                    SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP, MODE_PRIVATE);
                    Integer time = Integer.valueOf(delayTime);
                    if (time < 0) {
                        time = 0;
                    }
                    sp.edit().putInt(AutoReplyService.SET_DELAY_TIME, time).apply();
                    Toast.makeText(MainActivity.this, "成功设置延迟:" + time + "毫秒", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences(AutoReplyService.CONTENT_SP, MODE_PRIVATE);
        String content = sp.getString(AutoReplyService.SET_CATCH_CONTENT,"");
        String start = sp.getString(AutoReplyService.SET_CUT_START,"");
        String end = sp.getString(AutoReplyService.SET_CUT_END,"");
        String selfContent = sp.getString(AutoReplyService.SET_SELF_CONTENT,"");
        String delayMillis = sp.getInt(AutoReplyService.SET_DELAY_TIME,0)+"";
        AutoReplyService.rMode = sp.getInt("rMode",0);
        if(AutoReplyService.rMode == 1) {
            mModeSwitch.setChecked(true);
            mCutStart.setEnabled(false);
            mCutEnd.setEnabled(false);
            mCutStartB.setEnabled(false);
            mCutEndB.setEnabled(false);
            mSelfContent.setEnabled(true);
            mSetSelfContentB.setEnabled(true);
        }else{
            mModeSwitch.setChecked(false);
            mCutStart.setEnabled(true);
            mCutEnd.setEnabled(true);
            mCutStartB.setEnabled(true);
            mCutEndB.setEnabled(true);
            mSelfContent.setEnabled(false);
            mSetSelfContentB.setEnabled(false);
        }
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
        if(!selfContent.isEmpty())
            mSelfContent.setText(selfContent);
        if(!delayMillis.isEmpty())
            mDelayTimeEd.setText(delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccessibilitySettingsOn(this) && AutoReplyService.sCurState != AutoReplyService.STOP) {
            mStartSwitch.setChecked(true);
        } else {
            mStartSwitch.setChecked(false);
        }
        if(!isServiceRunning()) {
            Toast.makeText(this, "service no", Toast.LENGTH_SHORT).show();
//            Log.i("six","service no");
//            Intent i = new Intent(this, AutoReplyService.class);
//            startService(i);
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