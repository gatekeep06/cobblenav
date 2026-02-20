# Update 2.3.0

- 

# Update 2.2.5

- Fixes for compatibility with the latest version of Cobblemon.
- Fixed an issue with biome collector registration that resulted in missing biome data in tooltips and issues with biome platform selection.
- Old PokéNav has been removed from the loot table of ancient city chests, also the chance of obtaining Old PokéNav through fishing has been reduced.
- Fixed rendering of water block as an empty block.

# Update 2.2.4

- Added mark indicating if a Pokémon is nearby.
- Improved checking of installed mods. Now mod version will also be checked to avoid incompatibilities.
- Improved biome platforms.
- Removed the `enableMythsAndLegendsIntegration` parameter. MaL collectors can be now enabled/disabled in the same way as others.
- Fixed blur issue.
- Fixed incompatibility with the newest Cobblemon Counter versions.
- Some Location screen fixes.

# Update 2.2.3

- Updated the FishingNav UI:
  - Reduced size of fishing context widget.
  - Added a panel with buttons to close the menu, refresh data, and switch between buckets.
  - Added rare occurrence of Wingull.
  - Minor color changes.
  - Minor UI additions.
  - Clouds can now be added via a resource pack.
- Overhauled preference saving. Preferences are now saved locally in the client's PokéNav settings file, located at `<game-dir>/cobblenav/settings/pokenav.json`, rather than in the player's nbt file on the server.
- Revamped biome platform system.
- Added a few new platforms.
- Added small Cobblemon Counter integration.
- Added blur when blocking widgets.
- Added display of PokéBalls near caught pokémon.
- Added a parameter to the client config to disable the blur effect. Due to a conflict with an unknown mod, the blur effect may cause display issues in the UI.
- Added Wanderer's PokéNav.
- Fixed an issue where the radial menu could be opened when opening the context menu.
- A lot of refactoring.

# Hotfix 2.2.2

- Added the `enableMythsAndLegendsIntegration` parameter to the config to enable Myths And Legends condition collectors. Disabled by default, as many players are using an older, incompatible version of MaL and are unable to update.
- Fixed collector duplication when restarting the world.

# Update 2.2.1

- Removed unnecessary file name field from configs.
- Added collectors for the Myths and Legends spawning conditions.
- Changed the moment of collector registration.
- Added event to register custom collectors.
- Changed logging of collector registration.
- Minor changes to UI colors.
- Minor changes to table views.
- Track arrow changed to PokéBall model. Further refinement needed.
- Added biome platforms to the location screen. There are currently two platforms, and I'll be adding more in the future. You can add platforms through resource packs.
- Finder screen now indicates that pokémon not found.
- Removed biome collector.

# Update 2.2.0

- Added FishingNav:
  - updated the item sprite,
  - added screen,
  - added recipe.
- Changed the `collectableConditions` config parameter to simplify modification and support for custom collectors.
- Slightly optimized spawn checking.
- Added Old PokéNav to the fishing and ancient city chest loot tables.
- Fixed an issue on dedicated servers that caused PokéFinder to not be able to search for pokémon by label. This was achieved by synchronizing the species labels with the players' clients. Also, added a parameter to the config to disable this feature.
- Fixed a conflict with the blur mod that caused a transparent background.

# Update 2.1.0

- Revamped the Pokémon spawn details collection system. Made scalability easier, added ability to register collectors from outside, i.e. by other mods.
- Added ability to remove built-in collectors via config. In other words, added ability to limit available information in PokéNav tooltips.
- Added client config with the following settings:
  - PokéNav screen scale,
  - setting that determines whether rendering errors should be sent to the chat room,
  - setting that determines whether to shade unknown pokémon,
  - setting that determines whether PokéNav will use swimming animations when appropriate,
  - PokéFinder interface offset,
  - track arrow offset.
- Added PokéFinder configuration screen.
- Added pop-up notifications (So far, only to notify you when a species is added to your PokéFinder settings).
- Revamped PokéFinder settings.
- Removed the PokéFinder configuration command.
- Added animation of context menu opening.
- Added Chinese translation by Brzjomo.
- Added support for seasonal forms.
- Removed hand swing when using PokéNav.
- Fixed an issue that caused some special Pokémon forms to be considered unknown, even though logically they should be known.
- Fixed an issue with the PokéFinder interface layer hierarchy.
- Fixed line breaks for languages that do not use spaces.

# Hotfix alpha-2.0.2

- Fixed crash when starting a dedicated server

# Update alpha-2.0.1

- Removed a crash while attempting to render a Pokémon model that had broken animations as a result of improper addon management. Now such Pokémon will not be rendered, and an exception message will be sent to chat.
- Added config.
- The tooltip that appears when you click the question mark button has been replaced by a context menu.
- Updated explanatory information on the location screen, added explanatory information and a button to call the context menu with it on the finder screen
- Added display of depth conditions to the tooltips of the spawn information widgets.
- Pokémon displayed in the PokéNav will now play a swimming animation if they are conditioned to be submerged in water.
- Updated translations.
- Temporarily added a command to customize PokéFinder `/configure pokefinder species=<species list> aspects=<aspects list> shinyonly=<true/false>`