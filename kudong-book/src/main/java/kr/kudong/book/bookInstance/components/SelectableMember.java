package kr.kudong.book.bookInstance.components;

public class SelectableMember<T>
{
	public final T member;
	public final String show;
	
	public SelectableMember(T member, String show)
	{
		this.member = member;
		this.show = show;
	}
	
	
}
