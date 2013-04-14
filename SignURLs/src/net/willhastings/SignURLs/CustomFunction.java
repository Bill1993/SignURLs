package net.willhastings.SignURLs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class CustomFunction 
{
	private static HashMap<String, String> link = new HashMap<String, String>();
	private static ArrayList<String> list = new ArrayList<String>();
	public static final int PAGE_LINES = 9;
	
	public static int isURLSign(Sign sign) 
	{
		return findKey(sign.getLines());	
	}
	
	public static int findKey(String[] line) 
	{
		for(int i = 0; i < line.length; i++)
		{
			if(line[i].equalsIgnoreCase("[SignURLs]"))
			{
				return i;
			}
		}
		return -1;
	}
	
	public static boolean addLink(String lineText, String URL)
	{
		return addLink(lineText, URL, false);
	}
	
	public static boolean addLink(String lineText, String URL, boolean newLink)
	{
		if(lineText.length() > 15)
		{
			return false;
		}
		else if(!linkExists(lineText))
		{
			String temp = fixURL(URL);
			link.put(lineText, temp);
			if(newLink) SignURLs.addLink(lineText, temp);
			list.add(lineText);
			return true;
		}
		return false;
	}
	
	public static String fixURL(String URL)
	{
		String temp;
		if(URL.toLowerCase().startsWith("http://") || URL.toLowerCase().startsWith("https://")) temp = URL;
		else temp = "http://" + URL;
		return temp;
	}

	public static boolean linkExists(String lineText) 
	{
		return link.containsKey(lineText);
	}

	public static String fetchLink(String lineText) 
	{
		if(linkExists(lineText)) return link.get(lineText);
		return null;
	}

	public static boolean removeLink(String lineText) 
	{
		link.remove(lineText);
		list.remove(lineText);
		return SignURLs.removeLink(lineText);
	}

	public static boolean changeLink(String lineText, String newURL) 
	{
		String temp = fixURL(newURL);
		link.remove(lineText);
		link.put(lineText, temp);
		return SignURLs.updateLink(lineText, temp);
	}
	
	public static boolean hasPermission(Player player, String perm)
	{
		if (player.isOp()) return true;	
		else if(SignURLs.permission != null) 
		{
			if (SignURLs.permission.has(player, "*")) return true;
			else if (SignURLs.permission.has(player, perm)) return true;
		}
		else
		{
			if (player.hasPermission("*")) return true;
			else if (player.hasPermission(perm)) return true;
		}
		return false;
	}

	public static String[] getList(int pageNumber) 
	{
		String[] line = new String[PAGE_LINES];
		int temp = 0;
		if(pageNumber > 1) {temp = PAGE_LINES * (pageNumber - 1);}
		for(int i = 0; (i < PAGE_LINES); i++)
		{
			if(temp >= list.size()) break;
			line[i] = list.get(temp);
			temp++;
		}
		return line;
	}

	public static int getMaxListPages() 
	{
		return (list.size()/PAGE_LINES) + 1;
	}

	public static boolean reloadDB() 
	{
		link.clear();
		list.clear();	
		return SignURLs.loadlinkDB();
	}

	public static boolean purgeDB() 
	{
		link.clear();
		list.clear();	
		return SignURLs.purgeDB();
	}
}
