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


            val rect = RectF(0f, 0f, textureView.width.toFloat(), textureView.height.toFloat())
            matrix.mapRect(rect)

            val fixDx = when {
                rect.width() <= viewWidth -> viewWidth / 2 - rect.centerX()
                rect.left + dx > 0 -> -rect.left
                rect.right + dx < viewWidth -> viewWidth - rect.right
                else -> dx
            }

            val fixDy = when {
                rect.height() <= viewHeight -> viewHeight / 2 - rect.centerY()
                rect.top + dy > 0 -> -rect.top
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
//        val values = FloatArray(9)
//        matrix.getValues(values)
//        val scale = values[Matrix.MSCALE_X]
//        val tx = values[Matrix.MTRANS_X]
//        val ty = values[Matrix.MTRANS_Y]
//
//        val contentW = textureView.width * scale
//        val contentH = textureView.height * scale
//
//        val fixX = when {
//            contentW <= width -> (width - contentW) / 2 - tx
//            tx > 0 -> -tx
//            contentW + tx < width -> width - contentW - tx
//            else -> 0f
//        }
//
//        val fixY = when {
//            contentH <= height -> (height - contentH) / 2 - ty
//            ty > 0 -> -ty
//            contentH + ty < height -> height - contentH - ty
//            else -> 0f
//        }
//
//        matrix.postTranslate(fixX, fixY)
//        applyMatrix()
    }
}