

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;


public class GlobalPathsAlgorithm<V,E> implements IMatchingAlgorithm
{
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> graph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> copyOfGraph;
	private static SimpleWeightedGraph<String, ShowWeightWeightedEdge> gpaGraph;
	
	private static List<SimpleGraphPath<String,ShowWeightWeightedEdge>> pathList;
	private static List<String> tempVertices;
	private static List<List<ShowWeightWeightedEdge>> pathMaxMatching;
	private static double tempWeight;
	
	private static GlobalPathsAlgorithm instance = null;	
	private static boolean checkEdge = false;
	
	private static double EDGEWEIGHT = 9999.0;
	
	
	private GlobalPathsAlgorithm()
	{		
	}
	
	public static GlobalPathsAlgorithm getInstance()
	{
		if(instance==null)
			instance = new GlobalPathsAlgorithm();
		return instance;
	}
	
	public SimpleWeightedGraph<String, ShowWeightWeightedEdge> calculateMaximumMatching(SimpleWeightedGraph<String, ShowWeightWeightedEdge> graphParam) {
		graph = graphParam;
		pathList = new ArrayList<SimpleGraphPath<String, ShowWeightWeightedEdge>>();
		
		//create deep copy of graph but merge the vertices (positive and negative) to one, needed to calculate applicable edges
		copyOfGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
		Object[] vertices = graph.vertexSet().toArray();
				
		for (int i = 0; i < vertices.length; i++) {
			copyOfGraph.addVertex(String.valueOf(vertices[i]));
		}
				
		for (ShowWeightWeightedEdge currentEdge : graph.edgeSet())
		{
			ShowWeightWeightedEdge newEdge = copyOfGraph.addEdge(graph.getEdgeSource(currentEdge), graph.getEdgeTarget(currentEdge));
			copyOfGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(currentEdge));
		}

		
		//create a gpaGraph which only gets the vertices of graph
		gpaGraph = new SimpleWeightedGraph<>(ShowWeightWeightedEdge.class);
		for (int i = 0; i < vertices.length; i++) {
			gpaGraph.addVertex(String.valueOf(vertices[i]));
		}
		
		while(copyOfGraph.edgeSet().size() > 0) {
			double currentWeight = 0;
			double minWeight = Double.MAX_VALUE;
			String minCellSource = null;
			String minCellTarget = null;
			
			//search for the highest weighted edge and store its weight as well as the vertices connected to it
			for(ShowWeightWeightedEdge currentEdge : copyOfGraph.edgeSet())
			{
				currentWeight = copyOfGraph.getEdgeWeight(currentEdge);
				
				if (currentWeight < minWeight) {
					minWeight = currentWeight;
					minCellSource = copyOfGraph.getEdgeSource(currentEdge);
					minCellTarget = copyOfGraph.getEdgeTarget(currentEdge);
				}
			}
			
			//end loop if there is no minimum weight edge
			if (minWeight == Double.MAX_VALUE)
				break;
			
			checkEdge = isEdgeApplicable(minCellSource, minCellTarget);
			
			if(checkEdge) {
				gpaGraph.addEdge(minCellSource, minCellTarget);
				gpaGraph.setEdgeWeight(gpaGraph.getEdge(minCellSource, minCellTarget), minWeight);
				pathList.add(new SimpleGraphPath(gpaGraph, tempVertices, 0));
				copyOfGraph.removeEdge(minCellSource, minCellTarget);
			}
			else {
				copyOfGraph.removeEdge(minCellSource, minCellTarget);
			}
		}
		
		calculateMaxWeightMatching();
		
		return copyOfGraph;
	}
	
	public static boolean isEdgeApplicable(String minCellSource, String minCellTarget) {
		
		//stores vertices of the current run into a List that will be later stored into the PathList
		tempVertices = new ArrayList<String>();
		
		//Applicable: connection of two endpoints of different paths, connection of two endpoints of the same path forming a circle of an even number of edges
		//Not applicable: connection to an inner vertex, forming of a circle of an odd number of edges
		int degreeCellSource = gpaGraph.degreeOf(minCellSource);
		int degreeCellTarget = gpaGraph.degreeOf(minCellTarget);
			
		//vertex with two edges is always an inner vertex (degree = 2)
		if (degreeCellSource == 2 || degreeCellTarget == 2) {
			return false;
		}
		
		//two lone vertices can always be connected (degree = 0)
		else if (degreeCellSource == 0 && degreeCellTarget == 0) {
			tempVertices.add(minCellSource);
			tempVertices.add(minCellTarget);
			return true;
		}
		
		//if one vertex has no edge and the other has one, the lone vertex can be added
		//where on the path depends if it connects to the start or end vertex of the path
		else if (degreeCellSource == 0 || degreeCellTarget == 0) {
			
			String tempCell1;
			String tempCell2;
			if (degreeCellSource == 0) {
				tempCell1 = minCellTarget;
				tempCell2 = minCellSource;
			}
			else if (degreeCellTarget == 0) {
				tempCell1 = minCellSource;
				tempCell2 = minCellTarget;
			}
			else
				return false;
			
			for(int i = 0; i < pathList.size(); i++) {
				
				if(tempCell1.equals(pathList.get(i).getEndVertex())) {
					tempVertices.addAll(pathList.get(i).getVertexList());
					tempVertices.add(tempCell2);
					pathList.remove(i);
					return true;	
				}
				if(tempCell1.equals(pathList.get(i).getStartVertex())) {
					tempVertices.add(tempCell2);
					tempVertices.addAll(pathList.get(i).getVertexList());
					pathList.remove(i);
					return true;
				}
			}
			return false;
		}


		//vertices with 1 edge can be of the same path and connect to a circle or be of different paths
		else if (degreeCellSource == 1 && degreeCellTarget == 1) {
			
			for(int i = 0; i < pathList.size(); i++) {
				
				//searches for the path where the SourceVertex of the edge is either the starting or the end point
				if(minCellSource.equals(pathList.get(i).getStartVertex()) || minCellSource.equals(pathList.get(i).getEndVertex())) {
					
					//check if the TargetVertex is on the same path -> if true, the edge will close a circle
					if(minCellTarget.equals(pathList.get(i).getStartVertex()) || minCellTarget.equals(pathList.get(i).getEndVertex())) {
						
						//if circle will be build of even edges, the edge is applicable
						if(pathList.get(i).getEdgeList().size() % 2 == 1) {
							if (minCellSource.equals(pathList.get(i).getStartVertex())) {
								tempVertices.addAll(pathList.get(i).getVertexList());
								tempVertices.add(minCellSource);
							}
							else {
								tempVertices.add(minCellSource);
								tempVertices.addAll(pathList.get(i).getVertexList());
							}
							pathList.remove(i);
							return true;
						}
						else
							return false;
					}
					else {
						for(int j = 0; j < pathList.size(); j++) {
							
							//searches for the second path where the TargetVertex of the edge is either the starting or the end point
							if(minCellTarget.equals(pathList.get(j).getStartVertex()) || minCellTarget.equals(pathList.get(j).getEndVertex())) {
								
								String minCellSourceStr = minCellSource.toString();
								String endVertex = pathList.get(i).getEndVertex().toString();
								
								String minCellTargetStr = minCellTarget.toString();
								String startVertex = pathList.get(j).getStartVertex().toString();
								
								if(minCellSource.equals((minCellSource.equals(pathList.get(i).getStartVertex())) && minCellTarget.equals(pathList.get(j).getEndVertex())) ||
										(minCellSource.equals(pathList.get(i).getEndVertex()) && minCellTarget.equals(pathList.get(j).getStartVertex()))) {
									tempVertices.addAll(pathList.get(i).getVertexList());
									tempVertices.addAll(pathList.get(j).getVertexList());
								}
								else if(minCellSource.equals(pathList.get(i).getStartVertex()) && minCellTarget.equals(pathList.get(j).getStartVertex())) {
									Collections.reverse(pathList.get(i).getVertexList());
									tempVertices.addAll(pathList.get(i).getVertexList());
									tempVertices.addAll(pathList.get(j).getVertexList());
								}
								else if(minCellSource.equals(pathList.get(i).getEndVertex()) && minCellTarget.equals(pathList.get(j).getEndVertex())) {
									tempVertices.addAll(pathList.get(i).getVertexList());
									Collections.reverse(pathList.get(j).getVertexList());
									tempVertices.addAll(pathList.get(j).getVertexList());
								}
								else
									return false;
								
								if (i < j) {
									pathList.remove(j);
									pathList.remove(i);
								}
								else {
									pathList.remove(i);
									pathList.remove(j);
								}
								return true;
							}
						}
					}
				}	
			}
		}
		//if just everything fails, than edge is not applicable
		return false;
	}
	
	public static void calculateMaxWeightMatching() {
		pathMaxMatching = new ArrayList<List<ShowWeightWeightedEdge>>();
		
//		for(int x = 0; x < pathList.size(); x++)
//			System.out.println("Before Loop: " + x + ": " + pathList.get(x).getVertexList() + " | " + pathList.get(x).getEdgeList());
		
		for(int i = 0; i < pathList.size(); i++) {
			//if path has only one vertex there's nothing to match
			if(pathList.get(i).getVertexList().size() == 1) {
				continue;
			}
			
			//if path has only two vertices, the one edge will automatically be the MaxWeightMatching
			else if(pathList.get(i).getVertexList().size() == 2) {
				copyOfGraph.addEdge(pathList.get(i).getStartVertex(), pathList.get(i).getEndVertex());
				continue;
			}
			
			//initialization for a path greater or equal two edges
			if(pathList.get(i).getStartVertex().equals(pathList.get(i).getEndVertex())) {
				pathMaxMatching.add(MaxWeightMatchingCircle(i, pathList.get(i).getEdgeList()));
			}
			else {
				pathMaxMatching.add(MaxWeightMatchingPath(i, pathList.get(i).getEdgeList()));
			}
		}
	
		for(int i = 0; i < pathMaxMatching.size(); i++) {
			for(int j = 0; j < pathMaxMatching.get(i).size(); j++) {
				ShowWeightWeightedEdge currentEdge = pathMaxMatching.get(i).get(j);
				ShowWeightWeightedEdge newEdge = copyOfGraph.addEdge(copyOfGraph.getEdgeSource(currentEdge), copyOfGraph.getEdgeTarget(currentEdge));
				copyOfGraph.setEdgeWeight(newEdge, copyOfGraph.getEdgeWeight(currentEdge));
			}
		}
	}



	private static List<ShowWeightWeightedEdge> MaxWeightMatchingPath(int i, List<ShowWeightWeightedEdge> edgeList) {
		
		//saves the absolute weight for any iteration step, step 0 and step 1 are already given for the algorithm
		Map<Integer, Double> weight = new HashMap<Integer, Double>();
		weight.put(0, 0.0);
		weight.put(1, EDGEWEIGHT - gpaGraph.getEdgeWeight(edgeList.get(0)));
		
		//saves the edges which take part in the matching for every iteration step, step 0 and step 1 are already given for the algorithm
		Map<Integer, List<ShowWeightWeightedEdge>> matching = new HashMap<Integer, List<ShowWeightWeightedEdge>>();
		List<ShowWeightWeightedEdge> edges = new ArrayList<ShowWeightWeightedEdge>();
		matching.put(0, null);
		edges.add(edgeList.get(0));
		matching.put(1, edges);
		
		double tempWCurrent;
		double tempWPredecessor;
		
		for(int j = 2; j <= edgeList.size(); j++) {
			
			tempWCurrent = (EDGEWEIGHT - gpaGraph.getEdgeWeight(edgeList.get(j-1))) + weight.get(j-2);
			tempWPredecessor = weight.get(j-1);
			edges = new ArrayList<ShowWeightWeightedEdge>();
			
			if(tempWCurrent > tempWPredecessor) {
				weight.put(j, tempWCurrent);
				if(matching.get(j-2) != null)
					edges.addAll(matching.get(j-2));
				edges.add(edgeList.get(j-1));
				matching.put(j, edges);
			}
			else {
				weight.put(j, weight.get(j-1));
				matching.put(j, matching.get(j-1));
			}
			
		}
		tempWeight = weight.get(weight.size()-1);
		return matching.get(matching.size()-1);
	}


	private static List<ShowWeightWeightedEdge> MaxWeightMatchingCircle(int i, List<ShowWeightWeightedEdge> edgeList) {
		List<ShowWeightWeightedEdge> path1 = new ArrayList<ShowWeightWeightedEdge>();
		path1.addAll(edgeList);
		path1.remove(0);	
		path1 = MaxWeightMatchingPath(i, path1);
		double weightPath1 = tempWeight;
		
		List<ShowWeightWeightedEdge> path2 = edgeList;
		path2.remove(path2.size()-1);
		path2 = MaxWeightMatchingPath(i, path2);
		double weightPath2 = tempWeight;
		
		if(weightPath1 > weightPath2)
			return path1;
		else {
			return path2;
		}
	}
}