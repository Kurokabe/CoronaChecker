package ch.kurokabe.coronachecker


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 */
class AnalyserFragment : Fragment(), MicListener{

    var txtValue : TextView? = null

    override fun onUpdate(incomingValue: Int) {
        activity?.runOnUiThread {
            txtValue?.text = incomingValue.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_analyser, container, false)
        txtValue = v.findViewById(R.id.micValue)
        return v
    }


}
