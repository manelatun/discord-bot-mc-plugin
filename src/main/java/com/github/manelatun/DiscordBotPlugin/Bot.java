package com.github.manelatun.DiscordBotPlugin;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bot {

  private final Plugin plugin;
  public Bot(Plugin plugin) {
    this.plugin = plugin;
  }

  private JDA bot;
  void start() throws Exception {
    var token = plugin.getToken();
    var builder = JDABuilder
      .createDefault(token)
      .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
    if (plugin.getIsBridgeEnabled()) {
      builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
      builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
    }
    bot = builder.build();
    bot.getPresence().setStatus(OnlineStatus.ONLINE);
    bot.awaitReady();
  }

  private Guild guild;
  void setGuild() throws Exception {
    var guildId = plugin.getGuildId();
    guild = bot.getGuildById(guildId);
    if (guild == null) throw new Exception("The guild with ID '" + guildId + "' is not a valid guild.");
  }

  private TextChannel bridgeChannel;
  void startBridge() throws Exception {
    var channelId = plugin.getBridgeChannelId();
    bridgeChannel = guild.getTextChannelById(channelId);
    if (bridgeChannel == null) throw new Exception("The channel with ID '" + channelId + "' is not a valid text channel.");
    bot.addEventListener(new BotBridgeListener(bridgeChannel, plugin));
  }

  void sendBridgeMessage(String author, String message) {
    bridgeChannel.sendMessage("<" + author + "> " + message).queue();
  }

  private Message statusCardMessage;
  void startStatusCard() throws Exception {

    var channelId = plugin.getStatusCardChannelId();
    var messageId = plugin.getStatusCardMessageId();

    var channel = guild.getTextChannelById(channelId);
    if (channel == null) throw new Exception("The channel with ID '" + channelId + "' is not a valid text channel.");

    // Get the message to update or send a new message.
    try {
      statusCardMessage = channel.retrieveMessageById(messageId).submit().get();
      statusCardMessage.editMessageEmbeds(buildStatusCard(true, null)).submit().get();
    } catch (Exception e) {
      statusCardMessage = channel.sendMessageEmbeds(buildStatusCard(true, null)).submit().get();
    }

    plugin.setStatusCardMessageId(statusCardMessage.getIdLong());
  }

  void updateStatusCard(boolean online, List<String> overridePlayerList) {
    if (!plugin.getIsStatusCardEnabled()) return;
    statusCardMessage.editMessageEmbeds(buildStatusCard(online, overridePlayerList)).queue();
  }

  private static final Color onlineColor = new Color(57, 203, 117);
  private static final Color offlineColor = new Color(229, 77, 66);

  private MessageEmbed buildStatusCard(boolean online, List<String> overridePlayerList) {
    var maxPlayers = plugin.getMaxPlayers();
    var players = overridePlayerList != null ? overridePlayerList : plugin.getPlayers();
    var version = plugin.getVersion();
    var serverAddress = plugin.getServerAddress();

    var playerCount = players.size();
    var playerList = "No players online.";
    if (playerCount != 0) {
      var formattedPlayerList = new ArrayList<String>();
      for (var player : players) {
        formattedPlayerList.add("`" + player + "`");
      }
      playerList = String.join(" ", formattedPlayerList);
    }

    Date date = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm");
    String currentDate = sdfDate.format(date);

    EmbedBuilder card = new EmbedBuilder();
    card.setTitle("Minecraft Server Status");
    card.setColor(online ? onlineColor : offlineColor);
    card.addField("Status", online ? "Online" : "Offline", true);
    card.addField("Player Count", playerCount + "/" + maxPlayers, true);
    card.addField("Version", version, true);
    card.addField("Players", playerList, false);
    card.setFooter("The server " + (online ? "is" : "was") + " reachable at " + serverAddress + " • " + currentDate);

    return card.build();
  }

  void stop() {
    bot.shutdown();
  }
}
