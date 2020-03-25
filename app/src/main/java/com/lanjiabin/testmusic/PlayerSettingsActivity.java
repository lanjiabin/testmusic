package com.lanjiabin.testmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PlayerSettingsActivity extends Activity {
    private Button mShuffleBtn, mRepeatBtn, mOKBtn, mDoneBtn;
    private ImageView mShuffleLastIV, mShuffleNextIV, mRepeatLastIV, mRepeatNextIV;
    private MusicControlService mMusicControlService;
    private ServiceConnection mMusicServiceConnection;

    private String mOneText = "One";
    private String mAllText = "All";
    private String mDisabledText = "Disabled";
    private String mEnabledText = "Enabled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        OnClick();
    }

    public void initView() {
        setContentView(R.layout.activity_player_settings);
        mShuffleBtn = findViewById(R.id.shuffleBtn);
        mRepeatBtn = findViewById(R.id.repeatBtn);
        mShuffleLastIV = findViewById(R.id.shuffleLastIV);
        mShuffleNextIV = findViewById(R.id.shuffleNextIV);
        mRepeatLastIV = findViewById(R.id.repeatLastIV);
        mRepeatNextIV = findViewById(R.id.repeatNextIV);
        mOKBtn = findViewById(R.id.OKBtn);
        mDoneBtn = findViewById(R.id.doneBtn);

        mShuffleBtn.requestFocus();
        mShuffleBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        mShuffleBtn.setTextColor(Color.BLUE);
        mShuffleBtn.setBackgroundColor(Color.GRAY);
        mShuffleLastIV.setVisibility(View.VISIBLE);
        mShuffleNextIV.setVisibility(View.VISIBLE);

        mMusicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicControlService = ((MusicBinder) service).getService();

                if (mMusicControlService.mIsShuffle) {
                    mShuffleBtn.setText(mEnabledText);
                } else {
                    mShuffleBtn.setText(mDisabledText);
                }

                if (mMusicControlService.mPlayer.isLooping()) {
                    mRepeatBtn.setText(mOneText);
                } else {
                    mRepeatBtn.setText(mAllText);
                }
                if (!mMusicControlService.mIsListLoop) {
                    mRepeatBtn.setText(mDisabledText);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent musicServiceIntent = new Intent(this, MusicControlService.class);
        bindService(musicServiceIntent, mMusicServiceConnection, BIND_AUTO_CREATE);
    }

    public void OnClick() {
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterDialogGetName();
            }
        });
    }

    public void showAlterDialogGetName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("保存");
        builder.setMessage("是否保存？");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textRepeatBtn = mRepeatBtn.getText().toString();
                String textShuffleBtn = mShuffleBtn.getText().toString();

                if (textRepeatBtn.equals(mOneText)) {
                    mMusicControlService.mPlayer.setLooping(true);
                }
                if (textRepeatBtn.equals(mAllText)) {
                    mMusicControlService.mPlayer.setLooping(false);
                }
                if (textRepeatBtn.equals(mDisabledText)) {
                    mMusicControlService.mIsListLoop = false;
                }
                if (textShuffleBtn.equals(mEnabledText)) {
                    mMusicControlService.setShuffle(true);
                }
                if (textShuffleBtn.equals(mDisabledText)) {
                    mMusicControlService.setShuffle(false);
                }
                Toast.makeText(PlayerSettingsActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                Log.v("isFocused", "mRepeatBtn.isFocused()=" + mRepeatBtn.isFocused());
                Log.v("isFocused", "mShuffleBtn.isFocused()=" + mRepeatBtn.isFocused());
                if (mRepeatBtn.isFocused()) {
                    mShuffleBtn.requestFocus();
                    mShuffleBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                    mShuffleBtn.setTextColor(Color.BLUE);
                    mShuffleBtn.setBackgroundColor(Color.GRAY);
                    mShuffleLastIV.setVisibility(View.VISIBLE);
                    mShuffleNextIV.setVisibility(View.VISIBLE);

                    mRepeatBtn.setTextColor(Color.BLACK);
                    mRepeatBtn.setBackgroundColor(Color.TRANSPARENT);
                    mRepeatLastIV.setVisibility(View.GONE);
                    mRepeatNextIV.setVisibility(View.GONE);
                    return true;
                }
                if (mShuffleBtn.isFocused()) {
                    mRepeatBtn.requestFocus();
                    mRepeatBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                    mRepeatBtn.setTextColor(Color.BLUE);
                    mRepeatBtn.setBackgroundColor(Color.GRAY);
                    mRepeatLastIV.setVisibility(View.VISIBLE);
                    mRepeatNextIV.setVisibility(View.VISIBLE);

                    mShuffleBtn.setTextColor(Color.BLACK);
                    mShuffleBtn.setBackgroundColor(Color.TRANSPARENT);
                    mShuffleLastIV.setVisibility(View.GONE);
                    mShuffleNextIV.setVisibility(View.GONE);
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mDoneBtn.setText("Done");
                String textRepeatBtn = mRepeatBtn.getText().toString();
                String textShuffleBtn = mShuffleBtn.getText().toString();

                if (mRepeatBtn.isFocused()) {
                    if (mAllText.equals(textRepeatBtn)) {
                        mRepeatBtn.setText(mOneText);
                    }
                    if (mOneText.equals(textRepeatBtn)) {
                        mRepeatBtn.setText(mDisabledText);
                    }
                    if (mDisabledText.equals(textRepeatBtn)) {
                        mRepeatBtn.setText(mAllText);
                    }
                    return true;
                }

                if (mShuffleBtn.isFocused()) {
                    if (mEnabledText.equals(textShuffleBtn)) {
                        mShuffleBtn.setText(mDisabledText);
                    }
                    if (mDisabledText.equals(textShuffleBtn)) {
                        mShuffleBtn.setText(mEnabledText);
                    }
                    return true;
                }

                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                showAlterDialogGetName();
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(this, "未做任何修改", Toast.LENGTH_LONG).show();
                finish();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMusicServiceConnection);
        Log.v("showLog", "ActivityOnDestroy");
    }
}
