package com.lanjiabin.testmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicAddToPlaylistsActivity extends Activity {
    private Context mContext;
    private ListView mPlaylistsLV;
    private ArrayList<HashMap<String, String>> playListArrayList;
    private View mAlterDialogView;
    private EditText mPlaylistEdit;
    private Button mCreateBtn;
    private String mMusicID;
    private int mSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       initView();
       OnClick();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
            if (mCreateBtn.isSelected()){
                showAlterDialogGetName();
                return true;
            }else {
                String playlistName=playListArrayList.get(mSelectPosition).get("playlistname");
                MusicDBService.getInstance().updateForMusicId(mContext,playlistName,mMusicID);
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void initView(){
        setContentView(R.layout.activity_music_add_to_playlists);
        mContext = getApplicationContext();
        mPlaylistsLV=findViewById(R.id.playlistsLV);
        playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
        mAlterDialogView = View.inflate(this, R.layout.alterdialog_playlists_name, null);
        mPlaylistEdit = mAlterDialogView.findViewById(R.id.playlistEdit);
        mCreateBtn=findViewById(R.id.createBtn);
        Intent intent=getIntent();
        mMusicID=intent.getStringExtra("musicID");
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.music_playlist_item,
                R.id.playlistsItemTV,
                musicPlayListName);
        mPlaylistsLV.setAdapter(adapter);
    }

    public void OnClick(){
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterDialogGetName();
            }
        });

        mPlaylistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlistName=playListArrayList.get(position).get("playlistname");
                MusicDBService.getInstance().updateForMusicId(mContext,playlistName,mMusicID);
                Toast.makeText(mContext,"成功添加到"+playlistName+"列表",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        mPlaylistsLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectPosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showAlterDialogGetName() {

        String titleText = "提示";
        String tipMessageText = "输入新的列表名字";
        String okText = "确定";
        String cancelText = "取消";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleText);
        builder.setMessage(tipMessageText);
        builder.setView(mAlterDialogView);
        builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String editText = mPlaylistEdit.getText().toString().trim();
                if (editText.isEmpty()) {
                    Toast.makeText(mContext, "请输入正确的文字", Toast.LENGTH_LONG).show();
                    mPlaylistEdit.setText("");
                    return;
                }
                for (int i = 0; i < playListArrayList.size(); i++) {
                    String name = playListArrayList.get(i).get("playlistname");
                    if (editText.equals(name)) {
                        Toast.makeText(mContext, "该列表已经存在，请重新输入", Toast.LENGTH_LONG).show();
                        mPlaylistEdit.setText("");
                        return;
                    }
                }
                MusicDBService.getInstance().insertForPlaylistName(mContext, editText, "now", "now");
                playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
                setListViewAdapter();
                mPlaylistEdit.setText("");
            }
        });

        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
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
