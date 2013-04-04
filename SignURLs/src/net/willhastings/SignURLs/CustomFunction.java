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
		String line[] = sign.getLines();
		
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
			if(URL.toLowerCase().contains("http://"))
			{
				link.put(lineText, URL);
				if(newLink) SignURLs.addLink(lineText, URL);
			}
			else
			{
				link.put(lineText, "http://" + URL);
				if(newLink) SignURLs.addLink(lineText, "http://" + URL);
			}
			list.add(lineText);
				
			//if(newLink) System.out.println("|--------------------- NEW LINK --------------------|");
			/*System.out.println("|---------------------------------------------------|");
			System.out.println("[SignURLs] " + lineText + "|" + URL + " Has been added!");
			System.out.println("|---------------------------------------------------|");*/
			return true;
		}
		return false;
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
		return SignURLs.removeLink(lineText);
	}

	public static boolean changeLink(String lineText, String newURL) 
	{
		link.remove(lineText);
		if(newURL.toLowerCase().contains("http://"))
		{
			link.put(lineText, newURL);
			return SignURLs.updateLink(lineText, newURL);
		}
		else
		{
			link.put(lineText, "http://" + newURL);
			return SignURLs.updateLink(lineText, "http://" + newURL);
		}
	}
	
	public static boolean hasPermission(Player player, String perm)
	{
		if(SignURLs.permission != null) return SignURLs.permission.has(player, perm);
		else return player.hasPermission(perm);
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

}
