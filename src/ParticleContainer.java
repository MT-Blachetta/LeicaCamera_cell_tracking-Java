

/**
 * @author Thomas Temme
 * Container class for particles and positioning of Particles
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleWeightedGraph;







public class ParticleContainer {
	
	public static int MAX_POSSIBLE_CELL_NUMBER = 200; 
	public static int MAX_WELL_PIXEL_SIZEX = 2500;
	public static int MAX_WELL_PIXEL_SIZEY = 2500;
	public static int LOST_CELL_FRAME_THRESHOLD = 6;
	public static int MAX_FRAME_NUMBER = 15;
	public static int MAX_VISIBLE_TRACK_NUMBER = 5;
	
	// Sum of all factors should be equals 1
	public static double VELOCITY_FACTOR = 0.0;
	public static double TIME_FACTOR = 0.3;
	public static double SPATIAL_FACTOR = 0.5;
	public static double BRIGHTNESS_FACTOR = 0.2;
	
	public static int VELOCITY_FRAME_NUMBER = 10;
	public static int COMPARE_SECONDARY_CRITERION_WITHIN_PIXELS = 150;
	
	public static boolean SEND_COMMAND_PIPELINE_TWICE = false;
	public static boolean PHASE_CONTRAST_IMAGES = false;
	
	
	public static final String CAM_COMMAND_PIPELINE = "/cli:Leica_CAM_Tracking /app:matrix /cmd:startscan\r\n"+
"/cli:Leica_CAM_Tracking /app:matrix /cmd:deletelist\r\n"+
"/cli:Leica_CAM_Tracking /app:matrix /cmd:add /tar:camlist /exp:CAM_Scan_HighRes /ext:none /slide:0 /wellX:0 /wellY:0 /fieldX:0 /fieldY:0 /dxpos:0 /dypos:0\r\n"+
"/cli:Leica_CAM_Tracking /app:matrix /cmd:startcamscan /runtime:1 /repeattime:1\r\n"+
"/cli:Leica_CAM_Tracking /app:matrix /sys:1 /cmd:stopwaitingforcam";
	
	public static final double MAX_EDGE_WEIGHT = 99999.5;
	public static final ITrackingAlgorithm TRACKINGALGO = MultipartiteTrackingAlgorithm.getInstance();
	public static final IMatchingAlgorithm MATCHINGALGO = GreedyMaxWeightMatchingAlgorithm.getInstance();
	//public static final IMatchingAlgorithm MATCHINGALGO = LPSolveMaxWeightMatchingAlgorithm.getInstance();	
	
	
	//Map of Frames. Every Frame has a list of particles.
	private Map<Integer, List<Particle>> particles = new HashMap<Integer, List<Particle>>();
	//private ArrayList<ArrayList<Particle>> pathList = new ArrayList<ArrayList<Particle>>();
	
	private int currentDX;
	private int currentDY;
	
	private int globalPositionX=0;
	private int globalPositionY=0;
	
	
	private Double[][][] costMatrix;
	//private double[][] w;
	private double[][] dist;
	private int [][] next;
	private int cellLostForFrames = 0;
	
	//private static final Logger logger = LogManager.getLogger(GUIPluginMain.class);

	private int indexOrder[];
	
	//Äußeres X: Frames, Inneres X: Vertices 
	private List<List<Boolean>> adjacencyMatrixOldEdges = new ArrayList<List<Boolean>>();
	private List<List<Boolean>> adjacencyMatrixExtensionEdges = new ArrayList<List<Boolean>>();
	private List<List<Boolean>> adjacencyMatrixCorrectionEdges = new ArrayList<List<Boolean>>();
	//HashMap<CostMatrixIndex,Double> indexMap = new HashMap<CostMatrixIndex, Double>();	
	private static ParticleContainer instance;
	
	private static boolean documentCreated = false;
	
	private Graph splitGraph = null;
	private static int currentFrame;
	
	private Particle trackingParticle;
	
	
	public static int getCurrentFrame() {
		return currentFrame;
	}
	
	public static void setCurrentFrame(int counter) {
		currentFrame = counter;
	}
	
	public static void deleteOldestFrame() {
		if(currentFrame > MAX_FRAME_NUMBER)
		{
			ParticleContainer container = ParticleContainer.getInstance();
			//If currently tracked particle object is in removing frame, update it!
			if(container.getTrackingParticle().getTimeVector() == currentFrame)
			{
				if(container.getTrackingParticle().getOldEdgeSuccessor() != null)
					container.setTrackingParticle(ParticleContainer.getInstance().getTrackingParticle().getOldEdgeSuccessor());
				else
				{
					System.out.println("Cell lost. Looking for cell closest to midpoint as current cell.");
					int currentFrame = ParticleContainer.getCurrentFrame();
					List<Particle> currentCells = container.getParticles().get(currentFrame);
					int currentX = container.getGlobalPositionX();
					int currentY = container.getGlobalPositionY();
					double minDistance = Double.MIN_VALUE;
					//Closest cell of midpoint is cell next to coordinate
					Particle minParticle = null;
					int minDX=0;
					int minDY=0;
					for(int cell = 0; cell < currentCells.size(); cell++)
					{
						Particle currentParticle = currentCells.get(cell);					
						double dist = euclidDist((int)currentParticle.getX(), (int)currentParticle.getY(), currentX, currentY);
						if(dist < minDistance)
						{
							minParticle = currentParticle;
							minDistance = dist;
							int dx = (int)currentParticle.getX() - currentX;
							int dy = (int)currentParticle.getY() - currentY;
							container.setCurrentDX(dx);
							container.setCurrentDY(dy);
							minDX = dx;
							minDY=dy;						
							container.setTrackingParticle(minParticle);
						}
					}
					if(minParticle!=null)
					{
						ParticleContainer.getInstance().saveCurrentParticlePosition(minParticle);
						System.out.println("Original Cell lost, but tracking cell at midpoint instead. \r\n Moving stage "+minDX+" in x direction and "+minDY+" in y direction.");
					}
					else
					{
						System.out.println("Cell completly lost.");
					}
				}
			}
			getInstance().getParticles().remove(currentFrame-1 - MAX_FRAME_NUMBER);
		}
		
	}
	
	public static double euclidDist(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
	}
	
	public static void saveCurrentParticlePosition(Particle trackParticle) {
		try(FileWriter fw = new FileWriter("./res/tracked-images/000-particle-data.csv", true); 
				BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
			if(!documentCreated) {
				new FileWriter("./res/tracked-images/000-particle-data.csv");
				out.println("Cell ID; xPosition; yPosition;");
				documentCreated = true;
			}	
			out.println(trackParticle.getIdWithinCurrentFrame() +";"+ Math.round(trackParticle.getX()) +";" + Math.round(trackParticle.getY()) +";");
		} 
		catch(IOException e) {
			System.out.println(e);
			System.out.println("File was not saved.");
		}
	}
	
	public Graph getSplitGraph() {
		return splitGraph;
	}

	public void setSplitGraph(Graph splitGraph) {
		this.splitGraph = splitGraph;
	}	
	
	
	public Particle getParticleObject(int globalID)
	{
		int timeID = globalID / MAX_POSSIBLE_CELL_NUMBER;
		int localID = globalID % MAX_POSSIBLE_CELL_NUMBER - 1;
		return particles.get(timeID).get(localID);
	}
	
	public Map<Integer, List<Particle>> getParticles() {
		return particles;
	}
	
//	public HashMap<CostMatrixIndex, Double> getIndexMap() {
//		return indexMap;
//	}
//
//
//	public void setIndexMap(HashMap<CostMatrixIndex, Double> indexMap) {
//		this.indexMap = indexMap;
//	}
//	
//	
//	public double[][] getW() {
//		return w;
//	}
//
//
//	public void setW(double[][] w) {
//		this.w = w;
//	}
	

	public double[][] getDist() {
		return dist;
	}


	public void setDist(double[][] dist) {
		this.dist = dist;
	}


	public int[][] getNext() {
		return next;
	}


	public void setNext(int[][] next) {
		this.next = next;
	}
	
	private void DebugPositionVertexAt(JGraphModelAdapter graphAdapter,Object vertex, int x, int y) {
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


	
	public void calculateCostMatrix() throws Exception
	{
		//Gain function is initally just the euclid distance between two particles
		//Size of first dimension: Number of frames
		//Size of second dimension: Max number of detected cells
		//Assume that there are initally not more than hundred cells in a single frame
		//ToDo: Save indices in sorted order of values, during insertion.
		//START HERE!
		int frameSize = currentFrame+1; 
		int firstFrame = currentFrame+1 - MAX_FRAME_NUMBER;
		if(firstFrame < 0)
			firstFrame = 0;
		
		splitGraph = new Graph();
		splitGraph.createGraph();
		
		costMatrix = new Double[frameSize][MAX_POSSIBLE_CELL_NUMBER][MAX_POSSIBLE_CELL_NUMBER];
		//w = new double[frameSize * MAX_POSSIBLE_CELL_NUMBER][frameSize * MAX_POSSIBLE_CELL_NUMBER];
		
//		for(double[] row: w)
//		{
//			Arrays.fill(row, Double.MAX_VALUE);
//		}
		//ind
		//Arrays.fill(costMatrix, Double.MAX_VALUE);
		//for every frame:
		for(int i = firstFrame; i < frameSize; i++)
		{
			//For every vertex in this frame:
			List<Particle> currentParticleList = particles.get(i);
			for(int j=0; j<currentParticleList.size();j++)
			{
				Particle currentParticle = currentParticleList.get(j);
				int offset = 0;
				if(currentParticle.getOldEdgeSuccessor() != null && currentParticle.getOldEdgeSuccessor().getTimeVector() >= firstFrame)
				{
					double value = currentParticle.CompareTo(currentParticle.getOldEdgeSuccessor());
					//costMatrix[i][j][offset]=value;
				    //w[i*(MAX_POSSIBLE_CELL_NUMBER)+j][currentParticle.getOldEdgeSuccessor().getTimeVector() * (MAX_POSSIBLE_CELL_NUMBER)+currentParticle.getOldEdgeSuccessor().getIdWithinCurrentFrame()]=value;
					//CostMatrixIndex index = new CostMatrixIndex(i,j,offset,EdgeType.OldEdge);
					//indexMap.put(index, value);
					offset = 1;
					
					if(value <= MAX_EDGE_WEIGHT)
						splitGraph.addEdge(currentParticle, currentParticle.getOldEdgeSuccessor(), value);
				}
				
				//calculate cost matrix indices of all successors
				for(int k = 0; k < currentParticle.getExtensionSuccessors().size(); k++)
				{
					if(currentParticle.getExtensionSuccessors().get(k).getTimeVector() >= firstFrame) {
						double value = currentParticle.CompareTo(currentParticle.getExtensionSuccessors().get(k));
					
						//costMatrix[i][j][k+offset] = value;
						//w[i*(MAX_POSSIBLE_CELL_NUMBER)+j][currentParticle.getExtensionSuccessors().get(k).getTimeVector() * (MAX_POSSIBLE_CELL_NUMBER)+currentParticle.getExtensionSuccessors().get(k).getIdWithinCurrentFrame()]=value;
						//CostMatrixIndex index = new CostMatrixIndex(i,j,k+offset,EdgeType.ExtensionEdge);
						//indexMap.put(index, value);
						
						if(value <= MAX_EDGE_WEIGHT)
							splitGraph.addEdge(currentParticle, currentParticle.getExtensionSuccessors().get(k), value);
					}
				}
				offset=offset+currentParticle.getExtensionSuccessors().size();
				
				for(int k = 0; k < currentParticle.getCorrectionSuccessors().size(); k++)
				{
					if(currentParticle.getCorrectionSuccessors().get(k).getTimeVector() >= firstFrame) {
						double value = currentParticle.CompareTo(currentParticle.getCorrectionSuccessors().get(k));
						//costMatrix[i][j][k+offset] = value;
						//w[i*(MAX_POSSIBLE_CELL_NUMBER)+j][currentParticle.getCorrectionSuccessors().get(k).getTimeVector() * (MAX_POSSIBLE_CELL_NUMBER)+currentParticle.getCorrectionSuccessors().get(k).getIdWithinCurrentFrame()]=value;
						//CostMatrixIndex index = new CostMatrixIndex(i,j,k+offset,EdgeType.CorrectionEdge);
						//indexMap.put(index, value);
						
						if(value <= MAX_EDGE_WEIGHT)
							splitGraph.addEdge(currentParticle, currentParticle.getCorrectionSuccessors().get(k), value);
					}
				}				
			}
		}
		//indexMap = sortByValues(indexMap);	
		//if(ParticleContainer.currentFrame >= 21)
		//	splitGraph.callMatchingAlgorithm(MATCHINGALGO);
			//DebugShowGraph(Graph.getGraph());
		splitGraph.callMatchingAlgorithm(MATCHINGALGO);
		//if(ParticleContainer.currentFrame >= 21)
		//	DebugShowGraph(Graph.getMaxMatchingGraph());
	}
	
	
	public void DebugShowGraph(SimpleWeightedGraph maxMatchingGraph)
	{
		JGraphModelAdapter graphAdapter = new JGraphModelAdapter(maxMatchingGraph);
		
		ParticleContainer container = ParticleContainer.getInstance();
		
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
	    		DebugPositionVertexAt(graphAdapter,String.valueOf(container.getParticles().get(frame).get(cell).getGlobalId()),
	    				xPosCor * 200 + 70, (int)container.getParticles().get(frame).get(cell).getId() * 80);
	    		DebugPositionVertexAt(graphAdapter,String.valueOf(container.getParticles().get(frame).get(cell).getGlobalId() * (-1)),
	    				xPosCor * 200, (int)container.getParticles().get(frame).get(cell).getId() * 80);
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
		frame.setSize(1000, 800);
	    frame.getContentPane().add(panel);
		frame.setVisible(true);
	}
	
	 private static HashMap<CostMatrixIndex,Double> sortByValues(HashMap<CostMatrixIndex,Double> map) 
	 { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator()
	       {
	            public int compare(Object o1, Object o2)
	            {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       }
	       );

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap<CostMatrixIndex,Double> sortedHashMap = new LinkedHashMap<CostMatrixIndex,Double>();
	       for (Iterator it = list.iterator(); it.hasNext();)
	       {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put((CostMatrixIndex)entry.getKey(), (Double)entry.getValue());
	       } 
	       return sortedHashMap;
	  }
	
	
	public void addExtensionEdge(int cellNumber, int frameNumber)
	{		
//		if(adjacencyMatrixExtensionEdges.size() <= frameNumber)		
	//		adjacencyMatrixExtensionEdges.add(new ArrayList<Particle>());
		//adjacencyMatrixExtensionEdges.get(frameNumber).add()
		
	}
	
	public void addOldEdge()
	{		
	}
	
	public void addCorrectionEdge()
	{		
	}

	private ParticleContainer() {		
	}
	
	@SuppressWarnings("unchecked")
	public static ParticleContainer getInstance () {
	    if (instance == null) {
	    	instance = new ParticleContainer();
	    }
	    return instance;
	  }

	public Double[][][] getCostMatrix() {
		return costMatrix;
	}	
	
	public int getCurrentDX() {
		return currentDX;
	}

	public void setCurrentDX(int currentDX) {
		this.currentDX = currentDX;
	}
	
	public int getCurrentDY() {
		return currentDY;
	}


	public void setCurrentDY(int currentDY) {
		this.currentDY = currentDY;
	}


	public int getGlobalPositionX() {
		return globalPositionX;
	}


	public void setGlobalPositionX(int globalPositionX) {
		this.globalPositionX = globalPositionX;
	}


	public int getGlobalPositionY() {
		return globalPositionY;
	}


	public void setGlobalPositionY(int globalPositionY) {
		this.globalPositionY = globalPositionY;
	}
	
	public int getCellLostForFrames() {
		return cellLostForFrames;
	}


	public void setCellLostForFrames(int cellLostForFrames) {
		this.cellLostForFrames = cellLostForFrames;
	}

	public static IMatchingAlgorithm getIMatchingAlgorithm() {
		return MATCHINGALGO;
	}

	public Particle getTrackingParticle() {
		return trackingParticle;
	}

	public void setTrackingParticle(Particle trackingParticle) {
		this.trackingParticle = trackingParticle;
	}

}
