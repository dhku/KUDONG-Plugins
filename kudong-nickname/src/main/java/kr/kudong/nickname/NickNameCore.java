package kr.kudong.nickname;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.framework.FrameworkCore;
import kr.kudong.nickname.command.CommandManager;
import kr.kudong.nickname.controller.NickNameManager;
import kr.kudong.nickname.controller.NickNamePlayer;
import kr.kudong.nickname.db.SQLSchema;
import kr.kudong.nickname.listener.NickNameListener;

public class NickNameCore extends JavaPlugin
{
	private static JavaPlugin plugin;
	private Logger logger;
	private ConfigLoader configLoader;
	private PluginManager pluginManager;
	private NickNameManager nicknameManager;
	private NickNameListener listener;
	private CommandManager commandManager;
	private DBAccess dbAccess;

	@Override
	public void onEnable()
	{
		NickNameCore.plugin = this;

		/**
		 * 디펜던시 로드
		 */
		if(!this.setupFramework()) return;

		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);

		/**
		 * 컨트롤러
		 */
		this.nicknameManager = new NickNameManager(this.logger, this, this.dbAccess);
		this.commandManager = new CommandManager(this.logger, this, this.nicknameManager);
		this.listener = this.nicknameManager.getListener();

		this.registerEventListener();

		this.dbAccess.simpleAsyncExecute(SQLSchema.NickNameTable);
		this.loadPlayerData();
		
		this.logger.log(Level.INFO, "Kudong-NickName 플러그인이 성공적으로 활성화 되었습니다!");

	}

	private void loadPlayerData()
	{
		List<NickNamePlayer> list = this.nicknameManager.getService().selectNickNameAllPlayer();
		
		for(NickNamePlayer np : list)
		{
			UUID uuid = np.getUniqueID();
			this.nicknameManager.registerNickNamePlayer(uuid, np);
		}
		
		this.logger.log(Level.INFO, list.size()+"명의 플레이어 닉네임 정보가 로드 되었습니다.");
		
	}

	@Override
	public void onDisable()
	{
		this.nicknameManager.getMap().clear();
		this.logger.log(Level.INFO, "Kudong-NickName 플러그인이 성공적으로 비활성화 되었습니다!");
	}

	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(this.listener, this);
	}

	private boolean setupFramework()
	{
		if(getServer().getPluginManager().getPlugin("Kudong-framework") == null)
		{
			this.logger.log(Level.SEVERE, "Kudong-framework 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}

		Plugin core = Bukkit.getPluginManager().getPlugin("Kudong-framework");
		this.dbAccess = ((FrameworkCore)core).getDBAccess();
		return true;
	}

}
