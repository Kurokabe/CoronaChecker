package ch.kurokabe.coronachecker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    private var myAudioManager: AudioManager? = null
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO)

    private val PERMISSION_RECORD_AUDIO = 0
    var mThread: Thread? = null
    var coughThreshold = 30000

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted =
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAds()

        initFragment()

        initRecorder()

    }

    private fun initAds()
    {
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun initFragment()
    {
        var selectedFragment = AnalyserFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

    }

    private fun initRecorder()
    {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_RECORD_AUDIO
                )
                return
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    1
                )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_RECORD_AUDIO
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            myAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val x =
                myAudioManager!!.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED)

            runOnUiThread {
                val tvAccXValue = findViewById<TextView>(R.id.raw_available)
                tvAccXValue.text = x
            }

            mThread = Thread(Runnable { record() })
            mThread!!.start()
        }
    }

    private fun record() {
        val audioSource = MediaRecorder.AudioSource.MIC
        val samplingRate = 11025
        val channelConfig = AudioFormat.CHANNEL_IN_DEFAULT
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioFormat)

        val buffer = ShortArray(bufferSize / 4)
        val myRecord =
            AudioRecord(audioSource, samplingRate, channelConfig, audioFormat, bufferSize)

        myRecord.startRecording()

        var isCoughing = false
        var startTime = System.nanoTime()

        while (true) {
            val bufferResults = myRecord.read(buffer, 0, bufferSize / 4)
            for (i in 0 until bufferResults) {
                val elapsed = (System.nanoTime() - startTime) / 1000000

                val currentVal = buffer[i].toInt()
                if (currentVal > coughThreshold)
                {
                    isCoughing = true
                    startTime = System.nanoTime()
                }

                if(elapsed>3000) {
                    isCoughing = false
                }
                runOnUiThread {
                    val raw_value = findViewById<TextView>(R.id.sensor_value)
                    raw_value.text = isCoughing.toString()
                }

            }

        }
    }
}
