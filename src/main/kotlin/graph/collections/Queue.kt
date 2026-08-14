package graph.collections

import graph.ValidationConstants

class Queue<T> {
    private val items = ArrayDeque<T>()

    fun push(value: T) {
        items.addLast(value)
    }

    fun pop(): T {
        check(items.isNotEmpty()) { ValidationConstants.QUEUE_IS_EMPTY }
        return items.removeFirst()
    }

    fun front(): T {
        check(items.isNotEmpty()) { ValidationConstants.QUEUE_IS_EMPTY }
        return items.first()
    }

    fun back(): T {
        check(items.isNotEmpty()) { ValidationConstants.QUEUE_IS_EMPTY }
        return items.last()
    }

    fun isEmpty(): Boolean = items.isEmpty()
}
