package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlaylistsTreeActivity extends Activity {
    private ListView mPlaylistsTreeLV;
    private Button mAddSongBtn, mRemoveSongBtn, mMenuBtn;
    private TextView toolbarTitleTV;
    private Context mContext;
    private ArrayList<HashMap<String, String>> playTreeListArrayList;
    private String mPlayListName;

    private MusicControlService mMusicControlService;
    private ServiceConnection mMusicServiceConnection;
    private int mSelectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        OnClick();
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
        setContentView(R.layout.activity_music_playlists_tree);
        mPlaylistsTreeLV = findViewById(R.id.playlistsTreeLV);
        mAddSongBtn = findViewById(R.id.addSongBtn);
        mRemoveSongBtn = findViewById(R.id.removeViewBtn);
        mMenuBtn = findViewById(R.id.menuBtn);
        toolbarTitleTV=findViewById(R.id.toolbarTitle);
        Intent intent = getIntent();
        mPlayListName = intent.getStringExtra("playListName");
        toolbarTitleTV.setText(mPlayListName);
        playTreeListArrayList = MusicDBService.getInstance().queryAllSongsListForListName(mContext, mPlayListName);
        Log.v("TreeActivityName", "mPlayListName=" + mPlayListName);
        Log.v("TreeActivityName", "playTreeListArrayList=" + playTreeListArrayList.toString());
        if (!playTreeListArrayList.isEmpty()) {
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

    public void showPopupMenu(View view) {
        mMenuBtn.setText("Select");
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.playlist_tree_activity_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = item.getTitle().toString();
                if ("play".equals(title)) {
                    playMusic();
                }
                if ("Add".equals(title)) {
                    Intent intent = new Intent(mContext, AllSongsNoActionActivity.class);
                    intent.putExtra("playListName", mPlayListName);
                    intent.putExtra("starOrPause", 2);
                    startActivity(intent);
                }
                if ("Remove".equals(title)) {
                    removeOneSongsForPlayList();
                }
                if ("Details".equals(title)) {
                    Intent goDetailsIntent = new Intent();
                    String musicID = playTreeListArrayList.get(mSelectPosition).get("musicid");
                    goDetailsIntent.putExtra("musicID", musicID);
                    goDetailsIntent.setClass(mContext, MusicDetailsActivity.class);
                    startActivity(goDetailsIntent);
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


    public void setListViewAdapter() {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < playTreeListArrayList.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", playTreeListArrayList.get(i).get("musictitle"));
            map.put("artist", playTreeListArrayList.get(i).get("musicartist"));
            map.put("duration", getTime(playTreeListArrayList.get(i).get("musicduration")));
            list.add(map);
        }

        SimpleAdapter adapter1 = new SimpleAdapter(
                mContext,
                list,
                R.layout.music_allsongs_item,
                new String[]{"title", "duration", "artist"},
                new int[]{R.id.title, R.id.duration, R.id.artist});
        mPlaylistsTreeLV.setAdapter(adapter1);
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

        mAddSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllSongsNoActionActivity.class);
                intent.putExtra("playListName", mPlayListName);
                intent.putExtra("starOrPause", 2);
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

        mPlaylistsTreeLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPlaylistsTreeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectPosition = position;
                playMusic();
            }
        });
    }

    public void removeOneSongsForPlayList() {
        String musicId = playTreeListArrayList.get(mSelectPosition).get("musicid");
        MusicDBService.getInstance().updateForMusicId(mContext, "", musicId);
        playTreeListArrayList = MusicDBService.getInstance().queryAllSongsListForListName(mContext, mPlayListName);
        setListViewAdapter();

    }

    public void playMusic() {
        List<Map<String, Object>> myMusicList;
        myMusicList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < playTreeListArrayList.size(); i++) {
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
        mMusicControlService.mMusicList = myMusicList;
        mMusicControlService.mSongNum = mSelectPosition;
        mMusicControlService.play();
        String CurrentPlaylistName = playTreeListArrayList.get(mSelectPosition).get("playlistname");
        Intent intent = new Intent(mContext, NowPlayingActivity.class);
        intent.putExtra("starOrPause", 2);
        intent.putExtra("CurrentPlaylistName", CurrentPlaylistName);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mPlayListName.isEmpty()) {
            playTreeListArrayList = MusicDBService.getInstance().queryAllSongsListForListName(mContext, mPlayListName);
        }
        if (!playTreeListArrayList.isEmpty()) {
            setListViewAdapter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
    }
}
