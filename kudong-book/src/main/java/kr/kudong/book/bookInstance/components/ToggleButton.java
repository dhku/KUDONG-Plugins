package kr.kudong.book.bookInstance.components;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.util.FontSizeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ToggleButton extends SelectableComponents<Boolean>
{
	private final RadioButton radiobtn;
	private boolean state; 
	private String trueText = null;
	private String falseText = null;
	
	/**
	 * 라디오 버튼시 사용
	 * @param id
	 * @param page
	 * @param row
	 * @param column
	 * @param callback
	 * @param radiobtn
	 */
	public ToggleButton(String id, int page , int row, int column, BiConsumer<Component, Boolean> callback, RadioButton radiobtn)
	{
		super(id,page,row, column, callback);
		this.radiobtn = radiobtn;
		this.state = false;
		this.radiobtn.addBtn(this);
	}
	
	public ToggleButton(int row, BiConsumer<Component, Boolean> callback, RadioButton radiobtn)
	{
		super(UUID.randomUUID().toString(),0,row, 0, callback);
		this.radiobtn = radiobtn;
		this.state = false;
		this.radiobtn.addBtn(this);
	}
	
	/**
	 * 보편적인 토글 버튼
	 * @param id
	 * @param page
	 * @param row
	 * @param column
	 * @param init
	 * @param callback
	 */
	public ToggleButton(String id ,int page,int row, int column,boolean init,BiConsumer<Component, Boolean> callback)
	{
		super(id,page,row, column, callback);
		this.state = init;
		this.radiobtn = null;
	}
	
	public ToggleButton(int row , boolean init , BiConsumer<Component, Boolean> callback)
	{
		super(UUID.randomUUID().toString() ,0, row, 0, callback);
		this.state = init;
		this.radiobtn = null;
	}
	
	/**
	 * 라디오 버튼시 사용 
	 * @param id
	 * @param trueText
	 * @param falseText
	 * @param page
	 * @param row
	 * @param column
	 * @param callback
	 * @param radiobtn
	 */
	public ToggleButton(String id, String trueText , String falseText ,int page , int row, int column, BiConsumer<Component, Boolean> callback, RadioButton radiobtn)
	{
		super(id,page,row, column, callback);
		this.radiobtn = radiobtn;
		this.trueText = trueText;
		this.falseText = falseText;
		this.state = false;
		this.radiobtn.addBtn(this);

	}
	
	public ToggleButton(String trueText , String falseText , int row, BiConsumer<Component, Boolean> callback, RadioButton radiobtn)
	{
		super(UUID.randomUUID().toString() ,0, row, 0, callback);
		this.radiobtn = radiobtn;
		this.trueText = trueText;
		this.falseText = falseText;
		this.state = false;
		this.radiobtn.addBtn(this);

	}
	
	public ToggleButton(String id ,String trueText , String falseText , int page,int row, int column,boolean init,BiConsumer<Component, Boolean> callback)
	{
		super(id,page,row, column, callback);
		this.state = init;
		this.trueText = trueText;
		this.falseText = falseText;
		this.radiobtn = null;
	}
	
	public ToggleButton(String trueText , String falseText , int row, boolean init ,BiConsumer<Component, Boolean> callback)
	{
		super(UUID.randomUUID().toString() ,0, row, 0, callback);
		this.state = init;
		this.trueText = trueText;
		this.falseText = falseText;
		this.radiobtn = null;
	}

	public void setState(boolean btnState)
	{
		if(btnState != this.state)
		{
			this.state = btnState;
			this.stateChange(btnState);
		}
	}
	
	public boolean getState(){
		return state;
	}
	

	@Override
	public void touch()
	{
		if(this.radiobtn != null) 
		{
			if(this.state)
			{
				
				book.repaint(this.page);
				
				return;
			}
			else
			{
				this.state = true;
				this.radiobtn.toggle(this);
			}
		}
		else
		{
			if(this.state) this.state = false;
			else this.state = true;
			
		}
		this.stateChange(this.state);
	}

	@Override
	public int  buildBook(ComponentBuilder builder)
	{
		String show;
		builder.append(ChatColor.RESET+"",ComponentBuilder.FormatRetention.FORMATTING);
		ComponentBuilder temp;
		if(this.state) {
			if(trueText != null) {
				show = trueText;
				temp = new ComponentBuilder(show)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text("체크")));
			}else {
				show = "[○]";
				temp = new ComponentBuilder(show)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text("체크")));
			}
			
		}else {
			
			if(falseText != null) {
				show = falseText;
				temp = new ComponentBuilder(show)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new Text("체크")));
			}else {
				show = "[ ]";
				temp = new ComponentBuilder(show)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aldarbookupdate "+this.uuid.toString()))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new Text("체크")));
			}
			
		}
		
		builder.append(temp.create(),ComponentBuilder.FormatRetention.ALL);

		return FontSizeUtil.getByteSize(show);

	}
}
