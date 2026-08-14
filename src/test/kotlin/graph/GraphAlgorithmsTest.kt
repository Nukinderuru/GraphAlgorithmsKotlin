package graph

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File

class GraphAlgorithmsTest {
    private val algorithms = GraphAlgorithms()

    @Test
    fun `breadth first search returns deterministic order`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1, 1, 0),
                listOf(1, 0, 0, 1),
                listOf(1, 0, 0, 1),
                listOf(0, 1, 1, 0)
            )
        )

        assertEquals(listOf(1, 2, 3, 4), algorithms.breadthFirstSearch(graph, 1))
    }

    @Test
    fun `depth first search prefers lower numbered neighbors first`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1, 1, 1, 0),
                listOf(1, 0, 0, 0, 1),
                listOf(1, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0),
                listOf(0, 1, 0, 0, 0)
            )
        )

        assertEquals(listOf(1, 2, 5, 3, 4), algorithms.depthFirstSearch(graph, 1))
    }

    @Test
    fun `search works on directed graph`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1, 1, 0),
                listOf(0, 0, 0, 1),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 1, 0)
            )
        )

        assertEquals(listOf(1, 2, 3, 4), algorithms.breadthFirstSearch(graph, 1))
        assertEquals(listOf(1, 2, 4, 3), algorithms.depthFirstSearch(graph, 1))
    }

    @Test
    fun `search handles loops without revisiting`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(1, 1, 0),
                listOf(0, 0, 1),
                listOf(0, 0, 0)
            )
        )

        assertEquals(listOf(1, 2, 3), algorithms.breadthFirstSearch(graph, 1))
        assertEquals(listOf(1, 2, 3), algorithms.depthFirstSearch(graph, 1))
    }

    @Test
    fun `search on single vertex graph returns that vertex`() {
        val graph = Graph.fromAdjacencyMatrix(listOf(listOf(0)))

        assertEquals(listOf(1), algorithms.breadthFirstSearch(graph, 1))
        assertEquals(listOf(1), algorithms.depthFirstSearch(graph, 1))
    }

    @Test
    fun `search rejects invalid start vertex`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1),
                listOf(1, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.breadthFirstSearch(graph, 0)
        }

        assertEquals("Vertex 0 is out of range 1..2", exception.message)
    }

    @Test
    fun `dijkstra returns weighted shortest path`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 7, 9, 0, 0, 14),
                listOf(7, 0, 10, 15, 0, 0),
                listOf(9, 10, 0, 11, 0, 2),
                listOf(0, 15, 11, 0, 6, 0),
                listOf(0, 0, 0, 6, 0, 9),
                listOf(14, 0, 2, 0, 9, 0)
            )
        )

        assertEquals(20, algorithms.getShortestPathBetweenVertices(graph, 1, 5))
        assertEquals(0, algorithms.getShortestPathBetweenVertices(graph, 3, 3))
    }

    @Test
    fun `dijkstra rejects unreachable destination`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 5, 0),
                listOf(0, 0, 0),
                listOf(0, 0, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.getShortestPathBetweenVertices(graph, 2, 1)
        }

        assertEquals("No path exists between vertices 2 and 1", exception.message)
    }

    @Test
    fun `floyd warshall returns shortest path matrix`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 3, 10, 0),
                listOf(0, 0, 4, 8),
                listOf(0, 0, 0, 2),
                listOf(0, 1, 0, 0)
            )
        )

        val distances = algorithms.getShortestPathsBetweenAllVertices(graph)

        assertArrayEquals(intArrayOf(0, 3, 7, 9), distances[0])
        assertArrayEquals(intArrayOf(GraphAlgorithms.INFINITY, 0, 4, 6), distances[1])
        assertArrayEquals(intArrayOf(GraphAlgorithms.INFINITY, 3, 0, 2), distances[2])
        assertArrayEquals(intArrayOf(GraphAlgorithms.INFINITY, 1, 5, 0), distances[3])
    }

    @Test
    fun `prim returns minimum spanning tree adjacency matrix`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 2, 0, 6, 0),
                listOf(2, 0, 3, 8, 5),
                listOf(0, 3, 0, 0, 7),
                listOf(6, 8, 0, 0, 9),
                listOf(0, 5, 7, 9, 0)
            )
        )

        val tree = algorithms.getLeastSpanningTree(graph)

        assertArrayEquals(intArrayOf(0, 2, 0, 6, 0), tree[0])
        assertArrayEquals(intArrayOf(2, 0, 3, 0, 5), tree[1])
        assertArrayEquals(intArrayOf(0, 3, 0, 0, 0), tree[2])
        assertArrayEquals(intArrayOf(6, 0, 0, 0, 0), tree[3])
        assertArrayEquals(intArrayOf(0, 5, 0, 0, 0), tree[4])
    }

    @Test
    fun `prim rejects directed graph`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 4, 0),
                listOf(0, 0, 2),
                listOf(0, 2, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.getLeastSpanningTree(graph)
        }

        assertEquals(
            ValidationConstants.MINIMUM_SPANNING_TREE_REQUIRES_UNDIRECTED_GRAPH,
            exception.message
        )
    }

    @Test
    fun `prim rejects disconnected graph`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1, 0, 0),
                listOf(1, 0, 0, 0),
                listOf(0, 0, 0, 2),
                listOf(0, 0, 2, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.getLeastSpanningTree(graph)
        }

        assertEquals(
            ValidationConstants.MINIMUM_SPANNING_TREE_REQUIRES_CONNECTED_GRAPH,
            exception.message
        )
    }

    @Test
    fun `ant colony finds valid salesman tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 10, 15, 20),
                listOf(10, 0, 35, 25),
                listOf(15, 35, 0, 30),
                listOf(20, 25, 30, 0)
            )
        )

        val result = algorithms.solveTravelingSalesmanProblem(graph)

        assertEquals(5, result.vertices.size)
        assertEquals(result.vertices.first(), result.vertices.last())
        assertEquals(setOf(1, 2, 3, 4), result.vertices.dropLast(1).toSet())
        assertEquals(80.0, result.distance)
    }

    @Test
    fun `ant colony handles single vertex graph`() {
        val graph = Graph.fromAdjacencyMatrix(listOf(listOf(0)))

        val result = algorithms.solveTravelingSalesmanProblem(graph)

        assertArrayEquals(arrayOf(1, 1), result.vertices)
        assertEquals(0.0, result.distance)
    }

    @Test
    fun `ant colony rejects graph without full tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 5, 0, 0),
                listOf(5, 0, 7, 0),
                listOf(0, 7, 0, 2),
                listOf(0, 0, 2, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.solveTravelingSalesmanProblem(graph)
        }

        assertEquals(
            ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION,
            exception.message
        )
    }

    @Test
    fun `genetic algorithm finds valid salesman tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 10, 15, 20),
                listOf(10, 0, 35, 25),
                listOf(15, 35, 0, 30),
                listOf(20, 25, 30, 0)
            )
        )

        val result = algorithms.solveTravelingSalesmanProblemGenetic(graph)

        assertEquals(5, result.vertices.size)
        assertEquals(result.vertices.first(), result.vertices.last())
        assertEquals(setOf(1, 2, 3, 4), result.vertices.dropLast(1).toSet())
        assertEquals(80.0, result.distance)
    }

    @Test
    fun `genetic algorithm handles single vertex graph`() {
        val graph = Graph.fromAdjacencyMatrix(listOf(listOf(0)))

        val result = algorithms.solveTravelingSalesmanProblemGenetic(graph)

        assertArrayEquals(arrayOf(1, 1), result.vertices)
        assertEquals(0.0, result.distance)
    }

    @Test
    fun `genetic algorithm rejects graph without full tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 5, 0, 0),
                listOf(5, 0, 7, 0),
                listOf(0, 7, 0, 2),
                listOf(0, 0, 2, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.solveTravelingSalesmanProblemGenetic(graph)
        }

        assertEquals(
            ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION,
            exception.message
        )
    }

    @Test
    fun `simulated annealing finds valid salesman tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 10, 15, 20),
                listOf(10, 0, 35, 25),
                listOf(15, 35, 0, 30),
                listOf(20, 25, 30, 0)
            )
        )

        val result = algorithms.solveTravelingSalesmanProblemAnnealing(graph)

        assertEquals(5, result.vertices.size)
        assertEquals(result.vertices.first(), result.vertices.last())
        assertEquals(setOf(1, 2, 3, 4), result.vertices.dropLast(1).toSet())
        assertEquals(80.0, result.distance)
    }

    @Test
    fun `simulated annealing handles single vertex graph`() {
        val graph = Graph.fromAdjacencyMatrix(listOf(listOf(0)))

        val result = algorithms.solveTravelingSalesmanProblemAnnealing(graph)

        assertArrayEquals(arrayOf(1, 1), result.vertices)
        assertEquals(0.0, result.distance)
    }

    @Test
    fun `simulated annealing rejects graph without full tour`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 5, 0, 0),
                listOf(5, 0, 7, 0),
                listOf(0, 7, 0, 2),
                listOf(0, 0, 2, 0)
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            algorithms.solveTravelingSalesmanProblemAnnealing(graph)
        }

        assertEquals(
            ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION,
            exception.message
        )
    }

    @Test
    fun `all tsp solvers reach known optimum on project sample`() {
        val graph = Graph()
        graph.loadGraphFromFile(resourcePath("graphs/tsp_example.txt"))

        assertEquals(253.0, algorithms.solveTravelingSalesmanProblem(graph).distance)
        assertEquals(253.0, algorithms.solveTravelingSalesmanProblemGenetic(graph).distance)
        assertEquals(253.0, algorithms.solveTravelingSalesmanProblemAnnealing(graph).distance)
    }

    private fun resourcePath(path: String): String {
        return File(requireNotNull(javaClass.classLoader.getResource(path)) { "Missing resource $path" }.file)
            .absolutePath
    }
}
