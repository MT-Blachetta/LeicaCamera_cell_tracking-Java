package testTracksCreator.Tracker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	
	private int[][] imgArray;
 
	public Image(File path) { 
		try {
			BufferedImage img = ImageIO.read(path);
		
			int width = img.getWidth();
			int height = img.getHeight();
			imgArray = new int[width][height];
		
			for (int column = 0; column < width; column++)
			{
				for(int row = 0; row < height; row++) {
					imgArray[column][row] = img.getRGB(column, row);
				} 
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int[][] getImageArray() {
		return imgArray;
	}
	
	public static void saveImage(int[][] imgArray, String path) {
		int width = imgArray.length;
		int height = imgArray[0].length;
		
		BufferedImage bImage = new BufferedImage(width, height , BufferedImage.TYPE_INT_RGB);
		
		for(int column = 0; column < width; column++) {
			for(int row = 0; row < height; row++) {
				bImage.setRGB(column, row, imgArray[column][row]);
			}
		}

		File savedImage = new File(path);
		try {
			ImageIO.write(bImage, "png", savedImage);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}