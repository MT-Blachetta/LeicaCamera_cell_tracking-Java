package testTracksCreator.ImageManipulation;

public class Centering implements IImageManipulation {
	
	private static Centering instance = null;
	
	private Centering()
	{	
	}

	public static Centering getInstance()
	{
		if(instance == null)
			instance = new Centering();
		return instance;
	}
	
	
	public int[][] execute(int[][] imgArray, float x, float y) {
		int width = imgArray.length;
		int height = imgArray[0].length;
		int[][] imgTemp = new int[width][height];
		int[][] imgManp = new int[width][height];
		
		
		int center_x = width/2;
		int cut_x = 0;
		
		if(center_x > x)
			cut_x = center_x - (int)x;
		else
			cut_x = (width - (int)x) + center_x;
		
		for(int column = 0; column < cut_x; column++) {
			for(int row = 0; row < height; row++) {
				imgTemp[column][row] = imgArray[width - cut_x + column][row];
			}
		}
			
		for(int column = cut_x; column < width; column++) {
			for(int row = 0; row < height; row++) {
				imgTemp[column][row] = imgArray[column - cut_x][row];
			}
		}
		
		
		
		int center_y = height/2;
		int cut_y = 0;
		
		if(center_y > y)
			cut_y = center_y - (int)y;
		else
			cut_y = (height - (int)y) + center_y;
		
		for(int column = 0; column < width; column++) {
			for(int row = 0; row < cut_y; row++) {
				imgManp[column][row] = imgTemp[column][height - cut_y + row];
			}
		}
			
		for(int column = 0; column < width; column++) {
			for(int row = cut_y; row < height; row++) {
				imgManp[column][row] = imgTemp[column][row - cut_y];
			}
		}
		
		
		
		return imgManp;
	}
}