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

**AirwaveChat** is a radio chat plugin for Paper/Spigot/Folia 1.20.4+ servers, developed by **@joshlucem**. It lets players communicate over private frequencies with realistic radio behavior: proximity chat, signal degradation, and convincing static.

---

## Download
Get the latest build from the [Releases page](https://github.com/joshlucem/AirwaveChat/releases).

## Features
- 🔊 **Proximity-based chat** with configurable distance limits
- 📡 **AM/FM frequency bands** (1000+ frequencies available)
- 🔌 **Signal degradation** - messages distort with distance
- 📊 **Live signal strength indicator** in action bar
- 💾 **Persistent connections** - auto-reconnect on login
- 🔍 **Frequency search** - find channels by name
- 🎵 **Radio sound effects** - authentic connect/disconnect/static sounds
- ⚙️ **Custom frequencies** - define special channels
- 🎨 **MiniMessage support** - rich text formatting
- ⏱️ **Cooldown system** - prevent spam
- 📝 **Fully configurable** - messages, distances, and behavior

## Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/connect FM/AM <frequency>` | `/c`, `/radio`, `/tune` | Connect to a frequency |
| `/disconnect` | `/dc`, `/leave`, `/tune-off` | Disconnect from frequency |
| `/frequencies list [FM/AM] [page]` | `/freq l`, `/f l` | List available frequencies |
| `/frequencies current` | `/freq c`, `/f c` | Show your current frequency |
| `/frequencies info <frequency>` | `/freq i`, `/f i` | Show frequency details |
| `/frequencies top` | `/freq t`, `/f t` | Show most popular frequencies |
| `/frequencies search <query>` | `/freq s`, `/f s` | Search frequencies by name |
| `/airwavechat reload` | `/awc reload` | Reload config (Admin) |
| `/airwavechat about` | `/awc about` | Plugin information |
| `/airwavechat help` | `/awc help` | Show help menu |

## Permissions
- `airwavechat.user` - Access to user commands (default: true)
- `airwavechat.admin` - Access to admin commands (default: op)

## Configuration
The plugin offers extensive configuration options:

### Frequency Types
- **FM**: 100.0 - 199.9 MHz (40 block range by default)
- **AM**: 1000 - 1999 kHz (100 block range by default)
- **Custom**: Define special channels with custom names and ranges

### Static & Signal Effects
- Adjustable static threshold (when distortion starts)
- Configurable static intensity
- Real-time signal strength display
- Distance-based message degradation

### Sound Effects
- Customizable sounds for connect/disconnect/static
- Toggle on/off in config
- Volume adjusts with signal strength

## Requirements
- Paper/Spigot server 1.20.4+
- Java 21+

## Installation
1. Download the latest `.jar` from [Releases](https://github.com/joshlucem/AirwaveChat/releases).
2. Place it in your server's `plugins/` folder.
3. Restart your server.
4. Configure `config.yml` and `messages.yml` as needed.
5. Run `/awc reload` to apply changes.

## Quick Start
- Tune to a frequency: `/connect FM 101.1` or `/connect AM 1025`.
- Talk on the tuned channel by chatting normally.
- Check signal and distance in the action bar.
- Search for channels: `/frequencies search alpha`.
- Disconnect when done: `/disconnect`.

## Static Mechanic

AirwaveChat simulates radio interference as the signal weakens. Messages progressively degrade—characters drop out or become garbled—making long‑range communication harder and more authentic. The action bar also reflects your current band/frequency and signal strength.

Below are visual examples of how the static mechanic appears in‑game:

![Static Example 1](img/static_example_1.png)
![Static Example 2](img/static_example_2.png)

In these screenshots, missing and distorted characters indicate poor signal quality, encouraging players to move closer, switch bands, or change frequencies for clearer comms.

---

## Support & Feedback
- Join the community on Discord: https://discord.gg/UzXWvshGfh
- Report issues or request features on the [Issues](https://github.com/joshlucem/AirwaveChat/issues) page.

## License
This project is licensed under the terms described in `LICENSE`.
