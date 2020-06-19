import java.util.*;

/**
 * Library for graph analysis
 * Sunint Bindra, CS10, May 7, 2020, S20, Professor Li, SA7
 * 
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 *
	 * @param g     graph to walk on
	 * @param start initial vertex (assumed to be in graph)
	 * @param steps max number of steps
	 * @return a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * null if start isn't in graph
	 */
	public static <V, E> List<V> randomWalk(Graph<V, E> g, V start, int steps) {
		List<V> listOfPoints = new ArrayList<V>(); // Create new ArrayList to which we will add points
		V a = start; // Set variable a to starting node
		listOfPoints.add(a); // Adds start to list of points
		while (steps > 0) { // To cycle through steps
			if (g.outDegree(a) > 0) { // Checking if there is outgoing edge for node
				int randEdge = (int) (Math.random() * g.outDegree(a)); // Variable for random edge point
				Iterator vIterator = g.outNeighbors(a).iterator(); // Create iterator of outNeighbors from starting point a
				for (int i = 0; i < randEdge; i++) {
					vIterator.next();
				}
				a = (V) vIterator.next(); //Sets new point
				listOfPoints.add(a); // Adds new point to list
			}
			steps--; // Decrement steps
		}
	return listOfPoints; // Returns list of points
	}

	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		class VerticesComparator implements Comparator<V> {  // Creates ner VerticesComparator class implementing comparator
			public int compare(V v1, V v2) {  // Method to compare vertices
				return g.inDegree(v2) - g.inDegree(v1);  // Returns vertex with higher in-degree (incoming edges)
			}
		}
		List<V> sortedList = new ArrayList<V>();  // Creates new ArrayList of sorted points
		for (V vertexPoints : g.vertices()) {  // Iterates through all vertices
			sortedList.add(vertexPoints);  //Adds vertices to list
		}
		sortedList.sort((new VerticesComparator()));  // Sorts list of vertices using compare method
		return sortedList;  // Returns the sorted list
	}
}
