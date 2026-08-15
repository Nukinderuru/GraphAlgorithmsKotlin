package graph.collections

import graph.ValidationConstants

class PriorityQueue<T> {
    data class Entry<T>(
        val value: T,
        val priority: Int
    )

    private val heap = mutableListOf<Entry<T>>()

    fun push(value: T, priority: Int) {
        heap.add(Entry(value, priority))
        siftUp(heap.lastIndex)
    }

    fun pop(): Entry<T> {
        check(heap.isNotEmpty()) { ValidationConstants.PRIORITY_QUEUE_IS_EMPTY }
        val result = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        if (heap.isNotEmpty()) {
            heap[0] = last
            siftDown()
        }
        return result
    }

    fun peek(): Entry<T> {
        check(heap.isNotEmpty()) { ValidationConstants.PRIORITY_QUEUE_IS_EMPTY }
        return heap.first()
    }

    fun isEmpty(): Boolean = heap.isEmpty()

    private fun siftUp(startIndex: Int) {
        var index = startIndex
        while (index > 0) {
            val parentIndex = (index - 1) / 2
            if (heap[parentIndex].priority <= heap[index].priority) {
                return
            }
            swap(parentIndex, index)
            index = parentIndex
        }
    }

    private fun siftDown() {
        var index = 0
        while (true) {
            val leftChildIndex = index * 2 + 1
            val rightChildIndex = index * 2 + 2
            var smallestIndex = index

            if (leftChildIndex < heap.size && heap[leftChildIndex].priority < heap[smallestIndex].priority) {
                smallestIndex = leftChildIndex
            }
            if (rightChildIndex < heap.size && heap[rightChildIndex].priority < heap[smallestIndex].priority) {
                smallestIndex = rightChildIndex
            }
            if (smallestIndex == index) {
                return
            }

            swap(index, smallestIndex)
            index = smallestIndex
        }
    }

    private fun swap(firstIndex: Int, secondIndex: Int) {
        val temporary = heap[firstIndex]
        heap[firstIndex] = heap[secondIndex]
        heap[secondIndex] = temporary
    }
}
