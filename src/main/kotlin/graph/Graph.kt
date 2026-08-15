package graph

import java.io.File

/**
 * Represents a graph using an adjacency matrix for storage.
 */
class Graph {
    private var adjacencyMatrix: Array<IntArray> = emptyArray()

    val size: Int
        get() = adjacencyMatrix.size

    /**
     * Loads a graph from a file containing its adjacency matrix representation.
     *
     * The first line of the file should specify the size of the graph (number of vertices),
     * and the rest of the lines should represent the adjacency matrix.
     * Each element of the matrix represents the weight of the edge between two vertices
     * (use 0 for no edge). Noninteger or malformed entries will result in exceptions.
     *
     * @param filename The path to the file containing the graph's adjacency matrix.
     * @throws IllegalArgumentException If the file is empty, the size is non-positive,
     * the adjacency matrix is not square, contains invalid values, or does not match the declared size.
     */
    fun loadGraphFromFile(filename: String) {
        val lines = File(filename).readLines().filter { it.isNotBlank() }
        require(lines.isNotEmpty()) { ValidationConstants.GRAPH_FILE_IS_EMPTY }

        val declaredSize = lines.first().trim().toIntOrNull()
            ?: throw IllegalArgumentException(ValidationConstants.FIRST_LINE_MUST_CONTAIN_GRAPH_SIZE)
        require(declaredSize > 0) { ValidationConstants.GRAPH_SIZE_MUST_BE_POSITIVE }
        require(lines.size == declaredSize + 1) {
            ValidationConstants.graphFileMustContainMatrixRows(declaredSize)
        }

        val matrix = lines.drop(1).mapIndexed { index, line ->
            val row = line.trim().split(Regex("\\s+")).map {
                it.toIntOrNull() ?: throw IllegalArgumentException(
                    ValidationConstants.matrixRowContainsNonIntegerValue(index + 1)
                )
            }
            require(row.size == declaredSize) {
                ValidationConstants.matrixRowMustContainValues(index + 1, declaredSize)
            }
            row
        }

        replaceMatrix(validateAndCopy(matrix))
    }

    /**
     * Exports the graph to a `.dot` file for visualization using graphing tools like Graphviz.
     * The generated file represents the graph structure, including vertices and edges,
     * and supports both directed and undirected graphs based on the adjacency matrix.
     *
     * @param filename The path to the output file where the `.dot` representation of the graph will be saved.
     *                 If the file already exists, its contents will be overwritten.
     */
    fun exportGraphToDot(filename: String) {
        ensureLoaded()

        val directed = isDirected()
        val connector = if (directed) "->" else "--"
        val builder = StringBuilder()

        builder.append(if (directed) "digraph" else "graph")
            .append(" G {")
            .appendLine()

        for (vertex in 1..size) {
            builder.append("    ").append(vertex).append(';').appendLine()
        }

        for (from in adjacencyMatrix.indices) {
            for (to in adjacencyMatrix[from].indices) {
                val weight = adjacencyMatrix[from][to]
                if (weight == 0) {
                    continue
                }
                if (!directed && to < from) {
                    continue
                }
                builder.append("    ")
                    .append(from + 1)
                    .append(' ')
                    .append(connector)
                    .append(' ')
                    .append(to + 1)
                    .append(" [label=")
                    .append(weight)
                    .append("];")
                    .appendLine()
            }
        }

        builder.append('}').appendLine()
        File(filename).writeText(builder.toString())
    }

    /**
     * Retrieves the weight of the edge between two vertices in a graph.
     *
     * @param from The starting vertex of the edge. Must be within the valid vertex range of the graph.
     * @param to The ending vertex of the edge. Must be within the valid vertex range of the graph.
     * @return The weight of the edge between the specified vertices. If there is no edge, the weight is typically 0.
     * @throws IllegalArgumentException If either vertex is out of range.
     */
    fun edgeWeight(from: Int, to: Int): Int {
        validateVertex(from)
        validateVertex(to)
        return adjacencyMatrix[from - 1][to - 1]
    }

    /**
     * Retrieves the list of neighbors for the specified vertex in the graph.
     * A neighbor is any vertex that has a direct edge connection to the given vertex.
     *
     * @param vertex The vertex for which to find the neighbors. Must be within the valid range of the graph.
     * @return A list of integers representing the neighboring vertices of the given vertex.
     *         If the vertex has no neighbors, an empty list is returned.
     * @throws IllegalArgumentException If the given vertex is out of the valid vertex range.
     */
    fun neighbors(vertex: Int): List<Int> {
        validateVertex(vertex)
        val neighbors = mutableListOf<Int>()
        for ((index, weight) in adjacencyMatrix[vertex - 1].withIndex()) {
            if (weight > 0) {
                neighbors.add(index + 1)
            }
        }
        return neighbors
    }

    private fun ensureLoaded() {
        require(size > 0) { ValidationConstants.GRAPH_IS_NOT_LOADED }
    }

    private fun validateVertex(vertex: Int) {
        ensureLoaded()
        require(vertex in 1..size) { ValidationConstants.vertexOutOfRange(vertex, size) }
    }

    private fun isDirected(): Boolean {
        for (row in adjacencyMatrix.indices) {
            for (column in adjacencyMatrix[row].indices) {
                if (adjacencyMatrix[row][column] != adjacencyMatrix[column][row]) {
                    return true
                }
            }
        }
        return false
    }

    private fun replaceMatrix(matrix: Array<IntArray>) {
        adjacencyMatrix = matrix
    }

    companion object {
        fun fromAdjacencyMatrix(matrix: List<List<Int>>): Graph {
            val graph = Graph()
            graph.replaceMatrix(validateAndCopy(matrix))
            return graph
        }

        private fun validateAndCopy(matrix: List<List<Int>>): Array<IntArray> {
            require(matrix.isNotEmpty()) { ValidationConstants.ADJACENCY_MATRIX_MUST_NOT_BE_EMPTY }
            val size = matrix.size

            return Array(size) { rowIndex ->
                val row = matrix[rowIndex]
                require(row.size == size) { ValidationConstants.ADJACENCY_MATRIX_MUST_BE_SQUARE }
                IntArray(size) { columnIndex ->
                    val weight = row[columnIndex]
                    require(weight >= 0) { ValidationConstants.EDGE_WEIGHTS_MUST_BE_NON_NEGATIVE }
                    weight
                }
            }
        }
    }
}
