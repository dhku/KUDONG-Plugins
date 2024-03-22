package kr.kudong.entity.db;

public class SQLSchema
{
	/**
	 * DB 테이블
	 */
	public static final String RidingTable =
    		"CREATE TABLE IF NOT EXISTS entity_riding_data\r\n" + 
            "(   playeruuid CHAR(100),\r\n" +  
            "    presetname CHAR(100),\r\n" +
            "    PRIMARY KEY (playeruuid,presetname) USING BTREE\r\n" + 
            ") DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
	
	public static final String RidingTable_Select = "SELECT * FROM entity_riding_data;";
	public static final String RidingTable_Select_Player = "SELECT presetname FROM entity_riding_data where playeruuid = ?;";
	public static final String RidingTable_Insert ="INSERT INTO entity_riding_data VALUES(?,?);";
	public static final String RidingTable_Delete ="DELETE FROM entity_riding_data WHERE playeruuid=? AND presetname=?;";
	
	
	public static final String RidingPlayerTable =
    		"CREATE TABLE IF NOT EXISTS entity_riding_player_data\r\n" + 
            "(   playeruuid CHAR(100),\r\n" +  
            "    ispositionvisible CHAR(100),\r\n" +
            "    iscasualmode CHAR(100),\r\n" +
            "    PRIMARY KEY (playeruuid) USING BTREE\r\n" + 
            ") DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
	
	public static final String RidingPlayerTable_Select = "SELECT * FROM entity_riding_player_data;";
	public static final String RidingPlayerTable_Select_Player = "SELECT ispositionvisible,iscasualmode FROM entity_riding_player_data where playeruuid = ?;";
	public static final String RidingPlayerTable_Insert ="INSERT INTO entity_riding_player_data VALUES(?,?,?);";
	public static final String RidingPlayerTable_Delete ="DELETE FROM entity_riding_player_data WHERE playeruuid=?;";
	public static final String RidingPlayerTable_Update ="UPDATE entity_riding_player_data SET ispositionvisible = ?, iscasualmode = ? WHERE playeruuid=?;";
	
}
