package ch.kurokabe.coronachecker

import java.util.*
import kotlin.collections.ArrayList

class Analyzer {
    private var analysisListeners : MutableList<AnalysisEvents> = ArrayList()
    private var r = Random()
    private val minWait = 25
    private val maxWait = 150

    fun register(listener: AnalysisEvents)
    {
        analysisListeners.add(listener)
    }

    fun unregister(listener: AnalysisEvents)
    {
        analysisListeners.remove(listener)
    }

    fun start()
    {
        Thread(Runnable { updatePercentage() }).start()
    }

    private fun updatePercentage()
    {
        var percentage = 0
        while(percentage < 100)
        {
            Thread.sleep((r.nextInt(maxWait - minWait) + minWait).toLong())
            percentage++
            for(listener in analysisListeners)
            {
                listener.analysisPercentageUpdate(percentage)
            }
        }
    }
}