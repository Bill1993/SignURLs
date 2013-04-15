package net.willhastings.SignURLs.util;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import net.willhastings.SignURLs.SignURLs;

public class SQLite 
{
	private static final String driver = "org.sqlite.JDBC";
	private static Connection con;
	private static Driver d;
	private String url;
	
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
		try 
		{
			con.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Error while closing connection: " + e.toString());
		}
	}
	
	public Boolean Query(String sql)
	{
		Statement stmt;
		try 
		{
			stmt = con.createStatement();
			return stmt.execute(sql);
		} 
		catch (Exception e) 
		{
			System.out.println("Error running statement: " + e.toString());
			return false;
		}
		
	}
	
	public ResultSet QueryRes(String sql)
	{
		try
		{
			Statement stmt = con.createStatement();
			ResultSet temp = stmt.executeQuery(sql);
			return temp;
		} 
		catch (Exception e) 
		{
			System.out.println("Error running statement: " + e.toString());
			return null;
		}
	}
	
	private void loadDriver()
	{
		try 
		{
			d = (Driver)Class.forName(driver).newInstance();
			DriverManager.registerDriver(d);
		} 
		catch (Exception e) 
		{
			System.out.println("Error loading database driver: " + e.toString());
		}
		
	}
	
	private void openConnection()
	{
		try 
		{
			con = DriverManager.getConnection(url);
		} 
		catch (Exception e) 
		{
			System.out.println("Error creating connection: " + e.toString());
		}
	}

}
