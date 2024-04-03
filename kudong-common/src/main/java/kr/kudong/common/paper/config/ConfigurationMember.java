package kr.kudong.common.paper.config;

import java.util.Map;

public interface ConfigurationMember
{
	public boolean installConfig(Map<String, Object> config);
	public Map<String, Object> getModuleConfig();
}
