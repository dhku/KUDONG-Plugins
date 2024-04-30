package kr.kudong.framework.scoreboard;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import kr.kudong.framework.FrameworkCore;
import me.clip.placeholderapi.PlaceholderAPI;

public class TownyScoreboard
{
	public static Map<UUID,Boolean> map = new HashMap<>();
	
	private static final String moneyPart_KEY = ChatColor.RED + "" + ChatColor.WHITE;
	private static final String townPart_KEY = ChatColor.BLACK + "" + ChatColor.WHITE;
	
	private static String moneyPart 	= "  §e[돈] §f%money%원";
	private static String townPart 	= "  §2[땅] §f%towny_ground%";
	
	private static DecimalFormat formatter = new DecimalFormat("###,###.##");

	public static void create(Player player)
	{
		double balance = FrameworkCore.econ.getBalance(player);
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		
		Objective obj = board.registerNewObjective("towny", "dummy", "Towny");
		obj.setDisplayName("§6[§9마인스퀘어§a]");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score s = obj.getScore("§2");
		s.setScore(4);

		Team moneyCounter = board.registerNewTeam("moneyCounter");
		moneyCounter.addEntry(moneyPart_KEY);
		moneyCounter.setPrefix(translateMoney(balance));
		obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(3);
		
		Team townCounter = board.registerNewTeam("townCounter");
		townCounter.addEntry(townPart_KEY);
		townCounter.setPrefix(translateTown(player));
		obj.getScore(townPart_KEY).setScore(2);

		s = obj.getScore("§3");
		s.setScore(1);

		player.setScoreboard(board);
	}
	
	public static void Update(Player player)
	{
		double balance = FrameworkCore.econ.getBalance(player);
		Scoreboard board = player.getScoreboard();

		Team t1 = board.getTeam("moneyCounter");
		if(t1 != null) t1.setPrefix(translateMoney(balance));
		
		Team t2 = board.getTeam("townCounter");
		if(t2 != null) t2.setPrefix(translateTown(player));
	}
	
	public static void Close(Player player)
	{
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
//	public static void create(Player player)
//	{
//		double balance = FrameworkCore.econ.getBalance(player);
//		ScoreboardManager manager = Bukkit.getScoreboardManager();
//		Scoreboard board = manager.getNewScoreboard();
//		
//		Objective obj = board.registerNewObjective("towny", "dummy", "Towny");
//		obj.setDisplayName("§6[§9마인스퀘어§a]");
//		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//		
//		Score s = obj.getScore("§2");
//		s.setScore(4);
//
//		s = obj.getScore(translateMoney(balance));
//		s.setScore(3);
//		
//		
//		s = obj.getScore(translateTown(player));
//		s.setScore(2);
//		
//		s = obj.getScore("§3");
//		s.setScore(1);
//
//		player.setScoreboard(board);
//	}
//	
//	public static void Update(Player player)
//	{
//		double balance = FrameworkCore.econ.getBalance(player);
//		Scoreboard board = player.getScoreboard();
//		Objective obj = board.getObjective("towny");
//		if(obj != null)
//		{
//			Score s = obj.getScore("§2");
//			
//			s.setScore(4);
//
//			s = obj.getScore(translateMoney(balance));
//			s.setScore(3);
//			
//			s = obj.getScore(translateTown(player));
//			s.setScore(2);
//			
//			s = obj.getScore("§3");
//			s.setScore(1);
//
//			player.setScoreboard(board);
//		}
//	}
	
	public static String translateMoney(double balance)
	{
		return moneyPart.replace("%money%", !isInteger(balance) ? formatter.format(balance) : formatter.format((int)balance));
	}
	
	public static String translateTown(Player player)
	{
		return PlaceholderAPI.setPlaceholders(player, townPart);
	}
	
	public static boolean isInteger(double num) {
	    return num % 1 == 0.0;
	}

}
