public class ExNode {
	int pID;
	double[] weights;

	public ExNode() {
	}

	public ExNode(double[] weightlist, int PID){
		this.weights = weightlist;
		this.pID = PID;
	}



	public double getEntry(int lid){
		return weights[lid];

	}
}
