package kr.kudong.framework.db;

public class SQLSchema
{
	/**
	 * DB 테이블
	 */
	public static final String FrameworkPlayerTable =
    		"CREATE TABLE IF NOT EXISTS framework_player_data\r\n" + 
            "(   playeruuid CHAR(100),\r\n" +  
            "    username CHAR(100),\r\n" +
            "    PRIMARY KEY (playeruuid) USING BTREE\r\n" + 
            ") DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
	
	public static final String FrameworkPlayerTable_Select = "SELECT * FROM framework_player_data;";
	public static final String FrameworkPlayerTable_Select_Player = "SELECT username FROM framework_player_data where playeruuid = ?;";
	public static final String FrameworkPlayerTable_Select_Player_UUID = "SELECT playeruuid FROM framework_player_data where username = ?;";
	public static final String FrameworkPlayerTable_Insert ="INSERT INTO framework_player_data VALUES(?,?);";
	public static final String FrameworkPlayerTable_Delete ="DELETE FROM framework_player_data WHERE playeruuid=?";
	public static final String FrameworkPlayerTable_Update ="UPDATE framework_player_data SET username = ? WHERE playeruuid=?;";
}
