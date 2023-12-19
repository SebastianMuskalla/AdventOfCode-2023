private fun partOne(lines : List<String>) : Long {

    val splitLine = lines.indexOfFirst { it == "" }

    val parts = lines.subList(splitLine + 1, lines.size).map { partString ->
        buildMap {
            partString
                .removeSurrounding("{", "}")
                .split(',')
                .map { propertyString ->
                    val splits = propertyString.split('=')
                    put(splits[0], splits[1].toLong())
                }
        }
    }

    val automaton = buildMap {
        lines.subList(0, splitLine).forEach {
            val splits = it.split('{')
            val state = splits[0]
            val transitions = splits[1].removeSuffix("}").split(',')
            val final = transitions[transitions.lastIndex] // state we go to if no conditions match
            val other = transitions.subList(0, transitions.lastIndex).map { transition ->
                val splitsT = transition.split(':')
                val target = splitsT[1]
                val condition = splitsT[0].split('>', '<')
                val property = condition[0]
                val bound = condition[1].toLong()
                val lessThan = '<' in splitsT[0]

                Triple(property, lessThan, bound) to target
            }
            val rules = other to final
            put(state, rules)
        }
    }

    fun simulate(part : Map<String, Long>) : Boolean {

        var state = "in"

        while (true) {

            log("State for part $part is $state")

            if (state == "A") return true
            if (state == "R") return false

            val (rules, finalState) = automaton[state]!!

            var done = false

            rules.forEach { (condition, target) ->

                // hack because continue inside lambda is not supported
                if (done) return@forEach

                val (property, lessThan, bound) = condition

                if (lessThan) {
                    if (part[property]!! < bound) {
                        println("Comparison $property (${part[property]}) < $bound success!")
                        state = target
                        done = true
                        return@forEach
                    }
                    println("Comparison $property (${part[property]}) < $bound failure!")
                } else {
                    if (part[property]!! > bound) {
                        println("Comparison $property (${part[property]}) > $bound success!")
                        state = target
                        done = true
                        return@forEach
                    }
                    println("Comparison $property (${part[property]}) > $bound failure!")
                }
            }

            if (!done) {
                state = finalState
            }
        }
    }

    return parts
        .filter { simulate(it) }
        .sumOf { it.values.sum() }

}

private fun IntRange.simpleIntersect(other : IntRange) : IntRange {
    if (other.first > this.last || this.first > other.last)
        return IntRange.EMPTY

    return maxOf(this.first, other.first) .. minOf(this.last, other.last)
}

private data class Ranges(
    val x : IntRange,
    val m : IntRange,
    val a : IntRange,
    val s : IntRange,
) {
    val isEmpty : Boolean
        get() = x.isEmpty() || m.isEmpty() || a.isEmpty() || s.isEmpty()

    fun positive(condition : Triple<String, Boolean, Int>) : Ranges {
        if (isEmpty) return EMPTY

        val (property, lessThan, bound) = condition

        val range = if (lessThan) {
            1 ..< bound
        } else {
            bound + 1 .. 4000
        }

        return when (property) {
            "x" -> Ranges(x.simpleIntersect(range), m, a, s)
            "m" -> Ranges(x, m.simpleIntersect(range), a, s)
            "a" -> Ranges(x, m, a.simpleIntersect(range), s)
            "s" -> Ranges(x, m, a, s.simpleIntersect(range))
            else -> error("Unknown property $property in $condition")
        }
    }

    fun negative(condition : Triple<String, Boolean, Int>) : Ranges {
        if (isEmpty) return EMPTY

        val (property, lessThan, bound) = condition

        val range = if (lessThan) {
            bound .. 4000
        } else {
            1 .. bound
        }

        return when (property) {
            "x" -> Ranges(x.simpleIntersect(range), m, a, s)
            "m" -> Ranges(x, m.simpleIntersect(range), a, s)
            "a" -> Ranges(x, m, a.simpleIntersect(range), s)
            "s" -> Ranges(x, m, a, s.simpleIntersect(range))
            else -> error("Unknown property $property in $condition")
        }
    }

    companion object {
        val EMPTY = Ranges(IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY)
    }
}

private fun partTwo(lines : List<String>) : Long {

    val splitLine = lines.indexOfFirst { it == "" }

    val automaton = buildMap {
        lines.subList(0, splitLine).forEach {
            val splits = it.split('{')
            val state = splits[0]
            val transitions = splits[1].removeSuffix("}").split(',')
            val final = transitions[transitions.lastIndex] // state we go to if no conditions match
            val other = transitions.subList(0, transitions.lastIndex).map { transition ->
                val splitsT = transition.split(':')
                val target = splitsT[1]
                val condition = splitsT[0].split('>', '<')
                val property = condition[0]
                val bound = condition[1].toInt()
                val lessThan = '<' in splitsT[0]

                Triple(property, lessThan, bound) to target
            }
            val rules = other to final
            put(state, rules)
        }
    }

    fun acceptedFor(
        ranges : Ranges,
        state : String,
    ) : List<Ranges> {

        val (rules, finalState) = automaton[state]!!

        val previousConditions = mutableListOf<Triple<String, Boolean, Int>>()

        val intermediary = rules.flatMap { (condition, target) ->

            var newRanges = ranges
            previousConditions.forEach { previousCondition ->
                newRanges = newRanges.negative(previousCondition)
            }
            newRanges = newRanges.positive(condition)
            previousConditions.add(condition)

            when (target) {
                "R" -> listOf()
                "A" -> listOf(newRanges)
                else -> acceptedFor(newRanges, target)
            }
        }

        var finalRange = ranges
        previousConditions.forEach { previousCondition ->
            finalRange = finalRange.negative(previousCondition)
        }

        val final = when (finalState) {
            "R" -> listOf()
            "A" -> listOf(finalRange)
            else -> acceptedFor(finalRange, finalState)
        }

        return intermediary + final
    }

    val initial = Ranges(1 .. 4000, 1 .. 4000, 1 .. 4000, 1 .. 4000)

    val result = acceptedFor(initial, "in")

    log(result)

    // relies on the entries of result having no overlaps
    return result.sumOf {
        if (it.isEmpty) return@sumOf 0L

        (it.x.last - it.x.first + 1).toLong() * (it.m.last - it.m.first + 1).toLong() * (it.a.last - it.a.first + 1).toLong() * (it.s.last - it.s.first + 1).toLong()
    }
}

private fun main() {
    aoc("19-example.txt", "19114") { partOne(it) }
    aoc("19-input.txt", "382440") { partOne(it) }
    aoc("19-example.txt", "167409079868000") { partTwo(it) }
    aoc("19-input.txt", "136394217540123") { partTwo(it) }
}
