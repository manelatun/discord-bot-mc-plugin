# Discord Bot Minecraft Plugin

A Paper/Spigot plugin that hosts a Discord bot directly on a Minecraft server.

The plugin is primarily intended for small self-hosted personal servers with friends.

**Features**:
* Auto-updating card with the current server status and player list.
* Two-way bridge between the Minecraft server chat and a Discord channel.

Should work on all versions from 1.13 to 1.20.1, on both Spigot and Paper.

Originally written back on June 2021, it's been cleaned up and tested.

## Bot Setup & Permissions

Edit the `config.yml` file to set up the plugin.

The bot must have the `Send Messages` permission for both the Status Card and the Bridge Channel features to work.

To enable the Bridge Channel, the bot requires the additional `MESSAGE_CONTENT` elevated intent.

## License

The source code is licensed under the MIT license.
