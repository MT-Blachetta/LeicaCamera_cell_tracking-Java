import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//
import org.jgrapht.graph.SimpleWeightedGraph;

import lpsolve.LpSolve;
//
//import lpsolve.*;
//
public class LPSolveMaxWeightMatchingAlgorithm<V,E> implements IMatchingAlgorithm {
	
	
	
	
	
	
	
//	public SimpleWeightedGraph<String, ShowWeightWeightedEdge> calculateMaximumMatching(SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph) throws Exception
//	{
//		//Create deep copy of graph with NamedEdges
//	
//		SimpleWeightedGraph<String, NamedEdge> copyOfGraph = new SimpleWeightedGraph<>(NamedEdge.class);
//		Object[] vertices = graph.vertexSet().toArray();
//		HashMap<Integer,ShowWeightWeightedEdge> edgeDict = new HashMap<Integer,ShowWeightWeightedEdge>();
//		
//		
//		for (int i = 0; i < vertices.length; i++) {
//			copyOfGraph.addVertex(String.valueOf(vertices[i]));
//		}
//        
//    	Integer colCounter = 1;
//    	for (ShowWeightWeightedEdge currentEdge : graph.edgeSet())
//		{
//			NamedEdge newEdge = copyOfGraph.addEdge(graph.getEdgeSource(currentEdge), graph.getEdgeTarget(currentEdge));
//			copyOfGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(currentEdge));   			
//
//			newEdge.setEdgeName(colCounter);
//			currentEdge.setEdgeName(colCounter);
//			edgeDict.put(colCounter, currentEdge);
//			colCounter++;
//		}
//    	
//    	
//    	//Create deep copy for Result Graph    	
//    	copyOfGraph = LPSolver.calculateMaximumMatching(copyOfGraph);
//		
//		
//		
//		SimpleWeightedGraph<String, ShowWeightWeightedEdge> resultGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
//		vertices = copyOfGraph.vertexSet().toArray();
//		edgeDict.clear();
//		
//		
//		for (int i = 0; i < vertices.length; i++) {
//			resultGraph.addVertex(String.valueOf(vertices[i]));
//		}
//        
//    	colCounter = 1;
//    	for (NamedEdge currentEdge : copyOfGraph.edgeSet())
//		{
//    		ShowWeightWeightedEdge newEdge = resultGraph.addEdge(copyOfGraph.getEdgeSource(currentEdge), copyOfGraph.getEdgeTarget(currentEdge));
//			resultGraph.setEdgeWeight(newEdge, copyOfGraph.getEdgeWeight(currentEdge));   			
//
//			newEdge.setEdgeName(colCounter);
//			currentEdge.setEdgeName(colCounter);
//			edgeDict.put(colCounter, newEdge);
//			colCounter++;
//		}
//    	
//    	return resultGraph;
//		
//		
//	}

	
	private static LPSolveMaxWeightMatchingAlgorithm instance = null;
	
	private LPSolveMaxWeightMatchingAlgorithm()
	{		
	}
	
	public static LPSolveMaxWeightMatchingAlgorithm getInstance()
	{
		if (instance == null)
			instance = new LPSolveMaxWeightMatchingAlgorithm();
		return instance;
	}
	public SimpleWeightedGraph<String, ShowWeightWeightedEdge> calculateMaximumMatching(SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph) throws Exception
	{

		Date startTime = new Date();
		//Create LP file
		StringBuilder objectiveFunction = new StringBuilder("max: ");
		StringBuilder constraints = new StringBuilder("");
		StringBuilder vertexDeclaration = new StringBuilder("");
		String lpFile = "";

		
		
		//create deep copy of graph as it will get all edges removed in the process
		//SimpleWeightedGraph<String, ShowWeightWeightedEdge> copyOfGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
		Object[] vertices = graph.vertexSet().toArray();
		HashMap<String,ShowWeightWeightedEdge> edgeDict = new HashMap<String,ShowWeightWeightedEdge>();
		
		
//		for (int i = 0; i < vertices.length; i++) {
//			copyOfGraph.addVertex(String.valueOf(vertices[i]));
//		}
		
		
		//Create LP Solve Model
        int Ncol, j, ret = 0;
        
        //Number of variables (edges)
        Ncol = graph.edgeSet().size();
        
        
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        
        

		
        //if(lp.getLp() == 0)
        //  ret = 1; /* couldn't construct a new model... */

        if(ret == 0) {
        
        	Integer colCounter = 1;
        	for (ShowWeightWeightedEdge currentEdge : graph.edgeSet())
    		{
        		//ShowWeightWeightedEdge newEdge = copyOfGraph.addEdge(graph.getEdgeSource(currentEdge), graph.getEdgeTarget(currentEdge));
    			//copyOfGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(currentEdge));   			
    		//	lp.setColName(colCounter, colCounter.toString());
    		//	lp.setBinary(colCounter, true);
    			vertexDeclaration.append("bin ").append(base26(colCounter)).append(";\r\n");
    			//newEdge.setEdgeName(base26(colCounter));
    			currentEdge.setEdgeName(base26(colCounter));
    			edgeDict.put(base26(colCounter), currentEdge);
    			colCounter++;
    		}

    		//create a greedyGraph which only gets the vertices of graph, edges will be added dependent on their weight later
    		SimpleWeightedGraph<String, ShowWeightWeightedEdge> greedyGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
    		for (int i = 0; i < vertices.length; i++) {
    			greedyGraph.addVertex(String.valueOf(vertices[i]));		
    		}    		
    		
    		//Build constraint for every vertex
    		//Check for every vertex all edges
    		for (String currentVertex : graph.vertexSet())
    		{
    			j = 0;    			
    			for (ShowWeightWeightedEdge currentEdge : graph.edgesOf(currentVertex))
    			{
    				constraints.append(currentEdge.getEdgeName()).append(" + ");    				
    			}
    			if(graph.edgesOf(currentVertex).size() > 0)
    			{
    				constraints.delete(constraints.length()-3, constraints.length());
	    			//constraints = constraints.substring(0, constraints.length() - 3);
	    			constraints.append(" <= 1;\r\n");
    			}
    		}
    		
    		//Set Objective function
    		//Iterate over all edges and multiply edge weight
    		
    		j = 0;
    		for (ShowWeightWeightedEdge currentEdge : graph.edgeSet())
    		{
    			double edgeWeight = graph.getEdgeWeight(currentEdge);
    			edgeWeight = 99999 - edgeWeight;
    			objectiveFunction.append(edgeWeight).append(" ").append(currentEdge.getEdgeName()).append(" + ");
    		}
    		objectiveFunction = objectiveFunction.delete(objectiveFunction.length()-3,objectiveFunction.length()).append(";\r\n");
    		/* set the objective in lpsolve */
            lpFile = objectiveFunction + "\r\n" + constraints + "\r\n" + vertexDeclaration;
            
            byte data[] = lpFile.getBytes();
            FileOutputStream out = new FileOutputStream("tempLP.lp");
            out.write(data);
            out.close();
            
            //Call external lp solver
            String command = "cmd /c lp_solve -s tempLP.lp > output.txt";
            Date startTimeLP = new Date();
            Process p = Runtime.getRuntime().exec(command);
            Date endTimeLP = new Date();
            //Wait here to finish
            
            String line;
            Process proc = Runtime.getRuntime().exec
            	    (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            boolean lp_solveAv = true;
            while(lp_solveAv)
            {
            	lp_solveAv = false;
                proc = Runtime.getRuntime().exec
                	    (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
                input =
                        new BufferedReader(new InputStreamReader(proc.getInputStream()));
	            while ((line = input.readLine()) != null) {
	                if(line.contains("lp_solve.exe"))
	                	lp_solveAv = true;	                
	            }
	            input.close();
	            Thread.sleep(100);
            }
            
            //Read output file and rebuild the model
            List<String> result = readFile("output.txt");
            boolean start = false;
            for(int k=0; k<result.size();k++)
            {            	
            	if(start && result.get(k).isEmpty() == false)
            	{
            		int endVariableIndex = result.get(k).indexOf(" ");
            		String variableName = result.get(k).substring(0, endVariableIndex);
            		int numberValue = Integer.parseInt(result.get(k).substring(result.get(k).length()-1));
            		if(numberValue == 1)
            		{
            			//Set regarding edge in final graph.
            			ShowWeightWeightedEdge maxEdge = edgeDict.get(variableName);            			
            			greedyGraph.addEdge(maxEdge.getSourceVertex().toString(), maxEdge.getTargetVertex().toString());            			
            		}
            	}  
            	if(result.get(k).contains("Actual values of the variables")) 
            	{
            		start=true;
            	}            	      
            	
            }
//            Date stopTime = new Date();
//       	 	long diffInSeconds = (stopTime.getTime() - startTime.getTime()) / 1000;
//       	 	long diffInSecondsLP = (endTimeLP.getTime() - startTimeLP.getTime()) / 1000;
//       	 	System.out.print(diffInSeconds);
            return greedyGraph;
        }
        return null;
	}
	
	public static String base26(int num) {
		  if (num < 0) {
		    throw new IllegalArgumentException("Only positive numbers are supported");
		  }
		  StringBuilder s = new StringBuilder("aaaaaaa");
		  for (int pos = 6; pos >= 0 && num > 0 ; pos--) {
		    char digit = (char) ('a' + num % 26);
		    s.setCharAt(pos, digit);
		    num = num / 26;
		  }
		  return s.toString();
		}
	
	public static List<String> readFile(String filename)
	{
		BufferedReader br = null;
		List<String> result = new ArrayList<String>();		
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));

			while ((sCurrentLine = br.readLine()) != null) {
				result.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	
}
