package com.muricagaming.graylist;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener
{
	private Main plugin;
	
	public JoinListener(Main plugin)
	{
		this.plugin = plugin;		
	}

	@EventHandler
	public void join(PlayerJoinEvent event)
	{
		if(plugin.opq.contains(event.getPlayer().getUniqueId().toString()))
		{
			if(!event.getPlayer().hasPermission("graylist.bypass"))
			{
				if(plugin.getConfig().getString("command") != "NULL")
					event.getPlayer().performCommand(plugin.getConfig().getString("command"));
				
				if(plugin.getConfig().getString("gamemode").equalsIgnoreCase("survival"))
					event.getPlayer().setGameMode(GameMode.SURVIVAL);
				else if(plugin.getConfig().getString("gamemode").equalsIgnoreCase("creative"))
					event.getPlayer().setGameMode(GameMode.CREATIVE);
				else if(plugin.getConfig().getString("gamemode").equalsIgnoreCase("adventure"))
					event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
			
			plugin.opq.remove(event.getPlayer().getUniqueId().toString());
			plugin.saveToConfig("offlinePlayerQueue", plugin.opq);
		}
		
		if(!plugin.glist.contains(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission("graylist.bypass"))
		{
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + plugin.getConfig().getString("message"));
		}
	}	
}
