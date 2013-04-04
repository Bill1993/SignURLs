package net.willhastings.SignURLs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) 
	{		
		if(!(sender instanceof Player))
		{
			sender.sendMessage(SignURLs.PREFIX + " You can only access this command in-game!");
			return true;
		}
		 
		if(CustomFunction.hasPermission((Player) sender, "signurls.command")) {}
			else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
				else if (sender.isOp()) {}
					else return false;
		
		if(args.length < 1)
		{
			sender.sendMessage(SignURLs.PREFIX + " Invalid Sub-Command, Valid Sub-Commands are " + ChatColor.RED + "ADD" + ChatColor.WHITE + ", " + ChatColor.RED + "REMOVE" + ChatColor.WHITE + ", " 
					+ ChatColor.RED +"CHANGE" + ChatColor.WHITE + ChatColor.RED +"LIST" + ChatColor.WHITE +", and " + ChatColor.RED +"ADMIN");
			
			return true;
		}
		
		try 
		{
			switch(SubCommands.valueOf(args[0].toString().toUpperCase())) 
			{
				case ADD: 
				{ 
					if(CustomFunction.hasPermission((Player) sender, "signurls.command.add")) {}
						else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
							else if (sender.isOp()) {}
								else return false;
					
					if(args.length < 3)
					{
						sender.sendMessage(SignURLs.PREFIX + " Invalid Command Arguments: /SignUrls ADD {SignText} {URL}");
					}
					else
					{
						if(args[1].length() > 15)
						{
							sender.sendMessage(SignURLs.PREFIX + " SignText is too long. It can be no longer than 11 charectors!");
						}
						else
						{
							if(CustomFunction.linkExists(args[1]))
							{
								sender.sendMessage(SignURLs.PREFIX + " That text is already linked to:");
								sender.sendMessage(SignURLs.PREFIX + CustomFunction.fetchLink(args[1]));
								sender.sendMessage(SignURLs.PREFIX + " To change the pointer use /SignURLs CHANGE {SignText} {New URL}");
							}
							else
							{
								CustomFunction.addLink(args[1], args[2], true);
								sender.sendMessage(SignURLs.PREFIX + "'" + ChatColor.YELLOW + args[1] + ChatColor.WHITE + "' Has been linked to '" + ChatColor.YELLOW + args[2] + ChatColor.WHITE + "'!");
							}
						}
					}
					break;
				}
				case REMOVE:
				{ 
					if(CustomFunction.hasPermission((Player) sender, "signurls.command.remove")) {}
						else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
							else if (sender.isOp()) {}
								else return false;
					
					if(args.length < 2)
					{
						sender.sendMessage(SignURLs.PREFIX + " Invalid Command Arguments: /SignUrls REMOVE {SignText}");
					}
					else
					{
						if(CustomFunction.linkExists(args[1]))
						{
							if(CustomFunction.removeLink(args[1]))
							{
								sender.sendMessage(SignURLs.PREFIX + " '" + ChatColor.YELLOW + args[1] + ChatColor.WHITE + "' Has been removed from the DB!");
							}
							else
							{
								sender.sendMessage(SignURLs.PREFIX + " An unexpected error has occured, please check the logs!");
							}
						}
						else
						{
							sender.sendMessage(SignURLs.PREFIX + " '" + args[1] + "' Does not appear to be in the DB.");
						}
					}
					break;
				}
				case CHANGE:
				{
					if(CustomFunction.hasPermission((Player) sender, "signurls.command.change")) {}
						else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
							else if (sender.isOp()) {}
								else return false;
					
					if(args.length < 3)
					{
						sender.sendMessage(SignURLs.PREFIX + " Invalid Command Arguments: /SignUrls CHANGE {SignText} {NEW URL}");
					}
					else
					{
						if(CustomFunction.linkExists(args[1]))
						{
							if(CustomFunction.changeLink(args[1], args[2]))
							{
								sender.sendMessage(SignURLs.PREFIX + " '" + ChatColor.YELLOW + args[1] + ChatColor.WHITE + "' Will now direct to '" + ChatColor.YELLOW + args[2] + ChatColor.WHITE + "'");
							}
							else
							{
								sender.sendMessage(SignURLs.PREFIX + " An unexpected error has occured, please check the logs!");
							}
						}
						else
						{
							sender.sendMessage(SignURLs.PREFIX + " '"+ ChatColor.YELLOW + args[1] + ChatColor.WHITE + "' Does not appear to be in the DB.");
						}
					}
					break;
				}
				case LIST:
				{
					if(CustomFunction.hasPermission((Player) sender, "signurls.command.list")) {}
						else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
							else if (sender.isOp()) {}
								else return false;
					
					int pageNum, maxPages=CustomFunction.getMaxListPages();
					if(args.length < 2)
					{
						pageNum = 1;
					}
					else
					{
						try
						{
							pageNum = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage(SignURLs.PREFIX + " Please specify a page number [1-" + maxPages + "]");
							break;
						}
					}
					if(pageNum > maxPages)
					{
						sender.sendMessage(SignURLs.PREFIX + " Please specify a page number [1-" + maxPages + "]");
						break;
					}
					String[] list = CustomFunction.getList(pageNum);
					sender.sendMessage(SignURLs.PREFIX + ChatColor.DARK_GRAY + "|-------------- " + ChatColor.DARK_RED 
							+ "Page: " + ChatColor.WHITE + pageNum + ChatColor.DARK_RED + "/" + ChatColor.WHITE + maxPages 
							+ ChatColor.DARK_GRAY + " --------------|");
					for(String i : list)
					{
						if(i == null) break;
						sender.sendMessage(ChatColor.YELLOW + i + ChatColor.WHITE + " -> " + ChatColor.YELLOW + CustomFunction.fetchLink(i));
					}
					break;
				}
				case ADMIN:
				{
					if(CustomFunction.hasPermission((Player) sender, "signurls.command.admin")) {}
						else if (CustomFunction.hasPermission((Player) sender, "signurls.command.*")) {}
							else if (sender.isOp()) {}
								else return false;
					
					if(args.length < 3)
					{
						sender.sendMessage(SignURLs.PREFIX + "Invalid sub command usuage! /signurls ADMIN RELOAD DATABASE");
					}
					else if(args[1].equalsIgnoreCase("reload") && args[2].equalsIgnoreCase("database"))
					{
						if(CustomFunction.reloadDB()) sender.sendMessage(SignURLs.PREFIX + "Link DB has been Reloaded!");
							else sender.sendMessage(SignURLs.PREFIX + "LinkDB FAILED to Reload!");
					}
				}
			}
		}
		catch(IllegalArgumentException e) 
		{
			return false;
		}
		return true;
	}
	
	private enum SubCommands 
	{
		ADD,
		REMOVE,
		CHANGE,
		LIST,
		ADMIN
	}

}
