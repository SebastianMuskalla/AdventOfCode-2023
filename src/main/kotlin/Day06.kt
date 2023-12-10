private data class Race(
    val time : Long,
    val distance : Long,
)

private fun evaluate(
    race : Race,
    wait : Long,
) : Long {
    require(wait in 0 .. race.time)
    return (race.time - wait) * wait - race.distance
}

private fun binarySearchMaximum(race : Race) : Long {
    var lower = 0L
    var upper = race.time
    while (lower <= upper) {
        val index = (lower + upper) / 2
        val value = evaluate(race, index)
        when {
            lower == upper ->
                return index

            (index == lower && value >= evaluate(race, index + 1)) ->
                return index

            (index == upper && value >= evaluate(race, index - 1)) ->
                return index

            (value >= evaluate(race, index + 1) && value >= evaluate(race, index - 1)) ->
                return index

            (value <= evaluate(race, index + 1)) ->
                lower = index + 1

            (value <= evaluate(race, index - 1)) ->
                upper = index - 1
        }
    }
    error("unreachable")
}

/**
 * Finds the first value in the closed interval start..end so that monotonicFn(it) is true.
 *
 * Requires monotonicFn to be monotonic on start..end, i.e. its function values look are of the shape F*T*
 */
private fun binarySearchWithPredicate(
    start : Long,
    end : Long,
    monotonicFn : (Long) -> Boolean,
) : Long {
    var lower = start
    var upper = end
    while (upper - lower > 1) {
//        println("$lower $upper")
        val mid = (upper + lower) / 2
        if (monotonicFn(mid)) {
            upper = mid
        } else {
            lower = mid
        }
    }
//    println("Done with $lower $upper")
    return if (monotonicFn(upper)) upper else lower
}

private fun partOne(lines : List<String>) : Long {
    val timeLine = lines[0].removePrefix("Time:").split(" ").mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }
    val distanceLine = lines[1].removePrefix("Distance:").split(" ").mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }

    val races = timeLine.zip(distanceLine).map { (time, distance) -> Race(time, distance) }

    return races.map { race ->
        log("Race $race")
//        (0 .. race.time).forEach { log("Waiting ${it}ms -> ${evaluate(race, it)}") }
        val maximumAt = binarySearchMaximum(race)
        log("Maximum achieved at $maximumAt")
        val firstWin = binarySearchWithPredicate(0, maximumAt) { evaluate(race, it) > 0 }
        log("First >0 entry at $firstWin")
        val lastWin = binarySearchWithPredicate(maximumAt, race.time) { evaluate(race, it) <= 0 } - 1
        log("Last >0 entry at $lastWin")
        val numberOfWaysToWinTheRace = lastWin - firstWin + 1
        log("Number of ways to win the race $numberOfWaysToWinTheRace")

        numberOfWaysToWinTheRace
    }.fold(1L) { a, b -> a * b }
}

private fun partTwo(lines : List<String>) : Long {
    val time = lines[0].removePrefix("Time:").filter { it != ' ' }.toLong()
    val distance = lines[1].removePrefix("Distance:").filter { it != ' ' }.toLong()

    val race = Race(time, distance)

    log("Race $race")
//    (0 .. race.time).forEach { log("Waiting ${it}ms -> ${evaluate(race, it)}") }
    val maximumAt = binarySearchMaximum(race)
    log("Maximum achieved at $maximumAt")
    val firstWin = binarySearchWithPredicate(0, maximumAt) { evaluate(race, it) > 0 }
    log("First >0 entry at $firstWin")
    val lastWin = binarySearchWithPredicate(maximumAt, race.time) { evaluate(race, it) <= 0 } - 1
    log("Last >0 entry at $lastWin")
    val numberOfWaysToWinTheRace = lastWin - firstWin + 1
    log("Number of ways to win the race $numberOfWaysToWinTheRace")

    return numberOfWaysToWinTheRace
}

private fun main() {
    aoc("06-example.txt", "288") { partOne(it) }
    aoc("06-input.txt", "211904") { partOne(it) }
    aoc("06-example.txt", "71503") { partTwo(it) }
    aoc("06-input.txt", "43364472") { partTwo(it) }
}
