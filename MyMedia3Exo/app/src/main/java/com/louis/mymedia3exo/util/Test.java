package com.louis.mymedia3exo.util;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import com.louis.mymedia3exo.SurfaceTextureProxy;

public class Test {
    public void main(String[] args) {
        TextureView textureView = new TextureView();

        textureView.setSurfaceTextureListener(new SurfaceTextureProxy(textureView.getSurfaceTextureListener(), new SurfaceTextureProxy.Listener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, "onSurfaceTextureAvailable: zfq01");
            }
        }));
    }
}
