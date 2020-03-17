package com.lanjiabin.testmusic;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MusicService {
    public List<String> mMusicList;// 存放找到的所有mp3的绝对路径。
    public MediaPlayer mPlayer; // 定义多媒体对象
    public int mSongNum; // 当前播放的歌曲在List中的下标,flag为标志
    public String mSongName; // 当前播放的歌曲名
    Context mContext = null;

    //初始化，获得本地歌曲名和地址
    public MusicService(Context context) {
        super();
        mContext = context;
        mPlayer = new MediaPlayer();
        mMusicList = new ArrayList<String>();
        List<MusicFileInfo> musicFileInfoList = getMusicFileInfo();
        for (Iterator iterator = musicFileInfoList.iterator(); iterator.hasNext(); ) {
            MusicFileInfo musicFileInfo = (MusicFileInfo) iterator.next();
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("title", musicFileInfo.getTitle());
//            map.put("Artist", musicFileInfo.getArtist());
//            map.put("duration", String.valueOf(musicFileInfo.getDuration()));
//            map.put("size", String.valueOf(musicFileInfo.getSize()));
//            map.put("url", musicFileInfo.getUrl());
            mMusicList.add(musicFileInfo.getUrl());
        }
    }

    //获取所有mp3文件信息
    public List<MusicFileInfo> getMusicFileInfo() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<MusicFileInfo> musicFileInfos = new ArrayList<MusicFileInfo>();
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicFileInfo musicFileInfo = new MusicFileInfo();
            cursor.moveToNext();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA)); //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            String quality = MediaStore.EXTRA_VIDEO_QUALITY; //音乐质量
            String channel = MediaStore.EXTRA_MEDIA_RADIO_CHANNEL; //音乐质量


            if (isMusic != 0) {     //只把音乐添加到集合当中
                musicFileInfo.setId(id);
                musicFileInfo.setTitle(title);
                musicFileInfo.setArtist(artist);
                musicFileInfo.setDuration(duration);
                musicFileInfo.setSize(size);
                musicFileInfo.setUrl(url);
                musicFileInfo.setQuality(quality);
                musicFileInfo.setChannel(channel);
                musicFileInfos.add(musicFileInfo);
            }
        }
        return musicFileInfos;
    }
}
