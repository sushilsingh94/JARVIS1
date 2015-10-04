package com.voice.java.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Sushil on 10/3/2015.
 */
public class PhoneCallReceiver extends BroadcastReceiver {
    TelephonyManager telManager;
    Context context;
    private AudioManager mAudioManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

        telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
                        mAudioManager.setStreamMute(AudioManager.STREAM_RING,true);
                        mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
                        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                        mAudioManager.setSpeakerphoneOn(true);
                        /*try {
                            Method method = telManager.getClass().getMethod("answerRingingCall");
                            method.setAccessible(true);
                            method.invoke(telManager);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK);
                        i.putExtra(Intent.EXTRA_KEY_EVENT,event);
                        context.sendOrderedBroadcast(i,"android.permission.CALL_PRIVILEGED");

                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {

                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
                        mAudioManager.setStreamMute(AudioManager.STREAM_RING,false);
                        mAudioManager.setMode(AudioManager.MODE_NORMAL);
                        break;
                    }
                    default: { }
                }
            } catch (Exception ex) {

            }
        }
    };
}
