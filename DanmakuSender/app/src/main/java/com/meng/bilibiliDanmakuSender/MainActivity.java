package com.meng.bilibiliDanmakuSender;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.meng.bilibiliDanmakuSender.lib.ExceptionCatcher;
import com.meng.bilibiliDanmakuSender.lib.SharedPreferenceHelper;

public class MainActivity extends Activity{
    public static SharedPreferenceHelper sharedPreference;
    public static Boolean lightTheme=true;
    
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //ExceptionCatcher.getInstance().init(this);
        sharedPreference=new SharedPreferenceHelper(this,"main");
        lightTheme=sharedPreference.getBoolean("useLightTheme",true);
        startActivity(new Intent(MainActivity.this,MainActivity2.class).putExtra("setTheme",getIntent().getBooleanExtra("setTheme",false)));
        finish();
        overridePendingTransition(0,0);
    }

}
