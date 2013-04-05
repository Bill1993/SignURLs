package net.willhastings.SignURLs;

import java.sql.ResultSet;
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
	
	public static Permission permission = null;
	
	private static SQLite db;
	
	public void onEnable()
	{
		plugin = this;
		signlisener = new SignLisener(this, log);
		ResultSet res;
		
		if(getServer().getPluginManager().getPlugin("Vault") != null) setupPermissions();
		
		getCommand("signurls").setExecutor(new MainCommand());
		
		db = new SQLite(this.getDataFolder().getPath(), "links");
		
		try {
			 db.Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
			 
			 res = db.QueryRes("SELECT * FROM `database`");

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

	}
	
	public static SignURLs getPlugin()
	{
		return plugin;
	}

	public static boolean addLink(String lineText, String uRL) 
	{
		try 
		{
			db.Query("INSERT INTO `database` (signurl, url) VALUES ('" + lineText + "', '" + uRL + "')");
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
			db.Query("UPDATE `database` SET url='" + uRL + "' WHERE signurl='" + lineText + "'");
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
			db.Query("DELETE FROM `database` WHERE signurl='" + lineText + "'");
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
			db.Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
			 
			 res = db.QueryRes("SELECT * FROM `database`");

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
