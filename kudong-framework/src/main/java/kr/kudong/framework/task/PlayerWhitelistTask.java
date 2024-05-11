package kr.kudong.framework.task;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.MojangAPIUtil;
import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.command.WhitelistCommand;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.db.FrameworkWhiteList;
import kr.kudong.framework.db.NickNameResult;

public class PlayerWhitelistTask implements Runnable
{
	private final boolean isConsoleSender;
	private final Player player;
	private final String[] args;
	private final Logger logger;
	private final JavaPlugin plugin;
	
	public PlayerWhitelistTask(boolean isConsoleSender, Player player, String[] args,JavaPlugin plugin)
	{
		this.isConsoleSender = isConsoleSender;
		this.player = player;
		this.args = args;
		this.logger = Bukkit.getLogger();
		this.plugin = plugin;
	}
	
	public void printMsg(String msg)
	{
		if(!isConsoleSender) player.sendMessage(msg);
		else this.logger.log(Level.INFO, msg);
		return;
	}

	@Override
	public void run()
	{
		if(args.length == 2)
		{
			String username = args[1];

			if((args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("추가")) && args.length == 2)
			{
				NickNameResult result = NickNameResult.sendQuery(username);
				
				if(result != null)
				{
					this.printMsg("§c플레이어<§e"+username+"§c>는 화이트리스트에 이미 존재합니다.");
					return;
				}
				
				UUID uuid = MojangAPIUtil.getUUIDfromMojangAPI(username);
				
				if(uuid == null)
				{
					this.printMsg("§c아이디 <§e"+username+"§c>는 마인크래프트에서 존재하지 않는 아이디 입니다.");
					return;
				}
				else
				{
					if(FrameworkWhiteList.insertWhiteList(username, uuid))
					{
						this.printMsg("§a플레이어 <§e"+username+"§a>가 화이트리스트에 추가되었습니다.");
					}
					else
					{
						this.printMsg("§c플레이어 <§e"+username+"§c>를 화이트리스트에 추가하는데 실패하였습니다 :-(");
					}
				}
			}
			else if((args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("삭제")) && args.length == 2)
			{
				NickNameResult result = NickNameResult.sendQuery(username);
				
				if(result == null)
				{
					if(!isConsoleSender) player.sendMessage("§c플레이어<§e"+username+"§c>는 존재하지 않습니다!");
					else this.logger.log(Level.INFO, "플레이어<"+username+">는 존재하지 않습니다!");
					return;
				}
				else
				{
					if(FrameworkWhiteList.deleteWhiteList(result.getUuid()))
					{
						this.printMsg("§a플레이어 <§e"+username+"§a>가 화이트리스트에 삭제되었습니다.");
						Player pp = Bukkit.getPlayer(result.getUuid());
						
						String format = "당신은 해당 서버의 화이트리스트에서 제거 되었습니다 :-(";
						
						if(pp != null)
							pp.kickPlayer(format);
						else
						{
							if(!isConsoleSender) sendPlayerCommand(player.getUniqueId(),"/kick "+result.getOriginal()+" "+format);
						}
					}
					else
					{
						this.printMsg("§c플레이어 <§e"+username+"§c>를 화이트리스트에 삭제하는데 실패하였습니다 :-(");
					}
				}
			}
			else
				if(!isConsoleSender) WhitelistCommand.printHelpMessage(player);
		}
		else
			if(!isConsoleSender) WhitelistCommand.printHelpMessage(player);
	}
	
	public void sendPlayerCommand(UUID sender,String cmd)
	{
		if(FrameworkConfig.isBungeecord)
		{
			
			Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

			if(dummyPlayer == null)
			{
				this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
				return;
			}
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(ProtocolKey.COMMAND_PLAYER);
			out.writeUTF(sender.toString()); //format
			out.writeUTF(cmd); //format

			dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());

		}
	}
}