import android.graphics.Matrix
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.TextureView
import androidx.media3.ui.PlayerView

class ZoomablePlayerHelper(private val playerView: PlayerView) {

    private val textureView = playerView.videoSurfaceView as TextureView
    private val matrix = Matrix()
    private var curScale = 1.0f
    private val minScale = 1.0f
    private val maxScale = 10.0f

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val newScale = (curScale * detector.scaleFactor).coerceIn(minScale, maxScale)
            val factor = newScale / curScale
            matrix.postScale(factor, factor, detector.focusX, detector.focusY)
            applyMatrix()
            curScale = newScale
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            fixBounds()
        }
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            val dx = -distanceX
            val dy = -distanceY


            val viewWidth = playerView.width.toFloat()
            val viewHeight = playerView.height.toFloat()

            val textureViewWidth = textureView.width.toFloat()
            val textureViewHeight = textureView.height.toFloat()

//            val scaledWidth = textureViewWidth * curScale
//            val scaledHeight = textureViewHeight * curScale

            val rect = RectF(0f, 0f, textureViewWidth, textureViewHeight)
            matrix.mapRect(rect)

            val fixDx = when {
//                rect.width() <= viewWidth -> viewWidth / 2 - rect.centerX()
                rect.width() <= viewWidth -> (viewWidth - rect.width()) / 2 - rect.left
                rect.left + dx > 0 -> 0 - rect.left
                rect.right + dx < viewWidth -> viewWidth - rect.right
                else -> dx
            }

            val fixDy = when {
//                rect.height() <= viewHeight -> viewHeight / 2 - rect.centerY()
                rect.height() <= viewHeight -> (viewHeight - rect.height()) / 2 - rect.top
                rect.top + dy > 0 -> 0 - rect.top
                rect.bottom + dy < viewHeight -> viewHeight - rect.bottom
                else -> dy
            }

            matrix.postTranslate(fixDx, fixDy)
            applyMatrix()
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(playerView.context, scaleListener)
    private val gestureDetector = GestureDetector(playerView.context, scrollListener)

    init {
        playerView.setOnTouchListener { v, event ->
            scaleDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) fixBounds()
            true
        }
    }

    private fun applyMatrix() = textureView.setTransform(matrix)

    private fun fixBounds() {
        val values = FloatArray(9)
        matrix.getValues(values)
        val scale = values[Matrix.MSCALE_X]
        val transX = values[Matrix.MTRANS_X]
        val transY = values[Matrix.MTRANS_Y]

        val textureViewWidth = textureView.width
        val textureViewHeight = textureView.height

        val scaledWidth = textureViewWidth * scale
        val scaledHeight = textureViewHeight * scale

        val fixX = when {
            scaledWidth <= textureViewWidth -> (textureViewWidth - scaledWidth) / 2 - transX
            transX > 0 -> 0 - transX
            scaledWidth + transX < textureViewWidth -> textureViewWidth - (scaledWidth + transX)
            else -> 0f
        }

        val fixY = when {
            scaledHeight <= textureViewHeight -> (textureViewHeight - scaledHeight) / 2 - transY
            transY > 0 -> 0 - transY
            scaledHeight + transY < textureViewHeight -> textureViewHeight - (scaledHeight + transY)
            else -> 0f
        }

        matrix.postTranslate(fixX, fixY)
        applyMatrix()
    }

    // 边界检查（防止内容移出屏幕）
    private fun applyBoundaryCheck() {
        val rect = FloatArray(9)
        matrix.getValues(rect)
        val transX = rect[Matrix.MTRANS_X]
        val transY = rect[Matrix.MTRANS_Y]

        // 计算最大允许平移量
//        val maxTransX = (contentWidth * scaleFactor - viewportWidth).coerceAtLeast(0f)
//        val maxTransY = (contentHeight * scaleFactor - viewportHeight).coerceAtLeast(0f)
//
//        // 限制平移范围
//        rect[Matrix.MTRANS_X] = transX.coerceIn(-maxTransX, 0f)
//        rect[Matrix.MTRANS_Y] = transY.coerceIn(-maxTransY, 0f)
//        matrix.setValues(rect)
    }

    private fun checkBorderAndCenter() {
//        val rect = getMatrixRectF()
//        val width = width.toFloat()
//        val height = height.toFloat()
//
//        if (rect.width() > width) {
//            if (rect.left > 0) matrix.postTranslate(-rect.left, 0)
//            if (rect.right < width) matrix.postTranslate(width - rect.right, 0)
//        } else {
//            matrix.postTranslate((width - rect.width()) / 2 - rect.left, 0)
//        }
//
//        if (rect.height() > height) {
//            if (rect.top > 0) matrix.postTranslate(0, -rect.top)
//            if (rect.bottom < height) matrix.postTranslate(0, height - rect.bottom)
//        } else {
//            matrix.postTranslate(0, (height - rect.height()) / 2 - rect.top)
//        }
    }

    private fun getMatrixRectF(): RectF {
        val rect = RectF()
//        matrix.mapRect(rect, RectF(0f, 0f, intrinsicWidth.toFloat(), intrinsicHeight.toFloat()))
        return rect
    }

    private fun animateScale(targetScale: Float, pivotX: Float, pivotY: Float) {
//        val animator = ValueAnimator.ofFloat(currentScale, targetScale).apply {
//            duration = 300
//            addUpdateListener {
//                currentScale = it.animatedValue as Float
//                matrix.setScale(currentScale, currentScale, pivotX, pivotY)
//                checkBorder()
//                invalidate()
//            }
//        }
//        animator.start()
    }

//    private fun animateScale(targetScale: Float, focusX: Float, focusY: Float) {
//        val scaleDiff = targetScale - currentScale
//        val animator = ValueAnimator.ofFloat(1f, scaleDiff)
//        animator.addUpdateListener { animation ->
//            val factor = animation.animatedValue as Float
//            currentScale = currentScale + factor
//            matrix.postScale(factor, factor, focusX, focusY)
//            setImageMatrix(matrix)
//        }
//        animator.duration = 200
//        animator.start()
//    }
}