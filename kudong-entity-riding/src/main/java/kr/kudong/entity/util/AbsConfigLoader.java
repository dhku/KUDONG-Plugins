package kr.kudong.entity.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public abstract class AbsConfigLoader
{
	private static final String CONFIG_SUFFIX = "config.yml";
	private Logger logger;
	private Yaml yaml;
	private Map<String, ConfigurationMember> modules;
	private Map<String, Integer> checksums;
	private File configRootDir;

	protected AbsConfigLoader()
	{
		
	}
	
	protected void init(File file, Logger logger)
	{
		this.logger = logger;
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		this.modules = new HashMap<>();
		this.checksums = new HashMap<>();
		
		this.yaml = new Yaml(options);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdirs();
		}
		this.configRootDir = file;
	}

	public synchronized void registerModule(String key, ConfigurationMember module)
	{
		this.modules.put(key, module);
	}
	
	private File createOrGetFile(String moduleName)
	{
		File configFile = new File(this.configRootDir, moduleName+"_"+CONFIG_SUFFIX);
		if(!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
				this.logger.log(Level.SEVERE, "[config] 파일 생성 실패", e);
				return null;
			}
		}
		return configFile;
	}
	
	private void saveConfigTask(String moduleName, Map<String, Object> data)
	{
		File configFile = this.createOrGetFile(moduleName);
		if(configFile == null) return;
		int newChecksum = this.yaml.dump(data).hashCode();
		int checksum = this.checksums.getOrDefault(moduleName, 0);
		if(newChecksum == checksum)
		{
			this.logger.log(Level.INFO, moduleName+".yml 내부 모듈에서 변경사항이 발견되지 않았으므로 저장하지 않습니다.");
			return;
		}
		FileWriter fileWriter = null;
		try
		{
			this.logger.log(Level.INFO, moduleName+".yml 저장...");
			fileWriter = new FileWriter(configFile);
			this.yaml.dump(data, fileWriter);
			
			this.checksums.put(moduleName, newChecksum);
		}
		catch (Exception e)
		{
			this.logger.log(Level.SEVERE, "config 저장중 에러", e);
		}
		finally
		{
			if(fileWriter != null)
			{
				try
				{
					fileWriter.close();
				}
				catch(IOException e)
				{
					this.logger.log(Level.SEVERE, "config 저장중 에러(닫기 실패)", e);
				}
			}
		}
	}

	public synchronized void saveConfig()
	{
		for (Entry<String, ConfigurationMember> e : this.modules.entrySet())
		{
			this.saveConfigTask(e.getKey(), e.getValue().getModuleConfig());
		}
		
	}
	
	private void loadConfigTask(String moduleName)
	{
		this.logger.log(Level.INFO, "" + moduleName + ".yml 로드중...");
		File configFile = this.createOrGetFile(moduleName);
		if(configFile == null) return;
		ConfigurationMember module = this.modules.get(moduleName);
		FileInputStream fInputStream=null;
		try
		{
			fInputStream = new FileInputStream(configFile);
			Map<String, Object> section = this.yaml.load(fInputStream);
			if(section == null || section.isEmpty())
			{
				section = new HashMap<>();
				this.saveConfigTask(moduleName, module.getModuleConfig());
			}
			else
			{
				int checksum = this.yaml.dump(section).hashCode();
				this.checksums.put(moduleName, checksum);
				if(!module.installConfig(section))
				{
					this.logger.log(Level.WARNING, moduleName + ".yml 로드중 오류 반환, 해당모듈 config 초기화 바랍니다.");
				}
			}
			
		}
		catch (Exception e)
		{
			this.logger.log(Level.SEVERE, moduleName + ".yml 로드 실패", e);
		}
		finally
		{
			if(fInputStream != null)
			{
				try
				{
					fInputStream.close();
				}
				catch(IOException e)
				{
					this.logger.log(Level.SEVERE, moduleName + ".yml 닫기 실패", e);
				}
			}
			
		}
	}

	public synchronized void loadConfig()
	{
		this.logger.info("loading config...");
		for (Entry<String, ConfigurationMember> e : this.modules.entrySet())
		{
			this.loadConfigTask(e.getKey());
		}
	}
}
