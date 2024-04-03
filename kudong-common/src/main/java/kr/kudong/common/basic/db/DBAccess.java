package kr.kudong.common.basic.db;

import org.bukkit.Bukkit;

import kr.kudong.common.paper.config.ConfigurationMember;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBAccess implements ConfigurationMember
{
	private final Runnable dbCheckTaskFunc;
	private final Logger logger;
	private Connection connection = null;
	private boolean isRun = false;
	private LinkedHashMap<String, PreparedStatement> statements;
	private Queue<QueryData> offlinedbTaskQueue;
	private ScheduledExecutorService exeService;

	private ScheduledFuture<?> dbCheckTask;
	private String host, database, username, password;
	private int port, dbCheckInterval, timeout, maxChacheSize;
	private boolean online;
	private boolean showReconnectMsg;

	public DBAccess(Logger logger) //기본값
	{
		this.dbCheckTaskFunc = this::dbCheckTask;
		this.logger = logger;
		this.host = "localhost";
		this.port = 3306;
		this.database = "database";
		this.username = "username";
		this.password = "password";
		this.dbCheckInterval = 10000;
		this.timeout = 3000;
		this.maxChacheSize = 100;
		this.online = false;

		this.offlinedbTaskQueue = new LinkedList<QueryData>();
		this.statements = new LinkedHashMap<String, PreparedStatement>((int) (this.maxChacheSize * 1.25), 0.9f, true);
		this.showReconnectMsg = true;
	}
	

	public PreparedStatement findStmt(final String sql)
	{
		PreparedStatement ps = this.statements.get(sql);

		if(ps == null)
		{
			try
			{
				ps = this.connection.prepareStatement(sql);
			}
			catch(SQLException e)
			{
				this.logger.log(Level.WARNING, "statement 오류: "+e.getSQLState(), e);
				ps = null;
			}
			catch(Exception e)
			{
				ps = null;
			}
			if(ps != null)
			{
				boolean valid = true;
				
				if(this.statements.size() >= this.maxChacheSize)
				{
					String rmkey = this.statements.keySet().iterator().next();
					try
					{
						this.statements.get(rmkey).close();
					}
					catch(SQLException e)
					{
						this.logger.log(Level.WARNING, "close 오류: "+e.getSQLState(), e);
						valid = false;
					}

					this.statements.remove(rmkey);
				}
				
				this.statements.put(sql, ps);
				
				if (!valid) {
					ps = null;
				}
			}
		}

		return ps;
	}
	

	private void syncTask()
	{
		if(!this.online)
		{
			synchronized (this)
			{
				try
				{
					this.wait();
				}
				catch(InterruptedException e)
				{
					this.logger.log(Level.SEVERE, "동기화 실패", e);
				}
			}
		}
	}


	public PreparedStatement query(final String sql)
	{
		this.syncTask();
		
		return this.findStmt(sql);
	}
	

	public void asyncQuery(final Function<PreparedStatement, Boolean> callback, final String sql)
	{
		this.exeService.execute(() ->
		{
			if (!this.findStmtAndExeSql(callback, sql))
			{
				this.logger.log(Level.WARNING, "stmt유효하지 않음, 실행 오류: " + sql);
				this.offlinedbTaskQueue.add(new QueryData(callback, sql));
			}
		});
	}
	
	

	private synchronized boolean findStmtAndExeSql(Function<PreparedStatement, Boolean> callback, String sql)
	{
		PreparedStatement ps = this.findStmt(sql);
		if(ps != null)
		{
			boolean result = false;
			try
			{
				result = callback.apply(ps);
			}
			catch(Exception e)
			{
				this.logger.log(Level.WARNING, "쿼리 수행중 처리되지 않은 오류", e);
			}

			if(result)
			{
				return true;
			}
		}
		this.logger.log(Level.WARNING, "쿼리 수행중 callback에서 false 반환");
		return false;
	}
	
	

	public synchronized void start()
	{
		if (this.isRun)
			return;
		this.logger.log(Level.INFO, "DB 모듈 시작");
		this.exeService = Executors.newSingleThreadScheduledExecutor();
		this.dbCheckTask = this.exeService.scheduleWithFixedDelay(this.dbCheckTaskFunc, 0, this.dbCheckInterval,
				TimeUnit.MILLISECONDS);

		this.isRun = true;

		this.syncTask();
		this.logger.log(Level.INFO, "DB 모듈 시작 완료");
	}


	public synchronized void stop()
	{
		this.logger.log(Level.INFO, "DB 연결 정지");

		this.exeService.execute(() ->
		{
			if (!this.isRun)
				return;

			this.dbCheckTask.cancel(false);
			this.isRun = false;

			QueryData data;
			while ((data = this.offlinedbTaskQueue.poll()) != null)
			{
				this.logger.log(Level.WARNING, "실행하지 못한 쿼리가 있습니다: " + data.sql);
			}
			this.exeService.shutdown();
			this.exeService = null;
			if (this.connection != null)
			{
				try
				{
					this.statements.clear();
					this.connection.close();
				}
				catch (Exception e)
				{
					logger.log(Level.SEVERE, "에러: ", e);
				}
			}
		});
		
		if(this.exeService != null)
		{
			try
			{
				this.exeService.awaitTermination(this.timeout, TimeUnit.MILLISECONDS);
			}
			catch(InterruptedException e)
			{
				this.logger.log(Level.INFO, "DB 연결 정지 오류", e);
			}
			
			this.logger.log(Level.INFO, "DB 연결 정지 완료");
		}
	}

	private void dbCheckTask()
	{
		// Random r = new Random();
		// int uid = r.nextInt(10000);
		boolean isConnect = false;
		if (this.connection != null)
		{
			try
			{
				// long time = System.nanoTime();
				isConnect = this.connection.isValid(this.timeout);
				// long intv = System.nanoTime() - time;
				// this.logger.log(Level.INFO, String.format("%f", ((double)intv / 1000000)));

			}
			catch (SQLException e)
			{
				this.logger.log(Level.SEVERE, "DB 확인중 오류!", e);
			}
		}
		if (!isConnect)
		{
			this.online = false;
			if (this.connect())
			{
				this.online = true;
				synchronized(this)
				{
					this.notifyAll();
				}
				
			}
			else
			{
				if(this.showReconnectMsg)
				{
					this.logger.log(Level.SEVERE, "DB 연결 실패.. " + this.dbCheckInterval + "ms 후에 재시도 합니다. "
							+ this.offlinedbTaskQueue.size() + "개 작업 큐잉.");
				}
			}

		}
		if (this.online && !this.offlinedbTaskQueue.isEmpty())
		{
			QueryData data;
			while ((data = this.offlinedbTaskQueue.poll()) != null)
			{
				boolean result = this.findStmtAndExeSql(data.callback, data.sql);
				this.logger.log(Level.INFO, "실패한 작업 재시도: " + data.sql + " 결과:" + result);

			}
		}
	}

	private boolean connect()
	{

		if (this.connection != null)
		{
			try
			{
				this.statements.clear();
				this.connection.close();

			}
			catch (SQLException e)
			{
				this.logger.log(Level.SEVERE, "에러: ", e);
			}
		}

		Connection conn = null;
		boolean result = false;
		String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?characterEncoding=UTF-8";
		if(this.showReconnectMsg)
		{
			this.logger.log(Level.INFO, "DB연결 시도중...." + url);
		}
		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(url, this.username, this.password);

			this.logger.log(Level.INFO, "연결 성공: " + url);
			this.showReconnectMsg = true;
			this.connection = conn;
			result = true;
		}
		catch (Exception e)
		{
			if(this.showReconnectMsg)
			{
				this.logger.log(Level.SEVERE, "DB 연결중 오류: " + url + e.getMessage());
				this.logger.log(Level.SEVERE, "데이터베이스에 연결될 때까지 "+this.dbCheckInterval+"ms 마다 재시도 합니다. 재접속 시도 로그는 보이지 않습니다.");
				this.showReconnectMsg = false;
			}
		}
		return result;
	}


	public boolean simpleExecute(final String executequery)
	{
		this.syncTask();
		PreparedStatement stmt = this.query(executequery);

		try
		{
			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			this.logger.log(Level.WARNING, "table 생성 실패", e);
			return false;
		}

		this.logger.log(Level.INFO, "table 생성 확인."+executequery);
		return true;
	}


	public void simpleAsyncExecute(final String executequery)
	{
		this.asyncQuery(stmt ->
		{
			try
			{
				stmt.executeUpdate();
			}
			catch (SQLException e)
			{
				Bukkit.getLogger().log(Level.WARNING, "table 생성 실패", e);
				return false;
			}
			Bukkit.getLogger().log(Level.INFO, "table 생성 확인." + executequery);
			return true;
		}, executequery);
	}
	

	public void simpleAsyncUpdate(String updatequery, final String key, final String column, final Object value)
	{
		final String columnNameSetQuery = updatequery.replace("$columnName", column);
		this.asyncQuery(stmt ->
		{
			try
			{
				stmt.setObject(1, value);
				stmt.setString(2, key);

				stmt.executeUpdate();
			}
			catch (SQLException e)
			{
				this.logger.log(Level.WARNING,
						"업데이트 실패:" + updatequery + " " + key + " " + columnNameSetQuery + " " + value, e);
				return false;
			}
			return true;
		}, columnNameSetQuery);
	}
	

	public boolean simpleUpdate(String updatequery, final String key, final String column, final Object value)
	{
		this.syncTask();
		final String columnNameSetQuery = updatequery.replace("$columnName", column);

		PreparedStatement stmt = this.query(columnNameSetQuery);

		try
		{
			stmt.setObject(1, value);
			stmt.setString(2, key);

			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			this.logger.log(Level.WARNING, "업데이트 실패:"+updatequery+" "+key+" "+columnNameSetQuery+" "+value, e);
			return false;
		}
		
		return true;
	}
	
	public void simpleAsyncDelete(final String deletequery, final String key)
	{
		this.asyncQuery(stmt ->
		{
			try
			{
				stmt.setString(1, key);
				stmt.executeUpdate();
			}
			catch (SQLException e)
			{
				this.logger.log(Level.WARNING, "삭제 실패:" + deletequery + " " + key, e);
				return false;
			}
			return true;
		}, deletequery);
	}

	public boolean simpleDelete(final String deletequery, final String key)
	{
		this.syncTask();
		PreparedStatement stmt = this.query(deletequery);

		try
		{
			stmt.setString(1, key);
			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			this.logger.log(Level.WARNING, "삭제 실패:"+deletequery+" "+key, e);
			return false;
		}
		
		return true;
	}

	public Connection getConnection()
	{
		return connection;
	}

	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		try
		{
			this.host = config.getOrDefault("host", this.host).toString();
			this.port = Integer.parseInt(config.getOrDefault("port", this.port).toString());
			this.database = config.getOrDefault("database", this.database).toString();
			this.username = config.getOrDefault("username", this.username).toString();
			this.password = config.getOrDefault("password", this.password).toString();
			this.dbCheckInterval = Integer.parseInt(config.getOrDefault("dbCheckInterval", this.dbCheckInterval).toString());
			this.timeout = Integer.parseInt(config.getOrDefault("timeout", this.timeout).toString());
			this.maxChacheSize = Integer.parseInt(config.getOrDefault("maxChacheSize", this.maxChacheSize).toString());
		}
		catch(Exception e)
		{
			this.logger.log(Level.WARNING, "db config 로드 실패", e);
			return false;
		}
		
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("host", this.host);
		map.put("port", this.port);
		map.put("database", this.database);
		map.put("username", this.username);
		map.put("password", this.password);
		map.put("dbCheckInterval", this.dbCheckInterval);
		map.put("timeout", this.timeout);
		map.put("maxChacheSize", this.maxChacheSize);
		return map;
	}
}

class QueryData
{
	Function<PreparedStatement, Boolean> callback;
	final String sql;

	QueryData(Function<PreparedStatement, Boolean> callback, String sql)
	{
		this.callback = callback;
		this.sql = sql;
	}
}
