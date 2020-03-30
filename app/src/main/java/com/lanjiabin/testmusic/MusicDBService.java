package com.lanjiabin.testmusic;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicDBService {
    private static List<Map<String, Object>> mMusicList;
    private static MusicDBService musicDBService = null;
    private String mSql = "INSERT INTO allsongslist(musicid,musictitle,musicartist,musicduration,musicsize,musicurl,musicchannel,musicquality,playlistcreatetime,playlistupdatetime) VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static String mLocalTime;

    private MusicDBService() {
    }

    public static MusicDBService getInstance() {
        if (musicDBService == null) {
            musicDBService = new MusicDBService();
        }
        mMusicList = new ArrayList<Map<String, Object>>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Log.v("localTime", "df.format(new Date())=" + df.format(new Date()));
        mLocalTime = df.format(new Date());
        return musicDBService;
    }

    public List<Map<String, Object>> getMusicList() {
        return mMusicList;
    }

    public void setMusicList(List<Map<String, Object>> mMusicList) {
        this.mMusicList = mMusicList;
    }

    /**
     * 1.创建一个音乐播放列表
     *
     * @param playListName       列表名字
     * @param playListCreateTime 创建时间
     * @param playListUpdateTime 更新时间
     */
    public void insertForPlaylistName(Context context, String playListName, String playListCreateTime, String playListUpdateTime) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "insert into playlist(playlistname,playlistcreatetime,playlistupdatetime) values(?,?,?)";
        musicDBManager.updateSQLite(sql, new String[]{playListName, playListCreateTime, playListUpdateTime});
    }

    /**
     * 2.删除一个播放列表
     *
     * @param playListName 列表名字
     **/
    public void deleteForPlaylistName(Context context, String playListName) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "delete from playlist where playlistname=?";
        musicDBManager.updateSQLite(sql, new String[]{playListName});
    }

    /**
     * 3.更新一个播放列表,同时也要更新关联的名字
     *
     * @param newPlayListName 最新的名字
     * @param oldPlayListName 旧的名字
     **/
    public void updateForPlaylistName(Context context, String newPlayListName, String oldPlayListName) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
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
    public void updateForMusicId(Context context, String playListName, String musicId) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "UPDATE allsongslist SET playlistname=? WHERE musicid=?";
        musicDBManager.updateSQLite(sql, new String[]{playListName, musicId});
    }

    /**
     * 5.查询所有播放列表
     *
     * @param context 上下文
     **/
    public ArrayList<HashMap<String, String>> queryAllPlaylists(Context context) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "SELECT * FROM playlist";
        ArrayList<HashMap<String, String>> queryResult = musicDBManager.querySQLite(sql, null);
        return queryResult;
    }

    /**
     * 6.插入全部歌曲
     *
     * @param context 上下文
     **/
    public void replaceAllSongsList(Context context,
                                    String musicId,
                                    String musicTitle,
                                    String musicArtist,
                                    String musicDuration,
                                    String musicSize,
                                    String musicUrl,
                                    String musicChannel,
                                    String musicQuality,
                                    String playlistCreateTime,
                                    String playlistUpdateTime) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        if (!mMusicList.isEmpty()) {
            for (int i = 0; i < mMusicList.size(); i++) {
                if (musicId.equals(mMusicList.get(i).get("id"))) {
                    return;
                } else {
                    musicDBManager.updateSQLite(mSql, new String[]{musicId,
                            musicTitle,
                            musicArtist,
                            musicDuration,
                            musicSize,
                            musicUrl,
                            musicChannel,
                            musicQuality,
                            playlistCreateTime,
                            playlistUpdateTime});
                    return;
                }

            }
        }


    }

    /**
     * 7.查询所有歌曲
     *
     * @param context 上下文
     **/
    public ArrayList<HashMap<String, String>> queryAllSongsList(Context context) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "SELECT * FROM allsongslist";
        ArrayList<HashMap<String, String>> queryResult = musicDBManager.querySQLite(sql, null);
        return queryResult;
    }

    /**
     * 8.根据列表名字查询歌曲
     *
     * @param context 上下文
     **/
    public ArrayList<HashMap<String, String>> queryAllSongsListForListName(Context context, String playListName) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "SELECT * FROM allsongslist WHERE playlistname=?";
        ArrayList<HashMap<String, String>> queryResult = musicDBManager.querySQLite(sql, new String[]{playListName});
        return queryResult;
    }

    /**
     * 8.根据id查询歌曲
     *
     * @param context 上下文
     **/
    public ArrayList<HashMap<String, String>> queryMusicInfoByID(Context context, String musicId) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "SELECT * FROM allsongslist WHERE musicid=?";
        ArrayList<HashMap<String, String>> queryResult = musicDBManager.querySQLite(sql, new String[]{musicId});
        return queryResult;
    }

    /**
     * 9.删除一首歌
     *
     * @param url 歌曲文件地址
     **/
    public void deleteSongByUrl(Context context, String url) {
        MusicDBManager musicDBManager = new MusicDBManager(context);
        MusicDBHelper helper = new MusicDBHelper(context);
        String sql = "delete from allsongslist where musicurl=?";
        musicDBManager.updateSQLite(sql, new String[]{url});
    }
}
