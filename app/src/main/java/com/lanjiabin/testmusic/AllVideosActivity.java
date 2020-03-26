package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AllVideosActivity extends Activity {

    private Context mContext;
    private List<Map<String, Object>> mVideosList;
    private ListView allVideosLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListViewAdapter();
        OnClick();
    }

    public void initView() {
        setContentView(R.layout.activity_all_videos);
        mContext = getApplicationContext();
        mVideosList = new ArrayList<Map<String, Object>>();
        allVideosLV = findViewById(R.id.allVideosLV);

        List<VideoFileInfo> videoFileInfoList = getVideos();
        for (Iterator iterator = videoFileInfoList.iterator(); iterator.hasNext(); ) {
            VideoFileInfo videoFileInfo = (VideoFileInfo) iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", String.valueOf(videoFileInfo.getId()));
            map.put("path", videoFileInfo.getPath());
            map.put("name", videoFileInfo.getName());
            map.put("resolution", videoFileInfo.getResolution());
            map.put("size", String.valueOf(videoFileInfo.getSize()));
            map.put("date", String.valueOf(videoFileInfo.getDate()));
            map.put("duration", String.valueOf(videoFileInfo.getDuration()));
            mVideosList.add(map);
        }

    }

    private void setListViewAdapter() {
        String[] videosAllList = new String[mVideosList.size()];
        int j = 0;
        for (int i = 0; i < mVideosList.size(); i++) {
            String listName = (String) mVideosList.get(i).get("path");
            String fileName = getFileName(listName);
            videosAllList[j++] = fileName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                videosAllList);
        allVideosLV.setAdapter(adapter);
    }

    public void OnClick() {
        allVideosLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = (String) mVideosList.get(position).get("path");
                Log.v("setOnItemClickListener","path="+path);
                Intent intent = new Intent();
                intent.setClass(mContext, VideosPlayActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    public String getFileName(String fileName) {
        int index = fileName.lastIndexOf("/") + 1;
        return fileName.substring(index, fileName.length());
    }


    /**
     * 获取本机视频列表
     *
     * @return
     */
    public List<VideoFileInfo> getVideos() {
        List<VideoFileInfo> musicFileIfs = new ArrayList<VideoFileInfo>();
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间

                VideoFileInfo video = new VideoFileInfo(id, path, name, resolution, size, date, duration);
                musicFileIfs.add(video);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return musicFileIfs;
    }
}
