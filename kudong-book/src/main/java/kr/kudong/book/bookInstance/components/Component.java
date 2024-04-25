package kr.kudong.book.bookInstance.components;



import kr.kudong.book.api.AldarBook;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class Component
{
	public final String id;
	public final int row;
	public final int column;
	public final int page;
	protected AldarBook book;
	public ComponentBuilder builder;
	public String hoverText ="";
	
	public Component(String id ,int page ,int row, int column)
	{
		this.id = id;
		this.row = row;
		this.column = column;
		this.page = page;
//		this.book.addComponent(this);
	}
	
	public void setAldarBook(AldarBook book) {
		this.book = book;
	}
	
	public abstract int buildBook(ComponentBuilder builder);
	

}
