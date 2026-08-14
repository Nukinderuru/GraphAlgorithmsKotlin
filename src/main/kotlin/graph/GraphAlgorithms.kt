package graph

import graph.collections.Queue
import graph.collections.PriorityQueue
import graph.collections.Stack
import kotlin.math.exp
import kotlin.math.pow
import kotlin.random.Random

private typealias Tour = List<Int>
private typealias MutableTour = MutableList<Int>
private typealias ScoredTour = Pair<Tour, Double>
private typealias RankedTour = Pair<Tour, Double?>

/**
 * Provides algorithms for traversing or searching through a graph.
 */
class GraphAlgorithms {
    /**
     * Performs a breadth-first search (BFS) on the given graph starting from the specified vertex.
     *
     * @param graph The graph to traverse, represented as an instance of the Graph class.
     * @param startVertex The vertex from which the BFS traversal begins. Must be within the valid range of vertices in the graph.
     * @return A list of integers representing the order in which the vertices are visited during the BFS traversal.
     */
    fun breadthFirstSearch(graph: Graph, startVertex: Int): List<Int> {
        validateStartVertex(graph, startVertex)

        val visited = BooleanArray(graph.size + 1)
        val queue = Queue<Int>()
        val result = mutableListOf<Int>()

        visited[startVertex] = true
        queue.push(startVertex)

        while (!queue.isEmpty()) {
            val vertex = queue.pop()
            result.add(vertex)

            for (neighbor in graph.neighbors(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true
                    queue.push(neighbor)
                }
            }
        }

        return result
    }

    /**
     * Performs an iterative depth-first search (DFS) on the given graph starting from the specified vertex.
     *
     * @param graph The graph to traverse, represented as an instance of the Graph class.
     * @param startVertex The vertex from which the DFS traversal begins. Must be within the valid range of vertices in the graph.
     * @return A list of integers representing the order in which the vertices are visited during the DFS traversal.
     */
    fun depthFirstSearch(graph: Graph, startVertex: Int): List<Int> {
        validateStartVertex(graph, startVertex)

        val visited = BooleanArray(graph.size + 1)
        val stack = Stack<Int>()
        val result = mutableListOf<Int>()

        visited[startVertex] = true
        stack.push(startVertex)

        while (!stack.isEmpty()) {
            val vertex = stack.pop()
            result.add(vertex)

            val neighbors = graph.neighbors(vertex)
            for (index in neighbors.indices.reversed()) {
                val neighbor = neighbors[index]
                if (!visited[neighbor]) {
                    visited[neighbor] = true
                    stack.push(neighbor)
                }
            }
        }

        return result
    }

    /**
     * Computes the shortest path distance between two vertices in a graph using Dijkstra's algorithm.
     *
     * @param graph The graph to analyze, represented as an instance of the Graph class.
     * @param vertex1 The starting vertex for the path. Must be within the valid range of vertices in the graph.
     * @param vertex2 The destination vertex for the path. Must be within the valid range of vertices in the graph.
     * @return The shortest path distance between the given vertices. If no path exists between the vertices, an exception is thrown.
     */
    fun getShortestPathBetweenVertices(graph: Graph, vertex1: Int, vertex2: Int): Int {
        validateStartVertex(graph, vertex1)
        validateStartVertex(graph, vertex2)

        if (vertex1 == vertex2) {
            return 0
        }

        val distances = IntArray(graph.size + 1) { INFINITY }
        val visited = BooleanArray(graph.size + 1)
        val priorityQueue = PriorityQueue<Int>()
        distances[vertex1] = 0
        priorityQueue.push(vertex1, 0)

        while (!priorityQueue.isEmpty()) {
            val (currentVertex, currentDistance) = priorityQueue.pop()
            if (visited[currentVertex] || currentDistance != distances[currentVertex]) {
                continue
            }

            visited[currentVertex] = true
            if (currentVertex == vertex2) {
                break
            }

            for (neighbor in graph.neighbors(currentVertex)) {
                val edgeWeight = graph.edgeWeight(currentVertex, neighbor)
                val candidateDistance = distances[currentVertex] + edgeWeight
                if (candidateDistance < distances[neighbor]) {
                    distances[neighbor] = candidateDistance
                    priorityQueue.push(neighbor, candidateDistance)
                }
            }
        }

        require(distances[vertex2] != INFINITY) {
            ValidationConstants.noPathBetweenVertices(vertex1, vertex2)
        }
        return distances[vertex2]
    }

    /**
     * Computes the shortest path distances between all pairs of vertices in the provided graph.
     * The function uses the Floyd-Warshall algorithm to calculate the shortest paths.
     *
     * @param graph The graph for which the shortest path distances will be computed, represented as an instance of the Graph class.
     *              The graph must provide methods to access the weight of edges between vertices.
     * @return A two-dimensional array where the element at `[i][j]` represents the shortest path distance
     *         from vertex i+1 to vertex j+1. If no path exists between two vertices, the distance will be represented as infinity.
     */
    fun getShortestPathsBetweenAllVertices(graph: Graph): Array<IntArray> {
        val distances = Array(graph.size) { fromIndex ->
            IntArray(graph.size) { toIndex ->
                when {
                    fromIndex == toIndex -> 0
                    graph.edgeWeight(fromIndex + 1, toIndex + 1) > 0 -> {
                        graph.edgeWeight(fromIndex + 1, toIndex + 1)
                    }

                    else -> INFINITY
                }
            }
        }

        for (middle in distances.indices) {
            for (from in distances.indices) {
                if (distances[from][middle] == INFINITY) {
                    continue
                }
                for (to in distances.indices) {
                    if (distances[middle][to] == INFINITY) {
                        continue
                    }
                    val candidateDistance = distances[from][middle] + distances[middle][to]
                    if (candidateDistance < distances[from][to]) {
                        distances[from][to] = candidateDistance
                    }
                }
            }
        }

        return distances
    }

    /**
     * Computes the minimum spanning tree of an undirected connected graph using Prim's algorithm.
     *
     * @param graph The graph for which the minimum spanning tree will be built.
     * @return The adjacency matrix of the minimum spanning tree.
     */
    fun getLeastSpanningTree(graph: Graph): Array<IntArray> {
        require(!isDirected(graph)) {
            ValidationConstants.MINIMUM_SPANNING_TREE_REQUIRES_UNDIRECTED_GRAPH
        }
        require(isConnected(graph)) {
            ValidationConstants.MINIMUM_SPANNING_TREE_REQUIRES_CONNECTED_GRAPH
        }

        val visited = BooleanArray(graph.size + 1)
        val minimumTree = Array(graph.size) { IntArray(graph.size) }
        visited[1] = true

        repeat(graph.size - 1) {
            var bestFrom = -1
            var bestTo = -1
            var bestWeight = INFINITY

            for (from in 1..graph.size) {
                if (!visited[from]) {
                    continue
                }
                for (to in graph.neighbors(from)) {
                    val weight = graph.edgeWeight(from, to)
                    if (!visited[to] && weight < bestWeight) {
                        bestFrom = from
                        bestTo = to
                        bestWeight = weight
                    }
                }
            }

            require(bestTo != -1) {
                ValidationConstants.MINIMUM_SPANNING_TREE_REQUIRES_CONNECTED_GRAPH
            }

            visited[bestTo] = true
            minimumTree[bestFrom - 1][bestTo - 1] = bestWeight
            minimumTree[bestTo - 1][bestFrom - 1] = bestWeight
        }

        return minimumTree
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) for the given graph using a detailed heuristic approach.
     * The solution is finalized and refined using a two-opt optimization step.
     *
     * @param graph The graph representing the cities and the distances between them. Each vertex corresponds
     *              to a city, and each edge represents the distance or cost between two cities. The graph
     *              must be a valid instance of a TSP problem.
     * @return A result encapsulated in a [TsmResult], representing the finalized tour and its total cost
     *         after applying a two-opt refinement to the heuristic solution.
     */
    fun solveTravelingSalesmanProblem(graph: Graph): TsmResult {
        return solveTravelingSalesmanProblemDetailed(graph).afterTwoOpt
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using an Ant Colony Optimization (ACO) heuristic approach
     * and provides both the best heuristic result and the finalized detailed tour result.
     *
     * The method attempts to find an optimal route by simulating the behavior of a colony of ants, which
     * iteratively refine solutions using pheromone-based learning. Pheromones are updated based on the quality
     * of found routes, and evaporation ensures that unnecessary paths are less likely to be chosen in future iterations.
     *
     * @param graph The graph representing the cities and distances between them. Must be a valid representation
     *              of a TSP instance, where each vertex corresponds to a city and each edge represents the distance
     *              or cost between two cities.
     * @return A heuristic result encapsulated in a [HeuristicTsmResult] that provides both the initially identified
     *         best solution and the finalized tour solution. The finalized solution is refined and validated to ensure
     *         optimality and correctness.
     *
     * @throws IllegalArgumentException If the graph is empty or no valid TSP solution exists for the provided graph.
     */
    fun solveTravelingSalesmanProblemDetailed(graph: Graph): HeuristicTsmResult {
        if (graph.size == 1) {
            val result = TsmResult(arrayOf(1, 1), 0.0)
            return HeuristicTsmResult(result, result)
        }

        val pheromones = Array(graph.size) { DoubleArray(graph.size) { INITIAL_PHEROMONE } }
        val random = Random(ACO_RANDOM_SEED)
        var bestRoute: Tour? = null
        var bestDistance = Double.POSITIVE_INFINITY

        repeat(ACO_ITERATIONS) { iteration ->
            val successfulRoutes = mutableListOf<ScoredTour>()

            repeat(graph.size) { antIndex ->
                val startVertex = ((iteration + antIndex) % graph.size) + 1
                val route = buildAntRoute(graph, pheromones, startVertex, random)
                if (route != null) {
                    val distance = calculateRouteDistance(graph, route)
                    successfulRoutes.add(route to distance)
                    if (distance < bestDistance) {
                        bestDistance = distance
                        bestRoute = route
                    }
                }
            }

            evaporatePheromones(pheromones)
            depositPheromones(pheromones, successfulRoutes)
        }

        require(bestRoute != null) {
            ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION
        }

        return finalizeHeuristicTour(graph, bestRoute.dropLast(1))
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) for the given graph using a genetic algorithm approach.
     * The solution is refined and finalized using a two-opt optimization technique.
     *
     * @param graph The graph representing the cities and the distances between them. Each vertex corresponds
     *              to a city, and each edge represents the distance or cost between two cities. The graph
     *              must be a valid instance of a TSP problem.
     * @return A result encapsulated in a [TsmResult], representing the finalized tour and its total cost
     *         after applying a genetic algorithm followed by a two-opt refinement.
     */
    fun solveTravelingSalesmanProblemGenetic(graph: Graph): TsmResult {
        return solveTravelingSalesmanProblemGeneticDetailed(graph).afterTwoOpt
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using a genetic algorithm and returns a detailed heuristic result,
     * including the best-found solution and additional information on the search process.
     *
     * @param graph The graph representing the problem's nodes and edges. This should include the distances
     * between all pairs of nodes.
     * @return A [HeuristicTsmResult] containing the best solution found and additional heuristic data such as
     * the optimized route and its total distance.
     */
    fun solveTravelingSalesmanProblemGeneticDetailed(graph: Graph): HeuristicTsmResult {
        if (graph.size == 1) {
            val result = TsmResult(arrayOf(1, 1), 0.0)
            return HeuristicTsmResult(result, result)
        }

        val random = Random(GENETIC_RANDOM_SEED)
        var population = generateInitialPopulation(graph, random)
        var bestChromosome: Tour? = null
        var bestDistance = Double.POSITIVE_INFINITY

        repeat(GENETIC_GENERATIONS) {
            val rankedPopulation = rankPopulation(graph, population)
            val generationBest = bestValidTour(rankedPopulation)
            if (generationBest != null && generationBest.second!! < bestDistance) {
                bestChromosome = generationBest.first
                bestDistance = generationBest.second!!
            }

            val nextGeneration = rankedPopulation
                .take(GENETIC_ELITE_COUNT)
                .map { it.first }
                .toMutableList()

            while (nextGeneration.size < GENETIC_POPULATION_SIZE) {
                val parentOne = selectParent(rankedPopulation, random)
                val parentTwo = selectParent(rankedPopulation, random)
                var child = if (random.nextDouble() < GENETIC_CROSSOVER_RATE) {
                    crossover(parentOne, parentTwo, random)
                } else {
                    parentOne.toMutableList()
                }
                if (random.nextDouble() < GENETIC_MUTATION_RATE) {
                    child = mutate(child, random)
                }
                nextGeneration.add(child)
            }

            population = nextGeneration
        }

        val finalGenerationBest = bestValidTour(rankPopulation(graph, population))
        if (finalGenerationBest != null) {
            val (chromosome, distance) = finalGenerationBest

            if (distance != null && distance < bestDistance) {
                bestChromosome = chromosome
                bestDistance = distance
            }
        }

        require(bestChromosome != null) {
            ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION
        }

        return finalizeHeuristicTour(graph, bestChromosome)
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using the Simulated Annealing algorithm.
     *
     * @param graph The graph representing the TSP, with nodes and weighted edges.
     * @return A TsmResult object containing the solution to the TSP after applying simulated annealing
     *         and performing 2-opt optimization.
     */
    fun solveTravelingSalesmanProblemAnnealing(graph: Graph): TsmResult {
        return solveTravelingSalesmanProblemAnnealingDetailed(graph).afterTwoOpt
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using the simulated annealing heuristic approach.
     * This method attempts to find an efficient route for visiting each node in the graph exactly once
     * and returning to the starting point.
     *
     * @param graph The input graph representing the TSP problem. Each node signifies a city,
     *              and the edges represent possible paths with associated distances. The graph must contain
     *              at least one node for the problem to be solved.
     * @return A [HeuristicTsmResult] containing the approximate solution to the TSP problem. The result includes
     *         the best path found and the associated cost, computed from the simulated annealing process.
     * @throws IllegalArgumentException If the provided graph does not allow a valid TSP solution or contains invalid data.
     */
    fun solveTravelingSalesmanProblemAnnealingDetailed(graph: Graph): HeuristicTsmResult {
        if (graph.size == 1) {
            val result = TsmResult(arrayOf(1, 1), 0.0)
            return HeuristicTsmResult(result, result)
        }

        val random = Random(ANNEALING_RANDOM_SEED)
        var currentTour = initialAnnealingTour(graph, random)
            ?: throw IllegalArgumentException(ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION)
        var currentDistance = routeDistanceOrNull(graph, currentTour)
            ?: throw IllegalArgumentException(ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION)
        var bestTour = currentTour
        var bestDistance = currentDistance
        var temperature = ANNEALING_START_TEMPERATURE

        while (temperature > ANNEALING_MIN_TEMPERATURE) {
            repeat(ANNEALING_STEPS_PER_TEMPERATURE) {
                val candidateTour = annealingNeighbor(currentTour, random)
                val candidateDistance = routeDistanceOrNull(graph, candidateTour) ?: return@repeat
                val distanceDelta = candidateDistance - currentDistance

                if (distanceDelta <= 0.0 || random.nextDouble() < exp(-distanceDelta / temperature)) {
                    currentTour = candidateTour
                    currentDistance = candidateDistance
                    if (candidateDistance < bestDistance) {
                        bestTour = candidateTour
                        bestDistance = candidateDistance
                    }
                }
            }
            temperature *= ANNEALING_COOLING_RATE
        }

        return finalizeHeuristicTour(graph, bestTour)
    }

    /**
     * Validates whether the provided start vertex is within the valid range for the given graph.
     *
     * @param graph The graph object containing the vertices.
     * @param startVertex The starting vertex to validate.
     * @throws IllegalArgumentException if the start vertex is not within the valid range of the graph.
     */
    private fun validateStartVertex(graph: Graph, startVertex: Int) {
        require(startVertex in 1..graph.size) {
            ValidationConstants.vertexOutOfRange(startVertex, graph.size)
        }
    }

    /**
     * Constructs a complete route for an ant in the given graph, starting from a specific vertex.
     * The method uses pheromone levels and graph structure to probabilistically guide the route
     * construction process.
     *
     * @param graph The graph on which the ant travels. Represents edges and weights between vertices.
     * @param pheromones A matrix representing the pheromone levels between vertices in the form of a
     *                   2D array. Each element `pheromones[i][j]` denotes the pheromone level for the edge
     *                   between vertex `i` and vertex `j`.
     * @param startVertex The vertex where the ant starts its route.
     * @param random An instance of Random used to introduce stochasticity during route selection.
     * @return A Tour representing the complete route taken by the ant if a valid route exists, or `null`
     *         if the route cannot be completed due to lack of connectivity or invalid edges.
     */
    private fun buildAntRoute(
        graph: Graph,
        pheromones: Array<DoubleArray>,
        startVertex: Int,
        random: Random
    ): Tour? {
        val route = mutableListOf(startVertex)
        val visited = BooleanArray(graph.size + 1)
        visited[startVertex] = true
        var currentVertex = startVertex

        while (route.size < graph.size) {
            val candidates = graph.neighbors(currentVertex).filter { !visited[it] }
            if (candidates.isEmpty()) {
                return null
            }

            val nextVertex = selectNextVertexForAnt(graph, pheromones, currentVertex, candidates, random)
            route.add(nextVertex)
            visited[nextVertex] = true
            currentVertex = nextVertex
        }

        if (graph.edgeWeight(currentVertex, startVertex) == 0) {
            return null
        }

        route.add(startVertex)
        return route
    }

    /**
     * Selects the next vertex to visit in a graph based on pheromone levels, edge weights, and
     * stochastic selection influenced by desirability.
     *
     * @param graph The graph representing the problem space with vertices and edges.
     * @param pheromones A 2D array representing pheromone levels between vertices.
     * @param currentVertex The current vertex from which the next is to be selected.
     * @param candidates A collection of candidate vertices to choose from.
     * @param random Random number generator to introduce stochasticity into the selection process.
     * @return The vertex selected as the next to visit based on the desirability calculated from
     *         pheromone levels and edge weights.
     */
    private fun selectNextVertexForAnt(
        graph: Graph,
        pheromones: Array<DoubleArray>,
        currentVertex: Int,
        candidates: Tour,
        random: Random
    ): Int {
        val desirabilities = candidates.map { candidate ->
            val pheromone = pheromones[currentVertex - 1][candidate - 1].pow(PHEROMONE_INFLUENCE)
            val visibility = (1.0 / graph.edgeWeight(currentVertex, candidate)).pow(DISTANCE_INFLUENCE)
            pheromone * visibility
        }
        val totalDesirability = desirabilities.sum()

        if (totalDesirability == 0.0) {
            return candidates.minBy { graph.edgeWeight(currentVertex, it) }
        }

        var threshold = random.nextDouble() * totalDesirability
        for (index in candidates.indices) {
            threshold -= desirabilities[index]
            if (threshold <= 0.0) {
                return candidates[index]
            }
        }

        return candidates.last()
    }

    /**
     * Reduces the intensity of pheromones in the pheromone matrix by applying evaporation.
     * Each pheromone value is decreased proportionally to the evaporation rate,
     * ensuring it does not fall below the minimum pheromone threshold.
     *
     * @param pheromones A two-dimensional array representing the pheromone levels
     *                   between different nodes or agents.
     */
    private fun evaporatePheromones(pheromones: Array<DoubleArray>) {
        for (row in pheromones.indices) {
            for (column in pheromones[row].indices) {
                pheromones[row][column] = maxOf(MIN_PHEROMONE, pheromones[row][column] * (1.0 - EVAPORATION_RATE))
            }
        }
    }

    /**
     * Updates the pheromone levels on a pheromone matrix based on the given routes.
     *
     * @param pheromones A 2D array representing the pheromone intensity between nodes.
     * @param routes A list of scored tours where each tour contains a route and its corresponding distance.
     */
    private fun depositPheromones(
        pheromones: Array<DoubleArray>,
        routes: List<ScoredTour>
    ) {
        for ((route, distance) in routes) {
            val deposit = PHEROMONE_DEPOSIT / distance
            for (index in 0 until route.lastIndex) {
                val from = route[index] - 1
                val to = route[index + 1] - 1
                pheromones[from][to] += deposit
            }
        }
    }

    /**
     * Generates the initial population of tours for the genetic algorithm.
     *
     * @param graph The graph representing the problem space.
     * @param random A random number generator used to shuffle vertices for generating random chromosomes.
     * @return A mutable list of initial tours for the population.
     */
    private fun generateInitialPopulation(graph: Graph, random: Random): MutableList<Tour> {
        val baseVertices = (1..graph.size).toList()
        val population = mutableListOf<Tour>()

        for (startVertex in baseVertices) {
            population.add(greedyTour(graph, startVertex))
        }

        while (population.size < GENETIC_POPULATION_SIZE) {
            population.add(baseVertices.shuffled(random))
        }

        return population
    }

    /**
     * Constructs a greedy tour for the given graph, starting from the specified vertex.
     * The method incrementally builds the tour by always visiting the nearest unvisited neighbor
     * based on edge weights.
     *
     * @param graph the graph containing vertices and weighted edges
     * @param startVertex the starting vertex for constructing the tour
     * @return a tour represented as a list of vertex indices, starting and ending at the start vertex
     */
    private fun greedyTour(graph: Graph, startVertex: Int): Tour {
        val tour = mutableListOf(startVertex)
        val visited = mutableSetOf(startVertex)
        var currentVertex = startVertex

        while (tour.size < graph.size) {
            val nextVertex = graph.neighbors(currentVertex)
                .filter { it !in visited }
                .minByOrNull { graph.edgeWeight(currentVertex, it) }
                ?: ((1..graph.size).first { it !in visited })
            tour.add(nextVertex)
            visited.add(nextVertex)
            currentVertex = nextVertex
        }

        return tour
    }

    /**
     * Calculates the total distance of a given `tour` in the graph represented by `graph`.
     * Returns null if the `tour` is invalid, either because it does not visit all nodes in the graph
     * exactly once or because some required edges are missing (i.e., have zero weight).
     *
     * @param graph The graph containing the nodes and edges, along with their weights.
     * @param tour A list of nodes representing the route; it must visit all nodes exactly once
     * and return to the starting node.
     * @return The total distance of the route if the `tour` is valid, null otherwise.
     */
    private fun routeDistanceOrNull(graph: Graph, tour: Tour): Double? {
        if (tour.size != graph.size || tour.toSet().size != graph.size) {
            return null
        }

        var totalDistance = 0.0
        for (index in 0 until tour.lastIndex) {
            val edgeWeight = graph.edgeWeight(tour[index], tour[index + 1])
            if (edgeWeight == 0) {
                return null
            }
            totalDistance += edgeWeight
        }

        val returnEdgeWeight = graph.edgeWeight(tour.last(), tour.first())
        if (returnEdgeWeight == 0) {
            return null
        }

        return totalDistance + returnEdgeWeight
    }

    /**
     * Ranks a population of tours based on their computed distances within a given graph.
     *
     * @param graph The graph representing the structure of nodes and edges.
     * @param population A list of tours to be ranked according to their distances in the graph.
     * @return A list of ranked tours, where each tour is paired with its computed distance.
     */
    private fun rankPopulation(graph: Graph, population: List<Tour>): List<RankedTour> {
        return population
            .map { tour -> tour to routeDistanceOrNull(graph, tour) }
            .sortedBy { (_, distance) -> distance ?: Double.POSITIVE_INFINITY }
    }

    /**
     * Selects the best valid tour from a given ranked population.
     *
     * This method iterates through the ranked population of tours and returns the first tour
     * that has a valid (non-null) score.
     *
     * @param rankedPopulation A list of ranked tours, where each element is a pair consisting of
     * a tour and its associated score. The score can be null.
     * @return The first ranked tour with a non-null score, or null if no such tour exists.
     */
    private fun bestValidTour(rankedPopulation: List<RankedTour>): RankedTour? {
        return rankedPopulation.firstOrNull { it.second != null }
    }

    /**
     * Selects a parent Tour from the ranked population using a tournament selection process.
     *
     * @param rankedPopulation The list of RankedTour objects representing the population with their fitness scores.
     * @param random An instance of Random used to generate random indices for selection.
     * @return The selected Tour with the best fitness score among the tournament contenders.
     */
    private fun selectParent(
        rankedPopulation: List<RankedTour>,
        random: Random
    ): Tour {
        val contenders = List(GENETIC_TOURNAMENT_SIZE) {
            rankedPopulation[random.nextInt(rankedPopulation.size)]
        }
        return contenders.minBy { it.second ?: Double.POSITIVE_INFINITY }.first
    }

    /**
     * Performs a crossover operation between two parent tours to produce a new child tour (Order Crossover).
     * The child inherits a subsequence of genes from the first parent and fills the remaining
     * positions with genes from the second parent cyclically, starting right after the copied segment.
     *
     * @param parentOne The first parent tour from which a subsequence is inherited.
     * @param parentTwo The second parent tour from which the remaining genes are filled.
     * @param random A random number generator used to determine the crossover subsequence.
     * @return A new [MutableTour] representing the child tour produced by the crossover operation.
     */
    private fun crossover(parentOne: Tour, parentTwo: Tour, random: Random): MutableTour {
        val tourSize = parentOne.size
        val child = MutableList(tourSize) { 0 }
        val start = random.nextInt(tourSize)
        val end = random.nextInt(start, tourSize)
        val usedGenes = mutableSetOf<Int>()

        for (index in start..end) {
            child[index] = parentOne[index]
            usedGenes.add(parentOne[index])
        }

        var childIndex = (end + 1) % tourSize
        for (offset in parentTwo.indices) {
            val parentTwoIndex = (end + 1 + offset) % tourSize
            val gene = parentTwo[parentTwoIndex]
            if (gene !in usedGenes) {
                child[childIndex] = gene
                usedGenes.add(gene)
                childIndex = (childIndex + 1) % tourSize
            }
        }

        return child
    }

    /**
     * Performs a mutation on the given chromosome by swapping two random elements.
     *
     * @param chromosome the original chromosome to be mutated.
     * @param random the random number generator used to select indices for mutation.
     * @return a new mutated chromosome as a result of swapping two random elements.
     */
    private fun mutate(chromosome: MutableTour, random: Random): MutableTour {
        val firstIndex = random.nextInt(chromosome.size)
        var secondIndex = random.nextInt(chromosome.size)
        while (secondIndex == firstIndex) {
            secondIndex = random.nextInt(chromosome.size)
        }
        val mutated = chromosome.toMutableList()
        val temporary = mutated[firstIndex]
        mutated[firstIndex] = mutated[secondIndex]
        mutated[secondIndex] = temporary
        return mutated
    }

    /**
     * Generates an initial tour for the simulated annealing process based on either a greedy
     * approach or random restarts if no valid greedy tour is found.
     *
     * @param graph The graph representing the problem, containing nodes and distances between them.
     * @param random The random number generator used for shuffling nodes during the random restarts.
     * @return A valid tour as a list of node indices if found, or null if no valid tour could be generated.
     */
    private fun initialAnnealingTour(graph: Graph, random: Random): Tour? {
        val greedyStarts = (1..graph.size)
            .map { greedyTour(graph, it) }
            .filter { routeDistanceOrNull(graph, it) != null }

        if (greedyStarts.isNotEmpty()) {
            return greedyStarts.minBy { routeDistanceOrNull(graph, it) ?: Double.POSITIVE_INFINITY }
        }

        repeat(ANNEALING_RANDOM_RESTARTS) {
            val candidate = (1..graph.size).toList().shuffled(random)
            if (routeDistanceOrNull(graph, candidate) != null) {
                return candidate
            }
        }

        return null
    }

    /**
     * Generates a neighboring solution for a given [Tour] using a simulated annealing strategy.
     *
     * The method randomly selects two indices in the tour and either performs a mutation
     * or reverses a subsection of the tour between these indices. The choice of operation
     * is determined randomly.
     *
     * @param tour The current tour solution that will be used to generate a neighbor.
     * @param random An instance of [Random] used to generate random indices and determine operations.
     * @return A new [Tour] instance representing a neighbor of the input tour.
     */
    private fun annealingNeighbor(tour: Tour, random: Random): Tour {
        val firstIndex = random.nextInt(tour.size)
        var secondIndex = random.nextInt(tour.size)
        while (secondIndex == firstIndex) {
            secondIndex = random.nextInt(tour.size)
        }

        val lowerIndex = minOf(firstIndex, secondIndex)
        val upperIndex = maxOf(firstIndex, secondIndex)

        return if (random.nextBoolean()) {
            mutate(tour.toMutableList(), random)
        } else {
            val neighbor = tour.toMutableList()
            val reversedSlice = neighbor.subList(lowerIndex, upperIndex + 1).reversed()
            for (index in reversedSlice.indices) {
                neighbor[lowerIndex + index] = reversedSlice[index]
            }
            neighbor
        }
    }

    /**
     * Finalizes the heuristic tour for the Traveling Salesman Problem (TSP) by computing the initial
     * distance of the tour, refining it using the 2-opt algorithm, and calculating the improved distance.
     *
     * @param graph The graph representing the nodes and edges for the TSP.
     * @param tour The initial tour to be evaluated and potentially refined.
     * @return A result containing the TSP metrics before and after applying the 2-opt refinement.
     * @throws IllegalArgumentException If the TSP has no valid solution.
     */
    private fun finalizeHeuristicTour(graph: Graph, tour: Tour): HeuristicTsmResult {
        val initialDistance = routeDistanceOrNull(graph, tour)
            ?: throw IllegalArgumentException(ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION)
        val refinedTour = refineTourWithTwoOpt(graph, tour)
        val refinedDistance = routeDistanceOrNull(graph, refinedTour)
            ?: throw IllegalArgumentException(ValidationConstants.TRAVELING_SALESMAN_PROBLEM_HAS_NO_SOLUTION)

        return HeuristicTsmResult(
            beforeTwoOpt = toTsmResult(tour, initialDistance),
            afterTwoOpt = toTsmResult(refinedTour, refinedDistance)
        )
    }

    /**
     * Refines a given tour using the 2-opt algorithm to minimize the total route distance.
     * The 2-opt algorithm iteratively improves the tour by reversing segments of the path
     * to reduce the overall distance.
     *
     * @param graph The graph representing the structure of nodes and edges, including distance information.
     * @param tour The initial tour to be refined, represented as a sequence of node indices.
     * @return A potentially improved tour with a reduced total route distance,
     *         or the original tour if no improvement is found or it is ineligible for refinement.
     */
    private fun refineTourWithTwoOpt(graph: Graph, tour: Tour): Tour {
        if (tour.size < 4) {
            return tour
        }

        var bestTour = tour.toMutableList()
        var bestDistance = routeDistanceOrNull(graph, bestTour) ?: return tour
        var improved = true

        while (improved) {
            improved = false
            for (start in 1 until bestTour.lastIndex) {
                for (end in start + 1 until bestTour.size) {
                    val candidate = bestTour.toMutableList()
                    val reversedSegment = candidate.subList(start, end + 1).reversed()
                    for (index in reversedSegment.indices) {
                        candidate[start + index] = reversedSegment[index]
                    }

                    val candidateDistance = routeDistanceOrNull(graph, candidate) ?: continue
                    if (candidateDistance + TWO_OPT_EPSILON < bestDistance) {
                        bestTour = candidate
                        bestDistance = candidateDistance
                        improved = true
                    }
                }
            }
        }

        return bestTour
    }

    private fun toTsmResult(tour: Tour, distance: Double): TsmResult {
        val route = tour.toMutableList()
        route.add(tour.first())
        return TsmResult(route.toTypedArray(), distance)
    }

    private fun isDirected(graph: Graph): Boolean {
        for (from in 1..graph.size) {
            for (to in 1..graph.size) {
                if (graph.edgeWeight(from, to) != graph.edgeWeight(to, from)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isConnected(graph: Graph): Boolean {
        return breadthFirstSearch(graph, 1).size == graph.size
    }

    /**
     * Calculates the total distance of a given route based on the provided graph.
     *
     * @param graph The graph containing nodes and edges with associated weights.
     * @param route The sequence of nodes representing the route.
     * @return The total distance of the route as a Double.
     */
    private fun calculateRouteDistance(graph: Graph, route: Tour): Double {
        var totalDistance = 0.0
        for (index in 0 until route.lastIndex) {
            totalDistance += graph.edgeWeight(route[index], route[index + 1])
        }
        return totalDistance
    }

    companion object {
        const val INFINITY: Int = Int.MAX_VALUE / 4
        private const val TWO_OPT_EPSILON = 1e-9

        // Ant Colony Optimization
        private const val ACO_ITERATIONS = 80
        private const val ACO_RANDOM_SEED = 21
        private const val INITIAL_PHEROMONE = 1.0
        private const val MIN_PHEROMONE = 0.0001
        private const val EVAPORATION_RATE = 0.4
        private const val PHEROMONE_DEPOSIT = 100.0
        private const val PHEROMONE_INFLUENCE = 1.0
        private const val DISTANCE_INFLUENCE = 3.0

        // Genetic Algorithm
        private const val GENETIC_RANDOM_SEED = 84
        private const val GENETIC_POPULATION_SIZE = 100
        private const val GENETIC_GENERATIONS = 250
        private const val GENETIC_TOURNAMENT_SIZE = 3
        private const val GENETIC_ELITE_COUNT = 2
        private const val GENETIC_CROSSOVER_RATE = 0.8
        private const val GENETIC_MUTATION_RATE = 0.15

        // Simulated Annealing
        private const val ANNEALING_RANDOM_SEED = 126
        private const val ANNEALING_RANDOM_RESTARTS = 400
        private const val ANNEALING_START_TEMPERATURE = 250.0
        private const val ANNEALING_MIN_TEMPERATURE = 0.01
        private const val ANNEALING_COOLING_RATE = 0.95
        private const val ANNEALING_STEPS_PER_TEMPERATURE = 120
    }
}
