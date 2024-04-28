package kr.kudong.nickname.db;

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
	
	public static final String NickNameTable_Select = "SELECT * FROM player_nickname_data;";
	public static final String NickNameTable_Select_Player = "SELECT originalname,nickname,alias FROM player_nickname_data where playeruuid = ?;";
	public static final String NickNameTable_Insert_First_Join ="INSERT INTO player_nickname_data VALUES(?,?,NULL,NULL);";
	public static final String NickNameTable_Insert ="INSERT INTO player_nickname_data VALUES(?,?,?,?);";
	public static final String NickNameTable_Delete ="DELETE FROM player_nickname_data WHERE playeruuid=?";
	public static final String NickNameTable_Update ="UPDATE player_nickname_data SET nickname = ?, alias = ? WHERE playeruuid=?;";
	public static final String NickNameTable_Update_NickName ="UPDATE player_nickname_data SET nickname = ? WHERE playeruuid=?;";
	public static final String NickNameTable_Update_Alias ="UPDATE player_nickname_data SET alias = ? WHERE playeruuid=?;";

}
