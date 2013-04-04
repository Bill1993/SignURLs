package net.willhastings.SignURLs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class SignURLs extends JavaPlugin
{
	private static SignURLs plugin;
	private static Logger log = Logger.getLogger("Minecraft");
	
	public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "SignURLs" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
	public static SignLisener signlisener = null;
	
	public static final String driver = "org.sqlite.JDBC";
	public static Connection con;
	public static Driver d;
	
	public static Permission permission = null;
	
	public void onEnable()
	{
		plugin = this;
		signlisener = new SignLisener(this, log);
		ResultSet res;
		
		if(getServer().getPluginManager().getPlugin("Vault") != null) setupPermissions();
		
		getCommand("signurls").setExecutor(new MainCommand());
		
		try 
		{
			loadSQLDriver();
		} catch (Exception e) {
			System.out.println("Error loading database driver: " + e.toString());
		}
		
		try {
			startSQLConnection();
		} catch (Exception e) {
			System.out.println("Error creating connection: " + e.toString());
		}
		
		try {
			 Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
			 
			 res = QueryRes("SELECT * FROM `database`");

			 String lineText, URL;
			 while(res.next())
			 {
				 lineText = res.getString("signurl");
				 URL = res.getString("url");
				 CustomFunction.addLink(lineText, URL);
			 }
		} catch (Exception e) {
			System.out.println("Error creating or runnint statement: " + e.toString());
		}
		
		CustomFunction.addLink("Author Site", "http://www.willhastings.net", true);
		
		//Metrics-Lite
		try 
		{
		    MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (Exception e) {
		    System.out.println("Failed to submit stats! Stats will not be sent :(");
		}
		
		log.info("[SignURLs] Has been Loaded!");
	}

	public void onDisable()
	{
		try {
			con.close();
		} catch (Exception e) {
			System.out.println("Error while closing connection: " + e.toString());
		}
	}
	
	private static Boolean Query(String sql) throws SQLException 
	{
		Statement stmt = con.createStatement();
		return stmt.execute(sql);
	}

	private static ResultSet QueryRes(String sql) throws SQLException 
	{
		Statement stmt = con.createStatement();
		ResultSet temp = stmt.executeQuery(sql);
		return temp;
	}
	
	private void startSQLConnection() throws SQLException, IOException 
	{
		File dirCheck = new File(this.getDataFolder().getPath());
		if(!dirCheck.exists()) dirCheck.mkdirs();
		String url = "jdbc:sqlite:" + this.getDataFolder().getPath() + File.separator + "links.sqlite";
		con = DriverManager.getConnection(url);
	}
	
	private void loadSQLDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException 
	{
		d = (Driver)Class.forName(driver).newInstance();
		DriverManager.registerDriver(d);
	}
	
	public static SignURLs getPlugin()
	{
		return plugin;
	}

	public static boolean addLink(String lineText, String uRL) 
	{
		try 
		{
			Query("INSERT INTO `database` (signurl, url) VALUES ('" + lineText + "', '" + uRL + "')");
		} 
		catch (Exception e) 
		{
			System.out.println("Error creating or runnint statement: " + e.toString());
			return false;
		}
		return true;
	}
	
	public static boolean updateLink(String lineText, String uRL)
	{
		try 
		{
			Query("UPDATE `database` SET url='" + uRL + "' WHERE signurl='" + lineText + "'");
		} 
		catch (Exception e) 
		{
			System.out.println("Error updating or runnint statement: " + e.toString());
			return false;
		}
		return true;
	}
	
	public static boolean removeLink(String lineText)
	{
		try 
		{
			Query("DELETE FROM `database` WHERE signurl='" + lineText + "'");
		} 
		catch (Exception e) 
		{
			System.out.println("Error deleting or runnint statement: " + e.toString());
			return false;
		}
		return true;
	}

	public static boolean loadlinkDB() 
	{
		ResultSet res;
		try {
			 Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
			 
			 res = QueryRes("SELECT * FROM `database`");

			 String lineText, URL;
			 while(res.next())
			 {
				 lineText = res.getString("signurl");
				 URL = res.getString("url");
				 CustomFunction.addLink(lineText, URL);
			 }
		} catch (Exception e) {
			System.out.println("Error creating or runnint statement: " + e.toString());
			return false;
		}
		return true;
	}
	
	public static boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) 
        {
            permission = permissionProvider.getProvider();
            log.info(SignURLs.PREFIX + "Has found that you have Vault installed and has switched over to it to handle permissions!");
        }
        return (permission != null);
    }
}
