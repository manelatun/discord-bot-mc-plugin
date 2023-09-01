package com.github.manelatun.DiscordBotPlugin;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Plugin extends JavaPlugin {

  private final Server server = getServer();
  private final FileConfiguration config = getConfig();

  private final Bot bot = new Bot(this);

  @Override
  public void onEnable() {

    this.saveDefaultConfig();

    if (getToken() == null) {
      log(Level.SEVERE, "The 'DISCORD_TOKEN' field is missing or not valid.");
      return;
    }

    try {
      bot.start();
    } catch (Exception e) {
      log(Level.SEVERE, "Failed to initialize Discord Bot: " + e.getMessage());
      return;
    }

    if (getGuildId() == 0) {
      bot.stop();
      log(Level.SEVERE, "The 'DISCORD_SERVER_ID' field is missing or not valid.");
      return;
    }

    try {
      bot.setGuild();
    } catch (Exception e) {
      bot.stop();
      log(Level.SEVERE, "The 'DISCORD_SERVER_ID' field was not valid: " + e.getMessage());
      return;
    }

    if (!getIsBridgeEnabled()) {
      log(Level.INFO, "Bridge is disabled.");
    } else if (getBridgeChannelId() == 0) {
      log(Level.INFO, "The 'BRIDGE_CHANNEL_ID' field is empty or not valid. Bridge is disabled.");
    } else {
      log(Level.INFO, "Bridge is enabled.");
      startBridge();
    }

    if (!getIsStatusCardEnabled()) {
      log(Level.INFO, "Status card is disabled.");
    } else if (getStatusCardChannelId() == 0) {
      log(Level.INFO, "The 'STATUS_CARD_CHANNEL_ID' field is empty or not valid. Status card is disabled.");
    } else {
      log(Level.INFO, "Status card is enabled.");
      startStatusCard();
    }
  }

  private void startBridge() {
    try {
      bot.startBridge();
      server.getPluginManager().registerEvents(new PluginBridgeListener(bot), this);
    } catch (Exception e) {
      log(Level.WARNING, "Could not initialize bridge: " + e.getMessage());
    }
  }

  void sendBridgeMessage(String author, String message) {
    server.broadcastMessage("§9Discord§f <" + author + "> " + message);
  }

  private void startStatusCard() {
    try {
      bot.startStatusCard();
      server.getPluginManager().registerEvents(new PluginStatusListener(bot, this), this);
    } catch (Exception e) {
      log(Level.WARNING, "Could not initialize status card: " + e.getMessage());
    }
  }

  @Override
  public void onDisable() {
    bot.updateStatusCard(false, new ArrayList<>());
    bot.stop();
  }

  String getToken() {
    return config.getString("DISCORD_TOKEN");
  }

  Long getGuildId() {
    return config.getLong("DISCORD_SERVER_ID");
  }

  Boolean getIsBridgeEnabled() {
    return config.getBoolean("BRIDGE_ENABLED");
  }

  Long getBridgeChannelId() {
    return config.getLong("BRIDGE_CHANNEL_ID");
  }

  Boolean getIsStatusCardEnabled() {
    return config.getBoolean("STATUS_CARD_ENABLED");
  }

  Long getStatusCardChannelId() {
    return config.getLong("STATUS_CARD_CHANNEL_ID");
  }

  Long getStatusCardMessageId() {
    return config.getLong("STATUS_CARD_MESSAGE_ID");
  }

  void setStatusCardMessageId(Long messageId) {
    config.set("STATUS_CARD_MESSAGE_ID", messageId);
    this.saveConfig();
  }

  String getServerAddress() {
    return config.getString("MINECRAFT_SERVER_ADDRESS");
  }

  int getMaxPlayers() {
    return server.getMaxPlayers();
  }

  List<String> getPlayers() {
    var players = server.getOnlinePlayers();
    var playerNames = new ArrayList<String>();
    for (var player : players) {
      playerNames.add(player.getName());
    }
    return playerNames;
  }

  private final Pattern versionPattern = Pattern.compile("([0-9]+\\.[0-9]+(\\.[0-9]+)?)");
  String getVersion() {
    Matcher versionMatcher = versionPattern.matcher(getServer().getVersion());
    String version = "Unknown";
    if (versionMatcher.find()) {
      version = versionMatcher.group(0);
    }
    return version;
  }

  private final PluginLogger logger = new PluginLogger(this);
  private void log(Level level, String msg) {
    logger.log(new LogRecord(level, msg));
  }
}
