package kr.kudong.framework.bungee.db;

public class SQLSchema
{
	/**
	 * DB 테이블
	 */
	public static final String NickNameTable =
    		"CREATE TABLE IF NOT EXISTS player_nickname_data\r\n" + 
            "(   playeruuid CHAR(100),\r\n" +  
            "    originalname CHAR(100),\r\n" +
            "    nickname CHAR(100),\r\n" +
            "    alias CHAR(100),\r\n" +
            "    PRIMARY KEY (playeruuid) USING BTREE\r\n" + 
            ") DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
	
	public static final String NickNameTable_Select_Player = "SELECT * FROM player_nickname_data where playeruuid = ?;";
	public static final String NickNameTable_Select_UUID = "SELECT * FROM player_nickname_data where nickname = ? OR originalname = ?;";
}
