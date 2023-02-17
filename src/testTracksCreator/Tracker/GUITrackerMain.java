package testTracksCreator.Tracker;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import testTracksCreator.ImageManipulation.*;
import testTracksCreator.Noise.*;
 
public class GUITrackerMain{
	
	private static ArrayList<File> images;
	private JFrame frame;
	private static int frameNumber = 0;
	
	private static int PANEL_HEIGHT = 905;
	private static int PANEL_WIDTH = 935;
	
	private static int FRAME_HEIGHT = 950;
	private static int FRAME_WIDTH = 1200;
	
	private static String SOURCE_TRACKING_IMAGE = "res\\z_LabelledImage.png";
	
	private static boolean trackingFinished = false;
	
	public static int getPanelHeight() {
		return PANEL_HEIGHT;
	}
	
	public static int getPanelWidth() {
		return PANEL_WIDTH;
	}
	
	public static String getSourceImage() {
		return SOURCE_TRACKING_IMAGE;
	}
	
	public GUITrackerMain() {
		initialize();
	}
	
	public static File getImage(int index) {
		return images.get(index);
	}
	
	public static int getFrameNumber() {
		return frameNumber;
	}
	
	public static void increaseFrameNumber() {
		frameNumber++;
	}
	
	public static void main(String[] args) {
		
		try {
			File image = new File("res\\original_track");
			images = new ArrayList<File>(Arrays.asList(image.listFiles()));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		Tracking.analyze(0);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUITrackerMain gui = new GUITrackerMain();
					gui.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
     
     private void initialize() {
    	 //********************
    	 //Initialize Frame
    	 //********************
         frame = new JFrame("Test Track Creator");         
         
         Container pane = frame.getContentPane();
         pane.setLayout(null);

 		 frame.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
 		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 		 
 		 //********************
    	 //Tracking Image
    	 //********************
 		 
 		 JLabel labelImage = new JLabel();
         labelImage.setBounds(240, 0, 960, 950);
         labelImage.setVerticalAlignment(JLabel.TOP);
         pane.add(labelImage);
        
         try {
		    BufferedImage bufImg = ImageIO.read(new File(SOURCE_TRACKING_IMAGE));
		    labelImage.setIcon(new ImageIcon(bufImg));
		    labelImage.repaint();
		  }
		  catch (IOException ex) {
			 System.out.println("Unable to read image file");
	      }
 		
         
         //********************
    	 //Cell ID
    	 //********************
         
 		 JLabel labelTracker = new JLabel("Enter Cell-ID you wish to center");
         labelTracker.setBounds(10, 10, 250, 18);
         pane.add(labelTracker);
         
         JTextField tfieldCell = new JTextField(3);
         tfieldCell.setBounds(10, 30, 42, 18);
         pane.add(tfieldCell);
         
         //********************
    	 //Mode
    	 //********************
         
         JLabel labelMode = new JLabel("Mode");
         labelMode.setBounds(10, 59, 90, 18);
         pane.add(labelMode);
         
         JRadioButton modeCutting = new JRadioButton("Cutting");
         modeCutting.setBounds(10, 80, 70, 18);
         pane.add(modeCutting);
         
         //cutting mode as default setting
         Tracking.setMode("cutting");
         modeCutting.setSelected(true);
         
         JLabel labelWidth = new JLabel("W: ");
         labelWidth.setBounds(86, 80, 20, 18);
         pane.add(labelWidth); 
         
         JTextField tfieldCuttingWidth = new JTextField("700", 5);
         tfieldCuttingWidth.setBounds(105, 80, 40, 18);
         pane.add(tfieldCuttingWidth);
        
         JLabel labelHeight = new JLabel("H: ");
         labelHeight.setBounds(155, 80, 36, 18);
         pane.add(labelHeight);  
         
         JTextField tfieldCuttingHeight = new JTextField("500", 5);
         tfieldCuttingHeight.setBounds(168, 80, 40, 18);
         pane.add(tfieldCuttingHeight);
         
         JRadioButton modeCentering = new JRadioButton("Centering");
         modeCentering.setBounds(10, 100, 90, 18);
         pane.add(modeCentering);

         ButtonGroup group = new ButtonGroup();
         group.add(modeCutting);
         group.add(modeCentering);
         
         modeCutting.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 Tracking.setMode("cutting");
             }
         });
         
         modeCentering.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 Tracking.setMode("centering");
             }
         });
         
         //********************
    	 //Noise
    	 //********************
         
         JLabel labelNoise = new JLabel("Noise");
         labelNoise.setBounds(10, 140, 90, 18);
         pane.add(labelNoise);
         
         JCheckBox noiseMinMax = new JCheckBox("MinMax");
         noiseMinMax.setBounds(10, 160, 120, 18);
         pane.add(noiseMinMax);
         
         JCheckBox noiseRandomParticle = new JCheckBox("Random Particle");
         noiseRandomParticle.setBounds(10, 220, 120, 18);
         pane.add(noiseRandomParticle);
         
         JCheckBox noiseBlackImage = new JCheckBox("Black Image");
         noiseBlackImage.setBounds(10, 280, 120, 18);
         pane.add(noiseBlackImage);
         
         JLabel labelMinRate = new JLabel("Min Rate: ");
         labelMinRate.setBounds(41, 180, 60, 18);
         pane.add(labelMinRate);
         
         JTextField tfieldMinRate = new JTextField("1000", 5);
         tfieldMinRate.setBounds(100, 180, 70, 18);
         pane.add(tfieldMinRate);
         
         JLabel labelMaxRate = new JLabel("Min Rate: ");
         labelMaxRate.setBounds(41, 200, 60, 18);
         pane.add(labelMaxRate);
         
         JTextField tfieldMaxRate = new JTextField("1000", 5);
         tfieldMaxRate.setBounds(100, 200, 70, 18);
         pane.add(tfieldMaxRate);
         
         JLabel labelParticleNumber = new JLabel("Number: ");
         labelParticleNumber.setBounds(45, 240, 60, 18);
         pane.add(labelParticleNumber);
         
         JTextField tfieldParticleNumber = new JTextField("10", 5);
         tfieldParticleNumber.setBounds(100, 240, 70, 18);
         pane.add(tfieldParticleNumber);
         
         JLabel labelParticleSize = new JLabel("Size: ");
         labelParticleSize.setBounds(63, 260, 60, 18);
         pane.add(labelParticleSize);
         
         JTextField tfieldParticleSize = new JTextField("30", 5);
         tfieldParticleSize.setBounds(100, 260, 70, 18);
         pane.add(tfieldParticleSize);
         
         JButton buttonCreate = new JButton("Create");
         buttonCreate.setBounds(62, 30, 100, 18);
         pane.add(buttonCreate);
         
         //********************
    	 //Action when Button is finally pressed!
    	 //********************
         
         buttonCreate.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 
            	 if(trackingFinished)
            		 System.out.println("Tracking finished");
            	 else {
            		 boolean cuttingValidated = true;
	            	 boolean minMaxValidated = true;
	            	 boolean randomParticleValidated = true;
	            	 boolean numberValidated = false;
	            	 
	            	 //********************
	            	 //Modus: Cutting
	            	 //********************
	            	 
	            	 IImageManipulation mode = Tracking.getMode();
	         		 if(mode.equals(Cutting.getInstance())) {
	         			cuttingValidated = false;
	         			try {
	         				int width = Integer.parseInt(tfieldCuttingWidth.getText());
	               	   		int height = Integer.parseInt(tfieldCuttingHeight.getText());
	     					if(width > 0 && height <= 3000 && width > 0 && width <= 3000) {
	     						cuttingValidated = true;
	     					}
	     				 }
	     				 catch(NumberFormatException ee) {
	     				 }
	         		 }
	         		 
	         		 //********************
	            	 //Noise: MinMax
	            	 //********************
	            	 
	                 boolean checkMinMax = noiseMinMax.isSelected();
	                 if(checkMinMax) {
	                	 minMaxValidated = false;
	                	 try {
	                		 int minRate = Integer.parseInt(tfieldMinRate.getText());
	                    	 int maxRate = Integer.parseInt(tfieldMaxRate.getText());
	      					 if(minRate > 0 && minRate <= 5000 && maxRate > 0 && maxRate <= 5000) {
	      						 MinMax.getInstance().setNoiseRateMin(minRate);
	      						 MinMax.getInstance().setNoiseRateMax(maxRate);
	      						 minMaxValidated = true;
	      					}
	      				 }
	      				 catch(NumberFormatException ee) {
	      				 }
	                 }
	                 
	                 //********************
	            	 //Noise: RandomParticle
	            	 //********************
	                 
	                 boolean checkRandomParticle = noiseRandomParticle.isSelected();
	                 if(checkRandomParticle) {
	                	 randomParticleValidated = false;
	                	 try {
	                		 int particleNumber = Integer.parseInt(tfieldParticleNumber.getText());
	                    	 int particleSize = Integer.parseInt(tfieldParticleSize.getText());
	      					 if(particleNumber > 0 && particleNumber <= 500 && particleSize > 0 && particleSize <= 1000) {
	      						 RandomParticle.getInstance().setParticleNumber(particleNumber);
	      	                	 RandomParticle.getInstance().setParticleSize(particleSize);
	      	                	 randomParticleValidated = true;
	      					}
	      				 }
	      				 catch(NumberFormatException ee) {
	      				 }
	                 }
	                 
	                 //********************
	            	 //Noise: Black Image
	            	 //********************
	                 
	                 boolean checkBlackImage = noiseBlackImage.isSelected();
	                 
	                 //********************
	            	 //Choose Cell ID
	            	 //********************
	                 
	 				 String trackedCell;
	 				 int cellID = 0;
	
	 				 trackedCell = tfieldCell.getText();
	 				 try {
	 					cellID = Integer.parseInt(trackedCell);
	 					if(cellID >=0 && cellID < Tracking.getCellNumber()) {
	 						numberValidated = true;
	 					}
	 				 }
	 				 catch(NumberFormatException ee) {
	 				 }
	 				 
	 				 //********************
	            	 //Try Tracking
	            	 //********************
	                 
	 				 if(numberValidated && cuttingValidated && minMaxValidated && randomParticleValidated) {
		                 Tracking.modify(frameNumber, cellID, checkMinMax, checkRandomParticle, checkBlackImage);
		                 increaseFrameNumber();
		                 
		                 if(frameNumber < images.size()) {
		                	 Tracking.analyze(frameNumber);
		                	 try {
		                		 BufferedImage bufImg = ImageIO.read(new File(SOURCE_TRACKING_IMAGE));
		                		 labelImage.setIcon(new ImageIcon(bufImg));
		                		 labelImage.repaint();
		                	 }
		                	 catch (IOException ex) {
		                		 System.out.println("Unable to read image file");
		                	 }
		                 }		 
		                 else
		                	 trackingFinished = true;   
	 				 }
	 				 else {
	 					 if(!numberValidated)
	 						 System.out.println("CellID wrong - Only 0 to " + Tracking.getCellNumber() + " allowed!");
	 					 if(!cuttingValidated)
	 						 System.out.println("Cutting out of range - Only W: 0 - 3000, H: 0 - 3000 allowed!");
	 					 if(!minMaxValidated)
	 						 System.out.println("MinMax out of range - Only Min Rate: 0 - 5000, Max Rate: 0 - 5000 allowed!");
	 					 if(!randomParticleValidated)
	 						 System.out.println("Random Particle out of Range - Only Number: 0 - 500, Size: 0 - 1000 allowed!");
	 				 }

            	 }
            	 
              }
         });
 	}           	 
}