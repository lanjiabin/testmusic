package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AllVideosActivity extends Activity {

    private Context mContext;
    private List<Map<String, Object>> mVideosList;
    private ListView allVideosLV;
    private Button mMenuBtn;
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListViewAdapter();
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
        setContentView(R.layout.activity_all_videos);
        mContext = getApplicationContext();
        mVideosList = new ArrayList<Map<String, Object>>();
        allVideosLV = findViewById(R.id.allVideosLV);
        mMenuBtn = findViewById(R.id.allMenuBtn);
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
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mVideosList.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", (String) mVideosList.get(i).get("name"));
            map.put("duration", getTime((String) mVideosList.get(i).get("duration")));
            list.add(map);
        }

        SimpleAdapter adapter1 = new SimpleAdapter(
                mContext,
                list,
                R.layout.videos_item,
                new String[]{"title", "duration"},
                new int[]{R.id.title, R.id.duration});
        allVideosLV.setAdapter(adapter1);
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
        allVideosLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = (String) mVideosList.get(position).get("path");
                Log.v("setOnItemClickListener", "path=" + path);
                Intent intent = new Intent();
                intent.setClass(mContext, VideosPlayActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });

        allVideosLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public String getFileName(String fileName) {
        int index = fileName.lastIndexOf("/") + 1;
        return fileName.substring(index, fileName.length());
    }

    public void showPopupMenu(View view) {
        mMenuBtn.setText("Select");
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.allvideos_activity_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = item.getTitle().toString();
                if ("play".equals(title)) {
                    String path = (String) mVideosList.get(mSelectedPosition).get("path");
                    Log.v("setOnItemClickListener", "path=" + path);
                    Intent intent = new Intent();
                    intent.setClass(mContext, VideosPlayActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
                if ("Details".equals(title)) {
                    if (mVideosList != null) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, VideoDetailsActivity.class);
                        intent.putExtra("path", (String) mVideosList.get(mSelectedPosition).get("path"));
                        intent.putExtra("name", (String) mVideosList.get(mSelectedPosition).get("name"));
                        intent.putExtra("resolution", (String) mVideosList.get(mSelectedPosition).get("resolution"));
                        intent.putExtra("size", (String) mVideosList.get(mSelectedPosition).get("size"));
                        intent.putExtra("date", (String) mVideosList.get(mSelectedPosition).get("date"));
                        intent.putExtra("duration", (String) mVideosList.get(mSelectedPosition).get("duration"));
                        startActivity(intent);
                    }
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
