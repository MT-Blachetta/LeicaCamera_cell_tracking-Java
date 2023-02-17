

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.*;
import ij.process.AutoThresholder;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Color;
import java.io.IOException;
import java.util.*;

import org.jgrapht.graph.SimpleWeightedGraph;



/**
 * This class implements the cell tracking algorithms.
 * 
 * @author Thomas Temme
 */
public class CellTracking implements Measurements {

	
	
	//private static final Logger logger = LogManager.getLogger(CellTracking.class);
	
	private static Integer counter = 0;
	private GraphVisualizationFrame graphFrame = null;

	public static Integer getCounter() {
		return counter;
	}

	private void convertToGray(ImagePlus img) {
		ImageConverter converter = new ImageConverter(img);				
		converter.convertToGray8();
	}

	private void threshold(ImagePlus big) {
		ImageProcessor procBig = big.getProcessor();
		procBig.setAutoThreshold(AutoThresholder.Method.Li, true);		
		big.setProcessor(procBig);
	}
	
	private void filterPhaseContrast(ImagePlus big) {
		ImageProcessor procBig = big.getProcessor();
		//procBig.setThreshold(0, threshold, ImageProcessor.NONE);
		//big.setProcessor(procBig);
		//IJ.makeRectangle(1344, 2757, 1392, 1040);
		procBig.findEdges();
		//IJ.run("Fill Holes","slice");				
		//IJ.makeRectangle(1344, 2757, 1392, 1040);
		big.setProcessor(procBig);
	}

	public ImagePlus createOffsetImage(ImagePlus imp, int dx, int dy) throws InterruptedException
	{	
		//If debug mode stage movement is active:
		//Change the small picture in a way, that it gets moved by dx, dy.
		//Then the tracked cell should be  to the center of the small image and transfered correctly automatically in the offset image	
		
		//If debug mode stage movement is false:
		//Add current picture in the mid of the big image moved by gDX and gDY
		
		ParticleContainer.getInstance().setGlobalPositionX(ParticleContainer.getInstance().getGlobalPositionX() + dx);
		ParticleContainer.getInstance().setGlobalPositionY(ParticleContainer.getInstance().getGlobalPositionY() + dy);
		int globalDX = ParticleContainer.getInstance().getGlobalPositionX(); 
		int globalDY = ParticleContainer.getInstance().getGlobalPositionY();
		
		
		
		int newWidth = imp.getWidth();
		int newHeight = imp.getHeight();
		
		ImagePlus bigPicture = null;
		
		if(GUIPluginMain.__DEBUG_MODE_STAGE_MOVEMENT__ == false)
		{
			//Make imp bigger and pad zeroes around with size of dx and dy
			IJ.makeRectangle(0, 0, imp.getWidth(), imp.getHeight());
			System.out.println("Make rectangle Width:" +imp.getWidth());
			System.out.println("Make rectangle height:" +imp.getHeight());
			//IJ.run("Copy");
			imp.copy();
		}
		
		if(GUIPluginMain.__DEBUG_MODE_STAGE_MOVEMENT__ == false && GUIPluginMain.__DEBUG_MODE_ALGORITHM__ == true)
		{
			bigPicture = new ImagePlus("bigPicture", imp.getImage());
			ImageWindow bigWindow = new ImageWindow(bigPicture);
			return bigPicture;
		}
		else if(GUIPluginMain.__DEBUG_MODE_STAGE_MOVEMENT__ == true)
		{
					
			if(globalDX > 0 && globalDY > 0)
			{				
				//Copy picture beginning at position dx dy
				IJ.makeRectangle(globalDX,globalDY, imp.getProcessor().getWidth() - globalDX, imp.getProcessor().getHeight() - globalDY);
				imp.copy();
				newWidth = imp.getProcessor().getWidth()-globalDX;
				newHeight = imp.getProcessor().getHeight()-globalDY;
				
				bigPicture = NewImage.createByteImage("bigPicture",ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEY,ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEX, 1, NewImage.FILL_BLACK);
				ImageWindow bigWindow = new ImageWindow(bigPicture);
				int newX = (bigPicture.getProcessor().getWidth() / 2) - (imp.getProcessor().getWidth()/2) + globalDX;
				int newY = (bigPicture.getProcessor().getHeight() / 2) - (imp.getProcessor().getHeight()/2) + globalDY;		
				IJ.makeRectangle(newX,newY, newWidth, newHeight);
				bigPicture.paste();
			}			
			else if(globalDX <= 0 && globalDY <= 0)
			{
				//Copy picture beginning at startpoint until width - dx/dy
				IJ.makeRectangle(0,0, imp.getProcessor().getWidth() + globalDX, imp.getProcessor().getHeight() + globalDY);
				imp.copy();
				newWidth = imp.getProcessor().getWidth()+globalDX;
				newHeight = imp.getProcessor().getHeight()+globalDY;
				
				bigPicture = NewImage.createByteImage("bigPicture",ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEY,ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEX, 1, NewImage.FILL_BLACK);
				ImageWindow bigWindow = new ImageWindow(bigPicture);
				
				int newX = (bigPicture.getProcessor().getWidth() / 2) - (imp.getProcessor().getWidth() / 2);// + ParticleContainer.getInstance().getGlobalPositionX();
				int newY = (bigPicture.getProcessor().getHeight() / 2) - (imp.getProcessor().getHeight() / 2);// + ParticleContainer.getInstance().getGlobalPositionY();		
				IJ.makeRectangle(newX,newY, newWidth, newHeight);
				bigPicture.paste();
			}
			else if(globalDX <= 0 && globalDY > 0)
			{
				//Copy picture beginning at position dx dy
				IJ.makeRectangle(0,globalDY, imp.getProcessor().getWidth() + globalDX, imp.getProcessor().getHeight() - globalDY);
				imp.copy();
				newWidth = imp.getProcessor().getWidth()+globalDX;
				newHeight = imp.getProcessor().getHeight()-globalDY;
				
				bigPicture = NewImage.createByteImage("bigPicture",ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEY,ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEX, 1, NewImage.FILL_BLACK);
				ImageWindow bigWindow = new ImageWindow(bigPicture);
				int newX = (bigPicture.getProcessor().getWidth() / 2) - (imp.getProcessor().getWidth() / 2);
				int newY = (bigPicture.getProcessor().getHeight() / 2) - (imp.getProcessor().getHeight()/2) + globalDY;		
				IJ.makeRectangle(newX,newY, newWidth, newHeight);
				bigPicture.paste();
			}			
			else//(globalDX > 0 && globalDY <= 0)
			{
				//Copy picture beginning at startpoint until width - dx/dy
				IJ.makeRectangle(globalDX,0, imp.getProcessor().getWidth() - globalDX, imp.getProcessor().getHeight() + globalDY);
				imp.copy();
				newWidth = imp.getProcessor().getWidth()-globalDX;
				newHeight = imp.getProcessor().getHeight()+globalDY;
				bigPicture = NewImage.createByteImage("bigPicture",ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEY,ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEX, 1, NewImage.FILL_BLACK);
				ImageWindow bigWindow = new ImageWindow(bigPicture);
				int newX = (bigPicture.getProcessor().getWidth() / 2) - (imp.getProcessor().getWidth()/2) + globalDX;
				int newY = (bigPicture.getProcessor().getHeight() / 2) - (imp.getProcessor().getHeight() / 2);		
				IJ.makeRectangle(newX,newY, newWidth, newHeight);
				bigPicture.paste();
			}
			//newSmallPicture.paste();
			//Pasting seems to run asynchronus. So you have to wait here that it is successful.
			Thread.sleep(100);
		}
		else
		{
			
			//Production code
			bigPicture = NewImage.createShortImage("bigPicture",ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEY,ParticleContainer.getInstance().MAX_WELL_PIXEL_SIZEX, 1, NewImage.FILL_BLACK);
			ImageWindow bigWindow = new ImageWindow(bigPicture);
			//ToDo: Do this only if new images is not within current canvas
	
			System.out.println("DEBUG: Global DX:" +ParticleContainer.getInstance().getGlobalPositionX());
			System.out.println("DEBUG: Global DY:" +ParticleContainer.getInstance().getGlobalPositionY());
			
			int newX = (bigPicture.getProcessor().getWidth() / 2) - (imp.getProcessor().getWidth() / 2) + ParticleContainer.getInstance().getGlobalPositionX();
			int newY = (bigPicture.getProcessor().getHeight() / 2) - (imp.getProcessor().getHeight() / 2) + ParticleContainer.getInstance().getGlobalPositionY();		
			IJ.makeRectangle(newX,newY, imp.getProcessor().getWidth(), imp.getProcessor().getHeight());
			System.out.println("Width:" + imp.getProcessor().getWidth());
			System.out.println("Height" + imp.getProcessor().getHeight());
			
			System.out.println("X Copy:" +newX);
			System.out.println("Y Copy" +newY);
			//IJ.run("Paste");
			bigPicture.paste();
			Thread.sleep(100);
		}
		
		return bigPicture;
	}
	
	public boolean track(ImagePlus imp)
	{		
		try{	
			boolean isFirstTrack = false;
//			if(GUIPluginMain.__DEBUG_MODE_GRAPH__ && graphFrame == null)
//			{
//				//Create graph window
//				graphFrame = new GraphVisualizationFrame();			
//			}
			if(imp == null)
				System.out.println("Image Plus is null");
			IJ.run("Set Scale...", "distance=0 known=0 pixel=1 unit=pixel");
			System.out.println("Start creating offset imaging.");
			//if(!GUIPluginMain.__DEBUG_MODE_ALGORITHM__) {			
			imp = createOffsetImage(imp, ParticleContainer.getInstance().getCurrentDX(), ParticleContainer.getInstance().getCurrentDY());
			//}
			
			//Needs to be done before applying "Convert to Mask" as this method changes the image permanently
			
			//IJ.run("Restore Selection");
			// Whyever, ImageJ needs some time to fulfill the gray conversion.
			//Maybe it runs in another asynchronus thread so you have to wait here a little time for ImageJ to finish.
			System.out.println("Converting to gray image.");
			convertToGray(imp);
			Thread.sleep(500);
//			ImageProcessor iProc = imp.getProcessor();
//			System.out.println("Do Sobel operation");
//			iProc.findEdges();
//			imp.setProcessor(ip);
			//New: Duplicate image for getting mean intensity values out there
			ImagePlus duplicatedGrayImage = imp.duplicate();
			
			System.out.println("Thresholding image");
			if(ParticleContainer.getInstance().PHASE_CONTRAST_IMAGES == true)
			{
				IJ.run("Find Edges", "slice");
				IJ.run("Make Binary", "method=Li background=Default calculate");	
				IJ.run("Fill Holes", "slice");
				IJ.run("Invert", "slice");
			}
			threshold(imp);
			imp.deleteRoi();
		//	IJ.run("Fill Holes","slice");
			
			
			ImagePlus duplicatedImage = imp.duplicate();	
			
//			IJ.run("Convert to Mask");
//			IJ.run("Watershed");
			
			
			// init result table
			ResultsTable rt = ResultsTable.getResultsTable();
			if (rt == null)
				rt = new ResultsTable();
			rt.showRowNumbers(true);
			rt.reset(); 
			
//			ResultsTable rtMeanIntensity = ResultsTable.getResultsTable();
//			if (rtMeanIntensity == null)
//				rtMeanIntensity = new ResultsTable();
//			rtMeanIntensity.showRowNumbers(true);
//			rtMeanIntensity.reset(); 
			System.out.println("Starting Brightness Analysis");
			Analyzer brightnessAnalyzer = new Analyzer(imp,Analyzer.MEAN + Analyzer.MIN_MAX + Analyzer.AREA + Analyzer.CENTROID,rt);
			brightnessAnalyzer.setRedirectImage(imp);
			//brightnessAnalyzer.measure();
			System.out.println("Starting Particle Analysis");
			// analyze image
			ParticleAnalyzer particleAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.AREA + ParticleAnalyzer.CENTROID 
					+ ParticleAnalyzer.SHOW_NONE, Measurements.AREA + Measurements.CENTROID + Measurements.MEAN + ParticleAnalyzer.MEAN, rt, 350, 10000, 0, 1);
			particleAnalyzer.setHideOutputImage(true);
			particleAnalyzer.analyze(imp);
			System.out.println("Particle Analyser finished");
			float[] area = rt.getColumn(0);
			float[] meanIntensity = rt.getColumn(2);
			float[] x = rt.getColumn(6);
			float[] y = rt.getColumn(7);
			System.out.println("Read cell positions");	
			List<Particle> currentParticleList = new ArrayList<Particle>();
			if(area == null)
				System.out.println("no cell found. Area is null.");
			System.out.println("Number of particles: "+area.length);
			for (int i = 0; i < area.length; i++) {
				Particle currentParticle = new Particle(i, area[i],meanIntensity[i] , x[i], y[i], counter);
				currentParticle.setIdWithinCurrentFrame(i);			
				currentParticleList.add(currentParticle);			
				//System.out.println("ID: " + i + "\tArea: " + area[i] + "\tX: " + x[i] + "\tY: " + y[i]);
			}
			if(currentParticleList.size() == 0)
				System.out.println("ERROR: No particle found.");
			System.out.println("Set current particle.");
			ParticleContainer.getInstance().getParticles().put(counter, currentParticleList);
			ParticleContainer.setCurrentFrame(counter);
			rt.show("Results");
	
			Color textColor = Color.RED;
			System.out.println("Convert to color processor.");
			ColorProcessor proc = duplicatedImage.getProcessor().convertToColorProcessor();
			duplicatedImage.setProcessor(proc);
			ImageStatistics stats = proc.getStatistics();
			proc.setColor(textColor);
			double umrechnung = 1;
			//double umrechnungY =
			//double umrechnungX =
			System.out.println("Writing cell numbers into image");	
			if (counter <= 0)
			{ // first image --> draw cell ids			
				for (int i = 0; i < currentParticleList.size(); i++) 
				{				
					proc.setColor(textColor);
					proc.drawString(Integer.toString(currentParticleList.get(i).getGlobalId()), (int) ((int) currentParticleList.get(i)
							.getX() * umrechnung), (int) ((int) currentParticleList.get(i).getY() * umrechnung));	
//					if(graphFrame != null)
//					{
//						graphFrame.addNode(counter+1, currentParticleList.get(i).getId());
//					}
				}
				//Draw current ID
				
				isFirstTrack = true;
			}
			else
			{
				System.out.println("Starting selected tracking algorithm");
				ParticleContainer.TRACKINGALGO.track();
//				if(counter % 50 == 0)
//				{
//					Particle.overallMaximumDistance=0;
//					Particle.overallMaximumSpeed=0;
//				}
				
				
				//This function moves the stage
				this.calculateCurrentDelta();
				
				//Try to draw results: Start with last detected cells and go to predecessors!
				//Draw a path id to every frame of path
				for(int i = 0; i < currentParticleList.size(); i++)
				{
					if(GUIPluginMain.__DEBUG_MODE_GRAPH__)
						proc.drawString(Integer.toString(currentParticleList.get(i).getGlobalId()),
								(int)((int)currentParticleList.get(i).getX() * umrechnung),
								(int)((int)currentParticleList.get(i).getY() * umrechnung));
					else
						proc.drawString(Integer.toString(i), (int) ((int) currentParticleList.get(i).getX() * umrechnung),
								(int)((int) currentParticleList.get(i).getY() * umrechnung));
					Particle predecessor = currentParticleList.get(i).getOldEdgePredecessor();
					int predecessorCounter = 1;
					while(predecessor!=null && predecessorCounter < ParticleContainer.getInstance().MAX_VISIBLE_TRACK_NUMBER)
					{
						predecessorCounter++;
						//Draw track	
						if(GUIPluginMain.__DEBUG_MODE_GRAPH__)
							proc.drawString(Integer.toString(currentParticleList.get(i).getGlobalId()),
									(int)((int)predecessor.getX() * umrechnung), (int)((int)predecessor.getY() * umrechnung));
						else
							proc.drawString(Integer.toString(i), (int)((int)predecessor.getX() * umrechnung),
									(int)((int)predecessor.getY() * umrechnung));
						predecessor = predecessor.getOldEdgePredecessor();
					}
				}
			}
			System.out.println("Close image window");
			imp.changes = false;
			imp.getWindow().close();
			imp = new ImagePlus("title",proc);
			//imp.show();
			
			duplicatedImage.updateAndDraw();
	
			// save as file
			counter++;
			IJ.resetThreshold(imp);	
			//logger.error(current);
			System.out.println("Save image");
			System.out.println("Save image: "+("./res/tracked-images/" + String.format("%03d", counter) + ".png"));
			FileSaver fs = new FileSaver(imp);
			fs.saveAsPng("./res/tracked-images/" + String.format("%03d", counter) + ".png");
			duplicatedImage.flush();
			duplicatedImage.close();
			imp.close();
			imp=null;
			duplicatedImage = null;
			duplicatedGrayImage.changes = false;
			duplicatedGrayImage.flush();			
			duplicatedGrayImage.close();
			duplicatedGrayImage = null;
			//ParticleContainer.saveCurrentFrame();
			ParticleContainer.deleteOldestFrame();			
			return isFirstTrack;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString() + e.getMessage());
			return false;
		}
	}
	
	public void calculateCurrentDelta()
	{
		ParticleContainer container = ParticleContainer.getInstance();
		if(ParticleContainer.getInstance().getCurrentFrame() <= 1)
			calculateCurrentDeltaFirstFrame();
		else
		{
			//Starting from trackingParticle. Look for position of last successor
			Particle currentTrackingParticle = ParticleContainer.getInstance().getTrackingParticle();			
			
			while(currentTrackingParticle.getOldEdgeSuccessor() != null)
			{
				currentTrackingParticle = currentTrackingParticle.getOldEdgeSuccessor();
			}
			//int dx = (int) (((container.MAX_WELL_PIXEL_SIZEX / 2) - currentTrackingParticle.getX()) - ParticleContainer.getInstance().getCurrentDX());
			int dx = (int) currentTrackingParticle.getX() -  (((container.MAX_WELL_PIXEL_SIZEX / 2) + ParticleContainer.getInstance().getGlobalPositionX()));
			int dy = (int) currentTrackingParticle.getY() -  (((container.MAX_WELL_PIXEL_SIZEY / 2) + ParticleContainer.getInstance().getGlobalPositionY()));
			ParticleContainer.getInstance().setCurrentDX(dx);
			ParticleContainer.getInstance().setCurrentDY(dy);
			ParticleContainer.getInstance().saveCurrentParticlePosition(currentTrackingParticle);
			if(dx != 0 || dy != 0)
				System.out.println("Corresponding object found!\r\n Moving stage "+dx+" in x direction and "+dy+" in y direction.");
			else
				System.out.println("Corresponding object not found!\r\n. Hopefully it reoccurs in next frame. Moving stage "+dx+" in x direction and "+dy+" in y direction.");
		}
	}

	public void calculateCurrentDeltaFirstFrameOld()
	{		

		
		//Calculate dx and dy values for microscope stage movement!
		//Therefore: Look for center position of last detection
		//Check in current frame if there is a Predecessor-Object at this position
		//If yes, set dx, dy to the position of the Successor
		ParticleContainer container = ParticleContainer.getInstance();
		int currentFrame = ParticleContainer.getCurrentFrame();
		List<Particle> currentCells = container.getParticles().get(currentFrame);
		
		//Get mid of SMALL PICTURE within BIG PICTURE
	 
		int midY = (container.MAX_WELL_PIXEL_SIZEY/2) + container.getGlobalPositionY();
		int midX = (container.MAX_WELL_PIXEL_SIZEX/2) + container.getGlobalPositionX();
		double mindist = Double.MAX_VALUE;
		int maxTimeback = Integer.MIN_VALUE;
		outerLoop:
		for(int cell = 0; cell < currentCells.size(); cell++)
		{
			
			Particle currentParticle = currentCells.get(cell);
			
			//Check if there is a predecessor, very  to midpoint
			//ToDo: Check why tree model doesn't work here
			
			//check also for pre-pre-predecessor and so on.
			int timeBack = 0;
			while(currentParticle.getOldEdgePredecessor() != null)
			{
				timeBack--;				
				currentParticle = currentParticle.getOldEdgePredecessor();
				int xPart = (int)currentParticle.getX();
				int yPart = (int)currentParticle.getY();
				
				//Check if coordinates are very  to midpoint
				double dist = euclidDist(midX, midY, xPart, yPart);
				if(dist<mindist)
				{
					mindist=dist;
				}
				container.setCellLostForFrames(container.getCellLostForFrames()+1);
				if(dist < 10 && timeBack > maxTimeback)
				{
					//If yes, look for successor of this particle and set DX, DY
					//Check: Old Edge Successor correct?
					maxTimeback = timeBack;
					Particle successor = currentParticle.getOldEdgeSuccessor();					
					int xDiff = (int)successor.getX() - xPart;
					int yDiff = (int)successor.getY() - yPart;
					xDiff = ParticleContainer.getInstance().getGlobalPositionX();
					yDiff = ParticleContainer.getInstance().getGlobalPositionY();
					ParticleContainer.getInstance().setCurrentDX(0);
					ParticleContainer.getInstance().setCurrentDY(0);
					ParticleContainer.getInstance().saveCurrentParticlePosition(successor);
					ParticleContainer.getInstance().setTrackingParticle(currentParticle);
					System.out.println("Corresponding object found in frame " +timeBack+"!\r\n Moving stage "+xDiff+" in x direction and "+yDiff+" in y direction.");					
					if(timeBack==-1)
						break outerLoop;
					else
						break;
				}				
			}
		}
		if(mindist >= 10)
		{
			
		}
		//double test = mindist;
	}
	
	
	public void calculateCurrentDeltaFirstFrame()
	{		
		//Calculate dx and dy values for microscope stage movement!
		//Therefore: Look for center position of last detection
		//Check in current frame if there is a Predecessor-Object at this position
		//If yes, set dx, dy to the position of the Successor
		ParticleContainer container = ParticleContainer.getInstance();
		int currentFrame = ParticleContainer.getCurrentFrame();
		List<Particle> currentCells = container.getParticles().get(currentFrame);
		
		//Get mid of SMALL PICTURE within BIG PICTURE
		Particle minSuccessorParticle = null;
		Particle minParticle = null;
		int midY = (container.MAX_WELL_PIXEL_SIZEY/2) + container.getGlobalPositionY();
		int midX = (container.MAX_WELL_PIXEL_SIZEX/2) + container.getGlobalPositionX();
		double mindist = Double.MAX_VALUE;
		int maxTimeback = Integer.MIN_VALUE;
		outerLoop:
		
		for(int cell = 0; cell < currentCells.size(); cell++)
		{			
			Particle currentParticle = currentCells.get(cell);
			
			//Check if there is a predecessor, very close to midpoint			
			//check also for pre-pre-predecessor and so on.
			int timeBack = 0;
			if(currentParticle.getOldEdgePredecessor() != null)
			{
				timeBack--;				
				currentParticle = currentParticle.getOldEdgePredecessor();
				int xPart = (int)currentParticle.getX();
				int yPart = (int)currentParticle.getY();
				
				//Check if coordinates are very  to midpoint
				double dist = euclidDist(midX, midY, xPart, yPart);

				//container.setCellLostForFrames(container.getCellLostForFrames()+1);
				if(dist < mindist)
				{
					mindist=dist;
					//If yes, look for successor of this particle and set DX, DY
					//Check: Old Edge Successor correct?
					maxTimeback = timeBack;
					minSuccessorParticle = currentParticle.getOldEdgeSuccessor();					
//					int xDiff = (int)successor.getX() - xPart;
//					int yDiff = (int)successor.getY() - yPart;
//					xDiff = ParticleContainer.getInstance().getGlobalPositionX();
//					yDiff = ParticleContainer.getInstance().getGlobalPositionY();
					
					minParticle = currentParticle;
					
//					if(timeBack==-1)
//						break outerLoop;
//					else
//						break;
				}				
			}
		}
		
		//DEBUG!!!
//		for(int cell = 0; cell < currentCells.size(); cell++)
//		{	
//			Particle currentParticle = currentCells.get(cell);
//			if(currentParticle.getId() == 12)
//			{
//				minSuccessorParticle = currentParticle;
//				minParticle = currentParticle.getOldEdgePredecessor();
//			}
//			
//		}
		
		if(minSuccessorParticle != null)
		{
			ParticleContainer.getInstance().setCurrentDX(0);
			ParticleContainer.getInstance().setCurrentDY(0);
			ParticleContainer.getInstance().saveCurrentParticlePosition(minSuccessorParticle);
			ParticleContainer.getInstance().setTrackingParticle(minParticle);
			System.out.println("Tracking first object. Moving stage "+ParticleContainer.getInstance().getGlobalPositionX()+" in x direction and "+ParticleContainer.getInstance().getGlobalPositionY()+" in y direction.");
		}
		//double test = mindist;
	}
	
	public double euclidDist(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
	}
		
		
//	public double[][] floydWithPathReconstruction()
//	{
//		ParticleContainer container = ParticleContainer.getInstance();
//		//One entry in w represents an edge
//		double[][] w = container.getW();
//		//Double dist[][] = w.clone();
//		int next[][] = new int[w.length][w[0].length];
//		double dist[][] = new double[w.length][w[0].length];
//		//Iterate over edges
//			//For every edge: Initialize dist-Matrix and save follower in next array
//		for(int u=0;u<w.length;u++)
//		{
//			for(int v=0;v<w[u].length;v++)
//			{
//				dist[u][v]=w[u][v];
//				next[u][v]=v;
//			}
//		}		
//		for(int k=0;k<w.length;k++)
//		{
//			for(int i=0;i<w.length;i++)
//			{
//				for(int j=0;j<w.length;j++)
//				{
//					if(dist[i][k] + dist[k][j] < dist[i][j])
//					{
//						dist[i][j] = dist[i][k] + dist[k][j];
//						next[i][j] = next[i][k];
//					}
//				}
//			}			
//		}		
//		container.setDist(dist);
//		container.setNext(next);
//		return dist;
//	}
	
	public List<Integer> extractPath(int u, int v)
	{
		List<Particle> result = new ArrayList<Particle>();
		List<Integer> path = new ArrayList<Integer>();
		ParticleContainer container = ParticleContainer.getInstance();
		int[][] next = container.getNext();		
		path.add(u);
		if(next[u][v] == 0)
			return path;		
		while(u != v)
		{
			u = next[u][v];
			path.add(u);
		}		
		return path;
	}
}