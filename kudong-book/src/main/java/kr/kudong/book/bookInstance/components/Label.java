package kr.kudong.book.bookInstance.components;

import java.util.UUID;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.util.FontSizeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Label extends Component
{
	private String contents;
	private final String hoverText;
	
	public Label(String id ,String contents ,int page ,int row, int column)
	{
		super(id ,page, row, column);
		this.contents = contents;
		generateNewLine();
		this.hoverText = null;
	}
	
	public Label(String id ,String contents, String hovertext ,int page ,int row, int column)
	{
		super(id ,page, row, column);
		this.contents = contents;
		generateNewLine();
		this.hoverText = hovertext;
	}
	
	public String getContents()
	{
		return contents;
	}

	/**
	 * 첫페이지
	 * @param contents
	 * @param row
	 */
	public Label(String contents, int row)
	{
		super( UUID.randomUUID().toString() , 0 , row, 0);
		this.contents = contents;
		generateNewLine();
		this.hoverText = null;
	}
	
	/**
	 * 첫페이지
	 * @param contents
	 * @param hovertext
	 * @param row
	 */
	public Label(String contents, String hovertext , int row)
	{
		super( UUID.randomUUID().toString() , 0 , row, 0);
		this.contents = contents;
		generateNewLine();
		this.hoverText = hovertext;
	}

	private void generateNewLine() {
		
		int remainSize = AldarBook.COLUMN_MAX_SIZE - column;
		int contentSize = FontSizeUtil.getByteSize(contents);
		String finish = "";
		
		if(contentSize <= remainSize)
			return;

		if(remainSize<2) {
			
			finish += "\n";
			contentSize += 1;

		}
			
			int index = FontSizeUtil.getIndex(contents, remainSize-1);
			finish += contents.substring(0,index);
			finish += "\n";
			String temp = contents.substring(index);
			
			int row = FontSizeUtil.getByteSize(temp)/28;
			
			if(FontSizeUtil.getByteSize(temp) % 28 >0)
				row += 1;
			
			for(int i=0; i<row ;i++) {
				int a = FontSizeUtil.getIndex(temp, 26);
				String front = temp.substring(0,a+1);
				temp  = temp.substring(a+1);
				finish += front +"\n";
			}
			
			contents = finish;

	}
	

	@Override
	public int buildBook(ComponentBuilder builder)
	{
		builder.append(ChatColor.RESET+"",ComponentBuilder.FormatRetention.FORMATTING);
		
		ComponentBuilder temp;
		
		if(hoverText != null) {
			
			temp = new ComponentBuilder(contents)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(hoverText)));
			
		}else {
			
			temp = new ComponentBuilder(contents);
				

		}
		
		builder.append(temp.create(),ComponentBuilder.FormatRetention.ALL);
		return FontSizeUtil.getByteSize(contents);
	}
}
