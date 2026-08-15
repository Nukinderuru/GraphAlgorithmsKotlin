package graph

object ValidationConstants {
    const val GRAPH_FILE_IS_EMPTY = "Graph file is empty"
    const val FIRST_LINE_MUST_CONTAIN_GRAPH_SIZE = "First line must contain the graph size"
    const val GRAPH_SIZE_MUST_BE_POSITIVE = "Graph size must be positive"
    const val GRAPH_IS_NOT_LOADED = "Graph is not loaded"
    const val ADJACENCY_MATRIX_MUST_NOT_BE_EMPTY = "Adjacency matrix must not be empty"
    const val ADJACENCY_MATRIX_MUST_BE_SQUARE = "Adjacency matrix must be square"
    const val EDGE_WEIGHTS_MUST_BE_NON_NEGATIVE = "Edge weights must be non-negative"
    const val STACK_IS_EMPTY = "Stack is empty"
    const val QUEUE_IS_EMPTY = "Queue is empty"
    const val PRIORITY_QUEUE_IS_EMPTY = "Priority queue is empty"

    fun graphFileMustContainMatrixRows(size: Int): String =
        "Graph file must contain $size matrix rows"

    fun matrixRowContainsNonIntegerValue(rowNumber: Int): String =
        "Matrix row $rowNumber contains a non-integer value"

    fun matrixRowMustContainValues(rowNumber: Int, size: Int): String =
        "Matrix row $rowNumber must contain $size values"

    fun vertexOutOfRange(vertex: Int, size: Int): String =
        "Vertex $vertex is out of range 1..$size"

    fun noPathBetweenVertices(startVertex: Int, endVertex: Int): String =
        "No path exists between vertices $startVertex and $endVertex"

    const val MINIMUM_SPANNING_TREE_REQUIRES_UNDIRECTED_GRAPH =
        "Minimum spanning tree requires an undirected graph"

    const val MINIMUM_SPANNING_TREE_REQUIRES_CONNECTED_GRAPH =
        "Minimum spanning tree requires a connected graph"

    const val TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION =
        "Traveling salesman problem has no solution for this graph"
}
