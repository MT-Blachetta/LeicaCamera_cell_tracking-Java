package testTracksCreator.Noise;

public class MinMax {
	private static MinMax instance = null;
	private int rateMin = 1000;
	private int rateMax = 1000;
	
	private MinMax()
	{	
	}

	public static MinMax getInstance()
	{
		if(instance == null)
			instance = new MinMax();
		return instance;
	}
	
	public void setNoiseRateMin(int rateMin) {
		this.rateMin = rateMin;
	}
	
	public void setNoiseRateMax(int rateMax) {
		this.rateMin = rateMax;
	}
	
	public int[][] execute(int[][] imgArray) {
		int width = imgArray.length;
		int height = imgArray[0].length;
		
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < rateMin; i++) {
			x = (int)(Math.random() * width); //random double 0.0 - 1.0
			y = (int)(Math.random() * height);
			
			imgArray[x][y] = 16777215;
			
			//How color data is stored (single int)
			//00000000 00000000 00000000 00000000
			//Alpha    Red      Green    Blue
			//00000000 11111111 11111111 11111111 -> maximal color (pure white) in decimal: 16777215
		}
		
		for(int i = 0; i < rateMax; i++) {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
			
			imgArray[x][y] = 0;
		}
		return imgArray;
	}
}