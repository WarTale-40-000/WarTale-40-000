# WarTale 40,000 - Asset Style Guide and Naming Conventions

## Table of Contents

- [Asset Style Guide and Naming Conventions](#asset-style-guide-and-naming-conventions)
    - [1. Guiding Principles](#1-guiding-principles)
    - [2. Canonical Base Name Formula](#2-canonical-base-name-formula)
    - [3. Category Prefix Reference](#3-category-prefix-reference)
    - [4. File Variants Per Asset](#4-file-variants-per-asset)
    - [5. Directory Structure](#5-directory-structure)
    - [6. Naming Rules Per Asset Type](#6-naming-rules-per-asset-type)
    - [7. Quick-Reference Checklist for New Assets](#7-quick-reference-checklist-for-new-assets)

---

# Asset Style Guide and Naming Conventions

## 1. Guiding Principles

- One base name, all variants. Every asset has a single canonical base name. All file variants (model, texture,
  inventory icon, HUD icon, crafting-category icon, item-definition JSON) derive from that base name using only a suffix
  or directory change. Nothing else changes.
- PascalCase_with_underscores. Words within a logical segment use PascalCase. Segments are separated by a single
  underscore. Example: `Cadian_Boltpistol`, `BloodPact_Flamer`.
- No spaces, no special characters, no lowercase-only. File names are case-sensitive on Linux (the target Docker
  environment). Always use the casing rules below.
- Category prefix on item-definition files. JSON item-definition files include a category prefix (`Weapon_`, `Armor_`,
  `Magazine_`, `Bench_`, `Ingredient_`) that mirrors the parent directory name. This allows the JSON file name to be
  unambiguous when viewed in isolation.
- Faction segment comes second. For faction-specific items the faction name is the second segment:
  `Weapon_BloodPact_Boltpistol`, `Armor_Cadian_Chest`. For faction-agnostic items the faction segment is omitted:
  `Weapon_Chainsword`.

---

## 2. Canonical Base Name Formula

```
[CategoryPrefix]_[FactionName]_[ItemName]
```

- CategoryPrefix: mandatory, from the fixed list below.
- FactionName: optional. Include only when the item belongs to a specific faction. Omit for universal items.
- ItemName: the human-readable item name in PascalCase.

Examples of well-formed base names:

```
Weapon_Boltpistol
Weapon_BloodPact_Boltpistol
Weapon_Cadian_Powersword
Armor_Cadian_Chest
Armor_Sirdar_Head
Magazine_Bolt
Bench_Cadian_Workbench
Ingredient_Ceramite_Bar
Consumable_Corpse_Starch
```

---

## 3. Category Prefix Reference

| Prefix        | Item type                                 | JSON directory                                                           |
|---------------|-------------------------------------------|--------------------------------------------------------------------------|
| `Weapon_`     | All weapons (melee and ranged)            | `Server/Item/Items/Weapons/Melee/` or `Server/Item/Items/Weapons/Range/` |
| `Armor_`      | All wearable armour pieces                | `Server/Item/Items/Armors/{FactionName}/`                                |
| `Magazine_`   | All magazine items                        | `Server/Item/Items/Magazines/`                                           |
| `Bench_`      | Crafting bench block-items                | `Server/Item/Items/Crafting/`                                            |
| `Ingredient_` | Crafting material ingredients             | `Server/Item/Items/Crafting/Materials/`                                  |
| `Consumable_` | Food and consumable items                 | `Server/Item/Items/Consumables/`                                         |
| `Projectile_` | Server-side projectile entity definitions | `Server/Item/Items/Projectiles/`                                         |

---

## 4. File Variants Per Asset

For every asset with base name `{BaseName}`, the following files must exist and use exactly the names shown. No other
naming pattern is acceptable.

| Variant              | File name                     | Extension      | Example                                   |
|----------------------|-------------------------------|----------------|-------------------------------------------|
| Item-definition JSON | `{BaseName}`                  | `.json`        | `Weapon_BloodPact_Boltpistol.json`        |
| 3-D model            | `{BaseName}`                  | `.blockymodel` | `Weapon_BloodPact_Boltpistol.blockymodel` |
| Texture              | `{BaseName}_Texture`          | `.png`         | `Weapon_BloodPact_Boltpistol_Texture.png` |
| Inventory icon       | `{BaseName}`                  | `.png`         | `Weapon_BloodPact_Boltpistol.png`         |
| HUD icon             | `{BaseName}`                  | `.png`         | `Weapon_BloodPact_Boltpistol.png`         |
| Animation (optional) | `{BaseName}_{AnimationLabel}` | `.blockyanim`  | `Weapon_Chainsword_Spin.blockyanim`       |

Note: The inventory icon and HUD icon share the same file name because they live in different directories (see section
5). They do not need a distinguishing suffix.

---

## 5. Directory Structure

The resource tree is rooted at `src/main/resources/`. The structure below is normative. All paths are relative to that
root.

```
src/main/resources/
|
|-- manifest.json
|
|-- Common/
|   |-- Resources/
|   |   |-- Items/
|   |   |   |-- Warhammer/
|   |   |   |   |-- Weapons/
|   |   |   |   |   |-- {BaseName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |   |   |   |   |   |-- Animations/
|   |   |   |   |   |   |   |-- {BaseName}_{AnimationLabel}.blockyanim
|   |   |   |   |-- Armor/
|   |   |   |   |   |-- {FactionName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |   |   |   |-- Magazines/
|   |   |   |   |   |-- {BaseName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |   |   |   |-- Crafting/
|   |   |   |   |   |-- {BaseName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |   |   |   |-- Consumables/
|   |   |   |   |   |-- {BaseName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |   |   |   |-- Materials/
|   |   |   |       |-- (shared reusable models, e.g. Ingot.blockymodel)
|   |   |   |   |-- Projectiles/
|   |   |   |   |   |-- {BaseName}/
|   |   |   |   |   |   |-- {BaseName}.blockymodel
|   |   |   |   |   |   |-- {BaseName}_Texture.png
|   |-- Icons/
|   |   |-- ItemsGenerated/
|   |   |   |-- Warhammer/
|   |   |   |   |-- Weapons/
|   |   |   |   |   |-- {BaseName}.png
|   |   |   |   |-- Armors/
|   |   |   |   |   |-- {FactionName}/
|   |   |   |   |   |   |-- {BaseName}.png
|   |   |   |   |-- Magazines/
|   |   |   |   |   |-- {BaseName}.png
|   |   |   |   |-- Crafting/
|   |   |   |   |   |-- {BaseName}.png
|   |   |   |   |-- Consumables/
|   |   |   |   |   |-- {BaseName}.png
|   |   |-- Weapons/
|   |   |   |-- {BaseName}.png              (HUD weapon icon)
|   |   |-- CraftingCategories/
|   |   |   |-- {BenchBaseName}/
|   |   |   |   |-- {BaseName}.png          (category tab icon inside a bench)
|   |-- Sounds/
|   |   |-- Warhammer/
|   |   |   |-- {CategoryOrFaction}/
|   |   |   |   |-- {SoundEventId}.ogg
|   |-- UI/
|       |-- Custom/
|           |-- HUD/
|               |-- {HudName}.ui
|
|-- Server/
    |-- Item/
    |   |-- Items/
    |       |-- Weapons/
    |       |   |-- Melee/
    |       |   |   |-- {BaseName}.json
    |       |   |-- Range/
    |       |       |-- {BaseName}.json
    |       |-- Armors/
    |       |   |-- {FactionName}/
    |       |       |-- {BaseName}.json
    |       |-- Magazines/
    |       |   |-- {BaseName}.json
    |       |-- Crafting/
    |       |   |-- {BaseName}.json          (bench block-items)
    |       |   |-- Materials/
    |       |       |-- {BaseName}.json      (ingredient items)
    |       |-- Consumables/
    |           |-- {BaseName}.json
    |       |-- Projectiles/
    |           |-- {BaseName}.json          (projectile entity definitions)
```

---

## 6. Naming Rules Per Asset Type

### 6.1 Weapons

| File                 | Pattern                                     | Example                                   |
|----------------------|---------------------------------------------|-------------------------------------------|
| Item-definition JSON | `Weapon_[FactionName_]ItemName.json`        | `Weapon_BloodPact_Boltpistol.json`        |
| Model                | `Weapon_[FactionName_]ItemName.blockymodel` | `Weapon_BloodPact_Boltpistol.blockymodel` |
| Texture              | `Weapon_[FactionName_]ItemName_Texture.png` | `Weapon_BloodPact_Boltpistol_Texture.png` |
| Inventory icon       | `Weapon_[FactionName_]ItemName.png`         | `Weapon_BloodPact_Boltpistol.png`         |
| HUD icon             | `Weapon_[FactionName_]ItemName.png`         | `Weapon_BloodPact_Boltpistol.png`         |

Full example for a faction-specific weapon:

```
Server/Item/Items/Weapons/Range/Weapon_BloodPact_Boltpistol.json
Common/Resources/Items/Warhammer/Weapons/Weapon_BloodPact_Boltpistol/Weapon_BloodPact_Boltpistol.blockymodel
Common/Resources/Items/Warhammer/Weapons/Weapon_BloodPact_Boltpistol/Weapon_BloodPact_Boltpistol_Texture.png
Common/Icons/ItemsGenerated/Warhammer/Weapons/Weapon_BloodPact_Boltpistol.png
Common/Icons/Weapons/Weapon_BloodPact_Boltpistol.png
```

---

### 6.2 Armour

Slot values (fixed): `Head`, `Chest`, `Hands`, `Legs`, `Feet`

| File                 | Pattern                                  | Example                          |
|----------------------|------------------------------------------|----------------------------------|
| Item-definition JSON | `Armor_{FactionName}_{Slot}.json`        | `Armor_Cadian_Chest.json`        |
| Model                | `Armor_{FactionName}_{Slot}.blockymodel` | `Armor_Cadian_Chest.blockymodel` |
| Texture              | `Armor_{FactionName}_{Slot}_Texture.png` | `Armor_Cadian_Chest_Texture.png` |
| Inventory icon       | `Armor_{FactionName}_{Slot}.png`         | `Armor_Cadian_Chest.png`         |

Full example:

```
Server/Item/Items/Armors/Cadian_Guardsman/Armor_Cadian_Chest.json
Common/Resources/Items/Warhammer/Armor/Cadian_Guardsman/Armor_Cadian_Chest.blockymodel
Common/Resources/Items/Warhammer/Armor/Cadian_Guardsman/Armor_Cadian_Chest_Texture.png
Common/Icons/ItemsGenerated/Warhammer/Armors/Cadian_Guardsman/Armor_Cadian_Chest.png
```

---

### 6.3 Magazines

| File                 | Pattern                           | Example                     |
|----------------------|-----------------------------------|-----------------------------|
| Item-definition JSON | `Magazine_{ItemName}.json`        | `Magazine_Bolt.json`        |
| Model                | `Magazine_{ItemName}.blockymodel` | `Magazine_Bolt.blockymodel` |
| Texture              | `Magazine_{ItemName}_Texture.png` | `Magazine_Bolt_Texture.png` |
| Inventory icon       | `Magazine_{ItemName}.png`         | `Magazine_Bolt.png`         |

The subfolder under `Magazines/` takes the same base name as the item definition: `Magazine_Bolt/`, `Magazine_Auto/`, `Magazine_Laser/`.

Full example:

```
Server/Item/Items/Magazines/Magazine_Bolt.json
Common/Resources/Items/Warhammer/Magazines/Magazine_Bolt/Magazine_Bolt.blockymodel
Common/Resources/Items/Warhammer/Magazines/Magazine_Bolt/Magazine_Bolt_Texture.png
Common/Icons/ItemsGenerated/Warhammer/Magazines/Magazine_Bolt.png
```

---

### 6.4 Crafting Benches

| File                 | Pattern                                                                 | Example                                                                 |
|----------------------|-------------------------------------------------------------------------|-------------------------------------------------------------------------|
| Item-definition JSON | `Bench_{FactionName}_Workbench.json`                                    | `Bench_Cadian_Workbench.json`                                           |
| Model                | `Bench_{FactionName}_Workbench.blockymodel`                             | `Bench_Cadian_Workbench.blockymodel`                                    |
| Texture              | `Bench_{FactionName}_Workbench_Texture.png`                             | `Bench_Cadian_Workbench_Texture.png`                                    |
| Inventory icon       | `Bench_{FactionName}_Workbench.png`                                     | `Bench_Cadian_Workbench.png`                                            |
| Category tab icons   | `Icons/CraftingCategories/Bench_{FactionName}_Workbench/{BaseName}.png` | `Icons/CraftingCategories/Bench_Cadian_Workbench/Weapon_Boltpistol.png` |

---

### 6.5 Crafting Materials (Ingredients)

| File                 | Pattern                             | Example                               |
|----------------------|-------------------------------------|---------------------------------------|
| Item-definition JSON | `Ingredient_{ItemName}.json`        | `Ingredient_Ceramite_Bar.json`        |
| Texture              | `Ingredient_{ItemName}_Texture.png` | `Ingredient_Ceramite_Bar_Texture.png` |
| Inventory icon       | `Ingredient_{ItemName}.png`         | `Ingredient_Ceramite_Bar.png`         |

Note: Ingredients may share a generic model (e.g. `Ingot.blockymodel`) stored under
`Common/Resources/Items/Warhammer/Materials/`. Only the texture and icon are unique per ingredient.

---

### 6.6 Consumables

| File                 | Pattern                             | Example                                |
|----------------------|-------------------------------------|----------------------------------------|
| Item-definition JSON | `Consumable_{ItemName}.json`        | `Consumable_Corpse_Starch.json`        |
| Model                | `Consumable_{ItemName}.blockymodel` | `Consumable_Corpse_Starch.blockymodel` |
| Texture              | `Consumable_{ItemName}_Texture.png` | `Consumable_Corpse_Starch_Texture.png` |
| Inventory icon       | `Consumable_{ItemName}.png`         | `Consumable_Corpse_Starch.png`         |

---

### 6.7 Projectiles

Projectile textures do not use the `_Texture` suffix because they are referenced inside a flat model-entity definition
rather than an item JSON.

| File                   | Pattern                             | Example                      |
|------------------------|-------------------------------------|------------------------------|
| Entity-definition JSON | `Projectile_{ItemName}.json`        | `Projectile_Las.json`        |
| Model                  | `Projectile_{ItemName}.blockymodel` | `Projectile_Las.blockymodel` |
| Texture                | `Projectile_{ItemName}.png`         | `Projectile_Las.png`         |

---

### 6.8 Sounds

Sound files are OGG and are referenced by string event IDs only, never by file path in item-definition JSON. The
directory hierarchy is informational.

```
Common/Sounds/Warhammer/{Category}/{SoundEventId}.ogg
```

Sound event IDs follow the prefix convention:

```
SFX_{Category}_{ItemOrContext}_{Action}
```

Examples: `SFX_Boltpistol_Fire`, `SFX_Lasgun_Reload_Start`, `SFX_Cadian_Workbench_Open`

---

### 6.9 UI and HUD Files

HUD layout files use PascalCase with no underscore segments:

```
Common/UI/Custom/HUD/{HudName}.ui
```

Example: `WartaleHUD.ui`

The `weaponId` string interpolated in `WartaleHUD.java` to build the HUD icon path must be the full base name of the
weapon so the resolved path is consistent:

```
Icons/Weapons/Weapon_Boltpistol.png
Icons/Weapons/Weapon_BloodPact_Boltpistol.png
```

---

## 7. Quick-Reference Checklist for New Assets

When adding a new item, verify every line of this checklist before opening a pull request:

- [ ] The base name follows `[CategoryPrefix]_[FactionName_][ItemName]` with PascalCase segments.
- [ ] A JSON item-definition file named `{BaseName}.json` exists in the correct `Server/Item/Items/...` subdirectory.
- [ ] A folder named `{BaseName}` exists under the correct `Common/Resources/Items/Warhammer/...` subdirectory.
- [ ] Inside that folder, `{BaseName}.blockymodel` and `{BaseName}_Texture.png` both exist.
- [ ] `Common/Icons/ItemsGenerated/Warhammer/{Category}/[FactionName/]{BaseName}.png` exists.
- [ ] If the item is a weapon, `Common/Icons/Weapons/{BaseName}.png` also exists for the HUD.
- [ ] If the item appears in a crafting bench category, `Common/Icons/CraftingCategories/{BenchBaseName}/{BaseName}.png`
  exists.
- [ ] All path fields in the JSON (`Model`, `Texture`, `Icon`, and any `Animation`) resolve to files that actually exist
  in the repository using the exact formats from section 6.
- [ ] No file name contains spaces, special characters, or deviations from the PascalCase_underscore convention.
