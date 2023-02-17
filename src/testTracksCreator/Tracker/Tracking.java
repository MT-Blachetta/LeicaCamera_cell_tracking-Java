package testTracksCreator.Tracker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.AutoThresholder;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import testTracksCreator.ImageManipulation.*;
import testTracksCreator.Noise.*;

public class Tracking {

	private static Image img;
	private static int[][] imgArray;
	private static int[][] imgMod;
	
	private static int cellNumber;
	
	private static float[] x;
	private static float[] y;
	
	private static IImageManipulation mode;
	
	//size of image panel for particle tracking
	private static int IMAGE_RESIZE_HEIGHT = GUITrackerMain.getPanelHeight();
	private static int IMAGE_RESIZE_WIDTH = GUITrackerMain.getPanelWidth();
	private static String SOURCE_TRACKING_IMAGE = GUITrackerMain.getSourceImage();
	
	public static void setMode(String modus) {
		if(modus.equals("centering"))
			mode = Centering.getInstance();
		else if(modus.equals("cutting"))
			mode = Cutting.getInstance();
		else
			mode = Cutting.getInstance();
	}
	
	public static IImageManipulation getMode() {
		return mode;
	}
	
	public static int getCellNumber() {
		return cellNumber;
	}
	
	public static void setCellNumber(int value) {
		cellNumber = value;
	}
	
	
	
	public static void analyze(int imageNumber) {
		File imageFile = GUITrackerMain.getImage(imageNumber);
		ImagePlus imagePlus = new ImagePlus(imageFile.getAbsolutePath());
		
		ImageConverter converter = new ImageConverter(imagePlus);		
		converter.convertToGray8();
		
		ImageProcessor processor = imagePlus.getProcessor();
		processor.setAutoThreshold(AutoThresholder.Method.Default, true);
		imagePlus.setProcessor(processor);
		
		ResultsTable resultsTable = new ResultsTable();
		ParticleAnalyzer particleAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.AREA + ParticleAnalyzer.CENTROID 
				+ ParticleAnalyzer.SHOW_NONE, Measurements.AREA + Measurements.CENTROID, resultsTable, 100, 10000, 0, 1);
		particleAnalyzer.analyze(imagePlus);

		x = resultsTable.getColumn(6);
		y = resultsTable.getColumn(7);
		
		double factor_x = 1;
		double factor_y = 1;
		
		try {
 		    BufferedImage checkSizeImage = ImageIO.read(imageFile);
 		    if(checkSizeImage.getWidth() > IMAGE_RESIZE_WIDTH || checkSizeImage.getHeight() > IMAGE_RESIZE_HEIGHT) {
 		    	resize(imageFile, checkSizeImage.getWidth(), checkSizeImage.getHeight());
 		    	factor_x = (double)IMAGE_RESIZE_WIDTH/checkSizeImage.getWidth();
 		    	factor_y = (double)IMAGE_RESIZE_HEIGHT/checkSizeImage.getHeight();
 		    }
 		    	
 		    else {
 		    	File savedImage = new File(SOURCE_TRACKING_IMAGE);
	 			try {
	 				ImageIO.write(checkSizeImage, "png", savedImage);
	 			}
	 			catch (IOException e) {
	 				e.printStackTrace();
	 			}
 		    }
 		 }
 		 catch (IOException ex) {
 			 System.out.println("Unable to read image file");
 	     }
		
		ImagePlus imageString = new ImagePlus(SOURCE_TRACKING_IMAGE);
		
		ColorProcessor proc = imageString.getProcessor().convertToColorProcessor();
		imageString.setProcessor(proc);
		proc.setColor(Color.RED);
		
		int cell;
		for (cell = 0; cell < x.length; cell++) {
			proc.drawString(Integer.toString(cell), (int)(x[cell] * factor_x), (int)(y[cell] * factor_y));
		}
		setCellNumber(cell);
		
		FileSaver fs = new FileSaver(imageString);
		fs.saveAsPng(SOURCE_TRACKING_IMAGE);
	}
	
	
	
	private static void resize(File image, int width, int height) {
		
		if(width > IMAGE_RESIZE_WIDTH)
			width = IMAGE_RESIZE_WIDTH;
		if(height > IMAGE_RESIZE_HEIGHT)
			height = IMAGE_RESIZE_HEIGHT;
		
		BufferedImage original = null;
		try {
		    original = ImageIO.read(image);
		}
		catch (IOException ex) {
		    System.out.println("Unable to read image file");
		}

		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resized.createGraphics();
		g.drawImage(original, 0, 0, width, height, null);
		g.dispose();
		
		File savedImage = new File(SOURCE_TRACKING_IMAGE);
		try {
			ImageIO.write(resized, "png", savedImage);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void modify(int imageNumber, int cellID, boolean minMax, boolean randomParticle, boolean blackImage) {
		img = new Image(GUITrackerMain.getImage(imageNumber));
		imgArray = img.getImageArray();
		
		if(blackImage) {
			IImageManipulation mode = Tracking.getMode();
    		if(mode.equals(Cutting.getInstance())) {
    			imgMod = new int[Cutting.getInstance().getNewWidth()][Cutting.getInstance().getNewHeight()];
    			for(int column = 0; column < Cutting.getInstance().getNewWidth(); column++) {
    				for(int row = 0; row < Cutting.getInstance().getNewHeight(); row++) {
    					imgMod[column][row] = 0;
    				}
    			}
    		}
    		else {
    			imgMod = new int[imgArray.length][imgArray[0].length];
    			for(int column = 0; column < imgArray.length; column++) {
    				for(int row = 0; row < imgArray[0].length; row++) {
    					imgMod[column][row] = 0;
    				}
    			}
    		}
		}
		
		else {
			imgMod = mode.execute(imgArray, x[cellID], y[cellID]);
		}

		if(randomParticle)
			imgMod = RandomParticle.getInstance().execute(imgMod);
		if(minMax)
			imgMod = MinMax.getInstance().execute(imgMod);
			
		Image.saveImage(imgMod, "res\\artificial_track\\" + String.format("%03d", imageNumber) + ".png");
	}
}
