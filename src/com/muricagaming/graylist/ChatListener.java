package com.muricagaming.graylist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
	private Main plugin;
	
	public ChatListener(Main plugin)
	{
		this.plugin = plugin;		
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event)
	{
		if(!plugin.glist.contains(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission("graylist.bypass"))
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + plugin.getConfig().getString("message"));
		}
	}
}
