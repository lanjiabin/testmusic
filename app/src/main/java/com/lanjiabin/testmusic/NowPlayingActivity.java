package com.lanjiabin.testmusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class NowPlayingActivity extends Activity {
    private Context mContext;
    private Handler mHandler;
    private int mMessageToUpdateMusicInfo = 0x101;
    private int mStarOrPause = 1;

    private TextView mSongNameTV, mSongTimeTV;
    private ImageView mSongImageV;
    private Button mMenuBtn;
    private SeekBar mSongSeekBar;
    private Button mLoopBtn, mLastBtn, mPlayBtn, mNextBtn, mOrderBtn;
    private MusicControlService mMusicControlService;

    private ServiceConnection mMusicServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        musicThread();
        onClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(NowPlayingActivity.this,Music2BrowserActivity.class);
            startActivity(intent);
            return true;
        }
        if (keyCode==KeyEvent.KEYCODE_MENU){
            showPopupMenu(mMenuBtn);
            return true;
        }
        if (keyCode==KeyEvent.KEYCODE_ENTER){
            try {
                if (mStarOrPause == 1) {
                    mMusicControlService.play();
                    mStarOrPause++;
                } else {
                    if (!mMusicControlService.mPlayer.isPlaying()) {
                        mMusicControlService.goPlay();
                    } else if (mMusicControlService.mPlayer.isPlaying()) {
                        mMusicControlService.pause();
                    }
                }
            } catch (Exception e) {
            }
            return true;
        }
        return false;
    }

    public void initView() {
        Log.v("showLog", "ActivityOnCreate");
        setContentView(R.layout.activity_now_playing);
        mContext = getApplicationContext();
        mSongNameTV = findViewById(R.id.songNameTV);
        mSongTimeTV = findViewById(R.id.songTimeTV);
        mSongImageV = findViewById(R.id.songImageV);
        mSongSeekBar = findViewById(R.id.songSeekBar);
        mLoopBtn = findViewById(R.id.loopBtn);
        mLastBtn = findViewById(R.id.lastBtn);
        mPlayBtn = findViewById(R.id.playBtn);
        mNextBtn = findViewById(R.id.nextBtn);
        mOrderBtn = findViewById(R.id.orderBtn);
        mMenuBtn=findViewById(R.id.menuBtn);

        mMusicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicControlService = ((MusicBinder) service).getService();
                seekBarThread();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent musicServiceIntent = new Intent(this, MusicControlService.class);
        bindService(musicServiceIntent, mMusicServiceConnection, BIND_AUTO_CREATE);
    }

    private String setPlayInfo(int position, int max) {
        int pMinutes = 0;
        while (position >= 60) {
            pMinutes++;
            position -= 60;
        }
        String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":"
                + (position < 10 ? "0" + position : position);

        int mMinutes = 0;
        while (max >= 60) {
            mMinutes++;
            max -= 60;
        }
        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
                + (max < 10 ? "0" + max : max);

        return now + " / " + all;
    }

    @SuppressLint("HandlerLeak")
    public void musicThread() {
        Thread t = new Thread();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int mMax = mMusicControlService.mPlayer.getDuration();
                if (msg.what == mMessageToUpdateMusicInfo) {
                    try {
                        mSongSeekBar.setProgress(msg.arg1);
                        mSongTimeTV.setText(setPlayInfo(msg.arg2 / 1000, mMax / 1000));
                        mSongNameTV.setText(mMusicControlService.mSongName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mSongSeekBar.setProgress(0);
                }
            }
        };
        t.start();

    }

    public void seekBarThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runThread();
            }
        }).start();
    }

    public void runThread() {
        int position, mMax, sMax;
        while (!Thread.currentThread().isInterrupted()) {
            if (mMusicControlService.mPlayer != null && mMusicControlService.mPlayer.isPlaying()) {
                position = mMusicControlService.getCurrentProgress();
                mMax = mMusicControlService.mPlayer.getDuration();
                sMax = mSongSeekBar.getMax();
                Message m = mHandler.obtainMessage();
                m.arg1 = position * sMax / mMax;
                m.arg2 = position;
                m.what = mMessageToUpdateMusicInfo;
                mHandler.sendMessage(m);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick() {
        mLastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMusicControlService.last();
                } catch (Exception e) {
                }
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mStarOrPause == 1) {
                        mMusicControlService.play();
                        mStarOrPause++;
                    } else {
                        if (!mMusicControlService.mPlayer.isPlaying()) {
                            mMusicControlService.goPlay();
                        } else if (mMusicControlService.mPlayer.isPlaying()) {
                            mMusicControlService.pause();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMusicControlService.next();
                } catch (Exception e) {
                }
            }
        });

        mSongSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar mSeekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar mSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar mSeekBar) {
                int progress = mSeekBar.getProgress();
                int musicMax = mMusicControlService.mPlayer.getDuration();
                int seekBarMax = mSeekBar.getMax();
                mMusicControlService.mPlayer
                        .seekTo(musicMax * progress / seekBarMax);
            }
        });

        mLoopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlService.mPlayer.isLooping()){
                    mMusicControlService.mPlayer.setLooping(false);
                    mLoopBtn.setText("loop");
                }else {
                    mMusicControlService.mPlayer.setLooping(true);
                    mLoopBtn.setText("Loop One");
                }


            }
        });

        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(mMenuBtn);
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
        Log.v("showLog", "ActivityOnDestroy");
    }
}
