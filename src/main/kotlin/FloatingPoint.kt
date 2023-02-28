class FloatingPoint(val bits: IntArray) {

    override fun toString() = bits.joinToString("")

    operator fun plus(other: FloatingPoint) = FloatingPoint(sumDoubleFloatingPointBinaries(bits, other.bits))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FloatingPoint) return false
        return bits.contentEquals(other.bits)
    }

    override fun hashCode() = bits.sumOf { it.hashCode() }

}

val Float.fp: FloatingPoint
    get() = FloatingPoint(floatToBinaryArray(this))

fun floatToBinaryArray(value: Float): IntArray {
    val result = IntArray(BITS_COUNT) { 0 }
    for (i in result.indices) result[i] = (value.toBits() ushr i) and 0x1
    return result.reversedArray()
}

fun getIEEE754Components(value: IntArray): Triple<DirectBinary, DirectBinary, DirectBinary> {
    val sign = DirectBinary(intArrayOf(*IntArray(BITS_COUNT - SIGN_BITS_COUNT) { 0 },
                                       value[0] and 0x1))
    val exponent = DirectBinary(intArrayOf(*IntArray(BITS_COUNT - EXP_BITS_COUNT) { 0 },
                                           *value.copyOfRange(1, 9)))
    val mantissa = DirectBinary(intArrayOf(*IntArray(BITS_COUNT - MANTISSA_BITS_COUNT) { 0 },
                                *value.copyOfRange(9, BITS_COUNT))).set(8, 1)

    return Triple(sign, exponent, mantissa)
}

infix fun IntArray.shr(shift: Int): IntArray {
    val result = IntArray(this.size)
    for (i in indices.reversed()) {
        result[i] = if (i - shift !in indices) 0 else this[i - shift]
    }
    return result
}

fun sumDoubleFloatingPointBinaries(x: IntArray, y: IntArray): IntArray {
    var (_, exp1, man1) = getIEEE754Components(x)
    var (_, exp2, man2) = getIEEE754Components(y)

    val diff = (exp1 - exp2).set(0, 0)
    var exp3: DirectBinary

    if (exp1 < exp2) {
        man1 = DirectBinary(man1.bits shr diff.int)
        exp3 = exp2
    } else {
        man2 = DirectBinary(man2.bits shr diff.int)
        exp3 = exp1
    }

    var man3 = man1 + man2
    while (man3.bits.indexOfFirst { it == 1 } in 0 until SIGN_BITS_COUNT + EXP_BITS_COUNT - 1) {
        man3 = DirectBinary(man3.bits shr 1)
        exp3 += 1.db
    }

    return intArrayOf(
        0,
        *exp3.bits.copyOfRange(SIGN_BITS_COUNT + MANTISSA_BITS_COUNT, BITS_COUNT),
        *man3.bits.copyOfRange(SIGN_BITS_COUNT + EXP_BITS_COUNT, BITS_COUNT)
    )
}
