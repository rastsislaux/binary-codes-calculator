abstract class Binary(val bits: IntArray) {

    abstract val abs: Binary

    protected val inversed: IntArray
        get() = IntArray(BITS_COUNT) { if (bits[it] == 0) 1 else 0 }

    open operator fun set(index: Int, value: Int): Binary {
        bits[index] = value
        return this
    }

    operator fun get(index: Int) = bits[index]

    protected fun gt(other: Binary): Boolean {
        for (i in 0 until BITS_COUNT) {
            if (this[i] > other[i]) return true
            if (other[i] > this[i]) return false
        }
        return false
    }

    override fun toString() = bits.joinToString(separator = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Binary) return false
        return bits.contentEquals(other.bits)
    }

    override fun hashCode() = bits.sumOf { it.hashCode() }

    companion object {

        @JvmStatic
        protected fun addRawBinaries(a: IntArray, b: IntArray): IntArray {
            val result = IntArray(BITS_COUNT)
            var carry = 0
            (BITS_COUNT - 1 downTo 0).forEach { i ->
                val sum = a[i] + b[i] + carry
                result[i] = sum % 2
                carry = sum / 2
            }
            return result
        }

        @JvmStatic
        protected fun subRawBinaries(a: IntArray, b: IntArray): IntArray {
            val result = IntArray(BITS_COUNT)
            var borrow = 0
            (BITS_COUNT - 1 downTo 0).forEach { i ->
                val diff = a[i] - b[i] - borrow
                if (diff < 0) {
                    result[i] = diff + 2
                    borrow = 1
                } else {
                    result[i] = diff
                    borrow = 0
                }
            }
            return result
        }

    }

}