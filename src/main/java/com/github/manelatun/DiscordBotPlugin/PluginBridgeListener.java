package com.github.manelatun.DiscordBotPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PluginBridgeListener implements Listener {

  private final Bot bot;
  public PluginBridgeListener(Bot bot) {
    this.bot = bot;
  }

  @EventHandler
  public void onChatMessage(AsyncPlayerChatEvent event) {

    var author = event.getPlayer().getName();
    var message = event.getMessage();

    bot.sendBridgeMessage(author, message);
  }
}
