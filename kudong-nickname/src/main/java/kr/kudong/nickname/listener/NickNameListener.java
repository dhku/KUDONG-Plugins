package kr.kudong.nickname.listener;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import kr.kudong.nickname.controller.NickNameManager;
import kr.kudong.nickname.controller.NickNamePlayer;
import kr.kudong.nickname.db.NickNameDBService;

public class NickNameListener implements Listener
{
	private Logger logger;
	private NickNameManager manager;
	private JavaPlugin plugin;
	
	public NickNameListener(Logger logger,JavaPlugin plugin,NickNameManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) 
	{
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		NickNameDBService service = this.manager.getService();
		NickNamePlayer np = this.manager.getNickNamePlayer(uuid);
		if(np == null)
		{
			np = new NickNamePlayer(uuid,p.getName(),null,null);
			np.setBukkitPlayer(p);
			
			boolean result = service.insertNickNamePlayer(np);
			
			if(result == false)
				p.sendMessage("데이터베이스에서 플레이어 정보를 등록하는데 실패하였습니다.");
			
			this.manager.registerNickNamePlayer(uuid, np);
		}
		else
		{
			np.setBukkitPlayer(p);
			this.manager.applyNickName(np);
		}
		
		if(np.hasNickname())
			event.setJoinMessage("§a[+] §f"+np.getNickName());
		else
			event.setJoinMessage("§a[+] §f"+np.getOriginalName());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) 
	{
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		if(this.manager.containsNickNamePlayer(uuid))
		{
			NickNamePlayer np = this.manager.getNickNamePlayer(uuid);
			
			if(np.hasNickname())
				event.setQuitMessage("§c[-] §f"+np.getNickName());
			else
				event.setQuitMessage("§c[-] §f"+np.getOriginalName());
			
			np.setBukkitPlayer(null);
		}
	}
	
	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent event)
	{
		String cmd = event.getMessage();
		if(cmd.matches("/땅관리 목록(.*)"))
		{
			String[] args = cmd.split(" ");
			String a = findPlayerOriginalName(args[2]);
			event.setMessage("/땅관리 목록 "+a);
		}
		else if(cmd.matches("/tp(.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 3)
			{
				String a = findPlayerOriginalName(args[1]);
				String b = findPlayerOriginalName(args[2]);
				event.setMessage("/tp "+a+" "+b);
			}
			else if(args.length == 2)
			{
				String a = findPlayerOriginalName(args[1]);
				event.setMessage("/tp "+a);
			}
		}
	}
	
	/**
	 * 없으면 기존 이름 그대로 반환
	 * @param name
	 * @return
	 */
	public String findPlayerOriginalName(String name)
	{
		for(NickNamePlayer np : this.manager.getMap().values())
		{
			if(np.getOriginalName().equals(name) || (np.hasNickname() && np.getNickName().equals(name))) return np.getOriginalName();
		}
		return name;
	}
	
	public NickNamePlayer findPlayer(String name)
	{
		for(NickNamePlayer np : this.manager.getMap().values())
		{
			if(np.getOriginalName().equals(name) || (np.hasNickname() && np.getNickName().equals(name))) return np;
		}
		return null;
	}
	
//	public static void main(String[] args)
//	{
//		String cmd = "/땅관리 목록 동혁";
//		String cmd2 = "/tp dhku 동혁";
//		if(cmd.matches("/땅관리 목록(.*)"))
//		{
//			String[] args2 = cmd.split(" ");
//			System.out.println(args2[0]);
//			System.out.println(args2[1]);
//			System.out.println(args2[2]);
//			
//		}
//	}

}
