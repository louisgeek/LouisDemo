package com.louis.mymedia3exo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class GestureImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    //
    private val matrix = Matrix()

    //缩放
    private var scaleGestureDetector: ScaleGestureDetector
    private val MIN_SCALE = 0.5f
    private val MAX_SCALE = 3.0f
    private var currScale = 1f

    //拖动
    private var dragGestureDetector: GestureDetector

    //旋转
    private var rotationGestureDetector: RotationGestureDetector
    private var currRotation = 0f


    private var isZoomed = false
    private var scaleFactor = 2f // 放大倍数
    private var mDefaultScale = 1f

    init {
        scaleType = ScaleType.MATRIX
        //
        scaleGestureDetector = ScaleGestureDetector(
            context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                /**
                 * 当两个手指在屏幕上移动时持续调用
                 */
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    //detector.scaleFactor 当前帧的缩放因子（比如 1.05 表示放大 5%，0.95 表示缩小 5%）
                    var newScale = currScale * detector.scaleFactor //累积
                    newScale = newScale.coerceIn(MIN_SCALE, MAX_SCALE) //修正（限制缩放比例）
                    val realFactor = newScale / currScale //除回去，相当于修正后的 scaleFactor（计算限制后的实际缩放因子）

                    matrix.postScale(
                        realFactor, realFactor, detector.focusX, detector.focusY
                    ) //焦点坐标（两个手指的中心点）
                    currScale = newScale //记录

                    applyMatrix()
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    super.onScaleEnd(detector)
                }
            })
        dragGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                /**
                 * 拖动手势中持续触发
                 */
                override fun onScroll(
                    e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
                ): Boolean {
                    //distanceX 和 distanceY 表示当前事件和上一个事件之间的移动距离（不是总距离）
                    //distanceX 横向滑动距离，向右滑动为负数，x 值减小，向左滑动正数，x 值增加（prevX - currX）
                    //distanceX 纵向滑动距离，向下滑动负数，y 值减小，向上滑动正数，y 增加(prevY - currY)
                    val dx = -distanceX //distanceX 右滑 -4~-2   左滑 2~4
                    val dy = -distanceY
                    matrix.postTranslate(dx, dy)

                    applyMatrix()
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
//            zoomImage()
                    return true
                }
            })
        rotationGestureDetector =
            RotationGestureDetector(object : RotationGestureDetector.OnRotationGestureListener {
                override fun onRotation(angle: Float) {
                    currRotation += angle //累积
                    val px = width / 2f
                    val py = height / 2f
                    matrix.postRotate(angle, px, py)

                    applyMatrix()
                }
            })


        setImgToCenter(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        dragGestureDetector.onTouchEvent(event)
        rotationGestureDetector.onTouchEvent(event)
        return true
    }

    private fun applyMatrix() {
        this.imageMatrix = matrix
    }

    private fun zoomImage() {
//        log("currentScale:$currentScale,isZoomed:$isZoomed")
        isZoomed = currScale > mDefaultScale
        if (currScale == mDefaultScale) {
            scaleFactor = if (isZoomed) 0.5f else 2f
            matrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
            isZoomed = !isZoomed
            imageMatrix = matrix
            invalidate()
        } else {
            setImgToCenter(this)
            currScale = mDefaultScale
            isZoomed = false
        }
    }

    private fun getCurScale(): Float {
        val mArr = FloatArray(9)
        imageMatrix.getValues(mArr)
        return min(mArr[Matrix.MSCALE_X], mArr[Matrix.MSCALE_Y])
    }

    private fun setImgToCenter(img: ImageView) {
        img.post {
            val drawable = img.drawable //图片
            drawable?.let {
                matrix.reset()
                //计算宽高比例
                val widthScale = img.width.toFloat() / it.intrinsicWidth
                val heightScale = img.height.toFloat() / it.intrinsicHeight
                //选择较小的缩放比
                val scale = min(widthScale, heightScale)
                //缩放后的图片尺寸
                val scaledWidth = it.intrinsicWidth * scale
                val scaleHeight = it.intrinsicHeight * scale
                //计算平移量
                val dx = (img.width - scaledWidth) / 2
                val dy = (img.height - scaleHeight) / 2
                matrix.postScale(scale, scale)
                matrix.postTranslate(dx, dy)
                img.imageMatrix = matrix
            }
        }
    }


}