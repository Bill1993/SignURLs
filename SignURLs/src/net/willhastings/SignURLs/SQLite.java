package net.willhastings.SignURLs;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite 
{
	public static final String driver = "org.sqlite.JDBC";
	public static Connection con;
	public static Driver d;
	String url;
	
	public SQLite(String dir, String dbName)
	{
		loadDriver();
		
		File dirCheck = new File(SignURLs.getPlugin().getDataFolder().getPath());
		if(!dirCheck.exists()) dirCheck.mkdirs();
		url = "jdbc:sqlite:" + dir + File.separator + dbName + ".sqlite";
		
		openConnection();
	}
	
	public void closeCon()
	{
		try {
			con.close();
		} catch (Exception e) {
			System.out.println("Error while closing connection: " + e.toString());
		}
	}
	
	public Boolean Query(String sql) throws SQLException 
	{
		Statement stmt = con.createStatement();
		return stmt.execute(sql);
	}
	
	public ResultSet QueryRes(String sql) throws SQLException 
	{
		Statement stmt = con.createStatement();
		ResultSet temp = stmt.executeQuery(sql);
		return temp;
	}
	
	private void loadDriver()
	{
		try 
		{
			d = (Driver)Class.forName(driver).newInstance();
			DriverManager.registerDriver(d);
		} catch (Exception e) {
			System.out.println("Error loading database driver: " + e.toString());
		}
		
	}
	
	private void openConnection()
	{
		try 
		{
			con = DriverManager.getConnection(url);
		} catch (Exception e) {
			System.out.println("Error creating connection: " + e.toString());
		}
	}

}
