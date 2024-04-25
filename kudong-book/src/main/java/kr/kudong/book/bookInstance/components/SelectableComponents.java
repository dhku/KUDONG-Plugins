package kr.kudong.book.bookInstance.components;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import kr.kudong.book.AldarBookManager;
import kr.kudong.book.api.AldarBook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public abstract class SelectableComponents<T> extends Component
{
	protected BiConsumer<Component, T> callback;
	public final UUID uuid;

	public SelectableComponents(String id,int page,int row, int column, BiConsumer<Component, T> callback2)
	{

		super(id,page,row, column);
		this.callback = callback2;
		this.uuid = UUID.randomUUID();
		builder = new ComponentBuilder();

	}

	public abstract void touch();
	
	protected void stateChange(T state)
	{
		book.repaint(this.page);
		this.callback.accept(this, state);
	}
}
