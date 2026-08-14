package graph

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class GraphTest {
    @Test
    fun `builds graph from adjacency matrix`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 1, 0),
                listOf(1, 0, 2),
                listOf(0, 2, 0)
            )
        )

        assertEquals(3, graph.size)
        assertEquals(2, graph.edgeWeight(2, 3))
        assertEquals(listOf(1, 3), graph.neighbors(2))
    }

    @Test
    fun `loads graph from file`() {
        val graph = Graph()

        graph.loadGraphFromFile(resourcePath("graphs/undirected_graph.txt"))

        assertEquals(4, graph.size)
        assertEquals(listOf(2, 3), graph.neighbors(1))
        assertEquals(1, graph.edgeWeight(2, 4))
    }

    @Test
    fun `rejects malformed file row width`() {
        val graph = Graph()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            graph.loadGraphFromFile(resourcePath("graphs/invalid_row_width.txt"))
        }

        assertEquals("Matrix row 1 must contain 3 values", exception.message)
    }

    @Test
    fun `rejects empty adjacency matrix`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Graph.fromAdjacencyMatrix(emptyList())
        }

        assertEquals("Adjacency matrix must not be empty", exception.message)
    }

    @Test
    fun `rejects non square adjacency matrix`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Graph.fromAdjacencyMatrix(
                listOf(
                    listOf(0, 1),
                    listOf(1, 0, 1)
                )
            )
        }

        assertEquals("Adjacency matrix must be square", exception.message)
    }

    @Test
    fun `exports undirected graph to dot`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 7),
                listOf(7, 0)
            )
        )
        val tempFile = createTempDirectory().resolve("graph.dot").toFile()

        graph.exportGraphToDot(tempFile.absolutePath)

        val content = tempFile.readText()
        assertEquals(
            "graph G {\n    1;\n    2;\n    1 -- 2 [label=7];\n}\n",
            content
        )
    }

    @Test
    fun `exports directed graph to dot`() {
        val graph = Graph.fromAdjacencyMatrix(
            listOf(
                listOf(0, 5),
                listOf(0, 0)
            )
        )
        val tempFile = createTempDirectory().resolve("digraph.dot").toFile()

        graph.exportGraphToDot(tempFile.absolutePath)

        val content = tempFile.readText()
        assertEquals(
            "digraph G {\n    1;\n    2;\n    1 -> 2 [label=5];\n}\n",
            content
        )
    }

    private fun resourcePath(path: String): String {
        return File(requireNotNull(javaClass.classLoader.getResource(path)) { "Missing resource $path" }.file)
            .absolutePath
    }
}
