# Hotfix 2.2.2

- Added the `enableMythsAndLegendsIntegration` parameter to the config to enable Myths And Legends condition collectors. Disabled by default, as many players are using an older, incompatible version of MaL and are unable to update.

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