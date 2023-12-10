import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTimedValue

const val debug = true

const val enableChecks = true

fun aoc(
    fileName : String,
    check : String? = null,
    fn : (List<String>) -> Any?,
) {
    val lines = Path("src/main/resources/$fileName")
        .readLines()

    val timedSolution = measureTimedValue { fn(lines) }

    println(
        """
            
            ==========================
            ${timedSolution.value}
            computed in ${timedSolution.duration}
            ==========================
            
        """.trimIndent()
    )

    if (enableChecks && check != null && timedSolution.value.toString() != check) {
        throw AssertionError("Computed solution ${timedSolution.value} does not coincide with expected solution $check")
    }

}

fun log(message : Any?) = if (debug) println("DEBUG: $message") else Unit

inline fun <T> Iterable<T>.sumOfIndexed(selector : (Int, T) -> Long) : Long {
    var sum : Long = 0.toLong()
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}
