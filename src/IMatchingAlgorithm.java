

import org.jgrapht.graph.SimpleWeightedGraph;

public interface IMatchingAlgorithm {

	public SimpleWeightedGraph<String, ShowWeightWeightedEdge> calculateMaximumMatching(SimpleWeightedGraph<String, ShowWeightWeightedEdge> graphParam) throws Exception;
	
}
