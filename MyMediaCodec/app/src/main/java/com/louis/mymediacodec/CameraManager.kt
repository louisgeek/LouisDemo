package com.louis.mymediacodec

import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.util.Size
import android.view.SurfaceHolder
import java.io.IOException


/**
 * Created by louisgeek on 2025/2/7.
 * surfaceCreated surfaceDestroyed
 */
class CameraManager(private val surfaceHolder: SurfaceHolder) {
    private var mCamera: Camera? = null

    //    private var mCameraId = CameraTool.CAMERA_ID_NONE
    private var mCameraFacing = CameraTool.CAMERA_FACING_ANY


    private fun openCamera(cameraFacing: Int = CameraTool.CAMERA_FACING_ANY) {
        try {
            val cameraBean = CameraTool.getCamera(cameraFacing)
            mCamera = cameraBean?.camera
            val cameraInfo = cameraBean?.cameraInfo
//            cameraInfo.orientation
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * onResume
     */
    private fun startPreview() {
        try {
            val pictureSize = Size(1280, 720)
            val previewSize = Size(1280, 720)
            //
            val parameters = mCamera?.getParameters()

            /**
             * ImageFormat.JPEG
             * ImageFormat.RGB_565
             * ImageFormat.NV21 //YCbCr_420_SP
             * ImageFormat.NV16 //YCbCr_422_SP
             * ImageFormat.YUY2 //YCbCr_422_I
             * ImageFormat.YV12
             */
            parameters?.setPictureFormat(ImageFormat.JPEG) //相机照片格式 JPEG，默认 NV21
            parameters?.setPictureSize(pictureSize.width, pictureSize.height) //设置相机照片的大小
            parameters?.setPreviewFormat(ImageFormat.NV21) //默认 NV21
            parameters?.setPreviewSize(previewSize.width, previewSize.height)
            //此方法为官方提供的旋转显示部分的方法，并不会影响onPreviewFrame方法中的原始数据
            mCamera?.setDisplayOrientation(90)
            //
            mCamera?.setPreviewDisplay(surfaceHolder)
//            mCamera?.setPreviewTexture(SurfaceTexture)
            mCamera?.setPreviewCallback(object : PreviewCallback {
                override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                    data?.let { bytes ->
                        onPreviewFrame?.invoke(bytes)
                    }
                }
            })
            mCamera?.setParameters(parameters)
            //
            mCamera?.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * onPause
     */
    private fun stopPreview() {
        if (mCamera != null) {
            mCamera?.setPreviewCallback(null)
            mCamera?.stopPreview()
            mCamera?.release()
            mCamera = null
        }
    }


    fun initCamera() {
        this.openCamera(mCameraFacing)
        this.startPreview()
    }

    /**
     * onDestroy
     */
    fun releaseCamera() {
        this.stopPreview()
        //
//        mPreview.destroyDrawingCache()
    }

    fun switchCamera() {
        //
        this.releaseCamera()
        //
        if (mCameraFacing == CameraTool.CAMERA_FACING_BACK) {
            mCameraFacing = CameraTool.CAMERA_FACING_FRONT
        } else if (mCameraFacing == CameraTool.CAMERA_FACING_FRONT) {
            mCameraFacing = CameraTool.CAMERA_FACING_BACK
        }
        //
        this.initCamera()
    }

    private var onPreviewFrame: ((bytes: ByteArray) -> Unit)? = null
    fun setOnPreviewFrameListener(onPreviewFrame: ((bytes: ByteArray) -> Unit)?) {
        this.onPreviewFrame = onPreviewFrame
    }
}