

import javax.swing.JFrame;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.util.*;

public class GraphVisualizationFrame extends JFrame {
	
	//This frame visualizes the graph structure for debugging purposes
	
	private final int OFFSET_X = 50;
	private final int OFFSET_Y = 50;

	private static HashMap<Integer,Object> cellMap = new HashMap<Integer,Object>();
	
	private mxGraph graph = new mxGraph();	
	
	public GraphVisualizationFrame()
	{
		super();		
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);	
		this.setSize(1000, 600);
		this.setVisible(true);
	}
	
	
	public void addNode(Particle node)
	{
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try
		{
			Object newV = graph.insertVertex(parent, Integer.toString(node.getGlobalId()),
					node.getGlobalId(), (node.getTimeVector()+1)*OFFSET_X, (node.getId()+1) * OFFSET_Y, 20, 20);
			cellMap.put(node.getGlobalId(), newV);
			
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	public void addEdge(int id1, int id2)
	{
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try
		{			
			graph.insertEdge(parent, null, "", cellMap.get(id1), cellMap.get(id2));			
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}

}
