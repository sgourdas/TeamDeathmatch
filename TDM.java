package io.github.sgourdas.tdm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

//TODO can set lives to 0 and not use extra array
// TODO rewards on config
public class TDM extends JavaPlugin {
	
	protected static boolean gameRunning = false, isOpen = false;
	protected static ArrayList<Player> playersJoined;
	protected static ArrayList<Player> blueTeamPlayers;
	protected static ArrayList<Player> redTeamPlayers;
	protected static HashMap<Player, Integer> blueTeam;
	protected static HashMap<Player, Integer> redTeam;
	protected static int playerLives = 3;
	protected static String prefix = ChatColor.GOLD + "" +  ChatColor.BOLD + ">> " + ChatColor.RESET + ChatColor.GREEN;
	protected static Scoreboard sb;
	protected static Team blueTeamScoreboard;
	protected static Team redTeamScoreboard;
	protected static Location blueTeamSpawn = null;
	protected static Location redTeamSpawn = null;
	protected static Location spectatorSpawn = null;
	protected static TDM plugin;
	protected static List<String> rewards;
	
	public void onEnable() {
		
		TDM.plugin = this;
		
		new Events();
		new Commands();
		
		this.saveDefaultConfig();
		TDM.rewards = this.getConfig().getStringList("rewards");
		TDM.blueTeamSpawn = (Location) this.getConfig().get("spawns.blueteam");
		TDM.redTeamSpawn = (Location) this.getConfig().get("spawns.redteam");
		TDM.spectatorSpawn = (Location) this.getConfig().get("spawns.spectators");
		TDM.playerLives = this.getConfig().getInt("lives");
		TDM.plugin.saveConfig();
		
		TDM.sb = Bukkit.getScoreboardManager().getMainScoreboard();
		
		if((sb.getTeam("blueteamtdm") == null) || (sb.getTeam("redteamtdm") == null)) {
			
			TDM.blueTeamScoreboard = sb.registerNewTeam("blueteamtdm");
			TDM.redTeamScoreboard = sb.registerNewTeam("redteamtdm");
		
		} else {
			
			TDM.blueTeamScoreboard = sb.getTeam("blueteamtdm");
			TDM.redTeamScoreboard = sb.getTeam("redteamtdm");
			
		}
		
		System.out.println("~ Created by sgourdas ~");

    }
	
	public void onDisable() {
		
		TDM.blueTeamScoreboard.unregister();
		TDM.redTeamScoreboard.unregister();
		System.out.println("~ Created by sgourdas ~");

    }
	
	static public void playerRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		
    	if(TDM.redTeam.containsKey(player)) {

    		int currPlayerLives = TDM.redTeam.get(player).intValue();

	    	if(currPlayerLives == 1) {	// if the player had 1 life left and died, remove him from the game
	    					
	    		TDM.redTeam.remove(player);
	    		//TDM.redTeam.replace(player, currPlayerLives - 1);////
	    		Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has been eliminated from the Emerland Kingdom's team deathmatch!");
	    		TDM.redTeamScoreboard.removeEntry(player.getName());
	    			
	    		if(TDM.spectatorSpawn != null)
	    			event.setRespawnLocation(TDM.spectatorSpawn);
	    				
	    	} else {
	    				
	    		TDM.redTeam.replace(player, currPlayerLives - 1);
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
	    		event.setRespawnLocation(TDM.redTeamSpawn);
	    		player.sendMessage(TDM.prefix + "You have " + (currPlayerLives - 1) + " lives left for the deathmatch!");
	    				
	    	}
	    			
	    	if(TDM.redTeam.isEmpty()) {

	    		TDM.award(TDMTeam.BLUE);
	    		TDM.gameReset(TDMTeam.BLUE);
	    				
	    	}
	    		
	    } else if(TDM.blueTeam.containsKey(player)) {
	    		
		    int currPlayerLives = TDM.blueTeam.get(player).intValue();

	    	if(currPlayerLives == 1) {	// if the player had 1 life left, remove him from the game
	    					
	    		TDM.blueTeam.remove(player);
	    		Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has been eliminated from the Emerland Kingdom's team deathmatch!");
	    		TDM.blueTeamScoreboard.removeEntry(player.getName());
	    			
	    		if(TDM.spectatorSpawn != null)
	    			event.setRespawnLocation(TDM.spectatorSpawn);
	    			
	    	} else {
	    				
	    		TDM.blueTeam.replace(player, currPlayerLives - 1);
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
	    		event.setRespawnLocation(TDM.blueTeamSpawn);
	    		player.sendMessage(TDM.prefix + "You have " + (currPlayerLives - 1) + " lives left for the deathmatch!");
	    				
	    	}
	    			
	    	if(TDM.blueTeam.isEmpty()) {
	    			
	    		TDM.award(TDMTeam.RED);
	    		TDM.gameReset(TDMTeam.RED);
	    				
	    	}
    	
    	}
    	
    }
	
	static public void teamsInit() {
		
		TDM.blueTeamScoreboard.setColor(ChatColor.BLUE);
		TDM.redTeamScoreboard.setColor(ChatColor.RED);
		Player selectedPlayer;
    	TDM.redTeam = new HashMap<Player, Integer>();
    	TDM.blueTeam = new HashMap<Player, Integer>();
    	TDM.redTeamPlayers = new ArrayList<Player>();
    	TDM.blueTeamPlayers = new ArrayList<Player>();
    	Random rng = new Random();
    	int totalPlayers = TDM.playersJoined.size(), randomIndex;
    	
    	for(int i = 0 ; i < totalPlayers / 2 ; i++) {	// generate red team from totalPlayers/2 players
    		// get a random index
    		randomIndex = rng.nextInt(TDM.playersJoined.size());
    		selectedPlayer = TDM.playersJoined.get(randomIndex);
    		// add the player at random index from player list to red team
    		TDM.redTeamPlayers.add(selectedPlayer);
    		TDM.redTeam.put(selectedPlayer, TDM.playerLives);
    		TDM.redTeamScoreboard.addEntry(selectedPlayer.getName());
    		selectedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
    		selectedPlayer.teleport(TDM.redTeamSpawn);
    		TDM.playersJoined.remove(randomIndex);
    		
    	}
    	
    	while(!TDM.playersJoined.isEmpty()) {	// empty out playerList to blueTeam
    		
    		selectedPlayer = TDM.playersJoined.get(0);
    		TDM.blueTeamPlayers.add(selectedPlayer);
    		TDM.blueTeam.put(TDM.playersJoined.get(0), TDM.playerLives);
    		TDM.blueTeamScoreboard.addEntry(TDM.playersJoined.get(0).getName());
    		selectedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
    		selectedPlayer.teleport(TDM.blueTeamSpawn);
    		TDM.playersJoined.remove(0);
    		
    	}
    	
    }
	
	static public void award(TDMTeam teamWon) {
		
		for(Player onlineP : Bukkit.getServer().getOnlinePlayers())
    		onlineP.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + teamWon + " team is victorious!", ChatColor.GREEN + "The Emerland Kingdom praises them with gifts!", 10, 40, 20);
		
		ArrayList<Player> wTeam;
		
		if(teamWon == TDMTeam.BLUE)
			wTeam = TDM.blueTeamPlayers;
		else
			wTeam = TDM.redTeamPlayers;
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		String playerName;
		
		for(int i = 0 ; i < wTeam.size(); i++) {
			
			playerName = wTeam.get(i).getName();
			String command;
			
			for(int rewardCounter = 0 ; i < TDM.rewards.size() ; i++) {
				
				command = TDM.rewards.get(rewardCounter);
				command = command.replace("%player%", playerName);
				Bukkit.dispatchCommand(console, command);
				
			}
			
		}
		
	}
	
	static public void gameReset(TDMTeam teamWon) {
		
		if(teamWon == TDMTeam.BLUE)
			TDM.blueTeam.forEach((p, l) -> p.removePotionEffect(PotionEffectType.GLOWING));
		else
			TDM.redTeam.forEach((p, l) -> p.removePotionEffect(PotionEffectType.GLOWING));
		
		TDM.blueTeam.clear();
		TDM.redTeam.clear();
		
		for(int i = 0 ; i < TDM.redTeamPlayers.size(); i++)
			TDM.redTeamScoreboard.removeEntry(TDM.redTeamPlayers.get(i).getName());
		
		for(int i = 0 ; i < TDM.blueTeamPlayers.size(); i++) 
			TDM.blueTeamScoreboard.removeEntry(TDM.blueTeamPlayers.get(i).getName());
		
		TDM.isOpen = false;
		TDM.gameRunning = false;
		
	}
	
	enum TDMTeam {
		
        BLUE,
        RED,
    
	}
	
}
