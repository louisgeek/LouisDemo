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
}