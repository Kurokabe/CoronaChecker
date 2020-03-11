package ch.kurokabe.coronachecker


import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import pl.droidsonroids.gif.GifTextView


/**
 * A simple [Fragment] subclass.
 */
class AnalyserFragment : Fragment(), MicListener, AnalysisEvents{

    private var listener: OnFragmentInteractionListener? = null


    private var audioVisualizer : AudioVisualizer? = null
    private var analyseGif : GifTextView? = null
    private var txtInstruction : TextView? = null
    private var txtPercentage : TextView? = null

    private lateinit var mInterstitialAd: InterstitialAd

    private var coughThreshold = 30000
    private var waitBetweenCough = 250
    private val requiredCough = 3

    private var coughNumber = 0
    private var startTime = System.nanoTime()
    private var isAnalysing = false

    private var isAdClosed = false
    private var isAdShown = false
    private var isAnalysisComplete = false

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
        txtPercentage = v.findViewById(R.id.txtPercentage)


        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                isAdClosed = true
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                isAdClosed = true
            }
        }

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


        var analyzer = Analyzer()
        analyzer.register(this)
        analyzer.start()

        Thread(Runnable { waitForEndOfAdAndAnalysis()}).start()
        isAdShown = false
        isAnalysisComplete = false
    }

    private fun waitForEndOfAdAndAnalysis()
    {
        while(!isAnalysisComplete || !isAdClosed)
        {
            Thread.sleep(500)
        }
        listener?.onAnalysisOver()
    }

    override fun analysisPercentageUpdate(newPercentage: Int) {
        txtPercentage?.text = "$newPercentage%"

        activity?.runOnUiThread()
        {
            if (newPercentage > 10 && mInterstitialAd.isLoaded && !isAdShown) {
                mInterstitialAd.show()
                isAdShown = true
                isAdClosed = false
            }
        }

        if (newPercentage >= 100)
        {
            isAnalysisComplete = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AnalyserFragment.OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onAnalysisOver()
    }


}
