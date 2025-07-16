package com.louis.mymediacodec

import java.nio.ByteBuffer

/**
 * Created by louisgeek on 2025/2/12.
 */
object YuvHelper {
    /**
     * 传旋转前的宽高
     */
    fun nv21Rotation90(nv21Data: ByteArray, dstNV21Data: ByteArray, width: Int, height: Int) {
        var index = 0
        //u和v
        val ySize = width * height
        val uvHeight = height / 2
        for (i in 0 until width) {
            for (j in height - 1 downTo 0) {
                dstNV21Data[index++] = nv21Data[width * j + i]
            }
        }
        var i = 0
        while (i < width) {
            for (j in uvHeight - 1 downTo 0) {
                // v
                dstNV21Data[index++] = nv21Data[ySize + width * j + i]
                // u
                dstNV21Data[index++] = nv21Data[ySize + width * j + i + 1]
            }
            i += 2
        }
    }

//    fun nv21ToI420(nv21: ByteArray, width: Int, height: Int): ByteArray {
//        val i420 = ByteArray(nv21.size)
//        val total = width * height
//        val bufferY = ByteBuffer.wrap(i420, 0, total)
//        val bufferU = ByteBuffer.wrap(i420, total, total / 4)
//        val bufferV = ByteBuffer.wrap(i420, total + total / 4, total / 4)
//        bufferY.put(nv21, 0, total)
//        var i = total
//        while (i < nv21.size) {
//            bufferV.put(nv21[i])
//            bufferU.put(nv21[i + 1])
//            i += 2
//        }
//        return i420
//    }
//
//    fun nv21ToNV12(nv21: ByteArray, nv12: ByteArray, width: Int, height: Int) {
//        val total = width * height
//        // NV21: YYYYYYYY VUVU
//        // NV12: YYYYYYYY UVUV
//        System.arraycopy(nv21, 0, nv12, 0, total)
//        var i = 0
//        while (i < total / 2) {
//            // U
//            nv12[total + i * 2] = nv21[total + i + 1]
//            // V
//            nv12[total + i * 2 + 1] = nv21[total + i]
//            i += 2
//        }
//    }
//
//    fun rotateYUV420Degree90(data: ByteArray, width: Int, height: Int): ByteArray {
//        val yuv = ByteArray(width * height * 3 / 2)
//        var i = 0
//        for (x in 0 until width) {
//            for (y in height - 1 downTo 0) {
//                yuv[i] = data[y * width + x]
//                i++
//            }
//        }
//        i = width * height * 3 / 2 - 1
//        var x = width - 1
//        while (x > 0) {
//            for (y in 0 until height / 2) {
//                yuv[i] = data[(width * height) + (y * width) + x]
//                i--
//                yuv[i] = data[((width * height) + (y * width)
//                        + (x - 1))]
//                i--
//            }
//            x = x - 2
//        }
//        return yuv
//    }

}