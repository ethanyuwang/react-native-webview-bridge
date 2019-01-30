package com.github.alinz.reactnativewebviewbridge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.os.Handler;

import javax.annotation.Nullable;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClipBoardIntercepter extends ReactContextBaseJavaModule implements LifecycleEventListener {

    protected  @Nullable
    ClipboardManager mClipboard;

    protected @Nullable
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    private boolean disableCopy = true;
    private boolean disableCopyBackup = true;

    //customized handler that passes ReactApplicationContext and does the initialization
    public class MyRunnable implements Runnable {
        private ReactApplicationContext reactContext;
        public MyRunnable(ReactApplicationContext reactContext) {
            this.reactContext = reactContext;
        }

        @Override
        public void run() {
            mClipboard = (ClipboardManager) reactContext.getSystemService(Context.CLIPBOARD_SERVICE);
            initializeClipboardListener();
            subscribeClipboardManager();
        }
    }


    @Override
    public String getName() {
        return "ClipBoardIntercepter";
    }

    public ClipBoardIntercepter(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);

        //run the initialization in a handler to avoid `Can't create handler inside thread that has not called Looper.prepare()`
        Handler handler = new Handler(Looper.getMainLooper());
        MyRunnable obj = new MyRunnable(reactContext);
        handler.post(obj);
    }

    @Override
    public void onHostResume() {
        //subscribeClipboardManager();
        this.disableCopy = this.disableCopyBackup;
    }

    @Override
    public void onHostPause() {
        //unsubscribeClipboardManager();
        //TODO: fix unsubscribeClipboardManager not working properly
        this.disableCopy = false;
    }

    @Override
    public void onHostDestroy() {
        //unsubscribeClipboardManager();
        this.disableCopy = false;
    }

    private void initializeClipboardListener() {
        //init OnPrimaryClipChangedListener and register
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() {
                String defaultString = "Please subscribe to essayBot";
                //only when disableCopy is true and clipboard data has not been programmatically set
                if (disableCopy && !isClipBoardDataSetToDefault(mClipboard, defaultString)) {
                    mClipboard.setPrimaryClip(ClipData.newPlainText(null, "Please subscribe to essayBot"));
                    dispatchOnPasteboardChanged();
                }
            }
        };
    }

    private void subscribeClipboardManager() {
        mClipboard.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void unsubscribeClipboardManager() {
        mClipboard.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }



    private Boolean isClipBoardDataSetToDefault(ClipboardManager clipboard, String defaultString) {
        // if clipboard has data and data is plain text
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String clipboardText = item.getText().toString();
            return defaultString.equals(clipboardText);
        }

        //default is false
        return false;
    }

    private void dispatchOnPasteboardChanged() {
        WritableMap event = Arguments.createMap();
        ReactApplicationContext reactContext = this.getReactApplicationContext();
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onPasteboardChanged", event);
    }

    @ReactMethod
    public void setDisableCopy(Boolean disablePasteboard) {
        disableCopy = disablePasteboard;
        disableCopyBackup = disablePasteboard;
    }
}