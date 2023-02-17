

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap; 
import java.util.Map;

// I

public class BipartiteTrackingAlgorithm implements ITrackingAlgorithm {

//<DATA Links:> /I.0.0
public static ParticleContainer real; // eventuell in Konstruktor
private static BipartiteTrackingAlgorithm ref;
//<DATA>


//ACCESS:[Singleton] /I.0.1
private BipartiteTrackingAlgorithm(){
    	real = ParticleContainer.getInstance();
                   }
    
public static BipartiteTrackingAlgorithm getInstance(){
    	
    	if(ref == null)
    		ref = new BipartiteTrackingAlgorithm();
    		
    	return ref;
    		
    	
                                      }

public void track(){
	
    Map<Integer, List<Particle>> nodes = real.getParticles();
    //!COND_pre {"all Particles corresponding to nodes within complete frame window in ->'Particles' " , "Frames as 'List<Particle> starting with key 0 in consecutive order"}! 
    
   short frames = (short) nodes.size();
    
   List<Particle> old_particles = new ArrayList<Particle>();
    
    
    //#1
    System.out.println("\nframes = " + frames);
    //*
    
    if(frames<=1)
    	return;
    
	//ab hier wird der BipartiteTrackingAlgorithmus ausgeführt
	//jetzt von jedem Particle den before gain draufaddieren   
     ExNode aktuellerExitKnoten;
     FLnode aktuellerFLKnoten;

	Particle old;
	Particle extension;
	Particle pre;

    PrintState();

    int i = 0;
        
        ////////////////////////////////////////////////////////////////////
    
		for (int x = 0; x < frames-1; x++)    {

		//LOAD DATA
		Map<Integer, ExNode> Elist = new HashMap<Integer, ExNode>();//OK
	    Map<Integer, FLnode> Flist = new HashMap<Integer, FLnode>();//OK
		
		old_particles.addAll(nodes.get(x));
		List<Particle> ext = nodes.get(x+1);//OK--------------------------------------------------------------------------------
		
		//INIT old-side Graph-Partition
		for(int k = 0; k < old_particles.size(); k++){
		
		aktuellerFLKnoten = new FLnode(k,old_particles.get(k).getGlobalId(),Elist);//OK
		Flist.put(k,  aktuellerFLKnoten);            
													 }
	 


		for(int iterator = 0; iterator < ext.size(); iterator++)		{
		
		Particle E = ext.get(iterator);
		double w[] = new double[old_particles.size()]; // neues Array für neuen extension Particle
		i = 0;

			for(Particle I: old_particles){
		
				w[i] = rated(I.CompareTo(E));
				
				pre = I.getOldEdgePredecessor();
		
				while(pre != null){
					w[i] += rated(pre.getDistance());
					pre = pre.getOldEdgePredecessor();}
		
				i++;
		
								          }

		aktuellerExitKnoten  = new ExNode(w, E.getGlobalId());          //OK
		
		Elist.put(iterator, aktuellerExitKnoten);



													  }


		
		
	while( !Flist.isEmpty() && !Elist.isEmpty() ){
			
			FLnode OldMinNode = new FLnode();
			int ExMinNodeNr = -1;
			double min = -999999;
			int EXkey;
			double compare;
			
			
			for (Map.Entry<Integer, FLnode> entry : Flist.entrySet()) {
				
				int FLkey = entry.getKey();
				FLnode F = entry.getValue();
				
				EXkey = F.getMax();
				compare = Elist.get(EXkey).getEntry(FLkey);
				
				if(compare > min){
					OldMinNode = F;
					ExMinNodeNr = EXkey;
					min = compare;}
				
																	 }
			
			ExNode out = Elist.get(ExMinNodeNr); 
			
		
			old = BipartiteTrackingAlgorithm.real.getParticleObject(OldMinNode.pID);
			extension = BipartiteTrackingAlgorithm.real.getParticleObject(out.pID);
			
			if(old.getOldEdgeSuccessor() != null)
				old.getOldEdgeSuccessor().setOldEdgePredecessor(null); 
					
												  
			
			old.setOldEdgeSuccessor(extension);
			extension.setOldEdgePredecessor(old);
			
			
			old.setDistance(old.CompareTo(extension));

				Flist.remove(OldMinNode.lID);                                     
				Elist.remove(ExMinNodeNr);                    }

	}


PrintState();
    
	}
	
	public void PrintState(){
		
		Map<Integer, List<Particle>> nodes = real.getParticles();
		int framenumber = nodes.size();
		
		
		for(int i = 0; i < framenumber-1; i++){
			
			System.out.println();
			List<Particle> Frame = nodes.get(i);
			System.out.println("Correspondences starting with the first Particle of Frame " + i);
			System.out.println();
			
			for(Particle k: Frame){
				System.out.print(k.getGlobalId() + ": ");
				if (k.getOldEdgeSuccessor() == null)
					System.out.print("X   ");
				else
					System.out.print(k.getOldEdgeSuccessor().getGlobalId() + "   ");
			}
				
			
			
			
			
		                                      }
		
	                               }
	
	ArrayList<Particle> copy(List<Particle> origin){
		
		//Particle p_new; für Ganz Kopie: hier aber FALSCH
		ArrayList<Particle> kopie = new ArrayList<Particle>();
		
		for(Particle p: origin){
			//p_new = new Particle(p.getId(),p.getArea(),p.getX(),p.getY(),p.getTimeVector());
			kopie.add(p);
		}
		
		return kopie;
		
	}

           double rated(double raw){
             return 10000-raw;                      
                                   }
	

	}
	
	
	

