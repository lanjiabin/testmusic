package com.lanjiabin.testmusic;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicDBService {
    private static MusicDBService musicDBService = null;

    private MusicDBService() {
    }

    public static MusicDBService getInstance() {
        if (musicDBService == null) {
            musicDBService = new MusicDBService();
        }
        return musicDBService;
    }

    /**
     * 1.创建一个音乐播放列表
     *
     * @param playListName       列表名字
     * @param playListCreateTime 创建时间
     * @param playListUpdateTime 更新时间
     */
    public void insertForPlaylistName(Context context,String playListName, String playListCreateTime, String playListUpdateTime) {
        MusicDBManager musicDBManager=new MusicDBManager(context);
        MusicDBHelper helper=new MusicDBHelper(context);
        String sql = "insert into playlist(playlistname,playlistcreatetime,playlistupdatetime) values(?,?,?)";
        musicDBManager.updateSQLite(sql, new String[]{playListName, playListCreateTime, playListUpdateTime});
    }

    /**
     * 2.删除一个播放列表
     *
     * @param playListName 列表名字
     **/
    public void deleteForPlaylistName(Context context,String playListName) {
        MusicDBManager musicDBManager=new MusicDBManager(context);
        MusicDBHelper helper=new MusicDBHelper(context);
        String sql = "delete from playlist where playlistname=?";
        musicDBManager.updateSQLite(sql, new String[]{playListName});
    }

    /**
     * 3.更新一个播放列表,同时也要更新关联的名字
     *
     * @param newPlayListName 最新的名字
     * @param oldPlayListName 旧的名字
     **/
    public void updateForPlaylistName(Context context,String newPlayListName, String oldPlayListName) {
        MusicDBManager musicDBManager=new MusicDBManager(context);
        MusicDBHelper helper=new MusicDBHelper(context);
        String sql = "UPDATE playlist SET playlistname=? WHERE playlistname=?";
        String sql2 = "UPDATE allsongslist SET playlistname=? WHERE playlistname=?";
        musicDBManager.updateSQLite(sql, new String[]{newPlayListName, oldPlayListName});
        musicDBManager.updateSQLite(sql2, new String[]{newPlayListName, oldPlayListName});
    }

    /**
     * 4.把一个音乐加入到对应的播放列表中 or 5.把一个音乐移除播放列表
     *
     * @param playListName 播放列表名字
     * @param musicId      音乐的ID
     **/
    public void updateForMusicId(Context context,String playListName, String musicId) {
        MusicDBManager musicDBManager=new MusicDBManager(context);
        MusicDBHelper helper=new MusicDBHelper(context);
        String sql = "UPDATE allsongslist SET playlistname=? WHERE musicid=?";
        musicDBManager.updateSQLite(sql, new String[]{playListName, musicId});
    }

    public ArrayList<HashMap<String, String>> queryAllPlaylists(Context context){
        MusicDBManager musicDBManager=new MusicDBManager(context);
        MusicDBHelper helper=new MusicDBHelper(context);
        String sql="SELECT * FROM playlist";
        ArrayList<HashMap<String, String>> queryResult= musicDBManager.querySQLite(sql, null);
        return queryResult;
    }

}
