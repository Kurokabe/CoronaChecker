package ch.kurokabe.coronachecker

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ResultFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class ResultFragment : Fragment() {
    private var listener: OnFragmentResultInteractionListener? = null
    private var r = Random()
    private var chanceOfHavingCorona = 1/2f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_result, container, false)

        var hasCorona = testForCorona()

        if (hasCorona){
            v.findViewById<TextView>(R.id.txtPositive).text = getString(R.string.coronaPositive)
            v.findViewById<LinearLayout>(R.id.main_layout).setBackgroundResource(R.drawable.positive_background)
            v.findViewById<ImageView>(R.id.imgResult).setImageResource(R.drawable.pos)
        }
        else{
            v.findViewById<TextView>(R.id.txtPositive).text = getString(R.string.coronaNegative)
            v.findViewById<LinearLayout>(R.id.main_layout).setBackgroundResource(R.drawable.negative_background)
            v.findViewById<ImageView>(R.id.imgResult).setImageResource(R.drawable.neg)
        }

        val button = v.findViewById(R.id.btnBack) as Button
        button.setOnClickListener{onButtonPressed()}

        return v
    }

    private fun onButtonPressed() {
        listener?.onResultClosed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentResultInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun testForCorona() : Boolean
    {
        var chance = r.nextFloat()
        return chance < chanceOfHavingCorona

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentResultInteractionListener {
        // TODO: Update argument type and name
        fun onResultClosed()
    }

}
