package graph

/**
 * Represents the result of a traveling salesman problem (TSP) computation.
 *
 * This data class encapsulates the details of a TSP solution, which includes:
 * - The order of vertices visited in the computed tour.
 * - The total distance of the computed tour.
 *
 * @property vertices An array of integers representing the order of vertices in the computed TSP tour.
 * @property distance A double representing the total distance of the computed tour.
 */
data class TsmResult(
    val vertices: Array<Int>,
    val distance: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TsmResult

        if (distance != other.distance) return false
        if (!vertices.contentEquals(other.vertices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = distance.hashCode()
        result = 31 * result + vertices.contentHashCode()
        return result
    }
}

data class HeuristicTsmResult(
    val beforeTwoOpt: TsmResult,
    val afterTwoOpt: TsmResult
)
