package console

import graph.Graph
import graph.GraphAlgorithms
import graph.GraphAlgorithms.Companion.INFINITY
import graph.HeuristicTsmResult
import kotlin.system.measureNanoTime

fun main() {
    ConsoleApp().run()
}

class ConsoleApp(
    private val graph: Graph = Graph(),
    private val algorithms: GraphAlgorithms = GraphAlgorithms()
) {
    private val input = System.`in`.bufferedReader()

    fun run() {
        try {
            while (true) {
                printMenu()
                when (prompt("Choose an option")) {
                    "1" -> loadGraph()
                    "2" -> runBreadthFirstSearch()
                    "3" -> runDepthFirstSearch()
                    "4" -> runShortestPathBetweenVertices()
                    "5" -> runShortestPathsBetweenAllVertices()
                    "6" -> runLeastSpanningTree()
                    "7" -> runTravelingSalesmanProblem()
                    "8" -> runTravelingSalesmanProblemGenetic()
                    "9" -> runTravelingSalesmanProblemAnnealing()
                    "10" -> runTravelingSalesmanBenchmark()
                    "0" -> {
                        println("Goodbye")
                        return
                    }

                    else -> println("Unknown option")
                }
                println()
            }
        } catch (_: EndOfInputException) {
            println("Goodbye")
        }
    }

    private fun printMenu() {
        println("SimpleNavigator")
        println("1. Load graph from file")
        println("2. Traverse graph in breadth")
        println("3. Traverse graph in depth")
        println("4. Find shortest path between two vertices")
        println("5. Find shortest paths between all vertices")
        println("6. Find minimum spanning tree")
        println("7. Solve traveling salesman problem")
        println("8. Solve traveling salesman problem with genetic algorithm")
        println("9. Solve traveling salesman problem with simulated annealing")
        println("10. Compare TSP algorithm speed")
        println("0. Exit")
    }

    private fun loadGraph() {
        val filename = prompt("Enter graph file path")
        runSafely {
            graph.loadGraphFromFile(filename)
            println("Graph loaded: ${graph.size} vertices")
        }
    }

    private fun runBreadthFirstSearch() {
        runWithLoadedGraph {
            val startVertex = promptVertex("Enter start vertex")
            val result = algorithms.breadthFirstSearch(graph, startVertex)
            println("BFS: ${formatVertices(result)}")
        }
    }

    private fun runDepthFirstSearch() {
        runWithLoadedGraph {
            val startVertex = promptVertex("Enter start vertex")
            val result = algorithms.depthFirstSearch(graph, startVertex)
            println("DFS: ${formatVertices(result)}")
        }
    }

    private fun runShortestPathBetweenVertices() {
        runWithLoadedGraph {
            val from = promptVertex("Enter start vertex")
            val to = promptVertex("Enter destination vertex")
            val distance = algorithms.getShortestPathBetweenVertices(graph, from, to)
            println("Shortest path between $from and $to: $distance")
        }
    }

    private fun runShortestPathsBetweenAllVertices() {
        runWithLoadedGraph {
            val distances = algorithms.getShortestPathsBetweenAllVertices(graph)
            println("All-pairs shortest paths:")
            printMatrix(distances)
        }
    }

    private fun runLeastSpanningTree() {
        runWithLoadedGraph {
            val tree = algorithms.getLeastSpanningTree(graph)
            println("Minimum spanning tree adjacency matrix:")
            printMatrix(tree)
        }
    }

    private fun runTravelingSalesmanProblem() {
        runWithLoadedGraph {
            val result = algorithms.solveTravelingSalesmanProblemDetailed(graph)
            printHeuristicTsmResult(result)
        }
    }

    private fun runTravelingSalesmanProblemGenetic() {
        runWithLoadedGraph {
            val result = algorithms.solveTravelingSalesmanProblemGeneticDetailed(graph)
            printHeuristicTsmResult(result)
        }
    }

    private fun runTravelingSalesmanProblemAnnealing() {
        runWithLoadedGraph {
            val result = algorithms.solveTravelingSalesmanProblemAnnealingDetailed(graph)
            printHeuristicTsmResult(result)
        }
    }

    private fun runTravelingSalesmanBenchmark() {
        runWithLoadedGraph {
            val iterations = promptPositiveInt("Enter number of benchmark iterations")
            println("Benchmarking $iterations runs per algorithm...")

            val antTime = measureNanoTime {
                repeat(iterations) {
                    algorithms.solveTravelingSalesmanProblem(graph)
                }
            }
            val geneticTime = measureNanoTime {
                repeat(iterations) {
                    algorithms.solveTravelingSalesmanProblemGenetic(graph)
                }
            }
            val annealingTime = measureNanoTime {
                repeat(iterations) {
                    algorithms.solveTravelingSalesmanProblemAnnealing(graph)
                }
            }

            println("Ant colony: ${formatDurationMillis(antTime)} ms")
            println("Genetic algorithm: ${formatDurationMillis(geneticTime)} ms")
            println("Simulated annealing: ${formatDurationMillis(annealingTime)} ms")
        }
    }

    private fun runWithLoadedGraph(action: () -> Unit) {
        if (graph.size == 0) {
            println("Load a graph first")
            return
        }
        runSafely(action)
    }

    private fun runSafely(action: () -> Unit) {
        try {
            action()
        } catch (exception: EndOfInputException) {
            throw exception
        } catch (exception: Exception) {
            println("Error: ${exception.message}")
        }
    }

    private fun prompt(label: String): String {
        print("$label: ")
        return input.readLine()?.trim() ?: throw EndOfInputException()
    }

    private fun promptVertex(label: String): Int {
        return prompt(label).toIntOrNull() ?: throw IllegalArgumentException("Vertex must be an integer")
    }

    private fun promptPositiveInt(label: String): Int {
        val value = prompt(label).toIntOrNull() ?: throw IllegalArgumentException("Value must be an integer")
        require(value > 0) { "Value must be positive" }
        return value
    }

    private fun formatVertices(vertices: List<Int>): String = vertices.joinToString(" -> ")

    private fun formatDurationMillis(durationNanos: Long): String = "%.3f".format(durationNanos / 1_000_000.0)

    private fun printMatrix(matrix: Array<IntArray>) {
        for (row in matrix) {
            println(row.joinToString(" ") { value -> if (value == INFINITY) "INF" else value.toString() })
        }
    }

    private fun printHeuristicTsmResult(result: HeuristicTsmResult) {
        println("Traveling salesman route before 2-opt: ${result.beforeTwoOpt.vertices.joinToString(" -> ")}")
        println("Traveling salesman distance before 2-opt: ${result.beforeTwoOpt.distance}")
        println("Traveling salesman route after 2-opt: ${result.afterTwoOpt.vertices.joinToString(" -> ")}")
        println("Traveling salesman distance after 2-opt: ${result.afterTwoOpt.distance}")
    }

    private class EndOfInputException : RuntimeException(null, null, false, false)
}
