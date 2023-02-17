package testTracksCreator.ImageManipulation;

public class MirrorFlipping implements IImageManipulation {
	
	private static MirrorFlipping instance = null;
	
	private MirrorFlipping()
	{	
	}

	public static MirrorFlipping getInstance()
	{
		if(instance == null)
			instance = new MirrorFlipping();
		return instance;
	}
	
	
	public int[][] execute(int[][] imgArray, float x, float y) {
		int height = imgArray.length;
		int width = imgArray[0].length;
		int[][] imgManp = new int[height][width];

		for(int row = 0; row < height; row++) {
			for(int column = 0; column < width; column++) {
				imgManp[row][column] = imgArray[height-row-1][width-column-1];
			}
		}
		
		return imgManp;
	}
}
