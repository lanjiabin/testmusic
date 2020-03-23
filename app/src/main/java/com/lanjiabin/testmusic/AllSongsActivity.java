package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSongsActivity extends Activity {
    private ListView mAllSongsLV;
    private Button mMenuBtn;
    private MusicControlService mMusicControlService;
    private Context mContext;
    private ServiceConnection mMusicServiceConnection;
    private ArrayList<HashMap<String, String>> mPlayAllSongListArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        OnClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(mContext,Music2BrowserActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initView() {
        mContext=getApplicationContext();
        setContentView(R.layout.activity_all_songs);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mMenuBtn=findViewById(R.id.menuBtn);
        mContext=getApplicationContext();
        mPlayAllSongListArrayList=MusicDBService.getInstance().queryAllSongsList(mContext);

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
        String[] musicAllSongList = new String[mPlayAllSongListArrayList.size()];
        int j = 0;
        for (int i = 0; i < mPlayAllSongListArrayList.size(); i++) {
            String playlistName = mPlayAllSongListArrayList.get(i).get("musictitle");
            musicAllSongList[j++] = playlistName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                musicAllSongList);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mAllSongsLV.setAdapter(adapter);
    }
    public void OnClick(){

        mAllSongsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map<String, Object>> myMusicList;
                myMusicList = new ArrayList<Map<String, Object>>();
                for (int i=0;i<mPlayAllSongListArrayList.size();i++){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", mPlayAllSongListArrayList.get(i).get("musicid"));
                    map.put("title", mPlayAllSongListArrayList.get(i).get("musictitle"));
                    map.put("artist", mPlayAllSongListArrayList.get(i).get("musicartist"));
                    map.put("duration", mPlayAllSongListArrayList.get(i).get("musicduration"));
                    map.put("size", mPlayAllSongListArrayList.get(i).get("musicsize"));
                    map.put("url", mPlayAllSongListArrayList.get(i).get("musicurl"));
                    map.put("quality", mPlayAllSongListArrayList.get(i).get("musicchannel"));
                    map.put("channel", mPlayAllSongListArrayList.get(i).get("musicquality"));
                    myMusicList.add(map);
                }
                mMusicControlService.mMusicList=myMusicList;
                mMusicControlService.mSongNum=position;
                mMusicControlService.play();
                Intent intent=new Intent(mContext,NowPlayingActivity.class);
                startActivity(intent);
            }
        });

        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    public void showPopupMenu(View view){
        mMenuBtn.setText("Select");
        PopupMenu popupMenu=new PopupMenu(mContext,view);
        popupMenu.getMenuInflater().inflate(R.menu.sample_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(mContext,item.getTitle().toString(),Toast.LENGTH_LONG).show();
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                mMenuBtn.setText("Menu");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
    }
}
