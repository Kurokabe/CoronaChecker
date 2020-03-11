package ch.kurokabe.coronachecker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.R.attr.path
import android.R.attr.y
import android.R.attr.x
import java.nio.file.Files.size




class AudioVisualizer@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var values: MutableList<Int> = ArrayList()
    private val maxNumberOfPoint = 1000

    private val minValue = -25000.0
    private val maxValue = 25000.0

    init {
        for (i in 1..maxNumberOfPoint)
        {
            addValue(height / 2)
        }
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.BLACK

        paint.strokeWidth = 10F

        paint.style = Paint.Style.STROKE

        val path = Path()
//        path.moveTo(0f, height.toFloat() / 2)
//
//        var x = 0f
//        var step = (width / values.size) * 4
//
//        var first = true
//        var i = 0
//        while (i < values.size) {
//            val y = values[i].toFloat()
//            val nextX = x + step
//            when {
//                first -> {
//                    first = false
//                    path.moveTo(x, y)
//                }
//                i < values.size - 1 -> {
//                    val nextY = values[i + 1].toFloat()
//                    path.quadTo(x, y, nextX, nextY)
//                }
//                else -> path.lineTo(x, y)
//            }
//            i += 2
//            x = nextX
//        }
//
//        canvas.drawPath(path, paint)

        var x = 0f
        var step = (width / values.size) * 4

        var points: MutableList<Point> = ArrayList()
        var i = 0
        while (i < values.size) {
            var p = Point()
            p.x = x
            p.y = values[i].toFloat()
            points.add(p)
            x += step
            i++
        }


        if (points.size > 1) {
            for (i in points.size - 2 until points.size) {
                if (i >= 0) {
                    val point= points[i]

                    if (i == 0) {
                        val next = points[i + 1]
                        point.dx = (next.x - point.x) / 3
                        point.dy = (next.y - point.y) / 3
                    } else if (i == points.size - 1) {
                        val prev = points.get(i - 1)
                        point.dx = (point.x - prev.x) / 3
                        point.dy = (point.y - prev.y) / 3
                    } else {
                        val next = points.get(i + 1)
                        val prev = points.get(i - 1)
                        point.dx = (next.x - prev.x) / 3
                        point.dy = (next.y - prev.y) / 3
                    }
                }
            }
        }

        var first = true
        for (i in 0 until points.size) {
            val point = points[i]
            if (first) {
                first = false
                path.moveTo(point.x, point.y)
            } else {
                val prev = points[i-1]
                path.cubicTo(
                    prev.x + prev.dx,
                    prev.y + prev.dy,
                    point.x - point.dx,
                    point.y - point.dy,
                    point.x,
                    point.y
                )
            }
        }
        canvas.drawPath(path, paint)
    }

    class Point
    {
        var x = 0.0f
        var y = 0.0f
        var dx = 0.0f
        var dy = 0.0f

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