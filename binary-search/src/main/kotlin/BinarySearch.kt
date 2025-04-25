object BinarySearch {
    fun search(list: List<Int>, item: Int): Int {

        val sortedList = list.sorted().mapIndexed { index, i -> i to index }

        return recursiveSearch(sortedList, item)
    }

    fun recursiveSearch(list: List<Pair<Int, Int>>, item: Int): Int {

        println("Looking for $item")
        println("in this list $list")

        if (list.isEmpty()) throw NoSuchElementException()

        val middleIndex = list.size / 2

        return if (list[middleIndex].first == item) list[middleIndex].second
        else if (list.size == 1 && list[middleIndex].first != item) throw NoSuchElementException()
        else if (list[middleIndex].first > item) recursiveSearch(list.take(middleIndex), item)
        else if (list[middleIndex].first < item) recursiveSearch(list.takeLast(list.size - middleIndex), item)
        else 0
    }
}
