package com.flamapp.demo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.hardware.camera2.*
import android.opengl.GLSurfaceView

class MainActivity : AppCompatActivity() {
    private val TAG = "FlamApp"
    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var imageReader: ImageReader
    private lateinit var glView: GLSurfaceView
    private val jni = JNIInterface()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        if (perms[Manifest.permission.CAMERA] == true) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        // Simple layout: GLSurfaceView full screen
        glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(GLRenderer())
        setContentView(glView as ViewGroup)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val size = map!!.getOutputSizes(ImageFormat.YUV_420_888)[0]
            imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 2)
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                processImage(image)
                image.close()
            }, null)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    val surface = imageReader.surface
                    val previewSurface = Surface(glView.surfaceTexture)
                    camera.createCaptureSession(listOf(surface, previewSurface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            captureSession = session
                            try {
                                val request = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                request.addTarget(surface)
                                session.setRepeatingRequest(request.build(), null, null)
                            } catch (e: CameraAccessException) { Log.e(TAG, "capture error", e) }
                        }
                        override fun onConfigureFailed(session: CameraCaptureSession) { Log.e(TAG, "configure failed") }
                    }, null)
                }
                override fun onDisconnected(camera: CameraDevice) { camera.close() }
                override fun onError(camera: CameraDevice, error: Int) { Log.e(TAG, "camera error $error") }
            }, null)
        } catch (e: Exception) { Log.e(TAG, "startCamera error", e) }
    }

    private fun processImage(image: Image) {
        // Convert Y plane to a grayscale byte array (fast path)
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val width = image.width
        val height = image.height
        // Send Y-plane grayscale bytes to native
        val processed = jni.processGrayFrame(bytes, width, height)
        // Here you would pass 'processed' to GLRenderer to update texture.
        // For brevity, that connection is left as an exercise to wire updateTexture.
    }

    override fun onDestroy() {
        super.onDestroy()
        captureSession?.close()
        cameraDevice?.close()
    }
}
