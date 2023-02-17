package testTracksCreator.Noise;

public class RandomParticle {
	private static RandomParticle instance = null;
	private int size = 30;
	private int number = 10;
	
	private RandomParticle()
	{	
	}

	public static RandomParticle getInstance()
	{
		if(instance == null)
			instance = new RandomParticle();
		return instance;
	}
	
	public void setParticleNumber(int number) {
		this.number = number;
	}
	
	public void setParticleSize(int size) {
		this.size = size;
	}
	
	public int[][] execute(int[][] imgArray) {
		int width = imgArray.length;
		int height = imgArray[0].length;
		
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < number; i++) {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
			
			for(int column = 0; column < size; column++)
				for(int row = 0; row < size; row++) {
					if(x + column > width - 1 || y + row > height - 1)
						break;
					imgArray[x + column][y + row] = 16777215; //maximal color value
				}
		}
		
		return imgArray;
	}

}
