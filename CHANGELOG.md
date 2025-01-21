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