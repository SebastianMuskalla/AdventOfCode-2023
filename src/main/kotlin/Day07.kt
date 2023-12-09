private fun partOne(lines : List<String>) : Long {

    val hands = lines.map { line ->
        val splits = line.split(' ')
        splits[0] to splits[1].toLong()
    }

    fun rank(hand : String) : Int {
        require(hand.length == 5)

        val occurrences = mutableMapOf<Char, Int>()

        hand.toCharArray().forEach { card ->
            occurrences[card] = occurrences.getOrDefault(card, 0) + 1
        }
        val swag = occurrences.values.sortedDescending()

        return when {
            swag[0] == 5 -> 7 // 5 of a kind
            swag[0] == 4 -> 6 // 4 of a kind
            swag[0] == 3 && swag[1] == 2 -> 5 // full house
            swag[0] == 3 -> 4 // 3 of a kind
            swag[0] == 2 && swag[1] == 2 -> 3 // two pairs
            swag[0] == 2 -> 2 // one pair
            else -> 1 // high card
        }
    }

    val cardOrder = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

    fun lexicographicCompare(
        one : Pair<String, *>,
        two : Pair<String, *>,
    ) : Int {
        val handOne = one.first
        val handTwo = two.first
        require(handOne.length == 5 && handTwo.length == 5)

        for (i in 0 ..< 5) {
            val orderOne = cardOrder.indexOf(handOne[i])
            val orderTwo = cardOrder.indexOf(handTwo[i])
            require(orderOne != -1)
            require(orderTwo != -1)
            if (orderOne < orderTwo) return -1
            if (orderTwo < orderOne) return 1
        }
        return 0
    }

    val handComparator = compareBy<Pair<String, *>> { rank(it.first) }.thenComparator(::lexicographicCompare)

    return hands.sortedWith(handComparator).also { hand ->
        log(hand.joinToString(prefix = "\n", separator = "\n") {
            "${it.first} rank ${rank(it.first)} bid ${it.second}"
        })
    }.mapIndexed { i, pair -> (i + 1) to pair.second }.sumOf { it.first * it.second }
}

private fun partTwo(lines : List<String>) : Long {

    val hands = lines.map { line ->
        val splits = line.split(' ')
        splits[0] to splits[1].toLong()
    }

    fun rank(hand : String) : Int {
        require(hand.length == 5)

        val occurrences = mutableMapOf<Char, Int>()

        hand.toCharArray().forEach { card ->
            occurrences[card] = occurrences.getOrDefault(card, 0) + 1
        }
        val jokers = occurrences.getOrDefault('J', 0)
        val swag = occurrences.minus('J').values.sortedDescending()

        return when {
            jokers == 5 -> 7 // also 5 of a kind
            swag[0] + jokers == 5 -> 7 // 5 of a kind
            swag[0] + jokers == 4 -> 6 // 4 of a kind
            swag[0] + jokers == 3 && swag[1] == 2 -> 5 // full house
            swag[0] + jokers == 3 -> 4 // 3 of a kind
            swag[0] + jokers == 2 && swag[1] == 2 -> 3 // two pairs
            swag[0] + jokers == 2 -> 2 // one pair
            else -> 1 // high card
        }
    }

    val cardOrder = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    fun lexicographicCompare(
        one : Pair<String, *>,
        two : Pair<String, *>,
    ) : Int {
        val handOne = one.first
        val handTwo = two.first
        require(handOne.length == 5 && handTwo.length == 5)

        for (i in 0 ..< 5) {
            val orderOne = cardOrder.indexOf(handOne[i])
            val orderTwo = cardOrder.indexOf(handTwo[i])
            require(orderOne != -1)
            require(orderTwo != -1)
            if (orderOne < orderTwo) return -1
            if (orderTwo < orderOne) return 1
        }
        return 0
    }

    val handComparator = compareBy<Pair<String, *>> { rank(it.first) }.thenComparator(::lexicographicCompare)

    return hands.sortedWith(handComparator).also { hand ->
        log(hand.joinToString(prefix = "\n", separator = "\n") {
            "${it.first} rank ${rank(it.first)} bid ${it.second}"
        })
    }.mapIndexed { i, pair -> (i + 1) to pair.second }.sumOf { it.first * it.second }
}

private fun main() {
    aoc("07-example.txt", 6440L) { partOne(it) }
    aoc("07-input.txt", 248105065L) { partOne(it) }
    aoc("07-example.txt", 5905L) { partTwo(it) }
    aoc("07-input.txt", 249515436L) { partTwo(it) }
}
