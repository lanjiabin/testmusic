package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class VideoDetailsActivity extends Activity {
    public Button mFileNameBtn,mDurationBtn,mSizeBtn,mLocationBtn,mResolutionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    public void initView(){
        setContentView(R.layout.activity_video_details);
        mFileNameBtn=findViewById(R.id.fileNameBtn);
        mDurationBtn=findViewById(R.id.durationBtn);
        mSizeBtn=findViewById(R.id.sizeBtn);
        mLocationBtn=findViewById(R.id.locationBtn);
        mResolutionBtn=findViewById(R.id.resolutionBtn);

        Intent intent=getIntent();
        String path=intent.getStringExtra("path");
        String name=intent.getStringExtra("name");
        String resolution=intent.getStringExtra("resolution");
        String size=intent.getStringExtra("size");
        String date=intent.getStringExtra("date");
        String duration=intent.getStringExtra("duration");

        mFileNameBtn.setText(getFileName(path));
        mDurationBtn.setText(getTime(duration));
        mSizeBtn.setText(bytesToMB(size));
        mLocationBtn.setText(path);
        mResolutionBtn.setText(resolution);

    }



    public String getFileName(String fileName) {
        int index = fileName.lastIndexOf("/")+1;
        return fileName.substring(index, fileName.length());
    }

    private String getTime(String duration) {
        int time=Integer.parseInt(duration)/1000;
        int mMinutes = 0;
        while (time >= 60) {
            mMinutes++;
            time -= 60;
        }
        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
                + (time < 10 ? "0" + time : time);

        return  all;
    }

    private String bytesToMB(String bytes){
        double bytesDouble=Double.parseDouble(bytes);
        double mb = bytesDouble/1024/1024;
        return String.format("%.2f", mb)+" MB";
    }
}
