# Copilot instructions for WarTale 40,000

WarTale 40,000 is a shared Hytale server plugin and asset pack that adds Warhammer 40k content. Many contributors work
on it, so keep changes scoped and follow the conventions already in the repository.

## Working preferences

- Preserve unrelated changes and manual asset edits from other contributors. Ask before overwriting work that differs
  from its generator or source.
- Use Windows paths and PowerShell commands for local work. Hytale asset identifiers inside JSON still use forward
  slashes.
- Implement the requested change completely, but keep its scope narrow. For an analysis or explanation request,
  inspect the code and explain it without changing behavior.
- Keep responses concise. State what changed and any remaining limitation. Do not claim in-game verification from a
  successful build or software render.

## Useful skills

Load an available matching skill before starting the relevant work. Read only the references needed for the task.

- `hytale-modder` for server plugins, ECS components and systems, interactions, commands, custom UI and HUD, inventory,
  and world handling.
- `hytale-blockbench-generator` for reproducible `.blockymodel` models, texture atlases, icons, previews, and
  `.blockyanim` animations. Use its model, animation, preview, and icon reference scripts instead of inventing a new
  format.
- `python-expert` when writing or changing Python generators or preview tools.
- `unslop` for user-facing writing, including the README and issue or pull request text.
- `changelog-generator` when release notes or a changelog are requested.

Skills may not be installed in every environment. If a needed skill is unavailable, say so and use repository examples
and verified Hytale documentation. Do not pretend to have loaded it.

## Repository layout and plugin changes

- Java code lives in `src\main\java\com\warhammer\wartale`. `WartalePlugin.java` is the entry point and registers
  components, interaction codecs, commands, events, and systems in `setup()`.
- Keep code in its existing package: `assets`, `commands`, `components`, `core`, `eventHandlers`, `globalEvents`,
  `gui`, `interactions`, `items`, `masteryCore`, and `systems`. Reuse existing helpers before adding duplicate
  behavior.
- Resource files live in `src\main\resources`. Item definitions are under `Server\Item\Items`, interactions under
  `Server\Item\Interactions` and `Server\Item\RootInteractions`, sound events under `Server\Audio\SoundEvents`, models
  and textures under `Common\Resources\Items\Warhammer`, icons under `Common\Icons`, item animations under
  `Common\Characters\Animations\Items`, and UI under `Common\UI\Custom`.
- `dumpfiles` holds work-in-progress material that is not packaged. Do not reference it from shipped assets.
- Use the Hytale API version configured in `gradle.properties` (`hytale_version`) and `build.gradle`. Verify API
  signatures against available sources or dependencies rather than assuming a skill's example matches the installed
  version.
- Use `Ref<EntityStore>` and the relevant store for entity access. Check reference validity, and run world and store
  mutations on the owning world's execution thread. Do not retain stale entity references across asynchronous
  callbacks.
- Keep codec registration names consistent with item interaction `Type` values. Treat persisted component names and
  codec fields as compatibility-sensitive.
- Avoid codec defaults that resolve game assets before those assets are available during plugin setup.
- Keep Java, item definitions, interactions, sounds, effects, animations, HUD, and icons consistent when behavior
  changes. `gui\WartaleHUD.java` resolves weapon icons as `Icons/Weapons/{weaponId}.png` relative to
  `Common\UI\Custom`, so renaming a weapon also affects `Common\UI\Custom\Icons\Weapons`.
- Update the README when items, recipes, controls, or player-visible behavior change.
- Keep `includes_pack` enabled for packaged custom assets. Change metadata through `gradle.properties` and the manifest
  generation task rather than letting generated metadata drift.

## Asset naming and structure

`Asset-Styleguide.md` is the normative naming and directory reference. Read it before adding, moving, or renaming an
asset.

- New assets use one base name, `[CategoryPrefix]_[FactionName_]ItemName`, with PascalCase segments separated by
  underscores. All variants (JSON, model, `_Texture` PNG, inventory icon, HUD icon, crafting-category icon, animation)
  derive from that base name.
- File names are case-sensitive on the Linux server target. Never rely on Windows case-insensitivity.
- Some older assets do not follow the guide yet. Do not rename existing assets unless the task asks for it. When a
  rename is requested, update every reference, including JSON paths, Java strings, HUD icon lookups, and animations.
- Before finishing an asset task, walk through the guide's quick-reference checklist and confirm that every `Model`,
  `Texture`, `Icon`, and `Animation` path resolves to a file that exists.
- When adding content that matches an issue template in `.github\ISSUE_TEMPLATE` (item, block, model, animation, VFX,
  SFX, prefab, gameplay system), cover the deliverables that template asks for.

## Gradle validation

Use the checked-in Gradle wrapper from the repository root. The Java toolchain (`java_version=25`) is configured in
`gradle.properties`; do not replace it with the machine's default Java version.

Choose the smallest command that covers the change.

```powershell
# Compile Java changes.
.\gradlew.bat --console=plain compileJava

# Check formatting (Spotless with Google Java Format) and Checkstyle.
.\gradlew.bat --console=plain spotlessCheck checkstyleMain

# Run configured verification tasks.
.\gradlew.bat --console=plain check

# Build and package the plugin, including resources.
.\gradlew.bat --console=plain build
```

- Pull request CI (`.github\workflows\build-verification.yml`) runs `spotlessCheck`, `check`, and `build`. The Husky
  pre-commit hook runs `spotlessApply`, `spotlessCheck`, and `check`. Java changes should pass the same tasks locally.
- Use `.\gradlew.bat --console=plain spotlessApply` when formatting changes are needed. Review its diff and do not
  leave unrelated formatting edits. Checkstyle uses `config\checkstyle.xml`.
- For asset changes, validate the changed assets and build the packaged plugin.
- A Gradle build does not establish valid Hytale asset references, correct attachment placement, or working gameplay.
  Check these separately.
- Documentation-only changes do not need a Gradle build.
- Do not start with `clean`, delete caches, upgrade dependencies, or run `just reset` to work around a failure.
  Diagnose the actual error and report unresolved blockers.

Development tasks, when runtime validation is needed (the `justfile` wraps the same commands):

```powershell
.\gradlew.bat --console=plain updatePluginManifest
.\gradlew.bat --console=plain prepareRunServer
.\gradlew.bat --console=plain hytaleJvmDoctor
.\gradlew.bat --console=plain runServer -Dhotswap=true
```

Manifest generation writes metadata. Server tasks may download files or start a long-running process. Use them only
when relevant, keep temporary servers scoped to the task, and stop them when finished. Do not deploy a JAR to the
user's game installation (`just build-and-copy`) or build the Docker image without a request.

## Reproducible asset generation

- Store generator and related Python source files in `generate_blockbench` at the repository root. Derive paths from
  the script location, never from an absolute developer-machine path.
- Check whether `generate_blockbench` is ignored before preparing files for version control. Do not force-add files or
  change ignore rules without a request.
- Use a uv-managed Python project and run scripts with `uv run`. Reuse the skill's dependency requirements; add
  dependencies only when needed.
- Generate the model and matching texture together. Keep UV packing synchronized, use deterministic seeded randomness,
  and fail explicitly when the atlas has no space.
- Before regeneration, compare the current asset with the generator. Preserve meaningful manual changes to positions,
  pivots, rotations, geometry, and UVs. Ignore only known harmless Blockbench serialization differences.
- For user-adjusted assets, update the generator to reproduce the current model instead of reverting the model to an
  older generated version.
- Preserve root and attachment transforms and all animated node names. Check attachment height and pivots against the
  original model, especially for held weapons, worn armor, magazines, and projectiles.
- Keep visible faces intact. Check quad normals, face omission, and backface culling when flat geometry disappears.
- Keep the existing model format, scale conventions, and texture dimensions unless a change is requested. Match
  Hytale's chunky shapes, readable silhouettes, hand-painted pixel textures, and consistent texel density, while
  keeping the Warhammer 40k look of the faction (for example Cadian, Blood Pact, Sirdar).
- Only overwrite the intended model and texture. Inspect related item JSON, icons, animations, `Scale`, and
  `IconProperties`, but do not silently redesign or retune them for a model-only request.
- Check whether a model or texture is shared (for example generic material models) before replacing it.

## Previews and asset verification

- Render front, three-quarter, back, and side views after model changes. Also inspect the enlarged texture with
  nearest-neighbor scaling.
- Open and visually inspect the generated images. Display or attach the preview for the user, rather than only
  printing a filesystem path.
- Check silhouette, clipping, floating parts, missing faces, decal readability, UV alignment, and attachment placement.
  Iterate on visible defects before presenting the result.
- A preview script's default output goes to the generator directory containing that script. Keep an explicit output
  override, and use it to write temporary review images to the session artifact directory or a scoped temporary
  directory. Do not put scratch previews into packaged assets or overwrite curated README images.
- Generate icons when requested and inspect them at inventory size. Preserve existing icon framing settings unless
  their adjustment is part of the task.
- Validate model JSON, unique string node IDs, finite numbers, normalized quaternions, and UV rectangles within texture
  bounds. Check that referenced model, texture, icon, and animation assets resolve under Hytale's asset conventions.
- Validate animation target names against the model, keyframe order and duration, supported tracks, and quaternion
  values. Use the Hytale Blockbench plugin's format as the source of truth.
- A static preview does not verify animation playback or exact Hytale UV orientation. Confirm animations in Blockbench
  with the Hytale plugin and gameplay placement in-game when possible. State plainly when those checks remain
  unperformed.
