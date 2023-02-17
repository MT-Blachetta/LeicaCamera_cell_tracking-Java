

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*; import ij.gui.*;
import java.awt.event.*;

public class SelectCellMouseListener implements PlugInFilter, MouseListener
{
	ImagePlus img;
	ImageCanvas canvas;
	
	public int setup(String arg, ImagePlus img) 
	{
		this.img = img;
		return DOES_ALL+NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) 
	{
		ImageWindow win = img.getWindow();
		canvas = win.getCanvas();		
		canvas.addMouseListener(this);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();		
		canvas = (ImageCanvas)e.getSource();
		int offscreenX = canvas.offScreenX(x);
		int offscreenY = canvas.offScreenY(y);
		int[] dimensions = canvas.getImage().getDimensions();
		double height = dimensions[1];//canvas.getSize().getHeight();
		double width = dimensions[0];//canvas.getSize().getWidth();
		//Send this coordinate to Cell tracking API
		//Assume that medium coordinate is in the mid of the image and delta movements starts in the mid
		int dx = (int)(offscreenX - (width/2));
		int dy = (int)(offscreenY - (height/2));
		ParticleContainer.getInstance().setCurrentDX(dx);
		ParticleContainer.getInstance().setCurrentDY(dy);
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	
}
