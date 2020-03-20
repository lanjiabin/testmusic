package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlaylistsTreeActivity extends Activity {
    private ListView mPlaylistsTreeLV;
    private Button mAddSongBtn,mRemoveSongBtn;
    private Context mContext;
    private ArrayList<HashMap<String, String>> playTreeListArrayList;
    private String mPlayListName;

    private MusicControlService mMusicControlService;
    private ServiceConnection mMusicServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       initView();
       OnClick();
    }
    public void initView(){
        mContext=getApplicationContext();
        setContentView(R.layout.activity_music_playlists_tree);
        mPlaylistsTreeLV=findViewById(R.id.playlistsTreeLV);
        mAddSongBtn=findViewById(R.id.addSongBtn);
        mRemoveSongBtn=findViewById(R.id.removeViewBtn);
        Intent intent=getIntent();
        mPlayListName=intent.getStringExtra("playListName");
        playTreeListArrayList=MusicDBService.getInstance().queryAllSongsListForListName(mContext,mPlayListName);
        Log.v("TreeActivityName","mPlayListName="+mPlayListName);
        Log.v("TreeActivityName","playTreeListArrayList="+playTreeListArrayList.toString());
        if (!playTreeListArrayList.isEmpty()){
            setListViewAdapter();
        }
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
    public void setListViewAdapter() {
        String[] musicPlayListName = new String[playTreeListArrayList.size()];
        int j = 0;
        for (int i = 0; i < playTreeListArrayList.size(); i++) {
            String playlistName = playTreeListArrayList.get(i).get("musictitle");
            musicPlayListName[j++] = playlistName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.music_playlist_item,
                R.id.playlistsItemTV,
                musicPlayListName);
        mPlaylistsTreeLV.setAdapter(adapter);
    }
    public void OnClick(){

        mAddSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,AllSongsNoActionActivity.class);
                intent.putExtra("playListName",mPlayListName);
                startActivity(intent);
            }
        });

        mPlaylistsTreeLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPlaylistsTreeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map<String, Object>> myMusicList;
                myMusicList = new ArrayList<Map<String, Object>>();
                for (int i=0;i<playTreeListArrayList.size();i++){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", playTreeListArrayList.get(i).get("musicid"));
                    map.put("title", playTreeListArrayList.get(i).get("musictitle"));
                    map.put("artist", playTreeListArrayList.get(i).get("musicartist"));
                    map.put("duration", playTreeListArrayList.get(i).get("musicduration"));
                    map.put("size", playTreeListArrayList.get(i).get("musicsize"));
                    map.put("url", playTreeListArrayList.get(i).get("musicurl"));
                    map.put("quality", playTreeListArrayList.get(i).get("musicchannel"));
                    map.put("channel", playTreeListArrayList.get(i).get("musicquality"));
                    map.put("playlistname", playTreeListArrayList.get(i).get("playlistname"));
                    myMusicList.add(map);
                }
                mMusicControlService.mMusicList=myMusicList;
                mMusicControlService.mSongNum=position;
                mMusicControlService.play();
                Intent intent=new Intent(mContext,NowPlayingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mPlayListName.isEmpty()){
            playTreeListArrayList=MusicDBService.getInstance().queryAllSongsListForListName(mContext,mPlayListName);
        }
        if (!playTreeListArrayList.isEmpty()){
            setListViewAdapter();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
    }
}
