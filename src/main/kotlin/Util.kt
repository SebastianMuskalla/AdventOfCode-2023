import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTimedValue

fun aoc(
    fileName : String,
    check : Any? = null,
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

    if (check != null && timedSolution.value != check) {
        throw AssertionError("Computed solution ${timedSolution.value} does not coincide with expected solution $check")
    }

}

const val debug = true
fun log(message : Any?) = if (debug) println("DEBUG: $message") else Unit
