package com.louis.mymediacodec

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    lateinit var btnStartPlay: Button
    lateinit var btnStartRecord: Button
    lateinit var btnStopRecord: Button
    lateinit var surfaceView: SurfaceView

    var cameraManager: CameraManager? = null
    var mediaCodecManager: MediaCodecManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        btnStartPlay = findViewById(R.id.btnStartPlay)
        btnStartRecord = findViewById(R.id.btnStartRecord)
        btnStopRecord = findViewById(R.id.btnStopRecord)

        surfaceView = findViewById(R.id.surfaceView)
        //
        btnStartPlay.setOnClickListener {
            playMp4Video()
            Toast.makeText(this, "btnStartPlay", Toast.LENGTH_SHORT).show()
        }
        btnStartRecord.setOnClickListener {
            val filePath =
                Environment.getExternalStorageDirectory().path + "/Download/saveVideo.mp4"

            mediaCodecManager = MediaCodecManager()
            //mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_Init(filePath,1280,720) //横向视频
            mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_Init(
                filePath,
                720,
                1280
            ) //纵向视频
            Toast.makeText(this, "btnStartRecord", Toast.LENGTH_SHORT).show()
        }

        btnStopRecord.setOnClickListener {
            mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_Release()
            mediaCodecManager = null
            Toast.makeText(this, "btnStopRecord", Toast.LENGTH_SHORT).show()
        }


//        val rotation = this.getWindowManager().getDefaultDisplay().getRotation()

//        cameraManager = CameraManager(surfaceView.holder)
        cameraManager?.setOnPreviewFrameListener { nv21Data ->
            Log.e(TAG, "onCreate: nv21Data " + nv21Data)
            val width = 1280
            val height = 720
            val dstNV21Data = ByteArray(width * height * 3 / 2)
            YuvHelper.nv21Rotation90(nv21Data, dstNV21Data, width, height)
//            val i420data = YuvHelper.nv21ToI420(nv21Data, width, height)
//            val frameData = YuvHelper.rotateYUV420Degree90(i420data,height,width)
//
//            YuvHelper.nv21ToNV12(nv21Data,NV12, width, height)
//            val frameData = YuvHelper.rotateYUV420Degree90(NV12,width,height)
            //
            mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_EncodeFrame(dstNV21Data)
//            mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_EncodeFrame_Two(dstNV21Data)
//            mediaCodecManager?.encoder_CameraPreviewFrameToVideo_Sync_EncodeFrame_Three(dstNV21Data)
        }


        //
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                cameraManager?.initCamera()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraManager?.releaseCamera()
            }

        })
    }


    override fun onResume() {
        super.onResume()
//        cameraManager?.initCamera()
    }


    override fun onPause() {
        super.onPause()
//        cameraManager?.releaseCamera()
    }

    var thread: Thread? = null
    private fun playMp4Video() {
        val filePath = Environment.getExternalStorageDirectory().path + "/Download/testVideo.mp4"
        if (File(filePath).exists()) {
            Log.d(TAG, "onCreate:  filePath=$filePath")
//            MediaCodecHelper.decoder_VideoToSurface_Sync(surfaceView.holder.surface, filePath)
            MediaCodecHelper.decoder_Audio_Sync(filePath)
            val thread = Thread {
//                MediaCodecHelper.decoder_VideoToSurface_Async(surfaceView.holder.surface, filePath)
//            MediaCodecHelper.decoder_Audio_Async(filePath)
            }
            thread.start()
        } else {
            Log.e(TAG, "onCreate: error no file filePath=$filePath")
        }
    }
}