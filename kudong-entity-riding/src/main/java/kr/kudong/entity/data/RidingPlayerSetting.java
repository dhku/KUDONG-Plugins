package kr.kudong.entity.data;

import lombok.Data;

@Data
public class RidingPlayerSetting
{
	private boolean isPositionVisible = false;
	private boolean isCasualMode = false;
	private boolean isRegisteredDB = false;
	public RidingPlayerSetting() {}
}
