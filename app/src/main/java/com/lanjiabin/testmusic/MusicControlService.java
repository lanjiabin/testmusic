package com.lanjiabin.testmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MusicControlService extends Service {
    public List<Map<String, Object>> mMusicList;
    public MediaPlayer mPlayer;
    public int mSongNum;
    public String mSongName;
    Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("showLog","ServiceOnCreate");
        this.mContext=getApplicationContext();
        mPlayer = new MediaPlayer();
        mMusicList = new ArrayList<Map<String, Object>>();
        List<MusicFileInfo> musicFileInfoList = getMusicFileInfo();
        for (Iterator iterator = musicFileInfoList.iterator(); iterator.hasNext(); ) {
            MusicFileInfo musicFileInfo = (MusicFileInfo) iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", musicFileInfo.getTitle());
            map.put("Artist", musicFileInfo.getArtist());
            map.put("duration", String.valueOf(musicFileInfo.getDuration()));
            map.put("size", String.valueOf(musicFileInfo.getSize()));
            map.put("url", musicFileInfo.getUrl());
            mMusicList.add(map);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder(this);
    }

    public List<MusicFileInfo> getMusicFileInfo() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<MusicFileInfo> musicFileIfs = new ArrayList<MusicFileInfo>();
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicFileInfo musicFileInfo = new MusicFileInfo();
            cursor.moveToNext();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            String quality = MediaStore.EXTRA_VIDEO_QUALITY;
            String channel = MediaStore.EXTRA_MEDIA_RADIO_CHANNEL;


            if (isMusic != 0) {
                musicFileInfo.setId(id);
                musicFileInfo.setTitle(title);
                musicFileInfo.setArtist(artist);
                musicFileInfo.setDuration(duration);
                musicFileInfo.setSize(size);
                musicFileInfo.setUrl(url);
                musicFileInfo.setQuality(quality);
                musicFileInfo.setChannel(channel);
                musicFileIfs.add(musicFileInfo);
            }
        }
        return musicFileIfs;
    }

    public void play() {
        try {
            mPlayer.reset();
            String dataSource = (String) mMusicList.get(mSongNum).get("url");
            setPlayName(dataSource);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(dataSource);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    next();
                }
            });

        } catch (Exception e) {
        }
    }

    public void next() {
        mSongNum = mSongNum == mMusicList.size() - 1 ? 0 : mSongNum + 1;
        play();
    }

    public void last() {
        mSongNum = mSongNum == 0 ? mMusicList.size() - 1 : mSongNum - 1;
        play();
    }

    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void goPlay() {
        int position = getCurrentProgress();
        mPlayer.seekTo(position);
        try {
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer.start();
    }

    public int getCurrentProgress() {
        if (mPlayer != null & mPlayer.isPlaying()) {
            return mPlayer.getCurrentPosition();
        } else if (mPlayer != null & (!mPlayer.isPlaying())) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void setPlayName(String dataSource) {
        File file = new File(dataSource);
        String name = file.getName();
        int index = name.lastIndexOf(".");
        mSongName = name.substring(0, index);
    }
}
    class MusicBinder extends Binder{
        MusicControlService mMusicServiceControl;
        public MusicBinder(MusicControlService musicServiceControl) {
            mMusicServiceControl=musicServiceControl;
        }

        public MusicControlService getService(){
            return mMusicServiceControl;
        }
    }
