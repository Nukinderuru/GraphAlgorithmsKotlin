# Simple Navigator — Graph Algorithms in Kotlin

A Kotlin implementation of fundamental graph algorithms, ranging from graph traversal and shortest-path search to minimum spanning trees and heuristic solutions for the Traveling Salesman Problem.

The project provides a reusable graph representation, a collection of classical graph algorithms, custom stack and queue data structures, graph import/export functionality, and a console application for exploring and comparing the implemented algorithms.

## Features

### Graph Representation

The `Graph` abstraction stores weighted graphs using an adjacency matrix and provides functionality for:

* loading graphs from files;
* accessing vertices, edges, neighbors, and edge weights through a public API;
* exporting graphs to the Graphviz DOT format.

The algorithm layer is separated from the internal graph representation: graph algorithms operate exclusively through the public `Graph` interface.

### Graph Traversal

Two fundamental traversal algorithms are implemented:

* **Depth-First Search (DFS)** — iterative graph traversal using a custom stack implementation;
* **Breadth-First Search (BFS)** — level-by-level graph traversal using a custom queue implementation.

Both algorithms return vertices in traversal order starting from a specified vertex.

### Shortest Paths

The project includes two classical shortest-path algorithms:

* **Dijkstra's algorithm** for finding the shortest distance between two selected vertices in a weighted graph;
* **Floyd–Warshall algorithm** for computing shortest paths between every pair of vertices.

The latter produces a complete all-pairs shortest-path matrix.

### Minimum Spanning Tree

A minimum spanning tree is constructed using **Prim's algorithm**.

The algorithm connects all vertices while minimizing the total edge weight and returns the resulting tree as an adjacency matrix.

## Traveling Salesman Problem

A significant part of the project explores heuristic and metaheuristic approaches to the **Traveling Salesman Problem (TSP)**.

Three different optimization strategies are implemented and can be compared experimentally.

### Ant Colony Optimization

The primary TSP solver uses **Ant Colony Optimization (ACO)**.

Artificial ants probabilistically construct tours using a combination of:

* pheromone levels accumulated on graph edges;
* heuristic information based on edge weights;
* pheromone evaporation;
* reinforcement of promising routes.

This allows the colony to gradually concentrate its search around high-quality tours while preserving enough randomness to explore alternative solutions.

### Genetic Algorithm

The bonus implementation includes a **Genetic Algorithm (GA)** in which TSP tours are represented as permutations of graph vertices.

The implementation uses:

* a population of candidate tours;
* greedy and randomized population initialization;
* **Tournament Selection** for parent selection;
* **elitism** to preserve the best individuals between generations;
* **Order Crossover (OX)** for permutation-safe recombination;
* **Swap Mutation** for maintaining population diversity;
* deterministic random seeding for reproducible experiments.

The algorithm evolves the population over multiple generations while keeping track of the best valid tour discovered during the search.

### Simulated Annealing

The second bonus TSP solver uses **Simulated Annealing (SA)**.

Starting from an initial tour, the algorithm repeatedly explores neighboring solutions. Improvements are accepted immediately, while worse solutions may also be accepted according to the current temperature and the Metropolis acceptance probability.

As the system gradually cools, the search transitions from broad exploration to increasingly conservative local optimization.

This mechanism allows the algorithm to escape local optima that would trap a purely greedy local search.

### 2-opt Local Search

Solutions produced by the heuristic TSP algorithms are additionally refined using **2-opt local search**.

2-opt examines pairs of positions in the tour and reverses route segments when the resulting tour is shorter. This post-processing step removes inefficient local structures and helps improve solutions after the global search performed by ACO, GA, or SA.

The combination demonstrates a common optimization strategy:

> **global metaheuristic search → local refinement**

## TSP Algorithm Comparison

The console application includes an experimental mode for comparing the three TSP solvers:

* Ant Colony Optimization;
* Genetic Algorithm;
* Simulated Annealing.

For a loaded graph and a user-defined number of repetitions `N`, each solver is executed repeatedly and its total execution time is measured.

This provides a simple way to compare the computational cost of different metaheuristic approaches on the same problem instance.

## Console Application

The project includes an interactive CLI that allows the user to:

1. Load a graph from a file.
2. Traverse it using BFS.
3. Traverse it using DFS.
4. Find the shortest path between two vertices using Dijkstra's algorithm.
5. Compute all-pairs shortest paths using Floyd–Warshall.
6. Build a minimum spanning tree using Prim's algorithm.
7. Solve the Traveling Salesman Problem.
8. Compare the performance of multiple TSP algorithms.
9. Export graphs to Graphviz DOT format.

## Algorithms at a Glance

| Problem                     | Algorithm                |
| --------------------------- | ------------------------ |
| Graph traversal             | Depth-First Search       |
| Graph traversal             | Breadth-First Search     |
| Single-source shortest path | Dijkstra's algorithm     |
| All-pairs shortest paths    | Floyd–Warshall algorithm |
| Minimum spanning tree       | Prim's algorithm         |
| Traveling Salesman Problem  | Ant Colony Optimization  |
| Traveling Salesman Problem  | Genetic Algorithm        |
| Traveling Salesman Problem  | Simulated Annealing      |
| Tour refinement             | 2-opt local search       |

## Project Structure

The project follows a modular design that separates:

* graph representation and I/O;
* graph algorithms;
* custom data structures;
* TSP optimization strategies;
* console interaction;
* testing.

This keeps algorithm implementations independent from the graph's internal storage details and makes individual components easier to test and extend.

## What This Project Covers

The project brings together several important areas of algorithms and data structures:

* graph representation;
* iterative graph traversal;
* stacks and queues;
* shortest-path algorithms;
* dynamic programming;
* greedy algorithms;
* minimum spanning trees;
* combinatorial optimization;
* heuristic and metaheuristic search;
* evolutionary algorithms;
* local search;
* algorithm benchmarking.

Beyond implementing individual algorithms, the project provides an opportunity to compare fundamentally different approaches to optimization: deterministic classical algorithms, greedy construction, population-based search, probabilistic search, and local refinement.

## Tech Stack

* **Kotlin**
* **Gradle / Makefile**
* **Unit testing**
* **Graphviz DOT** for graph export and visualization

## Background

This project is a Kotlin implementation of the School 21 **Simple Navigator** educational project.

The original assignment focuses on implementing classical graph algorithms and an Ant Colony Optimization solver for the Traveling Salesman Problem. The optional extension requires two additional TSP algorithms and a comparative performance study.

For this implementation, the bonus TSP solvers are:

* **Genetic Algorithm**
* **Simulated Annealing**

Both are combined with **2-opt local refinement** to improve the final tours produced by the metaheuristic search.
