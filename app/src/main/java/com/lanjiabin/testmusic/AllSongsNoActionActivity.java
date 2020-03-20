package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


public class AllSongsNoActionActivity extends Activity {
    private ListView mAllSongsLV;
    private Context mContext;
    private ArrayList<HashMap<String, String>> mAllSongArray;
    private String mPlayListName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        OnClick();
    }

    public void initView() {
        mContext = getApplicationContext();
        setContentView(R.layout.activity_all_songs_no_action);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mContext = getApplicationContext();
        mAllSongArray = MusicDBService.getInstance().queryAllSongsList(mContext);
        Log.v("mAllSongArray","mAllSongArray="+mAllSongArray);
        if (!mAllSongArray.isEmpty()) {
            setListViewAdapter();
        }
        Intent intent = getIntent();
        mPlayListName = intent.getStringExtra("playListName");
    }

    public void setListViewAdapter() {
        String[] musicPlayListName = new String[mAllSongArray.size()];
        int j = 0;
        for (int i = 0; i < mAllSongArray.size(); i++) {
            String playlistName = mAllSongArray.get(i).get("musictitle");
            musicPlayListName[j++] = playlistName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.music_playlist_item,
                R.id.playlistsItemTV,
                musicPlayListName);
        mAllSongsLV.setAdapter(adapter);
    }

    public void OnClick() {
        mAllSongsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String musicId = mAllSongArray.get(position).get("musicid");
                if (!mAllSongArray.isEmpty() && !mPlayListName.isEmpty()){
                    MusicDBService.getInstance().updateForMusicId(mContext, mPlayListName, musicId);
                }
                finish();
            }
        });
    }

}
