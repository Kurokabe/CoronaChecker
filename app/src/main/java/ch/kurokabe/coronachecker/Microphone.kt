package ch.kurokabe.coronachecker

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService


class Microphone(myAudioManager: AudioManager) {
    private var listener: MicListener? = null

    private var myAudioManager: AudioManager? = null
    private var mThread: Thread? = null
    private var isRunning = true

    fun setListener(listener: MicListener) {
        this.listener = listener
    }

    fun removeListener() {
        this.listener = null
    }

    fun startListening()
    {
        isRunning = true
        mThread = Thread(Runnable { record() })
        mThread!!.start()
    }

    fun stopListening()
    {
        isRunning = false
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

        while (isRunning) {
            val bufferResults = myRecord.read(buffer, 0, bufferSize / 4)
            for (i in 0 until bufferResults) {

                val currentVal = buffer[i].toInt()
                listener?.onUpdate(currentVal)

            }

        }
    }
}