package ch.kurokabe.coronachecker


import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pl.droidsonroids.gif.GifTextView


/**
 * A simple [Fragment] subclass.
 */
class AnalyserFragment : Fragment(), MicListener{

    var audioVisualizer : AudioVisualizer? = null
    var analyseGif : GifTextView? = null
    var txtInstruction : TextView? = null

    private var coughThreshold = 30000
    private var waitBetweenCough = 250
    private val requiredCough = 3

    private var coughNumber = 0
    private var startTime = System.nanoTime()
    private var isAnalysing = false

    override fun onUpdate(incomingValue: Int) {
        if (isAnalysing)
            return

        activity?.runOnUiThread {
            audioVisualizer?.addValue(incomingValue)
        }

        val elapsed = (System.nanoTime() - startTime) / 1000000
        if (incomingValue > coughThreshold && elapsed > waitBetweenCough) {
            coughNumber++
            startTime = System.nanoTime()
        }

        val strCough = resources.getString(R.string.instruction, requiredCough - coughNumber)
        txtInstruction?.text = strCough

        if (coughNumber >= requiredCough)
            analyse()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_analyser, container, false)
        audioVisualizer = v.findViewById(R.id.audioVisualizer)
        analyseGif = v.findViewById(R.id.gifTextView)
        txtInstruction = v.findViewById(R.id.txtInstruction)
        return v
    }

    private fun analyse()
    {
        isAnalysing = true
        txtInstruction?.text = getString(R.string.analyzing)
        activity?.runOnUiThread {
            audioVisualizer?.visibility = View.GONE
            analyseGif?.visibility = View.VISIBLE
        }
    }


}
