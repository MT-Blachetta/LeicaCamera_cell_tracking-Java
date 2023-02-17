/**
 * @author Laura Struensee
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;
import org.jgrapht.Graphs;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleWeightedGraph;


public class Graph {
	
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> maxMatchingGraph;
	
	private static JGraphModelAdapter graphAdapter;
	private static ParticleContainer container;
	
	private final static int POSITION_X = 200;
	private final static int POSITION_Y = 80;
	
	private final static int FRAME_WIDTH = 1000;
	private final static int FRAME_HEIGHT = 800;
	
	private final static int MAX_FRAME_NUMBER = ParticleContainer.MAX_FRAME_NUMBER;
	private static int currentFrame;
	
	public Graph() {
	}
	
	//creation of just one Digraph for everything
	public static SimpleWeightedGraph<String, ShowWeightWeightedEdge> getGraph() {
	    if (graph == null) {
	    	graph = new SimpleWeightedGraph<String, ShowWeightWeightedEdge>(ShowWeightWeightedEdge.class);
	    }
	    return graph;
	  }
	
	public static SimpleWeightedGraph<String, ShowWeightWeightedEdge> getMaxMatchingGraph() {
	    if (maxMatchingGraph == null) {
	    	maxMatchingGraph = new SimpleWeightedGraph<String, ShowWeightWeightedEdge>(ShowWeightWeightedEdge.class);
	    }
	    return maxMatchingGraph;
	  }
	
	
	public void createGraph() {    
		getGraph(); //creates JGraph for first frame, returns already created Digraph for later frames
		
        //getting the particles from the Container class and the actual frame number
        container = ParticleContainer.getInstance();
        currentFrame = ParticleContainer.getCurrentFrame();
        
        //initializes the first frame as graph will first be created when they are already two frames
		//afterwards only one frame at a time will be added
        if(graph.vertexSet().isEmpty()) {
        	for(int cell = 0; cell < container.getParticles().get(0).size(); cell++) {
        		graph.addVertex(String.valueOf(container.getParticles().get(0).get(cell).getGlobalId()));
        		graph.addVertex(String.valueOf(container.getParticles().get(0).get(cell).getGlobalId() * (-1)));
        		//getGlobalID in Particle class changed, added + 1 for getting around the vertex 0 which can't be positive and negative
        	}
        }
        
        //converting particles of the new frame to vertices and add them to graph with their global ID
        for(int cell = 0; cell < container.getParticles().get(currentFrame).size(); cell++) {
        	graph.addVertex(String.valueOf(container.getParticles().get(currentFrame).get(cell).getGlobalId()));
        	graph.addVertex(String.valueOf(container.getParticles().get(currentFrame).get(cell).getGlobalId() * (-1)));
        }
        
        //remove older particles from the last one frame
        
        int removeFrame = currentFrame - MAX_FRAME_NUMBER;
        if(removeFrame >= 0) {
        	for(int cell = 0; cell < container.getParticles().get(removeFrame).size(); cell++) {
        		graph.removeVertex(String.valueOf(container.getParticles().get(removeFrame).get(cell).getGlobalId()));
        		graph.removeVertex(String.valueOf(container.getParticles().get(removeFrame).get(cell).getGlobalId() * (-1)));
        	}
        }
	}
	
	
	
	public void addEdge(Particle currentCell, Particle otherCell, double weight) {
		Graphs.addEdge(graph, String.valueOf(currentCell.getGlobalId()), String.valueOf(otherCell.getGlobalId() * (-1)), weight);
	}
	
	
	
	public void callMatchingAlgorithm(IMatchingAlgorithm algo) throws Exception {
		maxMatchingGraph = algo.calculateMaximumMatching(graph);
	}

	
	
	public void showGraph() {
		//adapter for visualizing graph
		graphAdapter = new JGraphModelAdapter(maxMatchingGraph);
		
		container = ParticleContainer.getInstance();
		
		//reduces the shown frames to NUMBER_OF_FRAMES
		int startingFrame;
        if(currentFrame >= MAX_FRAME_NUMBER)
        	startingFrame = currentFrame - MAX_FRAME_NUMBER + 1;
        else
        	startingFrame = 0;
        int xPosCor = 0; //correction for the x Position
        
        //positioning of the vertices
        for(int frame = startingFrame; frame <= currentFrame; frame++) {
        	for(int cell = 0; cell < container.getParticles().get(frame).size(); cell++) {
        		positionVertexAt(String.valueOf(container.getParticles().get(frame).get(cell).getGlobalId()),
        				xPosCor * POSITION_X + 70, (int)container.getParticles().get(frame).get(cell).getId() * POSITION_Y);
        		positionVertexAt(String.valueOf(container.getParticles().get(frame).get(cell).getGlobalId() * (-1)),
        				xPosCor * POSITION_X, (int)container.getParticles().get(frame).get(cell).getId() * POSITION_Y);
        	}
        	xPosCor++;
        }
        
		//creation of a window to display the graph
        JGraph visualGraph = new JGraph(graphAdapter);
        JPanel panelGraph = new JPanel();
        panelGraph.add(visualGraph);
        JScrollPane scrollPane = new JScrollPane(panelGraph);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
    	JFrame frame = new JFrame("JGraph");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.WHITE);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.getContentPane().add(panel);
		frame.setVisible(true);
		
	}

	/**
	 * @author Barak Naveh
	 */
	private void positionVertexAt(Object vertex, int x, int y) {
        DefaultGraphCell cell = graphAdapter.getVertexCell(vertex);
        AttributeMap vertexAttributes = cell.getAttributes();
        Rectangle2D rect = vertexAttributes.createRect(new Point(x,y),45);
        
        Map attr = cell.getAttributes();
        Map cellAttr = new HashMap();
        
        GraphConstants.setBounds(attr, rect);
        cellAttr.put(cell, attr);

        ParentMap m = new ParentMap();
        graphAdapter.edit(cellAttr, null, m, null);
    }
}