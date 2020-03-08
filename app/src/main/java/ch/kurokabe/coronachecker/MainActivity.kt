package ch.kurokabe.coronachecker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false

    private val PERMISSION_RECORD_AUDIO = 0
    private var microphone : Microphone? = null

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

        initRecorder()

        initFragment()

    }

    override fun onStart() {
        super.onStart()
        startRecording()
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
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

        microphone?.addListener(selectedFragment)
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
            microphone = Microphone(getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    private fun startRecording()
    {
        microphone?.startListening()
    }

    private fun stopRecording()
    {
        microphone?.stopListening()
    }



}
