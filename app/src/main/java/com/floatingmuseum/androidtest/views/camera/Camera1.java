package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class Camera1 extends CameraImpl {

    public Camera1(Context context, CameraPreview preview, CameraStateCallback stateCallback) {
        super(context, preview, stateCallback);
    }

    @Override
    public void setOutputs(int facing, int width, int height) {

    }

    @Override
    public void configureTransform(int width, int height) {

    }

    @Override
    public void openCamera() {

    }

    @Override
    public void takePhoto() {

    }
}
