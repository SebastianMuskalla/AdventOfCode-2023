private fun hash(step : String) : Int {
    var current = 0
    step.forEach { char ->
        current += char.code
        current *= 17
        current %= 256
    }
    return current
}

private fun partOne(lines : List<String>) : Long {

    val input = lines.joinToString(separator = "").split(',')

    return input.sumOf { step ->
        hash(step).toLong()
    }
}

private fun partTwo(lines : List<String>) : Long {

    val boxes = mutableListOf<MutableList<Pair<String, Int>>>().apply {
        repeat(256) { add(mutableListOf()) }
    }

    val input = lines.joinToString(separator = "").split(',')

    input.forEach { step ->

        val splits = step.split('=', '-')
        val label = splits[0]
        val operationRemove = step.contains('-')
        val box = hash(label)

        if (operationRemove) {
            boxes[box].removeAll { it.first == label }
        } else {
            val number = splits[1].toInt()
            val existingIndex = boxes[box].indexOfFirst { it.first == label }
            if (existingIndex != -1) {
                boxes[box][existingIndex] = label to number
            } else {
                boxes[box].add(label to number)
            }
        }
    }

    println(boxes.joinToString(separator = "\n"))

    return boxes.sumOfIndexed { boxIndex, lenses ->
        lenses.sumOfIndexed { lensIndex, (_, focalLength) ->
            ((1 + boxIndex) * (1 + lensIndex) * focalLength).toLong()
        }
    }
}

private fun main() {
    aoc("15-example.txt", "1320") { partOne(it) }
    aoc("15-input.txt", "508552") { partOne(it) }
    aoc("15-example.txt", "145") { partTwo(it) }
    aoc("15-input.txt", "265462") { partTwo(it) }
}
