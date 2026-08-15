package graph

import graph.collections.Queue
import graph.collections.PriorityQueue
import graph.collections.Stack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CollectionsTest {
    @Test
    fun `stack behaves as lifo`() {
        val stack = Stack<Int>()

        stack.push(1)
        stack.push(2)

        assertEquals(2, stack.top())
        assertEquals(2, stack.pop())
        assertEquals(1, stack.pop())
        assertEquals(true, stack.isEmpty())
    }

    @Test
    fun `stack rejects pop when empty`() {
        val stack = Stack<Int>()

        val exception = assertThrows(IllegalStateException::class.java) {
            stack.pop()
        }

        assertEquals("Stack is empty", exception.message)
    }

    @Test
    fun `queue behaves as fifo`() {
        val queue = Queue<Int>()

        queue.push(1)
        queue.push(2)
        queue.push(3)

        assertEquals(1, queue.front())
        assertEquals(3, queue.back())
        assertEquals(1, queue.pop())
        assertEquals(2, queue.pop())
        assertEquals(3, queue.pop())
        assertEquals(true, queue.isEmpty())
    }

    @Test
    fun `queue rejects front when empty`() {
        val queue = Queue<Int>()

        val exception = assertThrows(IllegalStateException::class.java) {
            queue.front()
        }

        assertEquals("Queue is empty", exception.message)
    }

    @Test
    fun `priority queue returns lowest priority first`() {
        val priorityQueue = PriorityQueue<String>()

        priorityQueue.push("medium", 20)
        priorityQueue.push("high", 10)
        priorityQueue.push("low", 30)

        assertEquals("high", priorityQueue.peek().value)
        assertEquals("high", priorityQueue.pop().value)
        assertEquals("medium", priorityQueue.pop().value)
        assertEquals("low", priorityQueue.pop().value)
        assertEquals(true, priorityQueue.isEmpty())
    }

    @Test
    fun `priority queue rejects pop when empty`() {
        val priorityQueue = PriorityQueue<Int>()

        val exception = assertThrows(IllegalStateException::class.java) {
            priorityQueue.pop()
        }

        assertEquals("Priority queue is empty", exception.message)
    }
}
