# Changelog

All notable changes to AirwaveChat will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2025-12-06

### Added
- **Multi-language Message Files**: Spanish (`messages_es.yml`), Portuguese (`messages_pt.yml`), and English (`messages_en.yml`) translations for all messages
- **Dynamic Language Switching**: Change server language by modifying `config.yml` and executing `/airwavechat reload`
- **Automatic Message Import System**: Language files are automatically imported into `messages.yml` on reload
- **Language Configuration Option**: New `options.language` setting in `config.yml` (en, es, pt)
- **Language Setup Guide**: New `LANGUAGE_SETUP.md` documentation for language configuration

### Changed
- **Code Optimization**: Improved caching and message handling efficiency
- **FrequencyManager**: Optimized frequency initialization with better memory management
- **AirwaveChat.java**: Added language loading system with `loadLanguageMessages()` and `copyConfigurationSection()` methods
- **config.yml**: Added language option to options section

### Performance
- Reduced memory footprint by using static message deserializer
- Improved frequency lookup performance with concurrent collections
- Optimized configuration file reading

## [1.0.1] - 2025-11-28

### Added
- **Disconnect Cooldown**: Configurable cooldown for `/disconnect` command
- **Auto-Disconnect on Logout**: Players automatically disconnect from frequencies when leaving the server
- **Persistent Connections**: Frequency connections are saved and restored on player login/rejoin
- **Command Aliases**: 
  - `/connect` → `/c`, `/radio`, `/tune`
  - `/disconnect` → `/dc`, `/leave`, `/tune-off`
  - `/frequencies` → `/freq`, `/freqs`, `/f`
- **Frequency Search**: New `/freq search <query>` subcommand to find channels by name
- **Signal Strength Indicator**: Live action bar display showing signal strength with visual bars and distance
- **Radio Sound Effects**: 
  - Connect sound effect
  - Disconnect sound effect
  - Static sound on degraded messages (volume increases with distance)
- **Static/Distortion System**: Messages progressively degrade with distance using custom static characters
- **Smart Frequency Listing**: 
  - Sort by listener count and frequency number
  - Filter by AM/FM type
  - Pagination with navigation hints
  - Shows active listener count per frequency
- **Subcommand Aliases**: Short versions (`l`, `c`, `i`, `t`, `s`) for all frequency subcommands
- **Data Persistence**: `playerdata.yml` stores player frequency connections
- **Signal Bar Task**: Automated task updating signal indicators every second (configurable)

### Changed
- **FrequenciesCommand**: Complete rewrite with proper subcommand routing
- **ConnectCommand**: Now reuses single instance, reads cooldown from config
- **DisconnectCommand**: Added cooldown system matching connect behavior
- **FrequencyManager**: Removed incorrect max_frequencies_per_player logic, added DataManager integration
- **Chat Format**: Now applies static effect per-receiver based on individual distance
- **plugin.yml**: Enhanced with full metadata and command aliases
- **config.yml**: Added options for static, sounds, signal bar, and cooldowns
- **messages.yml**: Streamlined and added missing keys

### Fixed
- Custom frequencies not loading due to typo (`frequencie` → `frequency`)
- Command instances being created twice (executor and tab completer)
- Invalid `api: PaperMC` field in plugin.yml
- Missing permissions declarations in plugin.yml
- Cooldown not being read from config in ConnectCommand
- Message length check applied to formatted string instead of original

### Performance
- Optimized frequency sorting with proper comparators
- Limited tab completion suggestions to 20 items for performance
- Signal bar updates at configurable intervals (default: 1 second)

## [1.0.0] - 2025-11-24

### Added
- Initial release
- Basic connect/disconnect commands
- Frequency listing and info commands
- Custom frequency support
- Basic permission system
- English messages file
- Top frequencies command

---

## Upgrade Notes

### 1.0.0 → 1.0.1
- **Config Changes**: New options added for static, sounds, and signal bar. Old configs will use defaults.
- **New Files**: `playerdata.yml` will be created automatically to store connections.
- **Command Changes**: All commands now have aliases. Old commands still work.
- **No Breaking Changes**: Fully backward compatible.
