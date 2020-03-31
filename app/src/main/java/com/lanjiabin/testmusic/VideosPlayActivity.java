package com.lanjiabin.testmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideosPlayActivity extends Activity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_play);
        videoView=findViewById(R.id.videoView);
        Intent intent=getIntent();
        String path=intent.getStringExtra("path");
        videoView.setVideoPath(path);
        MediaController mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
        if (event.getAction()!=KeyEvent.ACTION_DOWN){
            if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                Intent intent=new Intent();
                intent.setClass(this,AllVideosActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }
}
