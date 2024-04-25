package kr.kudong.book.api;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.bookInstance.components.SelectableComponents;
import kr.kudong.book.playerdata.AldarBookPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;

public interface AldarBookAPI
{
	/**
	 * 해당 id의 AldarBook 인스턴스를 생성합니다.
	 * @param id
	 * @return
	 */
	public AldarBook createAldarBook(String id);
	/**
	 * 해당 책을 엽니다.
	 * @param book
	 * @param playerUUID
	 */
	public void openBook(AldarBook book,UUID playerUUID);
	/**
	 * 책을 닫습니다. (책을 보고있는중인 플레이어도 전부 책을 닫습니다)
	 * @param id
	 */
	public void closeAldarBook(String id);
	/**
	 * 알다르북 플레이어를 가져옵니다. 
	 * @param uuid
	 * @return
	 */
	public AldarBookPlayer getAldarBookPlayer(UUID uuid);


}
