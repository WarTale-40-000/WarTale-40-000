# Wartale - Warhammer 40,000 for Hytale

**Wartale** is a Hytale mod that brings the grimdark universe of Warhammer 40,000 to life in Hytale. Experience the epic warfare of the 41st millennium with iconic weapons, items, and mechanics from the Warhammer 40k universe.

## 📋 Requirements

- **Hytale** (Server Version: Any - specified as `*` in manifest)
- **Java** 17 or higher
- **Gradle** 8.x or higher

## 🔧 Development Setup

### Prerequisites
1. Install Java Development Kit (JDK) 17+
2. Set up your local Hytale installation path in `gradle.properties`:
   ```properties
   hytale.home_path=<YOUR_HYTALE_PATH> # Example: C:/Games/Hytale
   ```
3. Add the other environment variables to `gradle.properties` to enable/disable compilation of sources and docs:
   ```properties
   env.java.compileSources=false
   env.java.compileDocs=false
   ```
4. Create a `.env` file in the project root and follow the template `.env.example` to set the path to your Hytale mods folder:
   ```env
   HYTALE_MODS_PATH=<YOUR_HYTALE_MODS_PATH> # Example: C:/Games/Hytale/mods
   ```

### Building the Mod

Clone the repository and build using Gradle:

```bash
# Clone the repository
git clone <repository-url>
cd WarTale-40-000

# Build the project
./gradlew build

# Generate Javadoc
./gradlew javadoc
```

The compiled mod will be available in `build/libs/Wartale-1.0.0.jar`

### Project Structure

```
WarTale-40-000/
├── src/main/java/com/warhammer/wartale/
│   ├── Wartale.java                      # Main plugin class
│   ├── components/
│   │   └── Weapon_Data.java              # Weapon ammunition data component
│   ├── interactions/weapons/
│   │   ├── Weapon_Interaction_Shoot.java # Shooting interaction handler
│   │   └── Weapon_Interaction_Reload.java# Reload interaction handler
│   ├── metadata/
│   │   └── WarhammerMetadataCollection.java # Weapon metadata registry
│   └── types/
│       └── WarhammerWeaponMetadata.java  # Weapon metadata type
├── src/main/resources/
│   ├── manifest.json                      # Mod manifest
│   ├── Server/Item/Items/Warhammer/      # Item definitions
│   ├── Common/Sounds/Warhammer/          # Sound effects
│   └── WeaponMetadata/                   # Weapon configuration
└── build.gradle.kts                       # Build configuration
```

## 🚀 Installation

1. Build or download the mod JAR file
2. Place `Wartale-1.0.0.jar` in your Hytale mods folder
3. Start your Hytale server
4. The mod will automatically load with all assets

## 💻 Technical Details

### Built With
- **Scaffoldit** v0.2.2 - Hytale mod development framework
- **Gradle** with Kotlin DSL
- **Hytale Server API**

### Component System
The mod uses Hytale's Entity Component System (ECS) to track weapon data:
- `Weapon_Data` component stores current ammunition per weapon
- Data persists across player sessions
- Custom codec for serialization/deserialization

### Interaction System
Custom interactions registered with Hytale's interaction framework:
- `Warhammer_Weapon_Interaction_Shoot` - Handles weapon firing
- `Warhammer_Weapon_Interaction_Reload` - Manages ammunition reloading

### Weapon Metadata
Weapons are configured via metadata collections:
```java
map.put("Warhammer_Bolter_Pistol_custom",
    new WarhammerWeaponMetadata("Warhammer_Bolter_Pistol_custom", 13, 5));
```
- **maxAmmo**: Magazine capacity
- **reload**: Reload time multiplier


---

**For the Emperor!** ⚔️

*In the grim darkness of the far future, there is only war... in Hytale.*
