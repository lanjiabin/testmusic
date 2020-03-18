package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.Map;

public class AllSongsActivity extends Activity {
    private ListView mAllSongsLV;
    private MusicControlService mMusicControlService;
    private Context mContext;
    private ServiceConnection mMusicServiceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        OnClick();
    }

    public void initView() {
        mContext=getApplicationContext();
        setContentView(R.layout.activity_all_songs);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mContext=getApplicationContext();

        mMusicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicControlService = ((MusicBinder) service).getService();
                setListViewAdapter();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent musicServiceIntent = new Intent(mContext, MusicControlService.class);
        bindService(musicServiceIntent, mMusicServiceConnection, BIND_AUTO_CREATE);
    }

    private void setListViewAdapter() {
        String[] musicNameArr = new String[mMusicControlService.mMusicList.size()];
        int i = 0;
        for (Map<String, Object> path : mMusicControlService.mMusicList) {
            File file = new File(String.valueOf(path.get("url")));
            musicNameArr[i++] = file.getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                musicNameArr);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mAllSongsLV.setAdapter(adapter);
    }
    public void OnClick(){

        mAllSongsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicControlService.mSongNum=position;
                mMusicControlService.play();
                Intent intent=new Intent(mContext,NowPlayingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
    }
}
