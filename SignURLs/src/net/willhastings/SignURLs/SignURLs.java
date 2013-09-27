package net.willhastings.SignURLs;

import java.sql.ResultSet;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.willhastings.SignURLs.util.Config;
import net.willhastings.SignURLs.util.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SignURLs extends JavaPlugin
{
	private static SignURLs plugin;
	private static Logger log = Logger.getLogger("Minecraft");
	
	public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "SignURLs" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
	public SignLisener signlisener = null;
	public static Permission permission = null;
	private static SQLite db;
	
	public void onEnable()
	{
		/* -------------- VARIABLE INITIALIZATION -------------- */
		plugin = this;
		
		/* -------------------- LISENERS ----------------------- */
		signlisener = new SignLisener(this, log);
		
		/* ------------------- PERMISSIONS --------------------- */
		if(this.getServer().getPluginManager().getPlugin("Vault") != null) setupPermissions();
		
		/* ------------------ CONFIGURATION -------------------- */		
		Config.loadConfig(this);
		
		/* -------------------- COMMANDS ----------------------- */
		getCommand("signurls").setExecutor(new MainCommand());
		
		/* -------------------- DATABASE ----------------------- */
		log.info("[SignURLs] attempting to open SQLite database.");
		db = new SQLite(this.getDataFolder().getPath(), "links");
		log.info("[SignURLs] attempting to load links from SQLite database.");
		loadlinkDB();
		CustomFunction.addLink("Author Site", "http://www.willhastings.net", true);
		
		log.info("[SignURLs] " + this.getDescription().getVersion() + " Has been Loaded!");
	}

	public void onDisable()
	{
		db.closeCon();
	}
	
	public static SignURLs getPlugin()
	{
		return plugin;
	}

	public static boolean addLink(String lineText, String uRL) 
	{
		return db.Query("INSERT INTO `database` (signurl, url) VALUES ('" + lineText + "', '" + uRL + "')");
	}
	
	public static boolean updateLink(String lineText, String uRL)
	{
		return db.Query("UPDATE `database` SET url='" + uRL + "' WHERE signurl='" + lineText + "'");
	}
	
	public static boolean removeLink(String lineText)
	{
		return db.Query("DELETE FROM `database` WHERE signurl='" + lineText + "'");
	}

	public static boolean loadlinkDB()
	{
		ResultSet res;
		String lineText, URL;
		int cnt = 0;
		
		db.Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
		res = db.QueryRes("SELECT * FROM `database`"); 
		try 
		{
			while(res.next())
			{
				lineText = res.getString("signurl");
				URL = res.getString("url");
				CustomFunction.addLink(lineText, URL);
				cnt++;
			}
			log.info("[SignURLs] " + cnt + " links have been loaded.");
		} 
		catch (Exception e) 
		{
			log.severe("Error executing ResultSet: " + e.toString());
		}
		return true;
	}
	
	public static boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) 
        {
        	permission = permissionProvider.getProvider();
            log.info("[SignURLs] Has found that you have [Vault] installed and will us it to handle permissions!");
        }
        return (permission != null);
	}

	public static boolean purgeDB() 
	{
		boolean a, b;
		a = db.Query("DROP TABLE `database`");
		b = db.Query("CREATE TABLE IF NOT EXISTS `database` (signurl varchar(20), url varchar(164))");
		CustomFunction.addLink("Author Site", "http://www.willhastings.net", true);
		if(a == b) return true;
			else return false;
	}
}
