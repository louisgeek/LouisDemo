package com.louis.mymediacodec

import android.hardware.Camera

/**
 * Created by louisgeek on 2025/2/12.
 */
data class CameraBean(
    val cameraId: Int,
    var cameraInfo: Camera.CameraInfo? = null,
    var camera: Camera? = null
)
