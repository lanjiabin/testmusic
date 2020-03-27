package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    private List<Map<String, Object>> mMusicList;
    private int mSelectedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        OnClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent intent = new Intent(mContext, Music2BrowserActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
            showPopupMenu(mMenuBtn);
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public void initView() {
        mContext = getApplicationContext();
        setContentView(R.layout.activity_all_songs);
        mAllSongsLV = findViewById(R.id.allSongsLV);
        mMenuBtn = findViewById(R.id.allMenuBtn);
        mContext = getApplicationContext();
        mPlayAllSongListArrayList = MusicDBService.getInstance().queryAllSongsList(mContext);

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

        mMusicList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mPlayAllSongListArrayList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", mPlayAllSongListArrayList.get(i).get("musicid"));
            map.put("title", mPlayAllSongListArrayList.get(i).get("musictitle"));
            map.put("artist", mPlayAllSongListArrayList.get(i).get("musicartist"));
            map.put("duration", mPlayAllSongListArrayList.get(i).get("musicduration"));
            map.put("size", mPlayAllSongListArrayList.get(i).get("musicsize"));
            map.put("url", mPlayAllSongListArrayList.get(i).get("musicurl"));
            map.put("quality", mPlayAllSongListArrayList.get(i).get("musicchannel"));
            map.put("channel", mPlayAllSongListArrayList.get(i).get("musicquality"));
            mMusicList.add(map);
        }
    }

    private void setListViewAdapter() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mPlayAllSongListArrayList.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mPlayAllSongListArrayList.get(i).get("musictitle"));
            map.put("artist", mPlayAllSongListArrayList.get(i).get("musicartist"));
            map.put("duration", getTime(mPlayAllSongListArrayList.get(i).get("musicduration")));
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
                mMusicControlService.mMusicList = mMusicList;
                mMusicControlService.mSongNum = position;
                mMusicControlService.play();
                Intent intent = new Intent(mContext, NowPlayingActivity.class);
                intent.putExtra("starOrPause", 2);
                intent.putExtra("CurrentPlaylistName", "allSong");
                startActivity(intent);
            }
        });

        mAllSongsLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    public void showPopupMenu(View view) {
        mMenuBtn.setText("Select");
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.allsongs_activity_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = item.getTitle().toString();
                if ("Details".equals(title)) {
                    Intent goDetailsIntent = new Intent();
                    String musicID = mPlayAllSongListArrayList.get(mSelectedPosition).get("musicid");
                    goDetailsIntent.putExtra("musicID", musicID);
                    goDetailsIntent.setClass(mContext, MusicDetailsActivity.class);
                    startActivity(goDetailsIntent);
                }
                if ("play".equals(title)) {
                    mMusicControlService.mMusicList = mMusicList;
                    mMusicControlService.mSongNum = mSelectedPosition;
                    mMusicControlService.play();
                    Intent intent = new Intent(mContext, NowPlayingActivity.class);
                    intent.putExtra("starOrPause", 2);
                    intent.putExtra("CurrentPlaylistName", "allSong");
                    startActivity(intent);
                }
                if ("Add to playlist".equals(title)){
                    Intent intent=new Intent(mContext,MusicAddToPlaylistsActivity.class);
                    String musicID=(String)mMusicList.get(mSelectedPosition).get("id");
                    intent.putExtra("musicID",musicID);
                    startActivity(intent);
                }
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
