package kr.kudong.framework.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

public class ReflectionService
{
	private final Logger logger;
	private final Server server;
	private final JavaPlugin plugin;
	private static String CRAFTBUKKIT_PACKAGE;
	private static String NMS_PACKAGE;
	private static String version;

	public ReflectionService(Logger logger, JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.server = plugin.getServer();
		CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
		NMS_PACKAGE = "net.minecraft.server.";
		version = this.server.getClass().getName().replace(".", ",").split(",")[3];
	}

	public static Class<?> getCraftClass(String name)
	{
		try
		{
			return Class.forName(CRAFTBUKKIT_PACKAGE + version+"."+name);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getNMSClass(String name)
	{
		try
		{
			return Class.forName(NMS_PACKAGE + version+"."+name);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void setGameProfile(Player player, String changeName)
	{
		try
		{
			Class<?> clazz = getCraftClass("entity.CraftPlayer");
			Object craftPlayer = clazz.cast(player);
			Method method = clazz.getMethod("getProfile", new Class[0]);

			Object gameProfile = method.invoke(craftPlayer, new Object[0]);
			Field field = gameProfile.getClass().getDeclaredField("name");
			
			field.setAccessible(true);
			field.set(gameProfile, changeName);
		}
		catch(NoSuchMethodException|java.lang.reflect.InvocationTargetException|IllegalAccessException
				|NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

}
