package kr.kudong.nickname.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import kr.kudong.nickname.controller.NickNameManager;
import kr.kudong.nickname.controller.NickNamePlayer;
import kr.kudong.nickname.db.NickNameDBService;

public class CommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private NickNameManager manager;

	private final String regex = "^[a-zA-Z0-9_가-힣]*$";
	private final int minLength = 2;
	private final int maxLength = 10;

	public CommandManager(Logger logger, JavaPlugin plugin, NickNameManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("nic");
		this.cmd.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player)sender;
		UUID uuid = p.getUniqueId();
		NickNameDBService db = this.manager.getService();

		if(args.length == 0)
		{
			this.printHelpMessage(p);
			return true;
		}
		
		if((args[0].equalsIgnoreCase("리셋") || args[0].equalsIgnoreCase("reset")) && args.length == 2)
		{
			String playerName = args[1];

			NickNamePlayer np = this.findPlayer(playerName);

			if(np == null)
			{
				p.sendMessage("§c해당 플레이어는 존재하지 않습니다.");
				return true;
			}

			db.asyncUpdateNickName(np.getUniqueID(), null, (isSuccess) ->
			{
				if(isSuccess)
				{
					Bukkit.getScheduler().runTask(plugin, () ->
					{
						np.setNickName(null);
						np.setHasNickname(false);
						if(np.isOnline()) this.manager.applyNickName(np.getBukkitPlayer(), np.getOriginalName());
						p.sendMessage("§a"+playerName+"님의 닉네임을 리셋하였습니다");
					});
				}
				else
				{
					Bukkit.getScheduler().runTask(plugin, () ->
					{
						p.sendMessage("§c데이터베이스 오류로 닉네임을 변경하는데 실패하였습니다.");
					});
				}
			});

			return true;
		}

		if(args.length == 2)
		{
			String playerName = args[0];
			String nickname = args[1];

			NickNamePlayer np = this.findPlayer(playerName);

			if(np == null)
			{
				p.sendMessage("§c해당 플레이어는 존재하지 않습니다.");
				return true;
			}

			if(this.findPlayer(nickname) != null)
			{
				p.sendMessage("§c해당 닉네임은 이미 존재합니다.");
				return true;
			}

			// 닉네임 검증
			if(!isValidNickName1(nickname))
			{
				p.sendMessage("§c닉네임을 입력할수 있는 범위를 벗어났습니다.");
				return true;
			}

			if(!isValidNickName2(nickname))
			{
				p.sendMessage("§c닉네임에 들어갈수 없는 문자열이 존재합니다.");
				return true;
			}

			if(np.hasNickname() && nickname.equals(np.getNickName()))
			{
				p.sendMessage("§c이미 동일한 닉네임을 사용하고 있습니다.");
				return true;
			}

			db.asyncUpdateNickName(np.getUniqueID(), nickname, (isSuccess) ->
			{
				if(isSuccess)
				{
					Bukkit.getScheduler().runTask(plugin, () ->
					{
						np.setNickName(nickname);
						np.setHasNickname(true);
						if(np.isOnline()) this.manager.applyNickName(np);
						p.sendMessage("§a"+playerName+"님의 닉네임을 "+nickname+"(으)로 설정하였습니다.");
					});
				}
				else
				{
					Bukkit.getScheduler().runTask(plugin, () ->
					{
						p.sendMessage("§c데이터베이스 오류로 닉네임을 변경하는데 실패하였습니다.");
					});
				}
			});
			return true;
		}
		
		this.printHelpMessage(p);
		return true;
	}
	
	public void printHelpMessage(Player p)
	{
		p.sendMessage("§a닉네임 §f시스템 명령어 도움말");
		p.sendMessage("========================================");
		p.sendMessage("§e/닉변 <플레이어> <바꿀이름> §6: 플레이어의 닉네임을 변경합니다.");
		p.sendMessage("§e/닉변 리셋 <플레이어> §6: 플레이어의 닉네임을 리셋 합니다.");
	}

	public NickNamePlayer findPlayer(String name)
	{
		for(NickNamePlayer np : this.manager.getMap().values())
		{
			if(np.getOriginalName().equals(name) || (np.hasNickname() && np.getNickName().equals(name))) return np;
		}
		return null;
	}

	public boolean isValidNickName1(String nickname)
	{
		if(nickname.isEmpty()) return false;
		int length = nickname.length();
		return(this.minLength <= length && length <= this.maxLength);
	}

	public boolean isValidNickName2(String nickname)
	{
		return nickname.matches(regex);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length <= 1)
		{
			List<String> list = getPlayers(args[0]);
			list.add("리셋");
			return list;		
		}	
		if(args.length == 2) return getPlayers(args[1]);
		return Collections.emptyList();
	}

	private List<String> getPlayers(String input)
	{
	    return Bukkit.getServer().getOnlinePlayers()
	    	      .stream()
	    	      .map(OfflinePlayer::getName)
	    	      .filter(name -> StringUtil.startsWithIgnoreCase(name, input))
	    	      .collect(Collectors.toList());
	}

}
