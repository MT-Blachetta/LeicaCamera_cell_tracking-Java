package testTracksCreator.ImageManipulation;

public class Cutting implements IImageManipulation {
	
	private static Cutting instance = null;
	private int newHeight = 500;
	private int newWidth = 700;
	
	private Cutting()
	{	
	}

	public static Cutting getInstance()
	{
		if(instance == null)
			instance = new Cutting();
		return instance;
	}
	
	public void setNewHeight(int height) {
		this.newHeight = height;
	}
	
	public int getNewHeight() {
		return newHeight;
	}
	
	public int getNewWidth() {
		return newWidth;
	}
	
	public void setNewWidth(int width) {
		this.newWidth = width;
	}
	
	
	public int[][] execute(int[][] imgArray, float x, float y) {
		int[][] imgManp = new int[newWidth][newHeight];
		
		int start_x = (int)x - (newWidth/2);
		int start_y = (int)y - (newHeight/2);
		
		for(int column = 0; column < newWidth; column++) {
			for(int row = 0; row < newHeight; row++) {
				if(start_y + row < 0 || start_x + column < 0 || start_y + row > imgArray[0].length - 1 || start_x + column > imgArray.length - 1)
					imgManp[column][row] = 0;
				else
					imgManp[column][row] = imgArray[start_x + column][start_y + row];
			}
		}
		
		return imgManp;
	}
}