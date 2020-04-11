package io.github.sgourdas.tdm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
//import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.sgourdas.tdm.TDM.TDMTeam;

public class Events implements Listener {
	
	public Events() {

		TDM.plugin.getServer().getPluginManager().registerEvents(this, TDM.plugin);

	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		
		if(TDM.gameRunning) {
			
			TDM.playerRespawn(event);
		
			if(TDM.blueTeam.containsKey(player)) {

				new BukkitRunnable() {
			        
		            public void run() { player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1)); }
		            
		        }.runTaskLater(TDM.plugin, 2);
				
			} else if(TDM.redTeam.containsKey(player)){
			
				new BukkitRunnable() {
			        
		            public void run() { player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1)); }
		            
		        }.runTaskLater(TDM.plugin, 2);
				
			}	
			
		}
		
	}
	
	@EventHandler
	public void onEntityHit(final EntityDamageByEntityEvent event) {

		if(TDM.gameRunning) {
			 
			if((event.getEntity() instanceof Player) && (event.getDamager() instanceof Player)) {
				 
				Player whoGotHit = (Player) event.getEntity(), whoHit = (Player) event.getDamager();

				if(((TDM.blueTeam.get(whoHit) != null) && (TDM.blueTeam.get(whoGotHit) != null)) || ((TDM.redTeam.get(whoHit) != null) && (TDM.redTeam.get(whoGotHit) != null)))	// if they are on the same team
					event.setCancelled(true);
				 
			} else if(event.getDamager() instanceof Arrow) {
				
				Arrow arrowHitting = (Arrow) event.getDamager();
				Player whoGotHit = (Player) event.getEntity(), whoHit = (Player) arrowHitting.getShooter();
				
				if(((TDM.blueTeam.get(whoHit) != null) && (TDM.blueTeam.get(whoGotHit) != null)) || ((TDM.redTeam.get(whoHit) != null) && (TDM.redTeam.get(whoGotHit) != null)))	// if they are on the same team
					event.setCancelled(true);
				
			}
			 
		}
		 
	}
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
    	
    	Player player = event.getPlayer();
    			
    	if(!TDM.gameRunning && TDM.isOpen) {
			
			TDM.playersJoined.remove(player);
			Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has left the queue (" + ChatColor.GOLD + TDM.playersJoined.size() + ChatColor.GREEN + ").");
			return;
			
		}
		
		if(TDM.gameRunning) {
	
			if(TDM.redTeam.containsKey(player)) {
				
				TDM.redTeam.remove(player);
		    	Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has been eliminated from the Team Deathmatch!");
		    	TDM.redTeamScoreboard.removeEntry(player.getName());
		    	player.removePotionEffect(PotionEffectType.GLOWING);
		    			
		    	if(TDM.redTeam.isEmpty()) {
	
		    		TDM.award(TDMTeam.BLUE);
		    		TDM.gameReset(TDMTeam.BLUE);
		    				
		    	}
		    		
		    } else if(TDM.blueTeam.containsKey(player)) {
		    		
		    	TDM.blueTeam.remove(player);
		    	Bukkit.broadcastMessage(TDM.prefix + player.getName() + " has been eliminated from the Team Deathmatch!");
		    	TDM.blueTeamScoreboard.removeEntry(player.getName());
		    	player.removePotionEffect(PotionEffectType.GLOWING);
		    	
		    	if(TDM.blueTeam.isEmpty()) {
		    			
		    		TDM.award(TDMTeam.RED);
		    		TDM.gameReset(TDMTeam.RED);
		    				
		    	}
	    	
	    	}
			
		}
        
    }
	
}
