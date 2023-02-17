

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


public class TestJGraphGPA {
	
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> gpaGraph;
	private static JGraphModelAdapter graphAdapter;
	
	private final static int FRAME_WIDTH = 1800;
	private final static int FRAME_HEIGHT = 400;
	
	public TestJGraphGPA() {
	}
	
	//Digraph that will contain all edges
	public static SimpleWeightedGraph<String, ShowWeightWeightedEdge> getGraph() {
	    if (graph == null) {
	    	graph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
	    }
	    return graph;
	  }
	
	//Digraph that is the product of using the Greedy Algorithm
	public static SimpleWeightedGraph<String, ShowWeightWeightedEdge> getGPAGraph() {
	    if (gpaGraph == null) {
	    	gpaGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
	    }
	    return gpaGraph;
	  }
	
	public static void main(String[] args) {    
		getGraph();
		
		TestJGraphGPA test = new TestJGraphGPA();
		test.createGraph();
		
		getGPAGraph();
		gpaGraph = GlobalPathsAlgorithm.getInstance().calculateMaximumMatching(graph);

		test.showGraph("Graph: Original Graph", false);
		test.showGraph("GPAGraph: After GPA Algorithm", true);
		
		test.expandGraph();
		gpaGraph = GlobalPathsAlgorithm.getInstance().calculateMaximumMatching(graph);
		
		test.showGraph("Graph: Original Graph", false);
		test.showGraph("GPAGraph: After GPA Algorithm", true);
		
		System.out.println("Programm beendet");
	}
	
	private void createGraph() {
//		graph.addVertex("1");
//		graph.addVertex("2");
//		graph.addVertex("3");
//		graph.addVertex("4");
//		
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(2), 55);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(3), 70);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(4), 2);
		
		
		graph.addVertex(String.valueOf(1));
		graph.addVertex(String.valueOf(2));
		graph.addVertex(String.valueOf(3));
		graph.addVertex(String.valueOf(4));
		graph.addVertex(String.valueOf(5));
		graph.addVertex(String.valueOf(6));
		graph.addVertex(String.valueOf(7));
		
	}
	
	public void expandGraph() {
		getGraph();
		
		graph.addVertex(String.valueOf(8));
		graph.addVertex(String.valueOf(9));
		graph.addVertex(String.valueOf(10));
		graph.addVertex(String.valueOf(11));
		graph.addVertex(String.valueOf(12));
		graph.addVertex(String.valueOf(13));
		graph.addVertex(String.valueOf(14));
		graph.addVertex(String.valueOf(15));
		
		Graphs.addEdge(graph, String.valueOf(8), String.valueOf(10), 14);
		Graphs.addEdge(graph, String.valueOf(10), String.valueOf(1), 37);
		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(4), 1);
		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(11), 34);
		
		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(6), 27);
		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(12), 78);
		Graphs.addEdge(graph, String.valueOf(12), String.valueOf(9), 24);
		Graphs.addEdge(graph, String.valueOf(9), String.valueOf(2), 4);
		
		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(5), 0.5);	
		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(7), 0.05);
		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(13), 1);
		Graphs.addEdge(graph, String.valueOf(13), String.valueOf(3), 54);
		
		Graphs.addEdge(graph, String.valueOf(14), String.valueOf(15), 333);
	
	}

//	private void createGraph() {
////		graph.addVertex("1");
////		graph.addVertex("2");
////		graph.addVertex("3");
////		graph.addVertex("4");
////		
////		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(2), 55);
////		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(3), 70);
////		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(4), 2);
//		
//		
//		graph.addVertex(String.valueOf(1));
//		graph.addVertex(String.valueOf(2));
//		graph.addVertex(String.valueOf(3));
//		graph.addVertex(String.valueOf(4));
//		graph.addVertex(String.valueOf(5));
//		graph.addVertex(String.valueOf(6));
//		graph.addVertex(String.valueOf(7));
//		
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(4), 1);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(5), 73);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(6), 45);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(7), 786);
//		
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(4), 768);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(5), 78);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(6), 27);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(7), 455);
//		
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(4), 2);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(5), 0.5);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(6), 278);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(7), 482);
//		
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(6), 34);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(7), 78);
//		
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(6), 458);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(7), 0.5);
//	}
//	
//	public void expandGraph() {
//		getGraph();
//		
//		graph.addVertex(String.valueOf(8));
//		graph.addVertex(String.valueOf(9));
//		graph.addVertex(String.valueOf(10));
//		graph.addVertex(String.valueOf(11));
//		graph.addVertex(String.valueOf(12));
//		graph.addVertex(String.valueOf(13));
//		graph.addVertex(String.valueOf(14));
//		graph.addVertex(String.valueOf(15));
//		
//		Graphs.addEdge(graph, String.valueOf(14), String.valueOf(15), 333);
//		
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(8), 247);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(9), 45);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(10), 347);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(11), 58);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(12), 78);
//		Graphs.addEdge(graph, String.valueOf(1), String.valueOf(13), 869);
//		
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(8), 458);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(9), 4);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(10), 76);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(11), 63);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(12), 786);
//		Graphs.addEdge(graph, String.valueOf(2), String.valueOf(13), 327);
//		
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(8), 75);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(9), 542);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(10), 54);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(11), 86);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(12), 94);
//		Graphs.addEdge(graph, String.valueOf(3), String.valueOf(13), 54);
//		
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(8), 45);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(9), 435);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(10), 48);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(11), 34);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(12), 49);
//		Graphs.addEdge(graph, String.valueOf(4), String.valueOf(13), 738);
//		
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(8), 478);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(9), 3);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(10), 45);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(11), 87);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(12), 15);
//		Graphs.addEdge(graph, String.valueOf(5), String.valueOf(13), 25);
//		
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(8), 245);
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(9), 254);
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(10), 45);
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(11), 64);
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(12), 78);
//		Graphs.addEdge(graph, String.valueOf(6), String.valueOf(13), 645);
//		
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(8), 42);
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(9), 48);
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(10), 754);
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(11), 57);
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(12), 547);
//		Graphs.addEdge(graph, String.valueOf(7), String.valueOf(13), 1);
//		
//		Graphs.addEdge(graph, String.valueOf(8), String.valueOf(11), 45);
//		Graphs.addEdge(graph, String.valueOf(8), String.valueOf(12), 378);
//		Graphs.addEdge(graph, String.valueOf(13), String.valueOf(8), 453);
//		
//		Graphs.addEdge(graph, String.valueOf(9), String.valueOf(10), 457);
//		Graphs.addEdge(graph, String.valueOf(9), String.valueOf(11), 3245);
//		Graphs.addEdge(graph, String.valueOf(9), String.valueOf(12), 24);
//		Graphs.addEdge(graph, String.valueOf(13), String.valueOf(9), 4);
//	
//	}
	
	private void showGraph(String titel, boolean greed) {
		if (greed) {
			getGPAGraph();
			graphAdapter = new JGraphModelAdapter(gpaGraph);
		}
		else {
			getGraph();
			graphAdapter = new JGraphModelAdapter(graph);
		}
		
		Object[] vertices = graph.vertexSet().toArray();

		positionVertexAt("1", 150, 0);
		positionVertexAt("2", 150, 80);
		positionVertexAt("3", 150, 160);
		positionVertexAt("4", 500, 0);
		positionVertexAt("5", 500, 160);
		positionVertexAt("6", 850, 0);
		positionVertexAt("7", 850, 80);
		if(graph.containsVertex("8")) {
			positionVertexAt("8", 1200, 0);
			positionVertexAt("9", 1200, 80);
			positionVertexAt("10", 1550, 0);
			positionVertexAt("11", 1550, 80);
			positionVertexAt("12", 1550, 160);
			positionVertexAt("13", 1550, 240);
			positionVertexAt("14", 1700, 0);
			positionVertexAt("15", 1700, 80);
		}
		

		
		//creation of a window to display the graph
        JGraph visualGraph = new JGraph(graphAdapter);
        JPanel panelGraph = new JPanel();
        panelGraph.add(visualGraph);
        JScrollPane scrollPane = new JScrollPane(panelGraph);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
    	JFrame frame = new JFrame(titel);
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