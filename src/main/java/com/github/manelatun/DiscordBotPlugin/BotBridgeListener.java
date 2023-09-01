package com.github.manelatun.DiscordBotPlugin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotBridgeListener extends ListenerAdapter {

  private final TextChannel channel;
  private final Plugin plugin;
  public BotBridgeListener(TextChannel channel, Plugin plugin) {
    this.channel = channel;
    this.plugin = plugin;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {

    if (event.getChannel().getIdLong() != channel.getIdLong()) return;

    if (event.getAuthor().isBot()) return;
    if (event.getAuthor().isSystem()) return;
    if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;

    var authorName = event.getAuthor().getName();
    var authorDiscriminator = event.getAuthor().getDiscriminator();
    var author = authorName + (!authorDiscriminator.equals("0000") ? "#" + authorDiscriminator : "");

    var message = event.getMessage().getContentStripped();

    plugin.sendBridgeMessage(author, message);
  }
}
