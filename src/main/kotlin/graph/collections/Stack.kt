package graph.collections

import graph.ValidationConstants

class Stack<T> {
    private val items = ArrayDeque<T>()

    fun push(value: T) {
        items.addLast(value)
    }

    fun pop(): T {
        check(items.isNotEmpty()) { ValidationConstants.STACK_IS_EMPTY }
        return items.removeLast()
    }

    fun top(): T {
        check(items.isNotEmpty()) { ValidationConstants.STACK_IS_EMPTY }
        return items.last()
    }

    fun isEmpty(): Boolean = items.isEmpty()
}
