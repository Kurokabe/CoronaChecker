package ch.kurokabe.coronachecker

class Utils {
    companion object {
        @JvmStatic
        fun clamp(value: Double, min: Double, max: Double) : Double {
            var newValue = value
            if (newValue < min)
                newValue = min
            else if (newValue > max)
                newValue = max

            return newValue
        }

        @JvmStatic
        fun map(value: Double, min: Double, max: Double, newMin: Double, newMax:Double) : Double
        {
            return ((value-min)/(max-min))*(newMax-newMin)+newMin
        }
    }
}