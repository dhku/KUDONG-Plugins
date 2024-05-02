package kr.kudong.framework.chat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import me.clip.placeholderapi.PlaceholderAPI;

public class ChatListener implements Listener
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private ChatManager manager;
	
	public ChatListener(Logger logger, JavaPlugin plugin,ChatManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();
		
    	String format = new String(ChatConfig.format);
    	format = format.replace("{name}", player.getName());
    	format = format.replace("{message}", e.getMessage());
    	format = PlaceholderAPI.setPlaceholders(player, format);
    	format = format.replaceAll("&", "§");
		
    	e.setFormat(format);
    
    	if(ChatConfig.isBungeecord)
    	{
        	//서버 -> 번지
    	    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    	    out.writeUTF( ProtocolKey.CHAT_MESSAGE ); 
    	    out.writeUTF( player.getUniqueId().toString()); 
    	    out.writeUTF( player.getName());
    	    out.writeUTF( format );
    		player.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL , out.toByteArray());
    	}

//		this.logger.log(Level.INFO, "포맷> "+format);
//		this.logger.log(Level.INFO, "메세지> "+e.getMessage());
//		this.logger.log(Level.INFO, "display> "+player.getDisplayName());	
		
	}
}
