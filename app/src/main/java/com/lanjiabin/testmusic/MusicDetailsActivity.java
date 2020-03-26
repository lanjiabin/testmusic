package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicDetailsActivity extends Activity {
    private Button mFileNameBtn,mTitleBtn,mDurationBtn,mSizeBtn,mLocationBtn,mArtistBtn;
    private String mMusicID;
    ArrayList<HashMap<String, String>> mSongInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    public void initView(){
        Intent intent=getIntent();
        mMusicID=intent.getStringExtra("musicID");
        mSongInfo=MusicDBService.getInstance().queryMusicInfoByID(this,mMusicID);

        String musicTitle=mSongInfo.get(0).get("musictitle"); //标题
        String musicArtist=mSongInfo.get(0).get("musicartist"); //作者
        String musicDuration=mSongInfo.get(0).get("musicduration"); //总共时长
        String musicSize=mSongInfo.get(0).get("musicsize"); //文件大小
        String musicUrl=mSongInfo.get(0).get("musicurl"); //文件地址
        String musicFileName=getFileName(musicUrl); //文件名

        setContentView(R.layout.activity_music_details);
        mFileNameBtn=findViewById(R.id.fileNameBtn);
        mTitleBtn=findViewById(R.id.titleBtn);
        mArtistBtn=findViewById(R.id.artistBtn);
        mDurationBtn=findViewById(R.id.durationBtn);
        mSizeBtn=findViewById(R.id.sizeBtn);
        mLocationBtn=findViewById(R.id.locationBtn);

        mFileNameBtn.setText(musicFileName);
        mTitleBtn.setText(musicTitle);
        mArtistBtn.setText(musicArtist);
        mDurationBtn.setText(getTime(musicDuration));
        mSizeBtn.setText( bytesToMB(musicSize));
        mLocationBtn.setText(musicUrl);

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
