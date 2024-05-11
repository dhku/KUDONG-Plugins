package kr.kudong.framework.command;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.controller.FrameworkManager;
import net.md_5.bungee.api.chat.TextComponent;


public class WhisperCommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;

	public WhisperCommandManager(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("ws");
		this.cmd.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isConsoleSender = false;
		Player player = null;

		if(sender instanceof ConsoleCommandSender)
		{
			isConsoleSender = true;
		}
		else
		{
			player = (Player)sender;
		}
		
		if(isConsoleSender) return true;

		if(args.length <= 1)
		{
			player.sendMessage(new TextComponent("§e/귓 <플레이어> <할말> §f: 플레이어에게 귓속말을 보냅니다."));
		}

		if(args.length > 1)
		{
			String target = args[0];

			StringBuilder s = new StringBuilder();
			for(int i = 1; i < args.length; i++)
			{
				s.append(args[i]);
				s.append(" ");
			}
			String msg = s.toString();

			UUID playeruuid = this.manager.findPlayer(target);

			if(playeruuid != null)
			{
				if(playeruuid.equals(player.getUniqueId()))
				{
					player.sendMessage(new TextComponent("§c자신에게는 메세지를 보낼수 없습니다."));
					return true;
				}
				this.sendWhisperMessage(player.getUniqueId(),playeruuid, msg);
			}
			else
			{
				player.sendMessage(new TextComponent("§c해당 플레이어는 존재하지 않습니다."));
			}
		}
		return true;
	}

	private void sendWhisperMessage(UUID base, UUID target, String msg)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO, "최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.WHISPER_MESSAGE);
		out.writeUTF(base.toString());
		out.writeUTF(target.toString());
		out.writeUTF(msg);

		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());

	}

}
