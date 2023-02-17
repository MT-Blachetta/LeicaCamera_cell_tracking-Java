

import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class ShowWeightWeightedEdge extends DefaultWeightedEdge {
	
	
	private String edgeName;
	
	@Override
	public String toString() {
		return "[" + getSource() + ":" + getTarget() + "]" + " " + getWeight();
	}
	
	public Object getSourceVertex() {
		Object sourceVertex = getSource();
		return sourceVertex;
	}
	
	public Object getTargetVertex() {
		Object targetVertex = getTarget();
		return targetVertex;
	}

	public String getEdgeName() {
		return edgeName;
	}

	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}
}