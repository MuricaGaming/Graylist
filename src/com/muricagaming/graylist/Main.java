package com.muricagaming.graylist;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
	public final Logger logger = Logger.getLogger("Minecraft");
	private PluginManager pm;
	JoinListener jl;
	ChatListener cl;
	CommandListener cmdl;
	List<String> glist;
	List<String> opq;

	
	public void onEnable()
	{	
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		loadLists();
		
		jl = new JoinListener(this);
		cl = new ChatListener(this);
		cmdl = new CommandListener(this);
		
		pm = getServer().getPluginManager();
		pm.registerEvents(jl, this);
		pm.registerEvents(cl, this);
		pm.registerEvents(cmdl, this);
		
		logger.info("Graylist has been enabled!");
	}
	
	public void onDisable()
	{
		logger.info("Graylist has been disabled!");
	}
	
	// Use the graylist command
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("graylist"))
		{
			if(args.length == 0)
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Possible arguments: add, remove, gamemode, command, message, list, version, reload");
			else if(args[0].equalsIgnoreCase("add") && args.length > 1)
			{
				if(listPlayer(args[1]))
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + args[1] + " has been added to the graylist.");
				else
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + args[1] + " is already on the graylist.");
			}
			else if(args[0].equalsIgnoreCase("remove") && args.length > 1)
			{
				if(unlistPlayer(args[1]))
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + args[1] + " has been removed from the graylist.");
				else
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + args[1] + " is not on the graylist.");
			}
			else if(args[0].equalsIgnoreCase("gamemode") && args.length > 1 && (args[1].equalsIgnoreCase("survival") || args[1].equalsIgnoreCase("creative") || args[1].equalsIgnoreCase("adventure")))
			{
				saveToConfig("gamemode", args[1]);
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "The default gamemode for graylisted players has been set to " + args[1] + ".");
			}
			else if(args[0].equalsIgnoreCase("command") && args.length > 1)
			{
				saveToConfig("command", args[1]);
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "The command has been set to /" + args[1] + ".");
			}
			else if(args[0].equalsIgnoreCase("message") && args.length > 1)
			{
				StringBuilder sb = new StringBuilder();
				String message;
				
				for(int i = 1; i < args.length; i++)
					if(i != args.length - 1)
						sb.append(args[i] + " ");
					else
						sb.append(args[i]);
				
				message = sb.toString();
				
				saveToConfig("message", message);
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "The message has been set to " + "\"" + message + "\"");
			}
			else if(args[0].equalsIgnoreCase("list"))
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Graylisted players:\n");
				
				for(String u: glist)
				{
					sb.append(ChatColor.GREEN + "- " + getServer().getOfflinePlayer(UUID.fromString(u)).getName() + "\n");
				}
				
				sender.sendMessage(sb.toString());
			}
			else if (args[0].equalsIgnoreCase("version"))
			{
				logger.info(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "This server is running Graylist version 1.1 by Lee Neighoff.");
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "This server is running Graylist version 1.1 by Lee Neighoff.");
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				getConfig().options().copyDefaults(true);
				saveConfig();
				
				loadLists();
				
				logger.info(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + "Graylist has been reloaded!");
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Graylist has been reloaded!");
			}
			else
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Invalid arguments!");
		}
		
		return false;
	}
	
	// Load the graylist and offline player queue
	public void loadLists()
	{
		glist = this.getConfig().getStringList("graylist");
		opq = this.getConfig().getStringList("offlinePlayerQueue");
	}
	
	// Add a player
	@SuppressWarnings("deprecation")
	public boolean listPlayer(String pname)
	{	
		OfflinePlayer op = getServer().getOfflinePlayer(pname);
		Player p;
		
		if(!glist.contains(op.getUniqueId().toString()))
		{			
			glist.add(op.getUniqueId().toString());
			saveToConfig("graylist", glist);
			
			if(getServer().getOnlinePlayers().contains(op))
			{
				p = getServer().getPlayer(pname);
				
				if(getConfig().getString("gamemode").equalsIgnoreCase("survival") && !p.hasPermission("graylist.bypass"))
					p.setGameMode(GameMode.SURVIVAL);
				else if(getConfig().getString("gamemode").equalsIgnoreCase("creative") && !p.hasPermission("graylist.bypass"))
					p.setGameMode(GameMode.CREATIVE);
				else if(getConfig().getString("gamemode").equalsIgnoreCase("adventure") && !p.hasPermission("graylist.bypass"))
					p.setGameMode(GameMode.ADVENTURE);
				
				p.performCommand(getConfig().getString("command"));
				
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "You have been graylisted.");
			}
			else
			{
				if(!opq.contains(op.getUniqueId().toString()))
				{
					opq.add(op.getUniqueId().toString());
					saveToConfig("offlinePlayerQueue", opq);
				}
			}
			
			return true;
		}
		else
			return false;
	}
	
	// Remove a player
	@SuppressWarnings("deprecation")
	public boolean unlistPlayer(String pname)
	{
		OfflinePlayer op = getServer().getOfflinePlayer(pname);
		Player p;
		
		if(glist.contains(op.getUniqueId().toString()))
		{		
			glist.remove(op.getUniqueId().toString());
			saveToConfig("graylist", glist);
			
			if(getServer().getOnlinePlayers().contains(op))
			{
				p = getServer().getPlayer(pname);
				
				if(!p.hasPermission("graylist.bypass"))
					p.setGameMode(GameMode.SPECTATOR);
				
				p.performCommand(getConfig().getString("command"));
				
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You have been un-graylisted.");
			}
			else
			{
				if(!opq.contains(op.getUniqueId().toString()))
				{
					opq.add(op.getUniqueId().toString());
					saveToConfig("offlinePlayerQueue", opq);
				}
			}
			
			return true;
		}
		else
			return false;
	}
	
	// Set something in the config
	public void saveToConfig(String key, Object s)
	{
		getConfig().set(key, s);
		saveConfig();
	}
}
