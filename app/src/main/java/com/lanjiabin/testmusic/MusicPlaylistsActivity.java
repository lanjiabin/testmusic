package com.lanjiabin.testmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicPlaylistsActivity extends Activity {
    private ListView mPlaylistsLV;
    private List<String> mPlayList;
    private Button mAddViewBtn, mRemoveViewBtn, mUpdateViewBtn, mMenuBtn;
    private View mAlterDialogView;
    private EditText mPlaylistEdit;
    private Context mContext;
    private ArrayList<HashMap<String, String>> playListArrayList;

    private int itemSelectedPosition;
    private String mPlayListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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
        mContext = getApplicationContext();
        playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
        setContentView(R.layout.activity_music_playlists);
        mPlaylistsLV = findViewById(R.id.playlistsLV);
        mAddViewBtn = findViewById(R.id.addViewBtn);
        mRemoveViewBtn = findViewById(R.id.removeViewBtn);
        mAlterDialogView = View.inflate(this, R.layout.alterdialog_playlists_name, null);
        mPlaylistEdit = mAlterDialogView.findViewById(R.id.playlistEdit);
        mUpdateViewBtn = findViewById(R.id.updateViewBtn);
        mMenuBtn = findViewById(R.id.menuBtn);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.music_playlist_item,
                R.id.playlistsItemTV,
                musicPlayListName);
        mPlaylistsLV.setAdapter(adapter);
    }

    public void OnClick() {
        mAddViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterDialogGetName("add");
            }
        });

        mRemoveViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListName != null) {
                    playListArrayList.remove(itemSelectedPosition).get(mPlayListName);
                    setListViewAdapter();
                    MusicDBService.getInstance().deleteForPlaylistName(mContext, mPlayListName);
                } else {
                    Toast.makeText(mContext, "请选择有效数据", Toast.LENGTH_LONG).show();
                }

            }
        });

        mUpdateViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListName != null) {
                    showAlterDialogGetName("update");
                } else {
                    Toast.makeText(mContext, "请选择一个选项", Toast.LENGTH_LONG).show();
                }

            }
        });

        mPlaylistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelectedPosition = position;
                TextView tv = parent.getChildAt(position).findViewById(R.id.playlistsItemTV);
                if (playListArrayList != null) {
                    mPlayListName = playListArrayList.get(position).get("playlistname");
                }

                Intent intent = new Intent(mContext, MusicPlaylistsTreeActivity.class);
                intent.putExtra("playListName", mPlayListName);
                startActivity(intent);
            }
        });

        mPlaylistsLV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                itemSelectedPosition = position;
                TextView tv = parent.getChildAt(position).findViewById(R.id.playlistsItemTV);
                if (playListArrayList != null) {
                    mPlayListName = playListArrayList.get(position).get("playlistname");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void showPopupMenu(View view) {
        mMenuBtn.setText("Select");
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.playlist_activity_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title=item.getTitle().toString();
                if ("Open".equals(title)){
                    Intent intent = new Intent(mContext, MusicPlaylistsTreeActivity.class);
                    intent.putExtra("playListName", mPlayListName);
                    startActivity(intent);
                }
                if ("New".equals(title)){
                    showAlterDialogGetName("add");
                }
                if ("Delete".equals(title)){
                    if (mPlayListName != null) {
                        playListArrayList.remove(itemSelectedPosition).get(mPlayListName);
                        setListViewAdapter();
                        MusicDBService.getInstance().deleteForPlaylistName(mContext, mPlayListName);
                    } else {
                        Toast.makeText(mContext, "请选择有效数据", Toast.LENGTH_LONG).show();
                    }
                }
                if ("Rename".equals(title)){
                    if (mPlayListName != null) {
                        showAlterDialogGetName("update");
                    } else {
                        Toast.makeText(mContext, "请选择一个选项", Toast.LENGTH_LONG).show();
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

    public void showAlterDialogGetName(final String function) {

        String titleText = null;
        String tipMessageText = null;
        String okText = null;
        String cancelText = null;

        if (function.equals("update")) {
            titleText = "提示";
            tipMessageText = "请输入歌曲列表名称";
            okText = "确定";
            cancelText = "取消";
        }
        if (function.equals("add")) {
            titleText = "提示";
            tipMessageText = "输入新的列表名字";
            okText = "确定";
            cancelText = "取消";
        }
        if (function.equals("update")) {
            mPlaylistEdit.setText(mPlayListName);
        }

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
                if (function.equals("add")) {
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
                if (function.equals("update")) {
                    MusicDBService.getInstance().updateForPlaylistName(mContext, editText, mPlayListName);
                    playListArrayList = MusicDBService.getInstance().queryAllPlaylists(mContext);
                    setListViewAdapter();
                    mPlaylistEdit.setText("");
                }


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
