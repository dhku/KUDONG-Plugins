package kr.kudong.framework.command;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.util.AldarLocation;
import kr.kudong.common.paper.util.AldarLocationUtil;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.db.NickNameResult;
import kr.kudong.framework.task.PlayerMoveTask;

public class FrameworkCommand implements TabExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;

	public FrameworkCommand(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("kudong");
		this.cmd.setExecutor(this);
		this.cmd.setTabCompleter(this);
	}

	public static void printHelpMessage(Player player)
	{
		player.sendMessage("§aKUDONG-FRAMEWORK §f명령어 도움말");
		player.sendMessage("========================================");
		player.sendMessage("§e/kudong 또는 /kd 또는 /구동 으로 사용가능");
		player.sendMessage("§e/kudong add §f<player>: 서버에 화이트리스트 플레이어를 추가합니다.");
		player.sendMessage("§e/kudong server : 현재 서버를 확인합니다.");
		player.sendMessage("§e/kudong server §f<서버> : 해당 서버로 이동합니다.");
		player.sendMessage("§e/kudong move §f<player> <서버>");
		player.sendMessage("§e/kudong move §f<player> <서버> <X> <Y> <Z> : world 기본월드로 전송");
		player.sendMessage("§e/kudong move §f<player> <서버> <월드> <X> <Y> <Z>");
		player.sendMessage("§e/kudong move §f<player> <서버> <월드> <X> <Y> <Z> <YAW> <PITCH>");
		player.sendMessage("§e/kudong tp §f<target_player>");
		player.sendMessage("§e/kudong tp §f<player> <target_player>");
		player.sendMessage("§e/kudong open §f: 일반유저 입장가능");
		player.sendMessage("§e/kudong close §f: 운영자만 입장가능");
		player.sendMessage("§e/kudong tab §f: Tab reload");

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isConsoleSender = false;
		Player player = null;

		if(sender instanceof ConsoleCommandSender) isConsoleSender = true;
		else player = (Player)sender;

		if(!isConsoleSender && !player.hasPermission("kudong.admin"))
		{
			player.sendMessage("§c해당 명령어를 사용할 권한이 없습니다.");
			return true;
		}

		if(args.length == 0)
		{
			if(!isConsoleSender) FrameworkCommand.printHelpMessage(player);
			return true;
		}

		if(FrameworkConfig.isBungeecord)
		{
			Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
					new PlayerMoveTask(isConsoleSender, player, args, plugin));
		}
		else
		{
			if(!isConsoleSender) player.sendMessage("§cKUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
			else this.logger.log(Level.INFO, "KUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length == 1) // args 길이가 1이 되기위해서 필요한 요소
		{
			return Arrays.asList("server", "move", "tp","tab","open","close");
		}
		else if(args.length == 2)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return getPlayers(args[1]);
			}
			else if((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("서버")))
			{
				return Arrays.asList("lobby", "server1", "server2");
			}
			else if((args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("텔포")))
			{
				return getPlayers(args[1]);
			}
		}
		else if(args.length == 3)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("lobby", "server1", "server2");
			}
			else if((args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("텔포")))
			{
				return getPlayers(args[1]);
			}
		}
		else if(args.length == 4)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("world", "world_nether", "world_the_end", "x");
			}
		}
		else if(args.length == 5)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("x", "y");
			}
		}
		else if(args.length == 6)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("y", "z");
			}
		}
		else if(args.length == 7)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("z");
			}
		}
		else if(args.length == 8)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("yaw");
			}
		}
		else if(args.length == 9)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
				return Arrays.asList("pitch");
			}
		}

		return Collections.emptyList();
	}

	private List<String> getPlayers(String input)
	{
		return Bukkit.getServer().getOnlinePlayers().stream().map(OfflinePlayer::getName)
				.filter(name -> StringUtil.startsWithIgnoreCase(name, input)).collect(Collectors.toList());
	}

}
