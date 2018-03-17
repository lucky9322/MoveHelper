package com.zdd.movehelper.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;

import com.zdd.movehelper.R;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class AssetsLoad {

    // 声音文件
    public static SoundPool soundPool;
    public static int putSoundId;
    public static int dropSoundId;
    //public static MediaPlayer player;

    // 图片文件
    public static Bitmap picMyTurn;
    public static Bitmap picEnemyTurn;
    public static Bitmap picIWin;
    public static Bitmap picEnemyWin;
    public static Bitmap picWhiteWin;
    public static Bitmap picBlackWin;
    public static Bitmap picWhiteTurn;
    public static Bitmap picBlackTurn;

    public static void load(Context context) {
        soundLoad(context);
        picLoad(context);
    }

    private static void picLoad(Context context) {
        // TODO Auto-generated method stub
        Resources res = context.getResources();
        picMyTurn = BitmapFactory.decodeResource(res, R.drawable.my_turn );
        picEnemyTurn = BitmapFactory.decodeResource(res, R.drawable.enemy_turn);
        picIWin = BitmapFactory.decodeResource(res, R.drawable.i_win);
        picEnemyWin = BitmapFactory.decodeResource(res, R.drawable.enemy_win);
        picWhiteWin = BitmapFactory.decodeResource(res, R.drawable.white_win);
        picBlackWin = BitmapFactory.decodeResource(res, R.drawable.black_win);
        picWhiteTurn = BitmapFactory.decodeResource(res, R.drawable.white_turn);
        picBlackTurn = BitmapFactory.decodeResource(res, R.drawable.black_turn);

    }

    public static void soundLoad(Context context) {
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        putSoundId = soundPool.load(context, R.raw.putsound, 1);
        dropSoundId = soundPool.load(context, R.raw.drop_sound, 1);
        //player = MediaPlayer.create(context, R.raw.background_music);
        //prepareBackgroundMusic(context);
    }

    public static void playSound(Context contenxt, int soundId) {
        AudioManager manager = (AudioManager) contenxt
                .getSystemService(Context.AUDIO_SERVICE);
        // 获取当前音量和当前音量
        float currVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = currVol / maxVol;
        soundPool.play(soundId, maxVol, maxVol, 1, 0, 1.0f);

    }
}
