// Do not delete this line
package converter

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun splitFractional(value: BigDecimal): Pair<BigDecimal, BigDecimal> {
    val integerPart: BigDecimal = value.setScale(0, RoundingMode.DOWN)
    val fractionalPart: BigDecimal = value.subtract(integerPart)
    return Pair(integerPart, fractionalPart)
}

fun getDigit(value: Int): String {
    if (value > 9) {
        return ('A' + (value - 10)).toString()
    }
    return value.toString()
}

fun convertToBase(num: BigDecimal, base: Int): String {
    val (integerPart, fractionalPart) = splitFractional(num)
    var intResult: String = ""
    var next: BigDecimal = integerPart
    val bigIntBase = BigDecimal.valueOf(base.toLong())
    do {
        val (quotient, remainder) = next.divideAndRemainder(bigIntBase)
        intResult = getDigit(remainder.toInt()) + intResult
        next = quotient
    } while (next > BigDecimal.ZERO)
    if (num.toString().contains('.')) {
        var fractionResult = ""
        next = fractionalPart
        do {
            val mult = bigIntBase * next
            val (integer, remainder) = splitFractional(mult)
            fractionResult += getDigit(integer.toInt())
            next = remainder
        } while (fractionResult.length < 5)
        return "$intResult.$fractionResult"
    }
    return intResult
}

fun convertToDecimal(num: String, base: Int): BigDecimal {
    val splitNum = num.split(".")
    val bigIntBase: BigDecimal = BigDecimal.valueOf(base.toLong())
    val integerPart: BigDecimal =
        splitNum[0].toList().map { BigDecimal.valueOf(it.digitToInt(radix = base).toLong()) }.reversed()
            .foldIndexed(BigDecimal.ZERO) { index, acc, value -> acc + value * bigIntBase.pow(index) }
    if (splitNum.size > 1) {
        val fractionalPart = splitNum[1]
            .toList()
            .map { BigDecimal.valueOf(it.digitToInt(radix = base).toLong()) }
            .foldIndexed(BigDecimal.ZERO) { index, acc, value -> acc + value * bigIntBase.pow((-index - 1), MathContext.DECIMAL32) }
        return (integerPart + fractionalPart)
    }
    return integerPart
}

fun convertBetweenBases(source: Int, target: Int) {
    do {
        print("Enter number in base $source to convert to base $target (To go back type /back)")
        val input = readln()
        when (input) {
            "/back" -> {}
            else -> {
                if (target == 10) {
                    println("Conversion result: ${convertToDecimal(input, source).setScale(5, RoundingMode.HALF_UP)}")
                } else if (source == 10) {
                    val num = BigDecimal(input)
                    println("Conversion result: ${convertToBase(num, target)}")
                } else {
                    val num = convertToDecimal(input, source)
                    println("Conversion result: ${convertToBase(num, target)}")
                }
            }
        }
    } while (input != "/back")
}

fun main() {
    do {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val input = readln()
        when (input) {
            "/exit" -> {}
            else -> {
                val (source, target) = input.split(" ").toList().map { it.toInt() }
                convertBetweenBases(source, target)
            }
        }
    } while (input != "/exit")
}