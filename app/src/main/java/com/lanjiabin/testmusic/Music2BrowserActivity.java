package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Music2BrowserActivity extends Activity{
    private Button mAllSongsBtn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    public void initView(){
        mContext=Music2BrowserActivity.this;
        setContentView(R.layout.activity_music2_browser);
        mAllSongsBtn=findViewById(R.id.allSongsBtn);

        mAllSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(mContext,AllSongsActivity.class);
                startActivity(intent);
            }
        });
    }

}
