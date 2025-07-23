package com.louis.mymedia3exo;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;

/**
 *  textureView.setSurfaceTextureListener(new SurfaceTextureProxy(textureView.getSurfaceTextureListener(), new SurfaceTextureProxy.Listener() {
 *      @Override
 *      public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
 *          Log.e(TAG, "onSurfaceTextureAvailable: zfq01");
 *      }
 *  }));
 */
public class SurfaceTextureProxy implements TextureView.SurfaceTextureListener {

    //源对象
    private TextureView.SurfaceTextureListener originListener;
    private Listener listener;

    public SurfaceTextureProxy(TextureView.SurfaceTextureListener originListener, Listener listener) {
        this.originListener = originListener;
        this.listener = listener;
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        originListener.onSurfaceTextureAvailable(surface, width, height);
        Log.e("TAG", "onSurfaceTextureAvailable: zfq0221" + originListener);
        //
        listener.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        originListener.onSurfaceTextureSizeChanged(surface, width, height);
        //
        listener.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        boolean result = originListener.onSurfaceTextureDestroyed(surface);
        //
        listener.onSurfaceTextureDestroyed(surface);
        return result;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        originListener.onSurfaceTextureUpdated(surface);
        //
        listener.onSurfaceTextureUpdated(surface);
    }


    public interface Listener {

        default void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        default void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        default void onSurfaceTextureDestroyed(SurfaceTexture surface) {
        }

        default void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
