<div align="center">
  <h1>AirwaveChat</h1>
  
  <a href="https://discord.gg/UzXWvshGfh">
    <img src="https://img.shields.io/discord/1435701600445399177?label=Join%20Discord&logo=discord&color=5865F2" alt="Discord" />
  </a>
  <br>
  <a href="https://github.com/joshlucem/AirwaveChat/releases">
    <img src="https://img.shields.io/github/v/release/joshlucem/AirwaveChat?label=Latest%20Release&logo=github" alt="Latest Release" />
  </a>
</div>

---

**AirwaveChat v2.0.0** is a stable, production-ready radio chat plugin for Paper/Spigot/Folia 1.20+ servers. Players communicate over **completely private** FM/AM frequencies with realistic radio behavior: proximity chat, signal degradation, and authentic static effects.

🔒 **Privacy First:** Radio communications are **100% private** - no other plugins can intercept or log your frequency messages.

---

## Download
Get the latest build from the [Releases page](https://github.com/joshlucem/AirwaveChat/releases).

## Features

### Core Radio System
- 📡 **FM/AM Frequency Bands** - 1000+ available frequencies
  - FM: 100.0 - 199.9 MHz (40 block range default)
  - AM: 1000 - 1999 kHz (100 block range default)
- 🔒 **Completely Private** - Messages never reach other plugins
  - Uses `EventPriority.LOWEST` for maximum isolation
  - No chat logging by external plugins
  - No interference from anti-spam or moderation tools
- 🔊 **Proximity-Based Chat** - Configurable distance limits per frequency type
- 🔌 **Signal Degradation** - Messages distort realistically with distance
- 📊 **Live Signal Strength** - Real-time action bar indicator with 5-bar display
- 💾 **Persistent Connections** - Auto-reconnect on player login
- ⚙️ **Custom Frequencies** - Define special channels (Police, Emergency, etc.)

### User Experience
- 🔍 **Frequency Discovery** - Search, list, and browse all channels
- 📈 **Top Frequencies** - See most popular channels
- 🎵 **Radio Sound Effects** - Authentic connect/disconnect/static sounds
- 🎨 **MiniMessage Support** - Rich text formatting with color codes
- ⏱️ **Cooldown System** - Prevent frequency-hopping spam
- 🌍 **World-Aware** - Cross-world communication blocked automatically
- 📝 **Fully Customizable** - All messages editable via `messages.yml`

### Technical Excellence
- 🧵 **Thread-Safe** - ConcurrentHashMap for all shared data
- ⚡ **Async I/O** - File operations never block main thread
- 🎯 **Folia Compatible** - Auto-detection and native support
- 🔄 **Hot Reload** - Update config without server restart
- 🛡️ **Null-Safe** - Comprehensive validation prevents crashes
- 📦 **Zero Dependencies** - No external plugins required
- 🎮 **Production Ready** - Stable release, minimal maintenance mode

## Commands

All commands support tab completion and have intuitive aliases.

### User Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/connect FM/AM <frequency>` | `/c`, `/radio`, `/tune` | Connect to a frequency |
| `/disconnect` | `/dc`, `/leave`, `/tune-off` | Disconnect from current frequency |
| `/frequencies list [FM/AM] [page]` | `/freq l`, `/f l` | List available frequencies with pagination |
| `/frequencies current` | `/freq c`, `/f c` | Show your current frequency connection |
| `/frequencies info <frequency>` | `/freq i`, `/f i` | Display detailed frequency information |
| `/frequencies top` | `/freq t`, `/f t` | Show top 10 most popular frequencies |
| `/frequencies search <query>` | `/freq s`, `/f s` | Search frequencies by name |

### Admin Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/airwavechat reload` | `/awc reload`, `/airchat reload` | Reload configuration and messages |
| `/airwavechat about` | `/awc about` | Display plugin information |
| `/airwavechat help` | `/awc help` | Show complete command help |

### Usage Examples
```
/connect FM 103.5          → Connect to FM frequency 103.5
/connect AM 1250           → Connect to AM frequency 1250
/frequencies list FM       → Show all FM frequencies
/frequencies search police → Find frequencies with "police" in name
/frequencies top           → See most active channels
/disconnect                → Leave your current frequency
```

## Permissions
- `airwavechat.user` - Access to all user commands (default: **true** - all players)
- `airwavechat.admin` - Access to admin commands like reload (default: **op**)

## Configuration

AirwaveChat offers extensive customization through two main files:

### config.yml

Configure frequency ranges, distances, and gameplay mechanics:

```yaml
frequencies:
  allow_custom: true
  fm:
    min: 100.0              # Minimum FM frequency
    max: 199.9              # Maximum FM frequency
    step: 0.1               # Frequency step (0.1 = 103.5, 103.6, etc.)
    default: 101.1          # Default connection
    chat_distance: 40.0     # Communication range in blocks
    max_count: 1000         # Maximum available frequencies
  am:
    min: 1000               # Minimum AM frequency
    max: 1999               # Maximum AM frequency
    step: 1                 # Frequency step (1 = 1250, 1251, etc.)
    default: 1000           # Default connection
    chat_distance: 100.0    # Communication range in blocks
    max_count: 1000         # Maximum available frequencies
  custom:                   # Define special frequencies
    Police:
      type: "FM"
      frequency: 123.0
      chat_distance: 60.0
    Emergency:
      type: "AM"
      frequency: 456
      chat_distance: 120.0

options:
  show_join_message: true              # Show connection messages
  show_disconnect_message: true        # Show disconnection messages
  enable_proximity: true               # Enable distance-based chat
  max_message_length: 256              # Maximum message length
  cooldown_connect: 2                  # Seconds between connections
  cooldown_disconnect: 2               # Seconds between disconnections
  enable_static: true                  # Enable signal degradation
  static_threshold: 0.75               # Distance ratio when static starts (0.0-1.0)
  static_intensity: 0.5                # How aggressive static distortion is (0.0-1.0)
  enable_signal_bar: true              # Show action bar signal indicator
  signal_update_interval: 20           # Ticks between signal bar updates (20 = 1 second)
  enable_sounds: true                  # Enable radio sound effects
  sound_connect: "block.note_block.pling"      # Sound on connection
  sound_disconnect: "block.note_block.bass"    # Sound on disconnection
  sound_static: "entity.experience_orb.pickup" # Sound when static occurs
  connect_cooldown_seconds: 5          # Cooldown to prevent frequency spam

advanced:
  debug_mode: false                    # Enable debug logging
```

### messages.yml

Customize all player-facing messages with MiniMessage formatting:

```yaml
messages:
  connect:
    success: "<green>Connected to <#00FFAA>{type}</#00FFAA> <yellow>{frequency}</yellow>.</green>"
    not_found: "<red>Frequency {frequency} not found for type {type}.</red>"
    invalid_type: "<red>Type must be FM or AM. Example: /connect FM 101.1</red>"
  
  disconnect:
    success: "<gray>Disconnected from current frequency.</gray>"
    not_connected: "<red>You are not connected to any frequency.</red>"
  
  chat:
    format: "<gray>[<#00FFAA>{frequency}</#00FFAA> <white>{type}]</white> <green>{player}:</green> <white>{message}</white>"
  
  # ... and 50+ more customizable messages
```

### Signal Strength & Static

The plugin simulates realistic radio behavior:

- **Signal Strength:** Displayed as 5-bar indicator (▮▮▮▮▮) in action bar
- **Static Distortion:** Characters become garbled based on distance
- **Threshold:** Configure when static begins (default: 75% of max distance)
- **Intensity:** Control how aggressive the distortion is (default: 50%)

### Custom Frequencies

Create named special channels for roleplay or organization:

```yaml
custom:
  Police:
    type: "FM"
    frequency: 123.0
    chat_distance: 60.0
  Medical:
    type: "AM"
    frequency: 911
    chat_distance: 80.0
  Taxi:
    type: "FM"
    frequency: 555.5
    chat_distance: 50.0
```

Players can connect using: `/connect FM Police` or `/connect FM 123.0`

## Requirements
- **Server:** Paper or Spigot 1.20+ (Folia supported)
- **Java:** 17 or higher (21 recommended)
- **Dependencies:** None - fully standalone plugin

## Installation

1. **Download** the latest `.jar` from [Releases](https://github.com/joshlucem/AirwaveChat/releases)
2. **Place** the file in your server's `plugins/` folder
3. **Restart** your server (plugin will generate default configs)
4. **Configure** `config.yml` and `messages.yml` to your preferences
5. **Reload** with `/airwavechat reload` to apply changes without restart

### First-Time Setup

After installation, the plugin creates these files:
```
plugins/
└── AirwaveChat/
    ├── config.yml          # Main configuration
    ├── messages.yml        # All player messages
    └── playerdata.yml      # Persistent player connections (auto-generated)
```

Edit `config.yml` to adjust frequency ranges, distances, and gameplay options.
Edit `messages.yml` to customize all text that players see.

## Quick Start

### For Players
```
1. /connect FM 103.5       → Tune to FM frequency 103.5
2. Chat normally           → Your messages go only to this frequency
3. Watch action bar        → See signal strength and frequency
4. /disconnect             → Leave frequency and return to normal chat
```

### For Admins
```
1. Edit config.yml         → Set up frequency ranges and distances
2. /awc reload             → Apply changes without restart
3. Create custom channels  → Add named frequencies in config.yml
4. Monitor connections     → Use /frequencies top to see activity
```

### Roleplay Examples
```
/connect FM Police         → Join police radio channel
/connect AM Emergency      → Join emergency services
/connect FM 103.5          → Join public frequency
/frequencies search taxi   → Find taxi frequencies
```

## How It Works

### Proximity System
Players on the same frequency must be within the configured distance to hear each other. Distance limits vary by frequency type:
- **FM:** 40 blocks default (good for local communication)
- **AM:** 100 blocks default (longer range)
- **Custom:** Configure per-frequency

### Signal Degradation
As distance increases, messages progressively degrade with realistic static effects:

| Distance | Effect | Example |
|----------|--------|---------|
| 0-60% | Clear | "Meet at the tower" |
| 60-75% | Minor static | "Meet at t▯e tower" |
| 75-90% | Heavy static | "M▯▯t at ▯▯e ▯o▯er" |
| 90-100% | Severe static | "▯▯▯▯ ▯▯ ▯▯▯ ▯▯▯▯▯" |
| 100%+ | No signal | *(message not received)* |

The action bar shows real-time signal strength: `📻 103.5 FM | ▮▮▮░░ 35m`

### Privacy & Security

**AirwaveChat uses EventPriority.LOWEST to ensure complete privacy:**

- ✅ Messages **immediately cancelled** for frequency users
- ✅ Event processed **before any other plugin**
- ✅ Radio chat **never logged** by external plugins
- ✅ **No interference** from anti-spam/moderation tools
- ✅ **Cross-world communication blocked** automatically
- ✅ Only players on **same frequency + same world** can communicate

This makes AirwaveChat perfect for roleplay servers where private communication is critical.

### Persistent Connections

Player frequency connections are saved to `playerdata.yml` and automatically restored on login:
```yaml
players:
  uuid-here:
    frequency: "103.5"
```

Players reconnect to their last frequency automatically when they join the server.

## Static Mechanic

AirwaveChat simulates radio interference as the signal weakens. Messages progressively degrade—characters drop out or become garbled—making long‑range communication harder and more authentic. The action bar also reflects your current band/frequency and signal strength.

Below are visual examples of how the static mechanic appears in‑game:

![Static Example 1](img/static_example_1.png)
![Static Example 2](img/static_example_2.png)

In these screenshots, missing and distorted characters indicate poor signal quality, encouraging players to move closer, switch bands, or change frequencies for clearer communications.

## Technical Details

### Thread Safety
- **ConcurrentHashMap** for all player and frequency data
- **Async file I/O** prevents main thread blocking
- **No race conditions** in frequency management

### Folia Support
Automatically detects and supports Folia's regionized scheduler:
```java
try {
    Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
    // Use Folia async scheduler
} catch (ClassNotFoundException e) {
    // Use traditional Bukkit scheduler
}
```

### Performance
- **Zero overhead** when players aren't on frequencies
- **Efficient distance calculations** using Location.distance()
- **Minimal memory footprint** (~40 KB plugin size)
- **Event-driven architecture** for responsive behavior

### Message Format
Uses Kyori Adventure's MiniMessage for rich text:
```
<gray>[<#00FFAA>103.5</#00FFAA> <white>FM]</white> <green>Steve:</green> <white>Hello!</white>
```
Renders as colored, formatted text in-game.

## Version Philosophy

**v2.0.0 is a stable release with no planned new features.**

Focus areas:
- ✅ Reliability and stability
- ✅ Performance optimization
- ✅ Production-ready quality
- ✅ Clean, maintainable code

Future updates will **only** address:
- Critical bug fixes
- Security patches
- Minecraft version compatibility

No breaking changes planned. No experimental features.

## Troubleshooting

### Messages not working?
- Check if player is connected: `/frequencies current`
- Verify players are in same world
- Check distance with action bar indicator
- Ensure `enable_proximity: true` in config

### Static too aggressive?
Adjust in `config.yml`:
```yaml
options:
  static_threshold: 0.85    # Start static later (85% distance)
  static_intensity: 0.3     # Reduce distortion intensity
```

### No sound effects?
Verify sound names in `config.yml` match Bukkit sound enums:
```yaml
options:
  enable_sounds: true
  sound_connect: "block.note_block.pling"
```

### Reload not working?
Use `/airwavechat reload` (not `/reload` or `/rl`) to properly reload configs.

---

## Support & Feedback
- Join the community on Discord: https://discord.gg/UzXWvshGfh
- Report issues or request features on the [Issues](https://github.com/joshlucem/AirwaveChat/issues) page.

## License
This project is licensed under the terms described in `LICENSE`.
