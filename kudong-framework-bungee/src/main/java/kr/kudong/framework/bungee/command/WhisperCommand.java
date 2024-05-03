package kr.kudong.framework.bungee.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.bungee.db.NickNameQuery;
import kr.kudong.framework.bungee.db.SQLSchema;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class WhisperCommand extends Command
{
	private final Logger logger;
	private final DBAccess dbAccess;
	
	public WhisperCommand(Logger logger,DBAccess dbAccess)
	{
		super("ws","귓","귓속말","rnlt");
		this.logger = logger;
		this.dbAccess = dbAccess;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (!(sender instanceof ProxiedPlayer))
			return;
		
		ProxiedPlayer me = (ProxiedPlayer)sender;

		if(args.length > 1)
		{
			String target = args[0];
			NickNameQuery q = NickNameQuery.getQuery(target,target);
			NickNameQuery q2 = NickNameQuery.getQuery(me.getName(),me.getName());
			
			if(q != null)
			{
				ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(q.getUuid());
				
				if(targetPlayer != null)
				{
					StringBuilder s = new StringBuilder();
					for(int i=1; i < args.length; i++)
					{
						s.append(args[i]);
						s.append(" ");
					}
					String msg = s.toString();
					
					me.sendMessage(new TextComponent("§6[§c나 §6-> §b"+q.getDisplayName()+"§6]§f" + msg));
					targetPlayer.sendMessage(new TextComponent("§6[§b"+q2.getDisplayName()+" §6-> §c나§6]§f" + msg));
				}
				else
				{
					me.sendMessage(new TextComponent("§c해당 플레이어는 존재하지 않습니다."));
				}
			}
			else
			{
				me.sendMessage(new TextComponent("§c해당 플레이어는 존재하지 않습니다."));
			}
		}
		else
		{
			me.sendMessage(new TextComponent("§e/귓 <플레이어> <할말> §f: 플레이어에게 귓속말을 보냅니다."));
		}
	}
}
