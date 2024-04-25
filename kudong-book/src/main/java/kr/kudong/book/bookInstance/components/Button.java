package kr.kudong.book.bookInstance.components;

import java.util.UUID;
import java.util.function.BiConsumer;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.util.FontSizeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Button extends SelectableComponents<Boolean>
{
	
	private final String text;
	private final String hoverText;

	public Button(String id, String text , int page, int row, int column, BiConsumer<Component, Boolean> callback2)
	{
		super(id, page, row, column, callback2);
		this.text = text;
		this.hoverText = null;
	}
	
	public Button(String id, String text , String hovertext , int page, int row, int column, BiConsumer<Component, Boolean> callback2)
	{
		super(id, page, row, column, callback2);
		this.text = text;
		this.hoverText = hovertext;
	}
	
	public Button(String text, int row, BiConsumer<Component, Boolean> callback2)
	{
		super(UUID.randomUUID().toString(), 0, row, 0, callback2);
		this.text = text;
		this.hoverText = null;
	}
	
	public Button(String text, String hovertext, int row, BiConsumer<Component, Boolean> callback2)
	{
		super(UUID.randomUUID().toString(), 0, row, 0, callback2);
		this.text = text;
		this.hoverText = hovertext;
	}

	@Override
	public void touch()
	{
		this.callback.accept(this, true);
	}

	@Override
	public int buildBook(ComponentBuilder builder)
	{
		builder.append(ChatColor.RESET+"",ComponentBuilder.FormatRetention.FORMATTING);
		ComponentBuilder temp;
		
		if(hoverText != null) {
			temp = new ComponentBuilder(text)
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(hoverText)));
			
		}
		else {
			temp = new ComponentBuilder(text)
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()));
		}

		builder.append(temp.create(),ComponentBuilder.FormatRetention.ALL);
		
		return FontSizeUtil.getByteSize(text);
		
	}
	
	

}
