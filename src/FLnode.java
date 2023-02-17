
import java.util.Map;



public class FLnode  {
	
	int pID;
	
	int lID;

	//chooseMethod C; // entscheidet ob Start oder Normal - Verhalten
	
    public Map<Integer, ExNode>	ExList; // dort sind die Gewichte gespeichert
    //FlexList F; // Verweis auf die angeh�rende Flexlist
	
	// FLnode start; m�glicherwise noch Verweis auf einen Startknoten
	
	FLnode next; 
	FLnode last;
	
	//double weight; bei ExList

public FLnode(){} // f�r den Dummy


//Hauptkonstruktor

public FLnode(int LID, int PID, Map<Integer, ExNode> Elist){
	
	this.ExList = Elist;
	
	this.lID = LID;
	this.pID = PID;
	

} 


	


/*	public void setWeight(double w){
		weight = w;
	}
*/
	
public int getMax() {
		
		int minNodeID = 0;
		double maxi = -999999;
		int keyNodeID = 0;
		ExNode node;
		
		for(Map.Entry<Integer, ExNode> e: ExList.entrySet() ){
			
			keyNodeID = e.getKey();
			node = e.getValue();
			
			if(node.getEntry(lID) > maxi){
				maxi = node.getEntry(lID);
			    minNodeID = keyNodeID;          }
			
		}
		
		return minNodeID;
		
		
	}

	

}