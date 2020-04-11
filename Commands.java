package io.github.sgourdas.tdm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor {
	
	public Commands() {
		
		TDM.plugin.getCommand("tdm").setExecutor(this);
		
	}
	// TODO Add console running support
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, String[] args) {
      
    	if ((sender instanceof Player) && (cmd.getName().equalsIgnoreCase("tdm"))) {	
    		
    		final Player player = (Player) sender;
    		
    		if(args.length == 0) {
    			
    			player.sendMessage(TDM.prefix + "Do" + ChatColor.GOLD + " /tdm help " + ChatColor.GREEN + "for help.");
    			return false;
    			
    		}

        	if(args[0].equalsIgnoreCase("open")) {
        		
        		if(!player.hasPermission("tdm.user")) {
	        		
	        		player.sendMessage(TDM.prefix + "You dont have permission for this.");
	        		return false;
	        		
	        	}
        		
        		if(TDM.isOpen) {
        			
        			player.sendMessage(TDM.prefix + "Team Deathmatch is already open.");
        			return false;
        			
        		}
        		
        		if(TDM.gameRunning) {
        			
        			player.sendMessage(TDM.prefix + "A Team Deatchmatch is already in progress.");
        			return false;
        			
        		}
        		
        		TDM.playersJoined = new ArrayList<Player>();
        		TDM.isOpen = true;
        		
        		for(Player onlineP : Bukkit.getServer().getOnlinePlayers()) {
        		 
        			onlineP.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Team Deathmatch is now open.", ChatColor.GREEN + "Type \"/tdm join\" to join.", 10, 60, 20);
        		
        		}
        		
        	} else if(args[0].equalsIgnoreCase("join")) {
        	
        		if(!TDM.isOpen) {
        			
        			player.sendMessage(TDM.prefix + "The Team Deathmatch has not opened yet.");
        			return false;
        			
        		}
        		
        		if(TDM.gameRunning) {
        			
        			player.sendMessage(TDM.prefix + "A Team Deatchmatch is already in progress.");
        			return false;
        			
        		}
        		
	        	if(!player.hasPermission("tdm.user")) {
	        		
	        		player.sendMessage(TDM.prefix + "You dont have permission for this.");
	        		return false;
	        		
	        	}
	        		
	        	if(!TDM.playersJoined.contains(player)) {	// Add the player to the game list
	        	
	        		TDM.playersJoined.add(player);
			       	Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has joined the Team Deathmatch queue (" + ChatColor.GOLD + TDM.playersJoined.size() + ChatColor.GREEN + ").");
		        	
	       		} else { player.sendMessage(TDM.prefix + "You have already joined the Team Deathmatch queue."); }
	        	
        	} else if(args[0].equalsIgnoreCase("leave")) {
        		
        		if(!TDM.isOpen) {
        			
        			player.sendMessage(TDM.prefix + "The Team Deathmatch has not even opened yet.");
        			return false;
        			
        		}
        		
        		if(TDM.gameRunning) {
        				
        			player.sendMessage(TDM.prefix + "You cannot quit an ongoing Deathmatch.");
        			return false;
        			
        		}
        		
        		if(!player.hasPermission("tdm.user")) {
	        		
	        		player.sendMessage(TDM.prefix + "You dont have permission for this.");
	        		return false;
	        		
	        	}
        		
        		if(TDM.playersJoined.contains(player)) {
        			
        			TDM.playersJoined.remove(player);
        			
        		} else { player.sendMessage(TDM.prefix + "You have not joined the Deathmatch queue."); }
        	
        	} else if(args[0].equalsIgnoreCase("team")) {
        		
        		if(TDM.gameRunning) {
        			
        			if(args.length == 1) {
        				
        				if(TDM.blueTeamPlayers.contains(player))
        					player.sendMessage(TDM.prefix + "You are in the blue team.");
        				else if(TDM.redTeamPlayers.contains(player))
        					player.sendMessage(TDM.prefix + "You are in the red team.");
        				else
        					player.sendMessage(TDM.prefix + "You are not in the TDM match.");
        				
        			} else {
	        			
        				Player p = Bukkit.getPlayer(args[1]);
        				
        				if(p == null)
        					player.sendMessage(TDM.prefix + args[1] + " is not in the TDM.");
        				else if(TDM.blueTeamPlayers.contains(p))
        					player.sendMessage(TDM.prefix + args[1] + " is in the blue team.");
        				else if(TDM.redTeamPlayers.contains(p))
        					player.sendMessage(TDM.prefix + args[1] + " is in the red team.");
        				
        				
        			}
        			
        		} else {
        			
        			player.sendMessage(TDM.prefix + "The Deatchmatch has not started yet.");
        			
        		}
        		
        	} else if(args[0].equalsIgnoreCase("start")) {
        		
        		if(!player.hasPermission("tdm.admin")) {
	        		
	        		player.sendMessage(TDM.prefix + "You dont have permission for this.");
	        		return false;
	        		
	        	}
        		
        		if(!TDM.isOpen) {
        			
        			player.sendMessage(TDM.prefix + "The Deathmatch has not even opened yet.");
        			return false;
        			
        		}
        		
        		if(TDM.gameRunning) {
        			
        			player.sendMessage(TDM.prefix + "The Deathmatch has already started.");
        			return false;
        			
        		}
        		
        		if(TDM.playersJoined.size() < 2) {	//TODO change 2 to a var
        			
        			player.sendMessage(TDM.prefix + "Not enough players have joined.");
        			return false;
        			
        		}
        		
        		if(TDM.redTeamSpawn == null || TDM.blueTeamSpawn == null) {
        			
        			player.sendMessage(TDM.prefix + "Not all spawns have been set.");
        			return false;
        			
        		}
        		
        		player.sendMessage(TDM.prefix + "You have started the Deatmatch.");
        		TDM.gameRunning = true;
        		TDM.teamsInit();
        		
        		for(Player onlineP : Bukkit.getServer().getOnlinePlayers()) {
           		 
        			onlineP.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "The Team Deathmatch has begun!", ChatColor.GREEN + "Last team standing wins.", 10, 40, 10);
        		
        		}
        		
        		TDM.playersJoined.clear();
        		
        	} else if(args[0].equalsIgnoreCase("set")) {	// /set
        		
        		if(!player.hasPermission("tdm.admin")) {
	        		
	        		player.sendMessage(TDM.prefix + "You do not have permission for this.");
	        		return false;
	        		
	        	}
        		
        		if(args.length == 1) {
        			
        			player.sendMessage(TDM.prefix + "Correct usage:");
        			player.sendMessage(ChatColor.GOLD + "/tdm set lives <lives>" + ChatColor.GREEN + " Set each player's lives.");
        			player.sendMessage(ChatColor.GOLD + "/tdm set spawn <blueteam | redteam | spectators>" + ChatColor.GREEN + " Set type of spawn.");
        			return false;
        			
        		}
        		
        		if(args[1].equalsIgnoreCase("lives")) {		// /set lives
        		
	        		if(args.length == 2) {
	        			
	        			player.sendMessage(TDM.prefix + "Please specify the number of lives to set per player.");
	        			player.sendMessage(ChatColor.GOLD + "/tdm set lives <lives>" + ChatColor.GREEN + " Set each player's lives.");
	        			return false;
	        			
	        		}
	        		
	        		try {
	        			
	        			TDM.playerLives = Integer.parseInt(args[2]); // catch exception
	        			TDM.plugin.getConfig().set("lives", TDM.playerLives);
	        			TDM.plugin.saveConfig();
		        		player.sendMessage(TDM.prefix + "Lives per player set to " + TDM.playerLives + ".");
		        		
	        		} catch (NumberFormatException e) {

	        			player.sendMessage(TDM.prefix + "Player's lives can only be an integer.");
	        		    return false;
	        		
	        		}	
        		
        		} else if(args[1].equalsIgnoreCase("spawn")) {	// /set spawn
        		
        			if(args.length == 2) {
	        			
	        			player.sendMessage(TDM.prefix + "Please specify the kind of spawn to set.");
	        			player.sendMessage(ChatColor.GOLD + "/tdm set spawn <blueteam | redteam | spectators>" + ChatColor.GREEN + " Set type of spawn.");
	        			return false;
	        		
        			}
        			
        			if(args[2].equalsIgnoreCase("blueteam")) {
	        		
	            		TDM.blueTeamSpawn = player.getLocation();
	            		TDM.plugin.getConfig().set("spawns.blueteam", TDM.blueTeamSpawn);
	            		TDM.plugin.saveConfig();
	            		player.sendMessage(TDM.prefix + "Blue Team's spawn set.");
	        			
	        		} else if(args[2].equalsIgnoreCase("redteam")) {
		        				
	            		TDM.redTeamSpawn = player.getLocation();
	            		TDM.plugin.getConfig().set("spawns.redteam", TDM.redTeamSpawn);
	            		TDM.plugin.saveConfig();
	            		player.sendMessage(TDM.prefix + "Red Team's spawn set.");
	        			
	        		} else if(args[2].equalsIgnoreCase("spectators")) {
		        		
	            		TDM.spectatorSpawn = player.getLocation();
	            		TDM.plugin.getConfig().set("spawns.spectators", TDM.spectatorSpawn);
	            		TDM.plugin.saveConfig();
	            		player.sendMessage(TDM.prefix + "Spectators' spawn set.");
	        			
	        		} else {
	        			
	        			player.sendMessage(TDM.prefix + "Spawn type can have one of the three values:");
	        			player.sendMessage(ChatColor.GOLD + "/tdm set spawn <blueteam | redteam | spectators>" + ChatColor.GREEN + " Set type of spawn.");
	        			return false;
	        			
	        		}
        		
        		} else {
        			
        			player.sendMessage(TDM.prefix + "Correct usage:");
        			player.sendMessage(ChatColor.GOLD + "/tdm set lives <lives>" + ChatColor.GREEN + " Set each player's lives.");
        			player.sendMessage(ChatColor.GOLD + "/tdm set spawn <blueteam | redteam | spectators>" + ChatColor.GREEN + " Set type of spawn.");
        			return false;
        			
        		}
        	
        	} else if(args[0].equalsIgnoreCase("reload")) {
        		
        		TDM.plugin.reloadConfig();
        		
        		TDM.rewards = TDM.plugin.getConfig().getStringList("rewards");
        		TDM.blueTeamSpawn = (Location) TDM.plugin.getConfig().get("spawns.blueteam");
        		TDM.redTeamSpawn = (Location) TDM.plugin.getConfig().get("spawns.redteam");
        		TDM.spectatorSpawn = (Location) TDM.plugin.getConfig().get("spawns.spectators");
        		TDM.playerLives = TDM.plugin.getConfig().getInt("lives");
        		
        		TDM.plugin.saveConfig();
        		System.out.println(TDM.rewards.get(0));
        		player.sendMessage(TDM.prefix + "TDM has been reloaded");
        		
        	} else if(args[0].equalsIgnoreCase("help")) {
        		
    			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "--------------------------------------------------");
        		
        		if(player.hasPermission("tdm.admin")) {
        			
        			player.sendMessage(ChatColor.GOLD + "/tdm join" + ChatColor.GREEN + " Join the TDM queue.");
        			player.sendMessage(ChatColor.GOLD + "/tdm leave" + ChatColor.GREEN + " Leave the TDM queue.");
        			player.sendMessage(ChatColor.GOLD + "/tdm team [player]" + ChatColor.GREEN + " Show the player's team.");
        			player.sendMessage(ChatColor.GOLD + "/tdm open" + ChatColor.GREEN + " Open the TDM queue.");
        			player.sendMessage(ChatColor.GOLD + "/tdm start" + ChatColor.GREEN + " Start the TDM match.");
        			player.sendMessage(ChatColor.GOLD + "/tdm set lives <lives>" + ChatColor.GREEN + " Set each player's lives.");
        			player.sendMessage(ChatColor.GOLD + "/tdm set spawn <blueteam | redteam | spectators>" + ChatColor.GREEN + " Set type of spawn.");
        			player.sendMessage(ChatColor.GOLD + "/tdm reload" + ChatColor.GREEN + " Reload the plugin configuration.");
        			player.sendMessage(ChatColor.GOLD + "/tdm help" + ChatColor.GREEN + " Show the help menu.");
        			
        		} else if(player.hasPermission("tdm.user")) {
        			
        			player.sendMessage(ChatColor.GOLD + "/tdm join" + ChatColor.GREEN + " Join the TDM queue."); 
        			player.sendMessage(ChatColor.GOLD + "/tdm leave" + ChatColor.GREEN + " Leave the TDM queue.");
        			player.sendMessage(ChatColor.GOLD + "/tdm team [player]" + ChatColor.GREEN + " Show the player's team.");
        			player.sendMessage(ChatColor.GOLD + "/tdm help" + ChatColor.GREEN + " Show the help menu.");
        			
        		}
        		
        		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "--------------------------------------------------");
        		
        		return false;
        	
    		} else { player.sendMessage(TDM.prefix + "Unkown command, do" + ChatColor.GOLD + " /tdm help " + ChatColor.GREEN + "for help."); }
        	
        	return true;
        	
        }
    	
        return false;
    
    }
    
}
