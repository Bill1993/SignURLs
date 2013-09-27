package net.willhastings.SignURLs;

import java.util.logging.Logger;

import net.willhastings.SignURLs.util.Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class SignLisener implements Listener
{
	Plugin plugin = null;
	Logger log = null;
	
	public SignLisener(SignURLs signURLs, Logger logger) 
	{
		signURLs.getServer().getPluginManager().registerEvents(this, signURLs);
		plugin = signURLs;
		log = logger;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		Material inhand = event.getPlayer().getItemInHand().getType();
		
		if(block.getState() instanceof Sign && inhand == Material.GOLDEN_CARROT)
		{
			Sign sign = (Sign) block.getState();
			boolean isURLSign = CustomFunction.isURLSign(sign) > -1 ? true:false;
			
			if(!isURLSign) return;
			else
			{
				Player player = event.getPlayer();
				
				if(CustomFunction.hasPermission(player, "signurls.break"))
				{
					if(!(inhand == Material.GOLDEN_CARROT))
					{
						player.sendMessage(SignURLs.PREFIX + "You need to use a Golden Carrot to destroy [SignURLs] Signs.");
						event.setCancelled(true);
					}
				}
				else event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event)
	{	
		Block block = event.getBlock();
		if(block.getState() instanceof Sign)
		{
			int line = CustomFunction.findKey(event.getLines());
			
			if(line == -1) return;
			
			if(event.getLine(line).equalsIgnoreCase("["+ Config.SIGN_TEXT + "]")) event.setLine(line, "["+ Config.SIGN_TEXT_COLOR + Config.SIGN_TEXT + "§0]");
			
			if(CustomFunction.hasPermission(event.getPlayer(), "signurls.place")) {}
				else
				{
					event.getPlayer().sendMessage(SignURLs.PREFIX + "You can not place [SignURLs] signs!");
					block.breakNaturally();
					return;
				}
			
			if(line == 3)
			{
				event.getPlayer().sendMessage(SignURLs.PREFIX + "You can not place [SignURLs] on the buttom line!");
				block.breakNaturally();
				return;
			}
			else
			{
				line++;
				String lineText = event.getLine(line);
				if(!CustomFunction.linkExists(lineText))
				{
					event.getPlayer().sendMessage(SignURLs.PREFIX + "The link " + ChatColor.YELLOW 
							+ lineText + ChatColor.WHITE + " could not be found in the DB!");
					block.breakNaturally();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		
		if(CustomFunction.hasPermission(player, "signurls.use")) {}
			else if (player.isOp()) {}
				else return;
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block block = event.getClickedBlock();
			if(block.getState() instanceof Sign)
			{
				if(Config.PLAYER_COOLDOWN > 0)
				{
					Long curr = System.currentTimeMillis();
					Long currPlayer = CustomFunction.getPlayerPrevTime(player);
					if(currPlayer > -1)
					{
						Long diff = (curr - currPlayer);
						if(diff < Config.PLAYER_COOLDOWN)
						{
							Long toWait = (Config.PLAYER_COOLDOWN - diff);
							player.sendMessage(Config.CHAT_PREFIX + ChatColor.WHITE 
									+ " You need to wait " + ChatColor.GOLD + toWait 
									+ "ms" + ChatColor.WHITE + " before requesting another link!");
							event.setCancelled(true);
							return;
						}
					}
				}
				
				Sign sign = (Sign)block.getState();	
				int line = CustomFunction.isURLSign(sign);

				if(line == -1) return;
				else if(line == 3)
				{
					event.getPlayer().sendMessage(SignURLs.PREFIX + "You can not place [" + Config.SIGN_TEXT + "] on the buttom line!");
					block.breakNaturally();
				}
				else
				{		
					line++;
					String lineText = sign.getLine(line);
					if(CustomFunction.linkExists(lineText))
					{
						String URL = CustomFunction.fetchLink(lineText);
						player.sendMessage(Config.CHAT_PREFIX + " " + ChatColor.WHITE + URL);
					}
					else
					{
						event.getPlayer().sendMessage(SignURLs.PREFIX + "The link '" + ChatColor.YELLOW 
								+ lineText + ChatColor.WHITE + "' could not be found in the DB!");
						block.breakNaturally();
					}
				}
			}
		}
	}
}
