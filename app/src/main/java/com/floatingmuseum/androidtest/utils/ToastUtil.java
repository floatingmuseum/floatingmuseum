package com.floatingmuseum.androidtest.utils;

import android.widget.Toast;

import com.floatingmuseum.androidtest.App;

/**
 * 单例Toast
 */
public class ToastUtil {
    private static Toast toast = null;

    private ToastUtil() {
    }

    /**
     * 传字符串
     *
     * @param content
     */
    public static void show(String content) {
        if (toast == null) {
            toast = Toast.makeText(App.context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /**
     * 传String资源id
     *
     * @param resId
     */
    public static void show(int resId) {
        show(App.context.getText(resId).toString());
    }
}
