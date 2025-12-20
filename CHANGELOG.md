# Changelog

All notable changes to AirwaveChat will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-12-19

### 🎯 Major Refactorization - Stable Release

This version represents a complete refactorization of AirwaveChat, focusing on stability, performance, and production readiness. All experimental features have been removed in favor of a clean, reliable codebase.

### Added
- **Centralized Messages System**: All user-facing text now loaded from `messages.yml`
  - No hardcoded strings in Java classes
  - Easy customization without recompilation
  - Consistent message formatting
- **Enhanced Message Keys**: New message keys for better granularity
  - `frequencies.list_filter_note` - Type filter indicator
  - `frequencies.list_next_page` - Pagination navigation
  - `frequencies.info_usage` - Info command usage
  - `frequencies.info_not_found` - Frequency not found error
  - `frequencies.search_*` - Complete search command messages
  - `frequencies.top_row` - Top frequencies row format
  - `signal.no_signal` - No signal indicator
  - `about.*` - About command messages
  - `error.save_data_error` - Data saving error message
- **English-Only Interface**: All messages, commands, and documentation in English
  - Clean, professional communication
  - Consistent terminology throughout
  - Easy to understand for international audience

### Removed
- **GUI System**: Removed all inventory-based GUIs
  - Deleted `GUIManager.java`
  - Deleted `GUIClickListener.java`
  - Deleted `gui.yml` configuration
  - Removed `/airwavechat gui` and `/airwavechat menu` commands
- **Structure System**: Removed automatic structure creation
  - Deleted `StructureInteractListener.java`
  - Removed all structure-related configuration
- **Encryption System**: Removed frequency encryption/passcode feature
  - Deleted `PasscodeListener.java`
  - Removed encryption logic from `FrequencyManager`
  - Removed `authorizedEncryptedAccess` map
  - Removed `createCustomFrequency` method
- **Multi-language Support**: Removed language switching system
  - Deleted `messages_en.yml`, `messages_es.yml`, `messages_pt.yml`
  - Deleted `messages/` directory
  - Removed language selection from config
  - Single `messages.yml` file in English
- **Build Artifacts**: Removed unnecessary files
  - Deleted `dependency-reduced-pom.xml` from repository

### Changed
- **All Messages to English**: Converted from Spanish to English
  - Command descriptions in `plugin.yml`
  - Permission descriptions
  - All user-facing messages in `messages.yml`
  - Action bar notifications
  - Error messages
- **Improved Message Formatting**: Better user experience
  - "Connected to FM 103.5" instead of generic messages
  - "Signal Strength: ███░░ (72%)" for action bar
  - Clear, friendly error messages
  - Consistent formatting across all commands
- **Code Clean-up**: Removed all code comments
  - Cleaner, more readable code
  - Self-documenting method names
  - Reduced file sizes
- **FrequenciesCommand**: Complete refactor
  - All hardcoded text replaced with message keys
  - Improved pagination system
  - Better search functionality
  - Consistent message formatting
- **AirwaveChatCommand**: Simplified
  - About command now uses message keys
  - Removed GUI-related code
- **SignalBarTask**: English interface
  - "No Signal" instead of "Sin señal"
  - Uses message key from config
- **DataManager**: Error messages from config
  - Save errors now use message keys
  - Better error reporting

### Fixed
- **Cross-World Communication Bug**: Players in different worlds can no longer communicate
  - Added world equality check in `FrequencyChatListener`
- **Null Pointer Exceptions**: Comprehensive null validation
  - Location null checks
  - World validation before distance calculation
  - Frequency existence validation
- **Memory Leaks**: Improved cleanup
  - Player references properly removed on disconnect
  - Frequency listeners set cleaned up
- **Thread Safety**: Enhanced concurrent operations
  - ConcurrentHashMap for all shared data
  - Thread-safe listener management
  - Async I/O for file operations
- **Race Conditions**: Fixed synchronization issues
  - Proper locking in frequency operations
  - Thread-safe player frequency tracking

### Performance
- **Reduced Memory Footprint**: Removed unused features and code
- **Faster Message Processing**: Direct message key lookup
- **Optimized Proximity Calculations**: Better distance checking
- **Improved Scheduler Usage**: Better Folia detection and support

### Security
- **Removed Attack Vectors**: Eliminated encryption system vulnerabilities
- **Input Validation**: Better command argument validation
- **Error Handling**: Comprehensive exception catching

### Documentation
- **README.md**: Updated with v2.0.0 features (English)
- **CHANGELOG.md**: Complete version history (English)
- **Code Comments**: Removed for cleaner code
- **Self-Documenting**: Clear method and variable names

### Migration from 1.0.3

**Breaking Changes:**
1. GUIs no longer available - use commands instead
2. Encryption/passcodes removed - frequencies are public
3. Structure creation removed
4. Multi-language support removed - English only
5. Custom frequency creation requires manual config edit

**Configuration:**
- Remove `options.language` from config.yml
- Remove any GUI-related settings
- Remove encryption/passcode settings
- Keep existing frequency configuration (still compatible)

**Messages:**
- Replace `messages_*.yml` files with single `messages.yml`
- All messages now in English
- New message keys available for customization

**Commands:**
- `/airwavechat gui` - REMOVED (use regular commands)
- `/airwavechat menu` - REMOVED (use regular commands)
- All other commands work the same

**Update Procedure:**
1. Backup your server
2. Stop the server
3. Replace AirwaveChat-1.0.3.jar with AirwaveChat-2.0.0.jar
4. Delete old `messages_*.yml` files
5. Review and update `config.yml` (remove obsolete options)
6. Start server
7. Test basic functionality
8. Customize `messages.yml` if needed

### Version Philosophy

v2.0.0 is a **stable release** with **no plans for new features**. The focus is on:
- ✅ Reliability and stability
- ✅ Performance optimization  
- ✅ Clean, maintainable code
- ✅ Production-ready quality
- ❌ No experimental features
- ❌ No breaking changes planned
- ❌ Minimal maintenance mode

Future updates will only address:
- Critical bug fixes
- Security patches
- Minecraft version compatibility

---

## [1.0.3] - 2025-12-06

### Added
- **GUI System**: Complete graphical interface for managing frequencies
  - Main menu with navigation to all features
  - Frequencies menu with pagination support
  - Favorites menu for quick access to saved frequencies
  - Statistics menu showing network information
  - Information menu with plugin details
- **GUI Configuration**: New `gui.yml` file for customizing all menus
  - Configurable inventory sizes, titles, and materials
  - Per-item slot positioning
  - Support for custom colors and lore
  - Sound effects configuration (ready for implementation)
- **GUIManager**: Central manager class for all inventory operations
  - Menu creation and caching
  - Player page tracking for pagination
  - Favorites storage and management
- **GUIClickListener**: Event-driven inventory click handler
  - Automatic menu routing based on title detection
  - Shift+Click detection for favorites management
  - Connect on regular click functionality
- **New Commands**: 
  - `/airwavechat gui` - Open main GUI menu
  - `/airwavechat menu` - Alias for gui command

### Changed
- **AirwaveChatCommand**: Enhanced with GUI subcommands and improved tab completion
- **Version**: Bumped to 1.0.3

### Technical
- New package: `com.joshlucem.airwavechat.gui` with GUIManager and GUIClickListener
- Integrated GUI listener registration in plugin startup
- Added gui.yml resource file loading in config reload process
- GUI system fully externalized through configuration file

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
