

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.WeightedMatchingAlgorithm;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GreedyMaxWeightMatchingAlgorithm<V,E> implements IMatchingAlgorithm {

	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph;
	private static double edgeWeight = 0;
	private static GreedyMaxWeightMatchingAlgorithm instance = null;
	
	private GreedyMaxWeightMatchingAlgorithm()
	{		
	}
	
	public static GreedyMaxWeightMatchingAlgorithm getInstance()
	{
		if (instance == null)
			instance = new GreedyMaxWeightMatchingAlgorithm();
		return instance;
	}
	

	
	public SimpleWeightedGraph<String, ShowWeightWeightedEdge> calculateMaximumMatching(SimpleWeightedGraph<String, ShowWeightWeightedEdge> graphParam) {
		graph = graphParam;
		//create deep copy of graph as it will get all edges removed in the process
		SimpleWeightedGraph<String, ShowWeightWeightedEdge> copyOfGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
		Object[] vertices = graph.vertexSet().toArray();
		
		for (int i = 0; i < vertices.length; i++) {
			copyOfGraph.addVertex(String.valueOf(vertices[i]));
		}
		
		for (ShowWeightWeightedEdge currentEdge : graph.edgeSet())
		{
			ShowWeightWeightedEdge newEdge = copyOfGraph.addEdge(graph.getEdgeSource(currentEdge), graph.getEdgeTarget(currentEdge));
			copyOfGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(currentEdge));
		}

		//create a greedyGraph which only gets the vertices of graph, edges will be added dependent on their weight later
		SimpleWeightedGraph<String, ShowWeightWeightedEdge> greedyGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
		for (int i = 0; i < vertices.length; i++) {
			greedyGraph.addVertex(String.valueOf(vertices[i]));
		}
		
		
		while(copyOfGraph.edgeSet().size() > 0)
		{
			double currentWeight = 0;
			double maxWeight = Double.MAX_VALUE;
			String maxCell1 = null;
			String maxCell2 = null;
			
			
			
			
			//search for the highest weighted edge and store its weight as well as the vertices connected to it
			for(ShowWeightWeightedEdge currentEdge : copyOfGraph.edgeSet())
			{
				currentWeight = copyOfGraph.getEdgeWeight(currentEdge);
				
				if (currentWeight < maxWeight) {
					maxWeight = currentWeight;
					maxCell1 = copyOfGraph.getEdgeSource(currentEdge);
					maxCell2 = copyOfGraph.getEdgeTarget(currentEdge);
				}
			}
			
			//end loop if there is no maximum weight edge or max weight edge exceeds limit.
			if (maxWeight == 0 )//|| maxWeight > 20)
				break;
			
			//save edge with maximal weight in greedyGraph
 			if(copyOfGraph.getEdge(maxCell1, maxCell2) != null) {
				greedyGraph.addEdge(maxCell1, maxCell2);
				greedyGraph.setEdgeWeight(greedyGraph.getEdge(maxCell1, maxCell2), maxWeight);
			}
			
			//remove all edges connected to the SourceVertex			
			for(ShowWeightWeightedEdge removeEdge : graph.edgesOf(maxCell1))
			{
				copyOfGraph.removeEdge(maxCell1, copyOfGraph.getEdgeTarget(removeEdge));
			}
			
			//remove all edges connected to the TargetVertex
			for(ShowWeightWeightedEdge removeEdge : graph.edgesOf(maxCell2))
			{
				copyOfGraph.removeEdge(copyOfGraph.getEdgeSource(removeEdge), maxCell2);
			}
		}
		
		//return the greedyGraph which only has the maximum edges saved
		return greedyGraph;
	}
}