package kr.kudong.book.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import kr.kudong.book.AldarBookCore;
import kr.kudong.book.AldarBookManager;
import kr.kudong.book.bookInstance.components.Component;
import kr.kudong.book.bookInstance.components.Label;
import kr.kudong.book.bookInstance.components.SelectableComponents;
import kr.kudong.book.playerdata.AldarBookPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class AldarBook
{
	public static final int COLUMN_MAX_SIZE = 28; //default 28
	public final String id;
	protected final Map<Integer,Map<String, Component>> instance;
	protected final List<UUID> buttonList;
	private AldarBookManager manager;
	public ItemStack book;
	
	/**
	 * 리프레쉬 기능입니다.
	 * @param page
	 */
	abstract public void repaint(int page);
	
	public AldarBook(String id)
	{
		this.id = id;
		this.instance = new LinkedHashMap<>(); 
		this.buttonList = new ArrayList<>();
		this.manager = AldarBookCore.getMain().getBookAPI();
		book = new ItemStack(Material.WRITTEN_BOOK);
	}
	
	/**
	 * 컴포넌트를 추가합니다.
	 * @param component
	 * @return
	 */
	public AldarBook addComponent(Component component)
	{
		int page = component.page;

		component.setAldarBook(this);
		
		if(component instanceof SelectableComponents<?>) {
			SelectableComponents<?> compo = (SelectableComponents<?>) component;
			buttonList.add(compo.uuid);
			this.manager.registerComponent(compo);
		}
			
		if(instance.containsKey(page)) { //페이지가 존재하면 
			
			instance.get(page).put(component.id , component);
			
		}
		else {
			instance.put(page, new LinkedHashMap<>());
			instance.get(page).put(component.id , component);
		}
		
		book = null;
		
		return this;
	}
	
	/**
	 * 첫 페이지에서 
	 * @param lores
	 * @return
	 */
	public AldarBook addComponent(List<String> lores)
	{
		int count = 0;
		for(String lore : lores)
		{
			this.addComponent(new Label(lore,count++));
		}
		return this;
	}
	
	
	
	/**
	 * 컴포넌트를 삭제합니다.
	 * @param component
	 * @return
	 */
	public AldarBook removeComponent(Component component)
	{
		int page = component.page;
		instance.get(page).remove(component.id);
		book = null;
		return this;
	}
	
	/**
	 * 모든 컴포넌트를 삭제합니다.
	 * @return
	 */
	public AldarBook removeAllComponent()
	{
		instance.clear();
		book = null;
		return this;
	}
	
	protected void update(int page , ItemStack book, BookMeta bookMeta) {
		
		bookMeta.setTitle("kudong-book");
		bookMeta.setAuthor("KUDONG");
		
		LinkedList<Map<String, Component>> sortPage = new LinkedList<>();
		
		for(Entry<Integer,Map<String, Component>> temp : instance.entrySet()) {
			if(page == temp.getKey()) sortPage.addFirst(temp.getValue());
			else sortPage.add(temp.getValue());
		}
		

	    for(Map<String,Component> components: sortPage) {
	    	
	    	if(components == null)
	    		continue;
	    	
	    	ComponentBuilder builder = new ComponentBuilder("");

			int currentCol = 0; //의미 0번째에 들어갈수있습니다.
			int currentRow = 0;
			
			List<Component> sortList = new ArrayList<>();
			
			for(Entry<String,Component> temp : components.entrySet()) 
			{
				sortList.add(temp.getValue());
			}
			
			Collections.sort(sortList, new Comparator<Component>() {

				@Override
				public int compare(Component o1, Component o2)
				{
					if(o1.page == o2.page && o1.row == o2.row && o1.column == o2.column)
						return 0;
					
					int offset = o1.row*COLUMN_MAX_SIZE + o1.column;
					int offset2 = o2.row*COLUMN_MAX_SIZE + o2.column;
					
					if(offset > offset2) return +1;
					else return -1;
				}
				
			});

			int exceed_Row = 0;
			
			for(Component component : sortList) {
				
				//만약 이전 컴포넌트가 글자수 초과라면
				if(currentCol > COLUMN_MAX_SIZE) {
					int exceed = currentCol - COLUMN_MAX_SIZE;
					int _exceed_Row = exceed / COLUMN_MAX_SIZE;
					int exceed_Col = exceed % COLUMN_MAX_SIZE;
					
					if(exceed_Col>0) {
						_exceed_Row++;
					}
					
					currentRow += _exceed_Row;
					exceed_Row += _exceed_Row;
					currentCol = 0;
					//System.out.println("exceed_row:"+_exceed_Row);
				} 

				//현재 몇층인지 확인 	
				if(component.row + exceed_Row > currentRow) {
					int num = component.row - currentRow;
					for(int i=0; i < num ; i++) {
						builder.append("\n");
					}
					currentRow += num; 
					currentCol = 0;
				}
				
				int spaceLength = component.column - currentCol;
				
				for(int i =0 ; i<spaceLength ; i++) {
					builder.append(" ");
				}

				int componentlength = component.buildBook(builder);
				currentCol += spaceLength + componentlength;
				
			}
			
			bookMeta.spigot().addPage(builder.create());

	    }
	
		book.setItemMeta(bookMeta);
		
	}
	
	public ItemStack getBookItem()
	{
		return this.book;
	}
	
	/**
	 * 사용금지
	 * @return
	 */
	public List<UUID> getButtonList()
	{
		return buttonList;
	}
	
}
