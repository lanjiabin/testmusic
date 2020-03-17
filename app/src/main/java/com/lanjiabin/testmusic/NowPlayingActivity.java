package com.lanjiabin.testmusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class NowPlayingActivity extends Activity{
    private MusicService mMusicService;
    private Context mContext;
    private Handler mHandler;
    private int mMessageToUpdateMusicInfo = 0x101;
    private int mStarOrPause = 1;

    private TextView mSongNameTV, mSongTimeTV;
    private ImageView mSongImageV;
    private SeekBar mSongSeekBar;
    private Button mLoopBtn, mLastBtn, mPlayBtn, mNextBtn, mOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        musicThread();
        seekBarThread();
        onClick();
    }

    public void initView() {
        setContentView(R.layout.activity_now_playing);
        mContext = getApplicationContext();
        mMusicService = new MusicService(mContext);
        mSongNameTV = findViewById(R.id.songNameTV);
        mSongTimeTV = findViewById(R.id.songTimeTV);
        mSongImageV = findViewById(R.id.songImageV);
        mSongSeekBar = findViewById(R.id.songSeekBar);
        mLoopBtn = findViewById(R.id.loopBtn);
        mLastBtn = findViewById(R.id.lastBtn);
        mPlayBtn = findViewById(R.id.playBtn);
        mNextBtn = findViewById(R.id.nextBtn);
        mOrderBtn = findViewById(R.id.orderBtn);
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
                int mMax = mMusicService.mPlayer.getDuration();
                if (msg.what == mMessageToUpdateMusicInfo) {
                    try {
                        mSongSeekBar.setProgress(msg.arg1);
                        mSongTimeTV.setText(setPlayInfo(msg.arg2 / 1000, mMax / 1000));
                        mSongNameTV.setText(mMusicService.mSongName);
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
            if (mMusicService.mPlayer != null && mMusicService.mPlayer.isPlaying()) {
                position = mMusicService.getCurrentProgress();
                mMax = mMusicService.mPlayer.getDuration();
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
                    mMusicService.last();
                } catch (Exception e) {
                }
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mStarOrPause == 1) {
                        mMusicService.play();
                        mStarOrPause++;
                    } else {
                        if (!mMusicService.mPlayer.isPlaying()) {
                            mMusicService.goPlay();
                        } else if (mMusicService.mPlayer.isPlaying()) {
                            mMusicService.pause();
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
                    mMusicService.next();
                } catch (Exception e) {
                }
            }
        });

        mSongSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar mSeekBar, int i, boolean b) {}
            @Override
            public void onStartTrackingTouch(SeekBar mSeekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar mSeekBar) {
                int progress = mSeekBar.getProgress();
                int musicMax = mMusicService.mPlayer.getDuration();
                int seekBarMax = mSeekBar.getMax();
                mMusicService.mPlayer
                        .seekTo(musicMax * progress / seekBarMax);
            }
        });
    }
}
