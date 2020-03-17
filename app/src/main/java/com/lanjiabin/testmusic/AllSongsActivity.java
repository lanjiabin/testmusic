package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.Map;

public class AllSongsActivity extends Activity {
    private ListView mAllSongsLV;
    private MusicService mMusicService;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListViewAdapter();
    }

    public void initView() {
        setContentView(R.layout.activity_all_songs);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mContext=getApplicationContext();
        mMusicService=new MusicService(mContext);
    }

    private void setListViewAdapter() {
        String[] musicNameArr = new String[mMusicService.mMusicList.size()];
        int i = 0;
        for (Map<String, Object> path : mMusicService.mMusicList) {
            File file = new File(String.valueOf(path.get("url")));
            musicNameArr[i++] = file.getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                musicNameArr);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mAllSongsLV.setAdapter(adapter);
    }
}
