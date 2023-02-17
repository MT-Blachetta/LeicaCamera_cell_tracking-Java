import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;

public class MultipartiteTrackingAlgorithm implements ITrackingAlgorithm {

	private static MultipartiteTrackingAlgorithm instance = null;
	private final static int MAX_FRAME_NUMBER = ParticleContainer.MAX_FRAME_NUMBER;
	
	private MultipartiteTrackingAlgorithm()
	{	
	}

	public static MultipartiteTrackingAlgorithm getInstance()
	{
		if(instance==null)
			instance=new MultipartiteTrackingAlgorithm();
		return instance;
	}
	
	public void track() throws Exception
	{
		//New: Create extension digraph
		System.out.println("Create extension graph");
		this.createExtensionGraph();
		//Compute edge weights
		System.out.println("Calculate cost matrix");
		ParticleContainer.getInstance().calculateCostMatrix();
		//logger.trace("Do Floyd path reconstruction");
		//double[][] pathDistances = this.floydWithPathReconstruction();
		//shows the calculated matching on a new frame with the graph
		System.out.println("Compute False Hypothesis Replacement");
		FalseHypothesisReplacement.executeFalseHypothesisReplacement();
		System.out.println("Create Max Path Cover Graph");
		this.createMaxPathCoverGraph();
		//compute the FalseHypothesis edge replacement

		if(GUIPluginMain.__DEBUG_MODE_GRAPH__) {
			Graph splitGraph = ParticleContainer.getInstance().getSplitGraph();
			splitGraph.showGraph();
		}
	}
	
	public void createExtensionGraph()
	{		
		//1. Take last vertices and create extension edges to every next vertex
		ParticleContainer container = ParticleContainer.getInstance();
		//get last particlelist
		int currentFrame = ParticleContainer.getCurrentFrame();
		List<Particle> preCells = container.getParticles().get(currentFrame-1);
		List<Particle> cells = container.getParticles().get(currentFrame);
		
		for(int cell = 0; cell < preCells.size(); cell++) {
			Particle currentParticle = preCells.get(cell);
			currentParticle.getExtensionSuccessors().addAll(cells);
		}
		
		//2. Look for vertices with no successor in previous Lists and look for a new successor in new List
		//ToDo: Check how many frames max back are possible and neccesary, especially when creating correction edges.
		int firstFrame = currentFrame - MAX_FRAME_NUMBER + 1;
		if(firstFrame < 0)
			firstFrame = 0;
		for(int frame = firstFrame; frame < currentFrame - 2; frame++)
		{
			List<Particle> currentList = container.getParticles().get(frame);
			for(int cell = 0; cell < currentList.size(); cell++)
			{
				//Check if there is a successor
				if(currentList.get(cell).getOldEdgeSuccessor() == null)
				{
					//No successor available.
					currentList.get(cell).getExtensionSuccessors().addAll(cells);					
				}
				else
				{
					//3. Make correction edges from every nonterminal vertex to every new vertex!
					currentList.get(cell).getCorrectionSuccessors().addAll(cells);
					//Check: Is it really needed to possibly correct every single already established edge?
				}		
			}
		}
	}
	
	public void createMaxPathCoverGraph()
	{
		ParticleContainer container = ParticleContainer.getInstance();
		
		Graph graph = ParticleContainer.getInstance().getSplitGraph();

		
		
		SimpleWeightedGraph<String,ShowWeightWeightedEdge> maxMatchingGraph = graph.getMaxMatchingGraph();
		
		int sourceID;
		int targetID;
		Particle sourceParticle;
		Particle targetParticle;
		
		for(ShowWeightWeightedEdge edge : maxMatchingGraph.edgeSet()) {
			
			sourceID = Integer.parseInt(maxMatchingGraph.getEdgeSource(edge));
			targetID = (Integer.parseInt(maxMatchingGraph.getEdgeTarget(edge)) * -1);
			
			sourceParticle = container.getParticleObject(sourceID);
			targetParticle = container.getParticleObject(targetID);
		
			sourceParticle.setOldEdgeSuccessor(targetParticle);
			targetParticle.setOldEdgePredecessor(sourceParticle);
		}
	}
	
}
