

import java.util.*;

import org.jgraph.*;
import org.jgrapht.graph.*;


public class TestJGraph {
	
   

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Test");
		
		
		
		
//		LpSolve lp;
//        int Ncol, j, ret = 0;
//        
//        //Number of variables (edges)
//        Ncol = 1;
//        
//        
//        int[] colno = new int[Ncol];
//        double[] row = new double[Ncol];
//        
//        
//		try {
//			lp = LpSolve.makeLp(0, Ncol);
//		} catch (LpSolveException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		System.out.println("Test2");
		
		//Here a simple TestGraph is created for testing the GPA, Greedy and ROMA-Algorithms
		SimpleWeightedGraph<String, ShowWeightWeightedEdge> testGraph = new SimpleWeightedGraph<String, ShowWeightWeightedEdge>(ShowWeightWeightedEdge.class);
		
		testGraph.addVertex("1");
		testGraph.addVertex("2");
		testGraph.addVertex("3");
		testGraph.addVertex("4");
		
		
		ShowWeightWeightedEdge e1 = testGraph.addEdge("1", "2");
		testGraph.setEdgeWeight(e1, 8);
		
		
		e1 = testGraph.addEdge("1", "3");
		testGraph.setEdgeWeight(e1, 5);
		
		e1 = testGraph.addEdge("2", "3");
		testGraph.setEdgeWeight(e1, 9);
		
		e1 = testGraph.addEdge("2", "4");
		testGraph.setEdgeWeight(e1, 3);
		
		e1 = testGraph.addEdge("3", "4");
		testGraph.setEdgeWeight(e1, 16);
		try {
			SimpleWeightedGraph maxMatchingGreedyGraph = GreedyMaxWeightMatchingAlgorithm.getInstance().calculateMaximumMatching(testGraph);
	
			//SimpleWeightedGraph maxMatchingLPGraph = LPSolveMaxWeightMatchingAlgorithm.getInstance().calculateMaximumMatching(testGraph);
			System.out.println("Test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
