package kr.kudong.towny.dynmap.marker;

import java.io.Serializable;
import org.bukkit.util.Vector;

public class Vertex implements Serializable , Comparable<Vertex>
{
	protected int X;
	protected int Z;
	
	public Vertex(int X, int Z)
	{
		this.X = X;
		this.Z = Z;
	}
	
	public Vertex(Vector v)
	{
		this.X = v.getBlockX();
		this.Z = v.getBlockZ();
	}
	
	public int getX()
	{
		return this.X;
	}
	
	public int getZ()
	{
		return this.Z;
	}
	
	public int getBlockX()
	{
		return this.X;
	}
	
	public int getBlockZ()
	{
		return this.Z;
	}
	
	public Vector toVector()
	{
		return new Vector(this.X,0,this.Z);
	}
	
	@Override
	public String toString()
	{
		return "Vertex [X = " + this.X + " , Z ="+this.Z+"]";
	}
	
	public String serialize()
	{
		return this.X + ","+this.Z;
	}
	
	public static Vertex deserialize(String arg)
	{
		String[] argarr = arg.split(",");
		
		if(!(argarr.length == 2))
		{
			return null;
		}
		
		return new Vertex(Integer.parseInt(argarr[0]),
				Integer.parseInt(argarr[1]));
	}
	
    @Override
    public boolean equals(Object obj) {
    	
        if (!(obj instanceof Vertex)) {
            return false;
        }

        Vertex other = (Vertex) obj;

        return this.X == other.X && this.Z == other.Z && (this.getClass().equals(obj.getClass()));
        
    }
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		
		int hashCode = 1;
		
		hashCode = prime * hashCode + X;
		hashCode = prime * hashCode + Z;
		
		return hashCode;
	}
	
	public int getCnt()
	{
		return cnt;
	}

	public void addCount()
	{
		this.cnt += 1;
	}
	
	private int cnt = 1;
	private static final long serialVersionUID = 7860750531888537284L;

	@Override
	public int compareTo(Vertex o)
	{
		if(this.X < o.X)
			 return -1;
		else if(this.X > o.X)
		{
			return 1;
		}
		else
		{
			if(this.Z < o.Z)
				return -1;
			else if (this.Z > o.Z)
				return 1;
			else
				return 0;
		}
	}
	
//	public static void main(String[] args)
//	{
//		Vertex a = new Vertex(1,2);
//		Vertex b = new Vertex(2,4);
//		
//		Map<Vertex,Integer> list = new HashMap<>();
//		
//		list.put(a, 2);
//		list.put(new Vertex(2,4), 3);
//		
//		if(a.equals(new Vertex(1,2)))
//			System.out.println("good");
//		
//		System.out.println(list.get(new Vertex(1,2)));
//		
//		
//	}
}