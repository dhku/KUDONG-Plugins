package kr.kudong.book.bookInstance.components;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.util.FontSizeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Switch<T> extends SelectableComponents<T>
{
	private SelectableMember<T>[] members;
	private int selectIndex;
	
	@SafeVarargs
	public Switch(String id,int page ,int row, int column,BiConsumer<Component, T> callback, int select, SelectableMember<T>... members)
	{
		super(id,page,row, column,  callback);
		this.members = members;
		this.selectIndex = select;
	}
	
	@SafeVarargs
	public Switch(int row ,BiConsumer<Component, T> callback, int select, SelectableMember<T>... members)
	{
		super(UUID.randomUUID().toString() ,0,row, 0,  callback);
		this.members = members;
		this.selectIndex = select;
	}
	
	@SafeVarargs
	public Switch(String id,int page,int row, int column, BiConsumer<Component, T> callback, SelectableMember<T>... members)
	{
		super(id,page,row, column, callback);
		this.members = members;
		this.selectIndex = 0;
	}
	
	@SafeVarargs
	public Switch(int row, BiConsumer<Component, T> callback, SelectableMember<T>... members)
	{
		super(UUID.randomUUID().toString() ,0,row, 0,  callback);
		this.members = members;
		this.selectIndex = 0;
	}
	

	public void touch()
	{
		
		++this.selectIndex;
		if(this.selectIndex >= this.members.length)
		{
			this.selectIndex = 0;
		}
		this.stateChange(this.members[this.selectIndex].member);
	}
	
	public T getState()
	{
		return this.members[this.selectIndex].member;
	}
	
	public String getStateShow()
	{
		return this.members[this.selectIndex].show;
	}


	@Override
	public int buildBook(ComponentBuilder builder)
	{
		
		
		builder.append(new ComponentBuilder(ChatColor.RESET+"").create(),ComponentBuilder.FormatRetention.FORMATTING);
		
		ComponentBuilder temp = new ComponentBuilder(getStateShow())
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text("버튼을 클릭해주세요")));
		
		builder.append(temp.create(),ComponentBuilder.FormatRetention.ALL);
		

		

		
		return FontSizeUtil.getByteSize(getStateShow());
		
	}

	
}
