package com.seika.musicplayer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer player;
    File sdroot = Environment.getExternalStorageDirectory();
    String rpath = sdroot.getPath() + "/music";
    File[] files;
    EditText et_path;
    int index = 0;
    ImageView cover;
    TextView title;
    Button btnPlay;
    Button btnNext;
    Button btnPrev;
    Button btnLoad;
    SeekBar seekBar;
    boolean findMP3 = false;
    boolean findNext = false;
    boolean findPrev = false;
    boolean isPause = true;
    boolean isSeekBarChanging = false;
    int currentPosition;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(clickListener);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(clickListener);
        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(clickListener);

        btnLoad = (Button) findViewById(R.id.btn_load);
        btnLoad.setOnClickListener(clickListener);
        et_path = (EditText) findViewById(R.id.et_path);
        et_path.setText(rpath);

        title = (TextView) findViewById(R.id.tv_title);
        cover = (ImageView) findViewById(R.id.iv_cover);
        cover.setOnTouchListener(touchListener);

        seekBar = (SeekBar) findViewById(R.id.sk_music);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        player = new MediaPlayer();
        player.setOnCompletionListener(comL);
    }

    public void onPause() {
        super.onPause();
        player.release();
    }

    private MediaPlayer.OnCompletionListener comL = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer nouse) {
            try {
                player.pause();
                currentPosition = 0;
                timer.purge();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
            }
            doNext();
        }
    };

    private void doLoad() {
        if (!isPause) {
            doStop();
        }
        onResume();
        findMP3 = false;
        String input_str = et_path.getText().toString();
        File f = new File(input_str);
        String fullname, subname;
        if (f.isDirectory()) {
            files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                fullname = files[i].getName();
                subname = files[i].getName().substring(fullname.lastIndexOf(46) + 1, fullname.length());
                if (subname.equals("mp3")) {
                    index = i;
                    String tmp = files[i].getPath();
                    File mp3File = new File(tmp);
                    if (mp3File.exists()) {
                        findMP3 = true;
                        title.setText(fullname.substring(0, fullname.lastIndexOf('.')));
                        try {
                            player.setDataSource(tmp);
                            player.prepare();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                        }

                        tmp = sdroot.getPath() + "/Pictures/" + fullname;
                        tmp = tmp.substring(0, tmp.lastIndexOf(46)) + ".jpg";
                        File imgFile = new File(tmp);
                        if (imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            cover.setImageBitmap(myBitmap);
                        } else {
                            cover.setImageResource(R.drawable.music);
                        }
                        Toast.makeText(MainActivity.this, "Find " + files.length + " songs.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
        if (!findMP3) {
            Toast.makeText(MainActivity.this, "Not found sound.", Toast.LENGTH_SHORT).show();
            title.setText("No sound.");
            return;
        }
    }

    private void doNext() {
        if (!findMP3) {
            Toast.makeText(MainActivity.this, "Not found sound.", Toast.LENGTH_SHORT).show();
            title.setText("No sound.");
            return;
        }
        int i = index + 1;
        findNext = false;
        String fullname, subname;
        for (; i < files.length; i++) {
            fullname = files[i].getName();
            subname = files[i].getName().substring(fullname.lastIndexOf(46) + 1, fullname.length());
            if (subname.equals("mp3")) {
                index = i;
                String tmp = files[i].getPath();
                File mp3File = new File(tmp);
                if (mp3File.exists()) {
                    findNext = true;
                    title.setText(fullname.substring(0, fullname.lastIndexOf('.')));
                    doStop();
                    onResume();
                    try {
                        player.setDataSource(tmp);
                        player.prepare();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                    doPlay();

                    tmp = sdroot.getPath() + "/Pictures/" + fullname;
                    tmp = tmp.substring(0, tmp.lastIndexOf(46)) + ".jpg";
                    File imgFile = new File(tmp);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        cover.setImageBitmap(myBitmap);
                    } else {
                        cover.setImageResource(R.drawable.music);
                    }
                    break;
                }
            }
        }
        if (!findNext) {
            Toast.makeText(MainActivity.this, "It is last sound.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doPrev() {
        if (!findMP3) {
            Toast.makeText(MainActivity.this, "Not found sound.", Toast.LENGTH_SHORT).show();
            title.setText("No sound.");
            return;
        }
        int i = index - 1;
        findPrev = false;
        String fullname, subname;
        for (; i >= 0; i--) {
            fullname = files[i].getName();
            subname = files[i].getName().substring(fullname.lastIndexOf(46) + 1, fullname.length());
            if (subname.equals("mp3")) {
                index = i;
                String tmp = files[i].getPath();
                File mp3File = new File(tmp);
                if (mp3File.exists()) {
                    findPrev = true;
                    title.setText(fullname.substring(0, fullname.lastIndexOf('.')));
                    doStop();
                    onResume();
                    try {
                        player.setDataSource(tmp);
                        player.prepare();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                    doPlay();

                    tmp = sdroot.getPath() + "/Pictures/" + fullname;
                    tmp = tmp.substring(0, tmp.lastIndexOf(46)) + ".jpg";
                    File imgFile = new File(tmp);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        cover.setImageBitmap(myBitmap);
                    } else {
                        cover.setImageResource(R.drawable.music);
                    }
                    break;
                }
            }
        }
        if (!findPrev) {
            Toast.makeText(MainActivity.this, "It is first sound.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doPlay() {
        if (!findMP3) {
            Toast.makeText(MainActivity.this, "Not found sound.", Toast.LENGTH_SHORT).show();
            title.setText("No sound.");
            return;
        }
        isPause = !isPause;
        if (!isPause) {
            try {
                player.start();
                player.seekTo(currentPosition);
                btnPlay.setText("PAUSE");
                seekBar.setMax(player.getDuration());
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(!isSeekBarChanging) {
                            currentPosition = player.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                            Intent intent = new Intent();
                            intent.setAction("seika.NOTIFICATION");
                            String tmp = files[index].getName();
                            tmp = tmp.substring(0, tmp.lastIndexOf('.'));
                            intent.putExtra("KEY_MSG", tmp);
                            intent.putExtra("progress", currentPosition);
                            intent.putExtra("progressMax", player.getDuration());
                            tmp = sdroot.getPath() + "/Pictures/" + tmp + ".jpg";
                            intent.putExtra("cover", tmp);
                            sendBroadcast(intent);
                        }
                    }
                }, 0, 50);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                currentPosition = player.getCurrentPosition();
                player.pause();
                btnPlay.setText("PLAY");
                timer.purge();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStop() {
        isPause = true;
        try {
            player.stop();
            player.prepare();
            btnPlay.setText("PLAY");
            currentPosition = 0;
            timer.purge();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
        }
    }

    private static int ACTIVITY_SET_INDEX = 0;

    private void doList() {
        if (!findMP3) {
            Toast.makeText(MainActivity.this, "Not found sound.", Toast.LENGTH_SHORT).show();
            title.setText("No sound.");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, GridActivity.class);
        ArrayList<String> musicList = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String tmp = files[i].getName();
            musicList.add(tmp.substring(0, tmp.lastIndexOf('.')));
        }
        intent.putExtra("musicList", musicList);
        startActivityForResult(intent, ACTIVITY_SET_INDEX);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (intent == null)
            return;
        super.onActivityResult(requestCode, resultCode, intent);
        int i = intent.getIntExtra("indexOrder", 1);
        String tmp = files[i].getPath();
        String fullname = files[i].getName();
        title.setText(fullname.substring(0,fullname.lastIndexOf('.')));
        if(!isPause) {
            doStop();
        }
        onResume();
        try {
            player.setDataSource(tmp);
            player.prepare();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error.", Toast.LENGTH_SHORT).show();
        }
        doPlay();
        tmp=sdroot.getPath()+"/Pictures/"+fullname;
        tmp=tmp.substring(0,tmp.lastIndexOf(46))+".jpg";
        File imgFile = new File(tmp);
        if(imgFile.exists()) {
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        cover.setImageBitmap(myBitmap);
        } else {
            cover.setImageResource(R.drawable.music);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
                    doPlay();
                    break;
				case R.id.btn_next:
					doNext();
					break;
				case R.id.btn_prev:
					doPrev();
					break;
				case R.id.btn_load:
					doLoad();
					break;
            }
        }
    };

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    doList();
                    break;
            }
            return true;
        }
    };
}
