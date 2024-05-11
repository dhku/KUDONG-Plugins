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
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.db.NickNameResult;
import kr.kudong.nickname.NickNameCore;
import kr.kudong.nickname.controller.NickNameConfig;
import kr.kudong.nickname.controller.NickNameManager;
import kr.kudong.nickname.controller.NickNamePlayer;
import kr.kudong.nickname.db.NickNameDBService;

public class NickNameListener implements Listener
{
	private Logger logger;
	private NickNameManager manager;
	private JavaPlugin plugin;

	public NickNameListener(Logger logger, JavaPlugin plugin, NickNameManager manager)
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

		NickNamePlayer db = null;
		
		if(NickNameCore.hasJoinDBCache())
		{
			NickNameResult r = NickNameCore.joinCache.get(uuid);
			db = new NickNamePlayer(uuid,r.getOriginal(),r.getNickName(),null);
		}
		
		NickNamePlayer cache = this.manager.getNickNamePlayer(uuid);

		if(db == null)
		{
			cache = new NickNamePlayer(uuid, p.getName(), null, null);
			cache.setBukkitPlayer(p);

			boolean result = service.insertNickNamePlayer(cache);

			if(result == false) p.sendMessage("데이터베이스에서 플레이어 정보를 등록하는데 실패하였습니다.");

		}
		else
		{
			if(cache == null) cache = db;
			cache.setNickName(db.getNickName());
			cache.setOriginalName(db.getOriginalName());
			cache.setHasNickname(db.hasNickname() ? true : false);
			cache.setBukkitPlayer(p);
		}

		this.manager.registerNickNamePlayer(uuid, cache);
		this.manager.applyNickName(cache);

		String name = cache.hasNickname() ? cache.getNickName() : cache.getOriginalName();
		String format = NickNameConfig.joinMessage;
		format = format.replace("{player}", name);
		event.setJoinMessage(format);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		if(this.manager.containsNickNamePlayer(uuid))
		{
			NickNamePlayer np = this.manager.getNickNamePlayer(uuid);

			String name = np.hasNickname() ? np.getNickName() : np.getOriginalName();
			String format = NickNameConfig.quitMessage;
			format = format.replace("{player}", name);
			event.setQuitMessage(format);

			np.setBukkitPlayer(null);
		}
	}

	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent event)
	{
		String cmd = event.getMessage();
		if(cmd.matches("/땅관리 목록 (.*)"))
		{
			String[] args = cmd.split(" ");
			String a = findPlayerOriginalName(args[2]);
			event.setMessage("/땅관리 목록 "+a);
		}
		else if(cmd.matches("/tp (.*)"))
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
		else if(cmd.matches("/tphere (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				String a = findPlayerOriginalName(args[1]);
				event.setMessage("/tphere "+a);
			}
		}
		else if(cmd.matches("/tpa (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				String a = findPlayerOriginalName(args[1]);
				event.setMessage("/tpa "+a);
			}
		}
		else if(cmd.matches("/거래 신청 (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 3)
			{
				String a = findPlayerOriginalName(args[2]);
				event.setMessage("/거래 신청 "+a);
			}
		}
		else if(cmd.matches("/kill (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				NickNamePlayer n = findPlayer(args[1]);

				if(n.hasNickname()) event.setMessage("/kill "+n.getNickName());
				else event.setMessage("/kill "+n.getOriginalName());
			}
		}
		else if(cmd.matches("/lp user (.*) parent set (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 6)
			{
				String a = findPlayerOriginalName(args[2]);
				String b = args[5];
				event.setMessage("/lp user "+a+" parent set "+b);
			}
		}
		else if(cmd.matches("/eco give (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 4)
			{
				String a = findPlayerOriginalName(args[2]);
				String b = args[3];
				event.setMessage("/eco give "+a+" "+b);
			}
		}
		else if(cmd.matches("/eco take (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 4)
			{
				String a = findPlayerOriginalName(args[2]);
				String b = args[3];
				event.setMessage("/eco take "+a+" "+b);
			}
		}
		else if(cmd.matches("/eco reset (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 4)
			{
				String a = findPlayerOriginalName(args[2]);
				String b = args[3];
				event.setMessage("/eco reset "+a+" "+b);
			}
		}
		else if(cmd.matches("/eco set (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 4)
			{
				String a = findPlayerOriginalName(args[2]);
				String b = args[3];
				event.setMessage("/eco set "+a+" "+b);
			}
		}
		else if(cmd.matches("/op (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				NickNamePlayer np = findPlayer(args[1]);
				String a = np.getOriginalName();
				this.manager.getService().asyncUpdateAlias(np.getUniqueID(), "op", (result)->{});
				event.setMessage("/op "+a);
			}
		}
		else if(cmd.matches("/deop (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				NickNamePlayer np = findPlayer(args[1]);
				String a = np.getOriginalName();
				this.manager.getService().asyncUpdateAlias(np.getUniqueID(), null , (result)->{});
				event.setMessage("/deop "+a);
			}
		}
		else if(cmd.matches("/seen (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				String a = findPlayerOriginalName(args[1]);
				event.setMessage("/seen "+a);
			}
		}
//		else if(cmd.matches("/kudong move (.*)") || cmd.matches("/kd move (.*)"))
//		{
//			String[] args = cmd.split(" ");
//			if(args.length >= 3)
//			{
//				String a = findPlayerOriginalName(args[2]);
//
//				StringBuilder s = new StringBuilder();
//				for(int i = 3; i < args.length; i++)
//				{
//					s.append(args[i]);
//					s.append(" ");
//				}
//				String format = s.toString();
//				event.setMessage("/kudong move "+a+" "+format);
//			}
//		}
//		else if(cmd.matches("/kudong tp (.*)") || cmd.matches("/kd tp (.*)"))
//		{
//			String[] args = cmd.split(" ");
//			if(args.length == 3)
//			{
//				String a = findPlayerOriginalName(args[2]);
//				event.setMessage("/kudong tp "+a);
//			}
//			if(args.length == 4)
//			{
//				String a = findPlayerOriginalName(args[2]);
//				String b = findPlayerOriginalName(args[3]);
//				event.setMessage("/kudong tp "+a+" "+b);
//			}
//		}
//		else if(cmd.matches("/ws (.*)") || cmd.matches("/귓 (.*)") || cmd.matches("/귓속말 (.*)"))
//		{
//			String[] args = cmd.split(" ");
//			if(args.length >= 2)
//			{
//				String a = findPlayerOriginalName(args[1]);
//
//				StringBuilder s = new StringBuilder();
//				for(int i = 2; i < args.length; i++)
//				{
//					s.append(args[i]);
//					s.append(" ");
//				}
//
//				String format = s.toString();
//
//				switch (args[0])
//				{
//					case "/ws":
//						event.setMessage("/ws "+a+" "+format);
//						break;
//					case "/귓":
//						event.setMessage("/귓 "+a+" "+format);
//						break;
//					case "/귓속말":
//						event.setMessage("/귓속말 "+a+" "+format);
//						break;
//				}
//			}
//		}

	}
	
	@EventHandler
	public void onConsoleCommand(ServerCommandEvent event)
	{
		String cmd = event.getCommand();
		
		if(cmd.matches("op (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				NickNamePlayer np = findPlayer(args[1]);
				if(np != null)
					this.manager.getService().asyncUpdateAlias(np.getUniqueID(), "op", (result)->{});
			}
		}
		else if(cmd.matches("deop (.*)"))
		{
			String[] args = cmd.split(" ");
			if(args.length == 2)
			{
				NickNamePlayer np = findPlayer(args[1]);
				if(np != null)
					this.manager.getService().asyncUpdateAlias(np.getUniqueID(), null , (result)->{});
			}
		}
	}

	/**
	 * 없으면 기존 이름 그대로 반환
	 * 
	 * @param name
	 * @return
	 */
	public String findPlayerOriginalName(String name)
	{
		for(NickNamePlayer np : this.manager.getMap().values())
		{
			if(np.getOriginalName().equals(name) || (np.hasNickname() && np.getNickName().equals(name)))
				return np.getOriginalName();
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

	public static void main(String[] args)
	{
//		String cmd = "/lp user2 dhku parent set 개발자";
//		if(cmd.matches("/lp user (.*) parent set (.*)"))
//		{
//			String[] args2 = cmd.split(" ");
//			System.out.println(args2[2]);
//			System.out.println(args2[5]);
////			System.out.println(args2[2]);
//			
//		}
//		String cmd = "/땅관리 목록 (.*)";
//		if(cmd.matches("/땅관리 멤버관리 (.*)"))
//		{
//			String[] args2 = cmd.split(" ");
//			System.out.println(args2[2]);
//			System.out.println(args2[5]);
////			System.out.println(args2[2]);
//			
//		}
	}

}
