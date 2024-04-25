package kr.kudong.book.bookInstance.components;

import java.util.function.BiConsumer;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.util.FontSizeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class UrlButton extends SelectableComponents<Boolean>
{
	
	private final String text;
	private final String hoverText;
	private final String url;
	
	public UrlButton(String id, String text ,String url , int page, int row, int column, BiConsumer<Component, Boolean> callback2)
	{
		super(id, page, row, column, callback2);
		this.text = text;
		this.hoverText = null;
		this.url = url;

	}
	
	public UrlButton(String id, String text , String url , String hovertext, int page, int row, int column, BiConsumer<Component, Boolean> callback2)
	{
		super(id, page, row, column, callback2);
		this.text = text;
		this.hoverText = hovertext;
		this.url = url;
	}

	@Override
	public void touch()
	{
		this.callback.accept(this, true);
	}

	@Override
	public int buildBook(ComponentBuilder builder)
	{
		ComponentBuilder temp;
		builder.append(ChatColor.RESET+"",ComponentBuilder.FormatRetention.FORMATTING);
		
		if(hoverText != null) {
			temp = new ComponentBuilder(text)
					.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(hoverText)));
			
		}
		else {
			temp = new ComponentBuilder(text)
					.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

		}

		builder.append(temp.create(),ComponentBuilder.FormatRetention.ALL);
		return FontSizeUtil.getByteSize(text);
		
	}
	

}
