package com.bitsplease.rakshak;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;


public class GestureDetector {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({State.UNKNOWN, State.HOLD, State.RELEASE})
    private @interface State {
        int UNKNOWN = 0;
        int HOLD = 1;
        int RELEASE = 2;
    }

    private final Handler mHandler =
            new Handler() {
                @SuppressWarnings("ResourceType")
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case State.HOLD:
                            onHold();
                            break;
                        case State.RELEASE:
                            onRelease();
                            break;
                    }
                }
            };

    private boolean mHandledDownAlready;
    private final Set<Integer> mKeyCodes = new HashSet<>();


    public GestureDetector(int... keyCodes) {
        for (int keyCode : keyCodes) {
            mKeyCodes.add(keyCode);
        }
    }

    protected void onHold() {}

    protected void onRelease() {}


    public boolean onKeyEvent(KeyEvent event) {
        if (!mKeyCodes.contains(event.getKeyCode())) {
            return false;
        }

        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                // KeyEvents will call ACTION_DOWN over and over again while held.
                // We only care about the first event, so we can ignore the rest.
                if (mHandledDownAlready) {
                    break;
                }
                mHandledDownAlready = true;
                mHandler.sendEmptyMessage(State.HOLD);
                break;
            case KeyEvent.ACTION_UP:
                mHandledDownAlready = false;
                mHandler.sendEmptyMessage(State.RELEASE);
                break;
        }

        return true;
    }
}

