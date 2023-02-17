/**
 * @author Laura Struensee
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;




public class FalseHypothesisReplacement {
	
	private static ParticleContainer container;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> maxMatchingGraph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> fhrGraph;
	private static IMatchingAlgorithm algo = ParticleContainer.getIMatchingAlgorithm();
	
	private final static int MAX_FRAME_NUMBER = ParticleContainer.MAX_FRAME_NUMBER;
	private final static int MAX_CELL_NUMBER = container.getInstance().MAX_POSSIBLE_CELL_NUMBER;
	private static int currentFrame;
	
	//stores all unmatched cells
	private static List<Integer> cellsPositive;
	private static List<Integer> cellsNegative;
	
	public static void executeFalseHypothesisReplacement()  throws Exception
	{
		maxMatchingGraph = Graph.getMaxMatchingGraph();
		fhrGraph = new SimpleWeightedGraph<String, ShowWeightWeightedEdge>(ShowWeightWeightedEdge.class);
		container = ParticleContainer.getInstance();
		currentFrame = ParticleContainer.getCurrentFrame();
		
		cellsPositive = new ArrayList<Integer>();
		cellsNegative = new ArrayList<Integer>();
		
		int firstFrame = currentFrame+1 - MAX_FRAME_NUMBER;
		if (firstFrame < 1)
			firstFrame = 1;
		int vertexToInt;
		
		//searched for all vertices without an edge in the Graph with the Maximum Matching
		//those vertices will be stored in a list with outgoing(positive) and incoming(negative) vertices
		for(String vertex : maxMatchingGraph.vertexSet()) {
			vertexToInt = Integer.parseInt(vertex);
			
			if(maxMatchingGraph.degreeOf(vertex) == 0 && Integer.parseInt(vertex) > 0 && 
					vertexToInt/(currentFrame * MAX_CELL_NUMBER) < 1) {
						cellsPositive.add(vertexToInt);
						fhrGraph.addVertex(vertex);
			}
			else if(maxMatchingGraph.degreeOf(vertex) == 0 && Integer.parseInt(vertex) < 0 &&
					vertexToInt/(firstFrame * MAX_CELL_NUMBER) < 0) {
						cellsNegative.add(vertexToInt);
						fhrGraph.addVertex(vertex);
			}
		}
		
		//two frames don't have correction edges, they only start to occur with 3 frames
		
		//Nothing happens here. False edges are already removed within the MaxMatching calculation
		
		//if(currentFrame > 1)
		//	falseEdgeRemovement();
		
		Collections.sort(cellsPositive);
		Collections.sort(cellsNegative, Collections.reverseOrder());
		
		graph = Graph.getGraph();
		
		int pointerCellsPos = 0;
		int pointerCellsNeg = 0;
		
		
		//For every vertex of plus frames, add an edge to every minus frame, if abs value of plus frame is lower than minus frame
		for(int i=0; i<cellsPositive.size(); i++)
		{
			for(int j=0; j<cellsNegative.size(); j++)
			{
				if((Math.abs(cellsPositive.get(i))) < Math.abs(cellsNegative.get(j)))
				{
					String positiveCellString = String.valueOf(cellsPositive.get(i));
					String negativeCellString = String.valueOf(cellsNegative.get(j));
					
					Particle particleStart = ParticleContainer.getInstance().getParticleObject(cellsPositive.get(i));
					Particle particleTarget = ParticleContainer.getInstance().getParticleObject(Math.abs(cellsNegative.get(j)));
					
					if(particleStart.getTimeVector() != particleTarget.getTimeVector())
					{						
						fhrGraph.addEdge(positiveCellString,negativeCellString);
						double edgeWeight = particleStart.CompareTo(particleTarget);
						fhrGraph.setEdgeWeight(fhrGraph.getEdge(positiveCellString, negativeCellString), edgeWeight);
					}
					
				}
			}
		}
		
		
//		//solve the 2-frame correspondence problem
//		for(int frame = firstFrame; frame <= currentFrame; frame++) {
//			while(pointerCellsPos < cellsPositive.size()) //&& cellsPositive.get(pointerCellsPos)/(frame * MAX_CELL_NUMBER) < 1)
//			{
//				fhrGraph.addVertex(String.valueOf(cellsPositive.get(pointerCellsPos)));
//				pointerCellsPos++;
//			}
//			while(pointerCellsNeg < cellsNegative.size()) // && cellsNegative.get(pointerCellsNeg)/((frame + 1) * MAX_CELL_NUMBER) > -1) {
//				fhrGraph.addVertex(String.valueOf(cellsNegative.get(pointerCellsNeg)));
//				pointerCellsNeg++;
//			}
//			
//			List<String> vertices = new ArrayList<String>();
//			vertices.addAll(fhrGraph.vertexSet());
//			String sourceVertex;
//			String targetVertex;
//			
//			//2-frame correspondence only has to be solved if there is at least one vertex in each frame
//			if(!cellsPositive.isEmpty() && !cellsNegative.isEmpty()) {
//				
//				//initialize the graph on which the matching algorithm will be used
//				for(int source = 0; source < pointerCellsPos; source++) {
//					for(int target = 0; target < pointerCellsNeg; target++) {
//						
//						sourceVertex = String.valueOf(vertices.get(source));
//						targetVertex = String.valueOf(vertices.get(target));
//						
//						if(graph.containsEdge(sourceVertex, targetVertex)) {
//							fhrGraph.addEdge(sourceVertex, targetVertex);
//							fhrGraph.setEdgeWeight(fhrGraph.getEdge(sourceVertex, targetVertex), graph.getEdgeWeight(graph.getEdge(sourceVertex, targetVertex)));
//						}
//					}
//				}
				
				//uses the same Matching Algorithm as before
		fhrGraph = algo.calculateMaximumMatching(fhrGraph);
		
		
		//inserts the new found edges into the Matching Graph
		for (ShowWeightWeightedEdge currentEdge : fhrGraph.edgeSet())
		{
			maxMatchingGraph.addEdge(fhrGraph.getEdgeSource(currentEdge), fhrGraph.getEdgeTarget(currentEdge));
			maxMatchingGraph.setEdgeWeight(maxMatchingGraph.getEdge(fhrGraph.getEdgeSource(currentEdge), fhrGraph.getEdgeTarget(currentEdge)), fhrGraph.getEdgeWeight(currentEdge));
		}
	}
		
	
	
	

	private static void falseEdgeRemovement() {
		Particle current;
		//follows the edges of every cell only from the last frame to check if it's a true path (no correction edge) or a false one
		for(int cell = 0; cell < container.getParticles().get(currentFrame).size(); cell++) {
			current = container.getParticles().get(currentFrame).get(cell);
			followPath(current);
		}
	}
	
	
	
	private static boolean followPath(Particle current) {
		boolean removeEdge = false;
		Particle predecessor = current.getOldEdgePredecessor();
		
		//if the last vertex of the path has no further predecessor, the path has no correction edge
		if(current.getOldEdgePredecessor() == null)
			return false;
		
		//if the predecessor of the last vertex has stored another successor than the vertex itself, a correction edge has been inserted
		if(predecessor.getOldEdgeSuccessor() != current)
			return true;
		
		current = predecessor;
		removeEdge = followPath(current);
		
		//if a correction edge was inserted, the whole path has to be deleted
		//edges that had been inserted wrongly in the Max Matching Graph will be removed
		if(removeEdge) {
			cellsPositive.add(current.getGlobalId());
			cellsNegative.add(current.getOldEdgeSuccessor().getGlobalId() * -1);
			String source = String.valueOf(current.getGlobalId());
			String target = String.valueOf(current.getOldEdgeSuccessor().getGlobalId() * -1);
			maxMatchingGraph.removeEdge(source, target);
			return true;
		}
		//if there is no correction edge, nothing has to be done
		else
			return false;
	}
}