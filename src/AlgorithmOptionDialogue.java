import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class AlgorithmOptionDialogue extends JFrame {

	private JPanel contentPane;
	
	private JTextField tfVisualTraceback;
	private JTextField tfEvaluationFrameCount;
	private JTextField tfMaxPossibleCellCount;
	private JTextField tfOffsetSize;
	private JTextField tfVelocityFrameCount;
	private JTextField tfCompareSecondaryCriterionWithinPixels;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AlgorithmOptionDialogue frame = new AlgorithmOptionDialogue();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AlgorithmOptionDialogue() {
		setTitle("Options");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 516, 373);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(248, 11, 246, 288);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblSecondaryOptions = new JLabel("Secondary Options");
		lblSecondaryOptions.setBounds(10, 11, 91, 14);
		panel.add(lblSecondaryOptions);
		
		tfVisualTraceback = new JTextField();
		tfVisualTraceback.setText("7");
		tfVisualTraceback.setColumns(10);
		tfVisualTraceback.setBounds(150, 133, 86, 20);
		panel.add(tfVisualTraceback);
		
		JLabel label = new JLabel("Visual Traceback");
		label.setBounds(62, 134, 86, 14);
		panel.add(label);
		
		JLabel label_1 = new JLabel("Evaluation Frame count");
		label_1.setBounds(28, 108, 125, 14);
		panel.add(label_1);
		
		tfEvaluationFrameCount = new JTextField();
		tfEvaluationFrameCount.setText("15");
		tfEvaluationFrameCount.setColumns(10);
		tfEvaluationFrameCount.setBounds(150, 104, 86, 20);
		panel.add(tfEvaluationFrameCount);
		
		tfMaxPossibleCellCount = new JTextField();
		tfMaxPossibleCellCount.setText("100");
		tfMaxPossibleCellCount.setColumns(10);
		tfMaxPossibleCellCount.setBounds(150, 76, 86, 20);
		panel.add(tfMaxPossibleCellCount);
		
		JLabel label_2 = new JLabel("Maximal possible cell count");
		label_2.setBounds(14, 79, 139, 14);
		panel.add(label_2);
		
		JLabel label_3 = new JLabel("Offset Size");
		label_3.setBounds(88, 47, 65, 21);
		panel.add(label_3);
		
		tfOffsetSize = new JTextField();
		tfOffsetSize.setText("2500");
		tfOffsetSize.setColumns(10);
		tfOffsetSize.setBounds(150, 47, 86, 20);
		panel.add(tfOffsetSize);
		
		JLabel Veloci = new JLabel("Velocity Frame count");
		Veloci.setBounds(40, 167, 100, 14);
		panel.add(Veloci);
		
		tfVelocityFrameCount = new JTextField();
		tfVelocityFrameCount.setText("10");
		tfVelocityFrameCount.setBounds(150, 164, 86, 20);
		panel.add(tfVelocityFrameCount);
		tfVelocityFrameCount.setColumns(10);
		
		tfCompareSecondaryCriterionWithinPixels = new JTextField();
		tfCompareSecondaryCriterionWithinPixels.setText("150");
		tfCompareSecondaryCriterionWithinPixels.setColumns(10);
		tfCompareSecondaryCriterionWithinPixels.setBounds(150, 198, 86, 20);
		panel.add(tfCompareSecondaryCriterionWithinPixels);
		
		JLabel lblMaxSpatialDistance = new JLabel("Max Spatial Distance");
		lblMaxSpatialDistance.setBounds(40, 201, 100, 14);
		panel.add(lblMaxSpatialDistance);
		
		JCheckBox cbSendPipelineTwice = new JCheckBox("Send Command Pipeline twice");
		cbSendPipelineTwice.setBounds(27, 226, 183, 23);
		panel.add(cbSendPipelineTwice);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 11, 228, 288);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblPrimaryAlgorithmOptions = new JLabel("Primary Algorithm Options");
		lblPrimaryAlgorithmOptions.setBounds(10, 11, 180, 14);
		panel_1.add(lblPrimaryAlgorithmOptions);
		
		JSlider slSpatial = new JSlider();
		slSpatial.setBounds(10, 53, 200, 26);
		panel_1.add(slSpatial);
		
		JLabel lblNewLabel = new JLabel("Spatial Weight");
		lblNewLabel.setBounds(20, 36, 86, 14);
		panel_1.add(lblNewLabel);
		
		JSlider slTime = new JSlider();
		slTime.setValue(30);
		slTime.setBounds(10, 105, 200, 26);
		panel_1.add(slTime);
		
		JLabel lblTimeWeight = new JLabel("Time Weight");
		lblTimeWeight.setBounds(20, 88, 86, 14);
		panel_1.add(lblTimeWeight);
		
		JLabel lblBrightnessWeight = new JLabel("Brightness Weight");
		lblBrightnessWeight.setBounds(20, 142, 102, 14);
		panel_1.add(lblBrightnessWeight);
		
		JSlider slBrightness = new JSlider();
		slBrightness.setValue(20);
		slBrightness.setBounds(10, 159, 200, 26);
		panel_1.add(slBrightness);
		
		JLabel lblVelocityWeight = new JLabel("Velocity Weight");
		lblVelocityWeight.setBounds(20, 196, 102, 14);
		panel_1.add(lblVelocityWeight);
		
		JSlider slVelocity = new JSlider();
		slVelocity.setValue(0);
		slVelocity.setBounds(10, 213, 200, 26);
		panel_1.add(slVelocity);
		
		JButton btOk = new JButton("OK");
		btOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParticleContainer.BRIGHTNESS_FACTOR = (double)(double)slBrightness.getValue() / (double)100;
				ParticleContainer.VELOCITY_FACTOR = (double)(double)slVelocity.getValue() / (double)100;
				ParticleContainer.SPATIAL_FACTOR = (double)(double)slSpatial.getValue() / (double)100;
				ParticleContainer.TIME_FACTOR = (double)(double)slTime.getValue() / (double)100;
				ParticleContainer.MAX_VISIBLE_TRACK_NUMBER = Integer.parseInt(tfVisualTraceback.getText());
				ParticleContainer.COMPARE_SECONDARY_CRITERION_WITHIN_PIXELS = Integer.parseInt(tfCompareSecondaryCriterionWithinPixels.getText());
				ParticleContainer.MAX_WELL_PIXEL_SIZEX = Integer.parseInt(tfOffsetSize.getText());
				ParticleContainer.MAX_WELL_PIXEL_SIZEY = Integer.parseInt(tfOffsetSize.getText());
				ParticleContainer.MAX_FRAME_NUMBER = Integer.parseInt(tfEvaluationFrameCount.getText());
				ParticleContainer.VELOCITY_FRAME_NUMBER = Integer.parseInt(tfVelocityFrameCount.getText());
				ParticleContainer.MAX_POSSIBLE_CELL_NUMBER = Integer.parseInt(tfMaxPossibleCellCount.getText());
				if(cbSendPipelineTwice.isSelected())
					ParticleContainer.SEND_COMMAND_PIPELINE_TWICE = true;
				else
					ParticleContainer.SEND_COMMAND_PIPELINE_TWICE = false;		
				CloseFrame();
			}
		});
		btOk.setBounds(10, 310, 89, 23);
		contentPane.add(btOk);
	}
	
	public void CloseFrame(){
		super.setVisible(false);
	    super.dispose();
	}
	
	
}
