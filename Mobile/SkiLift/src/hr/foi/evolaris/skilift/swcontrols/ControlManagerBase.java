/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package hr.foi.evolaris.skilift.swcontrols;

import hr.foi.evolaris.skilift.AdvancedLayoutsExtensionService;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

/**
 * The phone control manager manages which control to currently show on the
 * display. This class then forwards any life-cycle methods and events events to
 * the running control. This base class only handles API level 1 methods.
 */
public class ControlManagerBase extends ControlExtension {

    protected static ControlExtension mCurrentControl = null;

    private static final int STATE_IDLE = 0;

    protected static final int STATE_STARTED = 1;

    protected static final int STATE_FOREGROUND = 2;

    protected int mControlState = STATE_IDLE;

    /**
     * Create phone control manager.
     *
     * @param context The context to use.
     * @param packageName The package name of the host application.
     */
    public ControlManagerBase(final Context context, final String packageName) {
        super(context, packageName);
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "created");
        Log.d("AJMO1", "jedan");
    }

    @Override
    public void onDestroy() {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "ControlManager onDestroy");
        if (mCurrentControl != null) {
            mCurrentControl.onDestroy();
        }
    }

    @Override
    public void onStart() {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onStart");
        mControlState = STATE_STARTED;
        if (mCurrentControl != null) {
            mCurrentControl.onStart();
        }
    }

    @Override
    public void onStop() {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onStop");
        mControlState = STATE_IDLE;
        if (mCurrentControl != null) {
            mCurrentControl.onStop();
        }
    }

    @Override
    public void onPause() {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onPause");
        mControlState = STATE_STARTED;
        if (mCurrentControl != null) {
            mCurrentControl.onPause();
        }
    }

    @Override
    public void onResume() {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onResume");
        mControlState = STATE_FOREGROUND;
        if (mCurrentControl != null) {
            mCurrentControl.onResume();
        }
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onTouch");
        if (mCurrentControl != null) {
            mCurrentControl.onTouch(event);
        }
    }

    @Override
    public void onDoAction(int requestCode, Bundle bundle) {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onDoAction");
        if (mCurrentControl != null) {
            mCurrentControl.onDoAction(requestCode, bundle);
        }
    }

    @Override
    public void onError(int code) {
        Log.d(AdvancedLayoutsExtensionService.LOG_TAG, "onError");
        if (mCurrentControl != null) {
            mCurrentControl.onError(code);
        }
    }

    @Override
    public void onKey(int action, int keyCode, long timeStamp) {
        Log.v(AdvancedLayoutsExtensionService.LOG_TAG, "onKey");
        if (mCurrentControl != null) {
            mCurrentControl.onKey(action, keyCode, timeStamp);
        }
    }

    /**
     * Start a new control. Any currently running control will be stopped.
     *
     * @param newControlId The control to start.
     */
    protected void startControl(final ControlExtension newControl) {

        Log.d(AdvancedLayoutsExtensionService.LOG_TAG, "ControlManager startControl");
        if (newControl != null) {
            // Stop the current control
            int savedState = mControlState;
            closeCurrentControl();
            mCurrentControl = newControl;
            // Start and resume the new control
            if (mCurrentControl != null) {
                onStart();
                if (savedState == STATE_FOREGROUND) {
                    onResume();
                }
            }
        }
    }

    protected void closeCurrentControl() {
        if (mCurrentControl != null) {
            if (mControlState == STATE_FOREGROUND) {
                onPause();
            }
            if (mControlState == STATE_STARTED) {
                onStop();
            }
            mCurrentControl.onDestroy();
        }
    }
}
