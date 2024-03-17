package kr.kudong.entity.data;



import lombok.Data;

@Data
public class SteerablePreset
{
	public double FORWARD_DEFAULT_MAXSPEED = 1.2;
	public double FORWARD_BOOST_MAXSPEED = 1.8;
	public double ACCELERATION_RATE = 0.1;
	public double DECELERATION_RATE = 0.1; 
	public double TRACTION = 0.3; // 1이면 바로 그냥 꺽음 / 0에 가까울수록 미끄러짐
	
	public SteerablePreset(
			double FORWARD_DEFAULT_MAXSPEED,
			double FORWARD_BOOST_MAXSPEED,
			double ACCELERATION_RATE,
			double DECELERATION_RATE,
			double TRACTION
			)
	{
		this.FORWARD_DEFAULT_MAXSPEED = FORWARD_DEFAULT_MAXSPEED;
		this.FORWARD_BOOST_MAXSPEED = FORWARD_BOOST_MAXSPEED;
		this.ACCELERATION_RATE = ACCELERATION_RATE;
		this.DECELERATION_RATE = DECELERATION_RATE;
		this.TRACTION = TRACTION;
	}
	
	public SteerablePreset(){}
}
