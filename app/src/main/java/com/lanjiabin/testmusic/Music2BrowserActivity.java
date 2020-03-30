package com.lanjiabin.testmusic;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Music2BrowserActivity extends Activity {
    private Button mAllSongsBtn, mNowPlayingBtn, mPlayListsBtn, mAllVideosBtn;
    private ListView mListView;
    private Context mContext;
    private String[] mPermissions;
    private int mSelectPosition;

    private MusicControlService mMusicControlService;
    private ServiceConnection mMusicServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        requestPermission();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                switch (mSelectPosition + 1) {
                    case 1: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, NowPlayingActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    case 2: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AllSongsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    case 3: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AllVideosActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    case 4: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, MusicPlaylistsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    default:
                        break;
                }
            }
        }
        return true;
    }

    public void initView() {
        mPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        mContext = Music2BrowserActivity.this;
        setContentView(R.layout.activity_music2_browser);
        mAllSongsBtn = findViewById(R.id.allSongsBtn);
        mNowPlayingBtn = findViewById(R.id.nowPlayingBtn);
        mPlayListsBtn = findViewById(R.id.playlistsBtn);
        mAllVideosBtn = findViewById(R.id.allVideosBtn);
        mListView = findViewById(R.id.mainList);
        setListViewAdapter();
    }

    public void setListViewAdapter() {
        String title[] = {"Now playing", "All songs", "All videos", "Music playlists"};
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < title.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", title[i]);
            map.put("titleID", (i + 1) + "");
            list.add(map);
        }

        SimpleAdapter adapter1 = new SimpleAdapter(
                mContext,
                list,
                R.layout.music_browser_item,
                new String[]{"titleID", "title"},
                new int[]{R.id.titleTV, R.id.titleBtn});
        mListView.setAdapter(adapter1);
    }

    public void onClick() {

        mMusicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicControlService = ((MusicBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent musicServiceIntent = new Intent(mContext, MusicControlService.class);
        bindService(musicServiceIntent, mMusicServiceConnection, BIND_AUTO_CREATE);

        mAllSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, AllSongsActivity.class);
                startActivity(intent);
            }
        });

        mNowPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, NowPlayingActivity.class);
                startActivity(intent);
            }
        });

        mPlayListsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, MusicPlaylistsActivity.class);
                startActivity(intent);
            }
        });

        mAllVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, AllVideosActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mSelectPosition + 1) {
                    case 1: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, NowPlayingActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AllSongsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 3: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AllVideosActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 4: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, MusicPlaylistsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < mPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, mPermissions, 321);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        boolean b = shouldShowRequestPermissionRationale(permissions[i]);
                        if (b) {
                        } else {
                        }
                    } else {
                        onClick();
                    }
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
    }
}
