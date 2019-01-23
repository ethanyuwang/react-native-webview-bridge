package com.github.alinz.reactnativewebviewbridge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import javax.annotation.Nullable;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class RNKeyboard extends ReactContextBaseJavaModule {

    @Override
    public String getName() {
        return "RNKeyboard";
    }

    public RNKeyboard(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getReactApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
}