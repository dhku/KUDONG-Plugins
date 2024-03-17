package kr.kudong.entity.util;

import java.util.Map;

public interface ConfigurationMember
{
	public boolean installConfig(Map<String, Object> config);
	public Map<String, Object> getModuleConfig();
}
