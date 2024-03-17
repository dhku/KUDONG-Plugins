package kr.kudong.entity.data;

public class KeyInputState
{
	private float forward;
	private float sidewalks;
	private boolean shift;
	
	public KeyInputState()
	{
		this.forward = 0.0f;
		this.sidewalks = 0.0f;
		this.shift = false;
	}
	
	public float getForward()
	{
		return forward;
	}
	
	public float getSidewalks()
	{
		return sidewalks;
	}

	public boolean isShift()
	{
		return shift;
	}

	public void setState(float forward,float sidewalks,boolean shift)
	{
		this.forward = forward;
		this.sidewalks = sidewalks;
		this.shift = shift;
	}
	
	public void setForward(float forward)
	{
		this.forward = forward;
	}
	
	public void setSidewalks(float sidewalks)
	{
		this.sidewalks = sidewalks;
	}

	public void setShift(boolean shift)
	{
		this.shift = shift;
	}

}
