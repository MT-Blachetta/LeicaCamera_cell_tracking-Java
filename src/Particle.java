

/**
 * @author Thomas Temme
 */

import java.util.*;

public class Particle {
	private int id;
	private int timeVector;
	

	private int idWithinCurrentFrame;
	
	

	private double x;
	private double y;
	private double area;
	private double distance;
	private double meanIntensity;
	
	
	//Parameters for Multiframe Correspondence
	private Particle OldEdgePredecessor;
	private Particle OldEdgeSuccessor;
	private List<Particle> ExtensionSuccessors = new ArrayList<Particle>();
	private List<Particle> CorrectionSuccessors = new ArrayList<Particle>();
	private boolean visited = false;
	
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getGlobalId()
	{
		return ParticleContainer.getInstance().MAX_POSSIBLE_CELL_NUMBER * this.timeVector + this.idWithinCurrentFrame + 1;
	}
	
	public Particle getOldEdgePredecessor() {
		return OldEdgePredecessor;
	}

	public void setOldEdgePredecessor(Particle oldEdgePredecessor) {
		OldEdgePredecessor = oldEdgePredecessor;
	}
	
	public int getIdWithinCurrentFrame() {
		return idWithinCurrentFrame;
	}

	public void setIdWithinCurrentFrame(int idWithinCurrentFrame) {
		this.idWithinCurrentFrame = idWithinCurrentFrame;
	}
	
	public double getVelocity(int currentFrameBackCount, double currentDistance, Particle predecessor)
	{
		//Velocity is defined as the mean movement of a cell between two pixels
		//Assume that a velocity of 100 pixels per frame is 100% (maximum speed of a cell)
		if(predecessor != null)
		{
			currentDistance = currentDistance+this.getSpatialDistanceBetweenParticles(predecessor);
			currentFrameBackCount++;
			if(currentFrameBackCount < ParticleContainer.getInstance().VELOCITY_FRAME_NUMBER && predecessor.getOldEdgePredecessor() != null)
			{				
				return predecessor.getVelocity(currentFrameBackCount,currentDistance,predecessor.getOldEdgePredecessor());
			}
				
		}		

		return currentDistance / currentFrameBackCount;			
			
	}

	public Particle(int id, double area, double meanIntensity, double x, double y, int timeVector) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.timeVector = timeVector;
		this.area = area;
		this.meanIntensity=meanIntensity;
	}
	
	public double CompareTo(Particle otherParticle)
	{

			
		Double vThisParticle = this.getVelocity(0,0,this.getOldEdgePredecessor());
		Double vOtherParticle = otherParticle.getVelocity(0,0,this);
		Double velocityResult = Double.NaN;
		Double spatialResult = Double.NaN;
		Double timeResult = Double.NaN;
		Double brightnessResult = Double.NaN;
		
		spatialResult = getSpatialDistanceBetweenParticles(otherParticle);
		timeResult = getTimeDistanceBetweenParticles(otherParticle);
		brightnessResult = getBrightnessDistanceBetweenParticles(otherParticle);
		//Normalization
		//ToDo: Divide this by the image diagonal length
		
		//Werte den Abstand in quadrierter Weise ab 150 Pixeln abstand
		if(spatialResult > ParticleContainer.COMPARE_SECONDARY_CRITERION_WITHIN_PIXELS)
			spatialResult = Math.pow(spatialResult, 2);
		spatialResult = spatialResult / 1667;
		timeResult = timeResult / ParticleContainer.MAX_FRAME_NUMBER;
		brightnessResult = brightnessResult /60;
		
		
		velocityResult = 0.0;
		//ToDo: Komponenten müssen normalisiert werden, um die Faktoren auf einer einheitlichen Basis zu berechnen.
		if(vThisParticle.isNaN() == false && vOtherParticle.isNaN() == false && vOtherParticle.isInfinite() == false && vThisParticle.isInfinite() == false)
		{
			velocityResult = Math.abs(vThisParticle - vOtherParticle);
			//Normalization: Velocity difference of 100 is maximum
			//Spatial distance of 1667 is maximum
			velocityResult = velocityResult/100;	
		}
		double alternativeResult = (ParticleContainer.getInstance().VELOCITY_FACTOR) * velocityResult + (ParticleContainer.getInstance().SPATIAL_FACTOR) *  spatialResult + ParticleContainer.getInstance().TIME_FACTOR * timeResult + ParticleContainer.getInstance().BRIGHTNESS_FACTOR * brightnessResult;
		alternativeResult = alternativeResult * 100;
		this.setDistance(alternativeResult);
		return alternativeResult;
		
	}

	public double getTimeDistanceBetweenParticles(Particle otherParticle) {
		//+ Math.pow(this.getTimeVector() + otherParticle.getTimeVector()
		//Don't use spatial distance only. Also consider distance in time!		
		double spatialDistance = Math.pow(this.getX()-otherParticle.getX(), 2) + Math.pow(this.getY()-otherParticle.getY(), 2);
		spatialDistance = Math.sqrt(spatialDistance);
		double timeDistance =Math.pow(this.getTimeVector()-otherParticle.getTimeVector(), 2);
		timeDistance = Math.sqrt(timeDistance);
		//Special time penalty
		//timeDistance = timeDistance * 50;		
		return timeDistance;
	}
	
	public double getSpatialDistanceBetweenParticles(Particle otherParticle) {
		//+ Math.pow(this.getTimeVector() + otherParticle.getTimeVector()
		//Don't use spatial distance only. Also consider distance in time!		
		double spatialDistance = Math.pow(this.getX()-otherParticle.getX(), 2) + Math.pow(this.getY()-otherParticle.getY(), 2);
		spatialDistance = Math.sqrt(spatialDistance);
		return spatialDistance;		
	}
	
	public double getBrightnessDistanceBetweenParticles(Particle otherParticle)
	{
		return Math.abs(otherParticle.getMeanIntensity() - this.getMeanIntensity());
	}

	public double getArea() {
		return area;
	}
	
	public int getId() {
		return id;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getGlobalX()
	{
		return ParticleContainer.getInstance().getGlobalPositionX() + this.getX();
	}
	
	public double getGlobalY()
	{
		return ParticleContainer.getInstance().getGlobalPositionY() + this.getY();
	}
	
	public void setTimeVector(int timeVector) {
		this.timeVector = timeVector;
	}
	
	public void setArea(double area) {
		this.area = area;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public List<Particle> getExtensionSuccessors() {
		return ExtensionSuccessors;
	}

	public List<Particle> getCorrectionSuccessors() {
		return CorrectionSuccessors;
	}

	public Particle getOldEdgeSuccessor() {
		return OldEdgeSuccessor;
	}

	public void setOldEdgeSuccessor(Particle oldEdgeSuccessor) {
		OldEdgeSuccessor = oldEdgeSuccessor;
	}

	public int getTimeVector() {
		return timeVector;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getMeanIntensity() {
		return meanIntensity;
	}

	public void setMeanIntensity(double meanIntensity) {
		this.meanIntensity = meanIntensity;
	}

}
