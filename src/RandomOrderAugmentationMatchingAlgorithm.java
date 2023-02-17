

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;

public class RandomOrderAugmentationMatchingAlgorithm<V,E> implements MatchingAlgorithm<V,E>, WeightedMatchingAlgorithm<V,E> {

	
	private final WeightedGraph<V, E> graph;
	
	
	public RandomOrderAugmentationMatchingAlgorithm(final WeightedGraph<V, E> graph)
	{
		this.graph = graph;
	}
	
	@Override
	public double getMatchingWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<E> getMatching() {
		// TODO Auto-generated method stub
		return null;
	}
}
