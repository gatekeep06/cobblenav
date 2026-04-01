# Update 2.3.1

- New client config parameters:
  - `enableItemShaking` determines whether items from the mod will shake when there's a signal.
  - `enableDisplayOfNamesOnRadar` determines whether names of Pokémon will be displayed on the PokéFinder overlay.
  - `enableMultipleModelItems` determines whether items from the mod can change their models (disable for compatibility with the current version of Cobblemon 3D Pokenavs).
- Hopefully the PokéFinder overlay freezing issue has been fixed.

# Update 2.3.0

- Tooltips with data about conditions affecting spawns on the Location screen have been replaced with a new feature - Details Tab.
  - Click on any spawn entry on the Location screen or FishingNav screen to open the tab.
  - You can switch between other entries on the screen without closing the tab using the arrow buttons.
  - The tab sections display data about the spawn result, rarity, conditions, and anti-conditions. For convenience, sections can be collapsed.
  - `CONDITION_SECTION_WIDGETS_CREATED`, `ANTICONDITION_SECTION_WIDGETS_CREATED`, and `SPAWN_DATA_WIDGETS_CREATED` events have been added. You can use them to add yourn own widgets to the tab.
- The spawn data collection system has been heavily reworked, with increased scalability.
  - A spawn cataloging system has been added. 
    - Now, when a Pokémon spawns near you, PokéNav will record its spawn in the catalog. 
    - Currently, having an entry in the catalog unlocks data about spawn conditions and anti-conditions. 
    - In future updates, the system will also be used to implement a Catalog screen with all spawns known to you.
    - A command for modifying players' catalogs has also been added - `/pokenav catalogue <player> <grant/revoke/list> ...`
  - When using PokéNav on a PokéSnack block, the data for the PokéSnack spawner will be displayed, taking its effects into account.
  - Added collection of spawn data for herds.
  - By default, Pokémon are now not hidden. You can enable hiding using the `hideUnknownPokemon` parameter in the server config. In that case, if the player doesn't know the Pokémon, it will be replaced with a question mark, and all spawn result data will be hidden.
  - The purpose of the collectors has been expanded, and the functionality has been reworked accordingly. Now, collectors gather all spawn-related data.
  - Changes to bucket weights are now also taken into account.
- PokéFinder has been reworked.
  - The UI has been completely redesigned.
  - The configuration method has been changed. You can now add as many filters as you want. The radar will display Pokémon that satisfy at least one filter.
    - Added "Translated Name" filter.
    - Added "Pokémon Properties" filter.
    - Added "Pokémon Labels" filter.
    - Added "EV Yield" filter.
    - Added "Uncaught Pokémon" filter.
  - The counter on the left reflects the number of Pokémon in the area that match your filters.
- Buckets are now saved in the Pokémon's data as an aspect upon spawning. This allows you to use PokéFinder and the "Pokémon Properties" filter to locate Pokémon of a specific rarity. For example, by specifying `spawn_bucket=ultra-rare`, the radar will display Pokémon that spawned from an ultra-rare bucket.
- The biome platform system has been updated. By using the spawn conditions built into Cobblemon, greater flexibility has been achieved in determining suitable platforms.
- Implemented support for items with multiple models, similar to the spyglass and Poké Balls. Models for most items from the mod are now separated into inventory model, hand model, active state model, and active signal model.
- Some items will now signal certain events.
- New server config parameters:
  - `hideConditionsOfUnknownSpawns` determines whether conditions and anti-conditions for spawns not recorded in the catalog will be collected and shown.
  - `percentageForKnownHerd` determines the percentage of Pokémon known to the player required for the spawn result of a herd to be considered known to the player.
  - `syncEvYieldWithClient`, similarly to `syncLabelsWithClient`, determines whether Pokémon EV yield data will be synced with the client, which is necessary for the corresponding PokéFinder filter to work.
- New client config parameters:
  - `pokefinderScreenScale` determines scale of the PokéFinder screen.
  - `pokefinderOverlayScale` determines scale of the Radar overlay.
  - `pokefinderOverlayOffsetX` determines the overlay's offset along the x-axis from the edge of the screen.
  - `pokefinderOverlayOffsetY`determines the overlay's offset along the y-axis from the edge of the screen.
- Minor UI changes and fixes.
- Added `#cobblenav:pokefinder` item tag.
- Added a couple of new biome platforms.
- PokéFinder no longer opens the screen while in the off-hand if you have an item in your main hand.
- Client collectors have been removed.
- Myths and Legends and Cobblemon Counter integrations have been temporarily removed.
- Finder screen has been temporarily disabled.
- The transition to the Kotlin DSL has been completed. Thanks to Mikita Kurganovich for this.

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