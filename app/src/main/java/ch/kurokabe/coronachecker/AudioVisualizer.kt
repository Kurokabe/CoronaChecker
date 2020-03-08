package ch.kurokabe.coronachecker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


class AudioVisualizer@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var values: MutableList<Int> = ArrayList()
    private val maxNumberOfPoint = 250

    private val minValue = -15000.0
    private val maxValue = 15000.0

    init {
        for (i in 1..maxNumberOfPoint)
        {
            addValue(height / 2)
        }
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val paint = Paint()

        paint.color = Color.BLACK

        paint.strokeWidth = 10F

        paint.style = Paint.Style.STROKE

        val path = Path()
        path.moveTo(0f, height.toFloat() / 2)

        var x = 0f
        var step = (width / values.size) * 4

        var first = true
        var i = 0
        while (i < values.size) {
            val y = values[i].toFloat()
            val nextX = x + step
            when {
                first -> {
                    first = false
                    path.moveTo(x, y)
                }
                i < values.size - 1 -> {
                    val nextY = values[i + 1].toFloat()
                    path.quadTo(x, y, nextX, nextY)
                }
                else -> path.lineTo(x, y)
            }
            i += 2
            x = nextX
        }

        canvas.drawPath(path, paint)
    }

    fun addValue(value: Int)
    {
        var newValue = convertValue(value.toDouble())
        values.add(newValue)
//        Log.d("VALUE", height.toString() + " : " + convertValue(value.toDouble()) + " : " + value)
        if (values.size > maxNumberOfPoint)
            values.removeAt(0)

        invalidate()
    }

    private fun convertValue(value: Double) : Int
    {
        var newValue = Utils.clamp(value, minValue, maxValue)

        newValue = Utils.map(newValue, minValue, maxValue, 0.0, height.toDouble())
        return newValue.toInt()
    }
}