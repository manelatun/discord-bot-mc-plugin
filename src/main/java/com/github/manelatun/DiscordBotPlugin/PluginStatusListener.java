package com.github.manelatun.DiscordBotPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PluginStatusListener implements Listener {

  private final Bot bot;
  private final Plugin plugin;
  public PluginStatusListener(Bot bot, Plugin plugin) {
    this.bot = bot;
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    bot.updateStatusCard(true, null);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    var playerList = plugin.getPlayers();
    playerList.remove(event.getPlayer().getName());
    bot.updateStatusCard(true, playerList);
  }
}
