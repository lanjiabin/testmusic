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
import android.widget.SimpleAdapter;

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

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mAllSongArray.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mAllSongArray.get(i).get("musictitle"));
            map.put("artist", mAllSongArray.get(i).get("musicartist"));
            map.put("duration", getTime(mAllSongArray.get(i).get("musicduration")));
            list.add(map);
        }

        SimpleAdapter adapter1 = new SimpleAdapter(
                mContext,
                list,
                R.layout.music_allsongs_item,
                new String[]{"title", "duration", "artist"},
                new int[]{R.id.title, R.id.duration, R.id.artist});

        mAllSongsLV = findViewById(R.id.allSongsLV);
        mAllSongsLV.setAdapter(adapter1);



//
//        String[] musicPlayListName = new String[mAllSongArray.size()];
//        int j = 0;
//        for (int i = 0; i < mAllSongArray.size(); i++) {
//            String playlistName = mAllSongArray.get(i).get("musictitle");
//            musicPlayListName[j++] = playlistName;
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                R.layout.music_playlist_item,
//                R.id.playlistsItemTV,
//                musicPlayListName);
//        mAllSongsLV.setAdapter(adapter);
    }

    private String getTime(String duration) {
        int time = Integer.parseInt(duration) / 1000;
        int mMinutes = 0;
        while (time >= 60) {
            mMinutes++;
            time -= 60;
        }
        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
                + (time < 10 ? "0" + time : time);

        return all;
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
