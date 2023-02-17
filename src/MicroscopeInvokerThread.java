



import ij.ImagePlus;

public class MicroscopeInvokerThread implements Runnable {
	
	/**
	 * @author Thomas Temme
	 * 
	 * Invokes the Microscope to create automatically new images and to move the stage to the current position
	 */
	
	
	private int delayTime;
	private String jobName;
	private boolean shouldRun = true;	
	
	/**
	 * Constructor
	 */
	public MicroscopeInvokerThread(String jobName, int delayTime)
	{
		this.delayTime = delayTime;
		this.jobName = jobName;		
	}
	
	//private static final Logger logger = LogManager.getLogger(MicroscopeInvokerThread.class);
	
	
	
	public final String MicroscopeCommandPipeline = "/cli:Leica_CAM_Tracking /app:matrix /cmd:startscan[BR]"
				+"/cli:Leica_CAM_Tracking /app:matrix /cmd:deletelist[BR]"
				+"/cli:Leica_CAM_Tracking /app:matrix /cmd:add /tar:camlist /exp:[EXPNAME] /ext:none /slide:0 /wellX:0 /wellY:0 /fieldX:0 /fieldY:0 /dxpos:[DX] /dypos:[DY][BR]"
				+"/cli:Leica_CAM_Tracking /app:matrix /cmd:startcamscan /runtime:1 /repeattime:1[BR]"
				+"/cli:Leica_CAM_Tracking /app:matrix /sys:1 /cmd:stopwaitingforcam";	
	
	@Override
	public void run() {
		String experimentName = this.jobName;
		int sleepTime = this.delayTime;
		CAMConnection camConnection = CAMConnection.getInstance();		
		System.out.println("Starting automatic invocation of Microscope");
		while(shouldRun)
		{
			int dx = ParticleContainer.getInstance().getCurrentDX() + ParticleContainer.getInstance().getGlobalPositionX();
			int dy = ParticleContainer.getInstance().getCurrentDY() + ParticleContainer.getInstance().getGlobalPositionY();
			String commandString = MicroscopeCommandPipeline.replace("[EXPNAME]", experimentName);
			commandString = commandString.replace("[DX]", Integer.toString(dx));
			commandString = commandString.replace("[DY]", Integer.toString(dy));
			//Send current command pipeline to Microscope
			
			String commandList[] = commandString.split("BR");
			for(int i=0; i<commandList.length; i++)
			{
				commandList[i]=commandList[i].replace("[", "");
				commandList[i]=commandList[i].replace("]", "");
				camConnection.sendCAMCommand(commandList[i]);
			}
			try {
			if(ParticleContainer.SEND_COMMAND_PIPELINE_TWICE)
			{
				Thread.sleep(1000);
				for(int i=0; i<commandList.length; i++)
				{
					commandList[i]=commandList[i].replace("[", "");
					commandList[i]=commandList[i].replace("]", "");
					camConnection.sendCAMCommand(commandList[i]);
				}
			}
			
			
			
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error during automated microscope Invocation: "+e.getMessage());
			}
		}
		System.out.println("Cell tracking stopped.");
	}
	
	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}
	
	public boolean isShouldRun() {
		return shouldRun;
	}


}
