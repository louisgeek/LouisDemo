package com.louis.mymediacodec

import android.hardware.Camera
import android.view.Surface

/**
 * Created by louisgeek on 2025/2/7.
 */
object CameraTool {

    const val CAMERA_ID_NONE = -1

    const val CAMERA_FACING_ANY = -1
    const val CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK
    const val CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT

    @Deprecated("")
    //获取指定前后置摄像头的 CameraId，默认任意摄像头
    private fun getCameraBean(cameraFacing: Int = CAMERA_FACING_ANY): CameraBean {
        var cameraId = CAMERA_ID_NONE
        var cameraInfo: Camera.CameraInfo? = null
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (cameraFacing == CAMERA_FACING_ANY) {
                //任意一个
                cameraId = 0
                cameraInfo = info
                break
            } else if (cameraFacing == info.facing) {
                cameraId = i
                cameraInfo = info
                break
            }
        }
        return CameraBean(cameraId, cameraInfo)
    }

    //获取指定前后置摄像头的 Camera，默认任意摄像头
    fun getCamera(cameraFacing: Int = CAMERA_FACING_ANY): CameraBean? {
        val cameraBean = this.getCameraBean(cameraFacing)
        val cameraId = cameraBean.cameraId
        if (cameraId >= 0) {
            val camera = Camera.open(cameraId)
            cameraBean.camera = camera
            return cameraBean
        }
        return null
    }

    //获取后置摄像头的 Camera
    fun getBackFacingCamera(): CameraBean? {
        return this.getCamera(CAMERA_FACING_BACK)
    }

    //获取前置摄像头的 Camera
    fun getFrontFacingCamera(): CameraBean? {
        return this.getCamera(CAMERA_FACING_FRONT)
    }

    @Deprecated("")
    fun getCameraDisplayOrientation(
        displayRotation: Int,
        isFacingFront: Boolean,
        cameraOrientation: Int
    ): Int {
        var degrees = 0
        when (displayRotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (isFacingFront) {
            result = (cameraOrientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {
            // back-facing
            result = (cameraOrientation - degrees + 360) % 360
        }
        return result
    }
}