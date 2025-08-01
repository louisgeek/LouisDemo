package com.louis.mymedia3exo

import android.util.Log
import android.view.MotionEvent
import kotlin.math.atan2

/**
 * 实现旋转手势
 */
class RotationGestureDetector(private val listener: OnRotationGestureListener) {
    companion object {
        private const val TAG = "RotationGestureDetector"
    }

    private var currAngle = 0f

    fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    currAngle = calculateAngle(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val newAngle = calculateAngle(event)
                    val angle = newAngle - currAngle
                    listener.onRotation(angle)

                    currAngle = newAngle //记录
                }
            }

            MotionEvent.ACTION_UP -> {
                Log.e(TAG, "onTouchEvent: ")
            }
        }
    }

    /**
     * 根据两个触摸点的位置，计算它们连线相对于水平方向的角度（单位为度）
     */
    private fun calculateAngle(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        /**
         * 1、atan2(dy, dx) 计算连线与水平方向的夹角，返回的是弧度；
         * 2、Math.toDegrees(...) 把角度从弧度转换成角度制（degree），范围为 -180° ~ +180°
         */
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }

    interface OnRotationGestureListener {
        fun onRotation(angle: Float)
    }
}