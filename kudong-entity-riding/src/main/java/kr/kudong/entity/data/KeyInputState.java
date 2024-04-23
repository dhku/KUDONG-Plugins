package kr.kudong.entity.data;

public class KeyInputState
{
	private float forward;
	private float sidewalks;
	private boolean shift;
	private boolean spacebar;
	
	public KeyInputState()
	{
		this.forward = 0.0f;
		this.sidewalks = 0.0f;
		this.shift = false;
		this.spacebar = false;
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

	public void setState(float forward,float sidewalks,boolean shift,boolean spacebar)
	{
		this.forward = forward;
		this.sidewalks = sidewalks;
		this.shift = shift;
		this.spacebar = spacebar;
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

	public boolean isSpacebar()
	{
		return spacebar;
	}

	public void setSpacebar(boolean spacebar)
	{
		this.spacebar = spacebar;
	}

}
