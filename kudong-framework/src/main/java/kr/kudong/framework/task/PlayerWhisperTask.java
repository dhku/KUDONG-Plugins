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

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.db.NickNameResult;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerWhisperTask implements Runnable
{
	private final boolean isConsoleSender;
	private final Player player;
	private final String[] args;
	private final Logger logger;
	private final JavaPlugin plugin;
	
	public PlayerWhisperTask(boolean isConsoleSender, Player player, String[] args,JavaPlugin plugin)
	{
		this.isConsoleSender = isConsoleSender;
		this.player = player;
		this.args = args;
		this.logger = Bukkit.getLogger();
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		String target = args[0];

		StringBuilder s = new StringBuilder();
		for(int i = 1; i < args.length; i++)
		{
			s.append(args[i]);
			s.append(" ");
		}
		String msg = s.toString();
		
		NickNameResult result = NickNameResult.sendQuery(target);
		
		if(result == null)
		{
			player.sendMessage(new TextComponent("§c해당 플레이어는 존재하지 않습니다."));
			return;
		}
		
		UUID playeruuid = result.getUuid();

		if(playeruuid != null)
		{
			if(playeruuid.equals(player.getUniqueId()))
			{
				player.sendMessage(new TextComponent("§c자신에게는 메세지를 보낼수 없습니다."));
				return;
			}
			this.sendWhisperMessage(player.getUniqueId(),playeruuid, msg);
		}
		else
		{
			player.sendMessage(new TextComponent("§c해당 플레이어는 존재하지 않습니다."));
		}
		
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
