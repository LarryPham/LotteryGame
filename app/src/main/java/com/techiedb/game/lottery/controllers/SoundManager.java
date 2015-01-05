package com.techiedb.game.lottery.controllers;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * @author Larry Pham
 * @since January.05.2015
 * The <code>SoundManager</code> class which represents the sound-logic controllers to manage the sound asserts
 * into the Lottery-Game Application.
 */
public class SoundManager {
    public static final String TAG = SoundManager.class.getSimpleName();
    private Context mContext;
    private static SoundManager sInstance;

    private HashMap<Integer,Integer> soundPoolHashMap;
    private SoundPool soundPool;

    public static final int SOUND_LOOPING = 1;
    public static final int SOUND_START = 2;
    public static final int SOUND_STOP = 3;

    public static final int SOUND_WINNER_CONGRATULATION = 4;
    public static final int SOUND_LOSER_MESSAGE = 5;
    public static final int SOUND_MESSAGE_POPUP = 6;

    public static final int SOUND_SPIN_TOK = 7;
    public static final int SOUND_SPIN_TOK_END = 8;

    public static boolean mAudioOn = false;

    private SoundManager(Context context){
        this.mContext = context;
        this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
    }

    public static SoundManager getInstance(Context context){
        if (sInstance == null) {
            sInstance = new SoundManager(context);
        }
        return sInstance;
    }
}
