package com.lanjiabin.testmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlaylistsActivity extends Activity {
    private ListView mPlaylistsLV;
    private List<String> mPlayList;
    private Button mAddViewBtn, mRemoveViewBtn;
    private View mAlterDialogView;
    private EditText mPlaylistEdit;
    private Context mContext;
    private ArrayList<HashMap<String, String>> playListArrayList;

    private int ItemSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        OnClick();
    }

    public void initView() {
        mContext = getApplicationContext();
        playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
        setContentView(R.layout.activity_music_playlists);
        mPlaylistsLV = findViewById(R.id.playlistsLV);
        mAddViewBtn = findViewById(R.id.addViewBtn);
        mRemoveViewBtn = findViewById(R.id.removeViewBtn);
        mAlterDialogView = View.inflate(this, R.layout.alterdialog_playlists_name, null);
        mPlaylistEdit = mAlterDialogView.findViewById(R.id.playlistEdit);

        mPlayList = new ArrayList<String>();

        if (playListArrayList != null) {
            setListViewAdapter();
        }

    }

    public void setListViewAdapter() {
        String[] musicPlayListName = new String[playListArrayList.size()];
        int j = 0;
        for (int i = 0; i < playListArrayList.size(); i++) {
            String playlistName = playListArrayList.get(i).get("playlistname");
            musicPlayListName[j++] = playlistName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                musicPlayListName);
        mPlaylistsLV.setAdapter(adapter);
    }

    public void OnClick() {
        mAddViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterDialogGetName();
            }
        });

        mRemoveViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlaylistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MusicPlaylistsActivity.this, "" + position, Toast.LENGTH_LONG).show();
            }
        });

        mPlaylistsLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MusicPlaylistsActivity.this, "选择了" + position, Toast.LENGTH_LONG).show();
                ItemSelectedPosition = position;
                Log.v("showPosition", "ItemSelectedPosition==" + ItemSelectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showAlterDialogGetName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请输入歌曲列表名称");
        builder.setView(mAlterDialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicDBService.getInstance().queryAllPlaylists(mContext);
                MusicDBService.getInstance().insertForPlaylistName(mContext,mPlaylistEdit.getText().toString(),"now","now");
                playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
                setListViewAdapter();
                mPlaylistEdit.setText("");
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mAlterDialogView.getParent() != null) {
                        ((ViewGroup) mAlterDialogView.getParent()).removeView(mAlterDialogView);
                    }
                }
            });
        }
        builder.create().show();
    }

}
