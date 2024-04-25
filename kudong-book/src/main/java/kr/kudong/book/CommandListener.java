package kr.kudong.book;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import kr.kudong.book.api.AldarBook;
import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.bookInstance.components.Button;
import kr.kudong.book.bookInstance.components.Component;
import kr.kudong.book.bookInstance.components.Label;
import kr.kudong.book.bookInstance.components.RadioButton;
import kr.kudong.book.bookInstance.components.SelectableComponents;
import kr.kudong.book.bookInstance.components.SelectableMember;
import kr.kudong.book.bookInstance.components.Switch;
import kr.kudong.book.bookInstance.components.ToggleButton;
import kr.kudong.book.bookInstance.components.UrlButton;
import kr.kudong.book.playerdata.AldarBookPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CommandListener implements CommandExecutor
{

	private AldarBookManager bookManager;
	private Logger logger;
	private JavaPlugin plugin;

	public CommandListener(Logger logger, AldarBookManager bookManager, JavaPlugin plugin)
	{
		this.logger = logger;
		this.bookManager = bookManager;
		this.plugin = plugin;

		PluginCommand cmd;
		cmd = this.plugin.getCommand("aldarbooktest");
		cmd.setExecutor(this);
		cmd = this.plugin.getCommand("aldarbookupdate");
		cmd.setExecutor(this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args)
	{

		Player player = (Player) sender;

		if (command.getName().equals("aldarbooktest"))
		{

			String s = new FontImageWrapper("betterranks:developer").getString();
			
			RadioButton rb = new RadioButton();
			
			AldarBook book = bookManager.createAldarBook("메뉴");
			book.addComponent(new Label("hello",0))
				.addComponent(new Label("안녕하세요", 0))
				.addComponent(new Label( s , 1))
				.addComponent(new Label( " test" , 1))
				.addComponent(new Label("제목13", "아아아아아아아아아아아아아아아아아아", 0, 4, 0))
				
				.addComponent(new UrlButton("버튼4", "[홈페이지 버튼 클릭]", "https://www.naver.com", "www.naver.com 으로 이동합니다.",
						0, 7, 4, (a, b) -> { }))
				.addComponent(new Button("버튼5", "[눌러보랑께]","gkgkgkdfgdgdg", 0, 8, 4, (a, b) ->{ }));
					
			bookManager.openBook(book, player.getUniqueId());
			
			

		} else if (command.getName().equals("aldarbookupdate"))
		{

			if (args.length == 1)
			{

				SelectableComponents<?> c = bookManager.commandMap.get(UUID.fromString(args[0]));
				if(c != null)c.touch();

			}

		}

		return true;
	}

}
