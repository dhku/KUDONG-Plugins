package kr.kudong.common.basic.comm;

import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class MojangAPIUtil
{
	public static UUID getUUIDfromMojangAPI(String name)
	{
		Gson gson = new Gson();
		UUID uuid = null;
		String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
		try
		{
			String UUIDJson = IOUtils.toString(new URL(url), "UTF-8");
			Account a = gson.fromJson(UUIDJson, Account.class);
			
			if(a != null) return UUID.fromString(
				    a.getId()
				    .replaceFirst( 
				        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" 
				    )
				);
		}
		catch(Exception e){}
		return uuid;
	}
	
	public static void main(String[] args)
	{
		System.out.println(getUUIDfromMojangAPI("22342"));
	}
	
	class Account
	{
		private String id;
		private String name;
		
		public String getId()
		{
			return id;
		}
		public String getName()
		{
			return name;
		}
	}
}
